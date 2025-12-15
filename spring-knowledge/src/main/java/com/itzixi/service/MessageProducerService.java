package com.itzixi.service;

import com.itzixi.config.RabbitMQConfig;
import com.itzixi.dto.ArticleMessage;
import com.itzixi.dto.CommentMessage;
import com.itzixi.dto.StatsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 消息生产者服务
 * 负责发送消息到RabbitMQ队列
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducerService {
    
    private final RabbitTemplate rabbitTemplate;
    
    /**
     * 发送文章发布消息
     */
    public void sendArticlePublishMessage(Long articleId, Long userId, String content) {
        ArticleMessage message = new ArticleMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setArticleId(articleId);
        message.setOperationType("PUBLISH");
        message.setUserId(userId);
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ARTICLE_EXCHANGE,
                RabbitMQConfig.ARTICLE_PUBLISH_ROUTING_KEY,
                message
            );
            log.info("发送文章发布消息成功: articleId={}, messageId={}", articleId, message.getMessageId());
        } catch (Exception e) {
            log.error("发送文章发布消息失败: articleId={}, error={}", articleId, e.getMessage(), e);
        }
    }
    
    /**
     * 发送文章更新消息
     */
    public void sendArticleUpdateMessage(Long articleId, Long userId, String content) {
        ArticleMessage message = new ArticleMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setArticleId(articleId);
        message.setOperationType("UPDATE");
        message.setUserId(userId);
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ARTICLE_EXCHANGE,
                RabbitMQConfig.ARTICLE_UPDATE_ROUTING_KEY,
                message
            );
            log.info("发送文章更新消息成功: articleId={}, messageId={}", articleId, message.getMessageId());
        } catch (Exception e) {
            log.error("发送文章更新消息失败: articleId={}, error={}", articleId, e.getMessage(), e);
        }
    }
    
    /**
     * 发送评论发布消息
     */
    public void sendCommentPublishMessage(Long commentId, Long articleId, Long userId, String content) {
        CommentMessage message = new CommentMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setCommentId(commentId);
        message.setArticleId(articleId);
        message.setOperationType("PUBLISH");
        message.setUserId(userId);
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.COMMENT_EXCHANGE,
                RabbitMQConfig.COMMENT_PUBLISH_ROUTING_KEY,
                message
            );
            log.info("发送评论发布消息成功: commentId={}, messageId={}", commentId, message.getMessageId());
        } catch (Exception e) {
            log.error("发送评论发布消息失败: commentId={}, error={}", commentId, e.getMessage(), e);
        }
    }
    
    /**
     * 发送评论审核消息
     */
    public void sendCommentApproveMessage(Long commentId, Long articleId, Long userId, String content) {
        CommentMessage message = new CommentMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setCommentId(commentId);
        message.setArticleId(articleId);
        message.setOperationType("APPROVE");
        message.setUserId(userId);
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.COMMENT_EXCHANGE,
                RabbitMQConfig.COMMENT_APPROVE_ROUTING_KEY,
                message
            );
            log.info("发送评论审核消息成功: commentId={}, messageId={}", commentId, message.getMessageId());
        } catch (Exception e) {
            log.error("发送评论审核消息失败: commentId={}, error={}", commentId, e.getMessage(), e);
        }
    }
    
    /**
     * 发送访问统计消息
     */
    public void sendVisitStatsMessage(Long articleId, Long userId, String ipAddress, String userAgent) {
        StatsMessage message = new StatsMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setStatsType("VISIT");
        message.setRelatedId(articleId);
        message.setUserId(userId);
        message.setIpAddress(ipAddress);
        message.setUserAgent(userAgent);
        message.setCreateTime(LocalDateTime.now());
        
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.STATS_EXCHANGE,
                RabbitMQConfig.STATS_VISIT_ROUTING_KEY,
                message
            );
            log.debug("发送访问统计消息成功: articleId={}, messageId={}", articleId, message.getMessageId());
        } catch (Exception e) {
            log.error("发送访问统计消息失败: articleId={}, error={}", articleId, e.getMessage(), e);
        }
    }
    
    /**
     * 发送搜索统计消息
     */
    public void sendSearchStatsMessage(String keyword, Long userId, String ipAddress, String userAgent) {
        StatsMessage message = new StatsMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setStatsType("SEARCH");
        message.setKeyword(keyword);
        message.setUserId(userId);
        message.setIpAddress(ipAddress);
        message.setUserAgent(userAgent);
        message.setCreateTime(LocalDateTime.now());
        
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.STATS_EXCHANGE,
                RabbitMQConfig.STATS_SEARCH_ROUTING_KEY,
                message
            );
            log.debug("发送搜索统计消息成功: keyword={}, messageId={}", keyword, message.getMessageId());
        } catch (Exception e) {
            log.error("发送搜索统计消息失败: keyword={}, error={}", keyword, e.getMessage(), e);
        }
    }
} 