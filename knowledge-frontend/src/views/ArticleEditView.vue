<template>
  <div class="max-w-6xl mx-auto p-6">
    <!-- 消息提示 -->
    <div v-if="success" class="mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded-lg">
      {{ success }}
    </div>
    <div v-if="error" class="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
      {{ error }}
    </div>

    <!-- 头部操作栏 -->
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center space-x-4">
        <button
          @click="$router.back()"
          class="flex items-center space-x-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path>
          </svg>
          <span>返回</span>
        </button>
        <h1 class="text-2xl font-bold text-gray-900 drop-shadow-sm">
          {{ articleId ? '编辑文章' : '创建文章' }}
        </h1>
      </div>
      <div class="flex items-center space-x-3">
        <button
          @click="saveDraft"
          :disabled="isSaving"
          class="px-4 py-2 text-sm font-medium text-gray-800 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition disabled:opacity-50 font-semibold"
        >
          {{ isSaving ? '保存中...' : '保存草稿' }}
        </button>
        <button
          @click="publishArticle"
          :disabled="isSaving || !canPublish"
          class="px-4 py-2 text-sm font-medium text-white bg-primary-600 border border-transparent rounded-lg hover:bg-primary-700 transition disabled:opacity-50"
        >
          {{ isSaving ? '发布中...' : '发布文章' }}
        </button>
      </div>
    </div>

    <!-- 文章基本信息 -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label for="title" class="block text-sm font-semibold text-gray-800 mb-2">文章标题</label>
          <input
            id="title"
            v-model="article.title"
            type="text"
            placeholder="请输入文章标题"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          />
        </div>
        <div>
          <label for="category" class="block text-sm font-semibold text-gray-800 mb-2">分类</label>
          <select
            id="category"
            v-model="article.categoryId"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          >
            <option value="">选择分类</option>
            <option v-for="category in categoriesStore.categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
        </div>
        <div class="md:col-span-2">
          <label for="tags" class="block text-sm font-semibold text-gray-800 mb-2">标签</label>
          <div class="flex flex-wrap gap-2 mb-2">
            <span
              v-for="tag in article.tags"
              :key="tag"
              class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-primary-100 text-primary-800"
            >
              {{ tag }}
              <button
                @click="removeTag(tag)"
                class="ml-2 text-primary-600 hover:text-primary-800"
              >
                ×
              </button>
            </span>
          </div>
          <div class="flex space-x-2">
            <input
              v-model="newTag"
              @keyup.enter="addTag"
              type="text"
              placeholder="输入标签，按回车添加"
              class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
            />
            <button
              @click="addTag"
              class="px-4 py-2 text-sm font-medium text-white bg-primary-600 rounded-lg hover:bg-primary-700 transition"
            >
              添加
            </button>
          </div>
        </div>
        <div class="md:col-span-2">
          <label for="summary" class="block text-sm font-semibold text-gray-800 mb-2">摘要</label>
          <textarea
            id="summary"
            v-model="article.summary"
            rows="3"
            placeholder="请输入文章摘要（可选）"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          ></textarea>
        </div>
      </div>
    </div>

    <!-- 编辑器区域 -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      <!-- 编辑器工具栏 -->
      <div class="border-b border-gray-200 p-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-2">
            <button
              v-for="mode in editorModes"
              :key="mode.value"
              @click="editorMode = mode.value"
              :class="[
                'px-3 py-1 text-sm font-medium rounded-lg transition',
                editorMode === mode.value
                  ? 'bg-primary-100 text-primary-700'
                  : 'text-gray-800 hover:bg-gray-100 font-medium'
              ]"
            >
              {{ mode.label }}
            </button>
          </div>
          <div class="flex items-center space-x-4 text-sm text-gray-700 font-medium">
            <span>字数: {{ wordCount }}</span>
            <span>字符: {{ charCount }}</span>
          </div>
        </div>
      </div>

      <!-- 编辑器内容区 -->
      <div class="relative" style="height: 600px;">
        <!-- 编辑模式 -->
        <div
          v-if="editorMode === 'edit'"
          class="h-full"
        >
          <textarea
            v-model="article.content"
            placeholder="开始编写你的文章内容..."
            class="w-full h-full p-4 border-0 resize-none focus:ring-0 focus:outline-none font-mono text-sm"
          ></textarea>
        </div>

        <!-- 预览模式 -->
        <div
          v-else-if="editorMode === 'preview'"
          class="h-full overflow-y-auto p-4 prose prose-sm max-w-none"
          v-html="renderedContent"
        ></div>

        <!-- 分屏模式 -->
        <div
          v-else
          class="h-full flex"
        >
          <div class="w-1/2 border-r border-gray-200">
            <textarea
              v-model="article.content"
              placeholder="开始编写你的文章内容..."
              class="w-full h-full p-4 border-0 resize-none focus:ring-0 focus:outline-none font-mono text-sm"
            ></textarea>
          </div>
          <div class="w-1/2 overflow-y-auto p-4 prose prose-sm max-w-none" v-html="renderedContent"></div>
        </div>
      </div>
    </div>

    <!-- 发布设置 -->
    <div class="mt-6 bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h3 class="text-lg font-semibold text-gray-900 mb-4 drop-shadow-sm">发布设置</h3>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div>
          <label class="flex items-center">
            <input
              v-model="article.isPublished"
              type="checkbox"
              class="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
            />
            <span class="ml-2 text-sm text-gray-800 font-medium">立即发布</span>
          </label>
        </div>
        <div>
          <label class="flex items-center">
            <input
              v-model="article.allowComments"
              type="checkbox"
              class="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
            />
            <span class="ml-2 text-sm text-gray-800 font-medium">允许评论</span>
          </label>
        </div>
        <div>
          <label class="flex items-center">
            <input
              v-model="article.isTop"
              type="checkbox"
              class="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
            />
            <span class="ml-2 text-sm text-gray-800 font-medium">置顶文章</span>
          </label>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { marked } from 'marked'
