package com.itzixi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itzixi.entity.Article;

import java.util.List;

/**
 * 文章服务接口
 */
public interface ArticleService extends IService<Article> {
    
    /**
     * 分页查询文章列表
     */
    IPage<Article> getArticlePage(Page<Article> page, Integer status);
    
    /**
     * 根据ID查询文章详情
     */
    Article getArticleById(Long id);
    
    /**
     * 创建文章
     */
    Article createArticle(Article article, List<String> tagNames);
    
    /**
     * 更新文章
     */
    Article updateArticle(Article article, List<String> tagNames);
    
    /**
     * 删除文章
     */
    boolean deleteArticle(Long id);
    
    /**
     * 发布文章
     */
    boolean publishArticle(Long id);
    
    /**
     * 将文章设为草稿
     */
    boolean draftArticle(Long id);
    
    /**
     * 归档文章
     */
    boolean archiveArticle(Long id);
    
    /**
     * 根据分类查询文章
     */
    List<Article> getArticlesByCategory(Long categoryId);
    
    /**
     * 根据标签查询文章
     */
    List<Article> getArticlesByTag(String tagName);
    
    /**
     * 搜索文章
     */
    IPage<Article> searchArticles(Page<Article> page, String keyword);
    
    /**
     * 获取热门文章
     */
    List<Article> getPopularArticles(Integer limit);
    
    /**
     * 获取最新文章
     */
    List<Article> getLatestArticles(Integer limit);
    
    /**
     * 增加文章阅读量
     */
    void incrementViews(Long id);
    
    /**
     * 点赞文章
     */
    boolean likeArticle(Long id);
    
    /**
     * 取消点赞文章
     */
    boolean unlikeArticle(Long id);
} 