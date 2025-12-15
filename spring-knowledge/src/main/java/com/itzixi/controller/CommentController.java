package com.itzixi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzixi.common.result.Result;
import com.itzixi.entity.Comment;
import com.itzixi.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 评论控制器
 */
@Tag(name = "评论管理", description = "评论相关的API接口")
@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @Operation(summary = "获取文章评论", description = "获取指定文章的所有评论")
    @GetMapping("/article/{articleId}")
    public Result<List<Comment>> getCommentsByArticleId(@Parameter(description = "文章ID") @PathVariable Long articleId) {
        List<Comment> comments = commentService.getCommentsByArticleId(articleId);
        return Result.success(comments);
    }
    
    @Operation(summary = "分页查询评论", description = "获取评论列表，支持分页查询")
    @GetMapping
    public Result<IPage<Comment>> getComments(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "文章ID") @RequestParam(required = false) Long articleId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        Page<Comment> page = new Page<>(pageNum, pageSize);
        IPage<Comment> result = commentService.getCommentPage(page, articleId, status);
        return Result.success(result);
    }
    
    @Operation(summary = "获取最新评论", description = "获取最新的评论列表")
    @GetMapping("/latest")
    public Result<List<Comment>> getLatestComments(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<Comment> comments = commentService.getLatestComments(limit);
        return Result.success(comments);
    }
    
    @Operation(summary = "获取评论详情", description = "获取指定ID的评论详细信息")
    @GetMapping("/{id}")
    public Result<Comment> getCommentById(@Parameter(description = "评论ID") @PathVariable Long id) {
        Comment comment = commentService.getById(id);
        return comment != null ? Result.success(comment) : Result.notFound();
    }
    
    @Operation(summary = "发表评论", description = "为文章发表新评论")
    @PostMapping
    public Result<Comment> createComment(@RequestBody Comment comment, HttpServletRequest request) {
        // 获取用户IP地址
        String ip = getClientIpAddress(request);
        comment.setAuthorIp(ip);
        
        Comment created = commentService.createComment(comment);
        return Result.success("评论发表成功", created);
    }
    
    @Operation(summary = "回复评论", description = "回复指定的评论")
    @PostMapping("/reply")
    public Result<Comment> replyComment(
            @Parameter(description = "父评论ID") @RequestParam Long parentId,
            @RequestBody Comment comment,
            HttpServletRequest request) {
        
        // 获取用户IP地址
        String ip = getClientIpAddress(request);
        comment.setAuthorIp(ip);
        comment.setParentId(parentId);
        
        Comment created = commentService.createComment(comment);
        return Result.success("回复发表成功", created);
    }
    
    @Operation(summary = "删除评论", description = "删除指定ID的评论")
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@Parameter(description = "评论ID") @PathVariable Long id) {
        boolean success = commentService.deleteComment(id);
        return success ? Result.success("评论删除成功", null) : Result.error("评论删除失败");
    }
    
    @Operation(summary = "审核通过评论", description = "审核通过指定的评论")
    @PostMapping("/{id}/approve")
    public Result<Void> approveComment(@Parameter(description = "评论ID") @PathVariable Long id) {
        boolean success = commentService.approveComment(id);
        return success ? Result.success("评论审核通过", null) : Result.error("操作失败");
    }
    
    @Operation(summary = "拒绝评论", description = "拒绝指定的评论")
    @PostMapping("/{id}/reject")
    public Result<Void> rejectComment(@Parameter(description = "评论ID") @PathVariable Long id) {
        boolean success = commentService.rejectComment(id);
        return success ? Result.success("评论已拒绝", null) : Result.error("操作失败");
    }
    
    @Operation(summary = "点赞评论", description = "为评论点赞")
    @PostMapping("/{id}/like")
    public Result<Void> likeComment(@Parameter(description = "评论ID") @PathVariable Long id) {
        boolean success = commentService.likeComment(id);
        return success ? Result.success("点赞成功", null) : Result.error("点赞失败");
    }
    
    @Operation(summary = "取消点赞评论", description = "取消为评论点赞")
    @DeleteMapping("/{id}/like")
    public Result<Void> unlikeComment(@Parameter(description = "评论ID") @PathVariable Long id) {
        boolean success = commentService.unlikeComment(id);
        return success ? Result.success("取消点赞成功", null) : Result.error("操作失败");
    }
    
    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
} 