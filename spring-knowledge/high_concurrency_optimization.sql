-- ====================================================
-- 高并发数据库优化 SQL 脚本
-- 目标：支持500+并发用户访问
-- 作者：系统优化团队
-- 日期：2025-01-08
-- ====================================================

USE deepseek_doctor;

-- ====================================================
-- 1. 用户表索引优化
-- ====================================================

-- 用户表关键索引
CREATE INDEX IF NOT EXISTS idx_user_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_user_email ON sys_user(email);
CREATE INDEX IF NOT EXISTS idx_user_status ON sys_user(enabled, deleted);
CREATE INDEX IF NOT EXISTS idx_user_created_at ON sys_user(created_at);

-- 用户登录相关复合索引
CREATE INDEX IF NOT EXISTS idx_user_login ON sys_user(username, enabled, deleted);

-- ====================================================
-- 2. 文章表索引优化
-- ====================================================

-- 文章状态和发布时间索引（用于文章列表查询）
CREATE INDEX IF NOT EXISTS idx_article_status_published ON article(status, publish_time, created_at);

-- 文章作者索引（用于用户文章查询）
CREATE INDEX IF NOT EXISTS idx_article_author ON article(author_id, status, created_at);

-- 文章分类索引（用于分类文章查询）
CREATE INDEX IF NOT EXISTS idx_article_category ON article(category_id, status, created_at);

-- 文章热度索引（用于热门文章查询）
CREATE INDEX IF NOT EXISTS idx_article_hot ON article(views DESC, likes DESC, status, created_at);

-- 文章置顶索引（用于置顶文章查询）
CREATE INDEX IF NOT EXISTS idx_article_top ON article(is_top DESC, created_at DESC, status);

-- 文章创建时间索引（用于时间排序）
CREATE INDEX IF NOT EXISTS idx_article_created_at ON article(created_at DESC, status);

-- ====================================================
-- 3. 评论表索引优化
-- ====================================================

-- 评论文章索引（用于文章评论查询）
CREATE INDEX IF NOT EXISTS idx_comment_article ON comment(article_id, status, created_at);

-- 评论父级索引（用于嵌套评论查询）
CREATE INDEX IF NOT EXISTS idx_comment_parent ON comment(parent_id, status, created_at);

-- 评论用户索引（用于用户评论查询）
CREATE INDEX IF NOT EXISTS idx_comment_user ON comment(user_id, created_at);

-- 评论创建时间索引（用于时间排序）
CREATE INDEX IF NOT EXISTS idx_comment_created_at ON comment(created_at DESC);

-- ====================================================
-- 4. 标签表索引优化
-- ====================================================

-- 标签名称索引（用于标签查询）
CREATE INDEX IF NOT EXISTS idx_tag_name ON tag(name);

-- 标签热度索引（用于热门标签查询）
CREATE INDEX IF NOT EXISTS idx_tag_hot ON tag(hot_count DESC, created_at);

-- ====================================================
-- 5. 访问统计表索引优化
-- ====================================================

-- 访问日志文章索引（用于文章访问统计）
CREATE INDEX IF NOT EXISTS idx_visit_article ON visit_log(article_id, created_at);

-- 访问日志用户索引（用于用户访问统计）
CREATE INDEX IF NOT EXISTS idx_visit_user ON visit_log(user_id, created_at);

-- 访问日志时间索引（用于时间范围查询）
CREATE INDEX IF NOT EXISTS idx_visit_created_at ON visit_log(created_at DESC);

-- 访问日志复合索引（用于统计查询）
CREATE INDEX IF NOT EXISTS idx_visit_stats ON visit_log(article_id, DATE(created_at), user_id);

-- ====================================================
-- 6. 搜索日志表索引优化
-- ====================================================

-- 搜索关键词索引（用于搜索统计）
CREATE INDEX IF NOT EXISTS idx_search_keyword ON search_log(keyword, created_at);

-- 搜索用户索引（用于用户搜索历史）
CREATE INDEX IF NOT EXISTS idx_search_user ON search_log(user_id, created_at);

-- 搜索时间索引（用于时间范围查询）
CREATE INDEX IF NOT EXISTS idx_search_created_at ON search_log(created_at DESC);

-- ====================================================
-- 7. 分类表索引优化
-- ====================================================

-- 分类状态索引（用于分类列表查询）
CREATE INDEX IF NOT EXISTS idx_category_status ON category(status, sort_order);

-- 分类名称索引（用于分类搜索）
CREATE INDEX IF NOT EXISTS idx_category_name ON category(name);

-- ====================================================
-- 8. 文件表索引优化
-- ====================================================

-- 文件用户索引（用于用户文件查询）
CREATE INDEX IF NOT EXISTS idx_file_user ON file(user_id, created_at);

-- 文件类型索引（用于文件类型查询）
CREATE INDEX IF NOT EXISTS idx_file_type ON file(file_type, created_at);

-- 文件创建时间索引（用于时间排序）
CREATE INDEX IF NOT EXISTS idx_file_created_at ON file(created_at DESC);

-- ====================================================
-- 9. 通知表索引优化
-- ====================================================

