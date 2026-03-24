package backend.futurefinder.config;

import backend.futurefinder.util.security.JwtAuthenticationEntryPoint;
import backend.futurefinder.util.security.JwtAuthenticationFilter;
import backend.futurefinder.util.security.SilentAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint entryPoint; // JwtAuthenticationEntryPoint
    private final SilentAccessDeniedHandler silentAccessDeniedHandler; // SilentAccessDeniedHandler

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/create/account",
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/user/account-id",
                                "/api/auth/find/password",
                                "/api/auth/kakao",
                                "/docs/**",
                                "/health",

                                // 🔓 Swagger / OpenAPI
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(silentAccessDeniedHandler)
                )
                .csrf(csrf -> csrf.disable())     // ✅ 새 방식
                .cors(cors -> {
                });                // ✅ 새 방식 (기본 설정 사용 시)

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(
                "http://127.0.0.1:8080",
                "http://t2.mobidic.shop",
                "https://t2.mobidic.shop"   // HTTPS도 함께 허용 권장
        ));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Authorization","Location","Content-Disposition"));
        cfg.setAllowCredentials(true);   // 쿠키/Authorization 전송 허용
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
