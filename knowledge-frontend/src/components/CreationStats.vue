<template>
  <div class="bg-gradient-to-br from-gray-800 via-gray-900 to-gray-950 rounded-2xl p-6 shadow-lg border border-gray-700 hover:border-cyan-600/50 transition-all duration-300">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-bold text-cyan-300 flex items-center gap-2">
        <svg class="w-6 h-6 text-yellow-400" fill="currentColor" viewBox="0 0 24 24">
          <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        创作统计
      </h3>
      
      <!-- 时间范围选择 -->
      <select 
        v-model="selectedPeriod" 
        class="bg-gray-700/50 border border-gray-600 rounded-lg px-3 py-1 text-sm text-cyan-200 focus:outline-none focus:border-cyan-500 transition-colors"
      >
        <option value="all">全部时间</option>
        <option value="month">本月</option>
        <option value="week">本周</option>
      </select>
    </div>

    <!-- 统计卡片网格 -->
    <div class="grid grid-cols-2 gap-4">
      <!-- 文章总数 -->
      <div class="bg-gradient-to-r from-cyan-600/20 to-blue-600/20 rounded-xl p-4 border border-cyan-600/30 hover:border-cyan-500/50 transition-all duration-200 group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-cyan-200 text-sm opacity-80">文章总数</p>
            <p class="text-2xl font-bold text-cyan-100 mt-1 group-hover:text-cyan-50 transition-colors">
              {{ stats.totalArticles }}
            </p>
          </div>
          <div class="w-10 h-10 bg-cyan-500/20 rounded-lg flex items-center justify-center group-hover:bg-cyan-500/30 transition-colors">
            <svg class="w-5 h-5 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
            </svg>
          </div>
        </div>
        <div class="flex items-center mt-2 text-xs">
          <span class="text-green-400">+{{ stats.recentArticles }}</span>
          <span class="text-gray-400 ml-1">最近</span>
        </div>
      </div>

      <!-- 总浏览量 -->
      <div class="bg-gradient-to-r from-purple-600/20 to-pink-600/20 rounded-xl p-4 border border-purple-600/30 hover:border-purple-500/50 transition-all duration-200 group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-purple-200 text-sm opacity-80">总浏览量</p>
            <p class="text-2xl font-bold text-purple-100 mt-1 group-hover:text-purple-50 transition-colors">
              {{ formatNumber(stats.totalViews) }}
            </p>
          </div>
          <div class="w-10 h-10 bg-purple-500/20 rounded-lg flex items-center justify-center group-hover:bg-purple-500/30 transition-colors">
            <svg class="w-5 h-5 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
            </svg>
          </div>
        </div>
        <div class="flex items-center mt-2 text-xs">
          <span class="text-green-400">+{{ formatNumber(stats.recentViews) }}</span>
          <span class="text-gray-400 ml-1">最近</span>
        </div>
      </div>

      <!-- 总点赞数 -->
      <div class="bg-gradient-to-r from-red-600/20 to-orange-600/20 rounded-xl p-4 border border-red-600/30 hover:border-red-500/50 transition-all duration-200 group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-red-200 text-sm opacity-80">总点赞数</p>
            <p class="text-2xl font-bold text-red-100 mt-1 group-hover:text-red-50 transition-colors">
              {{ formatNumber(stats.totalLikes) }}
            </p>
          </div>
          <div class="w-10 h-10 bg-red-500/20 rounded-lg flex items-center justify-center group-hover:bg-red-500/30 transition-colors">
            <svg class="w-5 h-5 text-red-400" fill="currentColor" viewBox="0 0 24 24">
              <path d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
            </svg>
          </div>
        </div>
        <div class="flex items-center mt-2 text-xs">
          <span class="text-green-400">+{{ stats.recentLikes }}</span>
          <span class="text-gray-400 ml-1">最近</span>
        </div>
      </div>

      <!-- 总评论数 -->
      <div class="bg-gradient-to-r from-green-600/20 to-emerald-600/20 rounded-xl p-4 border border-green-600/30 hover:border-green-500/50 transition-all duration-200 group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-green-200 text-sm opacity-80">总评论数</p>
            <p class="text-2xl font-bold text-green-100 mt-1 group-hover:text-green-50 transition-colors">
              {{ formatNumber(stats.totalComments) }}
            </p>
          </div>
          <div class="w-10 h-10 bg-green-500/20 rounded-lg flex items-center justify-center group-hover:bg-green-500/30 transition-colors">
            <svg class="w-5 h-5 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
            </svg>
          </div>
        </div>
        <div class="flex items-center mt-2 text-xs">
          <span class="text-green-400">+{{ stats.recentComments }}</span>
          <span class="text-gray-400 ml-1">最近</span>
        </div>
      </div>
    </div>

    <!-- 详细统计信息 -->
    <div class="mt-6 pt-4 border-t border-gray-700/50">
      <div class="grid grid-cols-2 gap-4 text-sm">
        <div class="flex justify-between items-center">
          <span class="text-gray-400">平均阅读时长</span>
          <span class="text-cyan-200 font-medium">{{ stats.avgReadingTime }} 分钟</span>
        </div>
        <div class="flex justify-between items-center">
          <span class="text-gray-400">最受欢迎文章</span>
          <span class="text-cyan-200 font-medium truncate max-w-24" :title="stats.popularArticle">
            {{ stats.popularArticle || '暂无' }}
          </span>
        </div>
        <div class="flex justify-between items-center">
          <span class="text-gray-400">创作天数</span>
          <span class="text-cyan-200 font-medium">{{ stats.activeDays }} 天</span>
        </div>
        <div class="flex justify-between items-center">
          <span class="text-gray-400">字数统计</span>
          <span class="text-cyan-200 font-medium">{{ formatNumber(stats.totalWords) }} 字</span>
        </div>
      </div>
    </div>

    <!-- 创作频率图表 -->
    <div class="mt-6 pt-4 border-t border-gray-700/50">
      <h4 class="text-sm font-medium text-gray-300 mb-3">最近创作频率</h4>
      <div class="flex items-end justify-between h-12 gap-1">
        <div 
          v-for="(day, index) in stats.recentActivity" 
          :key="index"
          class="flex-1 bg-gray-700/30 rounded-t hover:bg-cyan-500/30 transition-all duration-200 relative group"
          :style="{ height: `${Math.max(day.count * 8, 4)}px` }"
          :title="`${day.date}: ${day.count} 篇文章`"
        >
          <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 bg-gray-800 text-xs text-white px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition-opacity mb-2 whitespace-nowrap">
            {{ day.date }}: {{ day.count }} 篇
          </div>
        </div>
      </div>
      <div class="flex justify-between text-xs text-gray-500 mt-2">
        <span>7天前</span>
        <span>今天</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useArticlesStore } from '../stores/articles'

