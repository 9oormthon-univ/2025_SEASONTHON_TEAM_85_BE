package backend.futurefinder.model.stock;

import java.math.BigDecimal;

public record StockItem(
        String name,
        BigDecimal price,
        BigDecimal change,     // 전일대비 금액 (등락폭, vs)
        BigDecimal changePct   // 등락률 (%)
) {
    public static StockItem of(String name, BigDecimal price, BigDecimal change, BigDecimal changePct) {
        return new StockItem(name, price, change, changePct);
    }
}