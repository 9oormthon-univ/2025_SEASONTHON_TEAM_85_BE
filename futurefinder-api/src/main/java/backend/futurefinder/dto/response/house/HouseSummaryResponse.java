// dto/response/house/HouseSummaryResponse.java
package backend.futurefinder.dto.response.house;

import java.math.BigDecimal;
import java.util.List;

public record HouseSummaryResponse(
        List<LocationResponse> currentLocations,
        List<LocationResponse> interestLocations,
        BigDecimal subscriptionTotalAmount,
        List<DepositResponse> recentDeposits
) {}
