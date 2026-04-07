package backend.futurefinder.repository.jpa.user;


import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.error.NotFoundException;
import backend.futurefinder.jpaentity.user.UserJpaEntity;
import backend.futurefinder.model.media.Media;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;
import backend.futurefinder.repository.user.UserRepository;

import backend.futurefinder.jparepository.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;


    @Override
    public UserInfo find(UserId userId) {
        return userJpaRepository.findById(
                                userId.getId()
                            ).map(UserJpaEntity::toUser).orElse(null);


    }



    @Override
    public UserInfo findByAccountId(String accountId, AccessStatus status) {
        return userJpaRepository.findByAccountIdAndStatus(
                        accountId, status
                ).map(UserJpaEntity::toUser) // ✅ 인스턴스 기준 메서드 참조
                .orElse(null);
    }

    @Override
    public Optional<UserId> findUserIdByNickName(String nickName) {
        return userJpaRepository.findByNickNameAndStatus(nickName, AccessStatus.ACCESS).map(e -> UserId.of(e.getUserId()));
    }

    @Override
    public UserInfo save(String accountId, String userName, String nickName){
        return userJpaRepository
                .findByAccountIdAndStatus(accountId, AccessStatus.NEED_CREATE_PASSWORD)
                .map(UserJpaEntity::toUser)
                .orElseGet(() -> {
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
    public void savePassword(UserId userId, String password) {
        UserJpaEntity entity = userJpaRepository.findById(userId.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        entity.updateAccessStatus(AccessStatus.ACCESS);
        entity.updatePassword(password);
        userJpaRepository.save(entity);
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
    public void updateProfile(UserId userId, String userName, String email, String phoneNumber, String birth) {
        UserJpaEntity entity = userJpaRepository.findById(userId.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        entity.updateUserProfile(userName, email, phoneNumber, birth);
        userJpaRepository.save(entity);
    }


    @Override
    public Optional<UserInfo> delete(UserId userId) {
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
    public UserInfo findByNickName(String nickName, AccessStatus status) {
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
    public UserInfo saveKakao(String accountId, String userName, String nickName){
        return userJpaRepository
                .findByAccountIdAndStatus(accountId, AccessStatus.ACCESS)
                .map(UserJpaEntity::toUser)
                .orElseGet(() -> {
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