package backend.futurefinder.repository.house;

import backend.futurefinder.model.house.DepositEntry;
import backend.futurefinder.model.house.LocationEntry;
import backend.futurefinder.model.user.LocationType;

import java.math.BigDecimal;
import java.util.List;

public interface HouseRepository {

    // 지역
    void saveLocation(String userId, String province, String city, LocationType type);
    List<LocationEntry> findLocations(String userId, LocationType type);

    // 청약 계좌 관리
    void saveSubscriptionAccount(String userId, String bankName, String accountNumber);
    BigDecimal findSubscriptionTotal(String userId);

    // 입금 관리
    void saveDeposit(String userId, String accountNumber, BigDecimal amount, String memo);
    List<DepositEntry> findRecentDeposits(String userId, int limit);
}
