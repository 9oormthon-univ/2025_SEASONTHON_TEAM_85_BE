package backend.futurefinder.controller.asset;


import backend.futurefinder.dto.request.asset.AssetRequest;
import backend.futurefinder.dto.response.asset.AssetListResponse;
import backend.futurefinder.dto.response.asset.AssetResponse;
import backend.futurefinder.model.asset.Asset;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.response.HttpResponse;
import backend.futurefinder.response.SuccessCreateResponse;
import backend.futurefinder.response.SuccessOnlyResponse;
import backend.futurefinder.service.asset.AssetService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset")
@RequiredArgsConstructor
public class AssetController {


    private final AssetService assetService;



    @Operation(
            summary = "자산 생성",
            description = "로그인된 사용자의 자산 정보를 생성한다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "자산 생성 요청 바디",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AssetRequest.class),
                            examples = @ExampleObject(name = "request", value = """
                        {
                          "assetType": " SAVINGS",
                          "amountKrw": 1500000.00,
                          "note": "적금이자 비상금(NULL로 비워도 됩니다.)",
                          "bankName": "KakaoBank",
                          "accountNumber": "3333-00-1234567"
                        }
                        """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessCreateResponse.class),
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": { "message": "성공" }
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
    @PostMapping("")
    public ResponseEntity<HttpResponse<SuccessCreateResponse>> createAsset(
            @CurrentUser UserId userId,
            @RequestBody AssetRequest request
    ){
        assetService.createAsset(
                userId,
                request.toType(),
                request.toAmountKrw(),
                request.toNote(),
                request.toBankName(),
                request.toAccountNumber()
        );
        return ResponseHelper.successCreateOnly();

    }






    @Operation(
            summary = "자산 목록 조회",
            description = "로그인된 사용자의 자산 목록을 조회한다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AssetListResponse.class),
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": {
                                    "assets": [
                                      {
                                        "assetType": "SAVINGS",
                                        "amountKrw": 1500000.00,
                                        "assetInformation": "비상금",
                                        "bankName": "카카오뱅크",
                                        "accountNumber": "3333-00-1234567"
                                      },
                                      {
                                        "assetType": "DEPOSIT",
                                        "amountKrw": 3000000.00,
                                        "assetInformation": "월급통장",
                                        "bankName": "국민은행",
                                        "accountNumber": "123-456-7890"
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

            }
    )
    @GetMapping("/list")
    public ResponseEntity<HttpResponse<AssetListResponse>> getAssets(
            @CurrentUser UserId userId
    ){
        List<Asset> assets = assetService.getAssets(userId);
        return ResponseHelper.success(AssetListResponse.of(assets));

    }








}
