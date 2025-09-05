package backend.futurefinder.jpaentity.economic;

import backend.futurefinder.jpaentity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "economic_word",
        schema = "futurefinder",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_word", columnNames = {"user_id", "word_name"})
        },
        indexes = {
                @Index(name = "idx_user_word", columnList = "user_id, word_name"),
                @Index(name = "idx_user_created_word", columnList = "user_id, created_at, word_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EconomicWordJpaEntity extends BaseEntity {

    @Id
    @Column(name = "word_id", length = 128, nullable = false)
    private String wordId;

    @Column(name = "word_name", length = 128, nullable = false)
    private String wordName;

    @Column(name = "meaning", nullable = false)
    private String meaning;

    @Column(name = "user_id", length = 128, nullable = false)
    private String userId;


    @Builder
    public EconomicWordJpaEntity(String wordName, String meaning, String userId) {
        this.wordId = UUID.randomUUID().toString();
        this.wordName = wordName;
        this.meaning  = meaning;
        this.userId = userId;
    }

    public static EconomicWordJpaEntity create(String wordName, String meaning, String userId) {
        return EconomicWordJpaEntity.builder()
                .wordName(wordName)
                .meaning(meaning)
                .userId(userId)
                .build();
    }

    public void updateMeaning(String newMeaning) {
        this.meaning = newMeaning;
    }

    public void rename(String newWordName) {
        this.wordName = newWordName;
    }


}