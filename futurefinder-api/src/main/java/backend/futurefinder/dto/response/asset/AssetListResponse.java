package backend.futurefinder.dto.response.asset;

import backend.futurefinder.model.asset.Asset;

import java.util.List;

public record AssetListResponse (
        List<AssetResponse> assets
){
    public static AssetListResponse of(List<Asset> assets) {
        List<AssetResponse> mapped = assets.stream()
                .map(AssetResponse::of)
                .toList();
        return new AssetListResponse(mapped);

    }

}
