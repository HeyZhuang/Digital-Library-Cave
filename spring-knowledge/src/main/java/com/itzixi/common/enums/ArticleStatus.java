package com.itzixi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文章状态枚举
 */
@Getter
@AllArgsConstructor
public enum ArticleStatus {
    
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    ARCHIVED(2, "已归档");
    
    private final Integer code;
    private final String description;
    
    public static ArticleStatus fromCode(Integer code) {
        for (ArticleStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown article status code: " + code);
    }
    
    /**
     * 检查状态转换是否合法
     */
    public boolean canTransitionTo(ArticleStatus target) {
        return switch (this) {
            case DRAFT -> target == PUBLISHED || target == ARCHIVED;
            case PUBLISHED -> target == ARCHIVED || target == DRAFT;
            case ARCHIVED -> target == DRAFT;
        };
    }
} 