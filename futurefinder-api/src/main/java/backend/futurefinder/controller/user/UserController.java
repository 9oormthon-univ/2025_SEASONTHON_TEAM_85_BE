package backend.futurefinder.controller.user;


import backend.futurefinder.dto.request.user.UserRequest;
import backend.futurefinder.dto.response.auth.AccountIdResponse;
import backend.futurefinder.model.media.FileCategory;
import backend.futurefinder.model.media.FileData;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.response.HttpResponse;
import backend.futurefinder.response.SuccessOnlyResponse;
import backend.futurefinder.service.user.UserService;
import backend.futurefinder.util.helper.FileHelper;
import backend.futurefinder.util.helper.ResponseHelper;
import backend.futurefinder.util.security.CurrentUser;
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


    @PostMapping(value = "/image", consumes = {"multipart/form-data"})
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> changeUserImage(
            @RequestPart("file") MultipartFile file,
            @CurrentUser UserId userId
    ) throws IOException {
        FileData convertedFile = FileHelper.convertMultipartFileToFileData(file);
        userService.updateFile(convertedFile, userId, FileCategory.PROFILE);
        return ResponseHelper.successOnly();
    }

    @PutMapping("/status/nickname")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> changeNickName(
            @CurrentUser UserId userId,
            @RequestBody UserRequest.UpdateNickName nickName
    ) {
        userService.updateNickName(userId, nickName.toNickName());
        return ResponseHelper.successOnly();
    }


    @DeleteMapping("") // 혹은 @DeleteMapping("/") 또는 @DeleteMapping
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> deleteUser(
            @CurrentUser UserId userId
    ) {
        userService.deleteUser(userId);
        return ResponseHelper.successOnly();
    }

    @GetMapping("/account-id") // 아이디 찾기용
    public ResponseEntity<HttpResponse<AccountIdResponse>> findAccountIdByNickName(
            @RequestBody UserRequest.getNickName request
    ) {
        String accountId = userService.findAccountIdByNickName(request.toNickName(), AccessStatus.ACCESS);
        return ResponseHelper.success(AccountIdResponse.of(accountId));
    }

    @GetMapping("/password") // 비밀번호 찾기용
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> findPasswordByAccountId(
            @RequestBody UserRequest.getAccountId request
    ) {
        userService.getUserByAccountId(request.toAccountId(), AccessStatus.ACCESS);
        return ResponseHelper.successOnly();
    }



}
