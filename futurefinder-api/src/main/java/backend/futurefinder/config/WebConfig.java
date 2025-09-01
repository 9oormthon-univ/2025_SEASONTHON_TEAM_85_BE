package backend.futurefinder.config;

import backend.futurefinder.util.converter.StringToFileCategoryConverter;
import backend.futurefinder.util.security.UserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UserArgumentResolver userArgumentResolver;

    public WebConfig(UserArgumentResolver userArgumentResolver) {
        this.userArgumentResolver = userArgumentResolver;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToFileCategoryConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/docs/**")
                .addResourceLocations("classpath:/static/docs/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        System.out.println(">> UserArgumentResolver registered");
        resolvers.add(userArgumentResolver); // ✅ 반드시 빈으로 주입된 resolver 사용
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // API 경로를 대상으로
                .allowedOrigins("http://localhost:5173")  // 허용할 출처 (프론트엔드 주소) -> 나중에 변동가능
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);  // 쿠키, 인증 헤더 허용 시 필요
    }

}