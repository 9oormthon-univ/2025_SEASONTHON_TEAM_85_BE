package backend.futurefinder.external;

import backend.futurefinder.model.auth.OAuthProfile;

public interface ExternalOAuthClient {
    OAuthProfile verifyKakao(String accessToken);  // 토큰 검증 + 프로필 조회
}