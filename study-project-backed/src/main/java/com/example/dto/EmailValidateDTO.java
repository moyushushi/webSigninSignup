package com.example.dto;


//邮箱验证通用 DTO（RegisterEmailDTO/ResetEmailDTO 可复用）
//
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmailValidateDTO {
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    private String email;
}