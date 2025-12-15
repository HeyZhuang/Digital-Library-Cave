<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import SidebarNav from './components/SidebarNav.vue'
import KnowledgeGalaxy from './components/KnowledgeGalaxy.vue'
import DigitalLibraryHeader from './components/DigitalLibraryHeader.vue'

const route = useRoute()
const sidebarOpen = ref(false)
const isDarkMode = ref(true)
const isLoading = ref(true)

const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value
}

const closeSidebar = () => {
  sidebarOpen.value = false
}

// 页面加载完成
onMounted(() => {
  setTimeout(() => {
    isLoading.value = false
  }, 1000)
})

const showScrollTop = ref(false)

// 页面过渡动画
const onBeforeEnter = (el: Element) => {
  const element = el as HTMLElement
  element.style.opacity = '0'
  element.style.transform = 'translateY(30px)'
}

const onEnter = (el: Element, done: () => void) => {
  const element = el as HTMLElement
  element.offsetHeight // 触发重排
  element.style.transition = 'all 0.6s ease-out'
  element.style.opacity = '1'
  element.style.transform = 'translateY(0)'
  setTimeout(done, 600)
}

const onLeave = (el: Element, done: () => void) => {
  const element = el as HTMLElement
  element.style.transition = 'all 0.3s ease-in'
  element.style.opacity = '0'
  element.style.transform = 'translateY(-20px)'
  setTimeout(done, 300)
}

// 滚动处理
const handleScroll = () => {
  showScrollTop.value = window.scrollY > 500
}

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 处理社交链接点击
const handleSocialLink = () => {
  // 这里可以添加实际的社交链接逻辑
  console.log('社交链接点击')
}
</script>

