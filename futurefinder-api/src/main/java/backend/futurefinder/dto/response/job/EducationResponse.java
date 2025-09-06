// futurefinder-api/src/main/java/backend/futurefinder/dto/response/job/EducationResponse.java
package backend.futurefinder.dto.response.job;

public record EducationResponse(
        Long id,
        String schoolName,
        String major,
        String status,
        Integer graduationYear
) {}
