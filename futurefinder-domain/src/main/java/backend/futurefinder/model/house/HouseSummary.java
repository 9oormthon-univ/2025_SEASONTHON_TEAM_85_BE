package backend.futurefinder.model.house;

import java.math.BigDecimal;
import java.util.List;

public record HouseSummary(
        List<LocationEntry> currentLocations,
        List<LocationEntry> interestLocations,
        BigDecimal subscriptionTotalAmount,
        List<DepositEntry> recentDeposits
) {}
