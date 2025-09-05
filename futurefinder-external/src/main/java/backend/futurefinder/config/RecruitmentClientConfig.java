package backend.futurefinder.config;

import backend.futurefinder.property.RecruitmentApiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(RecruitmentApiProperties.class)
public class RecruitmentClientConfig {

    @Bean("moefRecruitmentWebClient") // ← 이름 지정
    public WebClient moefRecruitmentWebClient(RecruitmentApiProperties props) {
        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .build();
    }
}