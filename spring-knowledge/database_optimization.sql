-- ====================================================
-- 数据库性能优化 SQL 脚本
-- 目标：解决 AsyncRequestNotUsableException 异常
-- 作者：系统优化团队
-- 日期：2025-01-08
-- ====================================================

USE deepseek_doctor;

-- ====================================================
-- 1. 索引优化
-- ====================================================

-- 检查现有索引
SHOW INDEX FROM article;
SHOW INDEX FROM sys_user;
SHOW INDEX FROM category;
SHOW INDEX FROM comment;

-- 为 article 表添加复合索引
-- 用于优化文章列表查询（按状态和创建时间排序）
ALTER TABLE article ADD INDEX IF NOT EXISTS idx_status_created_top (status, is_top, created_at);

-- 为 article 表添加状态+分类复合索引
-- 用于优化按分类查询文章
ALTER TABLE article ADD INDEX IF NOT EXISTS idx_status_category (status, category_id, created_at);

-- 为 article 表添加作者ID索引（如果不存在）
-- ALTER TABLE article ADD INDEX IF NOT EXISTS idx_author_id (author_id);  -- 已存在，注释掉

-- 为 article 表添加浏览量索引
-- 用于优化热门文章查询
ALTER TABLE article ADD INDEX IF NOT EXISTS idx_views_likes (views DESC, likes DESC, status);

-- 为 article 表添加全文索引（仅在需要全文搜索时使用）
-- ALTER TABLE article ADD FULLTEXT INDEX ft_title_summary (title, summary);

-- 为 comment 表添加复合索引
-- 用于优化评论查询
ALTER TABLE comment ADD INDEX IF NOT EXISTS idx_article_status_created (article_id, status, created_at);

-- 为 visit_log 表添加索引
-- 用于优化访问日志查询
ALTER TABLE visit_log ADD INDEX IF NOT EXISTS idx_created_user (created_at, user_id);
ALTER TABLE visit_log ADD INDEX IF NOT EXISTS idx_article_created (article_id, created_at);

-- 为 search_log 表添加索引
-- 用于优化搜索日志查询
ALTER TABLE search_log ADD INDEX IF NOT EXISTS idx_keyword_created (keyword, created_at);

-- ====================================================
-- 2. 表结构优化
-- ====================================================

-- 优化 article 表的 content 字段存储
-- 如果 content 字段很大，考虑分离存储
-- ALTER TABLE article MODIFY COLUMN content LONGTEXT;

-- 为经常查询的字段添加默认值
ALTER TABLE article MODIFY COLUMN views BIGINT NOT NULL DEFAULT 0;
ALTER TABLE article MODIFY COLUMN likes BIGINT NOT NULL DEFAULT 0;
ALTER TABLE article MODIFY COLUMN comments_count BIGINT NOT NULL DEFAULT 0;

-- ====================================================
-- 3. 查询优化视图
-- ====================================================

-- 创建文章列表视图（避免每次都进行 JOIN 操作）
CREATE OR REPLACE VIEW v_article_list AS
SELECT 
    a.id,
    a.title,
    a.summary,
    a.author_id,
    a.category_id,
    a.status,
    a.views,
    a.likes,
    a.comments_count,
    a.is_top,
    a.allow_comments,
    a.publish_time,
    a.created_at,
    a.updated_at,
    u.nickname AS author_name,
    c.name AS category_name
FROM article a
LEFT JOIN sys_user u ON a.author_id = u.id
LEFT JOIN category c ON a.category_id = c.id
WHERE a.status = 1;

-- 创建热门文章视图
CREATE OR REPLACE VIEW v_popular_articles AS
SELECT 
    a.id,
    a.title,
    a.summary,
    a.views,
    a.likes,
    a.publish_time,
    u.nickname AS author_name,
    c.name AS category_name
FROM article a
LEFT JOIN sys_user u ON a.author_id = u.id
LEFT JOIN category c ON a.category_id = c.id
WHERE a.status = 1
ORDER BY a.views DESC, a.likes DESC;

-- ====================================================
-- 4. 存储过程优化
-- ====================================================

DELIMITER //

-- 优化的文章分页查询存储过程
CREATE PROCEDURE GetArticlePageOptimized(
    IN page_num INT,
    IN page_size INT,
    IN article_status INT
)
BEGIN
    DECLARE offset_val INT DEFAULT 0;
    
    SET offset_val = (page_num - 1) * page_size;
    
    -- 使用优化的索引查询
    SELECT 
        a.id,
        a.title,
        a.summary,
        a.author_id,
        a.category_id,
        a.status,
        a.views,
        a.likes,
        a.comments_count,
        a.is_top,
        a.allow_comments,
        a.publish_time,
        a.created_at,
        a.updated_at,
        u.nickname AS author_name,
        c.name AS category_name
    FROM article a USE INDEX (idx_status_created_top)
    LEFT JOIN sys_user u ON a.author_id = u.id
    LEFT JOIN category c ON a.category_id = c.id
    WHERE (article_status IS NULL OR a.status = article_status)
    ORDER BY a.is_top DESC, a.created_at DESC
    LIMIT page_size OFFSET offset_val;
    
    -- 返回总数
    SELECT COUNT(*) AS total_count
    FROM article a
    WHERE (article_status IS NULL OR a.status = article_status);
