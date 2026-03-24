package backend.futurefinder.repository.word;

import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.word.PopularWord;
import backend.futurefinder.model.word.ScrapWord;

import java.util.List;

public interface WordRepository {

    boolean save(UserId userId, String word, String meaning);
    List<ScrapWord> findAll(UserId userId, int page);
    List<PopularWord> findPopularNames();
}
