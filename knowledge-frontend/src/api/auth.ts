import axios from 'axios'

const BASE_URL = 'http://localhost:8182/api'
// const BASE_URL = 'localhost:8182/api'

// 配置axios实例
const api = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  },
  // 添加参数序列化器，正确处理数组参数
  paramsSerializer: {
    serialize: (params: any) => {
      const searchParams = new URLSearchParams()
      
      Object.keys(params).forEach(key => {
        const value = params[key]
        if (Array.isArray(value)) {
          // 对于数组，使用重复的key而不是key[]格式
          value.forEach(item => {
            searchParams.append(key, String(item))
          })
        } else if (value !== null && value !== undefined) {
          searchParams.append(key, String(value))
        }
      })
      
      return searchParams.toString()
    }
  }
})

// 请求拦截器 - 添加JWT token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      // 清除过期token并跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    console.error('API调用错误:', error)
    return Promise.reject(error)
  }
)

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
  nickname?: string
}

export interface User {
  id: number
  username: string
  email: string
  nickname?: string
  avatar?: string
  createdAt: string
}

export interface AuthResponse {
  code: number
  message: string
  data: {
    token: string
    user: User
  }
  timestamp: number
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 认证API接口
export const authApi = {
  // 用户登录
  login(data: LoginRequest): Promise<AuthResponse> {
    return api.post('/auth/login', data)
  },

  // 用户注册
  register(data: RegisterRequest): Promise<AuthResponse> {
    return api.post('/auth/register', data)
  },

  // 获取当前用户信息
  getCurrentUser(): Promise<ApiResponse<User>> {
    return api.get('/auth/me')
  },

  // 用户登出
  logout(): Promise<ApiResponse<void>> {
    return api.post('/auth/logout')
  },

  // 刷新token
  refreshToken(): Promise<AuthResponse> {
    return api.post('/auth/refresh')
  },

  // 更新当前用户信息（预留，需后端支持）
  updateProfile(data: Partial<User>): Promise<ApiResponse<User>> {
    return api.put('/auth/me', data)
  }
}

export { api } 