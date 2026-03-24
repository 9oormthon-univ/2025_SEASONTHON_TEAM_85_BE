package backend.futurefinder.external;

import backend.futurefinder.external.support.ExternalClientUtils;
import backend.futurefinder.external.support.GovApiJsonParser;
import backend.futurefinder.external.support.TtlCache;
import backend.futurefinder.model.recruit.RecruitmentItem;
import backend.futurefinder.property.RecruitmentApiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static backend.futurefinder.external.support.ExternalClientUtils.nvl;
import static backend.futurefinder.external.support.ExternalClientUtils.truncate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalRecruitmentClientImpl implements ExternalRecruitmentClient {

    private static final String TAG = "MOEF";

    private final WebClient moefRecruitmentWebClient;
    private final RecruitmentApiProperties props;
    private final TtlCache<List<RecruitmentItem>> cache = new TtlCache<>();

    @Override
    public List<RecruitmentItem> fetch(int page) {
        int safePage = Math.max(page, 1);

        var tomorrow = LocalDate.now(ZoneId.of("Asia/Seoul")).plusDays(1);
        List<RecruitmentItem> all = cache.get(
                tomorrow,
                Duration.ofSeconds(props.getCacheTtlSeconds()),
                () -> fetchFromRemote(tomorrow)
        );

        int from = (safePage - 1) * props.getPageSize();
        if (from >= all.size()) return List.of();
        int to = Math.min(from + props.getPageSize(), all.size());
        log.debug("[{}][PAGE] req={}, from={}, to={}, total={}", TAG, safePage, from, to, all.size());
        return new ArrayList<>(all.subList(from, to));
    }

    private List<RecruitmentItem> fetchFromRemote(LocalDate pbancEndYmd) {
        log.debug("[{}][REQ] ongoingYn=Y, pbancEndYmd={}, numOfRows={}", TAG, pbancEndYmd, props.getFetchSize());

        JsonNode root = moefRecruitmentWebClient.get()
                .uri(uri -> uri.path("/list")
                        .queryParam("serviceKey", props.getServiceKey())
                        .queryParam("resultType", "json")
                        .queryParam("ongoingYn", "Y")
                        .queryParam("pbancEndYmd", pbancEndYmd.toString())
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", props.getFetchSize())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(res -> {
                    MediaType ct = res.headers().contentType().orElse(null);
                    if (ct != null && ct.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                        return res.bodyToMono(JsonNode.class)
                                .doOnNext(r -> GovApiJsonParser.logHeader(r, TAG));
                    }
                    return res.bodyToMono(String.class).flatMap(body ->
                            Mono.error(new IllegalStateException("Expected JSON but got %s, body=%s"
                                    .formatted(ct, truncate(body, 500)))));
                })
                .block(Duration.ofSeconds(props.getTimeoutSeconds()));

        return parseItems(root);
    }

    private List<RecruitmentItem> parseItems(JsonNode root) {
        JsonNode items = GovApiJsonParser.extractItems(root, TAG);

        List<RecruitmentItem> out = new ArrayList<>();
        if (items != null && items.isArray()) {
            for (JsonNode n : items) out.add(mapItem(n));
        } else if (items != null && items.isObject()) {
            out.add(mapItem(items));
        }
        return out;
    }

    private RecruitmentItem mapItem(JsonNode n) {
        String institute = n.path("instNm").asText("");
        String title = n.path("recrutPbancTtl").asText("");
        String recruitSe = n.path("recrutSeNm").asText(n.path("recrutSe").asText(""));
        String hireType = firstFromCommaList(
                n.path("hireTypeNmLst").asText(n.path("hireTypeLst").asText(""))
        );
        return RecruitmentItem.of(nvl(institute), nvl(title), nvl(recruitSe), nvl(hireType));
    }

    private static String firstFromCommaList(String s) {
        if (s == null || s.isBlank()) return "";
        for (String t : s.split(",")) {
            String v = t.trim();
            if (!v.isEmpty()) return v;
        }
        return "";
    }
}
