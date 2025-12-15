package com.itzixi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itzixi.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {
    
    /**
     * 获取所有分类（包含文章数量）
     */
    List<Category> getAllCategoriesWithCount();
    
    /**
     * 根据父分类ID获取子分类
     */
    List<Category> getCategoriesByParentId(Long parentId);
    
    /**
     * 获取根分类
     */
    List<Category> getRootCategories();
    
    /**
     * 创建分类
     */
    Category createCategory(Category category);
    
    /**
     * 更新分类
     */
    Category updateCategory(Category category);
    
    /**
     * 删除分类
     */
    boolean deleteCategory(Long id);
    
    /**
     * 根据名称查询分类
     */
    Category getCategoryByName(String name);
} 