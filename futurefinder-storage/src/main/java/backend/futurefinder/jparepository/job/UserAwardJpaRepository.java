package backend.futurefinder.jparepository.job;

import backend.futurefinder.jpaentity.user.UserAwardJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAwardJpaRepository extends JpaRepository<UserAwardJpaEntity, Long> {
    List<UserAwardJpaEntity> findByUserIdOrderByAwardedOnDesc(String userId);
}