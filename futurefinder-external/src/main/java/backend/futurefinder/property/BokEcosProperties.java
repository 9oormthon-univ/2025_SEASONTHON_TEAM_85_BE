package backend.futurefinder.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter @Setter
@ConfigurationProperties(prefix = "ecos.bok")
public class BokEcosProperties {

    private String baseUrl = "https://ecos.bok.or.kr";
    // 발급받은 ECOS API 키 (필수)


    private String apiKey;

    private String format = "json";

    private String lang = "kr";
    // 1회 조회 종료건수(페이지 크기). 딱 맞는 단어만 필터할 거라 50~100 정도면 충분./
    private int pageSize = 50;
    // 외부호출 타임아웃(초)
    private int timeoutSeconds = 5;
    // CONTENT 최대 길이(선택, 그대로 두면 제한 없음). 0이하면 무제한/
    private int maxContentLength = 0;
}