package backend.futurefinder.repository.auth;

import backend.futurefinder.model.token.RefreshToken;
import backend.futurefinder.model.user.UserId;

import java.util.Optional;

public interface LoggedInRepository {
    void save(RefreshToken refreshToken, UserId userId);
    void delete(String refreshToken);
    void update(RefreshToken refreshToken, RefreshToken preRefreshToken);
    Optional<RefreshToken> find(String refreshToken, UserId userId);
}