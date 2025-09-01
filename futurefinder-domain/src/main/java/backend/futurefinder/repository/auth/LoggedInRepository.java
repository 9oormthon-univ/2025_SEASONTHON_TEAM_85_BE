package backend.futurefinder.repository.auth;

import backend.futurefinder.model.token.RefreshToken;
import backend.futurefinder.model.user.UserId;

public interface LoggedInRepository {
    void append(RefreshToken refreshToken, UserId userId);
    void remove(String refreshToken);
}