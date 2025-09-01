package backend.futurefinder.facade.auth;


import backend.futurefinder.model.auth.OAuthProvider;
import backend.futurefinder.model.notification.PushInfo;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;
import backend.futurefinder.service.auth.AuthService;
import backend.futurefinder.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountFacade {

    private final AuthService authService;
    private final UserService userService;

    public UserId createUser(
            String accountId,
            String userName,
            String nickName,
            String appToken,
            PushInfo.Device device
    ){
        UserInfo user = userService.createUser(accountId, userName, nickName, appToken, device);
        return user.getUserId();
    }

    public void createPassword(UserId userId, String rawPassword) {
        String hashedPassword = authService.encryptPassword(rawPassword);
        userService.createPassword(userId, hashedPassword);
    }

    public UserId login(
            String accountId,
            String password,
            PushInfo.Device device,
            String appToken
    ) {
        UserInfo user = userService.getUserByAccountId(accountId, AccessStatus.ACCESS);
        authService.validatePassword(user, password);
        userService.createDeviceInfo(user, device, appToken);
        return user.getUserId();
    }


    public void logout(PushInfo.Device device, String token) {
        authService.logout(token);
        userService.removePushInfo(device);
    }

    public void changePassword(UserId userId, String password) {
        String encrypted = authService.encryptPassword(password);
        userService.updatePassword(userId, encrypted);
    }



}
