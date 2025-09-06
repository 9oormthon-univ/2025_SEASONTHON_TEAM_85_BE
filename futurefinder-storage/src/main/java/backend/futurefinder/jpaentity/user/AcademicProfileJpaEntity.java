package backend.futurefinder.jpaentity.user;

import backend.futurefinder.jpaentity.common.BaseEntity;
import backend.futurefinder.model.user.GraduationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "academic_profile",
        indexes = {
                @Index(name = "ap_idx_user", columnList = "user_id"),
                @Index(name = "ap_idx_user_grad", columnList = "user_id, graduation_status")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AcademicProfileJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // BIGINT AUTO_INCREMENT 가정
    @Column(name = "academic_id", nullable = false)
    private Long id;

    @Column(name = "school_name", length = 100, nullable = false)
    private String schoolName;

    @Column(name = "major_name", length = 100, nullable = false)
    private String majorName;

    @Column(name = "minor_name", length = 128)
    private String minorName; // (설명 주석은 계좌번호가 아니라 부전공명)

    // DECIMAL(3,2): 0.00 ~ 9.99 범위 (4.3/4.5 가능)
    @Column(name = "max_gpa", precision = 3, scale = 2, nullable = false)
    private BigDecimal maxGpa;

    @Column(name = "current_gpa", precision = 3, scale = 2)
    private BigDecimal currentGpa;

    @Column(name = "user_id", length = 128, nullable = false)
    private String userId;


    @Enumerated(EnumType.STRING)
    @Column(name = "graduation_status", length = 16) // ENUM('ENROLLED','GRADUATED','ON_LEAVE')
    private GraduationStatus graduationStatus;

    @Builder
    public AcademicProfileJpaEntity(String schoolName,
                                    String majorName,
                                    String minorName,
                                    BigDecimal maxGpa,
                                    BigDecimal currentGpa,
                                    String userId,
                                    GraduationStatus graduationStatus) {
        this.schoolName = schoolName;
        this.majorName = majorName;
        this.minorName = minorName;
        this.maxGpa = maxGpa;
        this.currentGpa = currentGpa;
        this.userId = userId;
        this.graduationStatus = graduationStatus;
    }

    // 편의 메서드
    public void updateGpa(BigDecimal currentGpa, BigDecimal maxGpa) {
        if (currentGpa != null) this.currentGpa = currentGpa;
        if (maxGpa != null) this.maxGpa = maxGpa;
    }

    public void updateInfo(String schoolName, String major, String minor, BigDecimal currentGpa, BigDecimal maxGpa, GraduationStatus status) {
        this.schoolName = schoolName;
        this.majorName = major;
        this.minorName = minor;
        this.currentGpa = currentGpa;
        this.maxGpa = maxGpa;
        this.graduationStatus = status;
    }

    public void changeStatus(GraduationStatus status) {
        this.graduationStatus = status;
    }


}

