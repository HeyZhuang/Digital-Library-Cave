<template>
  <header class="digital-library-header fixed top-0 left-0 right-0 z-50 transition-all duration-300">
    <!-- 磨砂玻璃背景 -->
    <div class="absolute inset-0 bg-deepSpace-900/80 backdrop-blur-xl border-b border-gold-600/20"></div>
    
    <!-- 鎏金装饰线 -->
    <div class="absolute bottom-0 left-0 right-0 h-px bg-gradient-to-r from-transparent via-gold-600/50 to-transparent"></div>
    
    <div class="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <!-- 左侧Logo区域 -->
        <div class="flex items-center space-x-4">
          <!-- 3D浮雕Logo -->
          <div class="logo-container group cursor-pointer" @click="$router.push('/')">
            <div class="logo-backdrop"></div>
            <div class="logo-content relative z-10 flex items-center space-x-3">
              <!-- 金镶玉玺图标 -->
              <div class="logo-icon w-10 h-10 rounded-lg bg-gradient-gold flex items-center justify-center shadow-lg group-hover:shadow-xl transition-all duration-300">
                <svg class="w-6 h-6 text-deepSpace-900" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 2L2 7v10c0 5.55 3.84 9.74 9 11 5.16-1.26 9-5.45 9-11V7l-10-5z"/>
                  <path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="2" fill="none"/>
                </svg>
              </div>
              <!-- Logo文字 -->
              <div class="logo-text">
                <h1 class="text-xl font-title text-parchment-100 font-semibold tracking-wide">
                  数字藏金阁
                </h1>
                <p class="text-xs text-gold-400 font-body">Digital Treasury of Wisdom</p>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 中央导航菜单 -->
        <nav class="hidden md:flex items-center space-x-1">
          <NavMenuItem 
            v-for="item in navItems" 
            :key="item.path"
            :item="item"
            :isActive="$route.path === item.path"
            @click="handleNavClick(item.path)"
          />
        </nav>
        
        <!-- 右侧工具区域 -->
        <div class="flex items-center space-x-4">
          <!-- 宝石切割搜索框 -->
          <div class="search-container hidden sm:block">
            <div class="relative">
              <div class="search-backdrop"></div>
              <input 
                v-model="searchQuery"
                type="text" 
                placeholder="搜索知识..."
                class="search-input w-64 pl-10 pr-4 py-2 bg-transparent border border-gold-600/30 rounded-lg text-parchment-100 placeholder-parchment-400 focus:outline-none focus:border-gold-500 transition-all duration-300"
                @keyup.enter="handleSearch"
              >
              <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gold-400" />
              <!-- 搜索建议浮层 -->
              <div v-if="searchSuggestions.length > 0" class="search-suggestions absolute top-full left-0 right-0 mt-2 bg-deepSpace-800/95 backdrop-blur-lg border border-gold-600/20 rounded-lg shadow-xl overflow-hidden">
                <div 
                  v-for="suggestion in searchSuggestions" 
                  :key="suggestion.id"
                  class="suggestion-item px-4 py-3 hover:bg-gold-600/10 cursor-pointer transition-colors duration-200"
                  @click="selectSuggestion(suggestion)"
                >
                  <div class="flex items-center space-x-3">
                    <div class="w-2 h-2 bg-gold-400 rounded-full"></div>
                    <span class="text-parchment-100 text-sm">{{ suggestion.title }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 主题切换 -->
          <button 
            @click="toggleTheme"
            class="theme-toggle p-2 rounded-lg bg-deepSpace-800/50 border border-gold-600/20 text-gold-400 hover:bg-gold-600/10 hover:text-gold-300 transition-all duration-300"
          >
            <SunIcon v-if="isDarkMode" class="w-5 h-5" />
            <MoonIcon v-else class="w-5 h-5" />
          </button>
          
          <!-- 用户头像区域 -->
          <div class="user-avatar-container relative group">
            <button class="user-avatar w-10 h-10 rounded-full bg-gradient-gold border-2 border-gold-600/30 overflow-hidden hover:border-gold-500 transition-all duration-300">
              <img 
                v-if="userAvatar" 
                :src="userAvatar" 
                :alt="userName"
                class="w-full h-full object-cover"
              >
              <div v-else class="w-full h-full flex items-center justify-center text-deepSpace-900 font-bold text-sm">
                {{ userInitials }}
              </div>
            </button>
            
            <!-- 用户下拉菜单 -->
            <div class="user-dropdown absolute right-0 top-full mt-2 w-48 bg-deepSpace-800/95 backdrop-blur-lg border border-gold-600/20 rounded-lg shadow-xl opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-300">
              <div class="p-3 border-b border-gold-600/20">
                <p class="text-parchment-100 font-medium">{{ userName }}</p>
                <p class="text-parchment-400 text-sm">{{ userEmail }}</p>
              </div>
              <div class="py-1">
                <DropdownMenuItem 
                  v-for="menuItem in userMenuItems" 
                  :key="menuItem.label"
                  :item="menuItem"
                  @click="handleUserMenuClick(menuItem)"
                />
              </div>
            </div>
          </div>
          
          <!-- 移动端菜单按钮 -->
          <button 
            @click="toggleMobileMenu"
            class="mobile-menu-toggle md:hidden p-2 rounded-lg bg-deepSpace-800/50 border border-gold-600/20 text-gold-400"
          >
            <Bars3Icon v-if="!mobileMenuOpen" class="w-5 h-5" />
            <XMarkIcon v-else class="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>
    
    <!-- 移动端导航菜单 -->
    <div 
      v-if="mobileMenuOpen"
      class="mobile-nav md:hidden absolute top-full left-0 right-0 bg-deepSpace-900/95 backdrop-blur-xl border-b border-gold-600/20"
    >
      <div class="px-4 py-2 space-y-1">
        <MobileNavItem 
          v-for="item in navItems" 
          :key="item.path"
          :item="item"
          :isActive="$route.path === item.path"
          @click="handleMobileNavClick(item.path)"
        />
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { MagnifyingGlassIcon, SunIcon, MoonIcon, Bars3Icon, XMarkIcon } from '@heroicons/vue/24/outline'
import { useAuthStore } from '../stores/auth'

// 子组件
const NavMenuItem = {
  props: ['item', 'isActive'],
  emits: ['click'],
  template: `
    <button 
      @click="$emit('click')"
      :class="[
        'nav-item px-4 py-2 rounded-lg text-sm font-medium transition-all duration-300',
        isActive 
          ? 'bg-gold-600/20 text-gold-300 shadow-lg' 
          : 'text-parchment-300 hover:text-gold-300 hover:bg-gold-600/10'
      ]"
    >
      {{ item.label }}
    </button>
  `
}

const DropdownMenuItem = {
  props: ['item'],
  emits: ['click'],
  template: `
    <button 
      @click="$emit('click')"
      class="dropdown-item w-full px-4 py-2 text-left text-parchment-300 hover:text-gold-300 hover:bg-gold-600/10 transition-colors duration-200"
    >
      {{ item.label }}
    </button>
  `
}

const MobileNavItem = {
  props: ['item', 'isActive'],
  emits: ['click'],
  template: `
    <button 
      @click="$emit('click')"
      :class="[
        'mobile-nav-item w-full px-4 py-3 text-left rounded-lg transition-all duration-300',
        isActive 
          ? 'bg-gold-600/20 text-gold-300' 
          : 'text-parchment-300 hover:text-gold-300 hover:bg-gold-600/10'
      ]"
    >
      {{ item.label }}
    </button>
  `
}

// 响应式数据
const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const searchQuery = ref('')
const searchSuggestions = ref<{id: number, title: string}[]>([])
const mobileMenuOpen = ref(false)
const isDarkMode = ref(true)

// 导航菜单项
const navItems = [
  { label: '知识宝库', path: '/' },
  { label: '文章殿堂', path: '/articles' },
  { label: '标签星图', path: '/tags' },
  { label: '搜索探索', path: '/search' },
  { label: '创作功坊', path: '/workshop' },
  { label: '分类管理', path: '/categories' }
]

// 用户信息（从auth store获取）
const userName = computed(() => authStore.userInfo?.nickname || authStore.userInfo?.username || '知识探索者')
const userEmail = computed(() => authStore.userInfo?.email || 'explorer@library.com')
const userAvatar = computed(() => authStore.userInfo?.avatar || '')
const userInitials = computed(() => {
  return userName.value.split('').slice(0, 2).join('')
})

// 用户菜单项
const userMenuItems = [
  { label: '个人资料', action: 'profile' },
  { label: '文章编辑', action: 'edit' },
  { label: '分类管理', action: 'categories' },
  { label: '创作记录', action: 'writings' },
  { label: '设置偏好', action: 'settings' },
  { label: '退出登录', action: 'logout' }
]

// 搜索功能
const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push(`/search?q=${encodeURIComponent(searchQuery.value)}`)
    searchQuery.value = ''
    searchSuggestions.value = []
  }
}

