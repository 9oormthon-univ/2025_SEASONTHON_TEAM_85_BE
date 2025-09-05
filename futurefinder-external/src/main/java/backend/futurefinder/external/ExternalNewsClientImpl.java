package backend.futurefinder.external;

import backend.futurefinder.dto.news.NaverNewsResponse;
import backend.futurefinder.model.news.NewsItem;
import backend.futurefinder.property.NaverOpenApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalNewsClientImpl implements ExternalNewsClient {

    private static final String ECON_SECTION_URL = "https://news.naver.com/section/101";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    // 수집/페이징/TTL 설정
    private static final int FETCH_SIZE = 50;           // 한 번에 모을 최대 기사 수
    private static final int PAGE_SIZE  = 5;            // 내부 페이징 단위
    private static final Duration CACHE_TTL = Duration.ofSeconds(60); // 캐시 유지 시간

    // 본문 시간 포맷
    private static final DateTimeFormatter NAVER_ATTR =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter NAVER_KO_TEXT =
            DateTimeFormatter.ofPattern("yyyy.MM.dd. a h:mm").withLocale(Locale.KOREAN);

    // ---- 간단한 in-memory TTL 캐시 ----
    private static final class CacheBox {
        final List<NewsItem> items; // 최신순 정렬된 0..50
        final Instant fetchedAt;
        CacheBox(List<NewsItem> items, Instant fetchedAt) {
            this.items = items; this.fetchedAt = fetchedAt;
        }
    }
    private volatile CacheBox cache;

    // ====== 새로 추가된 페이징 API ======
    public List<NewsItem> fetchEconomyPage(int page) {
        int safePage = (page <= 0) ? 1 : page;

        List<NewsItem> all = getCachedOrFetch(); // 0..50
        int from = (safePage - 1) * PAGE_SIZE;
        if (from >= all.size()) return List.of(); // 범위 밖 → 빈 리스트
        int to = Math.min(from + PAGE_SIZE, all.size());
        return new ArrayList<>(all.subList(from, to));
    }

    // (하위 호환용) 기존 인터페이스 메서드도 구현
    @Override
    public List<NewsItem> searchNews(int page, int size) {
        // 페이지 단위는 고정 5개 (PAGE_SIZE). size 파라미터는 무시.
        int safePage = (page <= 0) ? 1 : page;

        List<NewsItem> all = getCachedOrFetch(); // 최신순 정렬된 최대 50개 (TTL 적용)
        int from = (safePage - 1) * PAGE_SIZE;
        if (from >= all.size()) return List.of(); // 범위 밖이면 빈 리스트

        int to = Math.min(from + PAGE_SIZE, all.size());
        return new ArrayList<>(all.subList(from, to));
    }


    // ====== 캐시 접근 ======
    private List<NewsItem> getCachedOrFetch() {
        Instant now = Instant.now();
        CacheBox c = cache;
        if (c != null && now.isBefore(c.fetchedAt.plus(CACHE_TTL))) return c.items;

        synchronized (this) {
            c = cache;
            if (c != null && now.isBefore(c.fetchedAt.plus(CACHE_TTL))) return c.items;
            List<NewsItem> fresh = fetchCurrentList(FETCH_SIZE); // 스크래핑 + 정렬
            cache = new CacheBox(fresh, Instant.now());
            return fresh;
        }
    }

    // ====== 실제 스크래핑(최신 50 수집 후 최신순 정렬) ======
    private List<NewsItem> fetchCurrentList(int want) {
        try {
            Document doc = Jsoup.connect(ECON_SECTION_URL)
                    .timeout((int) Duration.ofSeconds(5).toMillis())
                    .userAgent("Mozilla/5.0 (compatible; ChewingBot/1.0)")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .get();

            Elements cards = doc.select("div.sa_item, li.sa_item");
            if (cards.isEmpty()) {
                cards = doc.select("section[id*=section_body] a[href*=\"/article/\"]").parents();
            }

            List<NewsItem> out = new ArrayList<>();
            Set<String> seenUrl = ConcurrentHashMap.newKeySet();

            for (Element card : cards) {
                if (out.size() >= want) break;

                Element a = Optional.ofNullable(card.selectFirst("a.sa_text_title"))
                        .orElse(card.selectFirst("a[href*=\"/article/\"]"));
                if (a == null) continue;

                String title = clean(a.text());
                String href  = a.absUrl("href");
                if (!StringUtils.hasText(title) || !StringUtils.hasText(href)) continue;
                if (!seenUrl.add(href)) continue; // URL 기준 중복 제거

                // 요약
                String summary = Optional.ofNullable(card.selectFirst(".sa_text_lede"))
                        .map(e -> clean(e.text()))
                        .orElse("");

                // 썸네일
                String thumb = null;
                Element imgEl = card.selectFirst("img[src], img[data-src]");
                if (imgEl != null) {
                    thumb = StringUtils.hasText(imgEl.attr("src"))
                            ? imgEl.absUrl("src")
                            : imgEl.absUrl("data-src");
                    if (!StringUtils.hasText(thumb)) thumb = null;
                }

                // 언론사(카드에서 우선)
                String press = findPressFromCard(card);

                // 본문 1회 GET → 메타/시간 보강
                ArticleMeta meta = fetchMeta(href);
                if (!StringUtils.hasText(summary)) summary = meta.description();
                if (!StringUtils.hasText(thumb))   thumb   = meta.image();
                if (!StringUtils.hasText(press))   press   = meta.press();

                LocalDateTime publishedKst = meta.publishedAt() == null
                        ? null
                        : LocalDateTime.ofInstant(meta.publishedAt(), KST);

                out.add(NewsItem.builder()
                        .title(title)
                        .content(limit(summary, 320))
                        .imageUrl(thumb)
                        .publishedAt(publishedKst)
                        .press(press)
                        .build());
            }

            // 최신순 정렬(null은 뒤로)
            out.sort(Comparator.comparing(
                            NewsItem::getPublishedAt,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    .reversed());

            return out;

        } catch (Exception e) {
            log.error("Failed to fetch economy section", e);
            return List.of();
        }
    }

    // ===================== helpers =====================

    private static String clean(String s) {
        if (s == null) return "";
        String t = Jsoup.parse(s).text();
        t = t.replaceAll("\\s+", " ").trim();
        return t;
    }

    private static String limit(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        int cut = Math.max(s.lastIndexOf(' ', Math.min(max, s.length() - 1)), max);
        return s.substring(0, Math.min(cut, s.length())).trim() + "…";
    }

    private String findPressFromCard(Element card) {
        Element el = Optional.ofNullable(card.selectFirst(".sa_text_press"))
                .orElse(card.selectFirst(".sa_text_info"));
        if (el == null) {
            el = Optional.ofNullable(card.selectFirst(".press"))
                    .orElse(card.selectFirst("[class*=press]"));
        }
        String v = el == null ? "" : clean(el.text());
        return StringUtils.hasText(v) ? v : "";
    }

    /** 기사 본문에서 description/og:image/press/published_time 추출 */
    private ArticleMeta fetchMeta(String pageUrl) {
        if (!StringUtils.hasText(pageUrl)) return ArticleMeta.EMPTY;
        try {
            Document d = Jsoup.connect(pageUrl)
                    .timeout((int) Duration.ofSeconds(4).toMillis())
                    .userAgent("Mozilla/5.0 (compatible; ChewingBot/1.0)")
                    .header("Accept", "*/*")
                    .get();

            // description
            String desc = firstNonEmptyContent(
                    d.selectFirst("meta[property=og:description]"),
                    d.selectFirst("meta[name=description]"));
            desc = clean(desc == null ? "" : desc);

            // image (abs)
            String imgAbs = null;
            Element ogImg = d.selectFirst("meta[property=og:image]");
            if (ogImg != null && StringUtils.hasText(ogImg.attr("content"))) {
                imgAbs = ogImg.attr("abs:content");
                if (!StringUtils.hasText(imgAbs)) imgAbs = ogImg.attr("content");
            }
            if (!StringUtils.hasText(imgAbs)) {
                Element anyImg = d.selectFirst("img[src]");
                if (anyImg != null) {
                    imgAbs = anyImg.absUrl("src");
                    if (!StringUtils.hasText(imgAbs)) imgAbs = anyImg.attr("src");
                }
            }

            // press
            String press = firstNonEmptyContent(
                    d.selectFirst("meta[property=og:site_name]"),
                    d.selectFirst("meta[name=og:site_name]"),
                    d.selectFirst("meta[name=twitter:site]"));
            if (!StringUtils.hasText(press)) {
                Element logo = d.selectFirst(".media_end_head_top_logo img[alt], a.media_end_head_top_logo img[alt]");
                if (logo != null) press = logo.attr("alt");
            }
            press = normalizePress(press);

            // published time
            Instant published = null;
            String iso = firstNonEmptyContent(
                    d.selectFirst("meta[property=article:published_time]"),
                    d.selectFirst("meta[property=og:article:published_time]"),
                    d.selectFirst("meta[property=article:modified_time]"),
                    d.selectFirst("meta[property=og:updated_time]"));
            if (StringUtils.hasText(iso)) {
                published = tryParseOffsetOrInstant(iso);
            }
            if (published == null) {
                Element timeEl = d.selectFirst("span.media_end_head_info_datestamp_time");
                if (timeEl != null) {
                    String attr = timeEl.attr("data-date-time"); // yyyy-MM-dd HH:mm:ss
                    if (StringUtils.hasText(attr)) {
                        try {
                            LocalDateTime ldt = LocalDateTime.parse(attr, NAVER_ATTR);
                            published = ldt.atZone(KST).toInstant();
                        } catch (Exception ignore) {}
                    }
                    if (published == null) {
                        String txt = clean(timeEl.text()).replace("입력", "").replace("기사입력", "").trim();
                        published = tryParseKorean(txt); // 2025.09.05. 오전 12:15
                    }
                }
            }

            return new ArticleMeta(desc, StringUtils.hasText(imgAbs) ? imgAbs : null, published, press);

        } catch (Exception e) {
            log.debug("OG/meta fetch failed: {}", pageUrl, e);
            return ArticleMeta.EMPTY;
        }
    }

    private String normalizePress(String s) {
        if (s == null) return "";
        String t = clean(s);
        if (t.startsWith("@")) t = t.substring(1); // twitter:site가 "@xxxx" 형태일 때
        return t;
    }

    private String firstNonEmptyContent(Element... metas) {
        for (Element m : metas) {
            if (m == null) continue;
            String v = m.attr("content");
            if (StringUtils.hasText(v)) return v;
        }
        return null;
    }

    private Instant tryParseOffsetOrInstant(String s) {
        try { return OffsetDateTime.parse(s).toInstant(); } catch (Exception ignored) {}
        try { return Instant.parse(s); } catch (Exception ignored) {}
        return null;
    }

    private Instant tryParseKorean(String s) {
        try {
            LocalDateTime ldt = LocalDateTime.parse(s, NAVER_KO_TEXT);
            return ldt.atZone(KST).toInstant();
        } catch (Exception e) {
            return null;
        }
    }

    // 내부 전용 컨테이너
    private record ArticleMeta(String description, String image, Instant publishedAt, String press) {
        static final ArticleMeta EMPTY = new ArticleMeta("", null, null, "");
    }
}