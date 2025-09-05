package backend.futurefinder.jpaentity.user;


import backend.futurefinder.model.user.LocationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_location",
        indexes = {
                @Index(name = "ul_idx_user", columnList = "user_id"),
                @Index(name = "ul_idx_user_type", columnList = "user_id, location_type")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLocationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // BIGINT AUTO_INCREMENT 가정
    @Column(name = "location_id", nullable = false)
    private Long id;

    @Column(name = "province", length = 128, nullable = false)
    private String province;

    @Column(name = "city", length = 128, nullable = false)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", length = 16) // ENUM('CURRENT','INTEREST')
    private LocationType locationType;

    @Column(name = "user_id", length = 128, nullable = false)
    private String userId;

    @Builder
    public UserLocationJpaEntity(String province,
                                 String city,
                                 LocationType locationType,
                                 String userId) {
        this.province = province;
        this.city = city;
        this.locationType = locationType;
        this.userId = userId;
    }

    // 편의 메서드
    public void changeAddress(String province, String city) {
        if (province != null) this.province = province;
        if (city != null) this.city = city;
    }

    public void changeType(LocationType type) {
        this.locationType = type;
    }
}