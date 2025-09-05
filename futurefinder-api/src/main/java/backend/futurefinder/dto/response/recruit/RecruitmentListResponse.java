package backend.futurefinder.dto.response.recruit;

import backend.futurefinder.dto.response.asset.AssetListResponse;
import backend.futurefinder.dto.response.asset.AssetResponse;
import backend.futurefinder.model.asset.Asset;
import backend.futurefinder.model.recruit.RecruitmentItem;

import java.util.List;

public record RecruitmentListResponse (
        List<RecruitmentResponse> recruitmentResponseList
){
    public static RecruitmentListResponse of(List<RecruitmentItem> recruitments) {
        List<RecruitmentResponse> mapped = recruitments.stream()
                .map(RecruitmentResponse::of)
                .toList();
        return new RecruitmentListResponse(mapped);

    }

}
