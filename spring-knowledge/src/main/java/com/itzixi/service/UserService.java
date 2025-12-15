package com.itzixi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itzixi.dto.auth.UpdateProfileRequest;
import com.itzixi.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
    
    /**
     * 用户注册
     */
    User register(String username, String password, String email, String nickname);
    
    /**
     * 用户登录
     */
    String login(String username, String password);
    
    /**
     * 刷新Token
     */
    String refreshToken(String oldToken);
    
    /**
     * 更新最后登录时间
     */
    void updateLastLogin(String username);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 更新用户个人资料
     */
    User updateProfile(Long userId, UpdateProfileRequest request);
    
    /**
     * 更新用户头像
     */
    User updateAvatar(Long userId, String avatarUrl);
} 