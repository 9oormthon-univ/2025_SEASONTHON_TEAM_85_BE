package backend.futurefinder.implementation.asset;


import backend.futurefinder.model.asset.Asset;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.repository.asset.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AssetReader {

    private final AssetRepository assetRepository;



    public List<Asset> reads(UserId userId){
        return assetRepository.reads(userId);
    }



}
