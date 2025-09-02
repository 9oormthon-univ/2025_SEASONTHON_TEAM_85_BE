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
        indexes = {
                @Index(name = "eco_word_idx_name", columnList = "word_name", unique = true)
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

    @Column(name = "meaning", length = 128, nullable = false)
    private String meaning;



    @Builder
    public EconomicWordJpaEntity(String wordName, String meaning) {
        this.wordId = UUID.randomUUID().toString();
        this.wordName = wordName;
        this.meaning  = meaning;
    }

    public static EconomicWordJpaEntity create(String wordName, String meaning) {
        return EconomicWordJpaEntity.builder()
                .wordName(wordName)
                .meaning(meaning)
                .build();
    }

    public void updateMeaning(String newMeaning) {
        this.meaning = newMeaning;
    }

    public void rename(String newWordName) {
        this.wordName = newWordName;
    }
}