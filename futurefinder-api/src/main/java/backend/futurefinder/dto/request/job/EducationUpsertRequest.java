package backend.futurefinder.dto.request.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EducationUpsertRequest(
        @NotBlank String schoolName,
        @NotBlank String major,
        @NotBlank String status,
        Integer graduationYear // null 가능
) {}
