package backend.futurefinder.controller.home;

import backend.futurefinder.dto.response.home.HomeResponse;
import backend.futurefinder.facade.home.HomeFacade;
import backend.futurefinder.model.home.HomeBundle;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.response.HttpResponse;
import backend.futurefinder.util.helper.ResponseHelper;
import backend.futurefinder.util.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

    private final HomeFacade homeFacade;

    /**
     * 홈 화면용 집계 API
     * - 최신 경제 뉴스 2개
     * - 주식 changePct 절댓값 Top 3
     * - 채용 공고 3개
     */
    @Operation(
            summary = "홈 집계",
            description = "홈 화면용 집계 데이터(최신 경제 뉴스 2개, 주식 changePct 절댓값 Top 3, 채용 공고 3개)를 조회한다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HomeResponse.class),
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": {
                                    "news": [
                                      {
                                        "title": "미 연준, 금리 동결 시사",
                                        "content": "연준 의장이 추가 긴축 가능성은 낮다고 언급...",
                                        "imageUrl": "https://example.com/img1.jpg",
                                        "publishedAt": "2025-09-05T00:15:00Z",
                                        "press": "연합뉴스"
                                      },
                                      {
                                        "title": "코스피, 반도체 강세에 상승",
                                        "content": "외국인 순매수 지속...",
                                        "imageUrl": null,
                                        "publishedAt": "2025-09-05T00:05:00Z",
                                        "press": "한국경제"
                                      }
                                    ],
                                    "stocks": [
                                      { "name": "삼성전자", "price": 72100.00, "change": 1100.00, "changePct": 1.55 },
                                      { "name": "NAVER",   "price": 200000.00, "change": -5000.00, "changePct": -2.44 },
                                      { "name": "카카오",   "price": 47400.00, "change":  800.00,  "changePct": 1.72 }
                                    ],
                                    "recruitments": [
                                      { "institute": "한국은행", "title": "2025년 상반기 신입 채용", "recruitType": "신입", "hireType": "정규직" },
                                      { "institute": "금융감독원", "title": "청년인턴 채용",        "recruitType": "인턴", "hireType": "계약직" },
                                      { "institute": "산업은행",   "title": "데이터 분석가",        "recruitType": "경력", "hireType": "정규직" }
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
                                  "data": { "errorCode": "AUTH_4", "message": "토큰을 확인해주세요" }
                                }
                                """)
                            )
                    ),

            }
    )
    @GetMapping
    public ResponseEntity<HttpResponse<HomeResponse>> getHome(
            @CurrentUser UserId userId
    ) {
        HomeBundle bundle = homeFacade.getHomeBundle();
        return ResponseHelper.success(HomeResponse.of(bundle));
    }



}