package backend.futurefinder.implementation.word;


import backend.futurefinder.error.ConflictException;
import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.repository.word.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WordAppender {

    private final WordRepository wordRepository;


    public void append(UserId userId, String word, String meaning) {
        boolean isExist = wordRepository.append(userId, word, meaning);
        if(!isExist){
            throw new ConflictException(ErrorCode.WORD_ALREADY_EXISTS);
        }
    }



}