const selectSuggestion = (suggestion: any) => {
  router.push(`/article/${suggestion.id}`)
  searchQuery.value = ''
  searchSuggestions.value = []
}

// 主题切换
const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value
  // 这里可以添加主题切换逻辑
  document.documentElement.classList.toggle('dark', isDarkMode.value)
}

// 移动端菜单
const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

const handleMobileNavClick = async (path: string) => {
  try {
    await router.push(path)
    mobileMenuOpen.value = false
  } catch (error) {
    console.error('移动端导航失败:', error)
    mobileMenuOpen.value = false
    // 备用方案：直接设置location
    window.location.href = path
  }
}

// 导航点击处理
const handleNavClick = async (path: string) => {
  try {
    await router.push(path)
  } catch (error) {
    console.error('导航失败:', error)
    // 备用方案：直接设置location
    window.location.href = path
  }
}

// 用户菜单处理
const handleUserMenuClick = (menuItem: any) => {
  switch (menuItem.action) {
    case 'profile':
      router.push('/profile')
      break
    case 'edit':
      router.push('/article/edit')
      break
    case 'categories':
      router.push('/categories')
      break
    case 'writings':
      router.push('/articles')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      // 退出登录逻辑
      authStore.logout()
      router.push('/login')
      break
  }
}

// 监听搜索输入
watch(searchQuery, (newValue) => {
  if (newValue.length > 2) {
    // 模拟搜索建议
    searchSuggestions.value = [
      { id: 1, title: 'Vue.js 高级特性详解' },
      { id: 2, title: 'TypeScript 类型体操' },
      { id: 3, title: '前端性能优化实战' }
    ].filter(item => 
      item.title.toLowerCase().includes(newValue.toLowerCase())
    )
  } else {
    searchSuggestions.value = []
  }
})
</script>

