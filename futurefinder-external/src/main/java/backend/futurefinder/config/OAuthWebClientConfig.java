package backend.futurefinder.config;

import backend.futurefinder.property.KakaoOAuthProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(KakaoOAuthProperties.class)
public class OAuthWebClientConfig {

    @Bean(name = "kakaoWebClient")
    public WebClient kakaoWebClient(WebClient.Builder builder, KakaoOAuthProperties props) {
        return builder
                .baseUrl(props.getBaseUrl())
                .build();
    }
}
