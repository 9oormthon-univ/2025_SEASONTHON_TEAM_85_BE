// futurefinder-api/src/main/java/backend/futurefinder/dto/response/job/ActivityResponse.java
package backend.futurefinder.dto.response.job;

import backend.futurefinder.model.user.ActivityType;

import java.time.LocalDate;

public record ActivityResponse(
        Long id,
        ActivityType type,
        String title,
        LocalDate startedAt,
        LocalDate endedAt,
        String memo
) {}
