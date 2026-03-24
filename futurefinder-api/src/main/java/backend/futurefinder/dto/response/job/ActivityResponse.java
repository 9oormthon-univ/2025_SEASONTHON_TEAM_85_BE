package backend.futurefinder.dto.response.job;

import backend.futurefinder.model.job.JobActivityEntry;
import backend.futurefinder.model.user.ActivityType;

import java.time.LocalDate;

public record ActivityResponse(
        Long id,
        ActivityType type,
        String title,
        LocalDate startedAt,
        LocalDate endedAt,
        String memo
) {
    public static ActivityResponse from(JobActivityEntry a) {
        return new ActivityResponse(a.id(), a.type(), a.title(), a.startedOn(), a.endedOn(), a.memo());
    }
}
