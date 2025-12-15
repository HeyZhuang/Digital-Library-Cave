package com.itzixi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章消息实体
 * 用于消息队列中的文章相关操作
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 文章ID
     */
    private Long articleId;
    
    /**
     * 操作类型：PUBLISH, UPDATE, DELETE
     */
    private String operationType;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 消息内容
     */
    private String content;
    
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