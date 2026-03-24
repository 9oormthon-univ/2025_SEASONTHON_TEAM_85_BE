package backend.futurefinder.external;

import backend.futurefinder.dto.word.EcosWordEnvelope;
import backend.futurefinder.external.support.ExternalClientUtils;
import backend.futurefinder.property.BokEcosProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExternalSearchClientImpl implements ExternalSearchClient {

    private final WebClient ecosWebClient;
    private final BokEcosProperties props;

    @Override
    public Optional<String> lookupMeaning(String term) {
        String normalizedInput = normalize(term);
        if (normalizedInput.isEmpty()) return Optional.empty();

        EcosWordEnvelope body = ecosWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("api","StatisticWord", props.getApiKey(),
                                props.getFormat(), props.getLang(),
                                "1", String.valueOf(props.getPageSize()),
                                normalizedInput)
                        .build())
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        res -> res.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(b -> Mono.error(new IllegalStateException(
                                        "ECOS API error: HTTP %s, body=%s"
                                                .formatted(res.statusCode(), ExternalClientUtils.truncate(b, 500))))))
                .bodyToMono(EcosWordEnvelope.class)
                .block(Duration.ofSeconds(props.getTimeoutSeconds()));

        if (body == null || body.statisticWord() == null || body.statisticWord().row() == null) {
            return Optional.empty();
        }

        // ✅ "유하지만 정확한" 매칭: 괄호/공백/말미기호 무시 후 완전일치
        return body.statisticWord().row().stream()
                .filter(r -> canonicalize(r.word()).equalsIgnoreCase(canonicalize(normalizedInput)))
                .findFirst()
                .map(r -> {
                    String content = r.content() == null ? "" : r.content().strip();
                    int max = props.getMaxContentLength();
                    return (max > 0 && content.length() > max) ? content.substring(0, max) : content;
                });
    }

    /** 기본 정규화 (strip + NFC) */
    private static String normalize(String s) {
        if (s == null) return "";
        String t = s.strip();
        return java.text.Normalizer.normalize(t, java.text.Normalizer.Form.NFC);
    }

    /** 괄호부(끝쪽) 반복 제거 + 내부 공백 정규화 + 말미 기호 제거 */
    private static String canonicalize(String s) {
        String x = normalize(s);
        // 내부 공백을 한 칸으로
        x = x.replaceAll("\\s+", " ").trim();

        // 말미 괄호부 반복 제거: (), [], {}, 전각괄호 등
        String before;
        do {
            before = x;
            x = x.replaceAll("\\s*[\\(\\[\\{（［｛〔【][^\\)\\]\\}）］｝〕】]*[\\)\\]\\}）］｝〕】]\\s*$", "")
                    .trim();
        } while (!x.equals(before));

        // 말미 구두점/기호 제거(콜론, 대시, 점 등)
        x = x.replaceAll("[\\p{Punct}·•:：–—…]+$", "").trim();
        return x;
    }

}