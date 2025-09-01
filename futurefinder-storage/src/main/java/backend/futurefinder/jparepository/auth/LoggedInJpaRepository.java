package backend.futurefinder.jparepository.auth;

import backend.futurefinder.jpaentity.auth.LoggedInJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface LoggedInJpaRepository extends JpaRepository<LoggedInJpaEntity, String> {
    void deleteByRefreshToken(String refreshToken);
}
