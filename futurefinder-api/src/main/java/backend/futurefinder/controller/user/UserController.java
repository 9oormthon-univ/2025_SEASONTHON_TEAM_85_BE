package backend.futurefinder.controller.user;


import backend.futurefinder.dto.request.user.UserRequest;
import backend.futurefinder.dto.response.auth.AccountIdResponse;
import backend.futurefinder.dto.response.auth.TokenResponse;
import backend.futurefinder.dto.response.user.UserResponse;
import backend.futurefinder.model.media.FileCategory;
import backend.futurefinder.model.media.FileData;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;
import backend.futurefinder.response.HttpResponse;
import backend.futurefinder.response.SuccessOnlyResponse;
import backend.futurefinder.service.user.UserService;
import backend.futurefinder.util.helper.FileHelper;
import backend.futurefinder.util.helper.ResponseHelper;
import backend.futurefinder.util.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @Operation(
            summary = "프로필 이미지 변경",
            description = "사용자의 프로필 이미지를 업로드하여 변경한다. multipart/form-data로 파일을 전송한다.",
            security = { @SecurityRequirement(name = "bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "success", value = """
            {
              "status": 200,
              "data": { "message": "성공" }
            }
            """)
                    )),
                    @ApiResponse(responseCode = "500", description = "서버 오류(파일 이름 없음 등)", content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "no-filename", value = """
            {
              "status": 500,
              "data": { "errorCode": "COMMON_4", "message": "Internal Server Error" }
            }
            """)
                    )),
                    @ApiResponse(responseCode = "409", description = "파일 처리 실패", content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "file-convert-fail", value = """
                {
                  "status": 409,
                  "data": { "errorCode": "FILE_3", "message": "파일 변환에 실패하였습니다." }
                }
                """),
                                    @ExampleObject(name = "file-store-fail", value = """
                {
                  "status": 409,
                  "data": { "errorCode": "FILE_1", "message": "파일 업로드를 실패하였습니다." }
                }
                """),
                                    @ExampleObject(name = "invalid-name", value = """
                {
                  "status": 409,
                  "data": { "errorCode": "FILE_6", "message": "파일 이름이 잘못되었습니다." }
                }
                """)
                            }
                    ))
            }
    )
    @PostMapping(value = "/image", consumes = {"multipart/form-data"})
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> changeUserImage(
            @RequestPart("file")
            @Parameter(
                    description = "업로드할 프로필 이미지 파일",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            MultipartFile file,

            @CurrentUser UserId userId
    ) throws IOException {
        FileData convertedFile = FileHelper.convertMultipartFileToFileData(file);
        userService.updateFile(convertedFile, userId, FileCategory.PROFILE);
        return ResponseHelper.successOnly();
    }






    @Operation(
            summary = "닉네임 변경",
            description = "로그인된 사용자의 닉네임을 변경한다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "닉네임 변경 요청 바디",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRequest.UpdateNickName.class),
                            examples = @ExampleObject(name = "request", value = """
                        {
                          "nickName": "새로운닉네임"
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
                                  "data": { "message": "성공" }
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
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "리소스 충돌",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "nickname_conflict", value = """
                                {
                                  "status": 409,
                                  "data": {
                                    "errorCode": "USER_6",
                                    "message": "이미 존재하는 유저 닉네임입니다."
                                  }
                                }
                                """)
                            )
                    )
            }
    )
    @PutMapping("/status/nickname")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> changeNickName(
            @CurrentUser UserId userId,
            @RequestBody UserRequest.UpdateNickName nickName
    ) {
        userService.updateNickName(userId, nickName.toNickName());
        return ResponseHelper.successOnly();
    }




    @Operation(
            summary = "회원 탈퇴",
            description = "로그인된 사용자의 계정을 삭제한다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
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
            }
    )
    @DeleteMapping("") // 혹은 @DeleteMapping("/") 또는 @DeleteMapping
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> deleteUser(
            @CurrentUser UserId userId
    ) {
        userService.deleteUser(userId);
        return ResponseHelper.successOnly();
    }



    @Operation(
            summary = "아이디 찾기",
            description = "닉네임으로 accountId를 조회한다.",
            security = { @SecurityRequirement(name = "") }, // 🔒 Access 토큰 필요
            parameters = {
                    @Parameter(name = "nickName", description = "닉네임", required = true, example = "길동이")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountIdResponse.class),
                            examples = @ExampleObject(name = "success", value = """
            {
              "status": 200,
              "data": { "accountId": "yhkim0525" }
            }
            """)
                    )),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "unauthorized", value = """
            {
              "status": 401,
              "data": { "errorCode": "AUTH_4", "message": "토큰을 확인해주세요" }
            }
            """)
                    )),
                    @ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "user-not-found", value = """
            {
              "status": 404,
              "data": { "errorCode": "USER_1", "message": "회원을 찾을 수 없음." }
            }
            """)
                    ))
            }
    )
    @GetMapping("/account-id")
    public ResponseEntity<HttpResponse<AccountIdResponse>> findAccountIdByNickName(
            @RequestParam String nickName
    ) {
        String accountId = userService.findAccountIdByNickName(nickName, AccessStatus.ACCESS);
        return ResponseHelper.success(AccountIdResponse.of(accountId));
    }




    @Operation(
            summary = "프로필 조회",
            description = "로그인된 사용자의 프로필 정보를 조회한다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = @ExampleObject(name = "success", value = """
                                {
                                  "status": 200,
                                  "data": {
                                    "username": "홍길동",
                                    "nickname": "길동이",
                                    "email": "gildong@example.com",
                                    "phoneNumber": "010-1234-5678",
                                    "birth": "1995-05-25",
                                    "imageUrl": "https://cdn.example.com/profile/abc.jpg"
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
                    // 필요 시 404(회원정보 없음)도 추가 가능
            }
    )
    @GetMapping("/profile")
    public ResponseEntity<HttpResponse<UserResponse>> getUserProfile(
            @CurrentUser UserId userId
    ){
        UserInfo userInfo = userService.getUserProfile(userId);
        return ResponseHelper.success(UserResponse.of(userInfo));

    }


    @Operation(
            summary = "프로필 수정",
            description = "사용자의 이름/이메일/전화번호/생일을 수정한다.",
            security = { @SecurityRequirement(name = "bearerAuth") }, // 🔒 Access 토큰 필요
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
    @PutMapping("/profile")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> updateUserProfile(
            @CurrentUser UserId userId,
            @RequestBody UserRequest.UpdateProfile request
    ){
        userService.changeUserProfile(userId, request.userName(), request.email(), request.phoneNumber(), request.birth());
        return ResponseHelper.successOnly();
    }





}
