-- 修复头像URL中的端口错误
-- 将 http://localhost:3000:8080 替换为 http://localhost:8080

UPDATE sys_user 
SET avatar = REPLACE(avatar, 'http://localhost:3000:8080', 'http://localhost:8080')
WHERE avatar LIKE '%http://localhost:3000:8080%';

-- 查看修复后的结果
SELECT id, username, avatar 
FROM sys_user 
WHERE avatar IS NOT NULL AND avatar != ''; 