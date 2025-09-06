// futurefinder-api/src/main/java/backend/futurefinder/dto/response/job/JobSummaryResponse.java
package backend.futurefinder.dto.response.job;

import java.util.List;

public record JobSummaryResponse(
        EducationResponse education,
        List<ActivityResponse> activities
) {}
