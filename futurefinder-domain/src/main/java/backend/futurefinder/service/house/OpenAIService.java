// futurefinder-domain/src/main/java/backend/futurefinder/service/external/OpenAIService.java
package backend.futurefinder.service.house;

import backend.futurefinder.model.house.ChatMessage;
import java.util.List;

public interface OpenAIService {
    String getChatCompletion(List<ChatMessage> messages);
}