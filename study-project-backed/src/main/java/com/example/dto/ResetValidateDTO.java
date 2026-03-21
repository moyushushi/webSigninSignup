package com.example.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

//重置密码验证 DTO
@Data
public class ResetValidateDTO {
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    private String email;

    @Length(min = 6, max = 6, message = "验证码必须为6位")
    private String code;
}