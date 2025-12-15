<template>
  <div class="bg-gradient-to-br from-gray-800 via-gray-900 to-gray-950 rounded-2xl p-6 shadow-lg border border-gray-700 hover:border-indigo-600/50 transition-all duration-300">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-bold text-indigo-300 flex items-center gap-2">
        <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
          <path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11zM7 10h5v5H7z"/>
        </svg>
        活动日历
      </h3>
      <div class="flex items-center gap-2 text-xs text-gray-400">
        <span>{{ totalContributions }} 项活动</span>
        <span>•</span>
        <span>过去{{ weeks }}周</span>
      </div>
    </div>

    <!-- 月份标签 -->
    <div class="mb-2">
      <div class="grid grid-cols-12 gap-1 text-xs text-gray-400 h-4">
        <div v-for="month in monthLabels" :key="month.name" :class="`col-span-${month.span} text-center`">
          {{ month.name }}
        </div>
      </div>
    </div>

    <!-- 日历网格 -->
    <div class="mb-4">
      <div class="flex gap-1">
        <!-- 星期标签 -->
        <div class="flex flex-col gap-1 w-8 text-xs text-gray-400 justify-center">
          <div class="h-3"></div>
          <div class="h-3 flex items-center">一</div>
          <div class="h-3"></div>
          <div class="h-3 flex items-center">三</div>
          <div class="h-3"></div>
          <div class="h-3 flex items-center">五</div>
          <div class="h-3"></div>
        </div>
        
        <!-- 日历格子 -->
        <div class="grid grid-cols-53 gap-1 flex-1">
          <div 
            v-for="day in calendarDays" 
            :key="day.date"
            class="w-3 h-3 rounded-sm transition-all duration-200 cursor-pointer group relative"
            :class="getDayColor(day.level)"
            :title="getTooltipText(day)"
            @mouseenter="hoveredDay = day"
            @mouseleave="hoveredDay = null"
          >
            <!-- 悬浮提示 -->
            <div 
              v-if="hoveredDay === day"
              class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-900 text-white text-xs rounded shadow-lg whitespace-nowrap z-10 border border-gray-600"
            >
              <div class="text-cyan-300 font-medium">{{ formatDate(day.date) }}</div>
              <div class="text-gray-300">{{ day.count }} 项活动</div>
              <div v-if="day.activities.length > 0" class="text-gray-400 text-xs mt-1">
                <div v-for="activity in day.activities.slice(0, 3)" :key="activity.type" class="truncate">
                  • {{ activity.type }}
                </div>
                <div v-if="day.activities.length > 3" class="text-gray-500">
                  ... 还有 {{ day.activities.length - 3 }} 项
                </div>
              </div>
              <!-- 箭头 -->
              <div class="absolute top-full left-1/2 transform -translate-x-1/2 w-0 h-0 border-l-4 border-r-4 border-t-4 border-transparent border-t-gray-900"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图例和统计 -->
    <div class="flex items-center justify-between">
      <!-- 活动强度图例 -->
      <div class="flex items-center gap-2">
        <span class="text-xs text-gray-400">少</span>
        <div class="flex gap-1">
          <div class="w-3 h-3 rounded-sm bg-gray-700"></div>
          <div class="w-3 h-3 rounded-sm bg-emerald-900/50"></div>
          <div class="w-3 h-3 rounded-sm bg-emerald-700/70"></div>
          <div class="w-3 h-3 rounded-sm bg-emerald-500"></div>
          <div class="w-3 h-3 rounded-sm bg-emerald-400"></div>
        </div>
        <span class="text-xs text-gray-400">多</span>
      </div>

      <!-- 统计信息 -->
      <div class="text-xs text-gray-400 space-y-1">
        <div class="flex justify-end gap-4">
          <span>最长连续: {{ longestStreak }} 天</span>
          <span>当前连续: {{ currentStreak }} 天</span>
        </div>
        <div class="flex justify-end gap-4">
          <span>平均/天: {{ averagePerDay.toFixed(1) }}</span>
          <span>最多单日: {{ maxDayCount }}</span>
        </div>
      </div>
    </div>

    <!-- 最近活跃天数 -->
    <div class="mt-4 pt-4 border-t border-gray-700/50">
      <div class="flex items-center justify-between mb-2">
        <h4 class="text-sm font-semibold text-gray-300">最近活跃</h4>
        <span class="text-xs text-gray-400">过去7天</span>
      </div>
      <div class="grid grid-cols-7 gap-2">
        <div v-for="day in recentDays" :key="day.date" class="text-center">
          <div class="text-xs text-gray-400 mb-1">{{ day.weekday }}</div>
          <div 
            class="h-6 rounded flex items-center justify-center text-xs font-medium transition-all duration-200"
            :class="day.count > 0 ? 'bg-gradient-to-br from-indigo-600/30 to-indigo-800/30 text-indigo-300 border border-indigo-600/50' : 'bg-gray-700/50 text-gray-500'"
          >
            {{ day.count }}
          </div>
          <div class="text-xs text-gray-500 mt-1">{{ day.shortDate }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useActivityFeed, type Activity } from '../composables/useActivityFeed'

