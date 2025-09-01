package backend.futurefinder.repository.push;

import backend.futurefinder.model.notification.PushInfo;
import backend.futurefinder.model.user.UserInfo;

public interface PushNotificationRepository {
    void remove(PushInfo.Device device);
    void append(PushInfo.Device device, String appToken, UserInfo userInfo);
}