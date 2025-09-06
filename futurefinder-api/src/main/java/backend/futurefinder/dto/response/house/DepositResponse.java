package backend.futurefinder.dto.response.house;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepositResponse(
        Long id,
        String accountNumber,
        BigDecimal amount,
        String memo,
        LocalDateTime createdAt
) {}
