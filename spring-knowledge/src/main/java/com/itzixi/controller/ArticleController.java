package com.itzixi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzixi.common.result.Result;
import com.itzixi.entity.Article;
import com.itzixi.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import com.itzixi.entity.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 文章控制器
 */
@Tag(name = "文章管理", description = "文章相关的API接口")
@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArticleController {
    
    private final ArticleService articleService;
    
    @Operation(summary = "分页查询文章列表", description = "获取文章列表，支持分页和状态筛选")
    @GetMapping
    public DeferredResult<Result<IPage<Article>>> getArticles(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小", example = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        DeferredResult<Result<IPage<Article>>> deferredResult = new DeferredResult<>(30000L);
        
        // 异步处理，避免阻塞请求线程
        CompletableFuture.supplyAsync(() -> {
            try {
                long startTime = System.currentTimeMillis();
                Page<Article> page = new Page<>(pageNum, pageSize);
                IPage<Article> result = articleService.getArticlePage(page, status);
                long endTime = System.currentTimeMillis();
                
                if (endTime - startTime > 1000) {
                    log.warn("文章列表查询耗时较长: {}ms, 页码: {}, 页大小: {}", 
                            endTime - startTime, pageNum, pageSize);
                }
                
                // 显式指定泛型类型解决类型推断问题
                return Result.<IPage<Article>>success(result);
            } catch (Exception e) {
                log.error("查询文章列表失败", e);
                return Result.<IPage<Article>>error("查询文章列表失败: " + e.getMessage());
            }
        }).whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("异步查询文章列表异常", throwable);
                deferredResult.setErrorResult(Result.<IPage<Article>>error("系统异常，请稍后重试"));
            } else {
                deferredResult.setResult(result);
            }
        });
        
        // 超时处理
        deferredResult.onTimeout(() -> {
            log.warn("文章列表查询超时，页码: {}, 页大小: {}", pageNum, pageSize);
            deferredResult.setErrorResult(Result.<IPage<Article>>error("请求超时，请稍后重试"));
        });
        
        return deferredResult;
    }
    
    @Operation(summary = "根据ID查询文章详情", description = "获取指定ID的文章详细信息")
    @GetMapping("/{id}")
    public Result<Article> getArticleById(@Parameter(description = "文章ID") @PathVariable Long id) {
        Article article = articleService.getArticleById(id);
        if (article == null) {
            return Result.notFound();
        }
        // 增加阅读量
        articleService.incrementViews(id);
        return Result.success(article);
    }
    
    @Operation(summary = "创建文章", description = "创建新的文章")
    @PostMapping
    public Result<Article> createArticle(@RequestBody Map<String, Object> requestData) {
        
        // 获取当前登录用户并设置作者ID
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.unauthorized("请先登录");
        }
        
        // 解析请求数据
        Article article = parseArticleFromRequest(requestData);
        List<String> tags = parseTagsFromRequest(requestData);
        
        article.setAuthorId(currentUser.getId());
        Article created = articleService.createArticle(article, tags);
        
        log.info("文章创建成功，ID: {}, 标签: {}", created.getId(), tags);
        return Result.success("文章创建成功", created);
    }
    
    @Operation(summary = "更新文章", description = "更新指定ID的文章")
    @PutMapping("/{id}")
    public Result<Article> updateArticle(
            @Parameter(description = "文章ID") @PathVariable Long id,
            @RequestBody Map<String, Object> requestData) {
        
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.unauthorized("请先登录");
        }
        
        // 验证文章是否存在
        Article existingArticle = articleService.getArticleById(id);
        if (existingArticle == null) {
            return Result.notFound();
        }
        
        // 检查是否是文章作者或管理员
        if (!existingArticle.getAuthorId().equals(currentUser.getId()) && !currentUser.isAdmin()) {
            return Result.forbidden();
        }
        
        // 解析请求数据
        Article article = parseArticleFromRequest(requestData);
        List<String> tags = parseTagsFromRequest(requestData);
        
        // 设置文章ID和作者ID（保持原作者不变）
        article.setId(id);
        article.setAuthorId(existingArticle.getAuthorId());
        
        Article updated = articleService.updateArticle(article, tags);
        
        log.info("文章更新成功，ID: {}, 标签: {}", updated.getId(), tags);
        return Result.success("文章更新成功", updated);
    }
    
    /**
     * 从请求数据中解析文章对象
     */
    private Article parseArticleFromRequest(Map<String, Object> requestData) {
        Article article = new Article();
        
        if (requestData.get("title") != null) {
            article.setTitle(requestData.get("title").toString());
        }
        if (requestData.get("content") != null) {
            article.setContent(requestData.get("content").toString());
        }
        if (requestData.get("summary") != null) {
            article.setSummary(requestData.get("summary").toString());
        }
        if (requestData.get("categoryId") != null) {
            Object categoryId = requestData.get("categoryId");
            if (categoryId instanceof Number) {
                article.setCategoryId(((Number) categoryId).longValue());
            } else if (categoryId instanceof String && !((String) categoryId).isEmpty()) {
                article.setCategoryId(Long.parseLong((String) categoryId));
            }
        }
        if (requestData.get("status") != null) {
            Object status = requestData.get("status");
            if (status instanceof Number) {
                article.setStatus(((Number) status).intValue());
            } else if (status instanceof String) {
                article.setStatus(Integer.parseInt((String) status));
            }
        }
        if (requestData.get("isTop") != null) {
            article.setIsTop((Boolean) requestData.get("isTop"));
        }
        if (requestData.get("allowComments") != null) {
            article.setAllowComments((Boolean) requestData.get("allowComments"));
        }
        
        return article;
    }
    
    /**
     * 从请求数据中解析标签列表
     */
    @SuppressWarnings("unchecked")
    private List<String> parseTagsFromRequest(Map<String, Object> requestData) {
        Object tagsObj = requestData.get("tags");
        if (tagsObj instanceof List) {
            return (List<String>) tagsObj;
        }
        return List.of();
    }
    
    @Operation(summary = "删除文章", description = "删除指定ID的文章")
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@Parameter(description = "文章ID") @PathVariable Long id) {
        boolean success = articleService.deleteArticle(id);
        return success ? Result.success("文章删除成功", null) : Result.error("文章删除失败");
    }
    
    @Operation(summary = "发布文章", description = "将草稿文章发布")
    @PostMapping("/{id}/publish")
    public Result<Void> publishArticle(@Parameter(description = "文章ID") @PathVariable Long id) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.unauthorized("请先登录");
        }
        
        // 验证文章是否存在
        Article existingArticle = articleService.getArticleById(id);
        if (existingArticle == null) {
            log.warn("尝试发布不存在的文章，ID: {}", id);
            return Result.error(404, "文章不存在");
        }
        
        // 检查是否是文章作者或管理员
        if (!existingArticle.getAuthorId().equals(currentUser.getId()) && !currentUser.isAdmin()) {
            log.warn("用户 {} 尝试发布非本人文章，文章ID: {}", currentUser.getUsername(), id);
            return Result.error(403, "只有文章作者或管理员可以发布文章");
        }
        
        // 检查文章状态是否可以发布
        if (existingArticle.isPublished()) {
            log.warn("尝试发布已发布的文章，ID: {}", id);
            return Result.badRequest("文章已经发布，无需重复发布");
        }
        
        if (!existingArticle.isDraft()) {
            log.warn("尝试发布非草稿状态的文章，ID: {}, 当前状态: {}", id, existingArticle.getStatus());
            return Result.badRequest("只有草稿状态的文章可以发布");
        }
        
        // 执行发布操作
        log.info("用户 {} 发布文章，ID: {}", currentUser.getUsername(), id);
        boolean success = articleService.publishArticle(id);
        if (success) {
            log.info("文章发布成功，ID: {}", id);
            return Result.success("文章发布成功", null);
        } else {
            log.error("文章发布失败，ID: {}", id);
            return Result.error("文章发布失败");
        }
    }
    
    @Operation(summary = "将文章设为草稿", description = "将已发布的文章设为草稿")
    @PostMapping("/{id}/draft")
    public Result<Void> draftArticle(@Parameter(description = "文章ID") @PathVariable Long id) {
        boolean success = articleService.draftArticle(id);
        return success ? Result.success("文章已设为草稿", null) : Result.error("操作失败");
    }
    
    @Operation(summary = "归档文章", description = "将文章归档")
    @PostMapping("/{id}/archive")
    public Result<Void> archiveArticle(@Parameter(description = "文章ID") @PathVariable Long id) {
        boolean success = articleService.archiveArticle(id);
        return success ? Result.success("文章已归档", null) : Result.error("操作失败");
    }
    
    @Operation(summary = "根据分类查询文章", description = "获取指定分类下的所有文章")
    @GetMapping("/category/{categoryId}")
    public Result<List<Article>> getArticlesByCategory(@Parameter(description = "分类ID") @PathVariable Long categoryId) {
        List<Article> articles = articleService.getArticlesByCategory(categoryId);
        return Result.success(articles);
    }
    
    @Operation(summary = "根据标签查询文章", description = "获取指定标签下的所有文章")
    @GetMapping("/tag/{tagName}")
    public Result<List<Article>> getArticlesByTag(@Parameter(description = "标签名称") @PathVariable String tagName) {
        List<Article> articles = articleService.getArticlesByTag(tagName);
        return Result.success(articles);
    }
    
    @Operation(summary = "搜索文章", description = "根据关键词搜索文章")
    @GetMapping("/search")
    public Result<IPage<Article>> searchArticles(
            @Parameter(description = "关键词") @RequestParam String keyword,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小", example = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Page<Article> page = new Page<>(pageNum, pageSize);
        IPage<Article> result = articleService.searchArticles(page, keyword);
        return Result.success(result);
    }
    
    @Operation(summary = "获取热门文章", description = "获取最受欢迎的文章列表")
    @GetMapping("/popular")
    public Result<List<Article>> getPopularArticles(
            @Parameter(description = "数量限制", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        List<Article> articles = articleService.getPopularArticles(limit);
        return Result.success(articles);
    }
    
    @Operation(summary = "获取最新文章", description = "获取最新发布的文章列表")
    @GetMapping("/latest")
    public Result<List<Article>> getLatestArticles(
            @Parameter(description = "数量限制", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        List<Article> articles = articleService.getLatestArticles(limit);
        return Result.success(articles);
    }
    
    @Operation(summary = "点赞文章", description = "为文章点赞")
    @PostMapping("/{id}/like")
    public Result<Void> likeArticle(@Parameter(description = "文章ID") @PathVariable Long id) {
        boolean success = articleService.likeArticle(id);
        return success ? Result.success("点赞成功", null) : Result.error("点赞失败");
    }
    
    @Operation(summary = "取消点赞文章", description = "取消文章点赞")
    @DeleteMapping("/{id}/like")
    public Result<Void> unlikeArticle(@Parameter(description = "文章ID") @PathVariable Long id) {
        boolean success = articleService.unlikeArticle(id);
        return success ? Result.success("取消点赞成功", null) : Result.error("操作失败");
    }
    
    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
} 