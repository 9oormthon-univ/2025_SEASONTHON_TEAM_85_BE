package backend.futurefinder.dto.response.job;

import backend.futurefinder.model.job.JobAwardEntry;

import java.time.LocalDate;

public record AwardResponse(
        Long id,
        String awardName,
        String organization,
        LocalDate awardedOn,
        String description
) {
    public static AwardResponse from(JobAwardEntry a) {
        return new AwardResponse(a.id(), a.awardName(), a.organization(), a.awardedOn(), a.description());
    }
}