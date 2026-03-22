package com.example.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

//重置密码执行 DTO
@Data
public class ResetPasswordDTO {
    @Length(min = 3, max = 14, message = "密码长度需在3-14位之间")
    private String password;
}