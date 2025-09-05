package backend.futurefinder.repository.word;

import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.word.PopularWord;
import backend.futurefinder.model.word.ScrapWord;

import java.util.List;

public interface WordRepository {

    boolean append(UserId userId, String word, String meaning);
    List<ScrapWord> reads(UserId userId, int page);
    List<PopularWord> readPopularNames();
}
