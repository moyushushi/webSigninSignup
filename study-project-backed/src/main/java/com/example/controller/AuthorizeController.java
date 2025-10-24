package com.example.controller;


import com.example.entity.RestBean;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/")
@RestController
public class AuthorizeController {

    @Resource
    AuthorizeService service;

    @PostMapping("/vali-email")
    public RestBean<String> validateEmail(@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
                                              @RequestParam("email")  String email, HttpSession session) {

        if (service.sendValidateEmail(email,session.getId())){
            return RestBean.success("邮件已发送");
        }else
            return RestBean.failure(400,"邮件发送失败，请联系管理员");
    }


}

