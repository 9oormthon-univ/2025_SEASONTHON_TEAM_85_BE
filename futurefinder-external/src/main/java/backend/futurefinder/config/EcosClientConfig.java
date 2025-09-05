package backend.futurefinder.config;

import backend.futurefinder.property.BokEcosProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(BokEcosProperties.class)
public class EcosClientConfig {

    @Bean
    public WebClient ecosWebClient(BokEcosProperties props) {
        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeaders(h -> h.setAccept(java.util.List.of(MediaType.APPLICATION_JSON)))
                .build();
    }
}