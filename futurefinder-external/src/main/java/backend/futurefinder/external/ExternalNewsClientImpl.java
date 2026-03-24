package backend.futurefinder.external;

import backend.futurefinder.external.support.ArticleMetaScraper;
import backend.futurefinder.external.support.ArticleMetaScraper.Meta;
import backend.futurefinder.external.support.TtlCache;
import backend.futurefinder.model.news.NewsItem;
import backend.futurefinder.property.NaverOpenApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static backend.futurefinder.external.support.ExternalClientUtils.cleanHtml;
import static backend.futurefinder.external.support.ExternalClientUtils.firstNonBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalNewsClientImpl implements ExternalNewsClient {

    private static final String ECON_SECTION_URL = "https://news.naver.com/section/101";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final NaverOpenApiProperties newsProps;
    private final TtlCache<List<NewsItem>> cache = new TtlCache<>();

    public List<NewsItem> fetchEconomyPage(int page) {
        return paginate(page);
    }

    @Override
    public List<NewsItem> searchNews(int page, int size) {
        return paginate(page);
    }

    private List<NewsItem> paginate(int page) {
        int safePage = Math.max(page, 1);
        List<NewsItem> all = cache.get(
                Duration.ofSeconds(newsProps.getCacheTtlSeconds()),
                () -> fetchCurrentList(newsProps.getFetchSize())
        );
        int from = (safePage - 1) * newsProps.getPageSize();
        if (from >= all.size()) return List.of();
        int to = Math.min(from + newsProps.getPageSize(), all.size());
        return new ArrayList<>(all.subList(from, to));
    }

    private List<NewsItem> fetchCurrentList(int want) {
        try {
            Document doc = Jsoup.connect(ECON_SECTION_URL)
                    .timeout(newsProps.getScrapingTimeoutSeconds() * 1000)
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
                NewsItem item = parseCard(card, seenUrl);
                if (item != null) out.add(item);
            }

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

    private NewsItem parseCard(Element card, Set<String> seenUrl) {
        Element a = Optional.ofNullable(card.selectFirst("a.sa_text_title"))
                .orElse(card.selectFirst("a[href*=\"/article/\"]"));
        if (a == null) return null;

        String title = cleanHtml(a.text());
        String href = a.absUrl("href");
        if (!StringUtils.hasText(title) || !StringUtils.hasText(href)) return null;
        if (!seenUrl.add(href)) return null;

        String summary = Optional.ofNullable(card.selectFirst(".sa_text_lede"))
                .map(e -> cleanHtml(e.text())).orElse("");

        Element img = card.selectFirst("img[src], img[data-src]");
        String thumb = (img != null)
                ? firstNonBlank(img.absUrl("src"), img.absUrl("data-src"))
                : null;
        if (thumb != null && thumb.isEmpty()) thumb = null;

        Element pressEl = card.selectFirst(".sa_text_press, .sa_text_info, .press, [class*=press]");
        String press = (pressEl != null) ? cleanHtml(pressEl.text()) : "";

        Meta meta = ArticleMetaScraper.scrape(href, newsProps.getMetaTimeoutSeconds());
        summary = firstNonBlank(summary, meta.description());
        thumb = StringUtils.hasText(thumb) ? thumb : meta.image();
        press = firstNonBlank(press, meta.press());

        LocalDateTime publishedKst = meta.publishedAt() == null
                ? null : LocalDateTime.ofInstant(meta.publishedAt(), KST);

        return NewsItem.builder()
                .title(title)
                .content(limit(summary, 320))
                .imageUrl(thumb)
                .publishedAt(publishedKst)
                .press(press)
                .build();
    }

    private static String limit(String s, int max) {
        if (s == null || s.length() <= max) return (s == null) ? "" : s;
        int cut = Math.max(s.lastIndexOf(' ', max), max);
        return s.substring(0, Math.min(cut, s.length())).trim() + "…";
    }
}
