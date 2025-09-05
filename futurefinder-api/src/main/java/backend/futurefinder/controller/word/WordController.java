package backend.futurefinder.controller.word;


import backend.futurefinder.dto.request.word.ScrapRequest;
import backend.futurefinder.dto.response.word.PopularWordListResponse;
import backend.futurefinder.dto.response.word.ScrapWordListResponse;
import backend.futurefinder.dto.response.word.WordMeaningResponse;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.word.PopularWord;
import backend.futurefinder.model.word.ScrapWord;
import backend.futurefinder.model.word.Wordinfo;
import backend.futurefinder.response.HttpResponse;
import backend.futurefinder.response.SuccessCreateResponse;
import backend.futurefinder.service.word.WordService;
import backend.futurefinder.util.helper.ResponseHelper;
import backend.futurefinder.util.security.CurrentUser;
import com.amazonaws.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dictionary")
public class WordController {

    private final WordService wordService;


    @Operation(
            summary = "경제 용어 검색",
            description = "질의어 `q`로 경제/금융 용어의 의미를 조회한다.",     // GET /api/dictionary/search?q=인플레이션
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordMeaningResponse.class),
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": {
                                    "term": "인플레이션",
                                    "meaning": "일정 기간 동안 전반적인 물가 수준이 지속적으로 상승하는 현상"
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
                            responseCode = "404",
                            description = "단어를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "word-not-found", value = """
                                {
                                  "status": 404,
                                  "data": {
                                    "errorCode": "WORD_1",
                                    "message": "해당 단어를 찾을 수 없음."
                                  }
                                }
                                """)
                            )
                    )
            }
    )
    @GetMapping("/search")
    public ResponseEntity<HttpResponse<WordMeaningResponse>> wordSearch(
            @RequestParam("q") String term,
            @CurrentUser UserId userId
    ) {
        Wordinfo res = wordService.search(term);
        return ResponseHelper.success(WordMeaningResponse.of(res.getWord(), res.getMeaning()));
    }



    @Operation(
            summary = "단어 스크랩 생성",
            description = "사용자의 스크랩 목록에 단어와 의미를 추가한다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "스크랩할 단어와 의미",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ScrapRequest.getWord.class),
                            examples = @ExampleObject(name = "request", value = """
                        {
                          "word": "인플레이션",
                          "meaning": "일정 기간 동안 전반적인 물가 수준이 상승하는 현상"
                        }
                        """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "생성 완료",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessCreateResponse.class),
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": { "message": "생성 완료" }
                                }
                                """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "이미 스크랩된 단어",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "already-exists", value = """
                                {
                                  "status": 409,
                                  "data": {
                                    "errorCode": "WORD_2",
                                    "message": "스크랩된 단어가 이미 존재합니다."
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
    @PostMapping("/scrap")
    public ResponseEntity<HttpResponse<SuccessCreateResponse>> wordScrap(
            @CurrentUser UserId userId,
            @RequestBody ScrapRequest.getWord request
    ){
        wordService.scrap(userId, request.toWord(), request.toMeaning());
        return ResponseHelper.successCreateOnly();

    }


    @Operation(
            summary = "스크랩 단어 목록 조회",
            description = "로그인된 사용자의 스크랩 단어 목록을 페이지 기준으로 조회한다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ScrapWordListResponse.class),
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": {
                                    "ScrapWords": [
                                      { "wordId": "w1", "wordName": "인플레이션", "meaning": "물가가 전반적으로 상승하는 현상" },
                                      { "wordId": "w2", "wordName": "디플레이션", "meaning": "물가가 전반적으로 하락하는 현상" }
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
    @GetMapping("/scrap/list")
    public ResponseEntity<HttpResponse<ScrapWordListResponse>> getWordList(
            @CurrentUser UserId userId,
            @RequestParam(defaultValue = "0") int page
    ){
        List<ScrapWord> word = wordService.getWordList(userId, page);
        return ResponseHelper.success(ScrapWordListResponse.of(word));
    }


    @Operation(
            summary = "인기 단어 목록 조회",
            description = "시스템에서 집계한 인기 단어 목록을 조회한다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PopularWordListResponse.class),
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": {
                                    "PopularWords": [
                                      { "wordName": "인플레이션" },
                                      { "wordName": "디플레이션" },
                                      { "wordName": "스태그플레이션" }
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

            }
    )
    @GetMapping("/popular/list")
    public ResponseEntity<HttpResponse<PopularWordListResponse>> getPopularWordList(
            @CurrentUser UserId userId
    ){
        List<PopularWord> word = wordService.getPopularWordList();
        return ResponseHelper.success(PopularWordListResponse.of(word));
    }


}