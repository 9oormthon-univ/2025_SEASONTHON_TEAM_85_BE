package backend.futurefinder.service.word;


import backend.futurefinder.implementation.word.WordAppender;
import backend.futurefinder.implementation.word.WordReader;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.word.PopularWord;
import backend.futurefinder.model.word.ScrapWord;
import backend.futurefinder.model.word.Wordinfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WordService {


    private final WordReader wordReader;
    private final WordAppender wordAppender;

    public Wordinfo search(String rawWord) {
        String word = normalize(rawWord);
        String meaning = wordReader.findMeaning(word); // 못 찾으면 내부에서 예외 또는 기본 메시지 처리
        return Wordinfo.of(word, meaning);
    }


    public void scrap(UserId userId, String word, String meaning){
        wordAppender.append(userId, word, meaning);
    }


    public List<ScrapWord> getWordList(UserId userId, int page){
        return wordReader.reads(userId, page);
    }

    public List<PopularWord> getPopularWordList(){
        return wordReader.reads();
    }


    private String normalize(String s) {
        if (s == null) return "";
        String t = s.strip();
        return t;
        // 필요시 유니코드 정규화:
        // return java.text.Normalizer.normalize(t, java.text.Normalizer.Form.NFC);
    }

}
