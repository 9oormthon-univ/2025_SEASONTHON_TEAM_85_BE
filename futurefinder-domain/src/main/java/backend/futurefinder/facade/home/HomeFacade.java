package backend.futurefinder.facade.home;

import backend.futurefinder.model.home.HomeBundle;
import backend.futurefinder.model.news.NewsItem;
import backend.futurefinder.model.recruit.RecruitmentItem;
import backend.futurefinder.model.stock.StockItem;
import backend.futurefinder.service.news.NewsService;
import backend.futurefinder.service.recruit.RecruitmentService;
import backend.futurefinder.service.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeFacade {

    private final NewsService newsService;
    private final StockService stockService;
    private final RecruitmentService recruitmentService;

    /**
     * 각 서비스의 기존 정렬/페이지 규칙을 그대로 활용.
     * - 뉴스: 내부 PAGE_SIZE=5 이므로 page=1 호출 후 상위 2개만 취함
     * - 주식: 이미 |changePct| desc로 정렬됨 → 상위 3개
     * - 채용: 내부 PAGE_SIZE=5 이므로 page=1 호출 후 상위 3개만 취함
     */
    public HomeBundle getHomeBundle() {
        List<NewsItem> news = newsService.getEconomyNews(1, 5); // size는 외부 구현에서 무시될 수 있음
        if (news.size() > 2) news = news.subList(0, 2);

        List<StockItem> stocks = stockService.getDailyMovers();
        if (stocks.size() > 3) stocks = stocks.subList(0, 3);

        List<RecruitmentItem> recruitments = recruitmentService.getRecruitmentList(1);
        if (recruitments.size() > 3) recruitments = recruitments.subList(0, 3);

        return HomeBundle.of(news, stocks, recruitments);
    }


}