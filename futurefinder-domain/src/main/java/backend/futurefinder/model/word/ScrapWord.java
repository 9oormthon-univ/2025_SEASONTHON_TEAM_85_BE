package backend.futurefinder.model.word;

import backend.futurefinder.model.auth.JwtToken;
import backend.futurefinder.model.user.UserId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScrapWord {

    private final WordId wordId;
    private final String wordName;
    private final String meaning;


    public static ScrapWord of(WordId wordId, String wordName, String meaning) {
        return new ScrapWord(wordId, wordName, meaning);
    }



}
