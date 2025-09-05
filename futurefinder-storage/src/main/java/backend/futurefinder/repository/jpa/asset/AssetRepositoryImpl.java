package backend.futurefinder.repository.jpa.asset;


import backend.futurefinder.jpaentity.asset.AssetJpaEntity;
import backend.futurefinder.jparepository.asset.AssetJpaRepository;
import backend.futurefinder.model.asset.Asset;
import backend.futurefinder.model.asset.AssetType;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.repository.asset.AssetRepository;
import backend.futurefinder.util.SortType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AssetRepositoryImpl implements AssetRepository {

    private final AssetJpaRepository assetJpaRepository;

    @Override
    @Transactional
    public void append(UserId userId, AssetType assetType, BigDecimal amountKrw, String note,
                       String bankName, String accountNumber) {
        final String uid = userId.getId();
        final BigDecimal amount = amountKrw != null ? amountKrw : BigDecimal.ZERO;
        final String info = (note == null || note.isBlank()) ? null : note;
        final String bank = (bankName == null || bankName.isBlank()) ? null : bankName.trim();
        final String acct = (accountNumber == null || accountNumber.isBlank()) ? null : accountNumber.trim();

        Optional<AssetJpaEntity> existing =
                (bank != null && acct != null)
                        ? assetJpaRepository.findByUserIdAndAssetTypeAndBankNameAndAccountNumber(uid, assetType, bank, acct)
                        : assetJpaRepository.findByUserIdAndAssetType(uid, assetType);

        if (existing.isPresent()) {
            AssetJpaEntity e = existing.get();
            e.updateAmount(amount);
            e.updateInformation(info);
            if (bank != null || acct != null) e.updateBank(bank, acct);
            // 변경감지로 저장
        } else {
            assetJpaRepository.save(
                    AssetJpaEntity.create(assetType, amount, info, uid, bank, acct)
            );
        }

    }

    @Override
    public List<Asset> reads(UserId userId) {
        // 1) 1차 정렬: assetType ASC
        Sort primary = Sort.by(Sort.Direction.ASC, "assetType");
        // 2) 2차 정렬: createdAt (기존 SortType 사용) — 최신순이면 LATEST, 오래된 순이면 OLDEST 선택
        Sort sort = primary.and(SortType.LATEST.toSort());

        return assetJpaRepository.findByUserId(userId.getId(), sort).stream()
                .map(e -> Asset.of(
                        e.getAssetType(),
                        e.getAmountKrw(),
                        e.getAssetInformation(),
                        e.getBankName(),
                        e.getAccountNumber()
                ))
                .toList();
    }


}
