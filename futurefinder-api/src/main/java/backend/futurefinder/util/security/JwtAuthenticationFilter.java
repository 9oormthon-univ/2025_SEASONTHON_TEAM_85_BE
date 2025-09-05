package backend.futurefinder.util.security;


import backend.futurefinder.error.AuthorizationException;
import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.model.user.UserId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final RequestMappingHandlerMapping handlerMapping;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = resolveToken(request);
            jwtTokenUtil.validateToken(token);
            UserId userId = jwtTokenUtil.getUserIdFromToken(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.setAttribute("userId", userId);
        } catch (Exception e) {
            request.setAttribute("Exception", e);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/create/account") ||
                path.startsWith("/api/user/account-id") ||
                path.startsWith("/api/auth/find/password") ||
                path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/logout") ||
                path.startsWith("/api/auth/kakao") ||
                path.startsWith("/docs") ||
                path.startsWith("/health") ||

                //
                path.equals("/swagger-ui.html") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");


    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return jwtTokenUtil.cleanedToken(bearerToken);
        }
        throw new AuthorizationException(ErrorCode.INVALID_TOKEN);
    }
}