package backend.futurefinder.service.user;


import backend.futurefinder.implementation.media.FileHandler;
import backend.futurefinder.implementation.user.*;
import backend.futurefinder.model.media.FileCategory;
import backend.futurefinder.model.media.FileData;
import backend.futurefinder.model.media.Media;
import backend.futurefinder.model.notification.PushInfo;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;
import backend.futurefinder.repository.push.PushNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {


    private final FileHandler fileHandler;
    private final UserAppender userAppender;
    private final UserRemover userRemover;
    private final UserReader userReader;
    private final UserUpdater userUpdater;
    private final UserValidator userValidator;
    private final PushNotificationRepository pushNotificationRepository;

    public UserInfo createUser(
            String accountId,
            String userName,
            String nickName,
            String appToken,
            PushInfo.Device device
    ){
        userValidator.isNotAlreadyCreated(accountId);
        userValidator.nickNameExists(nickName);
        UserInfo user = userAppender.append(accountId, userName, nickName);
        userRemover.removePushToken(device);
        userAppender.appendUserPushToken(user, appToken, device);
        return user;
    }

    public void createPassword(UserId userId, String password) {
        userAppender.appendPassword(userId, password);
    }


    public UserInfo getUserByAccountId(String accountId, AccessStatus accessStatus) {
        UserInfo userInfo = userReader.readByAccountId(accountId, accessStatus);
        return userInfo;
    }

    public UserInfo getUserProfile(UserId userId){
        return userReader.read(userId);
    }

    public void changeUserProfile(UserId userId, String userName, String email, String phoneNumber, String birth){
        userUpdater.UpdateProfile(userId, userName, email, phoneNumber, birth);
    }


    public void createDeviceInfo(UserInfo userInfo, PushInfo.Device device, String appToken) {
        userAppender.appendUserPushToken(userInfo, appToken, device);
    }

    public void removePushInfo(PushInfo.Device device) {
        userRemover.removePushToken(device);
    }



    public void updateFile(FileData file, UserId userId, FileCategory category) {
        Media newMedia = fileHandler.handleNewFile(userId, file, category);
        Media oldMedia = userUpdater.updateFileUrl(userId, newMedia);
        fileHandler.handleOldFile(oldMedia);
    }


    public void deleteUser(UserId userId) {
        UserInfo removedUser = userRemover.remove(userId);
        fileHandler.handleOldFile(removedUser.getImage());
    }


    public void updateNickName(UserId userId, String nickName) {
        userUpdater.updateNickName(userId, nickName);
    }


    public void updatePassword(UserId userId, String password) {
        userUpdater.updatePassword(userId, password);
    }

    // 단순히 아이디 찾기용
    public String findAccountIdByNickName(String nickName, AccessStatus accessStatus) {
        UserInfo userInfo = userReader.readByNickName(nickName, accessStatus);
        return userInfo.getAccountId();
    }

    // 소셜(OAuth) 공통 로그인/가입 로직
    public UserId loginWithOAuthAccount(String accountKey, String displayName, String appToken, PushInfo.Device device) {
        final String key  = accountKey == null ? "" : accountKey.trim();
        final String name = displayName == null ? "" : displayName.trim();

        // 1) 기존 가입자 조회 (ACCESS 우선 → 없으면 NEED_CREATE_PASSWORD)
        UserInfo user = userReader.readByKakaoId(key, AccessStatus.ACCESS);

        // 2) 신규 가입
        if (user == null) {
            // 기존 정책 유지: nickName = accountKey (유니크 보장)
            userValidator.nickNameExists(key);
            user = userAppender.appendKakao(key, name /* userName */, key /* nickName */);
        }

        // 3) 푸시 토큰 갱신
        userRemover.removePushToken(device);
        userAppender.appendUserPushToken(user, appToken, device);

        return user.getUserId();
    }
}
