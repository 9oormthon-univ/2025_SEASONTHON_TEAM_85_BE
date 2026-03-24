package backend.futurefinder.jpaentity.job;

import backend.futurefinder.model.user.ActivityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "job_activity",
        schema = "futurefinder",
        indexes = {
                @Index(name = "ja_idx_user", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobActivityJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActivityType type;

    @Column(nullable = false)
    private String title;

    private LocalDate startedAt;
    private LocalDate endedAt;

    private String memo;
}
