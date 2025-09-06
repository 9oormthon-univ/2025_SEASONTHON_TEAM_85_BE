package backend.futurefinder.dto.house;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OpenAIResponseDto(
        String id,
        String object,
        Long created,
        String model,
        List<ChoiceDto> choices,
        UsageDto usage
) {
    public record ChoiceDto(
            Integer index,
            MessageDto message,
            @JsonProperty("finish_reason") String finishReason
    ) {}

    public record MessageDto(
            String role,
            String content
    ) {}

    public record UsageDto(
            @JsonProperty("prompt_tokens") Integer promptTokens,
            @JsonProperty("completion_tokens") Integer completionTokens,
            @JsonProperty("total_tokens") Integer totalTokens
    ) {}
}