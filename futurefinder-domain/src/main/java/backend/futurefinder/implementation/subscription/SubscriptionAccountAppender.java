//package backend.futurefinder.implementation.subscription;
//
//import backend.futurefinder.jpaentity.subscription.SubscriptionAccountJpaEntity;
//import backend.futurefinder.jpaentity.user.UserJpaEntity;
//import backend.futurefinder.jparepository.subscription.SubscriptionAccountRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Component
//@RequiredArgsConstructor
//public class SubscriptionAccountAppender {
//    private final SubscriptionAccountRepository subscriptionAccountRepository;
//
//    @Transactional
//    public SubscriptionAccountJpaEntity append(UserJpaEntity user, String bankName, String accountNumber) {
//        SubscriptionAccountJpaEntity newAccount = SubscriptionAccountJpaEntity.builder()
//                .user(user)
//                .bankName(bankName)
//                .accountNumber(accountNumber)
//                .build();
//        return subscriptionAccountRepository.save(newAccount);
//    }
//}