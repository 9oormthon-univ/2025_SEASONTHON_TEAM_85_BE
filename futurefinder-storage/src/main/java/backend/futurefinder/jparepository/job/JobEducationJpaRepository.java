package backend.futurefinder.jparepository.job;

import backend.futurefinder.jpaentity.job.JobEducationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobEducationJpaRepository extends JpaRepository<JobEducationJpaEntity, Long> {
    Optional<JobEducationJpaEntity> findByUserId(String userId);
    List<JobEducationJpaEntity> findAllByUserId(String userId);
}