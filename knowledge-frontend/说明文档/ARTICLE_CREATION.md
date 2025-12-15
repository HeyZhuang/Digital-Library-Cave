# 文章创建功能说明

## 功能概述

为前端网站添加了多个便捷的文章创建入口，让用户可以快速访问文章编辑器来创建新文章。

## 新增功能位置

### 1. 侧边栏导航 - "写文章"按钮

**位置**: 左侧侧边栏，搜索框下方
**文件**: `src/components/SidebarNav.vue`
**功能**: 
- 只有登录用户可见
- 一键跳转到文章编辑器
- 移动端点击后自动关闭侧边栏

```html
<!-- 快速操作 -->
<div v-if="authStore.isAuthenticated" class="p-4 border-b border-gray-200">
  <RouterLink
    to="/article/edit"
    class="w-full flex items-center justify-center px-4 py-2.5 text-sm font-medium text-white bg-primary-600 rounded-lg hover:bg-primary-700 transition-colors shadow-sm"
  >
    <svg class="w-4 h-4 mr-2">...</svg>
    写文章
  </RouterLink>
</div>
```

### 2. 文章列表页 - "新建文章"按钮

**位置**: 文章归档页面右上角
**文件**: `src/views/ArticleListView.vue`
**功能**: 
- 页面头部显著位置
- 与页面标题对齐
- 快速创建新文章

```html
<div class="mb-8 flex items-center justify-between">
  <div>
    <h1 class="text-3xl font-bold text-title mb-2">文章归档</h1>
    <p class="text-gray-600">共 {{ totalArticles }} 篇文章</p>
  </div>
  <RouterLink to="/article/edit" class="flex items-center px-4 py-2 text-sm font-medium text-white bg-primary-600 rounded-lg hover:bg-primary-700 transition-colors shadow-sm">
    <svg class="w-4 h-4 mr-2">...</svg>
    新建文章
  </RouterLink>
</div>
```

### 3. 首页 - "写文章"按钮

**位置**: 首页最新文章区域
**文件**: `src/views/HomeView.vue`
**功能**: 
- 位于"最新文章"标题右侧
- 只有登录用户可见
- 与"查看全部"按钮并列显示

```html
<div class="flex items-center space-x-3">
  <RouterLink 
    v-if="authStore.isAuthenticated"
    to="/article/edit" 
    class="btn btn-primary"
  >
    <svg class="w-4 h-4 mr-1">...</svg>
    写文章
  </RouterLink>
  <RouterLink to="/articles" class="btn btn-secondary">
    查看全部
    <svg class="w-4 h-4 ml-1">...</svg>
  </RouterLink>
</div>
```

## 路由配置

文章编辑路由已配置：
```typescript
{
  path: '/article/edit/:id?',
  name: 'article-edit',
  component: () => import('../views/ArticleEditView.vue'),
  meta: { requiresAuth: true }
}
```

- 访问路径: `/article/edit` (新建) 或 `/article/edit/:id` (编辑)
- 需要用户登录认证
- 支持可选的文章ID参数用于编辑现有文章

## 文章编辑器功能

### 基本信息编辑
- ✅ 文章标题
- ✅ 分类选择
- ✅ 标签管理 (添加/删除)
- ✅ 文章摘要

### 编辑器模式
- ✅ **编辑模式**: 纯文本编辑
- ✅ **预览模式**: 渲染后的内容预览
- ✅ **分屏模式**: 编辑和预览同时显示

### 发布设置
- ✅ 立即发布选项
- ✅ 允许评论设置
- ✅ 保存草稿功能
- ✅ 一键发布

### 统计信息
- ✅ 字数统计
- ✅ 字符数统计
- ✅ 实时更新

## 用户体验优化

### 1. 权限控制
- 只有登录用户才能看到文章创建按钮
- 未登录用户会被重定向到登录页面

### 2. 响应式设计
- 所有按钮都支持移动端显示
- 侧边栏在移动端点击后自动关闭

### 3. 视觉设计
- 统一的按钮样式和颜色
- 清晰的图标指示
- 悬停效果和过渡动画

## 使用场景

### 快速创建
1. 用户登录后，可以通过多个入口快速访问文章编辑器
2. 无需记住复杂的URL路径
3. 从任何页面都能快速开始写作

### 便捷编辑
1. 从文章列表可以直接创建新文章
2. 编辑器支持多种模式，适应不同写作习惯
3. 自动保存和草稿功能防止内容丢失

### 内容管理
1. 分类和标签系统帮助组织内容
2. 发布设置提供灵活的内容控制
3. 统计信息帮助用户了解文章长度

## 技术实现

### 组件通信
- 使用 Vue Router 进行页面导航
- 通过 Pinia stores 管理用户认证状态
- 组件间通过 props 和 events 通信

### 样式系统
- 使用 Tailwind CSS 进行样式设计
- 自定义 CSS 类提供统一的视觉效果
- 响应式设计适配不同屏幕尺寸

### 状态管理
- useAuthStore 管理用户登录状态
- 路由守卫确保访问权限控制
- 组件级状态管理编辑器内容

现在用户可以从多个位置方便地创建新文章，大大提升了内容创作的便利性！🎉 