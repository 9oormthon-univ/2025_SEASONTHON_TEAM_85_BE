package backend.futurefinder.controller.auth;

import backend.futurefinder.dto.request.auth.LoginRequest;
import backend.futurefinder.dto.request.auth.LogoutRequest;
import backend.futurefinder.dto.request.auth.OAuthLoginRequest;
import backend.futurefinder.dto.request.auth.SignUpRequest;
import backend.futurefinder.dto.request.user.UserRequest;
import backend.futurefinder.dto.response.auth.TokenResponse;
import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.facade.auth.AccountFacade;
import backend.futurefinder.model.auth.JwtToken;
import backend.futurefinder.model.notification.PushInfo;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;
import backend.futurefinder.response.ErrorResponse;
import backend.futurefinder.response.HttpResponse;
import backend.futurefinder.response.SuccessCreateResponse;
import backend.futurefinder.response.SuccessOnlyResponse;
import backend.futurefinder.service.auth.AuthService;
import backend.futurefinder.util.helper.ResponseHelper;
import backend.futurefinder.util.security.CurrentUser;
import backend.futurefinder.util.security.JwtTokenUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AccountFacade accountFacade;
    private final JwtTokenUtil jwtTokenUtil;

    @Operation(
            summary = "회원가입",
            description = "계정을 생성하고 JWT(access/refresh)를 발급한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class),
                            examples = @ExampleObject(name = "success",
                                    value = """
                    {
                      "status": 200,
                      "data": {
                        "accessToken": "accessToken",
                        "refreshToken": "refreshToken"
                      }
                    }
                    """)
                    )),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "잘못된 요청",
                                    value = """
                    {
                       "status": 400,
                       "error": {
                          "errorCode": "COMMON_2",
                          "message": "요청 변수가 잘못되었습니다."
                       }
                    }
                    """)
                    )),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는 계정", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "충돌",
                                    value = """
                    {
                       "status": 409,
                       "error": {
                          "errorCode" : "USER_3",
                          "message" : "이미 가입된 사용자입니다."
                       }
                    }
                    """)
                    ))
            }
    )
    // 전역 보안 적용되어 있더라도, 회원가입은 인증 불필요 → 보안 해제
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "")
    @PostMapping("/create/account")
    public ResponseEntity<HttpResponse<TokenResponse>> signUp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "회원가입 요청 바디",
                    content = @Content(
                            schema = @Schema(implementation = SignUpRequest.Phone.class),
                            examples = @ExampleObject(name = "request",
                                    value = """
                        {
                          "accountId": "yhkim0525",
                          "userName": "홍길동",
                          "nickName": "길동이",
                          "deviceId": "device-abc-123",
                          "provider": "ios",
                          "appToken": "testpushtoken"
                        }
                        """)
                    )
            )
            @RequestBody SignUpRequest.Phone request
    ) {
        UserId userId = accountFacade.createUser(
                request.toAccountId(),
                request.toUserName(),
                request.toNickName(),
                request.toAppToken(),
                request.toDevice()
        );
        JwtToken jwtToken = jwtTokenUtil.createJwtToken(userId);
        authService.createLoginInfo(userId, jwtToken.getRefreshToken());
        return ResponseHelper.success(TokenResponse.of(jwtToken));
    }




    @Operation(
            summary = "비밀번호 생성",
            description = "회원가입 완료 후 최초 비밀번호를 생성한다.",
            security = { @SecurityRequirement(name = "bearerAuth") },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "비밀번호 생성 요청 바디",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignUpRequest.Password.class),
                            examples = @ExampleObject(name = "request", value = """
                        {
                          "password": "testPassword"
                        }
                        """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessCreateResponse.class),
                            examples = @ExampleObject(name = "success", value = """
                        {
                          "status": 200,
                          "data": {
                            "message": "성공"
                          }
                        }
                        """)
                    )),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "요청 변수 잘못됨", value = """
                        {
                           "status": 400,
                           "error": {
                              "errorCode": "COMMON_2",
                              "message": "요청 변수가 잘못되었습니다."
                           }
                        }
                        """)
                    )),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "unauthorized", value = """
                        {
                           "status": 401,
                           "error": {
                              "errorCode": "AUTH_4",
                              "message": "토큰을 확인해주세요"
                           }
                        }
                        """)
                    ))
            }
    )
    @PostMapping("/create/password")
    public ResponseEntity<HttpResponse<SuccessCreateResponse>> makePassword(
            @RequestBody SignUpRequest.Password request,
            @CurrentUser UserId userId
    ) {
        accountFacade.createPassword(userId, request.password());
        return ResponseHelper.successCreateOnly();
    }



    @Operation(
            summary = "로그인",
            description = "아이디/비밀번호로 로그인하고 JWT(access/refresh)를 발급한다.",
            // 전역 보안이 걸려 있어도, 로그인은 인증 불필요 → 빈 SecurityRequirement로 해제
            security = { @SecurityRequirement(name = "") },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "로그인 요청 바디",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "request",
                                    value = """
                                {
                                  "accountId": "yhkim0525",
                                  "password": "testPassword",
                                  "deviceId": "device-abc-123",
                                  "provider": "ios",
                                  "appToken": "push-token-abcdef"
                                }
                                """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenResponse.class),
                                    examples = @ExampleObject(
                                            name = "success",
                                            value = """
                                        {
                                          "status": 200,
                                          "data": {
                                            "accessToken": "accessToken",
                                            "refreshToken": "refreshToken"
                                          }
                                        }
                                        """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비밀번호 불일치",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "wrong-password",
                                            value = """
                                        {
                                          "status" : 401,
                                          "data" : {
                                            "errorCode" : "AUTH_9",
                                            "message" : "비밀번호가 틀렸습니다."
                                          }
                                        }
                                        """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "회원을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "user-not-found",
                                            value = """
                                        {
                                          "status" : 404,
                                          "data" : {
                                            "errorCode" : "USER_1",
                                            "message" : "회원을 찾을 수 없음."
                                          }
                                        }
                                        """
                                    )
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<HttpResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        UserId userId = accountFacade.login(
                request.toAccountId(),
                request.toPassword(),
                request.toDevice(),
                request.toAppToken()
        );
        JwtToken jwtToken = jwtTokenUtil.createJwtToken(userId);
        authService.createLoginInfo(userId, jwtToken.getRefreshToken());
        return ResponseHelper.success(TokenResponse.of(jwtToken));
    }



    @Operation(
            summary = "로그아웃",
            description = """
        기기/플랫폼 정보를 전달하고, Authorization 헤더의 **Bearer 리프레시 토큰**을 검증하여 로그아웃합니다.
        Swagger UI에서 우상단 **Authorize** 버튼을 눌러 `Bearer <refreshToken>` 형식으로 **리프레시 토큰**을 넣은 뒤 실행하세요.
        """,
            security = { @SecurityRequirement(name = "bearerAuth") }, // ← 이게 없으면 Swagger가 헤더를 안 넣음, // 🔒 리프레시 토큰 필요 (UI에서 Authorize 사용)
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "로그아웃 요청 바디",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LogoutRequest.class),
                            examples = @ExampleObject(name = "request", value = """
                        {
                          "deviceId": "testDeviceId",
                          "provider": "ios"
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
                                    schema = @Schema(implementation = SuccessOnlyResponse.class),
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": {
                                    "message": "성공"
                                  }
                                }
                                """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "토큰 검증 실패(리프레시 토큰)",
                            content = @Content(
                                    mediaType = "application/json",
                                    // 프로젝트 표준 에러 스키마가 있으면 여기에 지정하세요 (예: ErrorResponse)
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
    @DeleteMapping("/logout")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> logout(
            @RequestHeader("Authorization") String refreshToken,
            @RequestBody LogoutRequest request
    ) {

        jwtTokenUtil.validateRefreshToken(refreshToken);
        accountFacade.logout(
                request.toDevice(),
                refreshToken
        );
        return ResponseHelper.successOnly();
    }



    @Operation(
            summary = "비밀번호 찾기(토큰 발급)",
            description = "accountId로 사용자를 조회하여 JWT(access/refresh)를 발급한다.",
            security = { @SecurityRequirement(name = "") }, // 🔓 인증 불필요
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "계정 아이디",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRequest.getAccountId.class),
                            examples = @ExampleObject(name = "request", value = """
            {
              "accountId": "yhkim0525"
            }
            """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class),
                            examples = @ExampleObject(name = "success", value = """
            {
              "status": 200,
              "data": { "accessToken": "accessToken", "refreshToken": "refreshToken" }
            }
            """)
                    )),
                    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음", content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "user-not-found", value = """
            {
              "status": 404,
              "data": { "errorCode": "USER_1", "message": "회원을 찾을 수 없음." }
            }
            """)
                    )),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "bad-request", value = """
            {
              "status": 400,
              "data": { "errorCode": "COMMON_2", "message": "요청 변수가 잘못되었습니다." }
            }
            """)
                    ))
            }
    )
    @PostMapping("/find/password") // 비밀번호 찾기용
    public ResponseEntity<HttpResponse<TokenResponse>> findPasswordByAccountId(
            @RequestBody UserRequest.getAccountId request
    ) {
        UserInfo userInfo = accountFacade.getUserByAccountId(request.toAccountId(), AccessStatus.ACCESS);

        JwtToken jwtToken = jwtTokenUtil.createJwtToken(userInfo.getUserId());
        authService.createLoginInfo(userInfo.getUserId(), jwtToken.getRefreshToken());
        return ResponseHelper.success(TokenResponse.of(jwtToken));
    }

    @Operation(
            summary = "비밀번호 변경",
            description = "로그인된 사용자의 비밀번호를 변경한다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "비밀번호 변경 요청 바디",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignUpRequest.Password.class),
                            examples = @ExampleObject(name = "request", value = """
                        {
                          "password": "testPassword"
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
                                    schema = @Schema(implementation = SuccessOnlyResponse.class),
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": {
                                    "message": "성공"
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
                    )
            }
    )
    @PostMapping("/change/password")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> changePassword(
            @RequestBody SignUpRequest.Password request,
            @CurrentUser UserId userId
    ) {
        accountFacade.changePassword(userId, request.password());
        return ResponseHelper.successOnly();
    }

    @Operation(
            summary = "카카오 로그인",
            description = "카카오 액세스 토큰으로 로그인하고 JWT(access/refresh)를 발급한다.",
            security = { @SecurityRequirement(name = "") }, // 🔓 전역 보안 해제 (자체 JWT 불필요)
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenResponse.class),
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": {
                                    "accessToken": "accessToken",
                                    "refreshToken": "refreshToken"
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
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "카카오 토큰 검증 실패",
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
                    )
            }
    )

    // 카카오 로그인
    @PostMapping("/kakao")
    public ResponseEntity<HttpResponse<TokenResponse>> kakaoLogin(
            @Parameter(
                    name = "Authorization",
                    in = ParameterIn.HEADER,
                    description = "카카오 액세스 토큰. 형식: `Bearer {kakaoAccessToken}`",
                    required = true,
                    schema = @Schema(type = "string"),
                    example = "Bearer khbGciOi..."
            )
            @RequestHeader(value = "Authorization", required = false) String authorization, // ← 일단 optional로

            @Parameter(
                    name = "X-Device-Id",
                    in = ParameterIn.HEADER,
                    description = "디바이스 ID",
                    required = true,
                    schema = @Schema(type = "string"),
                    example = "device-abc-123"
            )
            @RequestHeader("X-Device-Id") String deviceId,

            @Parameter(
                    name = "X-Device-Provider",
                    in = ParameterIn.HEADER,
                    description = "디바이스 플랫폼",
                    required = true,
                    schema = @Schema(allowableValues = {"IOS","ANDROID"})
            )
            @RequestHeader("X-Device-Provider") String provider,

            @Parameter(
                    name = "X-App-Token",
                    in = ParameterIn.HEADER,
                    description = "푸시 토큰",
                    required = true,
                    schema = @Schema(type = "string"),
                    example = "push-token-abcdef"
            )
            @RequestHeader("X-App-Token") String appToken
    ) {
        String token = authorization.replaceFirst("(?i)^Bearer\\s+", "").trim();

        PushInfo.Device device = PushInfo.Device.of(
                deviceId,
                PushInfo.Provider.valueOf(provider.toUpperCase())
        );

        UserId userId = accountFacade.loginWithKakao(token, appToken, device);

        JwtToken jwtToken = jwtTokenUtil.createJwtToken(userId);
        authService.createLoginInfo(userId, jwtToken.getRefreshToken());
        return ResponseHelper.success(TokenResponse.of(jwtToken));
    }

    @Operation(
            summary = "JWT 리프레시",
            description = "리프레시 토큰을 이용해 새 JWT(access/refresh)를 발급한다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, //
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    // 래퍼(HttpResponse<TokenResponse>)를 예시로 표현
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": {
                                    "accessToken": "newAccessToken",
                                    "refreshToken": "newRefreshToken"
                                  }
                                }
                                """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "리프레시 토큰 검증 실패/만료",
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
    @GetMapping("/refresh")
    public ResponseEntity<HttpResponse<TokenResponse>> refreshJwtToken(
            @Parameter(
                    name = "Authorization",
                    in = ParameterIn.HEADER,
                    description = "리프레시 토큰. 형식: `Bearer {refreshToken}`",
                    required = true,
                    schema = @Schema(type = "string"),
                    example = "Bearer eyJhbGciOi..."
            )
            @RequestHeader("Authorization") String refreshToken
    ) {
        Pair<JwtToken, UserId> pair = jwtTokenUtil.refresh(refreshToken);
        JwtToken newToken = pair.getLeft();
        UserId userId = pair.getRight();

        String oldToken = jwtTokenUtil.cleanedToken(refreshToken);
        authService.updateLoginInfo(oldToken, newToken.getRefreshToken(), userId);
        return ResponseHelper.success(TokenResponse.of(newToken));
    }




}
