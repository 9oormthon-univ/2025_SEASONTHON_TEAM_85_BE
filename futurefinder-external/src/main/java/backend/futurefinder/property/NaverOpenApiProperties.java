package backend.futurefinder.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(prefix = "naver.openapi")
public class NaverOpenApiProperties {
    private String clientId;
    private String clientSecret;
    private String baseUrl = "https://openapi.naver.com";
    /** 뉴스 스크래핑 시 한 번에 모을 최대 기사 수 */
    private int fetchSize = 50;
    /** 내부 페이징 단위 */
    private int pageSize = 5;
    /** 캐시 유지 시간(초) */
    private int cacheTtlSeconds = 60;
    /** 스크래핑 타임아웃(초) */
    private int scrapingTimeoutSeconds = 5;
    /** 메타 조회 타임아웃(초) */
    private int metaTimeoutSeconds = 4;
}