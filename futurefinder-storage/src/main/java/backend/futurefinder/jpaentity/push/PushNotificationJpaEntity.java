package backend.futurefinder.jpaentity.push;


import backend.futurefinder.model.notification.NotificationStatus;
import backend.futurefinder.model.notification.PushInfo;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "push_notification",
        schema = "futurefinder",
        indexes = {
                @Index(name = "push_notification_idx_device_provider", columnList = "deviceId, provider"),
                @Index(name = "push_notification_idx_user_id", columnList = "userId"),
                @Index(name = "push_notification_idx_app_token_user_id", columnList = "appToken, userId")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushNotificationJpaEntity {

    @Id
    @Column(name = "push_notification_id")
    private String pushId = UUID.randomUUID().toString();

    private String appToken;

    private String deviceId;

    @Enumerated(EnumType.STRING)
    private PushInfo.Provider provider;

    @Enumerated(EnumType.STRING)
    private NotificationStatus passwordStatus = NotificationStatus.ALLOWED;

    private String userId;

    @Builder
    public PushNotificationJpaEntity(
            String appToken,
            String deviceId,
            PushInfo.Provider provider,
            NotificationStatus passwordStatus,
            String userId
    ) {
        this.pushId = UUID.randomUUID().toString();
        this.appToken = appToken;
        this.deviceId = deviceId;
        this.provider = provider;
        this.passwordStatus = passwordStatus != null ? passwordStatus : NotificationStatus.ALLOWED;
        this.userId = userId;
    }

    public static PushNotificationJpaEntity generate(String appToken, PushInfo.Device device, UserInfo userInfo) {
        return PushNotificationJpaEntity.builder()
                .appToken(appToken)
                .deviceId(device.getDeviceId())
                .provider(device.getProvider())
                .userId(userInfo.getUserId().getId())
                .build();
    }

    public PushInfo toPushToken() {
        return PushInfo.of(
                this.pushId,
                this.appToken,
                this.deviceId,
                this.provider,
                UserId.of(this.userId),
                this.passwordStatus
        );
    }

    public void updatePasswordStatus(NotificationStatus status) {
        this.passwordStatus = status;
    }


    public void updateAppToken(String appToken) {
        this.appToken = appToken;
    }
}