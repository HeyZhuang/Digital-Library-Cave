<template>
  <div class="creative-workshop min-h-screen bg-gradient-to-br from-purple-50 via-blue-50 to-indigo-50">
    <!-- 顶部导航栏 -->
    <header class="sticky top-0 z-50 bg-white/80 backdrop-blur-lg border-b border-gray-200/50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          <!-- 左侧 -->
          <div class="flex items-center space-x-4">
            <button
              @click="goBack"
              class="flex items-center space-x-2 px-3 py-2 text-gray-800 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-all duration-200 font-medium"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path>
              </svg>
              <span class="hidden sm:inline">返回</span>
            </button>
            <div class="flex items-center space-x-3">
              <div class="w-8 h-8 bg-gradient-to-r from-purple-500 to-blue-500 rounded-lg flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                </svg>
              </div>
              <div>
                <h1 class="text-lg font-bold text-gray-900 drop-shadow-sm">创作功坊</h1>
                <p class="text-sm text-gray-700 hidden sm:block font-medium">
                  {{ articleId ? '编辑文章' : '创建新文章' }}
                </p>
              </div>
            </div>
          </div>

          <!-- 中间状态指示器 -->
          <div class="hidden md:flex items-center space-x-4">
            <div class="flex items-center space-x-2 text-sm text-gray-700 font-medium">
              <div class="flex items-center space-x-1">
                <div :class="['w-2 h-2 rounded-full', autoSaveStatus === 'saved' ? 'bg-green-400' : autoSaveStatus === 'saving' ? 'bg-yellow-400' : 'bg-gray-300']"></div>
                <span>{{ autoSaveStatusText }}</span>
              </div>
              <span>•</span>
              <span>{{ wordCount }} 字</span>
              <span>•</span>
              <span>预计阅读 {{ readingTime }} 分钟</span>
            </div>
          </div>

          <!-- 右侧操作 -->
          <div class="flex items-center space-x-3">
            <button
              @click="() => saveDraft()"
              :disabled="isSaving"
              class="px-4 py-2 text-sm font-semibold text-gray-800 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 transition-all duration-200"
            >
              {{ isSaving ? '保存中...' : '保存草稿' }}
            </button>
            <button
              @click="showPublishModal = true"
              :disabled="!canPublish || isSaving"
              class="px-4 py-2 text-sm font-semibold text-white bg-gradient-to-r from-purple-500 to-blue-500 hover:from-purple-600 hover:to-blue-600 rounded-lg disabled:opacity-50 transition-all duration-200"
            >
              发布文章
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- 消息提示 -->
    <div v-if="notification.show" class="fixed top-20 right-4 z-40">
      <div :class="[
        'p-4 rounded-lg shadow-lg',
        notification.type === 'success' ? 'bg-green-100 text-green-800' :
        notification.type === 'error' ? 'bg-red-100 text-red-800' :
        notification.type === 'warning' ? 'bg-yellow-100 text-yellow-800' :
        'bg-blue-100 text-blue-800'
      ]">
        {{ notification.message }}
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
        <!-- 左侧工具栏 -->
        <aside class="lg:col-span-1">
          <div class="space-y-6">
            <!-- 文章信息 -->
            <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
              <h3 class="text-sm font-bold text-gray-900 mb-3 flex items-center drop-shadow-sm">
                <svg class="w-4 h-4 mr-2 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                文章信息
              </h3>
              <div class="space-y-3">
                <div>
                  <label class="block text-xs font-bold text-gray-800 mb-1">标题</label>
                  <input
                    v-model="article.title"
                    type="text"
                    placeholder="请输入文章标题"
                    class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                  />
                </div>
                <div>
                  <label class="block text-xs font-bold text-gray-800 mb-1">分类</label>
                  <select
                    v-model="article.categoryId"
                    class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                  >
                    <option value="">选择分类</option>
                    <option v-for="category in categoriesStore.categories" :key="category.id" :value="category.id">
                      {{ category.name }}
                    </option>
                  </select>
                </div>
                <div>
                  <label class="block text-xs font-bold text-gray-800 mb-1">摘要</label>
                  <textarea
                    v-model="article.summary"
                    rows="3"
                    placeholder="请输入文章摘要"
                    class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                  ></textarea>
                </div>
              </div>
            </div>

            <!-- 标签管理 -->
            <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
              <h3 class="text-sm font-bold text-gray-900 mb-3 drop-shadow-sm">标签</h3>
              <div class="space-y-2">
                <div class="flex flex-wrap gap-1">
                  <span
                    v-for="tag in article.tags"
                    :key="tag"
                    class="inline-flex items-center px-2 py-1 text-xs bg-purple-100 text-purple-800 rounded-full font-medium"
                  >
                    {{ tag }}
                    <button
                      @click="removeTag(tag)"
                      class="ml-1 text-purple-600 hover:text-purple-800 font-bold"
                    >
                      ×
                    </button>
                  </span>
                </div>
                <div class="flex space-x-1">
                  <input
                    v-model="newTag"
                    @keyup.enter="addTag"
                    type="text"
                    placeholder="添加标签"
                    class="flex-1 px-2 py-1 text-xs border border-gray-300 rounded"
                  />
                  <button
                    @click="addTag"
                    class="px-2 py-1 text-xs bg-purple-500 text-white rounded hover:bg-purple-600 font-semibold"
                  >
                    +
                  </button>
                </div>
              </div>
            </div>

            <!-- 发布设置 -->
            <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
              <h3 class="text-sm font-bold text-gray-900 mb-3 drop-shadow-sm">设置</h3>
              <div class="space-y-2">
                <label class="flex items-center text-sm">
                  <input
                    v-model="article.allowComments"
                    type="checkbox"
                    class="rounded border-gray-300 text-purple-600 focus:ring-purple-500"
                  />
                  <span class="ml-2 text-black font-medium">允许评论</span>
                </label>
                <label class="flex items-center text-sm">
                  <input
                    v-model="article.isTop"
                    type="checkbox"
                    class="rounded border-gray-300 text-purple-600 focus:ring-purple-500"
                  />
                <span class="ml-2 text-black font-medium">设为置顶</span>
                </label>
              </div>
            </div>
          </div>
        </aside>

        <!-- 主编辑区域 -->
        <main class="lg:col-span-3">
          <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
            <!-- 编辑器头部 -->
            <div class="border-b border-gray-200 p-4">
              <div class="flex items-center justify-between">
                <div class="flex items-center space-x-1">
                  <button
                    v-for="mode in editorModes"
                    :key="mode.value"
                    @click="editorMode = mode.value"
                    :class="[
                      'px-3 py-1 text-sm font-medium rounded-lg transition-all duration-200',
                      editorMode === mode.value
                        ? 'bg-purple-100 text-purple-700 font-semibold'
                        : 'text-gray-800 hover:bg-gray-100 font-medium'
                    ]"
                  >
                    {{ mode.label }}
                  </button>
                </div>
                <div class="flex items-center space-x-4 text-sm text-gray-700 font-medium">
                  <span>{{ charCount }} 字符</span>
                  <span>{{ lineCount }} 行</span>
                </div>
              </div>
            </div>

            <!-- 编辑器内容 -->
            <div class="relative" style="height: 600px;">
              <!-- 纯编辑模式 -->
              <div
                v-if="editorMode === 'edit'"
                class="h-full"
              >
                <textarea
                  ref="editorRef"
                  v-model="article.content"
                  @input="handleContentChange"
                  placeholder="开始编写你的故事..."
                  class="w-full h-full p-6 border-0 resize-none focus:ring-0 focus:outline-none font-mono text-sm leading-relaxed text-gray-900"
                ></textarea>
              </div>

              <!-- 纯预览模式 -->
              <div
                v-else-if="editorMode === 'preview'"
                class="h-full overflow-y-auto p-6 prose prose-sm max-w-none"
                v-html="renderedContent"
              ></div>

              <!-- 分屏模式 -->
              <div
                v-else
                class="h-full flex"
              >
                <div class="w-1/2 border-r border-gray-200">
                  <textarea
                    ref="editorRef"
                    v-model="article.content"
                    @input="handleContentChange"
                    placeholder="开始编写你的故事..."
                    class="w-full h-full p-6 border-0 resize-none focus:ring-0 focus:outline-none font-mono text-sm leading-relaxed text-gray-900"
                  ></textarea>
                </div>
                <div
                  class="w-1/2 overflow-y-auto p-6 prose prose-sm max-w-none"
                  v-html="renderedContent"
                ></div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>

    <!-- 发布模态框 -->
    <div
      v-if="showPublishModal"
      class="fixed inset-0 z-50 overflow-y-auto"
    >
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75" @click="showPublishModal = false"></div>
        <div class="relative bg-white rounded-lg p-6 max-w-md w-full">
          <h3 class="text-lg font-bold text-gray-900 mb-4 drop-shadow-sm">发布文章</h3>
          <p class="text-sm text-gray-700 mb-6 font-medium">确定要发布这篇文章吗？发布后所有用户都可以看到。</p>
          <div class="flex space-x-3">
            <button
              @click="publishArticle"
              :disabled="isPublishing"
              class="flex-1 px-4 py-2 bg-purple-500 text-white rounded-lg hover:bg-purple-600 disabled:opacity-50 font-semibold"
            >
              {{ isPublishing ? '发布中...' : '确认发布' }}
            </button>
            <button
              @click="showPublishModal = false"
              class="flex-1 px-4 py-2 border border-gray-300 text-gray-800 rounded-lg hover:bg-gray-50 font-medium"
            >
              取消
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { marked } from 'marked'
import { useArticlesStore } from '../stores/articles'
import type { CreateArticleRequest, UpdateArticleRequest } from '../api/articles'
import { tagApi } from '../api/tags'
import { useCategoriesStore } from '../stores/categories'

