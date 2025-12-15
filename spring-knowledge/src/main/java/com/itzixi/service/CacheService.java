package com.itzixi.service;

import com.itzixi.entity.Article;
import com.itzixi.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.util.List;

/**
 * 缓存服务类
 * 支持高并发场景的缓存策略
 */
@Service
@Slf4j
public class CacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String USER_CACHE_PREFIX = "user:";
    private static final String ARTICLE_CACHE_PREFIX = "article:";
    private static final String HOT_ARTICLES_KEY = "hot:articles";
    private static final String SEARCH_CACHE_PREFIX = "search:";
    private static final String CATEGORY_CACHE_PREFIX = "category:";
    private static final String TAG_CACHE_PREFIX = "tag:";
    private static final String STATS_CACHE_PREFIX = "stats:";
    
    /**
     * 用户信息缓存
     */
    @Cacheable(value = "user", key = "#username")
    public User getUserByUsername(String username) {
        log.info("缓存用户信息: {}", username);
        // 这里应该调用实际的用户服务
        return null; // 实际实现中需要注入UserService
    }
    
    /**
     * 文章详情缓存
     */
    @Cacheable(value = "article", key = "#articleId")
    public Article getArticleById(Long articleId) {
        log.info("缓存文章详情: {}", articleId);
        // 这里应该调用实际的文章服务
        return null; // 实际实现中需要注入ArticleService
    }
    
    /**
     * 热门文章缓存
     */
    @Cacheable(value = "hot", key = "'articles'")
    public List<Article> getHotArticles() {
        log.info("缓存热门文章列表");
        // 这里应该调用实际的文章服务
        return null; // 实际实现中需要注入ArticleService
    }
    
    /**
     * 搜索结果缓存
     */
    public List<Article> getSearchResults(String keyword) {
        String cacheKey = SEARCH_CACHE_PREFIX + DigestUtils.md5DigestAsHex(keyword.getBytes());
        List<Article> cached = (List<Article>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("从缓存获取搜索结果: {}", keyword);
            return cached;
        }
        
        log.info("从数据库获取搜索结果: {}", keyword);
        // 这里应该调用实际的搜索服务
        List<Article> results = null; // 实际实现中需要注入SearchService
        
        // 缓存搜索结果，5分钟过期
        if (results != null) {
            redisTemplate.opsForValue().set(cacheKey, results, Duration.ofMinutes(5));
        }
        
        return results;
    }
    
    /**
     * 分类列表缓存
     */
    @Cacheable(value = "category", key = "'list'")
    public List<Object> getCategoryList() {
        log.info("缓存分类列表");
        // 这里应该调用实际的分类服务
        return null; // 实际实现中需要注入CategoryService
    }
    
    /**
     * 标签列表缓存
     */
    @Cacheable(value = "tag", key = "'list'")
    public List<Object> getTagList() {
        log.info("缓存标签列表");
        // 这里应该调用实际的标签服务
        return null; // 实际实现中需要注入TagService
    }
    
    /**
     * 统计数据缓存
     */
    @Cacheable(value = "stats", key = "'dashboard'")
    public Object getDashboardStats() {
        log.info("缓存仪表板统计数据");
        // 这里应该调用实际的统计服务
        return null; // 实际实现中需要注入StatsService
    }
    
    /**
     * 清除文章缓存
     */
    @CacheEvict(value = "article", key = "#articleId")
    public void evictArticleCache(Long articleId) {
        log.info("清除文章缓存: {}", articleId);
    }
    
    /**
     * 清除用户缓存
     */
    @CacheEvict(value = "user", key = "#username")
    public void evictUserCache(String username) {
        log.info("清除用户缓存: {}", username);
    }
    
    /**
     * 清除热门文章缓存
     */
    @CacheEvict(value = "hot", key = "'articles'")
    public void evictHotArticlesCache() {
        log.info("清除热门文章缓存");
    }
    
    /**
     * 清除最新文章缓存
     */
    @CacheEvict(value = "latest", key = "'articles'")
    public void evictLatestArticlesCache() {
        log.info("清除最新文章缓存");
    }
    
    /**
     * 清除分类缓存
     */
    @CacheEvict(value = "category", key = "'list'")
    public void evictCategoryCache() {
        log.info("清除分类缓存");
    }
    
    /**
     * 清除标签缓存
     */
    @CacheEvict(value = "tag", key = "'list'")
    public void evictTagCache() {
        log.info("清除标签缓存");
    }
    
    /**
     * 清除统计数据缓存
     */
    @CacheEvict(value = "stats", key = "'dashboard'")
    public void evictStatsCache() {
        log.info("清除统计数据缓存");
    }
    
    /**
     * 清除所有缓存
     */
    public void clearAllCache() {
        log.info("清除所有缓存");
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
    
    /**
     * 获取缓存统计信息
     */
    public Object getCacheStats() {
        log.info("获取缓存统计信息");
        // 这里可以返回缓存的统计信息
        return null;
    }
} 