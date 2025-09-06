package backend.futurefinder.jparepository.job;

import backend.futurefinder.jpaentity.user.UserActivityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserActivityJpaRepository extends JpaRepository<UserActivityJpaEntity, Long> {
    List<UserActivityJpaEntity> findByUserIdOrderByStartedOnDesc(String userId);
}