package com.itzixi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 标签实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tag")
public class Tag {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("name")
    private String name;
    
    @TableField("color")
    private String color;
    
    @TableField("heat")
    private Long heat;
    
    @TableField("article_count")
    private Long articleCount;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /**
     * 增加热度值
     */
    public void incrementHeat() {
        this.heat = (this.heat == null ? 0 : this.heat) + 1;
    }
    
    /**
     * 增加文章数量
     */
    public void incrementArticleCount() {
        this.articleCount = (this.articleCount == null ? 0 : this.articleCount) + 1;
    }
    
    /**
     * 减少文章数量
     */
    public void decrementArticleCount() {
        this.articleCount = Math.max(0, (this.articleCount == null ? 0 : this.articleCount) - 1);
    }
} 