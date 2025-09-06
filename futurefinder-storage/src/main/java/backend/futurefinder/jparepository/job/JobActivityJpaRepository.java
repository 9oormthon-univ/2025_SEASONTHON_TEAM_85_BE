package backend.futurefinder.jparepository.job;

import backend.futurefinder.jpaentity.job.JobActivityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobActivityJpaRepository extends JpaRepository<JobActivityJpaEntity, Long> {
    List<JobActivityJpaEntity> findByUserIdOrderByStartedAtDesc(String userId);
}