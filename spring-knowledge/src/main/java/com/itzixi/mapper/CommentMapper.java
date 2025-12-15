package com.itzixi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzixi.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
    /**
     * 根据文章ID查询评论（树形结构）
     */
    List<Comment> selectByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 分页查询评论
     */
    IPage<Comment> selectCommentPage(Page<Comment> page, @Param("articleId") Long articleId, @Param("status") Integer status);
    
    /**
     * 查询最新评论
     */
    List<Comment> selectLatestComments(@Param("limit") Integer limit);
} 