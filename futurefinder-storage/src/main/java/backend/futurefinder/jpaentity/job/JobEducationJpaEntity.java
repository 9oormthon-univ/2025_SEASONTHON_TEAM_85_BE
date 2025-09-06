package backend.futurefinder.jpaentity.job;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_education")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobEducationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String schoolName;

    @Column(nullable = false)
    private String major;

    @Column(nullable = false)
    private String status;            // 재학/휴학/졸업 등

    // 졸업연도는 null 가능하게 두는 게 안전
    private Integer graduationYear;
}
