package backend.futurefinder.implementation.user;


import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.error.NotFoundException;
import backend.futurefinder.model.notification.PushInfo;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;
import backend.futurefinder.repository.push.PushNotificationRepository;
import backend.futurefinder.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRemover {

    private final UserRepository userRepository;
    private final PushNotificationRepository pushNotificationRepository;



    public void removePushToken(PushInfo.Device device) {
        pushNotificationRepository.remove(device);
    }

    public UserInfo remove(UserId userId) {
        return userRepository.remove(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

}
