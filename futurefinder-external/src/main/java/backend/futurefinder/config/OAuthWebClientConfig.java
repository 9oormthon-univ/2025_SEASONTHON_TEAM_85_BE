package backend.futurefinder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OAuthWebClientConfig {

    @Bean(name = "kakaoWebClient")
    public WebClient kakaoWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://kapi.kakao.com")
                .build();
    }
}