package com.itzixi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzixi.entity.Tag;
import com.itzixi.mapper.TagMapper;
import com.itzixi.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签服务实现类
 */
@Slf4j
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public List<Tag> getAllTags() {
        return baseMapper.selectList(new QueryWrapper<Tag>().orderByDesc("article_count"));
    }
    
    @Override
    public List<Tag> getTagsByArticleId(Long articleId) {
        return baseMapper.selectByArticleId(articleId);
    }
    
    @Override
    public List<Tag> getPopularTags(Integer limit) {
        return baseMapper.selectPopularTags(limit);
    }
    
    @Override
    public List<Tag> searchTagsByName(String name) {
        return baseMapper.selectByNameLike("%" + name + "%");
    }
    
    @Override
    @Transactional
    public Tag createOrGetTag(String name, String color) {
        // 先查询是否已存在该标签
        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);
        Tag existingTag = baseMapper.selectOne(wrapper);
        
        if (existingTag != null) {
            return existingTag;
        }
        
        // 创建新标签
        Tag tag = new Tag();
        tag.setName(name);
        tag.setColor(color != null ? color : "#6B7280"); // 默认颜色
        tag.setHeat(0L);
        tag.setArticleCount(0L);
        
        baseMapper.insert(tag);
        return tag;
    }
    
    @Override
    @Transactional
    public void associateTagsWithArticle(Long articleId, List<String> tagNames) {
        for (String tagName : tagNames) {
            // 创建或获取标签
            Tag tag = createOrGetTag(tagName, null);
            
            // 检查关联是否已存在
            String checkSql = "SELECT COUNT(*) FROM article_tag WHERE article_id = ? AND tag_id = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, articleId, tag.getId());
            
            if (count == 0) {
                // 创建文章-标签关联
                String insertSql = "INSERT INTO article_tag (article_id, tag_id) VALUES (?, ?)";
                jdbcTemplate.update(insertSql, articleId, tag.getId());
                
                // 增加标签的文章数量
                tag.incrementArticleCount();
                tag.incrementHeat();
                baseMapper.updateById(tag);
            }
        }
    }
    
    @Override
    @Transactional
    public void removeTagsFromArticle(Long articleId) {
        // 获取文章的所有标签
        List<Tag> tags = getTagsByArticleId(articleId);
        
        // 删除关联关系
        String deleteSql = "DELETE FROM article_tag WHERE article_id = ?";
        jdbcTemplate.update(deleteSql, articleId);
        
        // 减少标签的文章数量
        for (Tag tag : tags) {
            tag.decrementArticleCount();
            baseMapper.updateById(tag);
        }
    }
    
    @Override
    public Tag updateTag(Tag tag) {
        baseMapper.updateById(tag);
        return tag;
    }
    
    @Override
    @Transactional
    public boolean deleteTag(Long id) {
        // 检查是否有文章关联
        String checkSql = "SELECT COUNT(*) FROM article_tag WHERE tag_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);
        
        if (count > 0) {
            throw new RuntimeException("该标签下存在关联文章，无法删除");
        }
        
        return baseMapper.deleteById(id) > 0;
    }
} 