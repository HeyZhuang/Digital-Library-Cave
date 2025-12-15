package com.itzixi.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 认证响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfo userInfo;
    
    public AuthResponse(String token, String refreshToken, Long expiresIn, UserInfo userInfo) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.userInfo = userInfo;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String avatar;
        private String createdAt;
        
        // 新增个人资料字段
        private String bio;
        private String phone;
        private String location;
        private List<String> skills;
        private List<Map<String, Object>> achievements;
        private List<Map<String, Object>> activities;
        private List<Map<String, Object>> socials;
        
        // 保持原有构造函数兼容性
        public UserInfo(Long id, String username, String nickname, String email, String avatar, String createdAt) {
            this.id = id;
            this.username = username;
            this.nickname = nickname;
            this.email = email;
            this.avatar = avatar;
            this.createdAt = createdAt;
        }
    }
    
    /**
     * 登录数据DTO（匹配前端期望格式）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginData {
        private String token;
        private UserInfo user;
    }
} 