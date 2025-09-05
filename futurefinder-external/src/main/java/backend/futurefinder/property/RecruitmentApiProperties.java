package backend.futurefinder.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "moef.recruitment")
public class RecruitmentApiProperties {


    /** e.g. https://apis.data.go.kr/1051000/recruitment */
    private String baseUrl = "https://apis.data.go.kr/1051000/recruitment";
    /** 공공데이터포털에서 받은 (URL 인코딩된) 서비스키 */
    private String serviceKey; // 반드시 설정
    /** 타임아웃(초) */
    private int timeoutSeconds = 6;

}
