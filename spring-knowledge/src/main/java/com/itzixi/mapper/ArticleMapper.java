package com.itzixi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzixi.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章Mapper接口
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    
    /**
     * 分页查询文章列表（包含作者和分类信息）
     */
    IPage<Article> selectArticlePageWithDetails(Page<Article> page, @Param("status") Integer status);
    
    /**
     * 根据分类ID查询文章
     */
    List<Article> selectByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 根据标签查询文章
     */
    List<Article> selectByTagName(@Param("tagName") String tagName);
    
    /**
     * 搜索文章
     */
    IPage<Article> searchArticles(Page<Article> page, @Param("keyword") String keyword);
    
    /**
     * 获取热门文章
     */
    List<Article> selectPopularArticles(@Param("limit") Integer limit);
    
    /**
     * 获取最新文章
     */
    List<Article> selectLatestArticles(@Param("limit") Integer limit);
    
    /**
     * 增加文章阅读量
     */
    void incrementViews(@Param("id") Long id);
} 