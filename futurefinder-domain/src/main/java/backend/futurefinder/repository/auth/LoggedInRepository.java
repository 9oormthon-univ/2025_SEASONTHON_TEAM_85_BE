package backend.futurefinder.repository.auth;

import backend.futurefinder.model.token.RefreshToken;
import backend.futurefinder.model.user.UserId;

import java.util.Optional;

public interface LoggedInRepository {
    void append(RefreshToken refreshToken, UserId userId);
    void remove(String refreshToken);
    void update(RefreshToken refreshToken, RefreshToken preRefreshToken);
    Optional<RefreshToken> read(String refreshToken, UserId userId);
}