import hljs from 'highlight.js'
import { useArticlesStore } from '../stores/articles'
import type { CreateArticleRequest, UpdateArticleRequest } from '../api/articles'
import { tagApi } from '../api/tags'
import { useCategoriesStore } from '../stores/categories'

const route = useRoute()
const router = useRouter()
const articlesStore = useArticlesStore()
const categoriesStore = useCategoriesStore()

const articleId = computed(() => route.params.id ? Number(route.params.id) : null)

// 文章数据
const article = ref({
  title: '',
  content: '',
  summary: '',
  categoryId: '' as string | number,
  tags: [] as string[],
  isPublished: false,
  allowComments: true,
  isTop: false
})

// 编辑器状态
const editorMode = ref<'edit' | 'preview' | 'split'>('split')
const newTag = ref('')
const isSaving = ref(false)
const error = ref('')
const success = ref('')

const editorModes = [
  { value: 'edit' as const, label: '编辑' },
  { value: 'preview' as const, label: '预览' },
  { value: 'split' as const, label: '分屏' }
]

// 计算属性
const wordCount = computed(() => {
  return article.value.content.replace(/\s/g, '').length
})

const charCount = computed(() => {
  return article.value.content.length
})

const canPublish = computed(() => {
  return article.value.title.trim() && article.value.content.trim()
})

const renderedContent = computed(() => {
  if (!article.value.content) return ''
  
  // 配置marked
  marked.setOptions({
    breaks: true,
    gfm: true
  })
  
  // 处理代码高亮
  let html = marked(article.value.content) as string
  
  // 简单的代码高亮处理
  html = html.replace(/<pre><code class="language-(\w+)">([\s\S]*?)<\/code><\/pre>/g, (match, lang, code) => {
    if (lang && hljs.getLanguage(lang)) {
      try {
        const highlighted = hljs.highlight(code, { language: lang }).value
        return `<pre><code class="language-${lang} hljs">${highlighted}</code></pre>`
      } catch (e) {
        console.error('Highlight error:', e)
      }
    }
    return match
  })
  
  return html
})

