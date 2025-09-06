package backend.futurefinder.service.job;

import backend.futurefinder.model.job.JobActivityEntry;
import backend.futurefinder.model.job.JobAwardEntry;
import backend.futurefinder.model.job.JobEducationEntry;
import backend.futurefinder.repository.job.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<JobEducationEntry> findEducationByUserId(String userId) {
        return jobRepository.findEducationByUserId(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<JobEducationEntry> findEducationsByUserId(String userId) {
        return jobRepository.findEducationsByUserId(userId);
    }

    @Override
    public void saveEducation(JobEducationEntry entry) {
        jobRepository.saveEducation(entry);
    }

    @Transactional(readOnly = true)
    @Override
    public List<JobActivityEntry> findActivitiesByUserId(String userId) {
        return jobRepository.findActivitiesByUserId(userId);
    }

    @Override
    public void saveActivity(JobActivityEntry entry) {
        jobRepository.saveActivity(entry);
    }

    // 수상 내역
    @Transactional(readOnly = true)
    @Override
    public List<JobAwardEntry> findAwardsByUserId(String userId) {
        return jobRepository.findAwardsByUserId(userId);
    }

    @Override
    public void saveAward(JobAwardEntry entry) {
        jobRepository.saveAward(entry);
    }
}