<style scoped>
.digital-library-header {
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.logo-container {
  position: relative;
  transition: all 0.3s ease;
}

.logo-container:hover {
  transform: translateY(-2px);
}

.logo-backdrop {
  position: absolute;
  inset: -4px;
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.1), rgba(212, 175, 55, 0.05));
  border-radius: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.logo-container:hover .logo-backdrop {
  opacity: 1;
}

.logo-icon {
  box-shadow: 
    0 4px 15px rgba(212, 175, 55, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.logo-container:hover .logo-icon {
  transform: scale(1.05);
  box-shadow: 
    0 6px 20px rgba(212, 175, 55, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.search-container {
  position: relative;
}

.search-backdrop {
  position: absolute;
  inset: 0;
  background: rgba(30, 41, 59, 0.5);
  border-radius: 8px;
  backdrop-filter: blur(10px);
}

.search-input {
  background: rgba(30, 41, 59, 0.3);
  backdrop-filter: blur(10px);
}

.search-input:focus {
  background: rgba(30, 41, 59, 0.5);
  box-shadow: 0 0 0 2px rgba(212, 175, 55, 0.3);
}

.search-suggestions {
  animation: slideDown 0.2s ease-out;
}

.suggestion-item:hover {
  background: rgba(212, 175, 55, 0.1);
}

.user-avatar {
  box-shadow: 0 0 0 2px rgba(212, 175, 55, 0.2);
}

.user-avatar:hover {
  box-shadow: 0 0 0 2px rgba(212, 175, 55, 0.4);
}

.user-dropdown {
  animation: slideDown 0.2s ease-out;
}

.nav-item:hover {
  box-shadow: 0 2px 8px rgba(212, 175, 55, 0.2);
}

.theme-toggle:hover {
  box-shadow: 0 2px 8px rgba(212, 175, 55, 0.2);
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style> 