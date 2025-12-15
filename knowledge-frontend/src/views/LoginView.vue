<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div>
        <div class="mx-auto h-12 w-12 flex items-center justify-center bg-primary-600 rounded-lg">
          <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
          </svg>
        </div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          {{ isLoginMode ? '登录到你的账户' : '创建新账户' }}
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          {{ isLoginMode ? '还没有账户？' : '已有账户？' }}
          <button 
            @click="toggleMode" 
            class="font-medium text-primary-600 hover:text-primary-500 transition-colors"
          >
            {{ isLoginMode ? '立即注册' : '立即登录' }}
          </button>
        </p>
      </div>

      <form class="mt-8 space-y-6" @submit.prevent="handleSubmit">
        <div class="space-y-4">
          <!-- 用户名 -->
          <div>
            <label for="username" class="block text-sm font-medium text-gray-800 mb-2">
              用户名
            </label>
            <input
              id="username"
              v-model="form.username"
              type="text"
              required
              class="input-field"
              placeholder="请输入用户名"
              :disabled="loading"
            />
          </div>

          <!-- 邮箱（仅注册时显示） -->
          <div v-if="!isLoginMode">
            <label for="email" class="block text-sm font-medium text-gray-800 mb-2">
              邮箱
            </label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              required
              class="input-field"
              placeholder="请输入邮箱地址"
              :disabled="loading"
            />
          </div>

          <!-- 昵称（仅注册时显示） -->
          <div v-if="!isLoginMode">
            <label for="nickname" class="block text-sm font-medium text-gray-700 mb-2">
              昵称（可选）
            </label>
            <input
              id="nickname"
              v-model="form.nickname"
              type="text"
              class="input-field"
              placeholder="请输入昵称"
              :disabled="loading"
            />
          </div>

          <!-- 密码 -->
          <div>
            <label for="password" class="block text-sm font-medium text-gray-800 mb-2">
              密码
            </label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              required
              class="input-field"
              placeholder="请输入密码"
              :disabled="loading"
            />
          </div>

          <!-- 确认密码（仅注册时显示） -->
          <div v-if="!isLoginMode">
            <label for="confirmPassword" class="block text-sm font-medium text-gray-800 mb-2">
              确认密码
            </label>
            <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              required
              class="input-field"
              placeholder="请再次输入密码"
              :disabled="loading"
            />
          </div>
        </div>

        <!-- 错误信息 -->
        <div v-if="errorMessage" class="bg-red-50 border border-red-200 rounded-md p-3">
          <p class="text-sm text-red-600">{{ errorMessage }}</p>
        </div>

        <!-- 提交按钮 -->
        <div>
          <button
            type="submit"
            :disabled="loading || !isFormValid"
            class="btn btn-primary w-full disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <LoadingSpinner v-if="loading" size="sm" />
            <span>{{ isLoginMode ? '登录' : '注册' }}</span>
          </button>
        </div>

        <!-- 忘记密码链接（仅登录时显示） -->
        <div v-if="isLoginMode" class="text-center">
          <button 
            type="button"
            @click="handleForgotPassword" 
            class="text-sm text-primary-600 hover:text-primary-500 transition-colors"
          >
            忘记密码？
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import LoadingSpinner from '../components/LoadingSpinner.vue'

const router = useRouter()
const authStore = useAuthStore()

const isLoginMode = ref(true)
const loading = ref(false)
const errorMessage = ref('')

const form = reactive({
  username: '',
  email: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

// 切换登录/注册模式
const toggleMode = () => {
  isLoginMode.value = !isLoginMode.value
  errorMessage.value = ''
  // 清空表单
  Object.keys(form).forEach(key => {
    (form as any)[key] = ''
  })
}

// 表单验证
const isFormValid = computed(() => {
  if (isLoginMode.value) {
    return form.username.trim() && form.password.trim()
  } else {
    return (
      form.username.trim() && 
      form.email.trim() && 
      form.password.trim() && 
      form.confirmPassword.trim() &&
      form.password === form.confirmPassword
    )
  }
})

// 处理表单提交
const handleSubmit = async () => {
  errorMessage.value = ''
  
  if (!isFormValid.value) {
    errorMessage.value = '请填写所有必填字段'
    return
  }

  if (!isLoginMode.value && form.password !== form.confirmPassword) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }

  loading.value = true

  try {
    let result
    
    if (isLoginMode.value) {
      result = await authStore.login({
        username: form.username,
        password: form.password
      })
    } else {
      result = await authStore.register({
        username: form.username,
        email: form.email,
        password: form.password,
        nickname: form.nickname || undefined
      })
    }

    if (result.success) {
      // 登录/注册成功，跳转到首页
      router.push('/')
    } else {
      errorMessage.value = result.message
    }
  } catch (error) {
    console.error('认证错误:', error)
    errorMessage.value = '操作失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

// 处理忘记密码
const handleForgotPassword = () => {
  // 这里可以添加忘记密码的逻辑
  console.log('忘记密码功能')
  alert('忘记密码功能正在开发中...')
}
</script>

<style scoped>
.input-field {
  @apply appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500 focus:z-10 sm:text-sm;
}

.btn {
  @apply flex justify-center items-center space-x-2 py-2 px-4 text-sm font-medium rounded-md focus:outline-none focus:ring-2 focus:ring-offset-2 transition-colors;
}

.btn-primary {
  @apply text-white bg-primary-600 hover:bg-primary-700 focus:ring-primary-500;
}
</style> 