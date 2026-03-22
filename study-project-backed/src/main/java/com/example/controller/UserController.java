package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.user.AccountUser;
import com.example.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/me")
    public RestBean<AccountUser> me(HttpServletRequest request) {
        System.out.println("=== /user/me called ===");
        System.out.println("Session ID from request: " + request.getSession().getId());

        // 检查 session 中的 SecurityContext
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object securityContext = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            System.out.println("SecurityContext in session: " + securityContext);
            System.out.println("Account in session: " + session.getAttribute("account"));
        } else {
            System.out.println("No session found");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication from SecurityContextHolder: " + authentication);

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            return RestBean.failure(401, "未登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return RestBean.success(userDetails.getAccountUser());
    }
}
