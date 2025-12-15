package com.itzixi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itzixi.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {
    
    /**
     * 根据文章ID获取评论树
     */
    List<Comment> getCommentsByArticleId(Long articleId);
    
    /**
     * 分页查询评论
     */
    IPage<Comment> getCommentPage(Page<Comment> page, Long articleId, Integer status);
    
    /**
     * 创建评论
     */
    Comment createComment(Comment comment);
    
    /**
     * 删除评论
     */
    boolean deleteComment(Long id);
    
    /**
     * 审核评论
     */
    boolean approveComment(Long id);
    
    /**
     * 拒绝评论
     */
    boolean rejectComment(Long id);
    
    /**
     * 点赞评论
     */
    boolean likeComment(Long id);
    
    /**
     * 取消点赞评论
     */
    boolean unlikeComment(Long id);
    
    /**
     * 获取最新评论
     */
    List<Comment> getLatestComments(Integer limit);
} 