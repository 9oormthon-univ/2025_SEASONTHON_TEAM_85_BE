package backend.futurefinder.jparepository.push;

import backend.futurefinder.jpaentity.push.PushNotificationJpaEntity;
import backend.futurefinder.model.notification.PushInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PushNotificationJpaRepository  extends JpaRepository<PushNotificationJpaEntity, String> {

    void deleteAllByDeviceIdAndProvider(String deviceId, PushInfo.Provider deviceProvider);

    PushNotificationJpaEntity findByDeviceIdAndUserId(String deviceId, String userId);

}
