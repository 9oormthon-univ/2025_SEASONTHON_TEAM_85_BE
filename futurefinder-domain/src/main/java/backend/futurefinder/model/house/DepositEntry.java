package backend.futurefinder.model.house;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DepositEntry {
    private final Long id;
    private final String subscriptionAccountId; // 컨트롤러에서 그대로 씀
    private final BigDecimal depositAmount;
    private final String memo;
    private final LocalDateTime createdAt;
}
