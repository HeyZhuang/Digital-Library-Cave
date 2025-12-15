<template>
  <div class="h-full flex flex-col">
    <!-- Logo和标题 -->
    <div class="flex items-center p-6 border-b border-gray-200">
      <div class="flex items-center space-x-3">
        <div class="w-10 h-10 bg-primary-600 rounded-lg flex items-center justify-center">
          <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
          </svg>
        </div>
        <div>
          <h1 class="text-lg font-semibold text-title">陈壮壮的数字藏金阁</h1>
          <p class="text-sm text-gray-600">专注 & 洞察 & 分享</p>
        </div>
      </div>
    </div>

    <!-- 搜索框 -->
    <div class="p-4 border-b border-gray-200">
      <SearchBox />
    </div>

    <!-- 快速操作 -->
    <div v-if="authStore.isAuthenticated" class="p-4 border-b border-gray-200">
      <RouterLink
        to="/article/edit"
        class="w-full flex items-center justify-center px-4 py-2.5 text-sm font-medium text-white bg-primary-600 rounded-lg hover:bg-primary-700 transition-colors shadow-sm"
        @click="$emit('close')"
      >
        <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
        </svg>
        写文章
      </RouterLink>
    </div>

    <!-- 导航菜单 -->
    <nav class="flex-1 px-4 py-6 space-y-2">
      <RouterLink
        v-for="item in navigationItems"
        :key="item.name"
        :to="item.to"
        class="nav-item"
        :class="{ 'nav-item-active': $route.path === item.to }"
        @click="$emit('close')"
      >
        <component :is="item.icon" class="w-5 h-5" />
        <span>{{ item.name }}</span>
        <span v-if="item.count" class="ml-auto badge badge-secondary">{{ item.count }}</span>
      </RouterLink>
    </nav>

    <!-- 用户信息和统计信息 -->
    <div class="p-4 border-t border-gray-200 space-y-4">
      <!-- 用户信息 -->
      <div v-if="authStore.isAuthenticated" class="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
        <div class="w-10 h-10 bg-primary-600 rounded-full flex items-center justify-center">
          <span class="text-white font-medium">
            {{ authStore.userInfo?.nickname?.charAt(0) || authStore.userInfo?.username?.charAt(0) }}
          </span>
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium text-gray-900 truncate">
            {{ authStore.userInfo?.nickname || authStore.userInfo?.username }}
          </p>
                        <p class="text-xs text-gray-600 truncate">
            {{ authStore.userInfo?.email }}
          </p>
        </div>
        <button
          @click="handleLogout"
          class="text-gray-400 hover:text-red-500 transition-colors"
          title="登出"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
          </svg>
        </button>
      </div>
      
      <!-- 未登录状态 -->
      <div v-else class="p-3 bg-gray-50 rounded-lg text-center">
        <p class="text-sm text-gray-600 mb-2">欢迎访问知识宝库</p>
        <RouterLink
          to="/login"
          class="inline-flex items-center px-3 py-1.5 text-xs font-medium text-white bg-primary-600 rounded-md hover:bg-primary-700 transition-colors"
        >
          登录/注册
        </RouterLink>
      </div>

      <!-- 统计信息 -->
      <div class="grid grid-cols-2 gap-4">
        <div class="text-center">
          <div class="text-2xl font-bold text-title">{{ totalArticles }}</div>
                        <div class="text-sm text-gray-600">文章总数</div>
        </div>
        <div class="text-center">
          <div class="text-2xl font-bold text-title">{{ totalTags }}</div>
                        <div class="text-sm text-gray-600">标签总数</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useArticlesStore } from '../stores/articles'
import { useTagsStore } from '../stores/tags'
import { useAuthStore } from '../stores/auth'
import SearchBox from './SearchBox.vue'

// Icons (简化版SVG图标组件)
const HomeIcon = {
  template: `<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path></svg>`
}

const DocumentIcon = {
  template: `<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>`
}

const TagIcon = {
  template: `<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"></path></svg>`
}

const SearchIcon = {
  template: `<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>`
}

defineEmits<{
  close: []
}>()

const route = useRoute()
const router = useRouter()
const articlesStore = useArticlesStore()
const tagsStore = useTagsStore()
const authStore = useAuthStore()

const totalArticles = computed(() => articlesStore.totalArticles)
const totalTags = computed(() => tagsStore.totalTags)

const navigationItems = computed(() => [
  {
    name: '首页',
    to: '/',
    icon: HomeIcon,
    count: null
  },
  {
    name: '文章列表',
    to: '/articles',
    icon: DocumentIcon,
    count: totalArticles.value
  },
  {
    name: '标签管理',
    to: '/tags',
    icon: TagIcon,
    count: totalTags.value
  },
  {
    name: '搜索',
    to: '/search',
    icon: SearchIcon,
    count: null
  }
])

// 处理登出
const handleLogout = async () => {
  await authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.nav-item {
  @apply flex items-center px-3 py-2 text-sm font-medium rounded-lg text-gray-700 hover:bg-gray-100 hover:text-gray-900 transition-colors;
}

.nav-item-active {
  @apply bg-primary-100 text-primary-700;
}
</style> 