# 登录功能使用说明

## 问题诊断

根据你提供的日志信息：
```
15:04:37.784 [http-nio-8182-exec-4] WARN  c.i.s.JwtAuthenticationEntryPoint - 未授权访问: /api/articles - Full authentication is required to access this resource
15:04:37.784 [http-nio-8182-exec-5] WARN  c.i.s.JwtAuthenticationEntryPoint - 未授权访问: /api/tags - Full authentication is required to access this resource
```

这确实是因为前端没有登录功能，导致API调用时没有提供JWT认证token。

## 新增功能

我为你的前端项目添加了完整的用户认证功能：

### 1. 认证API (`src/api/auth.ts`)
- 用户登录接口
- 用户注册接口
- 获取当前用户信息
- 自动添加JWT token到请求头
- 401错误自动处理（清除token并跳转登录页）

### 2. 认证状态管理 (`src/stores/auth.ts`)
- Pinia store管理用户状态
- 本地存储用户信息和token
- 登录/注册/登出逻辑
- 自动初始化认证状态

### 3. 登录页面 (`src/views/LoginView.vue`)
- 登录/注册模式切换
- 表单验证
- 错误提示
- 美观的UI设计

### 4. 路由守卫 (`src/router/index.ts`)
- 需要认证的路由自动跳转到登录页
- 已登录用户访问登录页自动跳转到首页
- 支持登录后跳转到原目标页面

### 5. 导航栏更新 (`src/components/SidebarNav.vue`)
- 显示用户信息（头像、昵称、邮箱）
- 登录/登出按钮
- 根据登录状态显示不同内容

## 使用方法

### 启动前端项目
```bash
cd knowledge-frontend
npm run dev
```

### 访问登录页面
打开浏览器访问：`http://localhost:5173/login`

### 功能说明
1. **新用户注册**：
   - 填写用户名、邮箱、密码
   - 昵称为可选项
   - 注册成功后自动登录

2. **用户登录**：
   - 使用用户名和密码登录
   - 登录成功后跳转到首页或原目标页面

3. **自动认证**：
   - 刷新页面后自动恢复登录状态
   - API调用自动携带认证token
   - token过期自动跳转登录页

## API接口说明

前端期望的后端认证接口：

### 登录接口
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}

Response:
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "jwt_token_here",
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "nickname": "测试用户",
      "avatar": "avatar_url",
      "createdAt": "2025-01-01T00:00:00"
    }
  },
  "timestamp": 1735689600000
}
```

### 注册接口
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "string",
  "password": "string",
  "email": "string",
  "nickname": "string" // 可选
}
```

### 获取当前用户
```
GET /api/auth/me
Authorization: Bearer jwt_token_here
```

### 登出接口
```
POST /api/auth/logout
Authorization: Bearer jwt_token_here
```

## 下一步

现在你需要在后端实现对应的认证接口，确保：
1. JWT token生成和验证
2. 用户注册和登录逻辑
3. Spring Security配置允许认证接口访问
4. 其他API接口需要认证才能访问

完成后，你的前端就可以正常登录并访问需要认证的API了！ 