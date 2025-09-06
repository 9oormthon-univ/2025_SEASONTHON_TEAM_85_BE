// futurefinder-api/src/main/java/backend/futurefinder/dto/request/job/ActivityCreateRequest.java
package backend.futurefinder.dto.request.job;

import backend.futurefinder.model.user.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ActivityUpsertRequest(
        @NotNull ActivityType type,
        @NotBlank String title,
        LocalDate startedAt,
        LocalDate endedAt,
        String memo
) {}
