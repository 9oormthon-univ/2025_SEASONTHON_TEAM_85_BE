package backend.futurefinder.dto.response.word;


public record WordMeaningResponse(String term, String meaning) {
    public static WordMeaningResponse of(String term, String meaning) {
        return new WordMeaningResponse(term, meaning);
    }
}