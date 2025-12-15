<template>
  <div class="bg-gradient-to-br from-gray-800 via-gray-900 to-gray-950 rounded-2xl p-6 shadow-lg border border-gray-700 hover:border-emerald-600/50 transition-all duration-300">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-bold text-emerald-300 flex items-center gap-2">
        <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
        </svg>
        活动时间轴
      </h3>
      <div class="flex items-center gap-2">
        <button 
          v-for="filter in timeFilters" 
          :key="filter.key"
          @click="selectedTimeFilter = filter.key"
          :class="[
            'px-3 py-1 rounded-lg text-xs font-medium transition-all duration-200',
            selectedTimeFilter === filter.key 
              ? 'bg-emerald-600 text-white shadow-lg' 
              : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
          ]"
        >
          {{ filter.label }}
        </button>
      </div>
    </div>

    <!-- 时间轴容器 -->
    <div class="relative">
      <!-- 主时间轴线 -->
      <div class="absolute left-6 top-0 bottom-0 w-0.5 bg-gradient-to-b from-emerald-500 via-cyan-500 to-purple-500 opacity-60"></div>
      
      <!-- 活动项目 -->
      <div class="space-y-6">
        <div 
          v-for="(group, index) in groupedActivities" 
          :key="group.date"
          class="timeline-group"
          :style="{ animationDelay: `${index * 100}ms` }"
        >
          <!-- 日期分组标题 -->
          <div class="flex items-center gap-4 mb-4">
            <div class="relative z-10 flex items-center justify-center w-12 h-12 bg-gradient-to-br from-emerald-600 to-cyan-600 rounded-full shadow-lg border-4 border-gray-800">
              <svg class="w-5 h-5 text-white" fill="currentColor" viewBox="0 0 24 24">
                <path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11z"/>
              </svg>
            </div>
            <div>
              <h4 class="text-lg font-semibold text-gray-200">{{ group.dateLabel }}</h4>
              <p class="text-sm text-gray-400">{{ group.activities.length }} 项活动</p>
            </div>
          </div>

          <!-- 该日期的活动列表 -->
          <div class="ml-12 space-y-3">
            <div 
              v-for="(activity, activityIndex) in group.activities" 
              :key="activity.id"
              class="activity-item relative"
              :style="{ animationDelay: `${(index * 100) + (activityIndex * 50)}ms` }"
            >
              <!-- 连接线到主时间轴 -->
              <div class="absolute -left-6 top-4 w-6 h-0.5 bg-gradient-to-r from-gray-600 to-gray-500"></div>
              
              <!-- 活动节点 -->
              <div class="absolute -left-8 top-3 w-3 h-3 rounded-full border-2 border-gray-800 z-10 transition-all duration-300 hover:scale-125"
                   :class="getActivityNodeColor(activity.type)">
              </div>

              <!-- 活动内容卡片 -->
              <div 
                class="bg-gradient-to-br from-gray-700/50 to-gray-800/50 rounded-xl p-4 border border-gray-600/50 hover:border-gray-500/70 transition-all duration-300 hover:transform hover:translate-x-1 cursor-pointer group"
                @click="handleActivityClick(activity)"
              >
                <div class="flex items-start justify-between">
                  <div class="flex-1">
                    <!-- 活动类型标签 -->
                    <div class="flex items-center gap-2 mb-2">
                      <span 
                        class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium border"
                        :class="getActivityTypeStyle(activity.type)"
                      >
                        <svg class="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 24 24">
                          <path :d="getActivityIcon(activity.type)"/>
                        </svg>
                        {{ getActivityTypeLabel(activity.type) }}
                      </span>
                      <span class="text-xs text-gray-400">{{ activity.date }}</span>
                    </div>

                    <!-- 活动内容 -->
                    <p class="text-gray-200 text-sm font-medium mb-2 group-hover:text-gray-100 transition-colors">
                      {{ activity.content }}
                    </p>

                    <!-- 元数据 -->
                    <div v-if="activity.metadata" class="flex items-center gap-3 text-xs text-gray-400">
                      <span v-if="activity.metadata.category" class="flex items-center gap-1">
                        <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 24 24">
                          <path d="M10 4H4c-1.11 0-2 .89-2 2v12c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2h-8l-2-2z"/>
                        </svg>
                        {{ activity.metadata.category }}
                      </span>
                      <span v-if="activity.metadata.status !== undefined" class="flex items-center gap-1">
                        <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 24 24">
                          <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                        </svg>
                        {{ activity.metadata.status === 1 ? '已发布' : '草稿' }}
                      </span>
                    </div>
                  </div>

                  <!-- 箭头指示器 -->
                  <div class="opacity-0 group-hover:opacity-100 transition-opacity duration-200 ml-2">
                    <svg class="w-4 h-4 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                    </svg>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载更多 -->
      <div v-if="hasMore" class="flex justify-center mt-8">
        <button 
          @click="loadMore"
          :disabled="isLoadingMore"
          class="px-6 py-3 bg-gradient-to-r from-emerald-600 to-cyan-600 text-white rounded-lg font-medium hover:from-emerald-500 hover:to-cyan-500 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
        >
          <svg v-if="isLoadingMore" class="w-4 h-4 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
          </svg>
          {{ isLoadingMore ? '加载中...' : '加载更多' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useActivityFeed, type Activity } from '../composables/useActivityFeed'

const router = useRouter()
const { activities, formatTimeAgo } = useActivityFeed()

const selectedTimeFilter = ref('week')
const isLoadingMore = ref(false)
const displayCount = ref(10)

// 时间过滤器
const timeFilters = [
  { key: 'today', label: '今天' },
  { key: 'week', label: '本周' },
  { key: 'month', label: '本月' },
  { key: 'all', label: '全部' }
]

// 过滤活动
const filteredActivities = computed(() => {
  const now = new Date()
  let filterDate: Date

  switch (selectedTimeFilter.value) {
    case 'today':
      filterDate = new Date(now.setHours(0, 0, 0, 0))
      break
    case 'week':
      filterDate = new Date(now.setDate(now.getDate() - 7))
      break
    case 'month':
      filterDate = new Date(now.setMonth(now.getMonth() - 1))
      break
    default:
      return activities.value.slice(0, displayCount.value)
  }

  // 这里应该根据实际的时间戳进行过滤
  // 目前返回所有活动作为演示
  return activities.value.slice(0, displayCount.value)
})

// 按日期分组活动
const groupedActivities = computed(() => {
  const groups: { [date: string]: Activity[] } = {}
  
  filteredActivities.value.forEach(activity => {
    // 模拟日期分组，实际应该解析activity的实际日期
    const today = new Date()
    const yesterday = new Date(today)
    yesterday.setDate(today.getDate() - 1)
    const weekAgo = new Date(today)
    weekAgo.setDate(today.getDate() - 7)
    
    let groupKey: string
    if (activity.date === '刚刚' || activity.date.includes('分钟前') || activity.date.includes('小时前')) {
      groupKey = 'today'
    } else if (activity.date.includes('天前') && parseInt(activity.date) === 1) {
      groupKey = 'yesterday'
    } else {
      groupKey = 'earlier'
    }
    
    if (!groups[groupKey]) {
      groups[groupKey] = []
    }
    groups[groupKey].push(activity)
  })

  // 转换为数组并添加标签
  const result = Object.entries(groups).map(([key, activities]) => {
    let dateLabel: string
    switch (key) {
      case 'today': dateLabel = '今天'; break
      case 'yesterday': dateLabel = '昨天'; break
      case 'earlier': dateLabel = '更早'; break
      default: dateLabel = key
    }

    return {
      date: key,
      dateLabel,
      activities: activities.sort((a, b) => {
        // 简单的时间排序
        if (a.date === '刚刚') return -1
        if (b.date === '刚刚') return 1
        return 0
      })
    }
  })

  // 按时间顺序排序组
  const order = ['today', 'yesterday', 'earlier']
  return result.sort((a, b) => {
    const aIndex = order.indexOf(a.date)
    const bIndex = order.indexOf(b.date)
    return aIndex - bIndex
  })
})

// 获取活动节点颜色
const getActivityNodeColor = (type: string): string => {
  switch (type) {
    case 'article': return 'bg-cyan-400 shadow-cyan-400/50'
    case 'achievement': return 'bg-yellow-400 shadow-yellow-400/50'
    case 'update': return 'bg-pink-400 shadow-pink-400/50'
    case 'system': return 'bg-green-400 shadow-green-400/50'
    default: return 'bg-gray-400 shadow-gray-400/50'
  }
}

// 获取活动类型样式
const getActivityTypeStyle = (type: string): string => {
  switch (type) {
    case 'article': return 'bg-cyan-600/20 text-cyan-300 border-cyan-600/30'
    case 'achievement': return 'bg-yellow-600/20 text-yellow-300 border-yellow-600/30'
    case 'update': return 'bg-pink-600/20 text-pink-300 border-pink-600/30'
    case 'system': return 'bg-green-600/20 text-green-300 border-green-600/30'
    default: return 'bg-gray-600/20 text-gray-300 border-gray-600/30'
  }
}

// 获取活动类型标签
const getActivityTypeLabel = (type: string): string => {
  switch (type) {
    case 'article': return '文章'
    case 'achievement': return '成就'
    case 'update': return '更新'
    case 'system': return '系统'
    default: return '其他'
  }
}

// 获取活动图标
const getActivityIcon = (type: string): string => {
  switch (type) {
    case 'article': return 'M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z'
    case 'achievement': return 'M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z'
    case 'update': return 'M21 10.12h-6.78l2.74-2.82c-2.73-2.7-7.15-2.8-9.88-.1-2.73 2.71-2.73 7.08 0 9.79 2.73 2.71 7.15 2.71 9.88 0C18.32 15.65 19 14.08 19 12.1h2c0 1.98-.88 4.55-2.64 6.29-3.51 3.48-9.21 3.48-12.72 0-3.5-3.47-3.53-9.11-.02-12.58 3.51-3.47 9.14-3.47 12.65 0L21 3v7.12z'
    case 'system': return 'M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4z'
    default: return 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z'
  }
}

// 处理活动点击
const handleActivityClick = (activity: Activity) => {
  if (activity.articleId) {
    router.push(`/article/${activity.articleId}`)
  }
}

// 是否有更多内容
const hasMore = computed(() => {
  return displayCount.value < activities.value.length
})

// 加载更多
const loadMore = async () => {
  isLoadingMore.value = true
  
  // 模拟加载延迟
  await new Promise(resolve => setTimeout(resolve, 800))
  
  displayCount.value += 10
  isLoadingMore.value = false
}
</script>

<style scoped>
/* 时间轴动画 */
.timeline-group {
  animation: fadeInUp 0.6s ease-out forwards;
  opacity: 0;
  transform: translateY(20px);
}

.activity-item {
  animation: slideInRight 0.4s ease-out forwards;
  opacity: 0;
  transform: translateX(-20px);
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInRight {
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 节点脉冲效果 */
.activity-item:hover .absolute.-left-8 {
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

/* 主时间轴发光效果 */
.absolute.left-6::before {
  content: '';
  position: absolute;
  top: 0;
  left: -2px;
  right: -2px;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(16, 185, 129, 0.3), rgba(6, 182, 212, 0.3), rgba(147, 51, 234, 0.3));
  border-radius: 2px;
  animation: glow 3s ease-in-out infinite alternate;
}

@keyframes glow {
  from { opacity: 0.5; }
  to { opacity: 1; }
}

/* 悬浮效果增强 */
.activity-item .bg-gradient-to-br:hover {
  background: linear-gradient(135deg, rgba(55, 65, 81, 0.7), rgba(31, 41, 55, 0.7));
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.15);
}
</style> 