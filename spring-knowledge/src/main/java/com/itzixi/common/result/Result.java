package com.itzixi.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
    
    public static <T> Result<T> success() {
        return success(null);
    }
    
    public static <T> Result<T> success(T data) {
        return success("操作成功", data);
    }
    
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    public static <T> Result<T> error() {
        return error("操作失败");
    }
    
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
    
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    public static <T> Result<T> unauthorized() {
        return error(401, "未授权访问");
    }
    
    public static <T> Result<T> unauthorized(String message) {
        return error(401, message);
    }
    
    public static <T> Result<T> forbidden() {
        return error(403, "访问被禁止");
    }
    
    public static <T> Result<T> notFound() {
        return error(404, "资源不存在");
    }
    
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }
} 