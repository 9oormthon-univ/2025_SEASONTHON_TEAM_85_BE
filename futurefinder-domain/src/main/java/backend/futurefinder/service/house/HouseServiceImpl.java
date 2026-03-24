package backend.futurefinder.service.house;

import backend.futurefinder.model.house.DepositEntry;
import backend.futurefinder.model.house.HouseSummary;
import backend.futurefinder.model.house.LocationEntry;
import backend.futurefinder.model.user.LocationType;
import backend.futurefinder.repository.house.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HouseServiceImpl implements HouseService {

    private final HouseRepository houseRepository;

    @Override
    public void saveLocation(String userId, String province, String city, LocationType type) {
        houseRepository.saveLocation(userId, province, city, type);
    }

    @Override
    public List<LocationEntry> findLocations(String userId, LocationType type) {
        return houseRepository.findLocations(userId, type);
    }

    @Override
    public void saveSubscriptionAccount(String userId, String bankName, String accountNumber) {
        houseRepository.saveSubscriptionAccount(userId, bankName, accountNumber);
    }

    @Override
    public BigDecimal findSubscriptionTotal(String userId) {
        return houseRepository.findSubscriptionTotal(userId);
    }

    @Override
    public void saveDeposit(String userId, String accountNumber, BigDecimal amount, String memo) {
        houseRepository.saveDeposit(userId, accountNumber, amount, memo);
    }

    @Override
    public List<DepositEntry> findRecentDeposits(String userId, int limit) {
        return houseRepository.findRecentDeposits(userId, limit);
    }

    @Transactional(readOnly = true)
    @Override
    public HouseSummary getSummary(String userId) {
        return new HouseSummary(
                findLocations(userId, LocationType.CURRENT),
                findLocations(userId, LocationType.INTEREST),
                findSubscriptionTotal(userId),
                findRecentDeposits(userId, 3)
        );
    }
}
