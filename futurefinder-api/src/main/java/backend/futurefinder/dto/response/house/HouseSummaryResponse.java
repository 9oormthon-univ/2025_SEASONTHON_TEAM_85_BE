package backend.futurefinder.dto.response.house;

import backend.futurefinder.model.house.HouseSummary;

import java.math.BigDecimal;
import java.util.List;

public record HouseSummaryResponse(
        List<LocationResponse> currentLocations,
        List<LocationResponse> interestLocations,
        BigDecimal subscriptionTotalAmount,
        List<DepositResponse> recentDeposits
) {
    public static HouseSummaryResponse from(HouseSummary summary) {
        return new HouseSummaryResponse(
                summary.currentLocations().stream()
                        .map(e -> LocationResponse.from(e, "CURRENT")).toList(),
                summary.interestLocations().stream()
                        .map(e -> LocationResponse.from(e, "INTEREST")).toList(),
                summary.subscriptionTotalAmount(),
                summary.recentDeposits().stream()
                        .map(DepositResponse::from).toList()
        );
    }
}
