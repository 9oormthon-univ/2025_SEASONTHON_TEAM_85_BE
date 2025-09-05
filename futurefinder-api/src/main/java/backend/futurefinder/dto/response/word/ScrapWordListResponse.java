package backend.futurefinder.dto.response.word;

import backend.futurefinder.dto.response.asset.AssetListResponse;
import backend.futurefinder.dto.response.asset.AssetResponse;
import backend.futurefinder.model.asset.Asset;
import backend.futurefinder.model.word.ScrapWord;

import java.util.List;

import static java.util.stream.Collectors.toList;

public record ScrapWordListResponse (
        List<ScrapWordResponse> ScrapWords
){


    public static ScrapWordListResponse of(List<ScrapWord> scrapWords) {
        List<ScrapWordResponse> mapped = scrapWords.stream()
                .map(ScrapWordResponse::of)
                .toList();
        return new ScrapWordListResponse(mapped);

    }





}
