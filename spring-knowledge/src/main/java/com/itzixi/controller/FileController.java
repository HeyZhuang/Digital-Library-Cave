package com.itzixi.controller;

import com.itzixi.common.result.Result;
import com.itzixi.entity.User;
import com.itzixi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.annotation.Order;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Tag(name = "文件管理", description = "文件上传相关的API接口")
@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Order(1)
public class FileController {
    
    private final UserService userService;
    
    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${website.domain:http://localhost}")
    private String domain;
    
    /**
     * 上传头像
     */
    @Operation(summary = "上传头像", description = "上传用户头像图片")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> uploadAvatar(
            @Parameter(description = "头像文件") @RequestParam("file") MultipartFile file) {
        
        log.info("收到头像上传请求，文件名: {}", file.getOriginalFilename());
        
        if (file.isEmpty()) {
            return Result.badRequest("请选择要上传的文件");
        }
        
        // 获取当前用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.unauthorized("请先登录");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.badRequest("只支持图片文件");
        }
        
        // 检查文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.badRequest("文件大小不能超过5MB");
        }
        
        try {
            // 创建上传目录
            String avatarDir = uploadPath + "avatar/";
            File dir = new File(avatarDir);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("无法创建目录: " + avatarDir);
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
            String filename = "avatar_" + currentUser.getId() + "_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
                "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            
            // 保存文件
            Path filePath = Paths.get(avatarDir + filename);
            Files.write(filePath, file.getBytes());
            
            // 生成访问URL
            String avatarUrl = domain + "/uploads/avatar/" + filename;
            
            // 更新用户头像
            User updatedUser = userService.updateAvatar(currentUser.getId(), avatarUrl);
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("url", avatarUrl);
            result.put("filename", filename);
            result.put("size", file.getSize());
            result.put("user", updatedUser);
            
            log.info("用户 {} 上传头像成功: {}", currentUser.getUsername(), avatarUrl);
            return Result.success("头像上传成功", result);
            
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 通用文件上传
     */
    @Operation(summary = "通用文件上传", description = "上传通用文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> uploadFile(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件类型") @RequestParam(value = "type", defaultValue = "general") String type) {
        
        if (file.isEmpty()) {
            return Result.badRequest("请选择要上传的文件");
        }
        
        // 获取当前用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.unauthorized("请先登录");
        }
        
        // 检查文件大小（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.badRequest("文件大小不能超过10MB");
        }
        
        try {
            // 创建上传目录
            String typeDir = uploadPath + type + "/";
            File dir = new File(typeDir);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("无法创建目录: " + typeDir);
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
            String filename = type + "_" + currentUser.getId() + "_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
                "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            
            // 保存文件
            Path filePath = Paths.get(typeDir + filename);
            Files.write(filePath, file.getBytes());
            
            // 生成访问URL
            String fileUrl = domain + "/uploads/" + type + "/" + filename;
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);
            result.put("originalFilename", originalFilename);
            result.put("size", file.getSize());
            result.put("type", type);
            
            log.info("用户 {} 上传文件成功: {}", currentUser.getUsername(), fileUrl);
            return Result.success("文件上传成功", result);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
} 