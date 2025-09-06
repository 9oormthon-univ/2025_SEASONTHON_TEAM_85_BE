//package backend.futurefinder.implementation.subscription;
//
//import backend.futurefinder.jpaentity.subscription.SubscriptionAccountJpaEntity;
//import backend.futurefinder.jpaentity.subscription.SubscriptionDepositJpaEntity;
//import backend.futurefinder.jpaentity.user.UserJpaEntity;
//import backend.futurefinder.jparepository.subscription.SubscriptionDepositRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//@Component
//@RequiredArgsConstructor
//public class SubscriptionDepositAppender {
//    private final SubscriptionDepositRepository subscriptionDepositRepository;
//
//    @Transactional
//    public void append(UserJpaEntity user, SubscriptionAccountJpaEntity account, BigDecimal amount, LocalDate depositDate) {
//        SubscriptionDepositJpaEntity newDeposit = SubscriptionDepositJpaEntity.builder()
//                .userId(user.getUserId())
//                .subscriptionAccountId(String.valueOf(account.getId()))
//                .depositAmount(amount)
//                .build();
//        subscriptionDepositRepository.save(newDeposit);
//    }
//}