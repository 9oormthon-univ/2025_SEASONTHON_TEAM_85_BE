package backend.futurefinder.jpaentity.subscription;

import backend.futurefinder.jpaentity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "subscription_deposit",
        indexes = {
                @Index(name = "subdep_idx_user", columnList = "user_id"),
                @Index(name = "subdep_idx_account", columnList = "subscription_account_id"),
                @Index(name = "subdep_idx_user_created", columnList = "user_id, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionDepositJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_id", nullable = false)
    private Long id;

    @Column(name = "user_id", length = 128, nullable = false)
    private String userId;

    @Column(name = "subscription_account_id", length = 128, nullable = false)
    private String subscriptionAccountId;

    @Column(name = "deposit_amount", precision = 15, scale = 0, nullable = false)
    private BigDecimal depositAmount;

    @Column(name = "memo", length = 255)
    private String memo;

    @Builder
    public SubscriptionDepositJpaEntity(String userId,
                                        String subscriptionAccountId,
                                        BigDecimal depositAmount,
                                        String memo) {
        this.userId = userId;
        this.subscriptionAccountId = subscriptionAccountId;
        this.depositAmount = depositAmount;
        this.memo = memo;
    }

    // 계좌번호 업데이트 메서드 추가
    public void updateAccountNumber(String newAccountNumber) {
        if (newAccountNumber != null && !newAccountNumber.trim().isEmpty()) {
            this.subscriptionAccountId = newAccountNumber;
        }
    }

    // 입금액 수정 메서드 (필요한 경우)
    public void updateAmount(BigDecimal newAmount) {
        if (newAmount != null && newAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.depositAmount = newAmount;
        }
    }

    // 메모 수정 메서드 (필요한 경우)
    public void updateMemo(String newMemo) {
        this.memo = newMemo;
    }
}