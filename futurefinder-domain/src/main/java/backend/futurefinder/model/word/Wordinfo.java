package backend.futurefinder.model.word;


import backend.futurefinder.model.token.RefreshToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class Wordinfo {

    private final String word;
    private final String meaning;

    public static Wordinfo of(String word, String meaning) {
        return new Wordinfo(word, meaning);
    }


}
