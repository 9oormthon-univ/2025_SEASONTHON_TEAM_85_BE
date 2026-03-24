package backend.futurefinder.repository.jpa.push;


import backend.futurefinder.jpaentity.push.PushNotificationJpaEntity;
import backend.futurefinder.model.notification.PushInfo;
import backend.futurefinder.model.user.UserInfo;
import backend.futurefinder.repository.push.PushNotificationRepository;
import backend.futurefinder.jparepository.push.PushNotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class PushNotificationRepositoryImpl implements PushNotificationRepository {

    private final PushNotificationJpaRepository pushNotificationJpaRepository;

    @Override
    public void delete(PushInfo.Device device) {
        pushNotificationJpaRepository.deleteAllByDeviceIdAndProvider(
                device.getDeviceId(),
                device.getProvider()
        );
    }

    @Override
    public void save(PushInfo.Device device, String appToken, UserInfo userInfo) {
        PushNotificationJpaEntity entity = pushNotificationJpaRepository.findByDeviceIdAndUserId(
                device.getDeviceId(), userInfo.getUserId().getId()
        );

        if (entity == null) {
            PushNotificationJpaEntity newEntity = PushNotificationJpaEntity.generate(appToken, device, userInfo);
            pushNotificationJpaRepository.save(newEntity);
        } else {
            entity.updateAppToken(appToken);
            pushNotificationJpaRepository.save(entity);
        }
    }

}
