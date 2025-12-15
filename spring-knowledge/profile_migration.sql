-- Profile功能数据库迁移SQL
-- 为sys_user表添加个人资料相关字段

-- 添加个人资料字段
ALTER TABLE sys_user 
ADD COLUMN bio VARCHAR(500) COMMENT '个人简介' AFTER email,
ADD COLUMN phone VARCHAR(32) COMMENT '联系电话' AFTER bio,
ADD COLUMN location VARCHAR(100) COMMENT '所在地' AFTER phone,
ADD COLUMN skills JSON COMMENT '技能标签(JSON数组)' AFTER location,
ADD COLUMN achievements JSON COMMENT '成就徽章(JSON数组)' AFTER skills,
ADD COLUMN activities JSON COMMENT '动态记录(JSON数组)' AFTER achievements,
ADD COLUMN socials JSON COMMENT '社交链接(JSON数组)' AFTER activities;

-- 创建头像上传目录（需要手动创建物理目录）
-- mkdir -p uploads/avatar/

-- 示例数据更新（可选）
UPDATE sys_user SET 
    bio = '这个人很神秘，什么都没写',
    skills = JSON_ARRAY('Java', 'Spring Boot', 'Vue.js'),
    achievements = JSON_ARRAY(
        JSON_OBJECT('icon', '🏆', 'title', '新手上路', 'desc', '完成第一篇文章'),
        JSON_OBJECT('icon', '💡', 'title', '创作达人', 'desc', '发布10篇优质文章')
    ),
    activities = JSON_ARRAY(
        JSON_OBJECT('type', 'register', 'content', '加入知识库系统', 'date', DATE_FORMAT(created_at, '%Y-%m-%d'))
    ),
    socials = JSON_ARRAY(
        JSON_OBJECT('icon', 'github', 'url', 'https://github.com/user')
    )
WHERE bio IS NULL;

-- 添加索引优化查询性能
CREATE INDEX idx_user_location ON sys_user(location);
CREATE INDEX idx_user_phone ON sys_user(phone);

-- 验证表结构
-- DESC sys_user; 