package com.itzixi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统计消息实体
 * 用于消息队列中的统计相关操作
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 统计类型：VISIT, SEARCH, LIKE, COMMENT
     */
    private String statsType;
    
    /**
     * 关联ID（文章ID、用户ID等）
     */
    private Long relatedId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 用户代理
     */
    private String userAgent;
    
    /**
     * 搜索关键词（仅用于搜索统计）
     */
    private String keyword;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 重试次数
     */
    private Integer retryCount = 0;
    
    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 3;
    
    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }
    
    /**
     * 检查是否超过最大重试次数
     */
    public boolean isMaxRetryReached() {
        return this.retryCount >= MAX_RETRY_COUNT;
    }
} 