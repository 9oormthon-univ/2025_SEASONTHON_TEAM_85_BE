package backend.futurefinder.util.security;


import backend.futurefinder.error.AuthorizationException;
import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.model.user.UserId;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserId)) {
            throw new AuthorizationException(ErrorCode.NOT_AUTHORIZED);
        }

        return authentication.getPrincipal(); // ✅ 그대로 UserId 반환
    }
}