package backend.futurefinder.dto.response.asset;

import backend.futurefinder.model.asset.Asset;
import backend.futurefinder.model.asset.AssetType;

import java.math.BigDecimal;

public record AssetResponse (
        AssetType assetType,
        BigDecimal amountKrw,
        String assetInformation,
        String bankName,
        String accountNumber
){

    public static AssetResponse of(Asset asset){
        return new AssetResponse(
                asset.getAssetType(),
                asset.getAmountKrw(),
                asset.getAssetInformation(),
                asset.getBankName(),
                asset.getAccountNumber()
        );
    }




}
