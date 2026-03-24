package backend.futurefinder.implementation.asset;

import backend.futurefinder.model.asset.AssetType;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.repository.asset.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AssetAppender {

    private final AssetRepository assetRepository;

    public void append(UserId userId, AssetType assetType, BigDecimal amountKrw, String note, String BankName, String accountNumber){
        assetRepository.save(userId, assetType, amountKrw, note, BankName, accountNumber);
    }





}
