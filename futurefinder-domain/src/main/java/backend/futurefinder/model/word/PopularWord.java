package backend.futurefinder.model.word;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PopularWord {

    private final String wordName;


    public static PopularWord of(String wordName) {
        return new PopularWord(wordName);
    }



}
