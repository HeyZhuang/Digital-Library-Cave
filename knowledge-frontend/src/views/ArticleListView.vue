<template>
  <div class="article-list">
    <!-- 页面标题 -->
    <div class="mb-8 flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-yellow-500 mb-2">文章归档</h1>
        <p class="text-gray-800 font-medium">
        <span v-if="!loading && !isLoading" class="text-yellow-500">共 {{ totalArticles }} 篇文章</span>
          <span v-else-if="loading || isLoading">正在加载...</span>
          <span v-else-if="error">加载失败</span>
        </p>
      </div>
      <RouterLink
        to="/article/edit"
        class="flex items-center px-4 py-2 text-sm font-medium text-white bg-primary-600 rounded-lg hover:bg-primary-700 transition-colors shadow-sm"
      >
        <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
        </svg>
        新建文章
      </RouterLink>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading || isLoading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
      <p class="mt-4 text-gray-600">正在加载文章...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="text-center py-12">
      <svg class="w-16 h-16 mx-auto text-red-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
      </svg>
      <h3 class="text-lg font-semibold text-gray-900 mb-2">加载失败</h3>
      <p class="text-gray-600 mb-4">{{ error }}</p>
      <button 
        @click="retryLoad"
        class="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
      >
        重试
      </button>
    </div>

    <!-- 年份归档 -->
    <div v-else-if="!loading && !isLoading && !error" class="space-y-8">
      <div
        v-for="(year, idx) in Object.keys(articlesByYear).sort((a, b) => Number(b) - Number(a))"
        :key="year"
        class="year-section"
      >
        <!-- 年份标题 -->
        <div class="flex items-center mb-6">
          <h2 class="text-2xl font-bold text-yellow-500">{{ year }}年</h2>
          <span class="ml-3 badge badge-secondary">{{ articlesByYear[year].length }}篇</span>
        </div>

        <!-- 文章列表 -->
        <div class="space-y-4">
          <div
            v-for="article in articlesByYear[year]"
            :key="article.id"
            class="card hover:shadow-xl transition-shadow cursor-pointer"
            @click="navigateToArticle(article.id)"
          >
            <div class="flex flex-col md:flex-row md:items-center space-y-4 md:space-y-0 md:space-x-6">
              <!-- 封面图片 (暂时隐藏，因为Article接口中没有coverImage字段) -->
              <!-- <div v-if="article.coverImage" class="md:w-32 md:h-20 flex-shrink-0">
                <img
                  :src="article.coverImage"
                  :alt="article.title"
                  class="w-full h-40 md:h-20 object-cover rounded-lg"
                  loading="lazy"
                />
              </div> -->

              <!-- 文章信息 -->
              <div class="flex-1 space-y-3">
                <!-- 标题和分类 -->
                <div>
              <h3 class="text-lg font-semibold text-orange-500 mb-1 line-clamp-2 
            hover:text-orange-600 transition-colors duration-300 
            hover:underline hover:shadow-sm">
  {{ article.title }}
</h3>
                  <span class="category-badge">{{ article.categoryName || '未分类' }}</span>
                </div>

                <!-- 摘要 -->
                <p class="text-orange-800 text-sm leading-relaxed line-clamp-2 font-medium">
                  {{ article.summary || truncateContent(article.content, 150) }}
                </p>

                <!-- 标签 (暂时隐藏，Article接口中没有tags字段) -->
                <!-- <div class="flex flex-wrap gap-1">
                  <span
                    v-for="tag in article.tags"
                    :key="tag"
                    class="badge badge-secondary text-xs"
                  >
                    {{ tag }}
                  </span>
                </div> -->
              </div>

              <!-- 元数据 -->
              <div class="md:w-40 flex-shrink-0 text-right space-y-2">
                <div class="flex items-center justify-end text-xs text-gray-600 font-medium">
                  <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                  </svg>
                  {{ formatNumber(article.views || 0) }}
                </div>
                <div class="text-xs text-gray-600 font-medium">
                  {{ formatDate(article.createdAt || '') }}
                </div>
                <div v-if="article.updatedAt !== article.createdAt" class="text-xs text-gray-600 font-medium">
                  更新: {{ formatDate(article.updatedAt || '') }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!loading && !isLoading && !error && totalArticles === 0" class="text-center py-12">
      <svg class="w-16 h-16 mx-auto text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
      </svg>
      <h3 class="text-lg font-semibold text-gray-900 mb-2 drop-shadow-sm">暂无文章</h3>
      <p class="text-gray-700 font-medium">还没有发布任何文章</p>
      <RouterLink
        to="/article/edit"
        class="inline-flex items-center mt-4 px-4 py-2 text-sm font-medium text-white bg-primary-600 rounded-lg hover:bg-primary-700 transition-colors"
      >
        <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
        </svg>
        创建第一篇文章
      </RouterLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useArticlesStore } from '../stores/articles'

