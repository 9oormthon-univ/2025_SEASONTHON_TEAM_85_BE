package backend.futurefinder.dto.response.job;

import backend.futurefinder.model.job.JobInfo;

import java.util.List;

public record JobInfoResponse(
        List<EducationResponse> educations,
        List<ActivityResponse> activities,
        List<AwardResponse> awards
) {
    public static JobInfoResponse from(JobInfo info) {
        return new JobInfoResponse(
                info.educations().stream().map(EducationResponse::from).toList(),
                info.activities().stream().map(ActivityResponse::from).toList(),
                info.awards().stream().map(AwardResponse::from).toList()
        );
    }
}