package com.itzixi.controller;

import com.itzixi.common.result.Result;
import com.itzixi.entity.Tag;
import com.itzixi.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签管理", description = "标签相关的API接口")
@Slf4j
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TagController {
    
    private final TagService tagService;
    
    @Operation(summary = "获取所有标签", description = "获取系统中的所有标签")
    @GetMapping
    public Result<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return Result.success(tags);
    }
    
    @Operation(summary = "获取热门标签", description = "获取最受欢迎的标签列表")
    @GetMapping("/popular")
    public Result<List<Tag>> getPopularTags(
            @Parameter(description = "数量限制", example = "20") @RequestParam(defaultValue = "20") Integer limit) {
        List<Tag> tags = tagService.getPopularTags(limit);
        return Result.success(tags);
    }
    
    @Operation(summary = "根据文章ID获取标签", description = "获取指定文章的所有标签")
    @GetMapping("/article/{articleId}")
    public Result<List<Tag>> getTagsByArticleId(@Parameter(description = "文章ID") @PathVariable Long articleId) {
        List<Tag> tags = tagService.getTagsByArticleId(articleId);
        return Result.success(tags);
    }
    
    @Operation(summary = "搜索标签", description = "根据名称搜索标签")
    @GetMapping("/search")
    public Result<List<Tag>> searchTags(@Parameter(description = "标签名称") @RequestParam String name) {
        List<Tag> tags = tagService.searchTagsByName(name);
        return Result.success(tags);
    }
    
    @Operation(summary = "根据ID查询标签详情", description = "获取指定ID的标签详细信息")
    @GetMapping("/{id}")
    public Result<Tag> getTagById(@Parameter(description = "标签ID") @PathVariable Long id) {
        Tag tag = tagService.getById(id);
        return tag != null ? Result.success(tag) : Result.notFound();
    }
    
    @Operation(summary = "创建标签", description = "创建新的标签")
    @PostMapping
    public Result<Tag> createTag(
            @Parameter(description = "标签名称") @RequestParam String name,
            @Parameter(description = "标签颜色") @RequestParam(required = false) String color) {
        Tag tag = tagService.createOrGetTag(name, color);
        return Result.success("标签创建成功", tag);
    }
    
    @Operation(summary = "更新标签", description = "更新指定ID的标签")
    @PutMapping("/{id}")
    public Result<Tag> updateTag(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @RequestBody Tag tag) {
        tag.setId(id);
        Tag updated = tagService.updateTag(tag);
        return Result.success("标签更新成功", updated);
    }
    
    @Operation(summary = "删除标签", description = "删除指定ID的标签")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@Parameter(description = "标签ID") @PathVariable Long id) {
        boolean success = tagService.deleteTag(id);
        return success ? Result.success("标签删除成功", null) : Result.error("标签删除失败");
    }
} 