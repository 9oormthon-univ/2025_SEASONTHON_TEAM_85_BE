package backend.futurefinder.dto.response.house;

import java.time.LocalDateTime;

public record ChatResponse(
        Long messageId,
        String userMessage,
        String botResponse,
        LocalDateTime timestamp
) {}