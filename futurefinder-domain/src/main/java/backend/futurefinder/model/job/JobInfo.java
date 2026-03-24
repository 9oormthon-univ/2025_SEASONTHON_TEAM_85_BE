package backend.futurefinder.model.job;

import java.util.List;

public record JobInfo(
        List<JobEducationEntry> educations,
        List<JobActivityEntry> activities,
        List<JobAwardEntry> awards
) {}
