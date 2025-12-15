<template>
  <div class="bg-gradient-to-br from-gray-800 via-gray-900 to-gray-950 rounded-2xl p-6 shadow-lg border border-gray-700 hover:border-violet-600/50 transition-all duration-300">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-bold text-violet-300 flex items-center gap-2">
        <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
          <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
        </svg>
        活动洞察
      </h3>
      <div class="flex items-center gap-2">
        <select 
          v-model="selectedPeriod"
          class="px-3 py-1 rounded-lg bg-gray-700 text-gray-300 text-xs border border-gray-600 focus:border-violet-500 focus:outline-none"
        >
          <option value="week">过去一周</option>
          <option value="month">过去一月</option>
          <option value="quarter">过去三月</option>
        </select>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="grid grid-cols-2 gap-4 mb-6">
      <!-- 活跃度评分 -->
      <div class="bg-gradient-to-br from-violet-600/20 to-purple-800/20 rounded-xl p-4 border border-violet-600/30">
        <div class="flex items-center justify-between mb-3">
          <div>
            <p class="text-xs text-violet-300 mb-1">活跃度评分</p>
            <p class="text-2xl font-bold text-violet-100">{{ insights.activityScore }}</p>
          </div>
          <div class="w-12 h-12 relative">
            <!-- 圆环进度条 -->
            <svg class="w-12 h-12 transform -rotate-90" viewBox="0 0 48 48">
              <circle cx="24" cy="24" r="20" stroke="currentColor" stroke-width="4" fill="none" class="text-gray-600"/>
              <circle 
                cx="24" cy="24" r="20" 
                stroke="currentColor" 
                stroke-width="4" 
                fill="none" 
                class="text-violet-400"
                :stroke-dasharray="circumference"
                :stroke-dashoffset="circumference - (insights.activityScore / 100) * circumference"
                stroke-linecap="round"
              />
            </svg>
            <div class="absolute inset-0 flex items-center justify-center">
              <span class="text-xs font-bold text-violet-300">{{ insights.activityScore }}%</span>
            </div>
          </div>
        </div>
        <div class="flex items-center text-xs">
          <span :class="insights.scoreChange >= 0 ? 'text-green-400' : 'text-red-400'">
            {{ insights.scoreChange >= 0 ? '+' : '' }}{{ insights.scoreChange }}%
          </span>
          <span class="text-gray-400 ml-1">vs 上期</span>
        </div>
      </div>

      <!-- 影响力指数 -->
      <div class="bg-gradient-to-br from-blue-600/20 to-cyan-800/20 rounded-xl p-4 border border-blue-600/30">
        <div class="flex items-center justify-between mb-3">
          <div>
            <p class="text-xs text-blue-300 mb-1">影响力指数</p>
            <p class="text-2xl font-bold text-blue-100">{{ insights.influenceIndex }}</p>
          </div>
          <div class="w-10 h-10 bg-blue-600/30 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-blue-400" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
            </svg>
          </div>
        </div>
        <div class="flex items-center text-xs">
          <span :class="insights.influenceChange >= 0 ? 'text-green-400' : 'text-red-400'">
            {{ insights.influenceChange >= 0 ? '+' : '' }}{{ insights.influenceChange }}%
          </span>
          <span class="text-gray-400 ml-1">vs 上期</span>
        </div>
      </div>
    </div>

    <!-- 活动趋势图 -->
    <div class="mb-6">
      <h4 class="text-sm font-semibold text-gray-300 mb-3 flex items-center gap-2">
        <svg class="w-4 h-4 text-violet-400" fill="currentColor" viewBox="0 0 24 24">
          <path d="M16 6l2.29 2.29-4.88 4.88-4-4L2 16.59 3.41 18l6-6 4 4 6.3-6.29L22 12V6z"/>
        </svg>
        活动趋势
      </h4>
      <div class="bg-gray-800/30 rounded-lg p-4">
        <div class="flex items-end justify-between h-24 gap-1">
          <div v-for="(day, index) in insights.trendData" :key="index" class="flex-1 flex flex-col items-center">
            <div 
              class="w-full bg-gradient-to-t from-violet-600 to-violet-400 rounded-t transition-all duration-500"
              :style="{ height: `${(day.value / Math.max(...insights.trendData.map(d => d.value))) * 100}%` }"
            ></div>
            <span class="text-xs text-gray-400 mt-1">{{ day.label }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 智能建议 -->
    <div class="mb-6">
      <h4 class="text-sm font-semibold text-gray-300 mb-3 flex items-center gap-2">
        <svg class="w-4 h-4 text-yellow-400" fill="currentColor" viewBox="0 0 24 24">
          <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
        </svg>
        智能建议
      </h4>
      <div class="space-y-2">
        <div 
          v-for="suggestion in insights.suggestions" 
          :key="suggestion.id"
          class="flex items-start gap-3 p-3 rounded-lg bg-gray-800/40 hover:bg-gray-800/60 transition-all duration-200 border border-gray-700/50"
        >
          <div class="flex-shrink-0 w-6 h-6 rounded-full flex items-center justify-center mt-0.5"
               :class="getSuggestionColor(suggestion.type)">
            <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 24 24">
              <path :d="getSuggestionIcon(suggestion.type)"/>
            </svg>
          </div>
          <div class="flex-1">
            <p class="text-sm text-gray-200 font-medium">{{ suggestion.title }}</p>
            <p class="text-xs text-gray-400 mt-1">{{ suggestion.description }}</p>
            <div v-if="suggestion.impact" class="flex items-center mt-2">
              <span class="text-xs text-green-400 bg-green-600/20 px-2 py-0.5 rounded-full">
                预期提升 {{ suggestion.impact }}%
              </span>
            </div>
          </div>
          <button 
            class="flex-shrink-0 p-1 rounded text-violet-400 hover:text-violet-300 hover:bg-violet-600/20 transition-all duration-200"
            @click="applySuggestion(suggestion)"
            title="应用建议"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- 内容质量分析 -->
    <div class="mb-6">
      <h4 class="text-sm font-semibold text-gray-300 mb-3 flex items-center gap-2">
        <svg class="w-4 h-4 text-emerald-400" fill="currentColor" viewBox="0 0 24 24">
          <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
        </svg>
        内容质量分析
      </h4>
      <div class="grid grid-cols-2 gap-4">
        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <span class="text-xs text-gray-300">文章深度</span>
            <span class="text-xs text-emerald-400 font-medium">{{ insights.contentQuality.depth }}%</span>
          </div>
          <div class="w-full bg-gray-700 rounded-full h-1.5">
            <div 
              class="bg-gradient-to-r from-emerald-600 to-emerald-400 h-1.5 rounded-full transition-all duration-500"
              :style="{ width: `${insights.contentQuality.depth}%` }"
            ></div>
          </div>
          
          <div class="flex items-center justify-between">
            <span class="text-xs text-gray-300">互动质量</span>
            <span class="text-xs text-blue-400 font-medium">{{ insights.contentQuality.engagement }}%</span>
          </div>
          <div class="w-full bg-gray-700 rounded-full h-1.5">
            <div 
              class="bg-gradient-to-r from-blue-600 to-blue-400 h-1.5 rounded-full transition-all duration-500"
              :style="{ width: `${insights.contentQuality.engagement}%` }"
            ></div>
          </div>
        </div>
        
        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <span class="text-xs text-gray-300">原创度</span>
            <span class="text-xs text-purple-400 font-medium">{{ insights.contentQuality.originality }}%</span>
          </div>
          <div class="w-full bg-gray-700 rounded-full h-1.5">
            <div 
              class="bg-gradient-to-r from-purple-600 to-purple-400 h-1.5 rounded-full transition-all duration-500"
              :style="{ width: `${insights.contentQuality.originality}%` }"
            ></div>
          </div>
          
          <div class="flex items-center justify-between">
            <span class="text-xs text-gray-300">阅读体验</span>
            <span class="text-xs text-orange-400 font-medium">{{ insights.contentQuality.readability }}%</span>
          </div>
          <div class="w-full bg-gray-700 rounded-full h-1.5">
            <div 
              class="bg-gradient-to-r from-orange-600 to-orange-400 h-1.5 rounded-full transition-all duration-500"
              :style="{ width: `${insights.contentQuality.readability}%` }"
            ></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 目标设定 -->
    <div>
      <h4 class="text-sm font-semibold text-gray-300 mb-3 flex items-center gap-2">
        <svg class="w-4 h-4 text-cyan-400" fill="currentColor" viewBox="0 0 24 24">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
        </svg>
        本期目标
      </h4>
      <div class="space-y-3">
        <div 
          v-for="goal in insights.goals" 
          :key="goal.id"
          class="flex items-center justify-between p-3 rounded-lg bg-gray-800/40 border border-gray-700/50"
        >
          <div class="flex items-center gap-3">
            <div class="w-8 h-8 rounded-full flex items-center justify-center"
                 :class="goal.completed ? 'bg-green-600/30 text-green-400' : 'bg-gray-600/30 text-gray-400'">
              <svg v-if="goal.completed" class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
              </svg>
              <svg v-else class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z"/>
              </svg>
            </div>
            <div>
              <p class="text-sm text-gray-200 font-medium">{{ goal.title }}</p>
              <p class="text-xs text-gray-400">{{ goal.current }}/{{ goal.target }} {{ goal.unit }}</p>
            </div>
          </div>
          <div class="text-right">
            <div class="text-xs text-gray-300 font-medium">{{ Math.round((goal.current / goal.target) * 100) }}%</div>
            <div class="w-16 bg-gray-700 rounded-full h-1 mt-1">
              <div 
                class="bg-gradient-to-r from-cyan-600 to-cyan-400 h-1 rounded-full transition-all duration-500"
                :style="{ width: `${Math.min(100, (goal.current / goal.target) * 100)}%` }"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useActivityFeed } from '../composables/useActivityFeed'
