<template>
  <div class="bg-gradient-to-br from-gray-800 via-gray-900 to-gray-950 rounded-2xl p-6 shadow-lg border border-gray-700 hover:border-cyan-600/50 transition-all duration-300">
    <div class="flex items-center justify-between mb-4">
      <h3 class="text-xl font-bold text-cyan-300 flex items-center gap-2">
        <svg class="w-6 h-6 text-pink-400" fill="currentColor" viewBox="0 0 24 24">
          <path d="M19 21l-7-5-7 5V5a2 2 0 012-2h10a2 2 0 012 2z"/>
        </svg>
        创作动态
      </h3>
      <!-- 刷新按钮 -->
      <button 
        @click="onRefresh"
        :disabled="isLoading"
        class="p-2 rounded-lg bg-cyan-600/20 hover:bg-cyan-600/30 text-cyan-400 hover:text-cyan-300 transition-all duration-200 group"
        title="刷新动态"
      >
        <svg 
          :class="['w-4 h-4 transition-transform duration-300', isLoading && 'animate-spin']" 
          fill="none" 
          stroke="currentColor" 
          viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
        </svg>
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="isLoading && displayActivities.length === 0" class="flex items-center justify-center py-8">
      <div class="flex items-center space-x-3 text-cyan-400">
        <div class="w-5 h-5 border-2 border-cyan-400 border-t-transparent rounded-full animate-spin"></div>
        <span class="text-sm">加载动态中...</span>
      </div>
    </div>

    <!-- 活动列表 -->
    <ul v-else-if="displayActivities.length > 0" class="space-y-3 max-h-96 overflow-y-auto custom-scrollbar">
      <li 
        v-for="activity in displayActivities" 
        :key="activity.id" 
        class="activity-item flex items-start gap-3 p-3 rounded-lg bg-gray-800/30 hover:bg-gray-800/50 transition-all duration-200 group cursor-pointer"
        @click="handleActivityClick(activity)"
      >
        <!-- 活动类型指示器 -->
        <div class="flex-shrink-0 mt-1">
          <span 
            class="inline-block w-3 h-3 rounded-full shadow-sm transition-all duration-200 group-hover:scale-110" 
            :class="activityIndicatorClass(activity.type)"
          ></span>
        </div>
        
        <!-- 活动内容 -->
        <div class="flex-1 min-w-0">
          <div class="text-cyan-100 text-sm font-medium leading-relaxed group-hover:text-cyan-50 transition-colors duration-200">
            {{ activity.content }}
          </div>
          
          <!-- 元数据信息 -->
          <div v-if="activity.metadata?.category" class="flex items-center gap-2 mt-1">
            <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs bg-purple-600/20 text-purple-300 border border-purple-600/30">
              {{ activity.metadata.category }}
            </span>
          </div>
          
          <!-- 时间戳 -->
          <div class="flex items-center justify-between mt-2">
            <div class="text-xs text-gray-400 group-hover:text-gray-300 transition-colors duration-200">
              {{ activity.date }}
            </div>
            
            <!-- 文章链接指示 -->
            <div v-if="activity.articleId" class="opacity-0 group-hover:opacity-100 transition-opacity duration-200">
              <svg class="w-3 h-3 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
              </svg>
            </div>
          </div>
        </div>
      </li>
    </ul>

    <!-- 空状态 -->
    <div v-else class="text-center py-8">
      <div class="w-16 h-16 mx-auto mb-4 bg-gray-700/50 rounded-full flex items-center justify-center">
        <svg class="w-8 h-8 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
        </svg>
      </div>
      <p class="text-gray-400 text-sm">暂无创作动态</p>
      <p class="text-gray-500 text-xs mt-1">发布文章后，动态将在这里显示</p>
    </div>

    <!-- 查看更多 -->
    <div v-if="displayActivities.length > 0" class="mt-4 text-center border-t border-gray-700/50 pt-3">
      <button 
        @click="viewAllActivities"
        class="text-xs text-cyan-400 hover:text-cyan-300 transition-colors duration-200 flex items-center gap-1 mx-auto group"
      >
        <span>查看全部动态</span>
        <svg class="w-3 h-3 transition-transform duration-200 group-hover:translate-x-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useActivityFeed, type Activity } from '../composables/useActivityFeed'

// Props 保持向后兼容
interface Props {
  activities?: Activity[]
  maxItems?: number
}

const props = withDefaults(defineProps<Props>(), {
  activities: () => [],
  maxItems: 8
})

const router = useRouter()
const { 
  activities: realtimeActivities, 
  isLoading, 
  refreshActivities 
} = useActivityFeed()

// 使用实时活动数据或传入的活动数据
const displayActivities = computed(() => {
  const sourceActivities = props.activities.length > 0 ? props.activities : realtimeActivities.value
  return sourceActivities.slice(0, props.maxItems)
})

// 活动类型指示器样式
function activityIndicatorClass(type: string): string {
  switch (type) {
    case 'article': 
      return 'bg-cyan-400 shadow-cyan-400/50'
    case 'achievement': 
      return 'bg-yellow-400 shadow-yellow-400/50'
    case 'update': 
      return 'bg-pink-400 shadow-pink-400/50'
    case 'system': 
      return 'bg-green-400 shadow-green-400/50'
    case 'comment':
      return 'bg-blue-400 shadow-blue-400/50'
    case 'like':
      return 'bg-red-400 shadow-red-400/50'
    case 'share':
      return 'bg-purple-400 shadow-purple-400/50'
    case 'follow':
      return 'bg-indigo-400 shadow-indigo-400/50'
    case 'login':
      return 'bg-emerald-400 shadow-emerald-400/50'
    case 'tag':
      return 'bg-orange-400 shadow-orange-400/50'
    case 'category':
      return 'bg-teal-400 shadow-teal-400/50'
    default: 
      return 'bg-gray-400 shadow-gray-400/50'
  }
}

// 处理活动点击
const handleActivityClick = (activity: Activity) => {
  if (activity.articleId) {
    // 跳转到文章详情页
    router.push(`/article/${activity.articleId}`)
  } else if (activity.type === 'achievement') {
    // 处理成就点击，可以显示成就详情或跳转到成就页面
    console.log('查看成就详情:', activity.metadata?.title)
  }
}

// 刷新动态
const onRefresh = async () => {
  await refreshActivities()
}

// 查看全部动态
const viewAllActivities = () => {
  // 可以跳转到一个专门的活动时间线页面
  router.push('/profile?tab=activities')
}
</script>

<style scoped>
/* 自定义滚动条 */
.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: rgba(6, 182, 212, 0.3) transparent;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(6, 182, 212, 0.3);
  border-radius: 2px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(6, 182, 212, 0.5);
}

/* 活动项动画 */
.activity-item {
  animation: slideInUp 0.3s ease-out;
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

/* 悬浮效果增强 */
.activity-item:hover {
  transform: translateX(2px);
  box-shadow: 0 4px 12px rgba(6, 182, 212, 0.1);
}

/* 指示器脉冲效果 */
.activity-item:hover .inline-block {
  box-shadow: 0 0 10px currentColor;
}
</style> 