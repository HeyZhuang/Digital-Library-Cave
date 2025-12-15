<template>
  <div class="bg-gradient-to-br from-gray-800 via-gray-900 to-gray-950 rounded-2xl p-6 shadow-lg border border-gray-700 hover:border-cyan-600/50 transition-all duration-300">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-bold text-cyan-300 flex items-center gap-2">
        <svg class="w-6 h-6 text-purple-400" fill="currentColor" viewBox="0 0 24 24">
          <path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11zM7 10h5v5H7z"/>
        </svg>
        创作日历
      </h3>
      
      <!-- 月份导航 -->
      <div class="flex items-center gap-2">
        <button 
          @click="previousMonth"
          class="p-1.5 rounded-lg bg-gray-700/50 hover:bg-gray-600/50 text-gray-400 hover:text-cyan-300 transition-all duration-200"
          title="上个月"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
          </svg>
        </button>
        
        <div class="px-3 py-1.5 bg-gray-700/30 rounded-lg text-cyan-200 font-medium min-w-32 text-center">
          {{ formatMonthYear(currentDate) }}
        </div>
        
        <button 
          @click="nextMonth"
          class="p-1.5 rounded-lg bg-gray-700/50 hover:bg-gray-600/50 text-gray-400 hover:text-cyan-300 transition-all duration-200"
          title="下个月"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- 日历网格 -->
    <div class="calendar-grid">
      <!-- 星期标题 -->
      <div class="grid grid-cols-7 gap-1 mb-2">
        <div 
          v-for="day in weekDays" 
          :key="day"
          class="text-center text-sm font-medium text-gray-400 py-2"
        >
          {{ day }}
        </div>
      </div>

      <!-- 日期网格 -->
      <div class="grid grid-cols-7 gap-1">
        <div 
          v-for="date in calendarDates" 
          :key="date.fullDate"
          class="calendar-cell"
          :class="[
            getCellClass(date),
            { 'opacity-30': !date.isCurrentMonth }
          ]"
          :title="getDateTooltip(date)"
          @click="selectDate(date)"
        >
          <div class="day-number">{{ date.day }}</div>
          
          <!-- 创作活动指示器 -->
          <div v-if="date.activityData.count > 0" class="activity-indicators">
            <div 
              v-for="i in Math.min(date.activityData.count, 3)" 
              :key="i"
              class="activity-dot"
              :class="getActivityDotClass(date.activityData.intensity)"
            ></div>
            <div 
              v-if="date.activityData.count > 3" 
              class="activity-more"
            >
              +{{ date.activityData.count - 3 }}
            </div>
          </div>
          
          <!-- 特殊标记 -->
          <div v-if="date.achievements.length > 0" class="achievement-badge">
            🏆
          </div>
        </div>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="mt-6 pt-4 border-t border-gray-700/50">
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
        <div class="text-center">
          <div class="text-cyan-200 font-bold text-lg">{{ monthStats.totalArticles }}</div>
          <div class="text-gray-400">本月文章</div>
        </div>
        <div class="text-center">
          <div class="text-purple-200 font-bold text-lg">{{ monthStats.activeDays }}</div>
          <div class="text-gray-400">活跃天数</div>
        </div>
        <div class="text-center">
          <div class="text-green-200 font-bold text-lg">{{ formatNumber(monthStats.totalWords) }}</div>
          <div class="text-gray-400">总字数</div>
        </div>
        <div class="text-center">
          <div class="text-yellow-200 font-bold text-lg">{{ monthStats.longestStreak }}</div>
          <div class="text-gray-400">最长连击</div>
        </div>
      </div>
    </div>

    <!-- 活动强度图例 -->
    <div class="mt-4 flex items-center justify-between text-xs">
      <span class="text-gray-400">活动强度</span>
      <div class="flex items-center gap-1">
        <span class="text-gray-500">少</span>
        <div class="flex gap-1">
          <div class="w-3 h-3 rounded-sm bg-gray-700/50"></div>
          <div class="w-3 h-3 rounded-sm bg-cyan-600/30"></div>
          <div class="w-3 h-3 rounded-sm bg-cyan-500/50"></div>
          <div class="w-3 h-3 rounded-sm bg-cyan-400/70"></div>
          <div class="w-3 h-3 rounded-sm bg-cyan-300"></div>
        </div>
        <span class="text-gray-500">多</span>
      </div>
    </div>

    <!-- 选中日期详情 -->
    <div v-if="selectedDate" class="mt-6 pt-4 border-t border-gray-700/50">
      <h4 class="text-lg font-medium text-cyan-300 mb-3">
        {{ formatSelectedDate(selectedDate) }} 的创作活动
      </h4>
      
      <div v-if="selectedDate.articles.length > 0" class="space-y-2">
        <div 
          v-for="article in selectedDate.articles" 
          :key="article.id"
          class="flex items-center gap-3 p-3 bg-gray-800/30 rounded-lg hover:bg-gray-800/50 transition-all duration-200 cursor-pointer"
          @click="viewArticle(article.id!)"
        >
          <div class="flex-shrink-0 w-8 h-8 bg-cyan-500/20 rounded-lg flex items-center justify-center">
            <svg class="w-4 h-4 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
            </svg>
          </div>
          
          <div class="flex-1">
            <div class="text-cyan-200 font-medium">{{ article.title }}</div>
            <div class="text-gray-400 text-sm">
              {{ getArticleTimeString(article) }} · {{ estimateReadingTime(article.content || '') }} 分钟阅读
            </div>
          </div>
          
          <div class="flex items-center gap-2 text-xs text-gray-400">
            <span>{{ article.views || 0 }} 浏览</span>
            <span>{{ article.likes || 0 }} 点赞</span>
          </div>
        </div>
      </div>
      
      <div v-else class="text-center py-6 text-gray-400">
        这一天没有创作活动
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useArticlesStore } from '../stores/articles'
import type { Article } from '../api/articles'

