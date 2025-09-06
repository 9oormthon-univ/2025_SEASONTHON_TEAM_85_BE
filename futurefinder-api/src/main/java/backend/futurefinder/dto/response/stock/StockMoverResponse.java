package backend.futurefinder.dto.response.stock;

import backend.futurefinder.dto.response.recruit.RecruitmentResponse;
import backend.futurefinder.model.recruit.RecruitmentItem;
import backend.futurefinder.model.stock.StockItem;

import java.math.BigDecimal;

public record StockMoverResponse(
        String name,
        BigDecimal price,
        BigDecimal change,     // 전일대비 금액 (등락폭, vs)
        BigDecimal changePct

) {
    public static StockMoverResponse of(StockItem item) {
        return new StockMoverResponse(item.name(), item.price(), item.change(), item.changePct());
    }


}
