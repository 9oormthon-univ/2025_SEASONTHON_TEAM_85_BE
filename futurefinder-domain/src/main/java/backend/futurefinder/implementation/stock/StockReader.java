package backend.futurefinder.implementation.stock;

import backend.futurefinder.external.ExternalStockClient;
import backend.futurefinder.model.stock.StockItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StockReader {

    private final ExternalStockClient externalStockClient;

    public List<StockItem> fetchDailyMovers() {
        return externalStockClient.fetchDailyMovers();
    }
}