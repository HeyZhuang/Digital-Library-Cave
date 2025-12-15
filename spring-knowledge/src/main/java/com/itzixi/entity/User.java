package com.itzixi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itzixi.common.enums.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户实体类
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class User implements UserDetails {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("username")
    private String username;
    
    @JsonIgnore
    @TableField("password")
    private String password;
    
    @TableField("role")
    private Integer role;
    
    @TableField("avatar")
    private String avatar;
    
    @TableField("email")
    private String email;
    
    @TableField("nickname")
    private String nickname;
    
    // 新增个人资料字段
    @TableField("bio")
    private String bio;
    
    @TableField("phone")
    private String phone;
    
    @TableField("location")
    private String location;
    
    @TableField("skills")
    private String skillsJson;
    
    @TableField("achievements")
    private String achievementsJson;
    
    @TableField("activities")
    private String activitiesJson;
    
    @TableField("socials")
    private String socialsJson;
    
    @TableField("last_login")
    private LocalDateTime lastLogin;
    
    @TableField("enabled")
    private boolean enabled;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // JSON字段的便捷访问方法
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @TableField(exist = false)
    private List<String> skills;
    
    @TableField(exist = false)
    private List<Map<String, Object>> achievements;
    
    @TableField(exist = false)
    private List<Map<String, Object>> activities;
    
    @TableField(exist = false)
    private List<Map<String, Object>> socials;
    
    // Skills字段的getter/setter
    public List<String> getSkills() {
        if (skills == null && skillsJson != null) {
            try {
                skills = objectMapper.readValue(skillsJson, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                log.warn("解析skills JSON失败: {}", e.getMessage());
                skills = new ArrayList<>();
            }
        }
        return skills != null ? skills : new ArrayList<>();
    }
    
    public void setSkills(List<String> skills) {
        this.skills = skills;
        try {
            this.skillsJson = objectMapper.writeValueAsString(skills);
        } catch (Exception e) {
            log.warn("序列化skills失败: {}", e.getMessage());
        }
    }
    
    // Achievements字段的getter/setter
    public List<Map<String, Object>> getAchievements() {
        if (achievements == null && achievementsJson != null) {
            try {
                achievements = objectMapper.readValue(achievementsJson, new TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception e) {
                log.warn("解析achievements JSON失败: {}", e.getMessage());
                achievements = new ArrayList<>();
            }
        }
        return achievements != null ? achievements : new ArrayList<>();
    }
    
    public void setAchievements(List<Map<String, Object>> achievements) {
        this.achievements = achievements;
        try {
            this.achievementsJson = objectMapper.writeValueAsString(achievements);
        } catch (Exception e) {
            log.warn("序列化achievements失败: {}", e.getMessage());
        }
    }
    
    // Activities字段的getter/setter
    public List<Map<String, Object>> getActivities() {
        if (activities == null && activitiesJson != null) {
            try {
                activities = objectMapper.readValue(activitiesJson, new TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception e) {
                log.warn("解析activities JSON失败: {}", e.getMessage());
                activities = new ArrayList<>();
            }
        }
        return activities != null ? activities : new ArrayList<>();
    }
    
    public void setActivities(List<Map<String, Object>> activities) {
        this.activities = activities;
        try {
            this.activitiesJson = objectMapper.writeValueAsString(activities);
        } catch (Exception e) {
            log.warn("序列化activities失败: {}", e.getMessage());
        }
    }
    
    // Socials字段的getter/setter
    public List<Map<String, Object>> getSocials() {
        if (socials == null && socialsJson != null) {
            try {
                socials = objectMapper.readValue(socialsJson, new TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception e) {
                log.warn("解析socials JSON失败: {}", e.getMessage());
                socials = new ArrayList<>();
            }
        }
        return socials != null ? socials : new ArrayList<>();
    }
    
    public void setSocials(List<Map<String, Object>> socials) {
        this.socials = socials;
        try {
            this.socialsJson = objectMapper.writeValueAsString(socials);
        } catch (Exception e) {
            log.warn("序列化socials失败: {}", e.getMessage());
        }
    }
    
    // UserDetails 接口实现
    
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole userRole = UserRole.fromCode(this.role);
        return Collections.singletonList(new SimpleGrantedAuthority(userRole.getAuthority()));
    }
    
    @Override
    @JsonIgnore
    public String getPassword() {
        return this.password;
    }
    
    @Override
    public String getUsername() {
        return this.username;
    }
    
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.enabled;
    }
    
    // 业务方法
    
    public UserRole getUserRole() {
        return UserRole.fromCode(this.role);
    }
    
    public void setUserRole(UserRole userRole) {
        this.role = userRole.getCode();
    }
    
    public boolean isAdmin() {
        return UserRole.ADMIN.getCode().equals(this.role);
    }
} 