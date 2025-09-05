package backend.futurefinder.external;

import backend.futurefinder.model.stock.StockItem;
import backend.futurefinder.property.StockApiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ExternalStockClientImpl implements ExternalStockClient {

    private final WebClient krxWebClient;
    private final StockApiProperties props;

    private static final DateTimeFormatter D8  = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter D10 = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd

    @Override
    public List<StockItem> fetchDailyMovers() {
        ZoneId KST = ZoneId.of("Asia/Seoul");
        LocalDate base = LocalDate.now(KST);
        List<JsonNode> items = Collections.emptyList();

        // 오늘→어제→... 데이터 있는 날 찾기
        for (int i = 0; i < props.getMaxBackDays(); i++) {
            LocalDate target = base.minusDays(i);
            items = fetchAllPagesForDate(target);
            if (!items.isEmpty()) {
                System.out.println("[KRX] basDt=" + target + " items=" + items.size());
                break;
            }
        }
        if (items.isEmpty()) return List.of();

        List<StockItem> all = new ArrayList<>();

        for (JsonNode n : items) {
            String name       = n.path("itmsNm").asText("");
            BigDecimal price  = toDecimal(n.path("clpr").asText(""));
            BigDecimal change = toDecimal(n.path("vs").asText(""));       // 등락폭(원)
            BigDecimal pct    = toDecimal(n.path("fltRt").asText(""));    // 등락률(%)

            if (name.isBlank() || price == null || change == null || pct == null) continue;
            if (change.signum() == 0) continue; // 보합 제외(원하시면 제거)

            all.add(StockItem.of(name, price, change, pct));
        }

        // 정렬: |changePct| desc → |change| desc → name asc
        Comparator<StockItem> byAbsPctDesc =
                Comparator.comparing((StockItem m) -> m.changePct().abs()).reversed()
                        .thenComparing(m -> m.change().abs(), Comparator.reverseOrder())
                        .thenComparing(StockItem::name);

        all.sort(byAbsPctDesc);

        // 상위 N개 제한(필요 시 props로 빼세요)
        final int N = 50;
        if (all.size() > N) {
            return all.subList(0, N);
        }
        return all;
    }

    /** 주어진 날짜의 모든 페이지 수집 */
    private List<JsonNode> fetchAllPagesForDate(LocalDate date) {
        int pageNo = 1;
        int pageSize = props.getPageSize();
        List<JsonNode> acc = new ArrayList<>();

        while (true) {
            JsonNode root = callGetStockPriceInfo(date, pageNo, pageSize);
            if (root == null) break;

            JsonNode arr = extractItems(root);
            if (arr == null) break;

            int got = 0;
            if (arr.isArray()) {
                for (JsonNode n : arr) { acc.add(n); got++; }
            } else if (arr.isObject()) {
                acc.add(arr); got = 1;
            }

            if (got < pageSize) break; // 마지막 페이지
            pageNo++;
            if (pageNo > 200) break;   // 안전장치
        }
        return acc;
    }

    /** 실제 API 호출 (basDt는 yyyyMMdd와 yyyy-MM-dd 두 포맷 시도) */
    private JsonNode callGetStockPriceInfo(LocalDate date, int pageNo, int pageSize) {
        String[] formats = { D8.format(date), D10.format(date) };

        for (String f : formats) {
            try {
                JsonNode root = krxWebClient.get()
                        .uri(u -> u.path("/getStockPriceInfo")
                                .queryParam("serviceKey", props.getServiceKey())
                                .queryParam("resultType", "json")
                                .queryParam("numOfRows", pageSize)
                                .queryParam("pageNo", pageNo)
                                .queryParam("basDt", f)
                                .build())
                        .accept(MediaType.APPLICATION_JSON)
                        .exchangeToMono(res -> {
                            var ct = res.headers().contentType().orElse(null);
                            if (ct != null && ct.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                                return res.bodyToMono(JsonNode.class);
                            }
                            return res.bodyToMono(String.class).flatMap(body ->
                                    Mono.error(new IllegalStateException(
                                            "Expected JSON but got %s, body=%s"
                                                    .formatted(ct, truncate(body, 300)))));
                        })
                        .block(Duration.ofSeconds(props.getTimeoutSeconds()));

                printHeaderIfPresent(root);
                return root;
            } catch (Exception e) {
                System.err.println("[KRX][WARN] call failed basDt=" + f + ", pageNo=" + pageNo + ": " + e.getMessage());
            }
        }
        return null;
    }

    /** items 배열 추출: response.body.items.item | items | result */
    private JsonNode extractItems(JsonNode root) {
        if (root == null) return null;

        if (root.has("response")) {
            var body = root.path("response").path("body");
            var node = body.path("items").path("item");
            if (!node.isMissingNode() && !node.isNull()) return node;
            node = body.path("items");
            if (!node.isMissingNode() && !node.isNull()) return node;
        }
        if (root.has("result")) {
            var node = root.path("result");
            if (!node.isMissingNode() && !node.isNull()) return node;
        }
        var node = root.path("items").path("item");
        if (!node.isMissingNode() && !node.isNull()) return node;
        node = root.path("items");
        if (!node.isMissingNode() && !node.isNull()) return node;

        System.out.println("[KRX] Unknown shape: " + truncate(root.toString(), 400));
        return null;
    }

    private void printHeaderIfPresent(JsonNode root) {
        try {
            if (root == null) return;
            if (root.has("response")) {
                var header = root.path("response").path("header");
                String code = header.path("resultCode").asText("");
                String msg  = header.path("resultMsg").asText("");
                var body    = root.path("response").path("body");
                int total   = body.path("totalCount").asInt(-1);
                System.out.println("[KRX][HDR] code=" + code + ", msg=" + msg + ", totalCount=" + total);
            } else if (root.has("resultCode")) {
                int code = root.path("resultCode").asInt(-1);
                String msg = root.path("resultMsg").asText("");
                int total = root.path("totalCount").asInt(-1);
                System.out.println("[KRX][HDR(flat)] code=" + code + ", msg=" + msg + ", totalCount=" + total);
            }
        } catch (Exception ignore) {}
    }

    private static BigDecimal toDecimal(String s) {
        if (s == null || s.isBlank()) return null;
        try { return new BigDecimal(s.replaceAll(",", "")); }
        catch (Exception e) { return null; }
    }

    private static String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max) + "..." : s;
    }
}