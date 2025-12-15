import { api } from './auth'
import { apiCache, shortTermCache } from '../utils/apiCache'
import { debounce, throttle, requestDeduplicator, RequestRetry } from '../utils/requestOptimizer'

export interface Article {
  id?: number
  title: string
  content: string
  summary?: string
  authorId?: number
  categoryId?: number
  status?: number
  views?: number
  likes?: number
  commentsCount?: number
  isTop?: boolean
  allowComments?: boolean
  publishTime?: string
  createdAt?: string
  updatedAt?: string
  authorName?: string
  categoryName?: string
}

export interface CreateArticleRequest {
  title: string
  content: string
  summary?: string
  categoryId?: number
  status?: number
  isTop?: boolean
  allowComments?: boolean
  tags?: string[]
}

export interface UpdateArticleRequest extends CreateArticleRequest {
  id: number
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface PageData<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 创建请求重试实例
const requestRetry = new RequestRetry({
  maxRetries: 3,
  retryDelay: 1000,
  backoffMultiplier: 2
})

// 文章API接口 - 优化版本
export const articleApi = {
  // 分页查询文章列表 - 带缓存和防抖
  getArticles: debounce(async (pageNum = 1, pageSize = 10, status?: number): Promise<ApiResponse<PageData<Article>>> => {
    const cacheKey = `articles_${pageNum}_${pageSize}_${status}`
    
    // 尝试从缓存获取
    const cached = apiCache.get<ApiResponse<PageData<Article>>>('/articles', { pageNum, pageSize, status })
    if (cached) {
      return cached
    }

    // 使用请求去重
    return requestDeduplicator.deduplicate(cacheKey, async () => {
      const response = await requestRetry.execute(() => 
        api.get('/articles', { params: { pageNum, pageSize, status } })
      )
      
      // 缓存结果
      apiCache.set('/articles', { pageNum, pageSize, status }, response, 2 * 60 * 1000) // 2分钟缓存
      
      return response
    })
  }, 300),

  // 根据ID查询文章详情 - 带缓存
  getArticleById: async (id: number): Promise<ApiResponse<Article>> => {
    const cacheKey = `article_${id}`
    
    // 尝试从缓存获取
    const cached = apiCache.get<ApiResponse<Article>>(`/articles/${id}`)
    if (cached) {
      return cached
    }

    // 使用请求去重
    return requestDeduplicator.deduplicate(cacheKey, async () => {
      const response = await requestRetry.execute(() => 
        api.get(`/articles/${id}`)
      )
      
      // 缓存结果
      apiCache.set(`/articles/${id}`, {}, response, 5 * 60 * 1000) // 5分钟缓存
      
      return response
    })
  },

  // 创建文章 - 清除相关缓存
  createArticle: async (article: CreateArticleRequest): Promise<ApiResponse<Article>> => {
    const { tags, ...articleData } = article
    const requestData = {
      ...articleData,
      tags: tags || []
    }
    
    const response = await api.post('/articles', requestData)
    
    // 清除相关缓存
    apiCache.deletePattern('articles')
    apiCache.deletePattern('latest')
    apiCache.deletePattern('popular')
    
    return response
  },

  // 更新文章 - 清除相关缓存
  updateArticle: async (article: UpdateArticleRequest): Promise<ApiResponse<Article>> => {
    const { id, tags, ...articleData } = article
    const requestData = {
      ...articleData,
      tags: tags || []
    }
    
    const response = await api.put(`/articles/${id}`, requestData)
    
    // 清除相关缓存
    apiCache.delete(`/articles/${id}`)
    apiCache.deletePattern('articles')
    apiCache.deletePattern('latest')
    apiCache.deletePattern('popular')
    
    return response
  },

  // 删除文章 - 清除相关缓存
  deleteArticle: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete(`/articles/${id}`)
    
    // 清除相关缓存
    apiCache.delete(`/articles/${id}`)
    apiCache.deletePattern('articles')
    apiCache.deletePattern('latest')
    apiCache.deletePattern('popular')
    
    return response
  },

