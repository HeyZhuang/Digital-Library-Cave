import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, type User, type LoginRequest, type RegisterRequest } from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)
  const loading = ref(false)
  const initialized = ref(false)  // 添加初始化状态

  // 计算属性
  const isAuthenticated = computed(() => !!token.value && !!user.value)
  const userInfo = computed(() => user.value)

  // 初始化认证状态 - 改为异步
  const initAuth = async () => {
    if (initialized.value) return  // 避免重复初始化
    
    try {
      const savedToken = localStorage.getItem('token')
      const savedUser = localStorage.getItem('user')
      
      if (savedToken && savedUser) {
        token.value = savedToken
        try {
          user.value = JSON.parse(savedUser)
          // 验証token是否仍然有效
          await fetchCurrentUser()
        } catch (error) {
          console.error('解析用户信息失败:', error)
          clearAuth()
        }
      }
    } catch (error) {
      console.error('初始化认证状态失败:', error)
      clearAuth()
    } finally {
      initialized.value = true
    }
  }

  // 登录
  const login = async (credentials: LoginRequest) => {
    try {
      loading.value = true
      const response = await authApi.login(credentials)
      
      if (response.code === 200) {
        token.value = response.data.token
        user.value = response.data.user
        
        // 保存到本地存储
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('user', JSON.stringify(response.data.user))
        
        return { success: true, message: '登录成功' }
      } else {
        return { success: false, message: response.message || '登录失败' }
      }
    } catch (error: any) {
      console.error('登录错误:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '登录失败，请检查网络连接' 
      }
    } finally {
      loading.value = false
    }
  }

  // 注册
  const register = async (data: RegisterRequest) => {
    try {
      loading.value = true
      const response = await authApi.register(data)
      
      if (response.code === 200) {
        token.value = response.data.token
        user.value = response.data.user
        
        // 保存到本地存储
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('user', JSON.stringify(response.data.user))
        
        return { success: true, message: '注册成功' }
      } else {
        return { success: false, message: response.message || '注册失败' }
      }
    } catch (error: any) {
      console.error('注册错误:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '注册失败，请检查网络连接' 
      }
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = async () => {
    try {
      await authApi.logout()
    } catch (error) {
      console.error('登出请求失败:', error)
    } finally {
      clearAuth()
    }
  }

  // 清除认证信息
  const clearAuth = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // 获取当前用户信息
  const fetchCurrentUser = async () => {
    try {
      const response = await authApi.getCurrentUser()
      if (response.code === 200) {
        user.value = response.data
        localStorage.setItem('user', JSON.stringify(response.data))
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      clearAuth()
    }
  }

  return {
    user,
    token,
    loading,
    initialized,
    isAuthenticated,
    userInfo,
    initAuth,
    login,
    register,
    logout,
    clearAuth,
    fetchCurrentUser
  }
}) 