package com.itzixi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
@AllArgsConstructor
public enum UserRole {
    
    USER(0, "普通用户", "ROLE_USER"),
    ADMIN(1, "管理员", "ROLE_ADMIN");
    
    private final Integer code;
    private final String description;
    private final String authority;
    
    public static UserRole fromCode(Integer code) {
        for (UserRole role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown user role code: " + code);
    }
    
    public static UserRole fromAuthority(String authority) {
        for (UserRole role : values()) {
            if (role.getAuthority().equals(authority)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown user role authority: " + authority);
    }
} 