import { useArticlesStore } from '../stores/articles'

const { activities } = useActivityFeed()
const articlesStore = useArticlesStore()
const selectedPeriod = ref('week')

// 圆环周长
const circumference = 2 * Math.PI * 20

// 模拟洞察数据
const insights = computed(() => {
  const publishedArticles = articlesStore.articles.filter(a => a.status === 1).length
  const totalViews = articlesStore.articles.reduce((sum, article) => sum + (article.views || 0), 0)
  
  return {
    activityScore: Math.min(100, publishedArticles * 8 + Math.random() * 20),
    scoreChange: Math.floor(Math.random() * 30) - 10,
    influenceIndex: Math.min(999, totalViews / 10 + Math.random() * 100),
    influenceChange: Math.floor(Math.random() * 40) - 15,
    
    trendData: [
      { label: '周一', value: 8 },
      { label: '周二', value: 12 },
      { label: '周三', value: 6 },
      { label: '周四', value: 15 },
      { label: '周五', value: 10 },
      { label: '周六', value: 4 },
      { label: '周日', value: 9 }
    ],
    
    suggestions: [
      {
        id: 1,
        type: 'content',
        title: '增加技术深度内容',
        description: '根据读者反馈，建议在文章中加入更多实际案例和代码示例',
        impact: 15
      },
      {
        id: 2,
        type: 'timing',
        title: '优化发布时间',
        description: '数据显示周二和周四发布的文章获得更多互动',
        impact: 8
      },
      {
        id: 3,
        type: 'social',
        title: '加强社区互动',
        description: '主动回复评论和参与讨论可以提升你的影响力',
        impact: 12
      }
    ],
    
    contentQuality: {
      depth: 78,
      engagement: 65,
      originality: 89,
      readability: 72
    },
    
    goals: [
      {
        id: 1,
        title: '发布新文章',
        current: publishedArticles,
        target: publishedArticles + 3,
        unit: '篇',
        completed: false
      },
      {
        id: 2,
        title: '获得浏览量',
        current: totalViews,
        target: totalViews + 500,
        unit: '次',
        completed: false
      },
      {
        id: 3,
        title: '互动回复',
        current: 8,
        target: 15,
        unit: '条',
        completed: false
      }
    ]
  }
})

