package backend.futurefinder.repository.user;

import backend.futurefinder.model.media.Media;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;

import java.util.Optional;

public interface UserRepository {

    UserInfo read(UserId userId);
    UserInfo readByAccountId(String accountId, AccessStatus status);
    Optional<UserId> searchUser(String nickName);
    UserInfo append(String accountId, String userName, String nickName);
    void appendPassword(UserId userId, String password);
    Optional<Media> updateMedia(UserId userId, Media media);
    void UpdateProfile(UserId userId, String userName, String email, String phoneNumber, String birth);
    Optional<UserInfo> remove(UserId userId);
    boolean existsByNickName(String nickName);
    Optional<UserId> updateNickName(UserId userId, String nickName);
    Optional<UserId> updatePassword(UserId userId, String password);
    UserInfo readByNickName(String nickName, AccessStatus status);
    UserInfo appendKakao(String accountId, String userName, String nickName);
}
