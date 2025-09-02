package backend.futurefinder.jpaentity.user;

import backend.futurefinder.jpaentity.common.BaseEntity;
import backend.futurefinder.model.user.ActivityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_activity",
        indexes = {
                @Index(name = "ua_idx_user_time", columnList = "user_id, started_on, activity_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserActivityJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // BIGINT PK → AUTO_INCREMENT 가정
    @Column(name = "activity_id", nullable = false)
    private Long id;

    @Column(name = "activity_name", length = 100, nullable = false)
    private String activityName;

    @Column(name = "organization", length = 100, nullable = false)
    private String organization;

    @Column(name = "user_id", length = 128, nullable = false)
    private String userId;

    @Column(name = "description", length = 128)
    private String description;


    @Column(name = "started_on")
    private LocalDate startedOn;              // DDL: DATE

    @Column(name = "ended_on")
    private LocalDate endedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", length = 16) // ENUM(INTERNAL, EXTERNAL)
    private ActivityType activityType;

    @Builder
    public UserActivityJpaEntity(String activityName,
                                 String organization,
                                 String userId,
                                 String description,
                                 LocalDate startedOn,
                                 LocalDate endedOn,
                                 ActivityType activityType) {
        this.activityName = activityName;
        this.organization = organization;
        this.userId = userId;
        this.description = description;
        this.startedOn = startedOn;
        this.endedOn = endedOn;
        this.activityType = activityType;
    }

    // 편의 메서드
    public void updateBasic(String activityName, String organization, String description) {
        if (activityName != null) this.activityName = activityName;
        if (organization != null) this.organization = organization;
        this.description = description;
    }

    public void changePeriod(LocalDate startedOn, LocalDate endedOn) {
        this.startedOn = startedOn;
        this.endedOn = endedOn;
    }

    public void changeType(ActivityType type) {
        this.activityType = type;
    }
}