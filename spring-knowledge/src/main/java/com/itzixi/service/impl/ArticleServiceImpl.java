package com.itzixi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzixi.common.enums.ArticleStatus;
import com.itzixi.entity.Article;
import com.itzixi.mapper.ArticleMapper;
import com.itzixi.service.ArticleService;
import com.itzixi.service.TagService;
import com.itzixi.service.MessageProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    
    private final TagService tagService;
    private final MessageProducerService messageProducerService;
    
    @Override
    public IPage<Article> getArticlePage(Page<Article> page, Integer status) {
        return baseMapper.selectArticlePageWithDetails(page, status);
    }
    
    @Override
    public Article getArticleById(Long id) {
        Article article = baseMapper.selectById(id);
        if (article != null) {
            // 查询文章的标签
            // 这里可以在后续优化为联查
        }
        return article;
    }
    
    @Override
    @Transactional
    public Article createArticle(Article article, List<String> tagNames) {
        // 设置默认值
        if (article.getStatus() == null) {
            article.setArticleStatus(ArticleStatus.DRAFT);
        }
        if (article.getViews() == null) {
            article.setViews(0L);
        }
        if (article.getLikes() == null) {
            article.setLikes(0L);
        }
        if (article.getCommentsCount() == null) {
            article.setCommentsCount(0L);
        }
        if (article.getIsTop() == null) {
            article.setIsTop(false);
        }
        if (article.getAllowComments() == null) {
            article.setAllowComments(true);
        }
        
        // 如果是发布状态，设置发布时间
        if (article.isPublished() && article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
        
        // 保存文章
        baseMapper.insert(article);
        
        // 关联标签
        if (tagNames != null && !tagNames.isEmpty()) {
            tagService.associateTagsWithArticle(article.getId(), tagNames);
        }
        
        // 异步发送文章发布消息
        if (article.isPublished()) {
            asyncSendArticlePublishMessage(article.getId(), article.getAuthorId(), 
                    "文章发布: " + article.getTitle());
        }
        
        return article;
    }
    
    @Override
    @Transactional
    public Article updateArticle(Article article, List<String> tagNames) {
        // 如果状态改为发布且之前没有发布时间，设置发布时间
        Article existingArticle = baseMapper.selectById(article.getId());
        if (existingArticle != null && article.isPublished() && existingArticle.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
        
        // 更新文章
        baseMapper.updateById(article);
        
        // 更新标签关联
        if (tagNames != null) {
            tagService.removeTagsFromArticle(article.getId());
            if (!tagNames.isEmpty()) {
                tagService.associateTagsWithArticle(article.getId(), tagNames);
            }
        }
        
        return article;
    }
    
    @Override
    @Transactional
    public boolean deleteArticle(Long id) {
        // 删除标签关联
        tagService.removeTagsFromArticle(id);
        // 删除文章
        return baseMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean publishArticle(Long id) {
        Article article = baseMapper.selectById(id);
        if (article != null && article.canTransitionTo(ArticleStatus.PUBLISHED)) {
            article.setArticleStatus(ArticleStatus.PUBLISHED);
            if (article.getPublishTime() == null) {
                article.setPublishTime(LocalDateTime.now());
            }
            return baseMapper.updateById(article) > 0;
        }
        return false;
    }
    
    @Override
    public boolean draftArticle(Long id) {
        Article article = baseMapper.selectById(id);
        if (article != null && article.canTransitionTo(ArticleStatus.DRAFT)) {
            article.setArticleStatus(ArticleStatus.DRAFT);
            return baseMapper.updateById(article) > 0;
        }
        return false;
    }
    
    @Override
    public boolean archiveArticle(Long id) {
        Article article = baseMapper.selectById(id);
        if (article != null && article.canTransitionTo(ArticleStatus.ARCHIVED)) {
            article.setArticleStatus(ArticleStatus.ARCHIVED);
            return baseMapper.updateById(article) > 0;
        }
        return false;
    }
    
    @Override
    public List<Article> getArticlesByCategory(Long categoryId) {
        return baseMapper.selectByCategoryId(categoryId);
    }
    
    @Override
    public List<Article> getArticlesByTag(String tagName) {
        return baseMapper.selectByTagName(tagName);
    }
    
    @Override
    public IPage<Article> searchArticles(Page<Article> page, String keyword) {
        return baseMapper.searchArticles(page, keyword);
    }
    
    @Override
    public List<Article> getPopularArticles(Integer limit) {
        return baseMapper.selectPopularArticles(limit);
    }
    
    @Override
    public List<Article> getLatestArticles(Integer limit) {
        return baseMapper.selectLatestArticles(limit);
    }
    
    @Override
    public void incrementViews(Long id) {
        baseMapper.incrementViews(id);
    }
    
    @Override
    public boolean likeArticle(Long id) {
        Article article = baseMapper.selectById(id);
        if (article != null) {
            article.incrementLikes();
            return baseMapper.updateById(article) > 0;
        }
        return false;
    }
    
    @Override
    public boolean unlikeArticle(Long id) {
        Article article = baseMapper.selectById(id);
        if (article != null) {
            article.decrementLikes();
            return baseMapper.updateById(article) > 0;
        }
        return false;
    }
    
    /**
     * 异步发送文章发布消息
     */
    @Async("articleTaskExecutor")
    public void asyncSendArticlePublishMessage(Long articleId, Long userId, String content) {
        try {
            messageProducerService.sendArticlePublishMessage(articleId, userId, content);
        } catch (Exception e) {
            log.error("异步发送文章发布消息失败: articleId={}, error={}", articleId, e.getMessage(), e);
        }
    }
    
    /**
     * 异步发送文章更新消息
     */
    @Async("articleTaskExecutor")
    public void asyncSendArticleUpdateMessage(Long articleId, Long userId, String content) {
        try {
            messageProducerService.sendArticleUpdateMessage(articleId, userId, content);
        } catch (Exception e) {
            log.error("异步发送文章更新消息失败: articleId={}, error={}", articleId, e.getMessage(), e);
        }
    }
} 