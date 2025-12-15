# 前端数据持久化问题诊断与解决方案

## 🔍 问题现象
- **前端新增的数据无法永久保存**：刷新页面后消失
- **数据库直接新增的内容可以在前端显示**：说明读取功能正常

## 🚨 根本原因分析

### 1. **前端API接口不完整**
**问题**：`src/api/articles.ts` 中缺少写操作接口
- ❌ 没有 `createArticle` 方法
- ❌ 没有 `updateArticle` 方法  
- ❌ 没有 `deleteArticle` 方法
- ✅ 只有查询相关的方法

### 2. **前端Store缺少写操作**
**问题**：`src/stores/articles.ts` 中缺少数据修改功能
- ❌ 没有创建文章的Action
- ❌ 没有更新文章的Action
- ❌ 没有删除文章的Action
- ✅ 只有查询和搜索功能

### 3. **组件使用模拟调用**
**问题**：`ArticleEditView.vue` 中的保存逻辑是假的
- ❌ `saveDraft()` 使用 `setTimeout` 模拟
- ❌ `publishArticle()` 使用 `setTimeout` 模拟
- ❌ 没有真正调用后端API
- ❌ 数据只在前端内存中操作

### 4. **前后端连接断层**
**问题**：前端组件和后端API之间缺少桥梁
- ✅ 后端API已实现（`ArticleController.java`）
- ❌ 前端没有正确调用后端API
- ❌ 数据流程不完整

## ✅ 解决方案实施

### 1. **补充前端API接口**
```typescript
// src/api/articles.ts 新增方法：
export const articleApi = {
  // 创建文章
  createArticle(article: CreateArticleRequest): Promise<ApiResponse<Article>>
  
  // 更新文章  
  updateArticle(article: UpdateArticleRequest): Promise<ApiResponse<Article>>
  
  // 删除文章
  deleteArticle(id: number): Promise<ApiResponse<void>>
  
  // 发布/草稿操作
  publishArticle(id: number): Promise<ApiResponse<void>>
  draftArticle(id: number): Promise<ApiResponse<void>>
}
```

### 2. **扩展前端Store功能**
```typescript
// src/stores/articles.ts 新增Actions：
const articlesStore = {
  // 写操作
  createArticle(articleData: CreateArticleRequest): Promise<Article>
  updateArticle(articleData: UpdateArticleRequest): Promise<Article>
  deleteArticle(id: number): Promise<boolean>
  publishArticle(id: number): Promise<boolean>
  draftArticle(id: number): Promise<boolean>
}
```

### 3. **修复组件保存逻辑**
```typescript
// ArticleEditView.vue 真实API调用：
const saveDraft = async () => {
  try {
    // ✅ 真实API调用，数据持久化到数据库
    const articleData = { title, content, summary, ... }
    const result = await articlesStore.createArticle(articleData)
    showMessage('草稿保存成功')
  } catch (error) {
    showMessage('保存失败', true)
  }
}
```

## 🔄 完整数据流程

### 创建文章流程：
1. **前端填写** → `ArticleEditView.vue`
2. **点击保存** → `saveDraft()` 或 `publishArticle()`
3. **调用Store** → `articlesStore.createArticle()`
4. **调用API** → `articleApi.createArticle()`
5. **发送请求** → `POST /api/articles`
6. **后端处理** → `ArticleController.createArticle()`
7. **数据库保存** → 持久化存储
8. **返回结果** → 更新前端状态

### 更新文章流程：
1. **加载文章** → `loadArticle(id)`
2. **修改内容** → 用户编辑
3. **点击保存** → `saveDraft()` 或 `publishArticle()`
4. **调用Store** → `articlesStore.updateArticle()`
5. **调用API** → `articleApi.updateArticle()`
6. **发送请求** → `PUT /api/articles/{id}`
7. **后端处理** → `ArticleController.updateArticle()`
8. **数据库更新** → 持久化存储
9. **返回结果** → 更新前端状态

## 🧪 测试验证步骤

### 1. **启动服务**
```bash
# 启动后端服务
cd deepseek-doctor
./start.sh  # 或 start.bat

# 启动前端服务
cd knowledge-frontend  
npm run dev
```

### 2. **测试创建文章**
1. 访问 `http://localhost:3000/article/edit`
2. 填写标题和内容
3. 点击"保存草稿"
4. 检查是否显示成功消息
5. 刷新页面，验证数据是否保存

### 3. **测试更新文章**
1. 在文章列表中点击编辑
2. 修改内容
3. 点击"保存草稿"
4. 验证修改是否保存

### 4. **验证数据库**
```sql
-- 检查文章表数据
SELECT * FROM article ORDER BY created_at DESC LIMIT 10;
```

## 🚀 关键修复点

### Before（问题状态）：
```typescript
const saveDraft = async () => {
  // ❌ 模拟调用，数据不会保存
  await new Promise(resolve => setTimeout(resolve, 1000))
  console.log('草稿已保存')  // 只是控制台输出
}
```

### After（修复后）：
```typescript
const saveDraft = async () => {
  try {
    // ✅ 真实API调用，数据持久化到数据库
    const articleData = { title, content, summary, ... }
    const result = await articlesStore.createArticle(articleData)
    showMessage('草稿保存成功')
  } catch (error) {
    showMessage('保存失败', true)
  }
}
```

## 🔧 调试技巧

### 1. **查看网络请求**
- 打开浏览器开发者工具
- 切换到 Network 标签
- 点击保存按钮
- 检查是否有 `POST /api/articles` 请求

### 2. **查看控制台错误**
```javascript
// 在浏览器控制台中检查
console.log('Articles Store:', useArticlesStore())
console.log('Current Articles:', useArticlesStore().articles)
```

### 3. **后端日志检查**
```bash
# 查看后端日志
tail -f deepseek-doctor/logs/application.log
```

## 📋 常见问题解决

### Q1: 保存后显示成功但刷新后数据消失
**A**: 检查后端数据库事务是否正确提交

### Q2: 网络请求显示404错误
**A**: 检查后端服务是否启动，API路径是否正确

### Q3: 显示权限错误
**A**: 检查用户是否已登录，token是否有效

### Q4: 数据格式错误
**A**: 检查前端发送的数据格式是否符合后端接口要求

## 🎯 总结

**原问题**：前端新增数据无法永久保存  
**根本原因**：前端只在内存中操作，没有调用后端API  
**解决方案**：补充完整的前后端数据交互流程  
**验证方法**：测试创建、编辑、删除功能是否正常工作  

修复后，前端新增的数据将会：
1. ✅ 真正保存到数据库
2. ✅ 刷新页面后依然存在  
3. ✅ 与数据库直接添加的数据行为一致
4. ✅ 支持完整的CRUD操作 