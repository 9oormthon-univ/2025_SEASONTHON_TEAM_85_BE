//package backend.futurefinder.implementation.subscription;
//
//import backend.futurefinder.jpaentity.subscription.SubscriptionAccountJpaEntity;
//import backend.futurefinder.jpaentity.user.UserJpaEntity;
//import backend.futurefinder.jparepository.subscription.SubscriptionAccountRepository;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class SubscriptionAccountReader {
//    private final SubscriptionAccountRepository subscriptionAccountRepository;
//
//    public void validateExists(UserJpaEntity user) {
//        subscriptionAccountRepository.findByUserId(user).ifPresent(account -> {
//            throw new IllegalStateException("이미 등록된 청약 통장이 존재합니다.");
//        });
//    }
//
//    public SubscriptionAccountJpaEntity read(UserJpaEntity user) {
//        return subscriptionAccountRepository.findByUserId(user)
//                .orElseThrow(() -> new EntityNotFoundException("해당 사용자의 청약 통장 정보를 찾을 수 없습니다."));
//    }
//}