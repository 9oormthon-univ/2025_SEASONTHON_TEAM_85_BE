package backend.futurefinder.implementation.user;

import backend.futurefinder.error.ConflictException;
import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void isNotAlreadyCreated(String accountId) {
        if (userRepository.findByAccountId(accountId, AccessStatus.ACCESS) != null) {
            throw new ConflictException(ErrorCode.USER_ALREADY_CREATED);
        }
    }


    public void nickNameExists(String nickName) {
        if (userRepository.findUserIdByNickName(nickName).isPresent()) {
            throw new ConflictException(ErrorCode.USER_NICKNAME_EXISTS);
        }
    }


}
