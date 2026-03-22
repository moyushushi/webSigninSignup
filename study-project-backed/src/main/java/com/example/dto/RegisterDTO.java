package com.example.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


//注册接口 DTO
@Data
public class RegisterDTO {
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$", message = "用户名仅支持字母、数字、中文")
    @Length(min = 3, max = 14, message = "用户名长度需在3-14位之间")
    private String username;

    @Length(min = 3, max = 14, message = "密码长度需在3-14位之间")
    private String password;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    private String email;

    @Length(min = 6, max = 6, message = "验证码必须为6位")
    private String code;
}