const router = useRouter()
const articlesStore = useArticlesStore()

// 响应式状态
const isLoading = ref(false)
const error = ref<string | null>(null)

const articlesByYear = computed(() => articlesStore.articlesByYear)
const totalArticles = computed(() =>
  Object.values(articlesByYear.value).reduce((sum, arr) => sum + arr.length, 0)
)
const loading = computed(() => articlesStore.loading)

const navigateToArticle = (id: number | undefined) => {
  if (id) {
    router.push(`/article/${id}`)
  }
}

const truncateContent = (content: string | undefined, maxLength: number): string => {
  // 防护性检查：确保content存在且为字符串
  if (!content || typeof content !== 'string') {
    return '暂无内容...'
  }
  if (content.length <= maxLength) return content
  return content.substring(0, maxLength) + '...'
}

const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

const formatDate = (dateString: string | undefined): string => {
  // 防护性检查：确保dateString存在
  if (!dateString) {
    return '未知时间'
  }
  try {
    const date = new Date(dateString)
    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      return '无效日期'
    }
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'numeric',
      day: 'numeric'
    })
  } catch (error) {
    console.error('日期格式化错误:', error)
    return '格式错误'
  }
}

onMounted(async () => {
  try {
    isLoading.value = true
    error.value = null
    await articlesStore.initializeData()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载文章失败'
    console.error('Failed to load articles:', err)
  } finally {
    isLoading.value = false
  }
})

// 重试加载函数
const retryLoad = async () => {
  try {
    isLoading.value = true
    error.value = null
    await articlesStore.initializeData()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载文章失败'
    console.error('Failed to retry load articles:', err)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 增强字体对比度 */
.article-list {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* 卡片标题增强 */
.card h3 {
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  font-weight: 600;
}

/* 摘要文字增强 */
.card p {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* 元数据文字增强 */
.card .text-xs {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 年份标题增强 */
.year-section h2 {
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 按钮文字增强 */
a, button {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 空状态文字增强 */
.text-center h3, .text-center p {
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 分类标签样式 - 确保在各种背景下都有良好对比度 */
.category-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.375rem 0.75rem;
  font-size: 0.75rem;
  font-weight: 600;
  border-radius: 9999px;
  transition: all 0.2s ease-in-out;
  white-space: nowrap;
  
  /* 高对比度配色 - 深蓝背景金色文字 */
  background: linear-gradient(135deg, #1e40af, #2563eb);
  color: #ffd700;
  border: 1px solid rgba(255, 215, 0, 0.3);
  box-shadow: 
    0 1px 3px rgba(0, 0, 0, 0.1),
    0 0 8px rgba(255, 215, 0, 0.2);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.category-badge:hover {
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  border-color: rgba(255, 215, 0, 0.5);
  box-shadow: 
    0 2px 6px rgba(0, 0, 0, 0.15),
    0 0 12px rgba(255, 215, 0, 0.3);
  transform: translateY(-1px);
}

/* 暗色主题下的样式调整 */
@media (prefers-color-scheme: dark) {
  .category-badge {
    background: linear-gradient(135deg, #fbbf24, #f59e0b);
    color: #1f2937;
    border-color: rgba(31, 41, 55, 0.3);
    text-shadow: 0 1px 2px rgba(255, 255, 255, 0.3);
  }
  
  .category-badge:hover {
    background: linear-gradient(135deg, #fcd34d, #fbbf24);
    border-color: rgba(31, 41, 55, 0.5);
  }
}

/* 高对比度模式 */
@media (prefers-contrast: high) {
  .category-badge {
    background: #000000;
    color: #ffffff;
    border: 2px solid #ffffff;
    text-shadow: none;
    box-shadow: none;
  }
  
  .category-badge:hover {
    background: #1a1a1a;
    border-color: #cccccc;
  }
}
</style> 