const route = useRoute()
const router = useRouter()
const articlesStore = useArticlesStore()
const categoriesStore = useCategoriesStore()

// 响应式数据
const articleId = computed(() => route.params.id ? Number(route.params.id) : null)
const editorRef = ref<HTMLTextAreaElement | null>(null)

const article = ref({
  title: '',
  content: '',
  summary: '',
  categoryId: '',
  tags: [] as string[],
  allowComments: true,
  isTop: false
})

const editorMode = ref<'edit' | 'preview' | 'split'>('split')
const newTag = ref('')
const isSaving = ref(false)
const isPublishing = ref(false)
const showPublishModal = ref(false)
const autoSaveStatus = ref<'saved' | 'saving' | 'unsaved'>('saved')
const notification = ref({ 
  show: false, 
  message: '', 
  type: 'success' as 'success' | 'error' | 'info' | 'warning' 
})

const editorModes = [
  { value: 'edit' as const, label: '编辑' },
  { value: 'preview' as const, label: '预览' },
  { value: 'split' as const, label: '分屏' }
]

// 计算属性
const wordCount = computed(() => article.value.content.replace(/\s/g, '').length)
const charCount = computed(() => article.value.content.length)
const lineCount = computed(() => article.value.content.split('\n').length)
const readingTime = computed(() => Math.ceil(wordCount.value / 200))

