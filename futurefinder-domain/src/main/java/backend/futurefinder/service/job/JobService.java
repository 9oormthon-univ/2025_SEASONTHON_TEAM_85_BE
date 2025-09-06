// futurefinder-domain 모듈
package backend.futurefinder.service.job;

import backend.futurefinder.model.job.JobActivityEntry;
import backend.futurefinder.model.job.JobAwardEntry;
import backend.futurefinder.model.job.JobEducationEntry;

import java.util.List;
import java.util.Optional;

public interface JobService {
    Optional<JobEducationEntry> findEducationByUserId(String userId);
    void saveEducation(JobEducationEntry entry);

    List<JobEducationEntry> findEducationsByUserId(String userId);

    List<JobActivityEntry> findActivitiesByUserId(String userId);
    void saveActivity(JobActivityEntry entry);

    List<JobAwardEntry> findAwardsByUserId(String userId);
    void saveAward(JobAwardEntry entry);
}
