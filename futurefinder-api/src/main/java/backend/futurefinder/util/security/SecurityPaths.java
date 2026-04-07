package backend.futurefinder.util.security;

public final class SecurityPaths {

    private SecurityPaths() {}

    public static final String[] PUBLIC_PATHS = {
            "/api/auth/create/account",
            "/api/auth/login",
            "/api/auth/logout",
            "/api/user/account-id",
            "/api/auth/find/password",
            "/api/auth/kakao",
            "/docs/**",
            "/health",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml"
    };
}