const canPublish = computed(() => {
  return article.value.title.trim() && article.value.content.trim()
})

const autoSaveStatusText = computed(() => {
  switch (autoSaveStatus.value) {
    case 'saved': return '已保存'
    case 'saving': return '保存中...'
    case 'unsaved': return '未保存'
    default: return ''
  }
})

const renderedContent = computed(() => {
  if (!article.value.content) return '<p class="text-gray-400 italic">开始编写内容，预览将在此显示...</p>'
  
  marked.setOptions({
    breaks: true,
    gfm: true
  })
  
  return marked(article.value.content) as string
})

// 自动保存定时器
let autoSaveTimer: number | null = null

// 方法
const goBack = () => {
  router.back()
}

const addTag = () => {
  const tag = newTag.value.trim()
  if (tag && !article.value.tags.includes(tag)) {
    article.value.tags.push(tag)
    newTag.value = ''
  }
}

const removeTag = (tag: string) => {
  const index = article.value.tags.indexOf(tag)
  if (index > -1) {
    article.value.tags.splice(index, 1)
  }
}

const handleContentChange = () => {
  autoSaveStatus.value = 'unsaved'
  
  if (autoSaveTimer) {
    clearTimeout(autoSaveTimer)
  }
  
  autoSaveTimer = setTimeout(() => {
    autoSave()
  }, 3000)
}

