package backend.futurefinder.dto.response.house;

import java.math.BigDecimal;

public record AccountSummaryResponse(
        String bankName,
        String accountNumber,
        BigDecimal totalAmount
) {}
