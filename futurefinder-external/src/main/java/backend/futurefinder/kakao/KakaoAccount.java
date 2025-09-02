package backend.futurefinder.kakao;

public record KakaoAccount(
        Boolean has_email,
        Boolean email_needs_agreement,
        String email,
        KakaoProfile profile
) {}