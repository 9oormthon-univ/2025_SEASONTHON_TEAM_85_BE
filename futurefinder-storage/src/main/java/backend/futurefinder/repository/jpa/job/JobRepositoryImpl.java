// futurefinder-storage 모듈
package backend.futurefinder.repository.jpa.job;

import backend.futurefinder.jpaentity.job.JobEducationJpaEntity;
import backend.futurefinder.jpaentity.job.JobActivityJpaEntity;
import backend.futurefinder.jpaentity.user.UserAwardJpaEntity;
import backend.futurefinder.jparepository.job.JobEducationJpaRepository;
import backend.futurefinder.jparepository.job.JobActivityJpaRepository;
import backend.futurefinder.jparepository.job.UserAwardJpaRepository;
import backend.futurefinder.model.job.JobAwardEntry;
import backend.futurefinder.model.job.JobEducationEntry;
import backend.futurefinder.model.job.JobActivityEntry;
import backend.futurefinder.repository.job.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JobRepositoryImpl implements JobRepository {

    private final JobEducationJpaRepository educationRepository;
    private final JobActivityJpaRepository activityRepository;
    private final UserAwardJpaRepository awardRepository;

    // ------- 학력 -------
    @Override
    public Optional<JobEducationEntry> findEducationByUserId(String userId) {
        return educationRepository.findByUserId(userId)
                .map(e -> new JobEducationEntry(
                        e.getId(),
                        e.getUserId(),
                        e.getSchoolName(),
                        e.getMajor(),
                        e.getStatus(),
                        e.getGraduationYear()
                ));
    }

    @Override
    public List<JobEducationEntry> findEducationsByUserId(String userId) {
        return educationRepository.findAllByUserId(userId)  // findByUserId → findAllByUserId로 변경
                .stream()
                .map(e -> new JobEducationEntry(
                        e.getId(),
                        e.getUserId(),
                        e.getSchoolName(),
                        e.getMajor(),
                        e.getStatus(),
                        e.getGraduationYear()
                ))
                .toList();
    }

    @Override
    public void saveEducation(JobEducationEntry entry) {
        JobEducationJpaEntity entity = JobEducationJpaEntity.builder()
                .id(entry.id())
                .userId(entry.userId())
                .schoolName(entry.schoolName())
                .major(entry.major())
                .status(entry.status())
                .graduationYear(entry.graduationYear())
                .build();
        educationRepository.save(entity);
    }

    // ------- 대외활동 -------
    @Override
    public List<JobActivityEntry> findActivitiesByUserId(String userId) {
        return activityRepository.findByUserIdOrderByStartedAtDesc(userId)
                .stream()
                .map(a -> new JobActivityEntry(
                        a.getId(),
                        a.getUserId(),
                        a.getType(),
                        a.getTitle(),
                        a.getStartedAt(),
                        a.getEndedAt(),
                        a.getMemo()
                ))
                .toList();
    }

    @Override
    public void saveActivity(JobActivityEntry entry) {
        JobActivityJpaEntity entity = JobActivityJpaEntity.builder()
                .id(entry.id())
                .userId(entry.userId())
                .type(entry.type())
                .title(entry.title())
                .startedAt(entry.startedOn())
                .endedAt(entry.endedOn())
                .memo(entry.memo())
                .build();
        activityRepository.save(entity);
    }

    // ------- 수상 내역 -------
    @Override
    public List<JobAwardEntry> findAwardsByUserId(String userId) {
        return awardRepository.findByUserIdOrderByAwardedOnDesc(userId)
                .stream()
                .map(a -> new JobAwardEntry(
                        a.getId(),
                        a.getUserId(),
                        a.getAwardName(),
                        a.getOrganization(),
                        a.getAwardedOn(),
                        a.getDescription()
                ))
                .toList();
    }

    @Override
    public void saveAward(JobAwardEntry entry) {
        UserAwardJpaEntity entity = UserAwardJpaEntity.builder()
                .id(entry.id())
                .userId(entry.userId())
                .awardName(entry.awardName())
                .organization(entry.organization())
                .awardedOn(entry.awardedOn())
                .description(entry.description())
                .build();
        awardRepository.save(entity);
    }
}