package backend.futurefinder.jpaentity.asset;

import backend.futurefinder.jpaentity.common.BaseEntity;
import backend.futurefinder.model.asset.AssetType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;



@Entity
@Table(
        name = "asset",
        schema = "futurefinder",
        indexes = {
                @Index(name = "asset_idx_type", columnList = "asset_type")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssetJpaEntity extends BaseEntity {

    @Id
    @Column(name = "asset_id", length = 128, nullable = false)
    private String assetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", length = 32)
    private AssetType assetType;

    @Column(name = "amount_krw", precision = 18, scale = 2, nullable = false)
    private BigDecimal amountKrw;

    @Column(name = "asset_information", length = 128)
    private String assetInformation;

    @Column(name = "user_id", length = 128, nullable = false)
    private String userId;

    @Column(name = "bank_name", length = 100, nullable = false)
    private String bankName;

    @Column(name = "account_number", length = 128, nullable = false)
    private String accountNumber;

    @Builder
    public AssetJpaEntity(AssetType assetType, BigDecimal amountKrw, String assetInformation, String userId, String bankName, String accountNumber) {
        this.assetId = UUID.randomUUID().toString();
        this.assetType = assetType;
        this.amountKrw = amountKrw != null ? amountKrw : BigDecimal.ZERO;
        this.assetInformation = assetInformation;
        this.userId = userId;
        this.bankName = bankName;
        this.accountNumber = accountNumber;

    }

    public static AssetJpaEntity create(AssetType type, BigDecimal amount, String info, String userId, String bankName, String accountNumber) {
        return AssetJpaEntity.builder()
                .assetType(type)
                .amountKrw(amount)
                .assetInformation(info)
                .userId(userId)
                .bankName(bankName)
                .accountNumber(accountNumber)
                .build();
    }

    public void updateAmount(BigDecimal newAmount) {
        this.amountKrw = newAmount != null ? newAmount : BigDecimal.ZERO;
    }

    public void updateInformation(String info) {
        this.assetInformation = info;
    }

    public void changeType(AssetType newType) {
        this.assetType = newType;
    }

    public void updateBank(String bankName, String accountNumber) {
        this.bankName = normalize(bankName);
        this.accountNumber = normalize(accountNumber);
    }

    private String normalize(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }



}