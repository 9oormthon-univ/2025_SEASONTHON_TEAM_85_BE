package backend.futurefinder.facade.auth;


import backend.futurefinder.external.ExternalFileClient;
import backend.futurefinder.external.ExternalOAuthClient;
import backend.futurefinder.model.auth.OAuthProfile;
import backend.futurefinder.model.notification.PushInfo;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;
import backend.futurefinder.service.auth.AuthService;
import backend.futurefinder.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountFacade {

    private final AuthService authService;
    private final UserService userService;
    private final ExternalOAuthClient externalOAuthClient;

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

    public UserInfo getUserByAccountId(String accountId, AccessStatus accessStatus) {
        return userService.getUserByAccountId(accountId, accessStatus);
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


    // 외부 통신으로 프로필 획득 후 accountKey/nickname 구성
    public UserId loginWithKakao(String kakaoAccessToken, String appToken, PushInfo.Device device) {
        OAuthProfile profile = externalOAuthClient.verifyKakao(kakaoAccessToken);

        // 이메일 동의가 없으면 oauthId로 대체 키 생성
        String accountKey = (profile.email() != null && !profile.email().isBlank())
                ? profile.email().trim()
                : "kakao:" + profile.oauthId();

        String displayName = (profile.nickname() != null && !profile.nickname().isBlank())
                ? profile.nickname().trim()
                : "kakao_" + profile.oauthId();

        return userService.loginWithOAuthAccount(accountKey, displayName, appToken, device);
    }

}
