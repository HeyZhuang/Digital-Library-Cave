package com.itzixi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itzixi.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类Mapper接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    
    /**
     * 查询所有分类（包含文章数量）
     */
    List<Category> selectCategoriesWithArticleCount();
    
    /**
     * 根据父分类ID查询子分类
     */
    List<Category> selectByParentId(@Param("parentId") Long parentId);
    
    /**
     * 查询根分类（父分类为空的分类）
     */
    List<Category> selectRootCategories();
} 