package backend.futurefinder.external;

import backend.futurefinder.dto.job.RecommendedActivityResponse;
import backend.futurefinder.dto.job.RecommendedJobResponse;

import java.util.List;

public interface JobAIRecommendationService {
    List<RecommendedActivityResponse> generateRecommendedActivities(String userId);
    List<RecommendedJobResponse> generateRecommendedJobs(String userId);
}