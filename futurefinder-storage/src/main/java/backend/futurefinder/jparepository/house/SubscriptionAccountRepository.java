package backend.futurefinder.jparepository.house;

import backend.futurefinder.jpaentity.subscription.SubscriptionAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionAccountRepository extends JpaRepository<SubscriptionAccountJpaEntity, Long> {
    Optional<SubscriptionAccountJpaEntity> findByUserId(String userId);
    Optional<SubscriptionAccountJpaEntity> findByAccountNumber(String accountNumber);
}