const autoSave = async () => {
  if (!canPublish.value) return
  
  autoSaveStatus.value = 'saving'
  try {
    await saveDraft(true)
    autoSaveStatus.value = 'saved'
  } catch (error) {
    autoSaveStatus.value = 'unsaved'
    console.error('自动保存失败:', error)
  }
}

const showNotification = (message: string, type: 'success' | 'error' | 'info' | 'warning' = 'success') => {
  notification.value = { show: true, message, type }
  setTimeout(() => {
    notification.value.show = false
  }, 3000)
}

const saveDraft = async (silent = false) => {
  if (!canPublish.value) {
    if (!silent) showNotification('请填写标题和内容', 'error')
    return
  }

  isSaving.value = true
  try {
    const categoryId = article.value.categoryId ? Number(article.value.categoryId) : undefined
    
    const articleData: CreateArticleRequest | UpdateArticleRequest = {
      title: article.value.title,
      content: article.value.content,
      summary: article.value.summary,
      categoryId: categoryId,
      status: 0,
      isTop: article.value.isTop,
      allowComments: article.value.allowComments,
      tags: article.value.tags
    }

    if (articleId.value) {
      await articlesStore.updateArticle({ ...articleData, id: articleId.value })
      if (!silent) showNotification('草稿更新成功')
    } else {
      const newArticle = await articlesStore.createArticle(articleData)
      if (!silent) showNotification('草稿保存成功')
      router.replace(`/workshop/edit/${newArticle.id}`)
    }
    
    autoSaveStatus.value = 'saved'
  } catch (err: any) {
    console.error('保存草稿失败:', err)
    if (!silent) showNotification(err.message || '保存草稿失败', 'error')
    autoSaveStatus.value = 'unsaved'
  } finally {
    isSaving.value = false
  }
}

const publishArticle = async () => {
  if (!canPublish.value) {
    showNotification('请填写标题和内容', 'error')
    return
  }
  
  isPublishing.value = true
  try {
    await saveDraft(true)
    
    let targetId = articleId.value
    if (!targetId) {
      const currentPath = router.currentRoute.value.path
      const match = currentPath.match(/\/workshop\/edit\/(\d+)/)
      if (match) {
        targetId = Number(match[1])
      }
    }
    
    if (targetId) {
      const success = await articlesStore.publishArticle(targetId)
      if (success) {
        showNotification('文章发布成功', 'success')
        showPublishModal.value = false
        setTimeout(() => {
          router.push('/articles')
        }, 1500)
      } else {
        showNotification('文章发布失败', 'error')
      }
    } else {
      showNotification('无法获取文章ID，发布失败', 'error')
    }
  } catch (err: any) {
    console.error('发布文章失败:', err)
    showNotification(err.message || '发布文章失败', 'error')
  } finally {
    isPublishing.value = false
  }
}

