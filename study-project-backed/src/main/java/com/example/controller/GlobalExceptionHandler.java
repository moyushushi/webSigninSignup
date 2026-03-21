package com.example.controller;

import com.example.entity.RestBean;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;

/**
 * 全局异常处理器：统一处理参数校验、请求解析等异常，返回标准化 RestBean 格式
 */
@RestControllerAdvice // 替代 @ControllerAdvice + @ResponseBody，直接返回 JSON
public class GlobalExceptionHandler {

    /**
     * 处理 @RequestBody + @Validated 触发的参数校验异常（JSON 请求体校验失败）
     * 例如：邮箱格式错误、密码长度不够、验证码位数不对等
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestBean<String> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        // 获取具体的字段错误提示（如：邮箱格式不正确）
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMsg = fieldError != null ? fieldError.getDefaultMessage() : "参数格式错误";
        // 返回和业务逻辑一致的 400 + 错误信息
        return RestBean.failure(HttpStatus.BAD_REQUEST.value(), errorMsg);
    }

    /**
     * 处理 @RequestParam/@PathVariable 等参数校验异常（URL 参数校验失败）
     * 若后续有 URL 参数校验，该方法会生效
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public RestBean<String> handleConstraintViolation(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder errorMsg = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            errorMsg.append(violation.getMessage()).append("; ");
        }
        String msg = !errorMsg.isEmpty() ? errorMsg.substring(0, errorMsg.length() - 2) : "参数校验失败";
        return RestBean.failure(HttpStatus.BAD_REQUEST.value(), msg);
    }

    /**
     * 处理表单参数绑定/校验异常（若后续有 form-data 请求，该方法生效）
     */
    @ExceptionHandler(BindException.class)
    public RestBean<String> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMsg = fieldError != null ? fieldError.getDefaultMessage() : "表单参数错误";
        return RestBean.failure(HttpStatus.BAD_REQUEST.value(), errorMsg);
    }

    /**
     * 处理缺少必填参数异常（如：前端未传 @RequestParam 要求的参数）
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public RestBean<String> handleMissingParam(MissingServletRequestParameterException e) {
        String errorMsg = "缺少必填参数：" + e.getParameterName();
        return RestBean.failure(HttpStatus.BAD_REQUEST.value(), errorMsg);
    }

    /**
     * 兜底处理所有未捕获的异常（避免返回 500 错误页面）
     */
    @ExceptionHandler(Exception.class)
    public RestBean<String> handleGlobalException(Exception e) {
        // 生产环境建议打印日志，不返回具体异常信息
        e.printStackTrace();
        return RestBean.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误，请稍后重试");
    }
}
