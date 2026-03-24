package backend.futurefinder.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "openai.api")
public class OpenAIProperties {
    private String key;
    private String baseUrl = "https://api.openai.com/v1";
    private String model = "gpt-3.5-turbo";
    private int maxTokens = 500;
    private double temperature = 0.7;
    private int timeoutSeconds = 30;
}
