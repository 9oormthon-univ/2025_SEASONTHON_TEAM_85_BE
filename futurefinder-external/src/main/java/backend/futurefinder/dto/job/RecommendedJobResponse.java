package backend.futurefinder.dto.job;

public record RecommendedJobResponse(
        String companyName,
        String position,
        String description,
        String requirements,
        String deadline,
        String matchReason
){}
