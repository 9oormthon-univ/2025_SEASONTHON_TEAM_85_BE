package backend.futurefinder.external.support;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

/**
 * 공공데이터포털 API 공통 JSON 응답 파싱.
 * Recruitment(MOEF)와 Stock(KRX) 모두 동일한 응답 구조를 사용한다.
 */
@Slf4j
public final class GovApiJsonParser {

    private GovApiJsonParser() {}

    /**
     * 공공데이터 응답에서 items 배열을 추출한다.
     * 지원 구조: response.body.items.item | result | items.item | items
     */
    public static JsonNode extractItems(JsonNode root, String tag) {
        if (root == null) return null;

        if (root.has("response")) {
            var body = root.path("response").path("body");
            var node = body.path("items").path("item");
            if (isPresent(node)) return node;
            node = body.path("items");
            if (isPresent(node)) return node;
        }
        if (root.has("result")) {
            var node = root.path("result");
            if (isPresent(node)) return node;
        }
        var node = root.path("items").path("item");
        if (isPresent(node)) return node;
        node = root.path("items");
        if (isPresent(node)) return node;

        log.debug("[{}] Unknown response shape: {}", tag, ExternalClientUtils.truncate(root.toString(), 400));
        return null;
    }

    /**
     * 공공데이터 응답 헤더(resultCode, resultMsg, totalCount)를 로깅한다.
     */
    public static void logHeader(JsonNode root, String tag) {
        try {
            if (root == null) return;

            if (root.has("response")) {
                var header = root.path("response").path("header");
                String code = header.path("resultCode").asText("");
                String msg = header.path("resultMsg").asText("");
                var body = root.path("response").path("body");
                int total = body.path("totalCount").asInt(-1);
                int pageNo = body.path("pageNo").asInt(-1);
                int rows = body.path("numOfRows").asInt(-1);
                log.debug("[{}] resultCode={}, resultMsg={}, totalCount={}, pageNo={}, numOfRows={}",
                        tag, code, msg, total, pageNo, rows);
                if (!code.isEmpty() && !"00".equals(code)) {
                    log.warn("[{}] Non-success resultCode: {} ({})", tag, code, msg);
                }
                return;
            }

            if (root.has("resultCode")) {
                int code = root.path("resultCode").asInt(-1);
                String msg = root.path("resultMsg").asText("");
                int total = root.path("totalCount").asInt(-1);
                log.debug("[{}] (flat) resultCode={}, resultMsg={}, totalCount={}", tag, code, msg, total);
                if (code != 200 && code != 0) {
                    log.warn("[{}] Non-success resultCode: {} ({})", tag, code, msg);
                }
            }
        } catch (Exception e) {
            log.debug("[{}] logHeader failed: {}", tag, e.getMessage());
        }
    }

    private static boolean isPresent(JsonNode node) {
        return node != null && !node.isMissingNode() && !node.isNull();
    }
}
