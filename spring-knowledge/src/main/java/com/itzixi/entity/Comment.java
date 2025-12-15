package com.itzixi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("comment")
public class Comment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("article_id")
    private Long articleId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("parent_id")
    private Long parentId;
    
    @TableField("content")
    private String content;
    
    @TableField("author_name")
    private String authorName;
    
    @TableField("author_email")
    private String authorEmail;
    
    @TableField("author_ip")
    private String authorIp;
    
    @TableField("likes")
    private Long likes;
    
    @TableField("status")
    private Integer status; // 0-待审核，1-已发布，2-已删除
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // 非数据库字段
    @TableField(exist = false)
    private String articleTitle;
    
    @TableField(exist = false)
    private String parentAuthorName;
    
    /**
     * 增加点赞数
     */
    public void incrementLikes() {
        this.likes = (this.likes == null ? 0 : this.likes) + 1;
    }
    
    /**
     * 减少点赞数
     */
    public void decrementLikes() {
        this.likes = Math.max(0, (this.likes == null ? 0 : this.likes) - 1);
    }
} 