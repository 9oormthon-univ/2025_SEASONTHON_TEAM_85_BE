package backend.futurefinder.kakao;

public record KakaoTokenInfoResponse(
        Long id,           // 카카오 내부 사용자 ID
        Integer expires_in // 남은 만료초
) {}