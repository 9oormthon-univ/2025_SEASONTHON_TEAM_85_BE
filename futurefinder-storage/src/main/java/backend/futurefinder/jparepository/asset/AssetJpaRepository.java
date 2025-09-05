package backend.futurefinder.jparepository.asset;

import backend.futurefinder.jpaentity.asset.AssetJpaEntity;
import backend.futurefinder.model.asset.AssetType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetJpaRepository extends JpaRepository <AssetJpaEntity, String> {

    Optional<AssetJpaEntity> findByUserIdAndAssetType(String userId, AssetType assetType);
    Optional<AssetJpaEntity> findByUserIdAndAssetTypeAndBankNameAndAccountNumber(
            String userId, AssetType assetType, String bankName, String accountNumber
    );
    List<AssetJpaEntity> findByUserId(String userId, Sort sort);


}
