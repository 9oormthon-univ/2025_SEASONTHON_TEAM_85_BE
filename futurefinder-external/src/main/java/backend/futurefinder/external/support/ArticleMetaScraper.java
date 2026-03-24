package backend.futurefinder.external.support;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Stream;

import static backend.futurefinder.external.support.ExternalClientUtils.cleanHtml;

/**
 * 네이버 뉴스 기사 본문에서 OG 메타(description, image, press, publishedAt)를 추출한다.
 */
@Slf4j
public final class ArticleMetaScraper {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter NAVER_ATTR =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter NAVER_KO_TEXT =
            DateTimeFormatter.ofPattern("yyyy.MM.dd. a h:mm").withLocale(Locale.KOREAN);

    private ArticleMetaScraper() {}

    public record Meta(String description, String image, Instant publishedAt, String press) {
        public static final Meta EMPTY = new Meta("", null, null, "");
    }

    public static Meta scrape(String pageUrl, int timeoutSeconds) {
        if (!StringUtils.hasText(pageUrl)) return Meta.EMPTY;
        try {
            Document d = Jsoup.connect(pageUrl)
                    .timeout(timeoutSeconds * 1000)
                    .userAgent("Mozilla/5.0 (compatible; ChewingBot/1.0)")
                    .header("Accept", "*/*")
                    .get();

            return new Meta(
                    extractDescription(d),
                    extractImage(d),
                    extractPublishedAt(d),
                    extractPress(d)
            );
        } catch (Exception e) {
            log.debug("OG/meta fetch failed: {}", pageUrl, e);
            return Meta.EMPTY;
        }
    }

    // ---- 각 필드 추출 (fallback 체인) ----

    private static String extractDescription(Document d) {
        return cleanHtml(metaContent(d,
                "meta[property=og:description]",
                "meta[name=description]"));
    }

    private static String extractImage(Document d) {
        // og:image 우선
        String url = metaContent(d, "meta[property=og:image]");
        if (StringUtils.hasText(url)) return url;

        // fallback: 본문 첫 이미지
        Element img = d.selectFirst("img[src]");
        if (img != null) {
            String abs = img.absUrl("src");
            return StringUtils.hasText(abs) ? abs : img.attr("src");
        }
        return null;
    }

    private static String extractPress(Document d) {
        String press = metaContent(d,
                "meta[property=og:site_name]",
                "meta[name=og:site_name]",
                "meta[name=twitter:site]");

        if (!StringUtils.hasText(press)) {
            Element logo = d.selectFirst(".media_end_head_top_logo img[alt], a.media_end_head_top_logo img[alt]");
            if (logo != null) press = logo.attr("alt");
        }

        press = cleanHtml(press);
        return (press.startsWith("@")) ? press.substring(1) : press;
    }

    private static Instant extractPublishedAt(Document d) {
        // 1) meta 태그에서 ISO 파싱
        String iso = metaContent(d,
                "meta[property=article:published_time]",
                "meta[property=og:article:published_time]",
                "meta[property=article:modified_time]",
                "meta[property=og:updated_time]");

        Instant result = parseInstant(iso);
        if (result != null) return result;

        // 2) 네이버 뉴스 전용 시간 엘리먼트
        Element timeEl = d.selectFirst("span.media_end_head_info_datestamp_time");
        if (timeEl == null) return null;

        result = parseKst(timeEl.attr("data-date-time"), NAVER_ATTR);
        if (result != null) return result;

        String txt = cleanHtml(timeEl.text()).replace("입력", "").replace("기사입력", "").trim();
        return parseKst(txt, NAVER_KO_TEXT);
    }

    // ---- 공통 헬퍼 ----

    /** 여러 CSS 셀렉터를 시도하여 첫 번째 비어있지 않은 content 속성을 반환 */
    private static String metaContent(Document d, String... selectors) {
        return Stream.of(selectors)
                .map(d::selectFirst)
                .filter(el -> el != null && StringUtils.hasText(el.attr("content")))
                .map(el -> el.attr("content"))
                .findFirst()
                .orElse("");
    }

    private static Instant parseInstant(String s) {
        if (!StringUtils.hasText(s)) return null;
        try { return OffsetDateTime.parse(s).toInstant(); } catch (Exception ignored) {}
        try { return Instant.parse(s); } catch (Exception ignored) {}
        return null;
    }

    private static Instant parseKst(String s, DateTimeFormatter fmt) {
        if (!StringUtils.hasText(s)) return null;
        try {
            return LocalDateTime.parse(s, fmt).atZone(KST).toInstant();
        } catch (Exception e) {
            return null;
        }
    }
}
