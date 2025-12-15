<template>
  <div class="bg-gradient-to-br from-gray-800 via-gray-900 to-gray-950 rounded-2xl p-6 shadow-lg border border-gray-700 hover:border-orange-600/50 transition-all duration-300">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-bold text-orange-300 flex items-center gap-2">
        <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
          <path d="M16 4c0-1.11.89-2 2-2s2 .89 2 2-.89 2-2 2-2-.89-2-2zm4 18v-6h2.5l-2.54-7.63A2.97 2.97 0 0017.13 7H16.5c-.8 0-1.5.7-1.5 1.5v6c0 1.1.9 2 2 2h1v6h2zm-13.5-6c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm1.5 6v-6h-2v-2.5c0-1.1.9-2 2-2h6c1.1 0 2 .9 2 2v8h-2v-6H8v6H6.5z"/>
        </svg>
        最近访客
      </h3>
      <div class="flex items-center gap-2">
        <span class="text-xs text-gray-400">{{ totalVisitors }} 位访客</span>
        <button 
          @click="refreshVisitors"
          :disabled="isLoading"
          class="p-1.5 rounded-lg bg-orange-600/20 hover:bg-orange-600/30 text-orange-400 hover:text-orange-300 transition-all duration-200"
          title="刷新访客"
        >
          <svg 
            :class="['w-3 h-3 transition-transform duration-300', isLoading && 'animate-spin']" 
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
          </svg>
        </button>
      </div>
    </div>

    <!-- 访客列表 -->
    <div v-if="visitors.length > 0" class="space-y-3 max-h-80 overflow-y-auto custom-scrollbar">
      <div 
        v-for="visitor in visitors" 
        :key="visitor.id"
        class="visitor-item flex items-center gap-3 p-3 rounded-xl bg-gray-800/30 hover:bg-gray-800/50 transition-all duration-200 cursor-pointer group"
        @click="handleVisitorClick(visitor)"
      >
        <!-- 头像 -->
        <div class="relative flex-shrink-0">
          <img 
            :src="visitor.avatar" 
            :alt="visitor.name"
            class="w-10 h-10 rounded-full object-cover border-2 border-gray-600 group-hover:border-orange-500/50 transition-all duration-200"
            @error="handleImageError"
          >
          <!-- 在线状态 -->
          <div 
            v-if="visitor.isOnline"
            class="absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-green-400 rounded-full border-2 border-gray-800 shadow-sm"
            title="在线"
          ></div>
          <!-- 新访客标识 -->
          <div 
            v-if="visitor.isNew"
            class="absolute -top-1 -right-1 w-3 h-3 bg-orange-400 rounded-full border border-gray-800 animate-pulse"
            title="新访客"
          ></div>
        </div>

        <!-- 访客信息 -->
        <div class="flex-1 min-w-0">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <h4 class="text-sm font-medium text-gray-200 group-hover:text-gray-100 transition-colors truncate">
                {{ visitor.name }}
              </h4>
              <!-- VIP 标识 -->
              <span 
                v-if="visitor.isVip"
                class="inline-flex items-center px-1.5 py-0.5 rounded-full text-xs bg-gradient-to-r from-yellow-600/30 to-orange-600/30 text-yellow-300 border border-yellow-600/50"
                title="VIP用户"
              >
                <svg class="w-2.5 h-2.5 mr-0.5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                </svg>
                VIP
              </span>
              <!-- 认证标识 -->
              <span 
                v-if="visitor.isVerified"
                class="inline-flex items-center text-blue-400"
                title="已认证"
              >
                <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                </svg>
              </span>
            </div>
            <span class="text-xs text-gray-400">{{ visitor.visitTime }}</span>
          </div>

          <!-- 个人简介 -->
          <p class="text-xs text-gray-400 mt-1 truncate group-hover:text-gray-300 transition-colors">
            {{ visitor.bio || '这个人很懒，什么都没留下...' }}
          </p>

          <!-- 互动信息 -->
          <div class="flex items-center gap-3 mt-2 text-xs">
            <span v-if="visitor.articlesCount > 0" class="flex items-center gap-1 text-cyan-400">
              <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 24 24">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
              </svg>
              {{ visitor.articlesCount }} 篇文章
            </span>
            <span v-if="visitor.commentsCount > 0" class="flex items-center gap-1 text-green-400">
              <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 24 24">
                <path d="M21.99 4c0-1.1-.89-2-2-2H4c-1.1 0-2 .9-2 2v12c0 1.1.89 2 2 2h14l4 4-.01-18z"/>
              </svg>
              {{ visitor.commentsCount }} 条评论
            </span>
            <span v-if="visitor.likesCount > 0" class="flex items-center gap-1 text-pink-400">
              <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
              </svg>
              {{ visitor.likesCount }} 个赞
            </span>
          </div>
        </div>

        <!-- 访问次数 -->
        <div class="flex flex-col items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
          <div class="w-8 h-8 bg-orange-600/20 rounded-lg flex items-center justify-center">
            <span class="text-xs font-medium text-orange-300">{{ visitor.visitCount }}</span>
          </div>
          <span class="text-xs text-gray-500">次访问</span>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-8">
      <div class="w-16 h-16 mx-auto mb-4 bg-gray-700/50 rounded-full flex items-center justify-center">
        <svg class="w-8 h-8 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
        </svg>
      </div>
      <p class="text-gray-400 text-sm">暂无访客记录</p>
      <p class="text-gray-500 text-xs mt-1">分享你的资料页面吸引更多访客</p>
    </div>

    <!-- 统计信息 -->
    <div v-if="visitors.length > 0" class="mt-6 pt-4 border-t border-gray-700/50">
      <div class="grid grid-cols-3 gap-4 text-center">
        <div>
          <div class="text-lg font-bold text-orange-300">{{ todayVisitors }}</div>
          <div class="text-xs text-gray-400">今日访客</div>
        </div>
        <div>
          <div class="text-lg font-bold text-cyan-300">{{ weeklyVisitors }}</div>
          <div class="text-xs text-gray-400">本周访客</div>
        </div>
        <div>
          <div class="text-lg font-bold text-green-300">{{ monthlyVisitors }}</div>
          <div class="text-xs text-gray-400">本月访客</div>
        </div>
      </div>
    </div>

    <!-- 查看全部 -->
    <div v-if="visitors.length >= maxDisplay" class="mt-4 text-center">
      <button 
        @click="viewAllVisitors"
        class="text-xs text-orange-400 hover:text-orange-300 transition-colors duration-200 flex items-center gap-1 mx-auto group"
      >
        <span>查看全部访客</span>
        <svg class="w-3 h-3 transition-transform duration-200 group-hover:translate-x-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

