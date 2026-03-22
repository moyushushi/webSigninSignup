package com.example.config;

import com.example.interceptor.AuthorizeInterceptor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Resource
    AuthorizeInterceptor interceptor;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods}")
    private String allowedMethods;

    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${cors.expose-headers}")
    private String exposeHeaders;

    @Value("${cors.allow-credentials}")
    private boolean allowCredentials;

    @Value("${cors.max-age}")
    private long maxAge;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(interceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/register",
                        "/vali-register-email",
                        "/vali-reset-email",
                        "/start-reset",
                        "/do-password",
                        "/user/me",
                        "/error" // 排除错误页面
                );
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("=== WebConfiguration CORS ===");
        System.out.println("allowedOrigins: " + allowedOrigins);
        System.out.println("allowCredentials: " + allowCredentials);
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.split(",")) // 读取 yaml 配置
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders(allowedHeaders.split(","))
                .exposedHeaders(exposeHeaders.split(","))
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }

}
