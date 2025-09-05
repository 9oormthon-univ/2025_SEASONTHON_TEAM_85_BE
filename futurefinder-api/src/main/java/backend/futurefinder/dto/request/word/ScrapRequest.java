package backend.futurefinder.dto.request.word;

public record ScrapRequest (
        String word,
        String meaning
){
    public static record getWord(String word, String meaning) {
        public String toWord() { return word; }
        public String toMeaning() { return meaning; }
    }



}
