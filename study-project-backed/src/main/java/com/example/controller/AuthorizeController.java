package com.example.controller;


import com.example.entity.RestBean;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/")
@RestController
public class AuthorizeController {

    @Resource
    AuthorizeService service;

    @PostMapping("/vali-email")
    public RestBean<String> validateEmail(@RequestParam("email")  String email) {
        if (service.sendValidateEmail(email)){
            return RestBean.success("邮件已发送");
        }else
            return RestBean.failure(400,"邮件发送失败，请联系管理员");
    }

}

