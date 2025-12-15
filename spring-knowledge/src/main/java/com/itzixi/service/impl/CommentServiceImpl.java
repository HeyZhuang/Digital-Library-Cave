package com.itzixi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzixi.entity.Comment;
import com.itzixi.mapper.CommentMapper;
import com.itzixi.service.CommentService;
import com.itzixi.service.MessageProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    private final MessageProducerService messageProducerService;
    
    public CommentServiceImpl(MessageProducerService messageProducerService) {
        this.messageProducerService = messageProducerService;
    }
    
    @Override
    public List<Comment> getCommentsByArticleId(Long articleId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, articleId)
                .eq(Comment::getStatus, 1) // 只查询已发布的评论
                .orderByAsc(Comment::getCreatedAt);
        
        List<Comment> comments = this.list(wrapper);
        
        // 构建评论树结构
        return buildCommentTree(comments);
    }
    
    @Override
    public IPage<Comment> getCommentPage(Page<Comment> page, Long articleId, Integer status) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        
        if (articleId != null) {
            wrapper.eq(Comment::getArticleId, articleId);
        }
        if (status != null) {
            wrapper.eq(Comment::getStatus, status);
        }
        
        wrapper.orderByDesc(Comment::getCreatedAt);
        return this.page(page, wrapper);
    }
    
    @Override
    @Transactional
    public Comment createComment(Comment comment) {
        // 设置默认值
        if (comment.getStatus() == null) {
            comment.setStatus(1); // 默认已发布，如需审核可设为0
        }
        if (comment.getLikes() == null) {
            comment.setLikes(0L);
        }
        
        // 保存评论
        this.save(comment);
        
        // 异步发送评论发布消息
        asyncSendCommentPublishMessage(comment.getId(), comment.getArticleId(), 
                comment.getUserId(), "评论发布: " + comment.getContent());
        
        log.info("创建评论成功，ID: {}", comment.getId());
        return comment;
    }
    
    @Override
    @Transactional
    public boolean deleteComment(Long id) {
        Comment comment = this.getById(id);
        if (comment == null) {
            return false;
        }
        
        // 软删除，设置状态为已删除
        comment.setStatus(2);
        boolean result = this.updateById(comment);
        
        if (result) {
            log.info("删除评论成功，ID: {}", id);
        }
        return result;
    }
    
    @Override
    @Transactional
    public boolean approveComment(Long id) {
        Comment comment = this.getById(id);
        if (comment == null) {
            return false;
        }
        
        comment.setStatus(1); // 设为已发布
        boolean result = this.updateById(comment);
        
        if (result) {
            log.info("审核通过评论，ID: {}", id);
        }
        return result;
    }
    
    @Override
    @Transactional
    public boolean rejectComment(Long id) {
        Comment comment = this.getById(id);
        if (comment == null) {
            return false;
        }
        
        comment.setStatus(2); // 设为已删除
        boolean result = this.updateById(comment);
        
        if (result) {
            log.info("拒绝评论，ID: {}", id);
        }
        return result;
    }
    
    @Override
    @Transactional
    public boolean likeComment(Long id) {
        Comment comment = this.getById(id);
        if (comment == null) {
            return false;
        }
        
        // 增加点赞数，防止空指针异常
        Long currentLikes = comment.getLikes();
        comment.setLikes((currentLikes == null ? 0 : currentLikes) + 1);
        boolean result = this.updateById(comment);
        
        if (result) {
            log.info("点赞评论成功，ID: {}", id);
        }
        return result;
    }
    
    @Override
    @Transactional
    public boolean unlikeComment(Long id) {
        Comment comment = this.getById(id);
        if (comment == null) {
            return false;
        }
        
        // 减少点赞数，但不能小于0，防止空指针异常
        Long currentLikes = comment.getLikes();
        long likes = Math.max(0, (currentLikes == null ? 0 : currentLikes) - 1);
        comment.setLikes(likes);
        boolean result = this.updateById(comment);
        
        if (result) {
            log.info("取消点赞评论成功，ID: {}", id);
        }
        return result;
    }
    
    @Override
    public List<Comment> getLatestComments(Integer limit) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getStatus, 1) // 只查询已发布的评论
                .orderByDesc(Comment::getCreatedAt)
                .last("LIMIT " + limit);
        
        return this.list(wrapper);
    }
    
    /**
     * 构建评论树结构
     */
    private List<Comment> buildCommentTree(List<Comment> comments) {
        // 获取顶级评论（parentId为null的评论）
        List<Comment> topComments = comments.stream()
                .filter(comment -> comment.getParentId() == null)
                .collect(Collectors.toList());
        
        // 为每个顶级评论设置子评论
        for (Comment topComment : topComments) {
            setChildren(topComment, comments);
        }
        
        return topComments;
    }
    
    /**
     * 递归设置子评论
     */
    private void setChildren(Comment parent, List<Comment> allComments) {
        List<Comment> children = allComments.stream()
                .filter(comment -> parent.getId().equals(comment.getParentId()))
                .collect(Collectors.toList());
        
        // 如果Comment实体有children字段，这里可以设置
        // parent.setChildren(children);
        
        // 递归处理子评论
        for (Comment child : children) {
            setChildren(child, allComments);
        }
    }
    
    /**
     * 异步发送评论发布消息
     */
    @Async("commentTaskExecutor")
    public void asyncSendCommentPublishMessage(Long commentId, Long articleId, Long userId, String content) {
        try {
            messageProducerService.sendCommentPublishMessage(commentId, articleId, userId, content);
        } catch (Exception e) {
            log.error("异步发送评论发布消息失败: commentId={}, error={}", commentId, e.getMessage(), e);
        }
    }
    
    /**
     * 异步发送评论审核消息
     */
    @Async("commentTaskExecutor")
    public void asyncSendCommentApproveMessage(Long commentId, Long articleId, Long userId, String content) {
        try {
            messageProducerService.sendCommentApproveMessage(commentId, articleId, userId, content);
        } catch (Exception e) {
            log.error("异步发送评论审核消息失败: commentId={}, error={}", commentId, e.getMessage(), e);
        }
    }
} 