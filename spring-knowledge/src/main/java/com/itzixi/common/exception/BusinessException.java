package com.itzixi.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final Integer code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public static BusinessException unauthorized() {
        return new BusinessException(401, "未授权访问");
    }
    
    public static BusinessException unauthorized(String message) {
        return new BusinessException(401, message);
    }
    
    public static BusinessException forbidden() {
        return new BusinessException(403, "访问被禁止");
    }
    
    public static BusinessException notFound(String resource) {
        return new BusinessException(404, resource + "不存在");
    }
    
    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message);
    }
} 