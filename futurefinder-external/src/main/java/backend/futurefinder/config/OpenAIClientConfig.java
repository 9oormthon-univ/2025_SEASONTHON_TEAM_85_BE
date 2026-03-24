package backend.futurefinder.config;

import backend.futurefinder.dto.house.OpenAIRequestDto;
import backend.futurefinder.dto.house.OpenAIResponseDto;
import backend.futurefinder.property.OpenAIProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Slf4j
@Configuration
@EnableConfigurationProperties(OpenAIProperties.class)
public class OpenAIClientConfig {

    private final WebClient webClient;
    private final OpenAIProperties props;

    public OpenAIClientConfig(OpenAIProperties props) {
        this.props = props;
        this.webClient = WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + props.getKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public OpenAIResponseDto chatCompletion(OpenAIRequestDto requestDto) {
        try {
            return webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(OpenAIResponseDto.class)
                    .block(Duration.ofSeconds(props.getTimeoutSeconds()));
        } catch (Exception e) {
            log.error("OpenAI API 호출 실패", e);
            throw new RuntimeException("OpenAI API 호출 실패", e);
        }
    }

    public OpenAIProperties getProps() {
        return props;
    }
}
