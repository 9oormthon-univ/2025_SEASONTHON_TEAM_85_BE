package backend.futurefinder.model.house;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageEntry {
    private final Long id;
    private final String userId;
    private final String userMessage;
    private final String botResponse;
    private final LocalDateTime createdAt;
}