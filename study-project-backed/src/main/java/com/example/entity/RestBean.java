package com.example.entity;

import lombok.Data;

@Data
public class RestBean<T> {
    private int status;
    private boolean success;
    private String message;
    private T data;

    public RestBean(int status, boolean success, String message, T data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> RestBean<T> success() {
        return new RestBean<>(200, true, null, null);
    }

    public static <T> RestBean<T> success(String message) {
        return new RestBean<>(200, true, message, null);
    }

    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, true, null, data);
    }

    public static <T> RestBean<T> success(String message, T data) {
        return new RestBean<>(200, true, message, data);
    }

    public static <T> RestBean<T> failure(int status, String message) {
        return new RestBean<>(status, false, message, null);
    }
}