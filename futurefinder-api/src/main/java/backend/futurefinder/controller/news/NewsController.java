package backend.futurefinder.controller.news;

import backend.futurefinder.dto.response.news.NewsItemResponse;
import backend.futurefinder.model.news.NewsItem;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.response.HttpResponse;
import backend.futurefinder.service.news.NewsService;
import backend.futurefinder.util.helper.ResponseHelper;
import backend.futurefinder.util.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /**
     * 실시간 경제 뉴스 (기본 50건)
     * GET /api/news/economy
     */

    @Operation(
            summary = "실시간 경제 뉴스",
            description = "실시간 경제 뉴스를 페이지 기준으로 조회한다. (기본 50건), 페이지로 5개씩 조회하며, TTL을 써서 불필요한 데이터 호출을 줄인다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    // HttpResponse<List<NewsItemResponse>> 스키마는 예시로 표현
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": [
                                    {
                                      "title": "미 연준, 금리 동결 시사",
                                      "content": "연준 의장이 추가 긴축 가능성은 낮다고 언급...",
                                      "imageUrl": "https://example.com/img1.jpg",
                                      "publishedAt": "2025-09-05T00:15:00Z",
                                      "press": "연합뉴스"
                                    },
                                    {
                                      "title": "코스피, 외인 순매수에 상승 마감",
                                      "content": "반도체 강세에 지수 상승폭 확대...",
                                      "imageUrl": null,
                                      "publishedAt": "2025-09-05T00:05:00Z",
                                      "press": "한국경제"
                                    }
                                  ]
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
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "bad-request", value = """
                                {
                                  "status": 400,
                                  "data": { "errorCode": "COMMON_2", "message": "요청 변수가 잘못되었습니다." }
                                }
                                """)
                            )
                    )
            }
    )
    @GetMapping("/economy")
    public ResponseEntity<HttpResponse<List<NewsItemResponse>>> getEconomyNews(
            @CurrentUser UserId userId,
            @RequestParam(defaultValue = "1") int page
    ) {
        List<NewsItem> items = newsService.getEconomyNews(page, 50);
        List<NewsItemResponse> data = items.stream().map(NewsItemResponse::from).toList();
        return ResponseHelper.success(data);
    }


}