interface CreationStats {
  totalArticles: number
  totalViews: number
  totalLikes: number
  totalComments: number
  totalWords: number
  recentArticles: number
  recentViews: number
  recentLikes: number
  recentComments: number
  avgReadingTime: number
  popularArticle: string
  activeDays: number
  recentActivity: Array<{
    date: string
    count: number
  }>
}

const articlesStore = useArticlesStore()
const selectedPeriod = ref('all')

// 数字格式化
const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

// 计算统计数据
const stats = computed((): CreationStats => {
  const articles = articlesStore.articles || []
  const now = new Date()
  const oneWeekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
  const oneMonthAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000)

  // 根据选择的时间范围过滤文章
  let filteredArticles = articles
  if (selectedPeriod.value === 'week') {
    filteredArticles = articles.filter(article => 
      new Date(article.createdAt || '') >= oneWeekAgo
    )
  } else if (selectedPeriod.value === 'month') {
    filteredArticles = articles.filter(article => 
      new Date(article.createdAt || '') >= oneMonthAgo
    )
  }

  // 基础统计
  const totalArticles = filteredArticles.length
  const totalViews = filteredArticles.reduce((sum, article) => sum + (article.views || 0), 0)
  const totalLikes = filteredArticles.reduce((sum, article) => sum + (article.likes || 0), 0)
  const totalComments = filteredArticles.reduce((sum, article) => sum + (article.commentCount || 0), 0)
  
  // 估算总字数（基于内容长度）
  const totalWords = filteredArticles.reduce((sum, article) => {
    const content = article.content || ''
    // 简单估算：去除HTML标签后的字符数
    const textContent = content.replace(/<[^>]*>/g, '')
    return sum + textContent.length
  }, 0)

  // 最近数据（过去7天）
  const recentArticles = articles.filter(article => 
    new Date(article.createdAt || '') >= oneWeekAgo
  ).length

  const recentViewsData = articles.filter(article => 
    new Date(article.updatedAt || article.createdAt || '') >= oneWeekAgo
  )
  const recentViews = recentViewsData.reduce((sum, article) => sum + (article.views || 0), 0)
  const recentLikes = recentViewsData.reduce((sum, article) => sum + (article.likes || 0), 0)
  const recentComments = recentViewsData.reduce((sum, article) => sum + (article.commentCount || 0), 0)

  // 平均阅读时长（基于字数估算，平均200字/分钟）
  const avgReadingTime = totalWords > 0 ? Math.round(totalWords / 200) : 0

  // 最受欢迎的文章
  const popularArticle = filteredArticles.reduce((prev, current) => 
    (current.views || 0) > (prev.views || 0) ? current : prev, 
    filteredArticles[0] || {}
  )?.title || ''

  // 活跃天数
  const uniqueDates = new Set(
    filteredArticles.map(article => 
      new Date(article.createdAt || '').toDateString()
    )
  )
  const activeDays = uniqueDates.size

  // 最近7天的创作活动
  const recentActivity: Array<{date: string, count: number}> = []
  for (let i = 6; i >= 0; i--) {
    const date = new Date(now.getTime() - i * 24 * 60 * 60 * 1000)
    const dateStr = date.toISOString().split('T')[0]
    const count = articles.filter(article => {
      const articleDate = new Date(article.createdAt || '').toISOString().split('T')[0]
      return articleDate === dateStr
    }).length
    
    recentActivity.push({
      date: date.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' }),
      count
    })
  }

  return {
    totalArticles,
    totalViews,
    totalLikes,
    totalComments,
    totalWords,
    recentArticles,
    recentViews,
    recentLikes,
    recentComments,
    avgReadingTime,
    popularArticle,
    activeDays,
    recentActivity
  }
})

// 初始化
onMounted(() => {
  // 确保文章数据已加载
  if (articlesStore.articles.length === 0) {
    articlesStore.fetchArticles()
  }
})
</script>

<style scoped>
/* 卡片悬浮效果 */
.group:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

/* 图表柱状图动画 */
.flex-1 {
  transition: all 0.3s ease;
}

/* 选择框样式 */
select {
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e");
  background-position: right 8px center;
  background-repeat: no-repeat;
  background-size: 16px;
  padding-right: 32px;
}
</style> 