END //

DELIMITER ;

-- ====================================================
-- 5. 表分区（适用于大数据量场景）
-- ====================================================

-- 如果数据量很大，可以考虑对 visit_log 表进行分区
-- 按月份分区访问日志表
/*
ALTER TABLE visit_log PARTITION BY RANGE (YEAR(created_at) * 100 + MONTH(created_at)) (
    PARTITION p202501 VALUES LESS THAN (202502),
    PARTITION p202502 VALUES LESS THAN (202503),
    PARTITION p202503 VALUES LESS THAN (202504),
    PARTITION p202504 VALUES LESS THAN (202505),
    PARTITION p202505 VALUES LESS THAN (202506),
    PARTITION p202506 VALUES LESS THAN (202507),
    PARTITION p202507 VALUES LESS THAN (202508),
    PARTITION p202508 VALUES LESS THAN (202509),
    PARTITION p202509 VALUES LESS THAN (202510),
    PARTITION p202510 VALUES LESS THAN (202511),
    PARTITION p202511 VALUES LESS THAN (202512),
    PARTITION p202512 VALUES LESS THAN (202601),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
*/

-- ====================================================
-- 6. MySQL 配置优化建议
-- ====================================================

-- 查看当前配置
SHOW VARIABLES LIKE 'innodb_buffer_pool_size';
SHOW VARIABLES LIKE 'innodb_log_file_size';
SHOW VARIABLES LIKE 'max_connections';
SHOW VARIABLES LIKE 'query_cache_size';

-- 建议的 MySQL 配置（添加到 my.cnf）
/*
[mysqld]
# InnoDB 配置
innodb_buffer_pool_size = 1G          # 设置为系统内存的 70-80%
innodb_log_file_size = 256M            # 增加日志文件大小
innodb_log_buffer_size = 16M           # 增加日志缓冲区
innodb_flush_log_at_trx_commit = 2     # 提高性能（略微降低安全性）

# 连接配置
max_connections = 200                  # 增加最大连接数
wait_timeout = 600                     # 连接超时时间
interactive_timeout = 600              # 交互超时时间

# 查询缓存
query_cache_type = 1                   # 启用查询缓存
query_cache_size = 64M                 # 查询缓存大小

# 临时表配置
tmp_table_size = 64M                   # 内存临时表大小
max_heap_table_size = 64M              # 最大堆表大小

# 排序缓冲区
sort_buffer_size = 2M                  # 排序缓冲区
read_buffer_size = 1M                  # 读缓冲区
read_rnd_buffer_size = 2M              # 随机读缓冲区
*/

-- ====================================================
-- 7. 性能监控查询
-- ====================================================

-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query_log';
SHOW VARIABLES LIKE 'long_query_time';

-- 查看表的索引使用情况
SELECT 
    TABLE_SCHEMA,
    TABLE_NAME,
    INDEX_NAME,
    CARDINALITY,
    SUB_PART,
    NULLABLE,
    INDEX_TYPE
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'deepseek_doctor'
ORDER BY TABLE_NAME, SEQ_IN_INDEX;

-- 查看表的存储引擎和状态
SELECT 
    TABLE_NAME,
    ENGINE,
    TABLE_ROWS,
    DATA_LENGTH,
    INDEX_LENGTH,
    (DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024 AS total_size_mb
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'deepseek_doctor'
ORDER BY total_size_mb DESC;

-- ====================================================
-- 8. 数据清理和维护
-- ====================================================

-- 清理过期的访问日志（保留最近3个月）
DELETE FROM visit_log 
WHERE created_at < DATE_SUB(NOW(), INTERVAL 3 MONTH);

-- 清理过期的搜索日志（保留最近1个月）
DELETE FROM search_log 
WHERE created_at < DATE_SUB(NOW(), INTERVAL 1 MONTH);

-- 优化表（重建索引，回收空间）
OPTIMIZE TABLE article;
OPTIMIZE TABLE comment;
OPTIMIZE TABLE visit_log;
OPTIMIZE TABLE search_log;

-- ====================================================
-- 9. 执行计划分析
-- ====================================================

-- 分析关键查询的执行计划
EXPLAIN FORMAT=JSON
SELECT a.*, u.nickname AS author_name, c.name AS category_name
FROM article a
LEFT JOIN sys_user u ON a.author_id = u.id
LEFT JOIN category c ON a.category_id = c.id
WHERE a.status = 1
ORDER BY a.is_top DESC, a.created_at DESC
LIMIT 10;

-- ====================================================
-- 执行完毕
-- ====================================================

SELECT 'Database optimization completed!' AS status; 