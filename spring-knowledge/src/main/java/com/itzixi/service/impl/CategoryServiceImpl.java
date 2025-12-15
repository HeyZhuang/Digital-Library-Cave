package com.itzixi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzixi.entity.Category;
import com.itzixi.mapper.CategoryMapper;
import com.itzixi.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    
    @Override
    public List<Category> getAllCategoriesWithCount() {
        return baseMapper.selectCategoriesWithArticleCount();
    }
    
    @Override
    public List<Category> getCategoriesByParentId(Long parentId) {
        return baseMapper.selectByParentId(parentId);
    }
    
    @Override
    public List<Category> getRootCategories() {
        return baseMapper.selectRootCategories();
    }
    
    @Override
    public Category createCategory(Category category) {
        // 设置默认排序
        if (category.getSortOrder() == null) {
            // 查询同级分类的最大排序值
            QueryWrapper<Category> wrapper = new QueryWrapper<>();
            wrapper.eq("parent_id", category.getParentId())
                   .orderByDesc("sort_order")
                   .last("LIMIT 1");
            Category lastCategory = baseMapper.selectOne(wrapper);
            category.setSortOrder(lastCategory != null ? lastCategory.getSortOrder() + 1 : 1);
        }
        
        baseMapper.insert(category);
        return category;
    }
    
    @Override
    public Category updateCategory(Category category) {
        baseMapper.updateById(category);
        return category;
    }
    
    @Override
    public boolean deleteCategory(Long id) {
        // 检查是否有子分类
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该分类下存在子分类，无法删除");
        }
        
        // 检查是否有关联文章（这里可以根据需要添加检查逻辑）
        
        return baseMapper.deleteById(id) > 0;
    }
    
    @Override
    public Category getCategoryByName(String name) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);
        return baseMapper.selectOne(wrapper);
    }
} 