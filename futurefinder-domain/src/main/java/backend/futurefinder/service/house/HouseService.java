package backend.futurefinder.service.house;

import backend.futurefinder.model.house.DepositEntry;
import backend.futurefinder.model.house.LocationEntry;
import backend.futurefinder.model.user.LocationType;

import java.math.BigDecimal;
import java.util.List;

public interface HouseService {
    void upsertLocation(String userId, String province, String city, LocationType type);
    List<LocationEntry> getLocations(String userId, LocationType type);
    void upsertSubscriptionAccount(String userId, String bankName, String accountNumber);
    BigDecimal getSubscriptionTotal(String userId);
    void addDeposit(String userId, String accountNumber, BigDecimal amount, String memo);
    List<DepositEntry> getRecentDeposits(String userId, int limit);
}
