<template>
  <div class="max-w-4xl mx-auto p-8">
    <h1 class="text-3xl font-bold mb-8">路由测试页面</h1>
    
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <!-- RouterLink 测试 -->
      <div class="bg-white p-6 rounded-lg shadow">
        <h2 class="text-xl font-semibold mb-4">RouterLink 导航测试</h2>
        <div class="space-y-3">
          <RouterLink 
            to="/" 
            class="block px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
          >
            返回首页
          </RouterLink>
          <RouterLink 
            to="/articles" 
            class="block px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition-colors"
          >
            文章列表
          </RouterLink>
          <RouterLink 
            to="/tags" 
            class="block px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600 transition-colors"
          >
            标签管理
          </RouterLink>
        </div>
      </div>

      <!-- 编程式导航测试 -->
      <div class="bg-white p-6 rounded-lg shadow">
        <h2 class="text-xl font-semibold mb-4">编程式导航测试</h2>
        <div class="space-y-3">
          <button 
            @click="navigateToHome"
            class="block w-full px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
            :disabled="navigating"
          >
            {{ navigating ? '导航中...' : '导航到首页' }}
          </button>
          <button 
            @click="navigateToArticles"
            class="block w-full px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition-colors"
            :disabled="navigating"
          >
            {{ navigating ? '导航中...' : '导航到文章列表' }}
          </button>
          <button 
            @click="navigateToSearch"
            class="block w-full px-4 py-2 bg-yellow-500 text-white rounded hover:bg-yellow-600 transition-colors"
            :disabled="navigating"
          >
            {{ navigating ? '导航中...' : '导航到搜索页面' }}
          </button>
        </div>
      </div>

      <!-- 路由信息显示 -->
      <div class="bg-white p-6 rounded-lg shadow md:col-span-2">
        <h2 class="text-xl font-semibold mb-4">当前路由信息</h2>
        <div class="bg-gray-100 p-4 rounded text-sm font-mono">
          <div><strong>路径:</strong> {{ $route.path }}</div>
          <div><strong>名称:</strong> {{ $route.name }}</div>
          <div><strong>参数:</strong> {{ JSON.stringify($route.params) }}</div>
          <div><strong>查询:</strong> {{ JSON.stringify($route.query) }}</div>
          <div><strong>Meta:</strong> {{ JSON.stringify($route.meta) }}</div>
        </div>
      </div>

      <!-- 导航日志 -->
      <div class="bg-white p-6 rounded-lg shadow md:col-span-2">
        <h2 class="text-xl font-semibold mb-4">导航日志</h2>
        <div class="bg-gray-100 p-4 rounded max-h-40 overflow-y-auto">
          <div v-for="(log, index) in navigationLogs" :key="index" class="text-sm mb-1">
            {{ log }}
          </div>
        </div>
        <button 
          @click="clearLogs"
          class="mt-2 px-3 py-1 bg-red-500 text-white rounded text-sm hover:bg-red-600"
        >
          清除日志
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const navigating = ref(false)
const navigationLogs = ref<string[]>([])

const addLog = (message: string) => {
  const timestamp = new Date().toLocaleTimeString()
  navigationLogs.value.unshift(`[${timestamp}] ${message}`)
  if (navigationLogs.value.length > 50) {
    navigationLogs.value = navigationLogs.value.slice(0, 50)
  }
}

const navigateToHome = async () => {
  if (navigating.value) return
  navigating.value = true
  addLog('开始导航到首页')
  
  try {
    await router.push('/')
    addLog('成功导航到首页')
  } catch (error) {
    addLog(`导航失败: ${error}`)
    console.error('导航失败:', error)
  } finally {
    navigating.value = false
  }
}

const navigateToArticles = async () => {
  if (navigating.value) return
  navigating.value = true
  addLog('开始导航到文章列表')
  
  try {
    await router.push('/articles')
    addLog('成功导航到文章列表')
  } catch (error) {
    addLog(`导航失败: ${error}`)
    console.error('导航失败:', error)
  } finally {
    navigating.value = false
  }
}

const navigateToSearch = async () => {
  if (navigating.value) return
  navigating.value = true
  addLog('开始导航到搜索页面')
  
  try {
    await router.push('/search')
    addLog('成功导航到搜索页面')
  } catch (error) {
    addLog(`导航失败: ${error}`)
    console.error('导航失败:', error)
  } finally {
    navigating.value = false
  }
}

const clearLogs = () => {
  navigationLogs.value = []
}

// 监听路由变化
watch(() => route.path, (newPath, oldPath) => {
  addLog(`路由从 ${oldPath} 变更到 ${newPath}`)
})

onMounted(() => {
  addLog('测试页面已加载')
})
</script>

<style scoped>
.router-link-active {
  @apply bg-opacity-80;
}
</style> 