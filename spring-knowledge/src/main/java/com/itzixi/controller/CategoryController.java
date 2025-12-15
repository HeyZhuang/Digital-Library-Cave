package com.itzixi.controller;

import com.itzixi.common.result.Result;
import com.itzixi.entity.Category;
import com.itzixi.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 分类控制器
 */
@Tag(name = "分类管理", description = "分类相关的API接口")
@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @Operation(summary = "获取所有分类", description = "获取所有分类（包含文章数量）")
    @GetMapping
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategoriesWithCount();
        return Result.success(categories);
    }
    
    @Operation(summary = "获取分类统计数据", description = "获取分类统计数据，用于动态展示")
    @GetMapping("/stats")
    public Result<List<Map<String, Object>>> getCategoryStats() {
        List<Category> categories = categoryService.getAllCategoriesWithCount();
        
        // 创建统计数据
        List<Map<String, Object>> stats = categories.stream().map(category -> {
            Map<String, Object> stat = new HashMap<>();
            stat.put("id", category.getId());
            stat.put("name", category.getName());
            stat.put("description", category.getDescription());
            
            // 为不同分类设置不同的图标和颜色
            String icon = getIconForCategory(category.getName());
            String color = getColorForCategory(category.getName());
            stat.put("icon", icon);
            stat.put("color", color);
            
            // 设置统计数据
            Long articleCount = category.getArticleCount() != null ? category.getArticleCount() : 0L;
            stat.put("articleCount", articleCount);
            
            // 模拟总阅读量 (基于文章数量 * 随机因子)
            Random random = new Random(category.getId().hashCode()); // 使用ID作为种子保持一致性
            long totalViews = articleCount * (50 + random.nextInt(200)); // 每篇文章50-250阅读量
            stat.put("totalViews", totalViews);
            
            // 计算进度百分比 (基于文章数量)
            int progress = Math.min(100, Math.max(10, (int) (articleCount * 8 + random.nextInt(20))));
            stat.put("progress", progress);
            
            // 设置热门主题
            String[] topics = getTopicsForCategory(category.getName());
            stat.put("topics", topics);
            
            // 设置更新时间
            stat.put("lastUpdated", category.getUpdatedAt() != null ? category.getUpdatedAt().toString() : null);
            
            return stat;
        }).toList();
        
        return Result.success(stats);
    }
    
    /**
     * 根据分类名称获取图标
     */
    private String getIconForCategory(String categoryName) {
        if (categoryName.contains("前端") || categoryName.contains("Vue") || categoryName.contains("React")) {
            return "⚡";
        } else if (categoryName.contains("后端") || categoryName.contains("Java") || categoryName.contains("Spring")) {
            return "🚀";
        } else if (categoryName.contains("数据库") || categoryName.contains("MySQL") || categoryName.contains("Redis")) {
            return "🗄️";
        } else if (categoryName.contains("云计算") || categoryName.contains("Docker") || categoryName.contains("Kubernetes")) {
            return "☁️";
        } else if (categoryName.contains("人工智能") || categoryName.contains("AI") || categoryName.contains("机器学习")) {
            return "🤖";
        } else if (categoryName.contains("算法") || categoryName.contains("数据结构")) {
            return "🧮";
        } else if (categoryName.contains("网络") || categoryName.contains("安全")) {
            return "🔒";
        } else if (categoryName.contains("移动") || categoryName.contains("Android") || categoryName.contains("iOS")) {
            return "📱";
        } else if (categoryName.contains("编程语言") || categoryName.contains("语言")) {
            return "💻";
        } else {
            return "📚";
        }
    }
    
    /**
     * 根据分类名称获取颜色
     */
    private String getColorForCategory(String categoryName) {
        if (categoryName.contains("前端") || categoryName.contains("Vue") || categoryName.contains("React")) {
            return "vue";
        } else if (categoryName.contains("后端") || categoryName.contains("Java") || categoryName.contains("Spring")) {
            return "backend";
        } else if (categoryName.contains("数据库")) {
            return "database";
        } else if (categoryName.contains("云计算")) {
            return "cloud";
        } else if (categoryName.contains("人工智能") || categoryName.contains("AI")) {
            return "ai";
        } else if (categoryName.contains("编程语言")) {
            return "javascript";
        } else {
            return "default";
        }
    }
    
    /**
     * 根据分类名称获取热门主题
     */
    private String[] getTopicsForCategory(String categoryName) {
        if (categoryName.contains("前端") || categoryName.contains("Vue") || categoryName.contains("React")) {
            return new String[]{"Vue 3", "React", "TypeScript", "Vite"};
        } else if (categoryName.contains("后端") || categoryName.contains("Java") || categoryName.contains("Spring")) {
            return new String[]{"Spring Boot", "MyBatis", "Redis", "MySQL"};
        } else if (categoryName.contains("数据库")) {
            return new String[]{"MySQL", "Redis", "MongoDB", "PostgreSQL"};
        } else if (categoryName.contains("云计算")) {
            return new String[]{"Docker", "Kubernetes", "AWS", "微服务"};
        } else if (categoryName.contains("人工智能") || categoryName.contains("AI")) {
            return new String[]{"机器学习", "深度学习", "TensorFlow", "PyTorch"};
        } else if (categoryName.contains("编程语言")) {
            return new String[]{"JavaScript", "TypeScript", "Python", "Java"};
        } else {
            return new String[]{"基础知识", "最佳实践", "实战案例", "学习心得"};
        }
    }
    
    @Operation(summary = "获取根分类", description = "获取顶级分类列表")
    @GetMapping("/root")
    public Result<List<Category>> getRootCategories() {
        List<Category> categories = categoryService.getRootCategories();
        return Result.success(categories);
    }
    
    @Operation(summary = "根据父分类ID获取子分类", description = "获取指定父分类下的子分类")
    @GetMapping("/parent/{parentId}")
    public Result<List<Category>> getCategoriesByParentId(@Parameter(description = "父分类ID") @PathVariable Long parentId) {
        List<Category> categories = categoryService.getCategoriesByParentId(parentId);
        return Result.success(categories);
    }
    
    @Operation(summary = "根据ID查询分类详情", description = "获取指定ID的分类详细信息")
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@Parameter(description = "分类ID") @PathVariable Long id) {
        Category category = categoryService.getById(id);
        return category != null ? Result.success(category) : Result.notFound();
    }
    
    @Operation(summary = "创建分类", description = "创建新的分类")
    @PostMapping
    public Result<Category> createCategory(@RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return Result.success("分类创建成功", created);
    }
    
    @Operation(summary = "更新分类", description = "更新指定ID的分类")
    @PutMapping("/{id}")
    public Result<Category> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @RequestBody Category category) {
        category.setId(id);
        Category updated = categoryService.updateCategory(category);
        return Result.success("分类更新成功", updated);
    }
    
    @Operation(summary = "删除分类", description = "删除指定ID的分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@Parameter(description = "分类ID") @PathVariable Long id) {
        try {
            boolean success = categoryService.deleteCategory(id);
            return success ? Result.success("分类删除成功", null) : Result.error("分类删除失败");
        } catch (RuntimeException e) {
            return Result.badRequest(e.getMessage());
        }
    }
} 