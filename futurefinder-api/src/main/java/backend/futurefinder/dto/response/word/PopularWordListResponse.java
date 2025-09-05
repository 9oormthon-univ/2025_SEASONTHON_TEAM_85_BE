package backend.futurefinder.dto.response.word;

import backend.futurefinder.model.word.PopularWord;
import backend.futurefinder.model.word.ScrapWord;

import java.util.List;

public record PopularWordListResponse (
        List<PopularWordResponse> PopularWords
){


    public static PopularWordListResponse of(List<PopularWord> popularWords) {
        List<PopularWordResponse> mapped = popularWords.stream()
                .map(PopularWordResponse::of)
                .toList();
        return new PopularWordListResponse(mapped);

    }


}
