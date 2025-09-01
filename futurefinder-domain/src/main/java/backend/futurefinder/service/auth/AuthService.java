package backend.futurefinder.service.auth;

import backend.futurefinder.implementation.auth.AuthAppender;
import backend.futurefinder.implementation.auth.AuthGenerator;
import backend.futurefinder.implementation.auth.AuthRemover;
import backend.futurefinder.implementation.auth.AuthValidator;
import backend.futurefinder.model.token.RefreshToken;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthAppender authAppender;
    private final AuthGenerator authGenerator;
    private final AuthValidator authValidator;
    private final AuthRemover authRemover;

    public void createLoginInfo(UserId userId, RefreshToken refreshToken) {
        authAppender.appendLoggedIn(refreshToken, userId);
    }

    public String encryptPassword(String password) {
        return authGenerator.hashPassword(password);
    }

    public void validatePassword(UserInfo userInfo, String password) {
        authValidator.validatePassword(
                password, // sourcePassword
                userInfo.getPassword() // targetPassword
        );
    }

    public void logout(String refreshToken) {
        authRemover.removeLoginInfo(refreshToken);
    }


}