// 方法
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

const showMessage = (msg: string, isError = false) => {
  if (isError) {
    error.value = msg
    success.value = ''
  } else {
    success.value = msg
    error.value = ''
  }
  // 3秒后清除消息
  setTimeout(() => {
    error.value = ''
    success.value = ''
  }, 3000)
}

const saveDraft = async () => {
  if (!canPublish.value) {
    showMessage('请填写标题和内容', true)
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
      status: 0, // 草稿状态
      isTop: article.value.isTop,
      allowComments: article.value.allowComments,
      tags: article.value.tags
    }

    if (articleId.value) {
      // 更新现有文章
      await articlesStore.updateArticle({ ...articleData, id: articleId.value })
      showMessage('草稿更新成功')
    } else {
      // 创建新文章
      const newArticle = await articlesStore.createArticle(articleData)
      showMessage('草稿保存成功')
      // 跳转到编辑页面（带ID）
      router.replace(`/article/edit/${newArticle.id}`)
    }
  } catch (err: any) {
    console.error('保存草稿失败:', err)
    showMessage(err.message || '保存草稿失败', true)
  } finally {
    isSaving.value = false
  }
}

const publishArticle = async () => {
  if (!canPublish.value) {
    showMessage('请填写标题和内容', true)
    return
  }
  
  isSaving.value = true
  try {
    let articleToPublish: any = null
    
    if (articleId.value) {
      // 编辑现有文章
      const categoryId = article.value.categoryId ? Number(article.value.categoryId) : undefined
      const articleData: UpdateArticleRequest = {
        id: articleId.value,
        title: article.value.title,
        content: article.value.content,
        summary: article.value.summary,
        categoryId: categoryId,
        status: 0, // 先保存为草稿
        isTop: article.value.isTop,
        allowComments: article.value.allowComments,
        tags: article.value.tags
      }
      
      // 更新文章
      await articlesStore.updateArticle(articleData)
      articleToPublish = { id: articleId.value }
    } else {
      // 创建新文章
      const categoryId = article.value.categoryId ? Number(article.value.categoryId) : undefined
      const articleData: CreateArticleRequest = {
        title: article.value.title,
        content: article.value.content,
        summary: article.value.summary,
        categoryId: categoryId,
        status: 0, // 先保存为草稿
        isTop: article.value.isTop,
        allowComments: article.value.allowComments,
        tags: article.value.tags
      }
      
      // 创建文章
      articleToPublish = await articlesStore.createArticle(articleData)
    }
    
    // 发布文章
    if (articleToPublish?.id) {
      const publishSuccess = await articlesStore.publishArticle(articleToPublish.id)
      if (publishSuccess) {
        showMessage('文章发布成功')
        // 延迟跳转到文章列表
        setTimeout(() => {
          router.push('/articles')
        }, 1500)
      } else {
        showMessage('文章发布失败', true)
      }
    } else {
      showMessage('无法获取文章ID，发布失败', true)
    }
  } catch (err: any) {
    console.error('发布文章失败:', err)
    showMessage(err.message || '发布文章失败', true)
  } finally {
    isSaving.value = false
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
          isPublished: articleData.status === 1,
          allowComments: articleData.allowComments ?? true,
          isTop: articleData.isTop ?? false
        }
    }
  } catch (err: any) {
    console.error('加载文章失败:', err)
    showMessage('加载文章失败', true)
  }
}

// 生命周期
onMounted(async () => {
  // 初始化分类数据
  await categoriesStore.initializeData()
  
  // 如果是编辑模式，加载文章数据
  if (articleId.value) {
    loadArticle(articleId.value)
  }
})
</script>

<style>
/* 代码高亮样式 */
.prose pre {
  @apply bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto;
}

.prose code {
  @apply bg-gray-100 px-1 py-0.5 rounded text-sm;
}

.prose pre code {
  @apply bg-transparent p-0;
}

/* 增强字体对比度 */
.max-w-6xl {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* 输入框样式增强 */
input, textarea, select {
  color: #1f2937 !important;
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
</style> 