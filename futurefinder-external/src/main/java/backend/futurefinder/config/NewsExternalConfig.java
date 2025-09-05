package backend.futurefinder.config;

import backend.futurefinder.property.NaverOpenApiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(NaverOpenApiProperties.class)
public class NewsExternalConfig {

    @Bean
    public WebClient naverOpenApiWebClient() {
        return WebClient.builder()
                .baseUrl("https://openapi.naver.com")
                .clientConnector(new ReactorClientHttpConnector())
                .defaultHeaders(h -> {
                    // 개별 요청에서 헤더를 세팅하지만, 기본 Accept 정도는 지정 가능
                    h.add("Accept", "application/json");
                })
                .build();
    }
}