package backend.futurefinder.external;

import backend.futurefinder.model.recruit.RecruitmentItem;
import backend.futurefinder.property.RecruitmentApiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExternalRecruitmentClientImpl implements ExternalRecruitmentClient {

    private final WebClient moefRecruitmentWebClient;
    private final RecruitmentApiProperties props;

    private static final int FETCH_SIZE = 50; // 외부에서 한 번에 가져올 양
    private static final int PAGE_SIZE  = 5;  // 내부 페이징 단위
    private static final Duration CACHE_TTL = Duration.ofSeconds(60);

    /** 아주 단순한 in-memory 캐시 */
    private static final class CacheBox {
        final LocalDate pbancEndYmd;        // 쿼리 키(여기선 내일 날짜)
        final List<RecruitmentItem> items;  // 최대 50개
        final Instant fetchedAt;            // 적재 시각
        CacheBox(LocalDate k, List<RecruitmentItem> v, Instant t) {
            this.pbancEndYmd = k; this.items = v; this.fetchedAt = t;
        }
    }
    private volatile CacheBox cache; // 싱글톤 빈이므로 필드 캐시 OK

    @Override
    public List<RecruitmentItem> fetch(int page) {
        int safePage = (page <= 0) ? 1 : page;
        System.out.println("safepage 값: " + safePage + '\n');

        var tz = ZoneId.of("Asia/Seoul");
        var tomorrow = LocalDate.now(tz).plusDays(1);

        // 1) 캐시에서 50개 가져오거나, 필요 시 외부 호출로 갱신
        List<RecruitmentItem> all = get50CachedOrFetch(tomorrow);

        // 2) 내부 페이징 (5개씩 슬라이싱)
        int from = Math.max(0, (safePage - 1) * PAGE_SIZE);
        if (from >= all.size()) return List.of();
        int to = Math.min(from + PAGE_SIZE, all.size());
        System.out.printf("[MOEF][PAGE] req=%d, from=%d, to=%d, total=%d%n", safePage, from, to, all.size());
        return new ArrayList<>(all.subList(from, to));
    }

    /** 캐시 사용: 키(내일 날짜) 동일 & TTL 유효 시 캐시 반환, 아니면 외부 호출 */
    private List<RecruitmentItem> get50CachedOrFetch(LocalDate key) {
        var now = Instant.now();
        CacheBox c = cache;
        if (c != null && c.pbancEndYmd.equals(key) && now.isBefore(c.fetchedAt.plus(CACHE_TTL))) {
            return c.items;
        }
        synchronized (this) {
            c = cache; // 더블 체크
            if (c != null && c.pbancEndYmd.equals(key) && now.isBefore(c.fetchedAt.plus(CACHE_TTL))) {
                return c.items;
            }
            // 실제 외부 50개 호출
            List<RecruitmentItem> fresh = fetch50FromRemote(key);
            cache = new CacheBox(key, fresh, Instant.now());
            return fresh;
        }
    }

    /** 외부 API에서 1페이지 50건 가져오기 */
    private List<RecruitmentItem> fetch50FromRemote(LocalDate pbancEndYmd) {
        System.out.println("[MOEF][REQ] ongoingYn=Y, pbancEndYmd=" + pbancEndYmd
                + ", pageNo=1, numOfRows=" + FETCH_SIZE);

        JsonNode root = moefRecruitmentWebClient.get()
                .uri(uri -> uri.path("/list")
                        .queryParam("serviceKey", props.getServiceKey())
                        .queryParam("resultType", "json")
                        .queryParam("ongoingYn", "Y")
                        .queryParam("pbancEndYmd", pbancEndYmd.toString())
                        .queryParam("pageNo", 1)               // 항상 1페이지
                        .queryParam("numOfRows", FETCH_SIZE)   // 50건 요청
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(res -> {
                    MediaType ct = res.headers().contentType().orElse(null);
                    if (ct != null && ct.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                        return res.bodyToMono(JsonNode.class)
                                .doOnNext(this::printHeaderAndCounts);
                    }
                    return res.bodyToMono(String.class).flatMap(body ->
                            Mono.error(new IllegalStateException("Expected JSON but got %s, body=%s"
                                    .formatted(ct, truncate(body, 500)))));
                })
                .block(Duration.ofSeconds(props.getTimeoutSeconds()));

        return parseItems(root);
    }

    /** 헤더 성공/카운트 로그 (println) */
    private void printHeaderAndCounts(JsonNode root) {
        try {
            if (root == null) { System.out.println("[MOEF] body=null"); return; }

            if (root.has("response")) {
                var header = root.path("response").path("header");
                String code = header.path("resultCode").asText("");
                String msg  = header.path("resultMsg").asText("");
                var body    = root.path("response").path("body");
                int total   = body.path("totalCount").asInt(-1);
                int pageNo  = body.path("pageNo").asInt(-1);
                int rows    = body.path("numOfRows").asInt(-1);
                System.out.println("[MOEF] (wrapped) resultCode=" + code + ", resultMsg=" + msg
                        + ", totalCount=" + total + ", pageNo=" + pageNo + ", numOfRows=" + rows);
                if (!code.isEmpty() && !"00".equals(code)) {
                    System.err.println("[MOEF][WARN] Non-success resultCode: " + code + " (" + msg + ")");
                }
                return;
            }

            // ✅ 현재 로그 포맷: {resultCode:200, resultMsg:"성공했습니다.", totalCount:0, result:[]}
            if (root.has("resultCode")) {
                int code  = root.path("resultCode").asInt(-1);
                String msg= root.path("resultMsg").asText("");
                int total = root.path("totalCount").asInt(-1);
                System.out.println("[MOEF] (flat) resultCode=" + code + ", resultMsg=" + msg
                        + ", totalCount=" + total);
                if (code != 200 && code != 0) {
                    System.err.println("[MOEF][WARN] Non-success resultCode: " + code + " (" + msg + ")");
                }
                return;
            }

            System.out.println("[MOEF] unknown shape, root=" + truncate(root.toString(), 500));
        } catch (Exception e) {
            System.err.println("[MOEF][DEBUG] printHeaderAndCounts failed: " + e.getMessage());
        }
    }

    // ---- 기존 parseItems/mapItem/pick/nvl/truncate 그대로 ----
    private List<RecruitmentItem> parseItems(JsonNode root) {
        JsonNode items = null;

        // ✅ 현재 API 포맷: { resultCode: 200, resultMsg: "...", totalCount: 93, result: [...] }
        if (root != null && root.has("result")) {
            items = root.path("result");
        }

        // 기존 포맷 대응: response.body.items.item / items
        if ((items == null || items.isMissingNode() || items.isNull()) && root != null && root.has("response")) {
            var body = root.path("response").path("body");
            items = body.path("items").path("item");
            if (items.isMissingNode() || items.isNull()) items = body.path("items");
        }
        if (items == null || items.isMissingNode() || items.isNull()) {
            items = root != null ? root.path("items").path("item") : null;
            if (items != null && (items.isMissingNode() || items.isNull())) items = root.path("items");
        }

        List<RecruitmentItem> out = new ArrayList<>();
        if (items != null && items.isArray()) {
            for (JsonNode n : items) out.add(mapItem(n));
        } else if (items != null && items.isObject()) {
            out.add(mapItem(items));
        } else {
            // 디버깅용(원하면 지워도 됨)
            System.out.println("[MOEF] items not found. root=" + truncate(String.valueOf(root), 500));
        }
        if (items != null && items.isArray() && items.size() > 0) {
            JsonNode first = items.get(0);
            System.out.println("[MOEF][DEBUG] first item keys=" +
                    java.util.stream.StreamSupport.stream(
                            java.util.Spliterators.spliteratorUnknownSize(first.fieldNames(), 0), false
                    ).toList()
            );
        }
        return out;
    }

    private static String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max) + "..." : s;
    }
    private RecruitmentItem mapItem(JsonNode n) {
        // 공시기관
        String institute = n.path("instNm").asText("");

        // 공고 제목
        String title = n.path("recrutPbancTtl").asText("");

        // 채용구분 (명칭 우선, 없으면 코드)
        String recruitSe = n.path("recrutSeNm").asText(
                n.path("recrutSe").asText("")
        );

        // 고용형태: hireTypeNmLst → "A,B" 같은 문자열일 수 있어 첫 항목만 사용
        String hireType = firstFromCommaList(
                n.path("hireTypeNmLst").asText(
                        n.path("hireTypeLst").asText("") // 최후의 보루: 코드만 있을 때 (명칭 없으면 빈값일 수 있음)
                )
        );

        return RecruitmentItem.of(nvl(institute), nvl(title), nvl(recruitSe), nvl(hireType));
    }
    private static String firstFromCommaList(String s) {
        if (s == null || s.isBlank()) return "";
        for (String t : s.split(",")) {
            String v = t.trim();
            if (!v.isEmpty()) return v; // 첫 번째 유효 토큰 반환
        }
        return "";
    }


    private static String nvl(String s){ return s==null? "": s; }
}