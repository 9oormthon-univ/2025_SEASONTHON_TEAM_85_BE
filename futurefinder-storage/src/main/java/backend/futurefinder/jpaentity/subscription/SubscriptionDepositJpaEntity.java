package backend.futurefinder.jpaentity.subscription;

import backend.futurefinder.jpaentity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "subscription_deposit",
        indexes = {
                @Index(name = "sd_idx_user_time", columnList = "user_id, created_at"),
                @Index(name = "sd_idx_account_time", columnList = "subscription_account_id, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionDepositJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // BIGINT PK 가정(AUTO_INCREMENT 권장)
    @Column(name = "deposit_id", nullable = false)
    private Long id;

    @Column(name = "memo", length = 128)
    private String memo;

    @Column(name = "user_id", length = 128, nullable = false)
    private String userId;

    @Column(name = "deposit_amount", precision = 10, scale = 0, nullable = false)
    private BigDecimal depositAmount;


    @Column(name = "subscription_account_id", length = 128, nullable = false)
    private String subscriptionAccountId;

    @Builder
    public SubscriptionDepositJpaEntity(String memo,
                                        String userId,
                                        BigDecimal depositAmount,
                                        String subscriptionAccountId) {
        this.memo = memo;
        this.userId = userId;
        this.depositAmount = depositAmount != null ? depositAmount : BigDecimal.ZERO;
        this.subscriptionAccountId = subscriptionAccountId;
    }

    // 편의 메서드
    public void updateAmount(BigDecimal newAmount) {
        this.depositAmount = newAmount != null ? newAmount : BigDecimal.ZERO;
    }

    public void updateMemo(String newMemo) {
        this.memo = newMemo;
    }
}