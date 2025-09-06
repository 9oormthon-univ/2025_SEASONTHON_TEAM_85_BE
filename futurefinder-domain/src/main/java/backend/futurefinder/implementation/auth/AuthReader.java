package backend.futurefinder.implementation.auth;

import backend.futurefinder.error.AuthorizationException;
import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.model.token.RefreshToken;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.repository.auth.LoggedInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthReader {

    private final LoggedInRepository loggedInRepository;



    public RefreshToken readLoginInfo(String refreshToken, UserId userId) {
        return loggedInRepository.read(refreshToken, userId)
                .orElseThrow(() -> new AuthorizationException(ErrorCode.INVALID_TOKEN));
    }
}
