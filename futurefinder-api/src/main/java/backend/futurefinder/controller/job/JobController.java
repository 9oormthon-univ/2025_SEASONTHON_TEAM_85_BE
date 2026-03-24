// futurefinder-api 모듈
package backend.futurefinder.controller.job;

import backend.futurefinder.dto.job.RecommendedActivityResponse;
import backend.futurefinder.dto.job.RecommendedJobResponse;
import backend.futurefinder.dto.request.job.ActivityUpsertRequest;
import backend.futurefinder.dto.request.job.AwardUpsertRequest;
import backend.futurefinder.dto.request.job.EducationUpsertRequest;
import backend.futurefinder.dto.response.job.*;
import backend.futurefinder.model.job.JobActivityEntry;
import backend.futurefinder.model.job.JobAwardEntry;
import backend.futurefinder.model.job.JobEducationEntry;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.response.HttpResponse;
import backend.futurefinder.response.SuccessOnlyResponse;
import backend.futurefinder.external.JobAIRecommendationService;
import backend.futurefinder.service.job.JobService;
import backend.futurefinder.util.helper.ResponseHelper;
import backend.futurefinder.util.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job")
public class JobController {

    private final JobService jobService;
    private final JobAIRecommendationService jobAIRecommendationService;
    // ------- 정보 확인 (통합 조회) -------
    @Operation(summary = "취업 관련 정보 전체 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/info")
    public ResponseEntity<HttpResponse<JobInfoResponse>> getJobInfo(
            @CurrentUser UserId userId
    ) {
        return ResponseHelper.success(JobInfoResponse.from(jobService.getJobInfo(userId.getId())));
    }

    // ------- 학력 -------
    @Operation(summary = "학업 정보 등록/수정", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/education")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> upsertEducation(
            @CurrentUser UserId userId,
            @Valid @RequestBody EducationUpsertRequest req
    ) {
        jobService.saveEducation(new JobEducationEntry(
                null,
                userId.getId(),
                req.schoolName(),
                req.major(),
                req.status(),
                req.graduationYear()
        ));
        return ResponseHelper.successOnly();
    }

    @Operation(summary = "학업 정보 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/education")
    public ResponseEntity<HttpResponse<EducationResponse>> getEducation(
            @CurrentUser UserId userId
    ) {
        var edu = jobService.findEducationByUserId(userId.getId())
                .map(EducationResponse::from)
                .orElse(null);
        return ResponseHelper.success(edu);
    }

    // ------- 대외활동 -------
    @Operation(summary = "대외활동 등록", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/activity")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> upsertActivity(
            @CurrentUser UserId userId,
            @RequestBody ActivityUpsertRequest req
    ) {
        jobService.saveActivity(new JobActivityEntry(
                null,
                userId.getId(),
                req.type(),
                req.title(),
                req.startedAt(),
                req.endedAt(),
                req.memo()
        ));
        return ResponseHelper.successOnly();
    }

    @Operation(summary = "대외활동 목록 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/activities")
    public ResponseEntity<HttpResponse<List<ActivityResponse>>> getActivities(
            @CurrentUser UserId userId
    ) {
        var res = jobService.findActivitiesByUserId(userId.getId())
                .stream()
                .map(ActivityResponse::from)
                .toList();
        return ResponseHelper.success(res);
    }

    // ------- 수상 내역 -------
    @Operation(summary = "수상 내역 등록", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/award")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> upsertAward(
            @CurrentUser UserId userId,
            @Valid @RequestBody AwardUpsertRequest req
    ) {
        jobService.saveAward(new JobAwardEntry(
                null,
                userId.getId(),
                req.awardName(),
                req.organization(),
                req.awardedOn(),
                req.description()
        ));
        return ResponseHelper.successOnly();
    }

    @Operation(summary = "수상 내역 목록 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/awards")
    public ResponseEntity<HttpResponse<List<AwardResponse>>> getAwards(
            @CurrentUser UserId userId
    ) {
        var res = jobService.findAwardsByUserId(userId.getId())
                .stream()
                .map(AwardResponse::from)
                .toList();
        return ResponseHelper.success(res);
    }

    @Operation(summary = "AI 추천 대외활동 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/recommended-activities")
    public ResponseEntity<HttpResponse<List<RecommendedActivityResponse>>> getRecommendedActivities(
            @CurrentUser UserId userId
    ) {
        var recommendations = jobAIRecommendationService.generateRecommendedActivities(userId.getId());
        return ResponseHelper.success(recommendations);
    }

    @Operation(summary = "AI 맞춤형 취업 공고 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/recommended-jobs")
    public ResponseEntity<HttpResponse<List<RecommendedJobResponse>>> getRecommendedJobs(
            @CurrentUser UserId userId
    ) {
        var recommendations = jobAIRecommendationService.generateRecommendedJobs(userId.getId());
        return ResponseHelper.success(recommendations);
    }
}