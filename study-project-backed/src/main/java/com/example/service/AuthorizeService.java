package com.example.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {
    // 核心修复：resetPassword 参数顺序改为 email → password，和实现类匹配
    String sendValidateEmail(String email, String sessionId, boolean hasAccount);
    String validateAndRegister(String username, String password, String email, String code, String sessionId);
    String validateOnly(String email, String code, String sessionId);
    boolean resetPassword(String email, String password); // 修复参数顺序

    boolean authenticate(String username, String password);
}
