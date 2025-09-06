package backend.futurefinder.jparepository.job;

import backend.futurefinder.jpaentity.user.AcademicProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AcademicProfileRepository extends JpaRepository<AcademicProfileJpaEntity, Long> {
    Optional<AcademicProfileJpaEntity> findByUserId(String userId);
}