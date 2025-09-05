package backend.futurefinder.dto.response.word;

import backend.futurefinder.model.word.PopularWord;
import backend.futurefinder.model.word.ScrapWord;

public record PopularWordResponse (
        String wordName
){

    public static PopularWordResponse of(PopularWord popularWord){
        return new PopularWordResponse(
                popularWord.getWordName()
        );
    }




}
