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
    private String uid(UserId u) { return u.getId(); }

    // ------- 정보 확인 (통합 조회) -------
    @Operation(summary = "취업 관련 정보 전체 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/info")
    public ResponseEntity<HttpResponse<JobInfoResponse>> getJobInfo(
            @CurrentUser UserId userId
    ) {
        // 학업 정보 목록 조회 (여러 개 가능)
        var educations = jobService.findEducationsByUserId(uid(userId))
                .stream()
                .map(e -> new EducationResponse(
                        e.id(),
                        e.schoolName(),
                        e.major(),
                        e.status(),
                        e.graduationYear()
                ))
                .toList();

        // 대외활동 목록 조회
        var activities = jobService.findActivitiesByUserId(uid(userId))
                .stream()
                .map(a -> new ActivityResponse(
                        a.id(),
                        a.type(),
                        a.title(),
                        a.startedOn(),
                        a.endedOn(),
                        a.memo()
                ))
                .toList();

        // 수상 내역 목록 조회
        var awards = jobService.findAwardsByUserId(uid(userId))
                .stream()
                .map(a -> new AwardResponse(
                        a.id(),
                        a.awardName(),
                        a.organization(),
                        a.awardedOn(),
                        a.description()
                ))
                .toList();

        var response = new JobInfoResponse(educations, activities, awards);
        return ResponseHelper.success(response);
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
                uid(userId),
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
        var edu = jobService.findEducationByUserId(uid(userId))
                .map(e -> new EducationResponse(
                        e.id(),           // getId() → id()
                        e.schoolName(),   // getSchoolName() → schoolName()
                        e.major(),        // getMajor() → major()
                        e.status(),       // getStatus() → status()
                        e.graduationYear() // getGraduationYear() → graduationYear()
                ))
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
                uid(userId),
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
        var res = jobService.findActivitiesByUserId(uid(userId))
                .stream()
                .map(a -> new ActivityResponse(
                        a.id(),        // getId() → id()
                        a.type(),      // getType() → type()
                        a.title(),     // getTitle() → title()
                        a.startedOn(), // getStartedAt() → startedAt()
                        a.endedOn(),   // getEndedAt() → endedAt()
                        a.memo()       // getMemo() → memo()
                ))
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
                uid(userId),
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
        var res = jobService.findAwardsByUserId(uid(userId))
                .stream()
                .map(a -> new AwardResponse(
                        a.id(),
                        a.awardName(),
                        a.organization(),
                        a.awardedOn(),
                        a.description()
                ))
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