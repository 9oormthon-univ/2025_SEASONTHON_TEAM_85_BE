package backend.futurefinder.model.auth;

import backend.futurefinder.model.token.RefreshToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtToken {

    private final String accessToken;
    private final RefreshToken refreshToken;


    public static JwtToken of(String accessToken, RefreshToken refreshToken) {
        return new JwtToken(accessToken, refreshToken);
    }


}

