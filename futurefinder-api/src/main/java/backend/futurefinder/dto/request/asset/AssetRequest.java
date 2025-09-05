package backend.futurefinder.dto.request.asset;

import backend.futurefinder.model.asset.AssetType;

import java.math.BigDecimal;

public record AssetRequest(
        AssetType assetType,
        BigDecimal amountKrw,
        String note,
        String bankName,
        String accountNumber
) {
    public AssetType toType() { return assetType; }

    public BigDecimal toAmountKrw() { return amountKrw; }

    public String toNote() { return note; }

    public String toBankName() { return bankName; }

    public String toAccountNumber() { return accountNumber; }


}