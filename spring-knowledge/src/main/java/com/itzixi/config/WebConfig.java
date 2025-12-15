package com.itzixi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.http.MediaType;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 只处理静态文件访问，不处理API路径
        registry.addResourceHandler("/static/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(3600)
                .setUseLastModified(true);
                
        // 为了向后兼容，也支持 /uploads/** 但优先级较低
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(3600)
                .setUseLastModified(true);
    }
    
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 确保 API 路径优先于静态资源路径
        configurer.setUseTrailingSlashMatch(false)
                 .setUseRegisteredSuffixPatternMatch(false);
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 让 Spring Security 处理 CORS，这里不做额外配置
    }
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(false)
                  .ignoreAcceptHeader(false)
                  .defaultContentType(MediaType.APPLICATION_JSON)
                  .mediaType("json", MediaType.APPLICATION_JSON);
    }
} 