interface Visitor {
  id: string
  name: string
  avatar: string
  bio?: string
  visitTime: string
  visitCount: number
  isOnline: boolean
  isNew: boolean
  isVip: boolean
  isVerified: boolean
  articlesCount: number
  commentsCount: number
  likesCount: number
}

const router = useRouter()
const isLoading = ref(false)
const maxDisplay = 8

// 模拟访客数据
const visitors = ref<Visitor[]>([
  {
    id: '1',
    name: '李明轩',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=40&h=40&fit=crop&crop=face',
    bio: '全栈开发工程师，热爱技术分享',
    visitTime: '5分钟前',
    visitCount: 12,
    isOnline: true,
    isNew: true,
    isVip: true,
    isVerified: true,
    articlesCount: 25,
    commentsCount: 68,
    likesCount: 156
  },
  {
    id: '2',
    name: '王小美',
    avatar: 'https://images.unsplash.com/photo-1494790108755-2616b612b829?w=40&h=40&fit=crop&crop=face',
    bio: 'UI/UX设计师，专注用户体验',
    visitTime: '15分钟前',
    visitCount: 3,
    isOnline: true,
    isNew: false,
    isVip: false,
    isVerified: true,
    articlesCount: 8,
    commentsCount: 23,
    likesCount: 45
  },
  {
    id: '3',
    name: '张技术',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=40&h=40&fit=crop&crop=face',
    bio: '前端架构师，Vue.js 专家',
    visitTime: '2小时前',
    visitCount: 8,
    isOnline: false,
    isNew: false,
    isVip: true,
    isVerified: true,
    articlesCount: 42,
    commentsCount: 134,
    likesCount: 289
  },
  {
    id: '4',
    name: '刘产品',
    avatar: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=40&h=40&fit=crop&crop=face',
    bio: '产品经理，关注用户需求',
    visitTime: '3小时前',
    visitCount: 5,
    isOnline: false,
    isNew: true,
    isVip: false,
    isVerified: false,
    articlesCount: 15,
    commentsCount: 39,
    likesCount: 72
  },
  {
    id: '5',
    name: '陈运营',
    avatar: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=40&h=40&fit=crop&crop=face',
    bio: '运营专家，数据驱动增长',
    visitTime: '昨天',
    visitCount: 2,
    isOnline: false,
    isNew: false,
    isVip: false,
    isVerified: true,
    articlesCount: 6,
    commentsCount: 18,
    likesCount: 31
  },
  {
    id: '6',
    name: '周学生',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=40&h=40&fit=crop&crop=face',
    bio: '计算机专业学生，正在学习中',
    visitTime: '2天前',
    visitCount: 1,
    isOnline: false,
    isNew: true,
    isVip: false,
    isVerified: false,
    articlesCount: 2,
    commentsCount: 5,
    likesCount: 8
  }
])

