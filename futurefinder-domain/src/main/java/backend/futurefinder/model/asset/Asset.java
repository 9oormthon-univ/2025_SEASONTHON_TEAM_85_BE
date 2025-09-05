package backend.futurefinder.model.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.Assert;

import java.math.BigDecimal;


@Getter
@AllArgsConstructor
public class Asset {


    private AssetType assetType;
    private BigDecimal amountKrw;
    private String assetInformation;
    private String bankName;
    private String accountNumber;

    public static Asset of(AssetType assetType, BigDecimal amountKrw, String assetInformation, String bankName, String accountNumber){
        return new Asset(
                assetType,
                amountKrw,
                assetInformation,
                bankName,
                accountNumber
        );
    }



}
