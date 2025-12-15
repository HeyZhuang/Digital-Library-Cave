package com.itzixi.controller;

import com.itzixi.common.result.Result;
import com.itzixi.dto.auth.AuthResponse;
import com.itzixi.dto.auth.LoginRequest;
import com.itzixi.dto.auth.RegisterRequest;
import com.itzixi.dto.auth.UpdateProfileRequest;
import com.itzixi.entity.User;
import com.itzixi.service.UserService;
import com.itzixi.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<AuthResponse.LoginData> login(@Validated @RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        String refreshToken = jwtUtil.createRefreshToken(request.getUsername());
        
        User user = userService.findByUsername(request.getUsername());
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getEmail(),
            user.getAvatar(),
            user.getCreatedAt().toString()
        );
        
        AuthResponse.LoginData loginData = new AuthResponse.LoginData(token, userInfo);
        
        return Result.success("登录成功", loginData);
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<AuthResponse.LoginData> register(@Validated @RequestBody RegisterRequest request) {
        User user = userService.register(
            request.getUsername(), 
            request.getPassword(), 
            request.getEmail(), 
            request.getNickname()
        );
        
        String token = jwtUtil.createToken(user.getUsername());
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername());
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getEmail(),
            user.getAvatar(),
            user.getCreatedAt().toString()
        );
        
        AuthResponse.LoginData loginData = new AuthResponse.LoginData(token, userInfo);
        
        return Result.success("注册成功", loginData);
    }
    
    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public Result<AuthResponse.LoginData> refresh(HttpServletRequest request) {
        String oldToken = getTokenFromRequest(request);
        if (oldToken == null) {
            return Result.badRequest("Token不能为空");
        }
        
        String newToken = userService.refreshToken(oldToken);
        String username = jwtUtil.getUsernameFromToken(newToken);
        String refreshToken = jwtUtil.createRefreshToken(username);
        
        User user = userService.findByUsername(username);
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getEmail(),
            user.getAvatar(),
            user.getCreatedAt().toString()
        );
        
        AuthResponse.LoginData loginData = new AuthResponse.LoginData(newToken, userInfo);
        
        return Result.success("Token刷新成功", loginData);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<AuthResponse.UserInfo> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        // 重新从数据库获取用户信息，确保数据最新
        User currentUser = userService.getById(user.getId());
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
            currentUser.getId(),
            currentUser.getUsername(),
            currentUser.getNickname(),
            currentUser.getEmail(),
            currentUser.getAvatar(),
            currentUser.getCreatedAt().toString()
        );
        
        // 设置扩展字段
        userInfo.setBio(currentUser.getBio());
        userInfo.setPhone(currentUser.getPhone());
        userInfo.setLocation(currentUser.getLocation());
        userInfo.setSkills(currentUser.getSkills());
        userInfo.setAchievements(currentUser.getAchievements());
        userInfo.setActivities(currentUser.getActivities());
        userInfo.setSocials(currentUser.getSocials());
        
        return Result.success(userInfo);
    }
    
    /**
     * 更新当前用户信息
     */
    @PutMapping("/me")
    public Result<AuthResponse.UserInfo> updateMe(@Validated @RequestBody UpdateProfileRequest request) {
        log.info("收到更新用户信息请求: {}", request);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            log.error("用户认证信息无效");
            return Result.unauthorized("用户认证信息无效");
        }
        
        User currentUser = (User) authentication.getPrincipal();
        log.info("当前用户: {}", currentUser.getUsername());
        
        // 更新用户信息
        User updatedUser = userService.updateProfile(currentUser.getId(), request);
        
        // 构建返回数据
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
            updatedUser.getId(),
            updatedUser.getUsername(),
            updatedUser.getNickname(),
            updatedUser.getEmail(),
            updatedUser.getAvatar(),
            updatedUser.getCreatedAt().toString()
        );
        
        // 设置扩展字段
        userInfo.setBio(updatedUser.getBio());
        userInfo.setPhone(updatedUser.getPhone());
        userInfo.setLocation(updatedUser.getLocation());
        userInfo.setSkills(updatedUser.getSkills());
        userInfo.setAchievements(updatedUser.getAchievements());
        userInfo.setActivities(updatedUser.getActivities());
        userInfo.setSocials(updatedUser.getSocials());
        
        log.info("用户信息更新成功: {}", currentUser.getUsername());
        return Result.success("个人资料更新成功", userInfo);
    }
    
    /**
     * 测试端点 - 验证控制器是否正常工作
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("AuthController 正常工作");
    }
    
    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT是无状态的，客户端删除token即可
        // 这里可以将token加入黑名单（如果需要的话）
        return Result.success("注销成功", null);
    }
    
    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Result.success(!exists);
    }
    
    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return Result.success(!exists);
    }
    
    /**
     * 从请求头中提取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 