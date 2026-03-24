package backend.futurefinder.implementation.user;


import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.error.NotFoundException;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;
import backend.futurefinder.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReader {

    private final UserRepository userRepository;


    public UserInfo find(UserId userId){
        return userRepository.find(userId);
    }


    public UserInfo findByAccountId(String accountId, AccessStatus accessStatus){
        UserInfo userInfo = userRepository.findByAccountId(accountId, accessStatus);
        if (userInfo == null) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        return userInfo;
    }

    public UserInfo findByNickName(String nickName, AccessStatus accessStatus) {
        UserInfo userInfo = userRepository.findByNickName(nickName, accessStatus);
        if (userInfo == null) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        return userInfo;
    }


    public UserInfo findByKakaoId(String accountId, AccessStatus accessStatus){
        return userRepository.findByAccountId(accountId, accessStatus);
    }

}
