package backend.futurefinder.repository.jpa.house;

import backend.futurefinder.jpaentity.subscription.SubscriptionAccountJpaEntity;
import backend.futurefinder.jpaentity.subscription.SubscriptionDepositJpaEntity;
import backend.futurefinder.jpaentity.user.UserLocationJpaEntity;
import backend.futurefinder.jparepository.house.SubscriptionAccountRepository;
import backend.futurefinder.jparepository.house.SubscriptionDepositRepository;
import backend.futurefinder.jparepository.house.UserLocationRepository;
import backend.futurefinder.model.house.DepositEntry;
import backend.futurefinder.model.house.LocationEntry;
import backend.futurefinder.model.user.LocationType;
import backend.futurefinder.repository.house.HouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HouseRepositoryImpl implements HouseRepository {

    private final UserLocationRepository userLocationRepository;
    private final SubscriptionAccountRepository subscriptionAccountRepository;
    private final SubscriptionDepositRepository subscriptionDepositRepository;

    // ------- 지역 -------
    @Override
    public void upsertLocation(String userId, String province, String city, LocationType type) {
        if (type == LocationType.CURRENT) {
            // 현재 주거지는 하나만 (기존 삭제 후 새로 등록)
            userLocationRepository.deleteByUserIdAndLocationType(userId, type);
        } else if (type == LocationType.INTEREST) {
            // 관심지역은 중복 체크 후 추가
            boolean exists = userLocationRepository.existsByUserIdAndLocationTypeAndProvinceAndCity(
                    userId, type, province, city);
            if (exists) {
                log.info("관심지역 중복: userId={}, province={}, city={}", userId, province, city);
                return; // 이미 존재하면 추가하지 않음
            }
        }

        userLocationRepository.save(
                UserLocationJpaEntity.builder()
                        .userId(userId)
                        .province(province)
                        .city(city)
                        .locationType(type)
                        .build()
        );

        log.info("지역 저장 완료: userId={}, province={}, city={}, type={}", userId, province, city, type);
    }

    @Override
    public List<LocationEntry> getLocations(String userId, LocationType type) {
        return userLocationRepository.findByUserIdAndLocationType(userId, type)
                .stream()
                .map(e -> new LocationEntry(e.getProvince(), e.getCity()))
                .toList();
    }

    // ------- 청약 계좌 -------
    @Override
    public void upsertSubscriptionAccount(String userId, String bankName, String accountNumber) {
        subscriptionAccountRepository.findByUserId(userId)
                .ifPresentOrElse(
                        acc -> {
                            String oldAccountNumber = acc.getAccountNumber();
                            log.info("기존 계좌 발견: userId={}, 기존계좌={}, 새계좌={}", userId, oldAccountNumber, accountNumber);

                            // 계좌 정보 업데이트
                            acc.changeBank(bankName);
                            acc.changeAccountNumber(accountNumber);

                            // 계좌번호가 변경된 경우 기존 입금 내역들의 계좌번호도 업데이트
                            if (!oldAccountNumber.equals(accountNumber)) {
                                log.info("계좌번호 변경됨: {} -> {}", oldAccountNumber, accountNumber);

                                List<SubscriptionDepositJpaEntity> deposits =
                                        subscriptionDepositRepository.findBySubscriptionAccountId(oldAccountNumber);

                                log.info("업데이트할 입금 내역 수: {}", deposits.size());

                                for (SubscriptionDepositJpaEntity deposit : deposits) {
                                    deposit.updateAccountNumber(accountNumber);
                                    log.info("입금 내역 계좌번호 업데이트: depositId={}, 새계좌={}",
                                            deposit.getId(), accountNumber);
                                }

                                // 총액은 변경 불필요 (같은 사용자의 입금 내역이므로)
                                log.info("계좌 변경 완료: 총액 유지={}", acc.getTotalAmount());
                            }
                        },
                        () -> {
                            log.info("새 계좌 생성: userId={}, bankName={}, accountNumber={}", userId, bankName, accountNumber);
                            subscriptionAccountRepository.save(
                                    SubscriptionAccountJpaEntity.builder()
                                            .userId(userId)
                                            .bankName(bankName)
                                            .accountNumber(accountNumber)
                                            .totalAmount(BigDecimal.ZERO)
                                            .build()
                            );
                        }
                );
    }

    @Override
    public BigDecimal getSubscriptionTotal(String userId) {
        BigDecimal total = subscriptionAccountRepository.findByUserId(userId)
                .map(SubscriptionAccountJpaEntity::getTotalAmount)
                .orElse(BigDecimal.ZERO);

        log.info("청약 총액 조회: userId={}, totalAmount={}", userId, total);
        return total;
    }

    // ------- 입금 -------
    @Override
    public void addDeposit(String userId, String accountNumber, BigDecimal amount, String memo) {
        log.info("입금 등록 시작: userId={}, accountNumber={}, amount={}", userId, accountNumber, amount);

        // 계좌 존재 여부 확인
        SubscriptionAccountJpaEntity account = subscriptionAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("등록된 청약 계좌가 없습니다. 먼저 계좌를 등록해주세요."));

        // 계좌 소유자 확인
        if (!account.getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 계좌의 소유자가 아닙니다.");
        }

        // 입금 내역 저장
        SubscriptionDepositJpaEntity savedDeposit = subscriptionDepositRepository.save(
                SubscriptionDepositJpaEntity.builder()
                        .userId(userId)
                        .subscriptionAccountId(accountNumber)
                        .depositAmount(amount)
                        .memo(memo)
                        .build()
        );

        // 총액 업데이트
        BigDecimal newTotal = account.getTotalAmount().add(amount);
        account.updateTotalAmount(newTotal);

        log.info("입금 등록 완료: depositId={}, 기존총액={}, 새총액={}",
                savedDeposit.getId(), account.getTotalAmount().subtract(amount), newTotal);
    }

    @Override
    public List<DepositEntry> getRecentDeposits(String userId, int limit) {
        return subscriptionDepositRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, limit))
                .stream()
                .map(d -> new DepositEntry(
                        d.getId(),
                        d.getSubscriptionAccountId(),
                        d.getDepositAmount(),
                        d.getMemo(),
                        d.getCreatedAt()
                ))
                .toList();
    }
}