package backend.futurefinder.controller.recruit;

import backend.futurefinder.dto.response.recruit.RecruitmentListResponse;
import backend.futurefinder.model.recruit.RecruitmentItem;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.response.HttpResponse;
import backend.futurefinder.service.recruit.RecruitmentService;
import backend.futurefinder.util.helper.ResponseHelper;
import backend.futurefinder.util.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    // GET /api/recruitment/list?ongoingYn=Y&pageNo=1&numOfRows=10&recrutPbancTtl=보훈 ...
    @Operation(
            summary = "채용 공고 목록 조회",
            description = "페이지 번호를 기준으로 채용 공고 목록을 조회한다. 5개씩 페이징하며, 또한 TTL을 써서 불필요한 데이터 호출을 줄인다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RecruitmentListResponse.class),
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": {
                                    "recruitmentResponseList": [
                                      {
                                        "institute": "한국은행",
                                        "title": "2025년 상반기 신입 채용",
                                        "recruitType": "신입",
                                        "hireType": "정규직"
                                      },
                                      {
                                        "institute": "금융감독원",
                                        "title": "청년인턴 채용",
                                        "recruitType": "인턴",
                                        "hireType": "계약직"
                                      }
                                    ]
                                  }
                                }
                                """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "unauthorized", value = """
                                {
                                  "status": 401,
                                  "data": {
                                    "errorCode": "AUTH_4",
                                    "message": "토큰을 확인해주세요"
                                  }
                                }
                                """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "bad-request", value = """
                                {
                                  "status": 400,
                                  "data": {
                                    "errorCode": "COMMON_2",
                                    "message": "요청 변수가 잘못되었습니다."
                                  }
                                }
                                """)
                            )
                    )
            }
    )
    @GetMapping("/list")
    public ResponseEntity<HttpResponse<RecruitmentListResponse>> getRecruitList(
            @CurrentUser UserId userId,
            @Parameter(
                    description = "페이지 번호(1부터 시작)",
                    example = "1",
                    schema = @Schema(minimum = "1", defaultValue = "1")
            )
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        List<RecruitmentItem> items = recruitmentService.getRecruitmentList(page);
        return ResponseHelper.success(RecruitmentListResponse.of(items));
    }


}