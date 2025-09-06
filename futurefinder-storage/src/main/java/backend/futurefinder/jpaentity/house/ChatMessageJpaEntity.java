package backend.futurefinder.jpaentity.house;

import backend.futurefinder.jpaentity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "chat_message",
        indexes = {
                @Index(name = "cm_idx_user_time", columnList = "user_id, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(name = "user_id", length = 128, nullable = false)
    private String userId;

    @Column(name = "user_message", columnDefinition = "TEXT")
    private String userMessage;

    @Column(name = "bot_response", columnDefinition = "TEXT")
    private String botResponse;

    @Builder
    public ChatMessageJpaEntity(String userId, String userMessage, String botResponse) {
        this.userId = userId;
        this.userMessage = userMessage;
        this.botResponse = botResponse;
    }

    public void updateBotResponse(String botResponse) {
        this.botResponse = botResponse;
    }
}