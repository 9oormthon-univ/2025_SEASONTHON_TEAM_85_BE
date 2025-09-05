package backend.futurefinder.service.asset;


import backend.futurefinder.implementation.asset.AssetAppender;
import backend.futurefinder.implementation.asset.AssetReader;
import backend.futurefinder.model.asset.Asset;
import backend.futurefinder.model.asset.AssetType;
import backend.futurefinder.model.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetAppender assetAppender;
    private final AssetReader assetReader;

    public void createAsset(UserId userId, AssetType assetType, BigDecimal amountKrw, String note, String BankName, String accountNumber){
        assetAppender.append(userId, assetType, amountKrw, note, BankName, accountNumber);
    }

    public List<Asset> getAssets(UserId userId){
        return assetReader.reads(userId);
    }

}
