package backend.futurefinder.implementation.auth;

import backend.futurefinder.model.token.RefreshToken;
import backend.futurefinder.repository.auth.LoggedInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUpdater {

    private final LoggedInRepository loggedInRepository;

    public void updateLoginInfo(RefreshToken refreshToken, RefreshToken preRefreshToken) {
        loggedInRepository.update(refreshToken, preRefreshToken);
    }
}