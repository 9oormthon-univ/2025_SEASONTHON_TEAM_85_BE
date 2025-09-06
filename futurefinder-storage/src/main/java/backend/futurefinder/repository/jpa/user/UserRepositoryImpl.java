package backend.futurefinder.repository.jpa.user;


import backend.futurefinder.jpaentity.user.UserJpaEntity;
import backend.futurefinder.model.media.Media;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;
import backend.futurefinder.repository.user.UserRepository;

import backend.futurefinder.jparepository.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;


    @Override
    public UserInfo read(UserId userId) {
        return userJpaRepository.findById(
                                userId.getId()
                            ).map(UserJpaEntity::toUser).orElse(null);


    }



    @Override
    public UserInfo readByAccountId(String accountId, AccessStatus status) {
        return userJpaRepository.findByAccountIdAndStatus(
                        accountId, status
                ).map(UserJpaEntity::toUser) // ✅ 인스턴스 기준 메서드 참조
                .orElse(null);
    }

    @Override
    public Optional<UserId> searchUser(String nickName) {
        return userJpaRepository.findByNickNameAndStatus(nickName, AccessStatus.ACCESS).map(e -> UserId.of(e.getUserId()));
    }

    @Override
    public UserInfo append(String accountId, String userName, String nickName){
        return userJpaRepository
                .findByAccountIdAndStatus(accountId, AccessStatus.NEED_CREATE_PASSWORD)
                .map(entity -> {
                    userJpaRepository.save(entity);
                    return entity.toUser();
                })
                .orElseGet(() -> {
                    // UserJpaEntity.generate(...)가 PhoneNumber 기반이라면,
                    // accountId 기반의 팩토리 메서드를 새로 만들어줘야 함
                    UserJpaEntity userEntity = UserJpaEntity.generate(
                            userName,
                            nickName,
                            accountId,
                            AccessStatus.NEED_CREATE_PASSWORD
                    );
                    return userJpaRepository.save(userEntity).toUser();
                });
    }


    @Override
    public void appendPassword(UserId userId, String password) {
        Optional<UserJpaEntity> optional = userJpaRepository.findById(userId.getId());

        optional.ifPresent(entity -> {
            entity.updateAccessStatus(AccessStatus.ACCESS);
            entity.updatePassword(password);
            userJpaRepository.save(entity);
        });
    }

    @Override
    public Optional<Media> updateMedia(UserId userId, Media media) {
        return userJpaRepository.findById(userId.getId())
                .map(user -> {
                    // 수정 전 기존 미디어 정보 반환
                    Media previousMedia = user.toUser().getImage();

                    // 새로운 미디어 정보 업데이트
                    user.updateUserPictureUrl(media);

                    // 사용자 정보 저장
                    userJpaRepository.save(user);

                    return previousMedia;
                });
    }

    @Override
    public void UpdateProfile(UserId userId, String userName, String email, String phoneNumber, String birth) {
         userJpaRepository.findById(userId.getId())
                .map(user -> {

                    user.updateUserProfile(userName, email, phoneNumber, birth);

                    userJpaRepository.save(user);

                    return null;
                });
    }


    @Override
    public Optional<UserInfo> remove(UserId userId) {
        return userJpaRepository.findById(userId.getId())
                .map(entity -> {
                    entity.updateAccessStatus(AccessStatus.DELETE);
                    userJpaRepository.save(entity);
                    return entity.toUser();
                });
    }

    @Override
    public Optional<UserId> updateNickName(UserId userId, String nickName) {
        return userJpaRepository.findById(userId.getId())
                .map(entity -> {
                    entity.updateNickName(nickName);
                    userJpaRepository.save(entity);
                    return entity.toUserId();
                });
    }

    @Override
    public Optional<UserId> updatePassword(UserId userId, String password) {
        return userJpaRepository.findById(userId.getId())
                .map(entity -> {
                    entity.updatePassword(password);
                    userJpaRepository.save(entity);
                    return entity.toUserId();
                });
    }

    @Override
    public UserInfo readByNickName(String nickName, AccessStatus status) {
        return userJpaRepository.findByNickNameAndStatus(
                        nickName, status
                ).map(UserJpaEntity::toUser) // ✅ 인스턴스 기준 메서드 참조
                .orElse(null);
    }

    @Override
    public boolean existsByNickName(String nickName) {
        return userJpaRepository.existsByNickName(nickName);
    }


    @Override
    public UserInfo appendKakao(String accountId, String userName, String nickName){
        return userJpaRepository
                .findByAccountIdAndStatus(accountId, AccessStatus.ACCESS)
                .map(entity -> {
                    userJpaRepository.save(entity);
                    return entity.toUser();
                })
                .orElseGet(() -> {
                    // UserJpaEntity.generate(...)가 Id가 기반이라면,
                    // accountId 기반의 팩토리 메서드를 새로 만들어줘야 함
                    UserJpaEntity userEntity = UserJpaEntity.generate(
                            userName,
                            nickName,
                            accountId,
                            AccessStatus.ACCESS
                    );
                    return userJpaRepository.save(userEntity).toUser();
                });
    }


}