-- 通知用户索引（用于用户通知查询）
CREATE INDEX IF NOT EXISTS idx_notification_user ON notification(user_id, is_read, created_at);

-- 通知类型索引（用于通知类型查询）
CREATE INDEX IF NOT EXISTS idx_notification_type ON notification(type, created_at);

-- 通知创建时间索引（用于时间排序）
CREATE INDEX IF NOT EXISTS idx_notification_created_at ON notification(created_at DESC);

-- ====================================================
-- 10. 优化视图（提升查询性能）
-- ====================================================

-- 热门文章视图（预计算热门文章）
CREATE OR REPLACE VIEW v_hot_articles AS
SELECT 
    a.id,
    a.title,
    a.summary,
    a.views,
    a.likes,
    a.comments_count,
    a.publish_time,
    a.created_at,
    u.nickname AS author_name,
    c.name AS category_name,
    ROW_NUMBER() OVER (ORDER BY a.views DESC, a.likes DESC) AS hot_rank
FROM article a
LEFT JOIN sys_user u ON a.author_id = u.id
LEFT JOIN category c ON a.category_id = c.id
WHERE a.status = 1
ORDER BY a.views DESC, a.likes DESC
LIMIT 50;

-- 最新文章视图（预计算最新文章）
CREATE OR REPLACE VIEW v_latest_articles AS
SELECT 
    a.id,
    a.title,
    a.summary,
    a.views,
    a.likes,
    a.comments_count,
    a.publish_time,
    a.created_at,
    u.nickname AS author_name,
    c.name AS category_name
FROM article a
LEFT JOIN sys_user u ON a.author_id = u.id
LEFT JOIN category c ON a.category_id = c.id
WHERE a.status = 1
ORDER BY a.created_at DESC
LIMIT 100;

-- 用户活跃度视图（用于用户排名）
CREATE OR REPLACE VIEW v_user_activity AS
SELECT 
    u.id,
    u.username,
    u.nickname,
    u.avatar,
    COUNT(DISTINCT a.id) AS article_count,
    SUM(a.views) AS total_views,
    SUM(a.likes) AS total_likes,
    SUM(a.comments_count) AS total_comments,
    MAX(a.created_at) AS last_article_time
FROM sys_user u
LEFT JOIN article a ON u.id = a.author_id AND a.status = 1
WHERE u.enabled = 1 AND u.deleted = 0
GROUP BY u.id, u.username, u.nickname, u.avatar
ORDER BY total_views DESC, article_count DESC;

-- ====================================================
-- 11. 存储过程优化（提升复杂查询性能）
-- ====================================================

DELIMITER //

-- 优化的文章分页查询存储过程
CREATE PROCEDURE GetArticlePageOptimized(
    IN page_num INT,
    IN page_size INT,
    IN article_status INT,
    IN category_id INT,
    IN author_id INT
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
    FROM article a USE INDEX (idx_article_status_published)
    LEFT JOIN sys_user u ON a.author_id = u.id
    LEFT JOIN category c ON a.category_id = c.id
    WHERE (article_status IS NULL OR a.status = article_status)
      AND (category_id IS NULL OR a.category_id = category_id)
      AND (author_id IS NULL OR a.author_id = author_id)
    ORDER BY a.is_top DESC, a.created_at DESC
    LIMIT page_size OFFSET offset_val;
    
    -- 返回总数
    SELECT COUNT(*) AS total_count
    FROM article a
    WHERE (article_status IS NULL OR a.status = article_status)
      AND (category_id IS NULL OR a.category_id = category_id)
      AND (author_id IS NULL OR a.author_id = author_id);
END //

-- 热门文章查询存储过程
CREATE PROCEDURE GetHotArticlesOptimized(IN limit_count INT)
BEGIN
    SELECT 
        a.id,
        a.title,
        a.summary,
        a.views,
        a.likes,
        a.comments_count,
        a.publish_time,
        a.created_at,
        u.nickname AS author_name,
        c.name AS category_name
    FROM article a USE INDEX (idx_article_hot)
    LEFT JOIN sys_user u ON a.author_id = u.id
    LEFT JOIN category c ON a.category_id = c.id
    WHERE a.status = 1
    ORDER BY a.views DESC, a.likes DESC
    LIMIT limit_count;
END //

-- 用户文章统计存储过程
CREATE PROCEDURE GetUserArticleStats(IN user_id BIGINT)
BEGIN
    SELECT 
        COUNT(*) AS total_articles,
        SUM(views) AS total_views,
        SUM(likes) AS total_likes,
        SUM(comments_count) AS total_comments,
        MAX(created_at) AS last_article_time
    FROM article
    WHERE author_id = user_id AND status = 1;
END //

DELIMITER ;

-- ====================================================
-- 12. 表分区（适用于大数据量场景）
-- ====================================================

-- 访问日志表按月分区（如果数据量很大）
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
-- 13. 性能监控查询
-- ====================================================

-- 查看索引使用情况
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

-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query_log';
SHOW VARIABLES LIKE 'long_query_time';

-- ====================================================
-- 14. 数据清理和维护
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
OPTIMIZE TABLE sys_user;

-- ====================================================
-- 15. 执行计划分析
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

SELECT 'High concurrency database optimization completed!' AS status; 