package backend.futurefinder.service.house;

import backend.futurefinder.model.house.DepositEntry;
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
    public void upsertLocation(String userId, String province, String city, LocationType type) {
        houseRepository.upsertLocation(userId, province, city, type);
    }

    @Override
    public List<LocationEntry> getLocations(String userId, LocationType type) {
        return houseRepository.getLocations(userId, type);
    }

    @Override
    public void upsertSubscriptionAccount(String userId, String bankName, String accountNumber) {
        houseRepository.upsertSubscriptionAccount(userId, bankName, accountNumber);
    }

    @Override
    public BigDecimal getSubscriptionTotal(String userId) {
        return houseRepository.getSubscriptionTotal(userId);
    }

    @Override
    public void addDeposit(String userId, String accountNumber, BigDecimal amount, String memo) {
        houseRepository.addDeposit(userId, accountNumber, amount, memo);
    }

    @Override
    public List<DepositEntry> getRecentDeposits(String userId, int limit) {
        return houseRepository.getRecentDeposits(userId, limit);
    }
}
