package com.itzixi.service;

import com.itzixi.config.RabbitMQConfig;
import com.itzixi.dto.ArticleMessage;
import com.itzixi.dto.CommentMessage;
import com.itzixi.dto.StatsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 消息消费者服务
 * 负责处理RabbitMQ队列中的消息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageConsumerService {
    
    private final CacheService cacheService;
    
    /**
     * 处理文章发布消息
     */
    @RabbitListener(queues = RabbitMQConfig.ARTICLE_PUBLISH_QUEUE)
    public void handleArticlePublishMessage(ArticleMessage message) {
        try {
            log.info("开始处理文章发布消息: messageId={}, articleId={}", 
                    message.getMessageId(), message.getArticleId());
            
            // 清除相关缓存
            cacheService.evictArticleCache(message.getArticleId());
            cacheService.evictHotArticlesCache();
            cacheService.evictLatestArticlesCache();
            
            // 这里可以添加其他异步处理逻辑
            // 例如：更新搜索引擎索引、发送通知等
            
            log.info("文章发布消息处理完成: messageId={}, articleId={}", 
                    message.getMessageId(), message.getArticleId());
                    
        } catch (Exception e) {
            log.error("处理文章发布消息失败: messageId={}, articleId={}, error={}", 
                    message.getMessageId(), message.getArticleId(), e.getMessage(), e);
            
            // 增加重试次数
            message.incrementRetryCount();
            if (!message.isMaxRetryReached()) {
                log.info("文章发布消息将重试: messageId={}, retryCount={}", 
                        message.getMessageId(), message.getRetryCount());
                // 这里可以实现重试逻辑
            } else {
                log.error("文章发布消息达到最大重试次数: messageId={}", message.getMessageId());
            }
        }
    }
    
    /**
     * 处理文章更新消息
     */
    @RabbitListener(queues = RabbitMQConfig.ARTICLE_UPDATE_QUEUE)
    public void handleArticleUpdateMessage(ArticleMessage message) {
        try {
            log.info("开始处理文章更新消息: messageId={}, articleId={}", 
                    message.getMessageId(), message.getArticleId());
            
            // 清除相关缓存
            cacheService.evictArticleCache(message.getArticleId());
            cacheService.evictHotArticlesCache();
            cacheService.evictLatestArticlesCache();
            
            // 这里可以添加其他异步处理逻辑
            // 例如：更新搜索引擎索引、发送通知等
            
            log.info("文章更新消息处理完成: messageId={}, articleId={}", 
                    message.getMessageId(), message.getArticleId());
                    
        } catch (Exception e) {
            log.error("处理文章更新消息失败: messageId={}, articleId={}, error={}", 
                    message.getMessageId(), message.getArticleId(), e.getMessage(), e);
            
            message.incrementRetryCount();
            if (!message.isMaxRetryReached()) {
                log.info("文章更新消息将重试: messageId={}, retryCount={}", 
                        message.getMessageId(), message.getRetryCount());
            } else {
                log.error("文章更新消息达到最大重试次数: messageId={}", message.getMessageId());
            }
        }
    }
    
    /**
     * 处理评论发布消息
     */
    @RabbitListener(queues = RabbitMQConfig.COMMENT_PUBLISH_QUEUE)
    public void handleCommentPublishMessage(CommentMessage message) {
        try {
            log.info("开始处理评论发布消息: messageId={}, commentId={}, articleId={}", 
                    message.getMessageId(), message.getCommentId(), message.getArticleId());
            
            // 清除相关缓存
            cacheService.evictArticleCache(message.getArticleId());
            
            // 这里可以添加其他异步处理逻辑
            // 例如：发送邮件通知、更新评论计数等
            
            log.info("评论发布消息处理完成: messageId={}, commentId={}", 
                    message.getMessageId(), message.getCommentId());
                    
        } catch (Exception e) {
            log.error("处理评论发布消息失败: messageId={}, commentId={}, error={}", 
                    message.getMessageId(), message.getCommentId(), e.getMessage(), e);
            
            message.incrementRetryCount();
            if (!message.isMaxRetryReached()) {
                log.info("评论发布消息将重试: messageId={}, retryCount={}", 
                        message.getMessageId(), message.getRetryCount());
            } else {
                log.error("评论发布消息达到最大重试次数: messageId={}", message.getMessageId());
            }
        }
    }
    
    /**
     * 处理评论审核消息
     */
    @RabbitListener(queues = RabbitMQConfig.COMMENT_APPROVE_QUEUE)
    public void handleCommentApproveMessage(CommentMessage message) {
        try {
            log.info("开始处理评论审核消息: messageId={}, commentId={}, articleId={}", 
                    message.getMessageId(), message.getCommentId(), message.getArticleId());
            
            // 清除相关缓存
            cacheService.evictArticleCache(message.getArticleId());
            
            // 这里可以添加其他异步处理逻辑
            // 例如：发送邮件通知、更新统计等
            
            log.info("评论审核消息处理完成: messageId={}, commentId={}", 
                    message.getMessageId(), message.getCommentId());
                    
        } catch (Exception e) {
            log.error("处理评论审核消息失败: messageId={}, commentId={}, error={}", 
                    message.getMessageId(), message.getCommentId(), e.getMessage(), e);
            
            message.incrementRetryCount();
            if (!message.isMaxRetryReached()) {
                log.info("评论审核消息将重试: messageId={}, retryCount={}", 
                        message.getMessageId(), message.getRetryCount());
            } else {
                log.error("评论审核消息达到最大重试次数: messageId={}", message.getMessageId());
            }
        }
    }
    
    /**
     * 处理访问统计消息
     */
    @RabbitListener(queues = RabbitMQConfig.STATS_VISIT_QUEUE)
    public void handleVisitStatsMessage(StatsMessage message) {
        try {
            log.debug("开始处理访问统计消息: messageId={}, articleId={}", 
                    message.getMessageId(), message.getRelatedId());
            
            // 异步更新访问统计
            // 这里可以实现批量更新逻辑，提高性能
            
            log.debug("访问统计消息处理完成: messageId={}, articleId={}", 
                    message.getMessageId(), message.getRelatedId());
                    
        } catch (Exception e) {
            log.error("处理访问统计消息失败: messageId={}, articleId={}, error={}", 
                    message.getMessageId(), message.getRelatedId(), e.getMessage(), e);
            
            message.incrementRetryCount();
            if (!message.isMaxRetryReached()) {
                log.debug("访问统计消息将重试: messageId={}, retryCount={}", 
                        message.getMessageId(), message.getRetryCount());
            } else {
                log.error("访问统计消息达到最大重试次数: messageId={}", message.getMessageId());
            }
        }
    }
    
    /**
     * 处理搜索统计消息
     */
    @RabbitListener(queues = RabbitMQConfig.STATS_SEARCH_QUEUE)
    public void handleSearchStatsMessage(StatsMessage message) {
        try {
            log.debug("开始处理搜索统计消息: messageId={}, keyword={}", 
                    message.getMessageId(), message.getKeyword());
            
            // 异步更新搜索统计
            // 这里可以实现批量更新逻辑，提高性能
            
            log.debug("搜索统计消息处理完成: messageId={}, keyword={}", 
                    message.getMessageId(), message.getKeyword());
                    
        } catch (Exception e) {
            log.error("处理搜索统计消息失败: messageId={}, keyword={}, error={}", 
                    message.getMessageId(), message.getKeyword(), e.getMessage(), e);
            
            message.incrementRetryCount();
            if (!message.isMaxRetryReached()) {
                log.debug("搜索统计消息将重试: messageId={}, retryCount={}", 
                        message.getMessageId(), message.getRetryCount());
            } else {
                log.error("搜索统计消息达到最大重试次数: messageId={}", message.getMessageId());
            }
        }
    }
} 