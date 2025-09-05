package backend.futurefinder.dto.response.home;

import backend.futurefinder.dto.response.news.NewsItemResponse;
import backend.futurefinder.dto.response.recruit.RecruitmentResponse;
import backend.futurefinder.dto.response.stock.StockMoverResponse;
import backend.futurefinder.model.home.HomeBundle;

import java.util.List;

public record HomeResponse(
        List<NewsItemResponse> news,
        List<StockMoverResponse> stocks,
        List<RecruitmentResponse> recruitments
) {
    public static HomeResponse of(HomeBundle b) {
        List<NewsItemResponse> news = b.news().stream()
                .limit(2) // 안전 차단(파사드는 이미 2개로 잘라줌)
                .map(NewsItemResponse::from)
                .toList();

        List<StockMoverResponse> stocks = b.stocks().stream()
                .limit(3)
                .map(StockMoverResponse::of)
                .toList();

        List<RecruitmentResponse> recruitments = b.recruitments().stream()
                .limit(3)
                .map(RecruitmentResponse::of)
                .toList();

        return new HomeResponse(news, stocks, recruitments);
    }
}