interface CalendarDate {
  day: number
  fullDate: string
  isCurrentMonth: boolean
  isToday: boolean
  activityData: {
    count: number
    intensity: number  // 0-4
    articles: Article[]
  }
  articles: Article[]
  achievements: string[]
}

interface MonthStats {
  totalArticles: number
  activeDays: number
  totalWords: number
  longestStreak: number
}

const router = useRouter()
const articlesStore = useArticlesStore()
const currentDate = ref(new Date())
const selectedDate = ref<CalendarDate | null>(null)

const weekDays = ['日', '一', '二', '三', '四', '五', '六']

// 格式化月年显示
const formatMonthYear = (date: Date): string => {
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long' 
  })
}

// 格式化选中日期
const formatSelectedDate = (date: CalendarDate): string => {
  return new Date(date.fullDate).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
}

// 数字格式化
const formatNumber = (num: number): string => {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

// 估算阅读时长
const estimateReadingTime = (content: string): number => {
  const textContent = content.replace(/<[^>]*>/g, '')
  return Math.max(1, Math.round(textContent.length / 200))
}

// 获取文章时间字符串
const getArticleTimeString = (article: Article): string => {
  const date = new Date(article.createdAt || '')
  return date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

// 月份导航
const previousMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, 1)
  selectedDate.value = null
}

const nextMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 1)
  selectedDate.value = null
}

// 生成日历日期
const calendarDates = computed((): CalendarDate[] => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth()
  
  // 获取当月第一天和最后一天
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  
  // 获取需要显示的开始日期（包含上月末尾几天）
  const startDate = new Date(firstDay)
  startDate.setDate(startDate.getDate() - firstDay.getDay())
  
  // 获取需要显示的结束日期（包含下月开头几天）
  const endDate = new Date(lastDay)
  endDate.setDate(endDate.getDate() + (6 - lastDay.getDay()))
  
  const dates: CalendarDate[] = []
  const today = new Date()
  
  // 获取当前月份的所有文章
  const monthArticles = articlesStore.articles.filter(article => {
    const articleDate = new Date(article.createdAt || '')
    return articleDate.getFullYear() === year && articleDate.getMonth() === month
  })
  
  // 按日期分组文章
  const articlesByDate = new Map<string, Article[]>()
  monthArticles.forEach(article => {
    const dateKey = new Date(article.createdAt || '').toISOString().split('T')[0]
    if (!articlesByDate.has(dateKey)) {
      articlesByDate.set(dateKey, [])
    }
    articlesByDate.get(dateKey)!.push(article)
  })
  
  // 生成日期数组
  const current = new Date(startDate)
  while (current <= endDate) {
    const dateKey = current.toISOString().split('T')[0]
    const dayArticles = articlesByDate.get(dateKey) || []
    
    // 计算活动强度
    const count = dayArticles.length
    let intensity = 0
    if (count > 0) {
      if (count >= 4) intensity = 4
      else if (count >= 3) intensity = 3
      else if (count >= 2) intensity = 2
      else intensity = 1
    }
    
    // 检查成就
    const achievements: string[] = []
    if (count >= 3) achievements.push('高产作家')
    if (dayArticles.some(a => (a.views || 0) > 100)) achievements.push('热门文章')
    
    dates.push({
      day: current.getDate(),
      fullDate: dateKey,
      isCurrentMonth: current.getMonth() === month,
      isToday: current.toDateString() === today.toDateString(),
      activityData: {
        count,
        intensity,
        articles: dayArticles
      },
      articles: dayArticles,
      achievements
    })
    
    current.setDate(current.getDate() + 1)
  }
  
  return dates
})

