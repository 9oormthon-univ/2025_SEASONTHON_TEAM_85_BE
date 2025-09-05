package backend.futurefinder.model.home;

import backend.futurefinder.model.news.NewsItem;
import backend.futurefinder.model.recruit.RecruitmentItem;
import backend.futurefinder.model.stock.StockItem;

import java.util.List;

public record HomeBundle(
        List<NewsItem> news,
        List<StockItem> stocks,
        List<RecruitmentItem> recruitments
) {
    public static HomeBundle of(List<NewsItem> news,
                                List<StockItem> stocks,
                                List<RecruitmentItem> recruitments) {
        return new HomeBundle(news, stocks, recruitments);
    }
}