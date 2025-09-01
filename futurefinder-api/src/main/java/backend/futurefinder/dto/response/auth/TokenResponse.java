package backend.futurefinder.dto.response.auth;

import backend.futurefinder.model.auth.JwtToken;

public record TokenResponse(String accessToken, String refreshToken) {

    public static TokenResponse of(JwtToken token) {
        return new TokenResponse(token.getAccessToken(), token.getRefreshToken().getToken());
    }
}