// 月度统计
const monthStats = computed((): MonthStats => {
  const monthDates = calendarDates.value.filter(date => date.isCurrentMonth)
  const totalArticles = monthDates.reduce((sum, date) => sum + date.activityData.count, 0)
  const activeDays = monthDates.filter(date => date.activityData.count > 0).length
  
  const totalWords = monthDates.reduce((sum, date) => {
    return sum + date.articles.reduce((articleSum, article) => {
      const content = article.content || ''
      const textContent = content.replace(/<[^>]*>/g, '')
      return articleSum + textContent.length
    }, 0)
  }, 0)
  
  // 计算最长连续创作天数
  let longestStreak = 0
  let currentStreak = 0
  
  monthDates.forEach(date => {
    if (date.activityData.count > 0) {
      currentStreak++
      longestStreak = Math.max(longestStreak, currentStreak)
    } else {
      currentStreak = 0
    }
  })
  
  return {
    totalArticles,
    activeDays,
    totalWords,
    longestStreak
  }
})

// 获取日期单元格样式
const getCellClass = (date: CalendarDate): string => {
  const classes = [
    'relative', 'w-10', 'h-10', 'rounded-lg', 'flex', 'flex-col', 'items-center', 'justify-center',
    'transition-all', 'duration-200', 'cursor-pointer', 'group'
  ]
  
  if (date.isToday) {
    classes.push('ring-2', 'ring-cyan-400', 'ring-opacity-50')
  }
  
  if (date.activityData.count > 0) {
    classes.push('hover:bg-cyan-600/20', 'border', 'border-cyan-600/30')
  } else {
    classes.push('hover:bg-gray-700/30', 'border', 'border-gray-700/20')
  }
  
  return classes.join(' ')
}

// 获取活动点样式
const getActivityDotClass = (intensity: number): string => {
  const baseClass = 'w-1 h-1 rounded-full'
  switch (intensity) {
    case 1: return `${baseClass} bg-cyan-600/30`
    case 2: return `${baseClass} bg-cyan-500/50`
    case 3: return `${baseClass} bg-cyan-400/70`
    case 4: return `${baseClass} bg-cyan-300`
    default: return `${baseClass} bg-gray-600/50`
  }
}

// 获取日期工具提示
const getDateTooltip = (date: CalendarDate): string => {
  const dateStr = new Date(date.fullDate).toLocaleDateString('zh-CN')
  if (date.activityData.count === 0) {
    return `${dateStr}: 无创作活动`
  }
  
  const achievements = date.achievements.length > 0 ? ` · ${date.achievements.join(', ')}` : ''
  return `${dateStr}: ${date.activityData.count} 篇文章${achievements}`
}

// 选择日期
const selectDate = (date: CalendarDate) => {
  selectedDate.value = date
}

// 查看文章
const viewArticle = (articleId: number) => {
  router.push(`/article/${articleId}`)
}

// 初始化
onMounted(() => {
  // 确保文章数据已加载
  if (articlesStore.articles.length === 0) {
    articlesStore.fetchArticles()
  }
})
</script>

<style scoped>
.calendar-grid {
  @apply select-none;
}

.calendar-cell {
  min-height: 40px;
  position: relative;
}

.day-number {
  @apply text-sm font-medium text-gray-300 group-hover:text-cyan-100 transition-colors;
  font-size: 12px;
  line-height: 1;
}

.activity-indicators {
  @apply absolute bottom-1 left-1/2 transform -translate-x-1/2 flex gap-0.5;
}

.activity-dot {
  @apply transition-all duration-200 group-hover:scale-110;
}

.activity-more {
  @apply text-xs text-cyan-300 font-bold;
  font-size: 8px;
  line-height: 1;
}

.achievement-badge {
  @apply absolute top-0 right-0 text-xs;
  font-size: 8px;
  line-height: 1;
}

/* 今天的特殊样式 */
.calendar-cell:has(.day-number) {
  @apply hover:scale-105;
}

/* 有活动的日期悬浮效果 */
.calendar-cell:hover .activity-dot {
  @apply shadow-sm;
  box-shadow: 0 0 4px currentColor;
}

/* 响应式调整 */
@media (max-width: 640px) {
  .calendar-cell {
    min-height: 35px;
  }
  
  .day-number {
    font-size: 11px;
  }
  
  .activity-more {
    font-size: 7px;
  }
  
  .achievement-badge {
    font-size: 7px;
  }
}

/* 选中状态 */
.calendar-cell.selected {
  @apply bg-cyan-600/20 border-cyan-400 shadow-lg;
}

/* 动画效果 */
.calendar-cell {
  animation: slideInUp 0.1s ease-out;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style> 