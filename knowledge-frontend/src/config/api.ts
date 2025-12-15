import axios from 'axios'

// API配置
export const API_CONFIG = {
  // 开发环境API地址
  BASE_URL_DEV: 'http://localhost:8182/api',
  
  // 生产环境API地址
  BASE_URL_PROD: 'http://localhost:8182/api',
  
  // 超时时间 - 增加到30秒
  TIMEOUT: 30000,
  
  // 根据环境自动选择API地址
  BASE_URL: process.env.NODE_ENV === 'production' 
    ? 'http://localhost:8182/api'
    : 'http://localhost:8182/api'
}

// 响应状态码
export const HTTP_STATUS = {
  SUCCESS: 200,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500
}

// API响应接口定义
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 分页数据接口定义
export interface PageData<T = any> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 创建axios实例
export const request = axios.create({
  baseURL: API_CONFIG.BASE_URL,
  timeout: API_CONFIG.TIMEOUT,
  headers: {
    'Content-Type': 'application/json'
  },
  // 添加更多配置以提高连接稳定性
  withCredentials: true,  // 允许发送cookies
  maxRedirects: 5,        // 最大重定向次数
  maxContentLength: 50 * 1024 * 1024, // 50MB 最大内容长度
  maxBodyLength: 50 * 1024 * 1024      // 50MB 最大请求体长度
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 添加token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 添加请求标识，便于调试
    config.headers['X-Request-ID'] = `req-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
    
    return config
  },
  (error) => {
    console.error('Request interceptor error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    console.error('Response error:', error)
    
    // 处理网络错误
    if (error.code === 'ECONNABORTED') {
      console.error('请求超时，请检查网络连接')
      // 可以在这里添加用户提示
    }
    
    // 处理连接中断
    if (error.message && error.message.includes('Network Error')) {
      console.error('网络连接中断，请检查服务器状态')
      // 可以在这里添加用户提示
    }
    
    if (error.response?.status === HTTP_STATUS.UNAUTHORIZED) {
      // 清除token并跳转登录
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    
    return Promise.reject(error)
  }
) 