// futurefinder-api 모듈
package backend.futurefinder.dto.response.job;

import java.util.List;

public record JobInfoResponse(
        List<EducationResponse> educations,
        List<ActivityResponse> activities,
        List<AwardResponse> awards
) {
}