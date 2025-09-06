package backend.futurefinder.implementation.user;

import backend.futurefinder.error.ConflictException;
import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.error.NotFoundException;
import backend.futurefinder.model.media.Media;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.repository.push.PushNotificationRepository;
import backend.futurefinder.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUpdater {

    private final UserRepository userRepository;
    private final PushNotificationRepository pushNotificationRepository;

    /**
     * 주어진 사용자 정보를 업데이트합니다.
     */
    public Media updateFileUrl(UserId userId, Media media) {
        return userRepository.updateMedia(userId, media)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }


    public void updateNickName(UserId userId, String nickName) {
        // 닉네임 중복 체크
        if (userRepository.existsByNickName(nickName)) {
            throw new ConflictException(ErrorCode.USER_NICKNAME_EXISTS);
        }

        // 닉네임 변경 시도
        userRepository.updateNickName(userId, nickName)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }


    public void updatePassword(UserId userId, String password) {
        userRepository.updatePassword(userId, password)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    public void UpdateProfile(UserId userId, String userName, String email, String phoneNumber, String birth){
        userRepository.UpdateProfile(userId, userName, email, phoneNumber, birth);
    }

}