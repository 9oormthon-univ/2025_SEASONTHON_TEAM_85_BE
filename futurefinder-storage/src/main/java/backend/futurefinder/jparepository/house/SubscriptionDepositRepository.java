package backend.futurefinder.jparepository.house;

import backend.futurefinder.jpaentity.subscription.SubscriptionDepositJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionDepositRepository extends JpaRepository<SubscriptionDepositJpaEntity, Long> {

    // 사용자별 최근 입금 내역 조회 (기존)
    List<SubscriptionDepositJpaEntity> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    // 특정 계좌번호의 모든 입금 내역 조회 (새로 추가)
    List<SubscriptionDepositJpaEntity> findBySubscriptionAccountId(String accountNumber);

    // 사용자 + 계좌번호로 입금 내역 조회 (추가 보안)
    List<SubscriptionDepositJpaEntity> findByUserIdAndSubscriptionAccountId(String userId, String accountNumber);
}