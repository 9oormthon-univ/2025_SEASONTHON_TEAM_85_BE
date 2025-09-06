package backend.futurefinder.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import backend.futurefinder.dto.house.OpenAIRequestDto;
import backend.futurefinder.dto.house.OpenAIResponseDto;

@Slf4j
@Component
public class OpenAIClientConfig {

    @Value("${openai.api.key}")
    private String apiKey;

    private final String baseUrl = "https://api.openai.com/v1";
    private WebClient webClient;

    @PostConstruct
    public void init() {
        webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
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
                    .block();
        } catch (Exception e) {
            log.error("OpenAI API 호출 실패", e);
            throw new RuntimeException("OpenAI API 호출 실패", e);
        }
    }
}