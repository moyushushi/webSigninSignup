package com.example.controller;


import com.example.entity.RestBean;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/")
@RestController
public class AuthorizeController {
    private final String USERNAME="^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";

    @Resource
    AuthorizeService service;

    @PostMapping("/vali-email")
    public RestBean<String> validateEmail(@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
                                              @RequestParam("email")  String email, HttpSession session) {
        String s= service.sendValidateEmail(email,session.getId());
        if (s==null){
            return RestBean.success("邮件已发送");
        }else
            return RestBean.failure(400,s);
    }

    @PostMapping("/register")
    public RestBean<String> registerUser(@Pattern(regexp =USERNAME)@Length(min=3,max=14) @RequestParam("username") String username,
                                         @Length(min=3,max=14) @RequestParam("password") String password,
                                         @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")@RequestParam("email") String email,
                                         @Length(min=6,max=6) @RequestParam("code")  String code,
                                         HttpSession session) {
        String s=service.validateAndRegister(username,password,email,code,session.getId());
        if(s==null){
            return RestBean.success("注册成功");
        }else{
            return RestBean.failure(400,s);
        }
    }



}