interface CalendarDay {
  date: string
  count: number
  level: number
  activities: { type: string; content: string }[]
}

const { activities } = useActivityFeed()
const hoveredDay = ref<CalendarDay | null>(null)
const weeks = 52

// 生成日历数据
const calendarDays = computed(() => {
  const days: CalendarDay[] = []
  const today = new Date()
  const startDate = new Date(today)
  startDate.setDate(today.getDate() - (weeks * 7 - 1))

  // 调整到周一开始
  const dayOfWeek = startDate.getDay()
  const daysToMonday = dayOfWeek === 0 ? 6 : dayOfWeek - 1
  startDate.setDate(startDate.getDate() - daysToMonday)

  for (let i = 0; i < weeks * 7; i++) {
    const currentDate = new Date(startDate)
    currentDate.setDate(startDate.getDate() + i)
    
    const dateStr = currentDate.toISOString().split('T')[0]
    
    // 计算该日期的活动数量（模拟数据）
    const dayActivities = activities.value.filter(activity => {
      // 这里应该根据实际的日期字段进行过滤
      // 目前使用随机数模拟
      return Math.random() > 0.7
    })

    const count = dayActivities.length + Math.floor(Math.random() * 3)
    const level = Math.min(4, Math.floor(count / 2))

    days.push({
      date: dateStr,
      count,
      level,
      activities: dayActivities.map(a => ({ type: a.type, content: a.content }))
    })
  }

  return days
})

// 总贡献数
const totalContributions = computed(() => {
  return calendarDays.value.reduce((sum, day) => sum + day.count, 0)
})

// 获取日期颜色
const getDayColor = (level: number): string => {
  const colors = [
    'bg-gray-700 hover:bg-gray-600',
    'bg-emerald-900/50 hover:bg-emerald-800/70',
    'bg-emerald-700/70 hover:bg-emerald-600/80',
    'bg-emerald-500 hover:bg-emerald-400',
    'bg-emerald-400 hover:bg-emerald-300'
  ]
  return colors[level] || colors[0]
}

// 格式化日期
const formatDate = (dateStr: string): string => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  })
}

// 获取提示文本
const getTooltipText = (day: CalendarDay): string => {
  return `${formatDate(day.date)}: ${day.count} 项活动`
}

// 生成月份标签
const monthLabels = computed(() => {
  const months = []
  const now = new Date()
  for (let i = 11; i >= 0; i--) {
    const date = new Date(now.getFullYear(), now.getMonth() - i, 1)
    months.push({
      name: date.toLocaleDateString('zh-CN', { month: 'short' }),
      span: 1
    })
  }
  return months
})

// 最长连续天数
const longestStreak = computed(() => {
  let longest = 0
  let current = 0
  
  for (const day of calendarDays.value) {
    if (day.count > 0) {
      current++
      longest = Math.max(longest, current)
    } else {
      current = 0
    }
  }
  
  return longest
})

// 当前连续天数
const currentStreak = computed(() => {
  let streak = 0
  const reversedDays = [...calendarDays.value].reverse()
  
  for (const day of reversedDays) {
    if (day.count > 0) {
      streak++
    } else {
      break
    }
  }
  
  return streak
})

// 平均每天活动数
const averagePerDay = computed(() => {
  const activeDays = calendarDays.value.filter(day => day.count > 0)
  return activeDays.length > 0 ? totalContributions.value / activeDays.length : 0
})

// 最多单日活动数
const maxDayCount = computed(() => {
  return Math.max(...calendarDays.value.map(day => day.count))
})

// 最近7天的数据
const recentDays = computed(() => {
  const days = []
  const today = new Date()
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  
  for (let i = 6; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(today.getDate() - i)
    
    const dateStr = date.toISOString().split('T')[0]
    const dayData = calendarDays.value.find(d => d.date === dateStr)
    
    days.push({
      date: dateStr,
      weekday: weekdays[date.getDay()],
      shortDate: `${date.getMonth() + 1}/${date.getDate()}`,
      count: dayData?.count || Math.floor(Math.random() * 5)
    })
  }
  
  return days
})
</script>

<style scoped>
/* 自定义网格布局 */
.grid-cols-53 {
  grid-template-columns: repeat(53, minmax(0, 1fr));
}

/* 悬浮效果增强 */
.group:hover {
  transform: scale(1.2);
  z-index: 10;
}

/* 渐变动画 */
@keyframes glow {
  0%, 100% { box-shadow: 0 0 5px rgba(52, 211, 153, 0.3); }
  50% { box-shadow: 0 0 15px rgba(52, 211, 153, 0.6); }
}

.bg-emerald-400:hover {
  animation: glow 2s infinite;
}

/* 工具提示动画 */
.group-hover div {
  animation: fadeInUp 0.2s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(4px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style> 