// 获取建议类型颜色
const getSuggestionColor = (type: string): string => {
  switch (type) {
    case 'content': return 'bg-blue-600/30 text-blue-400'
    case 'timing': return 'bg-green-600/30 text-green-400'
    case 'social': return 'bg-purple-600/30 text-purple-400'
    default: return 'bg-gray-600/30 text-gray-400'
  }
}

// 获取建议图标
const getSuggestionIcon = (type: string): string => {
  switch (type) {
    case 'content': return 'M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z'
    case 'timing': return 'M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8z'
    case 'social': return 'M16 4c0-1.11.89-2 2-2s2 .89 2 2-.89 2-2 2-2-.89-2-2zm4 18v-6h2.5l-2.54-7.63A2.97 2.97 0 0017.13 7H16.5c-.8 0-1.5.7-1.5 1.5v6c0 1.1.9 2 2 2h1v6h2z'
    default: return 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z'
  }
}

// 应用建议
const applySuggestion = (suggestion: any) => {
  console.log('应用建议:', suggestion.title)
  // 这里可以实现具体的建议应用逻辑
}
</script>

<style scoped>
/* 圆环进度条动画 */
circle {
  transition: stroke-dashoffset 0.5s ease-in-out;
}

/* 趋势图动画 */
.trend-bar {
  animation: growUp 1s ease-out;
}

@keyframes growUp {
  from {
    height: 0;
  }
  to {
    height: var(--height);
  }
}

/* 进度条动画 */
.progress-bar {
  animation: fillProgress 1s ease-out;
}

@keyframes fillProgress {
  from {
    width: 0;
  }
  to {
    width: var(--width);
  }
}

/* 悬浮效果 */
.suggestion-item:hover {
  transform: translateX(2px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.15);
}

/* 目标项动画 */
.goal-item {
  animation: slideInUp 0.4s ease-out;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style> 