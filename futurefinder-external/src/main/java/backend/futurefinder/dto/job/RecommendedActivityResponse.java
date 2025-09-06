package backend.futurefinder.dto.job;

public record RecommendedActivityResponse(
        String title,
        String type,
        String description,
        String period,
        String benefits){}


