<template>
  <div class="bg-gradient-to-br from-gray-800 via-gray-900 to-gray-950 rounded-2xl p-6 shadow-lg border border-gray-700 hover:border-purple-600/50 transition-all duration-300">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-bold text-purple-300 flex items-center gap-2">
        <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
          <path d="M3 3h18v18H3V3zm16 16V5H5v14h14zM7 12h2v5H7v-5zm4-3h2v8h-2V9zm4-3h2v11h-2V6z"/>
        </svg>
        活动统计
      </h3>
      <div class="text-xs text-gray-400">最近30天</div>
    </div>

    <!-- 统计卡片网格 -->
    <div class="grid grid-cols-2 gap-4 mb-6">
      <!-- 总活动数 -->
      <div class="bg-gradient-to-br from-cyan-600/20 to-cyan-800/20 rounded-xl p-4 border border-cyan-600/30 hover:border-cyan-500/50 transition-all duration-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-cyan-300 mb-1">总活动</p>
            <p class="text-2xl font-bold text-cyan-100">{{ stats.totalActivities }}</p>
          </div>
          <div class="w-10 h-10 bg-cyan-600/30 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-cyan-400" fill="currentColor" viewBox="0 0 24 24">
              <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
            </svg>
          </div>
        </div>
        <div class="flex items-center mt-2">
          <span :class="['text-xs', stats.activityGrowth >= 0 ? 'text-green-400' : 'text-red-400']">
            {{ stats.activityGrowth >= 0 ? '+' : '' }}{{ stats.activityGrowth }}%
          </span>
          <span class="text-xs text-gray-400 ml-1">vs 上周</span>
        </div>
      </div>

      <!-- 发布文章数 -->
      <div class="bg-gradient-to-br from-pink-600/20 to-pink-800/20 rounded-xl p-4 border border-pink-600/30 hover:border-pink-500/50 transition-all duration-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-pink-300 mb-1">发布文章</p>
            <p class="text-2xl font-bold text-pink-100">{{ stats.publishedArticles }}</p>
          </div>
          <div class="w-10 h-10 bg-pink-600/30 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-pink-400" fill="currentColor" viewBox="0 0 24 24">
              <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
            </svg>
          </div>
        </div>
        <div class="flex items-center mt-2">
          <span :class="['text-xs', stats.articleGrowth >= 0 ? 'text-green-400' : 'text-red-400']">
            {{ stats.articleGrowth >= 0 ? '+' : '' }}{{ stats.articleGrowth }}%
          </span>
          <span class="text-xs text-gray-400 ml-1">vs 上周</span>
        </div>
      </div>

      <!-- 总浏览量 -->
      <div class="bg-gradient-to-br from-yellow-600/20 to-yellow-800/20 rounded-xl p-4 border border-yellow-600/30 hover:border-yellow-500/50 transition-all duration-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-yellow-300 mb-1">总浏览量</p>
            <p class="text-2xl font-bold text-yellow-100">{{ formatNumber(stats.totalViews) }}</p>
          </div>
          <div class="w-10 h-10 bg-yellow-600/30 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-yellow-400" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
            </svg>
          </div>
        </div>
        <div class="flex items-center mt-2">
          <span :class="['text-xs', stats.viewGrowth >= 0 ? 'text-green-400' : 'text-red-400']">
            {{ stats.viewGrowth >= 0 ? '+' : '' }}{{ stats.viewGrowth }}%
          </span>
          <span class="text-xs text-gray-400 ml-1">vs 上周</span>
        </div>
      </div>

      <!-- 解锁成就 -->
      <div class="bg-gradient-to-br from-green-600/20 to-green-800/20 rounded-xl p-4 border border-green-600/30 hover:border-green-500/50 transition-all duration-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-green-300 mb-1">解锁成就</p>
            <p class="text-2xl font-bold text-green-100">{{ stats.achievements }}</p>
          </div>
          <div class="w-10 h-10 bg-green-600/30 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-green-400" fill="currentColor" viewBox="0 0 24 24">
              <path d="M9 11H7v6h2v-6zm4 0h-2v6h2v-6zm4 0h-2v6h2v-6zm2.5-5H19V4c0-1.1-.9-2-2-2H7c-1.1 0-2 .9-2 2v2H3.5c-.28 0-.5.22-.5.5s.22.5.5.5h1v12c0 1.1.9 2 2 2h10c1.1 0 2-.9 2-2V7h1c.28 0 .5-.22.5-.5s-.22-.5-.5-.5z"/>
            </svg>
          </div>
        </div>
        <div class="flex items-center mt-2">
          <span class="text-xs text-green-400">+{{ stats.newAchievements }}</span>
          <span class="text-xs text-gray-400 ml-1">本周新增</span>
        </div>
      </div>
    </div>

    <!-- 活动类型分布 -->
    <div class="mb-6">
      <h4 class="text-sm font-semibold text-gray-300 mb-3">活动类型分布</h4>
      <div class="space-y-2">
        <div v-for="type in activityTypes" :key="type.name" class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span :class="['w-3 h-3 rounded-full', type.color]"></span>
            <span class="text-sm text-gray-300">{{ type.name }}</span>
          </div>
          <div class="flex items-center gap-2">
            <span class="text-sm text-gray-400">{{ type.count }}</span>
            <div class="w-16 h-2 bg-gray-700 rounded-full overflow-hidden">
              <div 
                :class="['h-full transition-all duration-500', type.color]" 
                :style="{ width: `${type.percentage}%` }"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最活跃时段 -->
    <div>
      <h4 class="text-sm font-semibold text-gray-300 mb-3">最活跃时段</h4>
      <div class="grid grid-cols-4 gap-2">
        <div v-for="(hour, index) in activeHours" :key="index" class="text-center">
          <div class="text-xs text-gray-400 mb-1">{{ hour.time }}</div>
          <div 
            class="h-8 bg-gray-700 rounded flex items-end justify-center overflow-hidden transition-all duration-300 hover:bg-gray-600"
          >
            <div 
              :class="['w-full transition-all duration-500', getActivityIntensityColor(hour.intensity)]"
              :style="{ height: `${hour.intensity}%` }"
            ></div>
          </div>
          <div class="text-xs text-gray-500 mt-1">{{ hour.count }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useActivityFeed } from '../composables/useActivityFeed'
