package backend.futurefinder.implementation.user;

import backend.futurefinder.model.notification.PushInfo;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;
import backend.futurefinder.repository.push.PushNotificationRepository;
import backend.futurefinder.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAppender {

    private final UserRepository userRepository;
    private final PushNotificationRepository pushNotificationRepository;

    public UserInfo save(String accountId, String userName, String nickName) {
        return userRepository.save(accountId, userName, nickName);
    }

    public void appendUserPushToken(UserInfo userInfo, String appToken, PushInfo.Device device) {
        pushNotificationRepository.save(device, appToken, userInfo);
    }


    public void savePassword(UserId userId, String password) {
        userRepository.savePassword(userId, password);
    }

    public UserInfo saveKakao(String accountId, String userName, String nickName) {
        return userRepository.saveKakao(accountId, userName, nickName);
    }




}
