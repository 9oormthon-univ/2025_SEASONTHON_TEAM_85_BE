package backend.futurefinder.repository.jpa.house;

import backend.futurefinder.jpaentity.house.ChatMessageJpaEntity;
import backend.futurefinder.jparepository.house.ChatMessageRepository;
import backend.futurefinder.model.house.ChatMessageEntry;
import backend.futurefinder.repository.house.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class ChatRepositoryImpl implements ChatRepository {

    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessageEntry saveMessage(String userId, String userMessage, String botResponse) {
        ChatMessageJpaEntity saved = chatMessageRepository.save(
                ChatMessageJpaEntity.builder()
                        .userId(userId)
                        .userMessage(userMessage)
                        .botResponse(botResponse)
                        .build()
        );

        return new ChatMessageEntry(
                saved.getId(),
                saved.getUserId(),
                saved.getUserMessage(),
                saved.getBotResponse(),
                saved.getCreatedAt()
        );
    }

    @Override
    public List<ChatMessageEntry> getRecentMessages(String userId, int limit) {
        return chatMessageRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, limit))
                .stream()
                .map(entity -> new ChatMessageEntry(
                        entity.getId(),
                        entity.getUserId(),
                        entity.getUserMessage(),
                        entity.getBotResponse(),
                        entity.getCreatedAt()
                ))
                .toList();
    }
}