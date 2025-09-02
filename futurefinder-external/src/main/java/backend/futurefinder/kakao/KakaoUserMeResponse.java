package backend.futurefinder.kakao;

public record KakaoUserMeResponse(
        Long id,
        KakaoAccount kakao_account
) {}