import { useArticlesStore } from '../stores/articles'

const { activities } = useActivityFeed()
const articlesStore = useArticlesStore()

// 计算统计数据
const stats = computed(() => {
  const totalActivities = activities.value.length
  const publishedArticles = articlesStore.articles.filter(a => a.status === 1).length
  const totalViews = articlesStore.articles.reduce((sum, article) => sum + (article.views || 0), 0)
  const achievements = activities.value.filter(a => a.type === 'achievement').length

  // 模拟增长率数据（实际项目中应该从API获取）
  return {
    totalActivities,
    publishedArticles,
    totalViews,
    achievements,
    activityGrowth: Math.floor(Math.random() * 40) - 10, // -10 到 30
    articleGrowth: Math.floor(Math.random() * 50) - 5,   // -5 到 45
    viewGrowth: Math.floor(Math.random() * 80) + 10,     // 10 到 90
    newAchievements: Math.floor(achievements * 0.3)      // 30% 为新成就
  }
})

// 活动类型分布
const activityTypes = computed(() => {
  const typeCount = activities.value.reduce((acc, activity) => {
    acc[activity.type] = (acc[activity.type] || 0) + 1
    return acc
  }, {} as Record<string, number>)

  const total = activities.value.length || 1
  
  const types = [
    { name: '文章发布', key: 'article', color: 'bg-cyan-400', count: typeCount.article || 0 },
    { name: '内容更新', key: 'update', color: 'bg-pink-400', count: typeCount.update || 0 },
    { name: '成就解锁', key: 'achievement', color: 'bg-yellow-400', count: typeCount.achievement || 0 },
    { name: '系统活动', key: 'system', color: 'bg-green-400', count: typeCount.system || 0 },
    { name: '评论互动', key: 'comment', color: 'bg-blue-400', count: typeCount.comment || 0 },
    { name: '点赞收藏', key: 'like', color: 'bg-red-400', count: typeCount.like || 0 },
    { name: '分享传播', key: 'share', color: 'bg-purple-400', count: typeCount.share || 0 },
    { name: '社交关注', key: 'follow', color: 'bg-indigo-400', count: typeCount.follow || 0 }
  ]

  return types.map(type => ({
    ...type,
    percentage: Math.round((type.count / total) * 100)
  }))
})

// 最活跃时段（模拟数据）
const activeHours = computed(() => {
  const hours = ['早晨', '上午', '下午', '晚上']
  const data = [
    { time: '6-9', intensity: 60, count: 12 },
    { time: '9-12', intensity: 85, count: 18 },
    { time: '12-18', intensity: 95, count: 22 },
    { time: '18-22', intensity: 75, count: 16 }
  ]
  
  return data.map((item, index) => ({
    ...item,
    time: hours[index]
  }))
})

// 格式化数字
const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

// 获取活动强度颜色
const getActivityIntensityColor = (intensity: number): string => {
  if (intensity >= 80) return 'bg-gradient-to-t from-purple-600 to-purple-400'
  if (intensity >= 60) return 'bg-gradient-to-t from-blue-600 to-blue-400'
  if (intensity >= 40) return 'bg-gradient-to-t from-cyan-600 to-cyan-400'
  return 'bg-gradient-to-t from-gray-600 to-gray-400'
}
</script>

<style scoped>
/* 添加渐变动画 */
.activity-bar {
  background: linear-gradient(45deg, transparent 25%, rgba(255,255,255,0.1) 25%, rgba(255,255,255,0.1) 50%, transparent 50%, transparent 75%, rgba(255,255,255,0.1) 75%);
  background-size: 8px 8px;
  animation: slide 1s linear infinite;
}

@keyframes slide {
  0% { background-position: 0 0; }
  100% { background-position: 8px 8px; }
}

/* 悬浮效果 */
.activity-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(168, 85, 247, 0.15);
}
</style> 