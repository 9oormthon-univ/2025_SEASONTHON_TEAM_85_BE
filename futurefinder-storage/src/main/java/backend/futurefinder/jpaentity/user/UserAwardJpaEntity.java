package backend.futurefinder.jpaentity.user;

import backend.futurefinder.jpaentity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_award",
        indexes = {
                @Index(name = "ua_idx_user_time", columnList = "user_id, awarded_on, award_id"),
                @Index(name = "ua_idx_activity", columnList = "activity_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAwardJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)      // BIGINT AUTO_INCREMENT 가정
    @Column(name = "award_id", nullable = false)
    private Long id;

    @Column(name = "award_name", length = 100, nullable = false)
    private String awardName;

    @Column(name = "organization", length = 100, nullable = false)
    private String organization;


    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "user_id", length = 128, nullable = false)
    private String userId;

    @Column(name = "description", length = 128)
    private String description;

    @Column(name = "awarded_on")
    private LocalDate awardedOn;                 // DATE → LocalDate

    @Column(name = "ended_on", length = 128)
    private LocalDate endedOn;

    @Builder
    public UserAwardJpaEntity(String awardName,
                              String organization,
                              Long activityId,
                              String userId,
                              String description,
                              LocalDate awardedOn,
                              LocalDate endedOn) {
        this.awardName = awardName;
        this.organization = organization;
        this.activityId = activityId;
        this.userId = userId;
        this.description = description;
        this.awardedOn = awardedOn;
        this.endedOn = endedOn;
    }

    // 편의 메서드
    public void updateBasic(String awardName, String organization, String description) {
        if (awardName != null) this.awardName = awardName;
        if (organization != null) this.organization = organization;
        this.description = description;
    }

    public void linkActivity(Long activityId) {
        this.activityId = activityId;
    }

    public void changeDates(LocalDate awardedOn, LocalDate endedOn) {
        this.awardedOn = awardedOn;
        this.endedOn = endedOn;
    }
}