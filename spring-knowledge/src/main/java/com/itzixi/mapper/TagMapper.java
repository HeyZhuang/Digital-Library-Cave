package com.itzixi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itzixi.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签Mapper接口
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    
    /**
     * 根据文章ID查询标签
     */
    List<Tag> selectByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 获取热门标签
     */
    List<Tag> selectPopularTags(@Param("limit") Integer limit);
    
    /**
     * 根据标签名称查询（模糊匹配）
     */
    List<Tag> selectByNameLike(@Param("name") String name);
} 