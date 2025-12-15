package com.itzixi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itzixi.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService extends IService<Tag> {
    
    /**
     * 获取所有标签
     */
    List<Tag> getAllTags();
    
    /**
     * 根据文章ID获取标签
     */
    List<Tag> getTagsByArticleId(Long articleId);
    
    /**
     * 获取热门标签
     */
    List<Tag> getPopularTags(Integer limit);
    
    /**
     * 根据名称搜索标签
     */
    List<Tag> searchTagsByName(String name);
    
    /**
     * 创建或获取标签
     */
    Tag createOrGetTag(String name, String color);
    
    /**
     * 为文章关联标签
     */
    void associateTagsWithArticle(Long articleId, List<String> tagNames);
    
    /**
     * 移除文章的标签关联
     */
    void removeTagsFromArticle(Long articleId);
    
    /**
     * 更新标签
     */
    Tag updateTag(Tag tag);
    
    /**
     * 删除标签
     */
    boolean deleteTag(Long id);
} 