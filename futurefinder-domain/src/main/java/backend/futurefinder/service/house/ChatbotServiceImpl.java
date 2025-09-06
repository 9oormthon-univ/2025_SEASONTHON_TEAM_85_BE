// futurefinder-domain/src/main/java/backend/futurefinder/service/house/ChatbotServiceImpl.java
package backend.futurefinder.service.house;

import backend.futurefinder.model.house.ChatMessage;
import backend.futurefinder.model.house.ChatMessageEntry;
import backend.futurefinder.repository.house.ChatRepository;
import backend.futurefinder.service.house.OpenAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {

    private final ChatRepository chatRepository;
    private final OpenAIService openAIService;

    @Override
    public String sendMessage(String userId, String message) {
        try {
            List<ChatMessage> messages = List.of(
                    ChatMessage.system("당신은 한국의 청약 전문가입니다. 친근하고 정확하게 답변해주세요."),
                    ChatMessage.user(message)
            );

            String response = openAIService.getChatCompletion(messages);
            chatRepository.saveMessage(userId, message, response);

            return response;

        } catch (Exception e) {
            log.error("ChatBot 처리 실패: ", e);
            return "죄송합니다. 잠시 후 다시 시도해주세요.";
        }
    }

    @Override
    public List<ChatMessageEntry> getChatHistory(String userId, int limit) {
        return chatRepository.getRecentMessages(userId, limit);
    }
}