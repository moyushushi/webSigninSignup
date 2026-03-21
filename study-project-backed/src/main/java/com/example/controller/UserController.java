package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.user.AccountUser;
import com.example.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/me")
    public RestBean<AccountUser> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return RestBean.failure(401, "未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            AccountUser account = userDetails.getAccountUser();
            return RestBean.success(account);
        }
        return RestBean.failure(401, "未登录");
    }
}
