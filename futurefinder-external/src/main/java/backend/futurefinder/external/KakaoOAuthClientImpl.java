package backend.futurefinder.external;

import backend.futurefinder.kakao.KakaoAccount;
import backend.futurefinder.kakao.KakaoProfile;
import backend.futurefinder.kakao.KakaoTokenInfoResponse;
import backend.futurefinder.kakao.KakaoUserMeResponse;
import backend.futurefinder.model.auth.OAuthProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KakaoOAuthClientImpl implements ExternalOAuthClient {

    private final WebClient kakaoWebClient; // @Qualifier("kakaoWebClient") 가 필요하면 추가

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    @Override
    public OAuthProfile verifyKakao(String accessToken) {
        // 1) 토큰 유효성 검증
        KakaoTokenInfoResponse info = kakaoWebClient.get()
                .uri("/v1/user/access_token_info")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new IllegalArgumentException("Kakao token invalid: " + body))
                        ))
                .bodyToMono(KakaoTokenInfoResponse.class)
                .block(TIMEOUT);

        if (info == null || info.id() == null) {
            throw new IllegalArgumentException("Kakao token invalid: empty body");
        }

        // 2) 프로필 조회
        KakaoUserMeResponse me = kakaoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/user/me")
                        // 필요한 필드만 요청(옵션)
                        .queryParam("property_keys", "[\"kakao_account.email\",\"kakao_account.profile.nickname\"]")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new IllegalStateException("Kakao /v2/user/me error: " + body))
                        ))
                .bodyToMono(KakaoUserMeResponse.class)
                .block(TIMEOUT);

        String email = Optional.ofNullable(me)
                .map(KakaoUserMeResponse::kakao_account)
                .map(KakaoAccount::email)
                .orElse(null);

        String nickname = Optional.ofNullable(me)
                .map(KakaoUserMeResponse::kakao_account)
                .map(KakaoAccount::profile)
                .map(KakaoProfile::nickname)
                .orElse(null);

        return new OAuthProfile(
                "KAKAO",
                String.valueOf(me.id()),
                email,
                nickname
        );
    }
}