package backend.futurefinder.repository.house;

import backend.futurefinder.model.house.DepositEntry;
import backend.futurefinder.model.house.LocationEntry;
import backend.futurefinder.model.user.LocationType;

import java.math.BigDecimal;
import java.util.List;

public interface HouseRepository {

    // 지역
    void upsertLocation(String userId, String province, String city, LocationType type);
    List<LocationEntry> getLocations(String userId, LocationType type);

    // 청약 계좌 관리
    void upsertSubscriptionAccount(String userId, String bankName, String accountNumber);
    BigDecimal getSubscriptionTotal(String userId);

    // 입금 관리
    void addDeposit(String userId, String accountNumber, BigDecimal amount, String memo);
    List<DepositEntry> getRecentDeposits(String userId, int limit);
}
