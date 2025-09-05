package backend.futurefinder.implementation.word;


import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.error.NotFoundException;
import backend.futurefinder.external.ExternalSearchClient;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.word.PopularWord;
import backend.futurefinder.model.word.ScrapWord;
import backend.futurefinder.repository.word.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static backend.futurefinder.error.ErrorCode.WORD_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class WordReader {

    private final ExternalSearchClient externalSearchClient;
    private final WordRepository wordRepository;


    public String findMeaning(String term) {
        return externalSearchClient.lookupMeaning(term)
                .orElseThrow(() -> new NotFoundException(ErrorCode.WORD_NOT_FOUND));
    }

    public List<ScrapWord> reads(UserId userId, int page){
        return wordRepository.reads(userId, page);
    }
    public List<PopularWord> reads(){
        return wordRepository.readPopularNames();
    }


}
