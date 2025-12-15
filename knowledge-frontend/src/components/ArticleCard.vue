<template>
  <div class="card hover:shadow-xl transition-shadow cursor-pointer" @click="navigateToArticle">
    <!-- 文章内容 -->
    <div class="space-y-3">
      <!-- 标题 -->
      <h3 class="text-2xl font-bold text-yellow-500 mb-2">
        {{ article.title || '无标题' }}
      </h3>

      <!-- 分类标签 -->
      <div class="flex items-center space-x-2">
        <span class="category-badge">{{ article.categoryName || '未分类' }}</span>
      </div>

      <!-- 摘要 -->
      <p class="text-gray-700 text-sm leading-relaxed line-clamp-3">
        {{ article.summary || truncateContent(article.content, 100) }}
      </p>

      <!-- 元数据 -->
      <div class="flex items-center justify-between text-xs text-gray-600 pt-2 border-t border-gray-100">
        <div class="flex items-center space-x-4">
          <span v-if="article.views" class="flex items-center">
            <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
            </svg>
            {{ formatNumber(article.views) }}
          </span>
          <span v-if="article.createdAt" class="flex items-center">
            <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
            </svg>
            {{ formatDate(article.createdAt) }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { Article } from '@/api/articles'

interface Props {
  article: Article
}

const props = defineProps<Props>()
const router = useRouter()
const navigating = ref(false)

const navigateToArticle = async () => {
  if (navigating.value) return // 防止重复点击
  
  try {
    navigating.value = true
    await router.push(`/article/${props.article.id}`)
  } catch (error) {
    console.error('导航失败:', error)
    // 如果导航失败，尝试使用 replace
    try {
      await router.replace(`/article/${props.article.id}`)
    } catch (replaceError) {
      console.error('导航替换失败:', replaceError)
      // 最后的备选方案：使用 window.location
      window.location.href = `/article/${props.article.id}`
    }
  } finally {
    // 延迟重置状态，避免快速连击
    setTimeout(() => {
      navigating.value = false
    }, 500)
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

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = Math.abs(now.getTime() - date.getTime())
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

  if (diffDays === 1) {
    return '昨天'
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else if (diffDays < 30) {
    return `${Math.floor(diffDays / 7)}周前`
  } else {
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'numeric',
      day: 'numeric'
    })
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

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
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

/* 为特定主题页面优化（如HomeView的深色背景） */
.card:hover .category-badge {
  background: linear-gradient(135deg, #dc2626, #ef4444);
  color: #ffffff;
  border-color: rgba(255, 255, 255, 0.3);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}
</style> 