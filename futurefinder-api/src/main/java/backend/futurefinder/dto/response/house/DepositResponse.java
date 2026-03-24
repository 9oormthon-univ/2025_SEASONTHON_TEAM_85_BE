package backend.futurefinder.dto.response.house;

import backend.futurefinder.model.house.DepositEntry;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepositResponse(
        Long id,
        String accountNumber,
        BigDecimal amount,
        String memo,
        LocalDateTime createdAt
) {
    public static DepositResponse from(DepositEntry entry) {
        return new DepositResponse(
                entry.getId(),
                entry.getSubscriptionAccountId(),
                entry.getDepositAmount(),
                entry.getMemo(),
                entry.getCreatedAt()
        );
    }
}
