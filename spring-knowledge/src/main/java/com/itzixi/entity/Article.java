package com.itzixi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.itzixi.common.enums.ArticleStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文章实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("article")
public class Article {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("title")
    private String title;
    
    @TableField("content")
    private String content;
    
    @TableField("summary")
    private String summary;
    
    @TableField("author_id")
    private Long authorId;
    
    @TableField("category_id")
    private Long categoryId;
    
    @TableField("status")
    private Integer status;
    
    @TableField("views")
    private Long views;
    
    @TableField("likes")
    private Long likes;
    
    @TableField("comments_count")
    private Long commentsCount;
    
    @TableField("is_top")
    private Boolean isTop;
    
    @TableField("allow_comments")
    private Boolean allowComments;
    
    @TableField("publish_time")
    private LocalDateTime publishTime;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // 非数据库字段
    @TableField(exist = false)
    private String authorName;
    
    @TableField(exist = false)
    private String categoryName;
    
    // 业务方法
    
    public ArticleStatus getArticleStatus() {
        return ArticleStatus.fromCode(this.status);
    }
    
    public void setArticleStatus(ArticleStatus articleStatus) {
        this.status = articleStatus.getCode();
    }
    
    public boolean isDraft() {
        return ArticleStatus.DRAFT.getCode().equals(this.status);
    }
    
    public boolean isPublished() {
        return ArticleStatus.PUBLISHED.getCode().equals(this.status);
    }
    
    public boolean isArchived() {
        return ArticleStatus.ARCHIVED.getCode().equals(this.status);
    }
    
    public boolean canTransitionTo(ArticleStatus target) {
        return getArticleStatus().canTransitionTo(target);
    }
    
    public void incrementViews() {
        this.views = (this.views == null ? 0 : this.views) + 1;
    }
    
    public void incrementLikes() {
        this.likes = (this.likes == null ? 0 : this.likes) + 1;
    }
    
    public void decrementLikes() {
        this.likes = Math.max(0, (this.likes == null ? 0 : this.likes) - 1);
    }
    
    public void incrementCommentsCount() {
        this.commentsCount = (this.commentsCount == null ? 0 : this.commentsCount) + 1;
    }
    
    public void decrementCommentsCount() {
        this.commentsCount = Math.max(0, (this.commentsCount == null ? 0 : this.commentsCount) - 1);
    }
} 