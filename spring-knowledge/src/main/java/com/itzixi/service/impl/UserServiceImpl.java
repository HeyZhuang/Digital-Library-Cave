package com.itzixi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzixi.common.enums.UserRole;
import com.itzixi.common.exception.BusinessException;
import com.itzixi.entity.User;
import com.itzixi.mapper.UserMapper;
import com.itzixi.service.UserService;
import com.itzixi.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itzixi.dto.auth.UpdateProfileRequest;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> 
        implements UserService, UserDetailsService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        return user;
    }
    
    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return this.getOne(wrapper);
    }
    
    @Override
    @Transactional
    public User register(String username, String password, String email, String nickname) {
        // 检查用户名是否已存在
        if (existsByUsername(username)) {
            throw BusinessException.badRequest("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (existsByEmail(email)) {
            throw BusinessException.badRequest("邮箱已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setNickname(nickname != null ? nickname : username);
        user.setUserRole(UserRole.USER);
        user.setEnabled(true);
        
        if (!this.save(user)) {
            throw new BusinessException("注册失败");
        }
        
        log.info("用户注册成功: {}", username);
        return user;
    }
    
    @Override
    public String login(String username, String password) {
        try {
            // 进行身份验证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // 生成JWT Token
            String token = jwtUtil.createToken(username);
            
            // 更新最后登录时间
            updateLastLogin(username);
            
            log.info("用户登录成功: {}", username);
            return token;
            
        } catch (Exception e) {
            log.warn("用户登录失败: {} - {}", username, e.getMessage());
            throw BusinessException.unauthorized("用户名或密码错误");
        }
    }
    
    @Override
    public String refreshToken(String oldToken) {
        try {
            return jwtUtil.refreshToken(oldToken);
        } catch (Exception e) {
            throw BusinessException.unauthorized("Token刷新失败");
        }
    }
    
    @Override
    @Transactional
    public void updateLastLogin(String username) {
        User user = findByUsername(username);
        if (user != null) {
            user.setLastLogin(LocalDateTime.now());
            this.updateById(user);
        }
    }
    
    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return this.count(wrapper) > 0;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查邮箱是否被其他用户使用
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (existsByEmail(request.getEmail())) {
                throw BusinessException.badRequest("邮箱已被其他用户使用");
            }
            user.setEmail(request.getEmail());
        }
        
        // 更新基本信息
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        
        // 更新JSON字段
        if (request.getSkills() != null) {
            user.setSkills(request.getSkills());
        }
        if (request.getAchievements() != null) {
            user.setAchievements(request.getAchievements());
        }
        if (request.getActivities() != null) {
            user.setActivities(request.getActivities());
        }
        if (request.getSocials() != null) {
            user.setSocials(request.getSocials());
        }
        
        // 保存更新
        if (!this.updateById(user)) {
            throw new BusinessException("更新个人资料失败");
        }
        
        log.info("用户 {} 更新个人资料成功", user.getUsername());
        return user;
    }
    
    @Override
    @Transactional
    public User updateAvatar(Long userId, String avatarUrl) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setAvatar(avatarUrl);
        if (!this.updateById(user)) {
            throw new BusinessException("更新头像失败");
        }
        
        log.info("用户 {} 更新头像成功: {}", user.getUsername(), avatarUrl);
        return user;
    }
} 