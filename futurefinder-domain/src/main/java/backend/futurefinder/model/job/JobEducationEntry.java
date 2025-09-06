package backend.futurefinder.model.job;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record JobEducationEntry(
        Long id,
        String userId,
        String schoolName,
        String major,
        String status,
        Integer graduationYear
) {}
