package backend.futurefinder.model.notification;

import backend.futurefinder.model.user.UserId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public final class PushInfo {

    private final String pushId;
    private final String pushToken;
    private final Device device;
    private final UserId userId;
    private final NotificationStatusInfo statusInfo;

    private PushInfo(
            String pushId,
            String pushToken,
            Device device,
            UserId userId,
            NotificationStatusInfo statusInfo
    ) {
        this.pushId = pushId;
        this.pushToken = pushToken;
        this.device = device;
        this.userId = userId;
        this.statusInfo = statusInfo;
    }

    public static PushInfo of(
            String pushTokenId,
            String fcmToken,
            String deviceId,
            Provider provider,
            UserId userId,
            NotificationStatus passwordStatus
    ) {
        return new PushInfo(
                pushTokenId,
                fcmToken,
                Device.of(deviceId, provider),
                userId,
                new NotificationStatusInfo(passwordStatus)
        );
    }

    // ✅ 내부 정적 클래스: Device
    @Getter
    @RequiredArgsConstructor
    public static class Device {
        private final String deviceId;
        private final Provider provider;

        public static Device of(String deviceId, Provider provider) {
            return new Device(deviceId, provider);
        }
    }

    // ✅ 내부 정적 클래스: NotificationStatusInfo
    @Getter
    @RequiredArgsConstructor
    public static class NotificationStatusInfo {
        private final NotificationStatus passwordStatus;
    }

    // ✅ Enum: Provider
    public enum Provider {
        ANDROID,
        IOS
    }

//
//    public enum PushTarget {
//        CHAT,
//        SCHEDULE
//    }


}