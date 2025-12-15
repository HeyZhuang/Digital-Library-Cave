<template>
  <div class="avatar-test-view min-h-screen bg-gray-100 p-8">
    <div class="max-w-4xl mx-auto bg-white rounded-lg shadow-lg p-6">
      <h1 class="text-3xl font-bold text-gray-800 mb-6">头像显示测试页面</h1>
      
      <!-- 认证状态检查 -->
      <div class="mb-8 p-4 border rounded-lg">
        <h2 class="text-xl font-semibold mb-4">认证状态</h2>
        <div class="space-y-2">
          <p><strong>是否登录:</strong> {{ authStore.isAuthenticated ? '是' : '否' }}</p>
          <p><strong>用户信息:</strong> {{ authStore.userInfo ? 'OK' : '无' }}</p>
          <p v-if="authStore.userInfo"><strong>用户名:</strong> {{ authStore.userInfo.username }}</p>
          <p v-if="authStore.userInfo"><strong>昵称:</strong> {{ authStore.userInfo.nickname }}</p>
          <p v-if="authStore.userInfo"><strong>邮箱:</strong> {{ authStore.userInfo.email }}</p>
          <p v-if="authStore.userInfo"><strong>头像URL:</strong> {{ authStore.userInfo.avatar || '未设置' }}</p>
        </div>
      </div>
      
      <!-- 头像显示测试 -->
      <div class="mb-8 p-4 border rounded-lg">
        <h2 class="text-xl font-semibold mb-4">头像显示测试</h2>
        
        <div v-if="authStore.userInfo?.avatar" class="space-y-4">
          <!-- 原始头像URL显示 -->
          <div>
            <p class="font-medium mb-2">原始头像URL:</p>
            <code class="bg-gray-100 p-2 rounded block break-all">{{ authStore.userInfo.avatar }}</code>
          </div>
          
          <!-- 头像图片显示 -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <!-- 方式1: 直接使用URL -->
            <div>
              <h3 class="font-medium mb-2">方式1: 直接使用URL</h3>
              <img 
                :src="authStore.userInfo.avatar" 
                alt="用户头像"
                class="w-32 h-32 rounded-full border-2 border-gray-300 object-cover"
                @load="onImageLoad('direct')"
                @error="onImageError('direct')"
              />
              <p class="text-sm mt-2" :class="imageStatus.direct.success ? 'text-green-600' : 'text-red-600'">
                状态: {{ imageStatus.direct.message }}
              </p>
            </div>
            
            <!-- 方式2: 使用代理URL -->
            <div v-if="proxyUrl">
              <h3 class="font-medium mb-2">方式2: 代理URL</h3>
              <img 
                :src="proxyUrl" 
                alt="用户头像"
                class="w-32 h-32 rounded-full border-2 border-gray-300 object-cover"
                @load="onImageLoad('proxy')"
                @error="onImageError('proxy')"
              />
              <p class="text-sm mt-2" :class="imageStatus.proxy.success ? 'text-green-600' : 'text-red-600'">
                状态: {{ imageStatus.proxy.message }}
              </p>
            </div>
            
            <!-- 方式3: 使用相对路径 -->
            <div v-if="relativePath">
              <h3 class="font-medium mb-2">方式3: 相对路径</h3>
              <img 
                :src="relativePath" 
                alt="用户头像"
                class="w-32 h-32 rounded-full border-2 border-gray-300 object-cover"
                @load="onImageLoad('relative')"
                @error="onImageError('relative')"
              />
              <p class="text-sm mt-2" :class="imageStatus.relative.success ? 'text-green-600' : 'text-red-600'">
                状态: {{ imageStatus.relative.message }}
              </p>
            </div>
          </div>
        </div>
        
        <div v-else class="text-gray-600">
          用户未设置头像或未登录
        </div>
      </div>
      
      <!-- 网络测试 -->
      <div class="mb-8 p-4 border rounded-lg">
        <h2 class="text-xl font-semibold mb-4">网络连接测试</h2>
        <div class="space-y-2">
          <button 
            @click="testDirectAccess" 
            class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            :disabled="testing"
          >
            {{ testing ? '测试中...' : '测试直接访问头像URL' }}
          </button>
          <div v-if="networkTestResult" class="mt-2">
            <p :class="networkTestResult.success ? 'text-green-600' : 'text-red-600'">
              {{ networkTestResult.message }}
            </p>
          </div>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="space-x-4">
        <button 
          @click="refreshUserInfo" 
          class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
        >
          刷新用户信息
        </button>
        <router-link 
          to="/profile" 
          class="bg-purple-500 text-white px-4 py-2 rounded hover:bg-purple-600 inline-block"
        >
          去个人资料页面
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()

// 图片加载状态
const imageStatus = ref({
  direct: { success: false, message: '未测试' },
  proxy: { success: false, message: '未测试' },
  relative: { success: false, message: '未测试' }
})

// 网络测试状态
const testing = ref(false)
const networkTestResult = ref(null as any)

// 计算不同的URL格式
const proxyUrl = computed(() => {
  if (!authStore.userInfo?.avatar) return null
  return authStore.userInfo.avatar.replace('http://localhost:8182', 'http://localhost:3002/api/proxy')
})

const relativePath = computed(() => {
  if (!authStore.userInfo?.avatar) return null
  const match = authStore.userInfo.avatar.match(/\/uploads\/(.+)$/)
  return match ? `/uploads/${match[1]}` : null
})

// 图片加载处理
const onImageLoad = (type: string) => {
  imageStatus.value[type] = { success: true, message: '加载成功' }
}

const onImageError = (type: string) => {
  imageStatus.value[type] = { success: false, message: '加载失败' }
}

// 测试直接访问
const testDirectAccess = async () => {
  if (!authStore.userInfo?.avatar) return
  
  testing.value = true
  try {
    const response = await fetch(authStore.userInfo.avatar, {
      method: 'HEAD',  // 只获取头部信息
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    if (response.ok) {
      networkTestResult.value = {
        success: true,
        message: `网络访问成功 (${response.status})`
      }
    } else {
      networkTestResult.value = {
        success: false,
        message: `网络访问失败 (${response.status}: ${response.statusText})`
      }
    }
  } catch (error) {
    networkTestResult.value = {
      success: false,
      message: `网络错误: ${error.message}`
    }
  } finally {
    testing.value = false
  }
}

// 刷新用户信息
const refreshUserInfo = async () => {
  await authStore.fetchCurrentUser()
}

// 初始化
onMounted(async () => {
  if (!authStore.isAuthenticated) {
    await authStore.initAuth()
  }
})
</script>

<style scoped>
code {
  font-family: 'Courier New', monospace;
}
</style> 