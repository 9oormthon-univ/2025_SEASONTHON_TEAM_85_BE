package backend.futurefinder.dto.request.house;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank String message
) {}