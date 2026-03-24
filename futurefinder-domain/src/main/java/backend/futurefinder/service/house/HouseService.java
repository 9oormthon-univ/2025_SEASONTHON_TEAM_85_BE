package backend.futurefinder.service.house;

import backend.futurefinder.model.house.DepositEntry;
import backend.futurefinder.model.house.HouseSummary;
import backend.futurefinder.model.house.LocationEntry;
import backend.futurefinder.model.user.LocationType;

import java.math.BigDecimal;
import java.util.List;

public interface HouseService {
    void saveLocation(String userId, String province, String city, LocationType type);
    List<LocationEntry> findLocations(String userId, LocationType type);
    void saveSubscriptionAccount(String userId, String bankName, String accountNumber);
    BigDecimal findSubscriptionTotal(String userId);
    void saveDeposit(String userId, String accountNumber, BigDecimal amount, String memo);
    List<DepositEntry> findRecentDeposits(String userId, int limit);
    HouseSummary getSummary(String userId);
}
