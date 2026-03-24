package backend.futurefinder.dto.response.job;

import backend.futurefinder.model.job.JobEducationEntry;

public record EducationResponse(
        Long id,
        String schoolName,
        String major,
        String status,
        Integer graduationYear
) {
    public static EducationResponse from(JobEducationEntry e) {
        return new EducationResponse(e.id(), e.schoolName(), e.major(), e.status(), e.graduationYear());
    }
}