// 总访客数
const totalVisitors = computed(() => visitors.value.length)

// 今日访客
const todayVisitors = computed(() => {
  return visitors.value.filter(v => 
    v.visitTime.includes('分钟前') || 
    v.visitTime.includes('小时前') || 
    v.visitTime === '刚刚'
  ).length
})

// 本周访客
const weeklyVisitors = computed(() => {
  return visitors.value.filter(v => 
    !v.visitTime.includes('周前') && 
    !v.visitTime.includes('个月前')
  ).length
})

// 本月访客
const monthlyVisitors = computed(() => {
  return visitors.value.filter(v => 
    !v.visitTime.includes('个月前')
  ).length
})

// 处理头像加载错误
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = 'https://via.placeholder.com/40x40/374151/9CA3AF?text=?'
}

// 处理访客点击
const handleVisitorClick = (visitor: Visitor) => {
  // 跳转到访客的个人资料页面
  router.push(`/profile/${visitor.id}`)
}

// 刷新访客
const refreshVisitors = async () => {
  isLoading.value = true
  
  // 模拟API请求
  await new Promise(resolve => setTimeout(resolve, 1000))
  
  // 模拟新增访客
  const newVisitor: Visitor = {
    id: Date.now().toString(),
    name: '新访客',
    avatar: 'https://via.placeholder.com/40x40/6366F1/FFFFFF?text=N',
    bio: '刚刚访问了你的页面',
    visitTime: '刚刚',
    visitCount: 1,
    isOnline: true,
    isNew: true,
    isVip: false,
    isVerified: false,
    articlesCount: 0,
    commentsCount: 0,
    likesCount: 0
  }
  
  visitors.value.unshift(newVisitor)
  
  // 限制显示数量
  if (visitors.value.length > maxDisplay) {
    visitors.value = visitors.value.slice(0, maxDisplay)
  }
  
  isLoading.value = false
}

// 查看全部访客
const viewAllVisitors = () => {
  router.push('/profile?tab=visitors')
}

onMounted(() => {
  // 初始化时可以从API加载访客数据
})
</script>

<style scoped>
/* 自定义滚动条 */
.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: rgba(251, 146, 60, 0.3) transparent;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(251, 146, 60, 0.3);
  border-radius: 2px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(251, 146, 60, 0.5);
}

/* 访客项动画 */
.visitor-item {
  animation: slideInLeft 0.3s ease-out;
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 悬浮效果增强 */
.visitor-item:hover {
  transform: translateX(2px);
  box-shadow: 0 4px 12px rgba(251, 146, 60, 0.1);
}

/* 头像悬浮效果 */
.visitor-item:hover img {
  transform: scale(1.05);
}

/* 在线状态脉冲 */
.bg-green-400 {
  animation: pulse-green 2s infinite;
}

@keyframes pulse-green {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

/* 新访客标识脉冲 */
.animate-pulse {
  animation: pulse-orange 1.5s infinite;
}

@keyframes pulse-orange {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.1);
  }
}
</style> 