package backend.futurefinder.external;

import backend.futurefinder.config.OpenAIClientConfig;
import backend.futurefinder.dto.house.OpenAIRequestDto;
import backend.futurefinder.dto.house.OpenAIResponseDto;
import backend.futurefinder.model.house.ChatMessage;
import backend.futurefinder.property.OpenAIProperties;
import backend.futurefinder.service.house.OpenAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIServiceImpl implements OpenAIService {

    private final OpenAIClientConfig openAIClient;

    @Override
    public String getChatCompletion(List<ChatMessage> messages) {
        try {
            OpenAIProperties props = openAIClient.getProps();

            List<OpenAIRequestDto.MessageDto> messageDtos = messages.stream()
                    .map(msg -> OpenAIRequestDto.MessageDto.builder()
                            .role(msg.getRole())
                            .content(msg.getContent())
                            .build())
                    .toList();

            OpenAIRequestDto request = OpenAIRequestDto.builder()
                    .model(props.getModel())
                    .messages(messageDtos)
                    .maxTokens(props.getMaxTokens())
                    .temperature(props.getTemperature())
                    .build();

            OpenAIResponseDto response = openAIClient.chatCompletion(request);

            return response.choices().get(0).message().content();

        } catch (Exception e) {
            log.error("OpenAI 서비스 처리 실패", e);
            throw new RuntimeException("AI 응답 생성 실패", e);
        }
    }
}
