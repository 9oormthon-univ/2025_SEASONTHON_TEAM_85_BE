package backend.futurefinder.dto.response.stock;

import backend.futurefinder.dto.response.recruit.RecruitmentListResponse;
import backend.futurefinder.dto.response.recruit.RecruitmentResponse;
import backend.futurefinder.model.recruit.RecruitmentItem;
import backend.futurefinder.model.stock.StockItem;

import java.util.List;

public record StockMoversResponse(
        List<StockMoverResponse> stocks
) {
    public static StockMoversResponse of(List<StockItem> m) {
        List<StockMoverResponse> mapped = m.stream()
                .map(StockMoverResponse::of)
                .toList();
        return new StockMoversResponse(mapped);

    }


}