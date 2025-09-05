package backend.futurefinder.service.news;

import backend.futurefinder.implementation.news.NewsReader;
import backend.futurefinder.model.news.NewsItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsReader newsReader;

    public List<NewsItem> getEconomyNews(int page, int size){

        return newsReader.fetchByQuery(page, size);
    }


}
