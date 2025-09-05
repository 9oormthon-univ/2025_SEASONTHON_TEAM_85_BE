package backend.futurefinder.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "krx.stock")
public class StockApiProperties {
    /** 예: https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService */
    private String baseUrl;
    /** 공공데이터포털 서비스키 (권장: 디코딩 키) */
    private String serviceKey;
    /** 초 단위 타임아웃 */
    private int timeoutSeconds = 8;
    /** 페이지 사이즈 */
    private int pageSize = 100;
    /** 최근 영업일 탐색 최대 일수 */
    private int maxBackDays = 7;
}