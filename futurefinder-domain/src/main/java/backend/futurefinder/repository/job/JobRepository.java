package backend.futurefinder.repository.job;

import backend.futurefinder.model.job.JobAwardEntry;
import backend.futurefinder.model.job.JobEducationEntry;
import backend.futurefinder.model.job.JobActivityEntry;

import java.util.List;
import java.util.Optional;

public interface JobRepository {

    // 학력
    Optional<JobEducationEntry> findEducationByUserId(String userId);
    List<JobEducationEntry> findEducationsByUserId(String userId);  // 추가
    void saveEducation(JobEducationEntry education);

    // 대외활동
    List<JobActivityEntry> findActivitiesByUserId(String userId);
    void saveActivity(JobActivityEntry activity);

    // 수상 내역
    List<JobAwardEntry> findAwardsByUserId(String userId);
    void saveAward(JobAwardEntry award);
}