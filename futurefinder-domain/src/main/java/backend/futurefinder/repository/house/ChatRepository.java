// futurefinder-domain/src/main/java/backend/futurefinder/repository/house/ChatRepository.java
package backend.futurefinder.repository.house;

import backend.futurefinder.model.house.ChatMessageEntry;
import java.util.List;

public interface ChatRepository {
    ChatMessageEntry save(String userId, String userMessage, String botResponse);
    List<ChatMessageEntry> findRecentMessages(String userId, int limit);
}