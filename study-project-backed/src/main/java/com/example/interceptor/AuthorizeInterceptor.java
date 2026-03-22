package com.example.interceptor;

import com.example.entity.user.AccountUser;

import com.example.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class AuthorizeInterceptor implements HandlerInterceptor {

    @Resource
    UserMapper mapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        // 1. 判断是否已认证（排除匿名访问）
        if (authentication == null || !authentication.isAuthenticated()) {
            return true; // 匿名访问直接放行，不处理session
        }

        Object principal = authentication.getPrincipal();
        // 2. 校验principal类型，仅处理User对象（排除anonymousUser字符串）
        if (!(principal instanceof User)) {
            return true;
        }

        // 3. 已认证且类型正确，获取用户信息
        User user = (User) principal;
        String username = user.getUsername();
        AccountUser account = mapper.findAccountUserByNameOrEmail(username);
        if (account != null) { // 增加空值判断，避免空指针
            request.getSession().setAttribute("account", account);
        }
        return true;
    }

}
