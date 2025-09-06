package backend.futurefinder.model.job;

import backend.futurefinder.model.user.ActivityType;

import java.time.LocalDate;

public record JobActivityEntry(
        Long id,
        String userId,
        ActivityType type,
        String title,
        LocalDate startedOn,  // startedAt → startedOn
        LocalDate endedOn,    // endedAt → endedOn
        String memo
) {}