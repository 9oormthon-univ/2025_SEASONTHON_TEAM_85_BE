package backend.futurefinder.service.stock;

import backend.futurefinder.implementation.stock.StockReader;
import backend.futurefinder.model.stock.StockItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockReader stockReader;

    public List<StockItem> getDailyMovers() {
        return stockReader.fetchDailyMovers();
    }
}