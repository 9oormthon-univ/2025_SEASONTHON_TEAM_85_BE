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

    public UserInfo append(String accountId, String userName, String nickName) {
        return userRepository.append(accountId, userName, nickName);
    }

    public void appendUserPushToken(UserInfo userInfo, String appToken, PushInfo.Device device) {
        pushNotificationRepository.append(device, appToken, userInfo);
    }


    public void appendPassword(UserId userId, String password) {
        System.out.println("userId = " + userId);                // 로그1
        System.out.println("userId.getId() = " + userId.getId()); // 로그2
        userRepository.appendPassword(userId, password);

    }

    public UserInfo appendKakao(String accountId, String userName, String nickName) {
        return userRepository.appendKakao(accountId, userName, nickName);
    }




}
