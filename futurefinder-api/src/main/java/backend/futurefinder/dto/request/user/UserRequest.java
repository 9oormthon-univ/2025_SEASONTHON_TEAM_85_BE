package backend.futurefinder.dto.request.user;


import backend.futurefinder.model.notification.NotificationStatus;

public final class UserRequest {

    public static record UpdateNickName(String nickName) {
        public String toNickName() { return nickName; }
    }

    public static record UpdateNotification(boolean status, String deviceId) {
        public NotificationStatus toNotification() {
            return status ? NotificationStatus.ALLOWED : NotificationStatus.NOT_ALLOWED;
        }
        public String toDeviceId() { return deviceId; }
    }

    public static record getNickName(String nickName) {
        public String toNickName() { return nickName; }
    }

    public static record getAccountId(String accountId) {
        public String toAccountId() { return accountId; }
    }


}