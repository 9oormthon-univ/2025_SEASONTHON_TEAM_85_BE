package backend.futurefinder.external;

import backend.futurefinder.model.stock.StockItem;

import java.util.List;

public interface ExternalStockClient {
    List<StockItem> fetchDailyMovers();
}
