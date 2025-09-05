package backend.futurefinder.dto.response.word;

import backend.futurefinder.dto.response.asset.AssetResponse;
import backend.futurefinder.model.asset.Asset;
import backend.futurefinder.model.word.ScrapWord;

public record ScrapWordResponse (
        String wordId,
        String wordName,
        String meaning
){

    public static ScrapWordResponse of(ScrapWord scrapWord){
        return new ScrapWordResponse(
                scrapWord.getWordId().getId(),
                scrapWord.getWordName(),
                scrapWord.getMeaning()
        );
    }




}