<template>
  <div id="app" class="min-h-screen bg-gradient-deepspace text-parchment-100 font-body relative overflow-hidden">
    <!-- 知识星云背景 -->
    <KnowledgeGalaxy :isDarkMode="isDarkMode" />
    
    <!-- 羊皮纸纹理叠加 -->
    <div class="fixed inset-0 bg-parchment-texture opacity-5 z-10 pointer-events-none"></div>
    
    <!-- 页面加载动画 -->
    <div 
      v-if="isLoading"
      class="fixed inset-0 z-50 bg-deepSpace-900 flex items-center justify-center"
    >
      <div class="loading-container text-center">
        <!-- 古籍翻页动画 -->
        <div class="book-loader relative w-20 h-16 mx-auto mb-6">
          <div class="book-page absolute inset-0 bg-gold-600 rounded-r-lg animate-pulse"></div>
          <div class="book-page absolute inset-0 bg-gold-500 rounded-r-lg animate-pulse" style="animation-delay: 0.2s"></div>
          <div class="book-page absolute inset-0 bg-gold-400 rounded-r-lg animate-pulse" style="animation-delay: 0.4s"></div>
        </div>
        <h2 class="text-xl font-title text-gold-400 mb-2">载入知识宝库</h2>
        <p class="text-parchment-400 font-body">正在为您准备智慧殿堂...</p>
        <!-- 金沙流动进度条 -->
        <div class="w-48 h-1 bg-deepSpace-700 rounded-full mx-auto mt-4 overflow-hidden">
          <div class="h-full bg-gradient-gold animate-shimmer"></div>
        </div>
      </div>
    </div>

    <!-- 主应用内容 -->
    <div class="relative z-20">
      <!-- 数字智库头部导航 -->
      <DigitalLibraryHeader />
      
      <!-- 主内容区域 -->
      <main class="main-content pt-16 min-h-screen">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <!-- 页面内容 -->
          <div class="content-wrapper py-8">
            <RouterView v-slot="{ Component, route }">
              <transition 
                name="page-transition" 
                mode="out-in"
                @before-enter="onBeforeEnter"
                @enter="onEnter"
                @leave="onLeave"
              >
                <component :is="Component" :key="route.path" />
              </transition>
            </RouterView>
          </div>
        </div>
      </main>
      
      <!-- 页脚 - 古籍风格 -->
      <footer class="footer relative z-10 mt-16 border-t border-gold-600/20 bg-deepSpace-900/80 backdrop-blur-xl">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
            <!-- 品牌信息 -->
            <div class="col-span-1 md:col-span-2">
              <div class="flex items-center space-x-3 mb-4">
                <div class="w-8 h-8 bg-gradient-gold rounded-lg flex items-center justify-center">
                  <svg class="w-5 h-5 text-deepSpace-900" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M12 2L2 7v10c0 5.55 3.84 9.74 9 11 5.16-1.26 9-5.45 9-11V7l-10-5z"/>
                  </svg>
                </div>
                <h3 class="text-lg font-title text-parchment-100">数字藏金阁</h3>
              </div>
              <p class="text-parchment-400 leading-relaxed mb-4">
                汇集天下智慧，传承千年文明。在这座数字智库中，每一份知识都如珍珠般闪耀，
                每一篇文章都承载着思想的力量。
              </p>
              <div class="flex space-x-4">
                <button class="text-gold-400 hover:text-gold-300 transition-colors" @click="handleSocialLink">
                  <span class="sr-only">关注微信</span>
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M24 12.3c0-6.1-6.1-11.1-13.5-11.1S-3 6.2-3 12.3c0 5.5 4.4 10.1 10.2 11v-7.8H4.8v-3.2h2.4V9.9c0-2.3 1.4-3.6 3.5-3.6 1 0 2.1.2 2.1.2v2.3h-1.2c-1.2 0-1.5.7-1.5 1.4v1.7h2.6l-.4 3.2h-2.2V23.3c5.8-.9 10.2-5.5 10.2-11z"/>
                  </svg>
                </button>
              </div>
            </div>
            
            <!-- 快速导航 -->
            <div>
              <h4 class="text-gold-400 font-title mb-4">宝库导览</h4>
              <ul class="space-y-2">
                <li><router-link to="/" class="text-parchment-400 hover:text-gold-300 transition-colors">知识殿堂</router-link></li>
                <li><router-link to="/articles" class="text-parchment-400 hover:text-gold-300 transition-colors">文章精选</router-link></li>
                <li><router-link to="/tags" class="text-parchment-400 hover:text-gold-300 transition-colors">标签星图</router-link></li>
                <li><router-link to="/search" class="text-parchment-400 hover:text-gold-300 transition-colors">搜索探索</router-link></li>
              </ul>
            </div>
            
            <!-- 管理功能 -->
            <div>
              <h4 class="text-gold-400 font-title mb-4">管理中心</h4>
              <ul class="space-y-2">
                <li><router-link to="/categories" class="text-parchment-400 hover:text-gold-300 transition-colors">分类管理</router-link></li>
                <li><router-link to="/workshop" class="text-parchment-400 hover:text-gold-300 transition-colors">创作工坊</router-link></li>
                <li><router-link to="/article/edit" class="text-parchment-400 hover:text-gold-300 transition-colors">文章编辑</router-link></li>
                <li><router-link to="/accessibility-demo" class="text-parchment-400 hover:text-gold-300 transition-colors">无障碍演示</router-link></li>
              </ul>
            </div>
          </div>
          
          <!-- 版权信息 -->
          <div class="border-t border-gold-600/20 mt-8 pt-8 text-center">
            <p class="text-parchment-400 text-sm">
              © 2025 数字藏金阁 | 智慧传承，薪火相传 | 
              <span class="text-gold-400">Built with 陈大壮 and Ancient Wisdom</span>
            </p>
          </div>
        </div>
      </footer>
    </div>
    
    <!-- 回到顶部按钮 -->
    <button 
      v-show="showScrollTop"
      @click="scrollToTop"
      class="scroll-top-btn fixed bottom-8 right-8 z-30 w-12 h-12 bg-gradient-gold rounded-full shadow-lg flex items-center justify-center text-deepSpace-900 hover:shadow-xl transition-all duration-300"
    >
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"/>
      </svg>
    </button>
  </div>
</template>

<style scoped>
/* 页面过渡动画 */
.page-transition-enter-active,
.page-transition-leave-active {
  transition: all 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.page-transition-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.page-transition-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 加载动画 */
.book-loader .book-page {
  transform-origin: left center;
  animation: flip 1.5s ease-in-out infinite;
}

@keyframes flip {
  0%, 80% { transform: rotateY(0deg); }
  20%, 60% { transform: rotateY(-90deg); }
}

/* 滚动按钮 */
.scroll-top-btn:hover {
  transform: translateY(-2px) scale(1.05);
}

/* 主内容区域 */
.main-content {
  background: 
    radial-gradient(circle at 20% 80%, rgba(212, 175, 55, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(212, 175, 55, 0.05) 0%, transparent 50%);
}

/* 页脚样式 */
.footer {
  background: 
    linear-gradient(135deg, rgba(15, 23, 42, 0.95), rgba(30, 41, 59, 0.95)),
    url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23d4af37' fill-opacity='0.05'%3E%3Ccircle cx='30' cy='30' r='2'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}
</style>
