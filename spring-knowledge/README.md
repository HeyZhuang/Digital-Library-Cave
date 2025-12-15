# Deepseek SpringAI 知识库系统

## 项目简介

基于Spring Boot 3.3.8 + JDK 21构建的现代化知识库管理系统，集成了用户认证、文章管理、搜索服务、统计分析等完整功能。

## 技术栈

### 后端技术
- **Spring Boot 3.3.8** - 核心框架
- **Spring Security 6** - 安全认证
- **JWT** - 无状态认证
- **MyBatis Plus 3.5.10** - ORM框架
- **MySQL 8.0** - 主数据库
- **Redis** - 缓存服务
- **Elasticsearch** - 全文搜索
- **Quartz** - 定时任务
- **HikariCP** - 数据库连接池

### 前端技术
- **Vue 3** - 前端框架
- **TypeScript** - 类型安全
- **Tailwind CSS** - 样式框架
- **Vite** - 构建工具

## 核心功能

### 1. 用户认证模块
- ✅ JWT认证授权
- ✅ 用户注册/登录
- ✅ 角色权限控制（USER/ADMIN）
- ✅ Token刷新机制
- ✅ 密码加密存储

### 2. 文章管理系统
- ✅ 文章CRUD操作
- ✅ 文章状态管理（草稿/发布/归档）
- ✅ 版本历史记录
- ✅ 文章导出（PDF/Markdown）
- ✅ 定时发布功能
- ✅ 文章分类管理

### 3. 标签系统
- ✅ 标签热度计算
- ✅ 标签合并功能
- ✅ 标签自动建议
- ✅ 标签统计分析

### 4. 评论系统
- ✅ 嵌套评论支持
- ✅ 评论点赞功能
- ✅ 评论审核机制
- ✅ Markdown渲染

### 5. 搜索服务
- ✅ Elasticsearch全文搜索
- ✅ 高亮显示匹配内容
- ✅ 相关度排序
- ✅ 搜索统计分析

### 6. 数据统计
- ✅ 访问统计记录
- ✅ 热门内容分析
- ✅ 用户行为统计
- ✅ 搜索关键词分析

### 7. 文件管理
- ✅ 图片上传服务
- ✅ OSS存储集成
- ✅ 文件压缩处理
- ✅ 冗余文件清理

### 8. 定时任务
- ✅ 数据库备份
- ✅ 热度数据计算
- ✅ 死链检测
- ✅ 任务监控管理

## 项目结构

```
deepseek-doctor/
├── src/main/java/com/itzixi/
│   ├── common/                 # 通用组件
│   │   ├── enums/             # 枚举类
│   │   ├── exception/         # 异常处理
│   │   └── result/           # 统一响应
│   ├── config/                # 配置类
│   │   ├── SecurityConfig.java
│   │   └── MyBatisPlusConfig.java
│   ├── controller/            # 控制器
│   ├── entity/               # 实体类
│   ├── dto/                  # 数据传输对象
│   ├── mapper/               # 数据访问层
│   ├── service/              # 业务逻辑层
│   ├── security/             # 安全组件
│   └── utils/                # 工具类
├── src/main/resources/
│   ├── application.yml       # 主配置
│   ├── application-dev.yml   # 开发环境
│   └── application-prod.yml  # 生产环境
└── knowledge-frontend/       # 前端项目
```

## 数据库设计

### 核心表结构
- `sys_user` - 用户表
- `article` - 文章表
- `article_version` - 文章版本表
- `category` - 分类表
- `tag` - 标签表
- `article_tag` - 文章标签关联表
- `comment` - 评论表
- `visit_log` - 访问日志表
- `search_log` - 搜索日志表
- `file_info` - 文件信息表
- `scheduled_task` - 定时任务表

## 快速开始

### 环境要求
- JDK 21+
- MySQL 8.0+
- Redis 6.0+
- Elasticsearch 8.0+
- Node.js 18+

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd deepseek-springai-family-doctor
```

2. **数据库初始化**
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE `deepseek-doctor` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据结构
mysql -u root -p deepseek-doctor < deepseek-doctor.sql
```

3. **配置文件修改**
```yaml
# 修改 application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/deepseek-doctor
    username: your-username
    password: your-password
  data:
    redis:
      host: localhost
      port: 6379
    elasticsearch:
      uris: http://localhost:9200
```

4. **启动后端服务**
```bash
cd deepseek-doctor
mvn clean install
mvn spring-boot:run
```

5. **启动前端服务**
```bash
cd knowledge-frontend
npm install
npm run dev
```

### 默认账户
- 用户名：`admin`
- 密码：`123456`
- 角色：管理员

## API 文档

### 认证接口
```http
POST /auth/login          # 用户登录
POST /auth/register       # 用户注册
POST /auth/refresh        # 刷新Token
GET  /auth/profile        # 获取用户信息
POST /auth/logout         # 用户注销
```

### 文章接口
```http
GET    /articles          # 获取文章列表
POST   /articles          # 创建文章
GET    /articles/{id}     # 获取文章详情
PUT    /articles/{id}     # 更新文章
DELETE /articles/{id}     # 删除文章
GET    /articles/{id}/versions  # 获取版本历史
```

### 标签接口
```http
GET    /tags              # 获取标签列表
POST   /tags              # 创建标签
PUT    /tags/{id}         # 更新标签
DELETE /tags/{id}         # 删除标签
GET    /tags/suggest      # 标签建议
POST   /tags/merge        # 标签合并
```

### 搜索接口
```http
GET    /search            # 全文搜索
GET    /search/suggest    # 搜索建议
```

## 部署说明

### Docker 部署
```bash
# 构建镜像
docker build -t knowledge-base .

# 运行容器
docker run -d -p 8080:8080 knowledge-base
```

### 生产环境配置
1. 修改 `application-prod.yml`
2. 配置SSL证书
3. 设置反向代理
4. 配置日志收集

## 开发指南

### 添加新功能
1. 在对应的包下创建实体类
2. 创建Mapper接口
3. 实现Service业务逻辑
4. 编写Controller接口
5. 添加单元测试

### 代码规范
- 使用 Lombok 简化代码
- 统一异常处理
- RESTful API 设计
- 完善的日志记录

## 性能优化

### 数据库优化
- 合理使用索引
- 查询语句优化
- 连接池调优

### 缓存策略
- Redis 缓存热点数据
- 本地缓存配合使用
- 缓存失效策略

### 搜索优化
- Elasticsearch 索引优化
- 分词器配置
- 搜索结果缓存

## 监控告警

### 应用监控
- Spring Boot Actuator
- Micrometer 指标收集
- Prometheus + Grafana

### 日志管理
- Logback 配置
- 日志文件轮转
- ELK 日志分析

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交代码
4. 发起 Pull Request

## 许可证

MIT License

## 联系方式

- 作者：[Your Name]
- 邮箱：[your-email@example.com]
- 项目地址：[GitHub Repository]

---

**注意**：本项目仅供学习交流使用，请勿用于商业用途。 