  // 发布文章 - 清除相关缓存
  publishArticle: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.post(`/articles/${id}/publish`)
    
    // 清除相关缓存
    apiCache.delete(`/articles/${id}`)
    apiCache.deletePattern('articles')
    apiCache.deletePattern('latest')
    apiCache.deletePattern('popular')
    
    return response
  },

  // 设为草稿 - 清除相关缓存
  draftArticle: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.post(`/articles/${id}/draft`)
    
    // 清除相关缓存
    apiCache.delete(`/articles/${id}`)
    apiCache.deletePattern('articles')
    
    return response
  },

  // 搜索文章 - 带防抖和缓存
  searchArticles: debounce(async (keyword: string, pageNum = 1, pageSize = 10): Promise<ApiResponse<PageData<Article>>> => {
    const cacheKey = `search_${keyword}_${pageNum}_${pageSize}`
    
    // 尝试从缓存获取
    const cached = shortTermCache.get<ApiResponse<PageData<Article>>>('/articles/search', { keyword, pageNum, pageSize })
    if (cached) {
      return cached
    }

    // 使用请求去重
    return requestDeduplicator.deduplicate(cacheKey, async () => {
      const response = await requestRetry.execute(() => 
        api.get('/articles/search', { params: { keyword, pageNum, pageSize } })
      )
      
      // 缓存结果
      shortTermCache.set('/articles/search', { keyword, pageNum, pageSize }, response, 1 * 60 * 1000) // 1分钟缓存
      
      return response
    })
  }, 500),

  // 获取热门文章 - 带缓存
  getPopularArticles: throttle(async (limit = 10): Promise<ApiResponse<Article[]>> => {
    const cacheKey = `popular_${limit}`
    
    // 尝试从缓存获取
    const cached = apiCache.get<ApiResponse<Article[]>>('/articles/popular', { limit })
    if (cached) {
      return cached
    }

    // 使用请求去重
    return requestDeduplicator.deduplicate(cacheKey, async () => {
      const response = await requestRetry.execute(() => 
        api.get('/articles/popular', { params: { limit } })
      )
      
      // 缓存结果
      apiCache.set('/articles/popular', { limit }, response, 10 * 60 * 1000) // 10分钟缓存
      
      return response
    })
  }, 1000),

  // 获取最新文章 - 带缓存
  getLatestArticles: throttle(async (limit = 10): Promise<ApiResponse<Article[]>> => {
    const cacheKey = `latest_${limit}`
    
    // 尝试从缓存获取
    const cached = apiCache.get<ApiResponse<Article[]>>('/articles/latest', { limit })
    if (cached) {
      return cached
    }

    // 使用请求去重
    return requestDeduplicator.deduplicate(cacheKey, async () => {
      const response = await requestRetry.execute(() => 
        api.get('/articles/latest', { params: { limit } })
      )
      
      // 缓存结果
      apiCache.set('/articles/latest', { limit }, response, 5 * 60 * 1000) // 5分钟缓存
      
      return response
    })
  }, 1000),

  // 点赞文章 - 带防抖
  likeArticle: debounce(async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.post(`/articles/${id}/like`)
    
    // 清除相关缓存
    apiCache.delete(`/articles/${id}`)
    apiCache.deletePattern('popular')
    
    return response
  }, 300),

  // 取消点赞 - 带防抖
  unlikeArticle: debounce(async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete(`/articles/${id}/like`)
    
    // 清除相关缓存
    apiCache.delete(`/articles/${id}`)
    apiCache.deletePattern('popular')
    
    return response
  }, 300),

  // 清除所有文章相关缓存
  clearCache: () => {
    apiCache.deletePattern('articles')
    apiCache.deletePattern('latest')
    apiCache.deletePattern('popular')
    apiCache.deletePattern('search')
  }
} 