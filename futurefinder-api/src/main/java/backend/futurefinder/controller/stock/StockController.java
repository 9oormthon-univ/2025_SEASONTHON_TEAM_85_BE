package backend.futurefinder.controller.stock;

import backend.futurefinder.dto.response.stock.StockMoversResponse;
import backend.futurefinder.model.stock.StockItem;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.response.HttpResponse;
import backend.futurefinder.service.stock.StockService;
import backend.futurefinder.util.helper.ResponseHelper;
import backend.futurefinder.util.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    // 파라미터 없음
    @Operation(
            summary = "급등락 종목(일일)",
            description = "당일 기준 급등/급락 상위 종목을 조회한다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": {
                                    "stocks": [
                                      { "name": "NAVER",   "price": 200000.00, "change": -5000.00, "changePct": -2.44 }
                                      { "name": "삼성전자", "price": 72100.00, "change": 1100.00, "changePct": 1.55 },
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
    @GetMapping("/movers")
    public ResponseEntity<HttpResponse<StockMoversResponse>> getMovers(
            @CurrentUser UserId userId
    ) {
        List<StockItem> movers = stockService.getDailyMovers();
        return ResponseHelper.success(StockMoversResponse.of(movers));
    }

}