package backend.futurefinder.jparepository.user;

import backend.futurefinder.jpaentity.user.UserJpaEntity;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.model.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {
    Optional<UserJpaEntity> findByAccountIdAndStatus(String accountId, AccessStatus status);
    Optional<UserJpaEntity> findByUserName(String userName);
    Optional<UserJpaEntity> findByNickNameAndStatus(String nickName, AccessStatus status);
}

