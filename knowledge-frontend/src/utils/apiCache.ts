/**
 * API缓存服务
 * 用于优化API请求性能，减少重复请求
 */

interface CacheItem<T> {
  data: T
  timestamp: number
  ttl: number
}

interface CacheConfig {
  ttl: number // 缓存时间（毫秒）
  maxSize: number // 最大缓存条目数
}

class ApiCache {
  private cache = new Map<string, CacheItem<any>>()
  private config: CacheConfig

  constructor(config: Partial<CacheConfig> = {}) {
    this.config = {
      ttl: 5 * 60 * 1000, // 默认5分钟
      maxSize: 100, // 默认最大100个缓存条目
      ...config
    }
  }

  /**
   * 生成缓存键
   */
  private generateKey(url: string, params?: any): string {
    const paramStr = params ? JSON.stringify(params) : ''
    return `${url}${paramStr}`
  }

  /**
   * 检查缓存是否有效
   */
  private isCacheValid(item: CacheItem<any>): boolean {
    return Date.now() - item.timestamp < item.ttl
  }

  /**
   * 清理过期缓存
   */
  private cleanExpiredCache(): void {
    const now = Date.now()
    for (const [key, item] of this.cache.entries()) {
      if (now - item.timestamp >= item.ttl) {
        this.cache.delete(key)
      }
    }
  }

  /**
   * 清理最旧的缓存条目（当达到最大大小时）
   */
  private cleanOldestCache(): void {
    if (this.cache.size >= this.config.maxSize) {
      let oldestKey = ''
      let oldestTime = Date.now()

      for (const [key, item] of this.cache.entries()) {
        if (item.timestamp < oldestTime) {
          oldestTime = item.timestamp
          oldestKey = key
        }
      }

      if (oldestKey) {
        this.cache.delete(oldestKey)
      }
    }
  }

  /**
   * 设置缓存
   */
  set<T>(url: string, params: any, data: T, ttl?: number): void {
    this.cleanExpiredCache()
    this.cleanOldestCache()

    const key = this.generateKey(url, params)
    this.cache.set(key, {
      data,
      timestamp: Date.now(),
      ttl: ttl || this.config.ttl
    })
  }

  /**
   * 获取缓存
   */
  get<T>(url: string, params?: any): T | null {
    this.cleanExpiredCache()

    const key = this.generateKey(url, params)
    const item = this.cache.get(key)

    if (item && this.isCacheValid(item)) {
      return item.data
    }

    if (item) {
      this.cache.delete(key)
    }

    return null
  }

  /**
   * 删除缓存
   */
  delete(url: string, params?: any): boolean {
    const key = this.generateKey(url, params)
    return this.cache.delete(key)
  }

  /**
   * 清空所有缓存
   */
  clear(): void {
    this.cache.clear()
  }

  /**
   * 获取缓存统计信息
   */
  getStats(): { size: number; maxSize: number } {
    return {
      size: this.cache.size,
      maxSize: this.config.maxSize
    }
  }

  /**
   * 批量删除相关缓存
   */
  deletePattern(pattern: string): void {
    for (const key of this.cache.keys()) {
      if (key.includes(pattern)) {
        this.cache.delete(key)
      }
    }
  }
}

// 创建默认缓存实例
export const apiCache = new ApiCache()

// 创建不同TTL的缓存实例
export const shortTermCache = new ApiCache({ ttl: 1 * 60 * 1000 }) // 1分钟
export const longTermCache = new ApiCache({ ttl: 30 * 60 * 1000 }) // 30分钟

export default ApiCache 