package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.user.AccountUser;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Resource
@RequestMapping("/user")
public class UserController {

    @GetMapping("/me")
    public RestBean<AccountUser> me(){
        return RestBean.success();
    }

}
