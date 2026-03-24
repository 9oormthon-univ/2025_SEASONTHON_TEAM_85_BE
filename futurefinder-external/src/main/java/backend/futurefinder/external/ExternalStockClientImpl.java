package backend.futurefinder.external;

import backend.futurefinder.external.support.GovApiJsonParser;
import backend.futurefinder.model.stock.StockItem;
import backend.futurefinder.property.StockApiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static backend.futurefinder.external.support.ExternalClientUtils.truncate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalStockClientImpl implements ExternalStockClient {

    private static final String TAG = "KRX";

    private final WebClient krxWebClient;
    private final StockApiProperties props;

    private static final DateTimeFormatter D8 = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter D10 = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public List<StockItem> fetchDailyMovers() {
        ZoneId KST = ZoneId.of("Asia/Seoul");
        LocalDate base = LocalDate.now(KST);
        List<JsonNode> items = Collections.emptyList();

        for (int i = 0; i < props.getMaxBackDays(); i++) {
            LocalDate target = base.minusDays(i);
            items = fetchAllPagesForDate(target);
            if (!items.isEmpty()) {
                log.debug("[{}] basDt={}, items={}", TAG, target, items.size());
                break;
            }
        }
        if (items.isEmpty()) return List.of();

        List<StockItem> all = new ArrayList<>();
        for (JsonNode n : items) {
            String name = n.path("itmsNm").asText("");
            BigDecimal price = toDecimal(n.path("clpr").asText(""));
            BigDecimal change = toDecimal(n.path("vs").asText(""));
            BigDecimal pct = toDecimal(n.path("fltRt").asText(""));

            if (name.isBlank() || price == null || change == null || pct == null) continue;
            if (change.signum() == 0) continue;

            all.add(StockItem.of(name, price, change, pct));
        }

        all.sort(Comparator.comparing((StockItem m) -> m.changePct().abs()).reversed()
                .thenComparing(m -> m.change().abs(), Comparator.reverseOrder())
                .thenComparing(StockItem::name));

        int topLimit = props.getTopLimit();
        if (all.size() > topLimit) {
            return all.subList(0, topLimit);
        }
        return all;
    }

    private List<JsonNode> fetchAllPagesForDate(LocalDate date) {
        int pageNo = 1;
        int pageSize = props.getPageSize();
        List<JsonNode> acc = new ArrayList<>();

        while (true) {
            JsonNode root = callGetStockPriceInfo(date, pageNo, pageSize);
            if (root == null) break;

            JsonNode arr = GovApiJsonParser.extractItems(root, TAG);
            if (arr == null) break;

            int got = 0;
            if (arr.isArray()) {
                for (JsonNode n : arr) { acc.add(n); got++; }
            } else if (arr.isObject()) {
                acc.add(arr); got = 1;
            }

            if (got < pageSize) break;
            pageNo++;
            if (pageNo > 200) break;
        }
        return acc;
    }

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

                GovApiJsonParser.logHeader(root, TAG);
                return root;
            } catch (Exception e) {
                log.warn("[{}] call failed basDt={}, pageNo={}: {}", TAG, f, pageNo, e.getMessage());
            }
        }
        return null;
    }

    private static BigDecimal toDecimal(String s) {
        if (s == null || s.isBlank()) return null;
        try { return new BigDecimal(s.replaceAll(",", "")); }
        catch (Exception e) { return null; }
    }
}
