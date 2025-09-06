package backend.futurefinder.dto.response.job;

import java.time.LocalDate;

public record AwardResponse(
        Long id,
        String awardName,
        String organization,
        LocalDate awardedOn,
        String description
) {}