const loadArticle = async (id: number) => {
  try {
    const articleData = await articlesStore.getArticleById(id)
    if (articleData) {
      // 加载文章标签
      const tagResponse = await tagApi.getTagsByArticleId(id)
      const articleTags = tagResponse.code === 200 ? tagResponse.data.map(tag => tag.name) : []
      
      article.value = {
        title: articleData.title,
        content: articleData.content,
        summary: articleData.summary || '',
        categoryId: articleData.categoryId?.toString() || '',
        tags: articleTags,
        allowComments: articleData.allowComments ?? true,
        isTop: articleData.isTop ?? false
      }
      autoSaveStatus.value = 'saved'
    }
  } catch (err: any) {
    console.error('加载文章失败:', err)
    showNotification('加载文章失败', 'error')
  }
}

onMounted(async () => {
  // 初始化分类数据
  await categoriesStore.initializeData()
  
  // 如果是编辑模式，加载文章数据
  if (articleId.value) {
    await loadArticle(articleId.value)
  }
})

onUnmounted(() => {
  if (autoSaveTimer) {
    clearTimeout(autoSaveTimer)
  }
})
</script>

<style scoped>
/* 增强字体对比度和可读性 */
.creative-workshop {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* 输入框和文本区域增强 */
input, textarea, select {
  color: #111827 !important;
  font-weight: 500;
}

input::placeholder, textarea::placeholder {
  color: #6b7280 !important;
  font-weight: 400;
}

/* 按钮文字增强 */
button {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 标签文字增强 */
label {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* 卡片标题增强 */
h1, h2, h3 {
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 通知消息增强 */
.notification {
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.prose {
  color: #111827;
  max-width: none;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.prose h1, .prose h2, .prose h3 {
  color: #000000;
  font-weight: 700;
  margin-bottom: 0.5rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.prose p {
  margin-bottom: 1rem;
  line-height: 1.75;
  color: #1f2937;
  font-weight: 500;
}

.prose code {
  color: #dc2626;
  background-color: #f3f4f6;
  padding: 0.125rem 0.25rem;
  border-radius: 0.25rem;
  font-weight: 600;
  text-shadow: none;
}

.prose pre {
  background-color: #1f2937;
  color: #f9fafb;
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
  margin: 1rem 0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.prose strong {
  color: #000000;
  font-weight: 700;
}

.prose em {
  color: #374151;
  font-style: italic;
}

.prose blockquote {
  color: #4b5563;
  border-left: 4px solid #d1d5db;
  padding-left: 1rem;
  font-style: italic;
  font-weight: 500;
}

.prose ul, .prose ol {
  color: #1f2937;
}

.prose li {
  font-weight: 500;
}

/* 链接样式增强 */
.prose a {
  color: #3b82f6;
  font-weight: 600;
  text-decoration: underline;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.prose a:hover {
  color: #1d4ed8;
}

/* 表格样式增强 */
.prose table {
  border-collapse: collapse;
  width: 100%;
}

.prose th, .prose td {
  border: 1px solid #d1d5db;
  padding: 0.5rem 1rem;
  text-align: left;
  font-weight: 500;
}

.prose th {
  background-color: #f9fafb;
  font-weight: 700;
  color: #111827;
}

/* 滚动条样式 */
.creative-workshop ::-webkit-scrollbar {
  width: 6px;
}

.creative-workshop ::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.creative-workshop ::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.creative-workshop ::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 状态指示器文字增强 */
.status-indicator {
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 编辑器模式按钮文字增强 */
.editor-mode-btn {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 标签样式增强 */
.tag-item {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  font-weight: 600;
}

/* 复选框标签增强 */
.checkbox-label {
  color: #1f2937 !important;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* 状态文字增强 */
.text-gray-500 {
  color: #6b7280 !important;
  font-weight: 500;
}

.text-gray-600 {
  color: #4b5563 !important;
  font-weight: 500;
}

.text-gray-700 {
  color: #374151 !important;
  font-weight: 600;
}

/* 选项标签增强 */
option {
  color: #111827 !important;
  font-weight: 500;
}
</style> 