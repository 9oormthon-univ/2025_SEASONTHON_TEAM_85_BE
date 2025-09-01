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



}
