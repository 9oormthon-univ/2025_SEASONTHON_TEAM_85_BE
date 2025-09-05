package backend.futurefinder.implementation.news;

import backend.futurefinder.external.ExternalNewsClient;
import backend.futurefinder.model.news.NewsItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsReader {

    private final ExternalNewsClient externalNewsClient;

    public List<NewsItem> fetchByQuery(int page, int size) {
        return externalNewsClient.searchNews(page, size);
    }


}