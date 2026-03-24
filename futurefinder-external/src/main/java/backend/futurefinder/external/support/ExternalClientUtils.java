package backend.futurefinder.external.support;

import org.jsoup.Jsoup;
import org.springframework.util.StringUtils;

/**
 * 외부 API 클라이언트에서 공통으로 사용하는 유틸리티.
 */
public final class ExternalClientUtils {

    private ExternalClientUtils() {}

    public static String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max) + "..." : s;
    }

    public static String nvl(String s) {
        return s == null ? "" : s;
    }

    /** HTML 태그 제거 + 공백 정규화 */
    public static String cleanHtml(String s) {
        if (s == null) return "";
        return Jsoup.parse(s).text().replaceAll("\\s+", " ").trim();
    }

    /** 문자열이 비어있으면 fallback 반환 */
    public static String firstNonBlank(String... values) {
        for (String v : values) {
            if (StringUtils.hasText(v)) return v;
        }
        return "";
    }
}
