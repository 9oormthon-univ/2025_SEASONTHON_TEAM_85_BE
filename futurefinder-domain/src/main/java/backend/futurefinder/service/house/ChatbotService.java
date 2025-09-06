package backend.futurefinder.service.house;

import backend.futurefinder.model.house.ChatMessageEntry;
import java.util.List;

public interface ChatbotService {
    String sendMessage(String userId, String message);
    List<ChatMessageEntry> getChatHistory(String userId, int limit);
}