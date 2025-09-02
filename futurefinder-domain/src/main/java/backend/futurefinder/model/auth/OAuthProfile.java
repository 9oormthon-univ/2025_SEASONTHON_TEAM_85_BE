package backend.futurefinder.model.auth;

public record OAuthProfile(
        String provider,  // "KAKAO"
        String oauthId,   // 카카오 user id (문자열 화)
        String email,     // 동의 없으면 null
        String nickname   // 프로필 닉네임 (없을 수 있음)
) {}
