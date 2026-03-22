package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginDTO {

    @NotBlank(message = "用户名/邮箱不能为空")
    private String username;   // 与前端 JSON 中的键名一致

    @NotBlank(message = "密码不能为空")
    @Length(min = 3, max = 14, message = "密码长度需在3-14位之间")
    private String password;

    private Boolean remember;  // 可选字段，使用 Boolean 避免基本类型默认值干扰
}