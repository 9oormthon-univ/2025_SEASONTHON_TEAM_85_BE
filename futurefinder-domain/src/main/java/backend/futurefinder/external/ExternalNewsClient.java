package backend.futurefinder.external;

import backend.futurefinder.model.news.NewsItem;

import java.util.List;

public interface ExternalNewsClient {
    /**
     * 네이버 뉴스 html 스크래핑
     * @param size   1~100
     */
    List<NewsItem> searchNews(int page, int size);
}