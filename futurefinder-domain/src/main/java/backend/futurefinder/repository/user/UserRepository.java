package backend.futurefinder.repository.user;

import backend.futurefinder.model.media.Media;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;

import java.util.Optional;

public interface UserRepository {

    UserInfo find(UserId userId);
    UserInfo findByAccountId(String accountId, AccessStatus status);
    Optional<UserId> findUserIdByNickName(String nickName);
    UserInfo save(String accountId, String userName, String nickName);
    void savePassword(UserId userId, String password);
    Optional<Media> updateMedia(UserId userId, Media media);
    void updateProfile(UserId userId, String userName, String email, String phoneNumber, String birth);
    Optional<UserInfo> delete(UserId userId);
    boolean existsByNickName(String nickName);
    Optional<UserId> updateNickName(UserId userId, String nickName);
    Optional<UserId> updatePassword(UserId userId, String password);
    UserInfo findByNickName(String nickName, AccessStatus status);
    UserInfo saveKakao(String accountId, String userName, String nickName);
}
