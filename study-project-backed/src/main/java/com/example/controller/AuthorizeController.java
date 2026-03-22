package com.example.controller;


import com.example.dto.*;
import com.example.entity.RestBean;
import com.example.entity.user.AccountUser;
import com.example.mapper.UserMapper;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RequestMapping("/")
@RestController
public class AuthorizeController {
    private final String USERNAME="^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";

    @Resource
    AuthorizeService service;

    /**
     * 注册邮箱验证（JSON 请求体接收）
     */
    @PostMapping("/vali-register-email")
    public RestBean<String> validateRegisterEmail(@Validated @RequestBody EmailValidateDTO dto,
                                                  HttpSession session) {
        String s = service.sendValidateEmail(dto.getEmail(), session.getId(), false);
        if (s == null) {
            return RestBean.success("邮件已发送");
        } else {
            return RestBean.failure(400, s);
        }
    }

    /**
     * 重置密码邮箱验证（JSON 请求体接收）
     */
    @PostMapping("/vali-reset-email")
    public RestBean<String> validateResetEmail(@Validated @RequestBody EmailValidateDTO dto,
                                               HttpSession session) {
        String s = service.sendValidateEmail(dto.getEmail(), session.getId(), true);
        if (s == null) {
            return RestBean.success("邮件已发送");
        } else {
            return RestBean.failure(400, s);
        }
    }

    /**
     * 用户注册（JSON 请求体接收）
     */
    @PostMapping("/register")
    public RestBean<String> registerUser(@Validated @RequestBody RegisterDTO dto,
                                         HttpSession session) {
        String s = service.validateAndRegister(
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail(),
                dto.getCode(),
                session.getId()
        );
        if (s == null) {
            return RestBean.success("注册成功");
        } else {
            return RestBean.failure(400, s);
        }
    }

    /**
     * 开始重置密码（验证邮箱+验证码）
     */
    @PostMapping("/start-reset")
    public RestBean<String> startReset(@Validated @RequestBody ResetValidateDTO dto,
                                       HttpSession session) {
        String s = service.validateOnly(dto.getEmail(), dto.getCode(), session.getId());
        if (s == null) {
            session.setAttribute("reset-password", dto.getEmail());
            session.setMaxInactiveInterval(300);
            return RestBean.success();
        } else {
            return RestBean.failure(400, s);
        }
    }

    /**
     * 执行密码重置
     */
    @PostMapping("/do-password")
    public RestBean<String> resetPassword(@Validated @RequestBody ResetPasswordDTO dto,
                                          HttpSession session) {
        String email = (String) session.getAttribute("reset-password");
        if (email == null) {
            return RestBean.failure(401, "请先完成邮箱验证");
        } else if (service.resetPassword(email, dto.getPassword())) {
            session.removeAttribute("reset-password");
            return RestBean.success("密码重置成功");
        } else {
            return RestBean.failure(500, "内部错误");
        }
    }

}

