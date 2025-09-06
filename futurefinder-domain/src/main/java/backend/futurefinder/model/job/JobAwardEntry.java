package backend.futurefinder.model.job;

import java.time.LocalDate;

public record JobAwardEntry(
        Long id,
        String userId,
        String awardName,
        String organization,
        LocalDate awardedOn,
        String description
) {}