package backend.futurefinder.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kakao.oauth")
public class KakaoOAuthProperties {
    private String baseUrl = "https://kapi.kakao.com";
    private int timeoutSeconds = 5;
}
