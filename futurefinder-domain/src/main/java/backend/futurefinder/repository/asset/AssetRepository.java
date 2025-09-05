package backend.futurefinder.repository.asset;

import backend.futurefinder.model.asset.Asset;
import backend.futurefinder.model.asset.AssetType;
import backend.futurefinder.model.user.UserId;

import java.math.BigDecimal;
import java.util.List;

public interface AssetRepository {
    void append(UserId userId, AssetType assetType, BigDecimal amountKrw, String note, String BankName, String accountNumber);
    List<Asset> reads(UserId userId);

}
