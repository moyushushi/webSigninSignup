package com.example.config;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.RestBean;
import com.example.entity.user.AccountUser;
import com.example.filter.JsonLoginFilter;
import com.example.security.CustomUserDetails;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Resource
    AuthorizeService authorizeService;

    @Resource
    DataSource dataSource;
    // ========== 从 yaml 读取 CORS 配置 ==========
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

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



    @Bean
    public SecurityFilterChain FilterChain(HttpSecurity http, PersistentTokenRepository repository,
                                           AuthenticationManager authenticationManager) throws Exception {
        JsonLoginFilter jsonLoginFilter = new JsonLoginFilter();
        jsonLoginFilter.setFilterProcessesUrl("/login");
        jsonLoginFilter.setAuthenticationSuccessHandler(this::onAuthenticationSuccess);
        jsonLoginFilter.setAuthenticationFailureHandler(this::onAuthenticationFailure);
        jsonLoginFilter.setAuthenticationManager(authenticationManager);
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login","/register","/vali-register-email","/vali-reset-email","/start-reset","/do-password").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .logout(logout -> logout
                    .logoutUrl("/my/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("web_test")
                    .permitAll())
                .rememberMe(rememberMe->rememberMe
                        .rememberMeParameter("remember")
                        .tokenRepository(repository)
                        .tokenValiditySeconds(3600*24*7))
                .userDetailsService(authorizeService)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAt(jsonLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors->cors.configurationSource(this.corsConfigurationSource()))
                .exceptionHandling(e ->e
                        .authenticationEntryPoint(this::onAuthenticationFailure));
        return http.build();
    }
    @Bean
    public PersistentTokenRepository tokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        tokenRepository.setCreateTableOnStartup(false);
        return tokenRepository;
    }
    // ========== 读取 yaml 配置生成 CORS 规则 ==========
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        // 分割多域名（若yaml配置多个，用逗号分隔）
        // 空值校验：避免分割空字符串导致的无效配置
        if (StringUtils.hasText(allowedOrigins)) {
            cors.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        }
        // 其他配置增加空值校验
        if (StringUtils.hasText(allowedMethods)) {
            cors.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        }
        if (StringUtils.hasText(allowedHeaders)) {
            cors.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        }
        if (StringUtils.hasText(exposeHeaders)) {
            cors.setExposedHeaders(Arrays.asList(exposeHeaders.split(",")));
        }
        cors.setAllowCredentials(allowCredentials);
        cors.setMaxAge(maxAge);
        /**
         *
         */
        System.out.println("CORS allowed origins: " + allowedOrigins);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
    private void onLogoutSuccess(HttpServletRequest req,
                                 HttpServletResponse res,
                                 Authentication auth) throws IOException {
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");
        res.getWriter().write(JSONObject.toJSONString(RestBean.success("退出登录")));
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        AccountUser account = userDetails.getAccountUser();
        HttpSession session = request.getSession();
        session.setAttribute("account", account);
        System.out.println("=== Authentication Success ===");
        System.out.println("Session ID: " + session.getId());
        System.out.println("Account set: " + account);
        response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功")));
    }
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        // 如果是登录接口认证失败（用户名或密码错误），返回 401
        if (request.getRequestURI().endsWith("/login")) {
            response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, "用户名或密码错误")));
            return;
        }

        // 其他需要认证的接口认证失败，返回 401 提示先登录
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, "请先登录后再操作")));
    }
}
