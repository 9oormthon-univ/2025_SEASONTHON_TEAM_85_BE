package backend.futurefinder.dto.house;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record OpenAIRequestDto(
        String model,
        List<MessageDto> messages,
        @JsonProperty("max_tokens") Integer maxTokens,
        Double temperature
) {
    @Builder
    public record MessageDto(
            String role,
            String content
    ) {}
}