package com.itzixi.dto.auth;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * 更新个人资料请求DTO
 */
@Data
public class UpdateProfileRequest {
    
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Size(max = 500, message = "个人简介长度不能超过500")
    private String bio;
    
    @Size(max = 32, message = "电话号码长度不能超过32")
    private String phone;
    
    @Size(max = 100, message = "所在地长度不能超过100")
    private String location;
    
    private String avatar;
    
    private List<String> skills;
    
    private List<Map<String, Object>> achievements;
    
    private List<Map<String, Object>> activities;
    
    private List<Map<String, Object>> socials;
} 