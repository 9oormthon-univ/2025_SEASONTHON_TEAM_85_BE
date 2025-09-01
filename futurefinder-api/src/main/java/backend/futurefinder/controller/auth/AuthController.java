package backend.futurefinder.controller.auth;

import backend.futurefinder.dto.request.auth.LoginRequest;
import backend.futurefinder.dto.request.auth.LogoutRequest;
import backend.futurefinder.dto.request.auth.SignUpRequest;
import backend.futurefinder.dto.response.auth.TokenResponse;
import backend.futurefinder.facade.auth.AccountFacade;
import backend.futurefinder.model.auth.JwtToken;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.response.HttpResponse;
import backend.futurefinder.response.SuccessCreateResponse;
import backend.futurefinder.response.SuccessOnlyResponse;
import backend.futurefinder.service.auth.AuthService;
import backend.futurefinder.util.helper.ResponseHelper;
import backend.futurefinder.util.security.CurrentUser;
import backend.futurefinder.util.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AccountFacade accountFacade;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/create/account")
    public ResponseEntity<HttpResponse<TokenResponse>> signUp(@RequestBody SignUpRequest.Phone request) {
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

    @PostMapping("/create/password")
    public ResponseEntity<HttpResponse<SuccessCreateResponse>> makePassword(
            @RequestBody SignUpRequest.Password request,
            @CurrentUser UserId userId
    ) {
        accountFacade.createPassword(userId, request.password());
        return ResponseHelper.successCreateOnly();
    }

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




    @PostMapping("/change/password")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> changePassword(
            @RequestBody SignUpRequest.Password request,
            @CurrentUser UserId userId
    ) {
        accountFacade.changePassword(userId, request.password());
        return ResponseHelper.successOnly();
    }



}
