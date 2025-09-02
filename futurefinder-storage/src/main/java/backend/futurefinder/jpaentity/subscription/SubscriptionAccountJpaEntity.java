package backend.futurefinder.jpaentity.subscription;
import backend.futurefinder.jpaentity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;


@Entity
@Table(
        name = "subscription_account",
        indexes = {
                @Index(name = "subacct_idx_user", columnList = "user_id"),
                @Index(name = "subacct_idx_account_no", columnList = "account_number", unique = true)
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionAccountJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // BIGINT PK → AUTO_INCREMENT 사용 시
    @Column(name = "subscription_account_id", nullable = false)
    private Long id;

    @Column(name = "bank_name", length = 100, nullable = false)
    private String bankName;

    @Column(name = "account_number", length = 128, nullable = false)
    private String accountNumber;

    @Column(name = "user_id", length = 128, nullable = false)
    private String userId;

    @Column(name = "total_amount", precision = 15, scale = 0, nullable = false)
    private BigDecimal totalAmount;


    @Builder
    public SubscriptionAccountJpaEntity(String bankName,
                                        String accountNumber,
                                        String userId,
                                        BigDecimal totalAmount) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    // 도메인 편의 메서드
    public void updateTotalAmount(BigDecimal newAmount) {
        this.totalAmount = newAmount != null ? newAmount : BigDecimal.ZERO;
    }

    public void changeBank(String newBankName) {
        this.bankName = newBankName;
    }
}