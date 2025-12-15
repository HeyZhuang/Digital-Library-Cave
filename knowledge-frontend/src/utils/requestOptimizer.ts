/**
 * 请求优化工具
 * 包含防抖、节流、请求去重等功能
 */

interface DebounceOptions {
  delay: number
  leading?: boolean
  trailing?: boolean
}

interface ThrottleOptions {
  delay: number
  leading?: boolean
  trailing?: boolean
}

/**
 * 防抖函数
 */
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  delay: number,
  options: Partial<DebounceOptions> = {}
): (...args: Parameters<T>) => void {
  const { leading = false, trailing = true } = options
  let timeoutId: NodeJS.Timeout | null = null
  let lastCallTime = 0

  return function debounced(...args: Parameters<T>) {
    const now = Date.now()
    const timeSinceLastCall = now - lastCallTime

    if (leading && timeSinceLastCall >= delay) {
      func.apply(this, args)
      lastCallTime = now
    }

    if (timeoutId) {
      clearTimeout(timeoutId)
    }

    timeoutId = setTimeout(() => {
      if (trailing) {
        func.apply(this, args)
      }
      timeoutId = null
    }, delay)
  }
}

/**
 * 节流函数
 */
export function throttle<T extends (...args: any[]) => any>(
  func: T,
  delay: number,
  options: Partial<ThrottleOptions> = {}
): (...args: Parameters<T>) => void {
  const { leading = true, trailing = true } = options
  let timeoutId: NodeJS.Timeout | null = null
  let lastCallTime = 0

  return function throttled(...args: Parameters<T>) {
    const now = Date.now()
    const timeSinceLastCall = now - lastCallTime

    if (timeSinceLastCall >= delay) {
      if (leading) {
        func.apply(this, args)
        lastCallTime = now
      }
    } else if (trailing && !timeoutId) {
      timeoutId = setTimeout(() => {
        func.apply(this, args)
        timeoutId = null
        lastCallTime = Date.now()
      }, delay - timeSinceLastCall)
    }
  }
}

/**
 * 请求去重管理器
 */
class RequestDeduplicator {
  private pendingRequests = new Map<string, Promise<any>>()

  /**
   * 执行去重请求
   */
  async deduplicate<T>(
    key: string,
    requestFn: () => Promise<T>
  ): Promise<T> {
    if (this.pendingRequests.has(key)) {
      return this.pendingRequests.get(key)!
    }

    const promise = requestFn().finally(() => {
      this.pendingRequests.delete(key)
    })

    this.pendingRequests.set(key, promise)
    return promise
  }

  /**
   * 取消请求
   */
  cancel(key: string): boolean {
    return this.pendingRequests.delete(key)
  }

  /**
   * 清空所有待处理请求
   */
  clear(): void {
    this.pendingRequests.clear()
  }

  /**
   * 获取待处理请求数量
   */
  getPendingCount(): number {
    return this.pendingRequests.size
  }
}

// 创建全局请求去重实例
export const requestDeduplicator = new RequestDeduplicator()

/**
 * 请求重试工具
 */
export class RequestRetry {
  private maxRetries: number
  private retryDelay: number
  private backoffMultiplier: number

  constructor(options: {
    maxRetries?: number
    retryDelay?: number
    backoffMultiplier?: number
  } = {}) {
    this.maxRetries = options.maxRetries || 3
    this.retryDelay = options.retryDelay || 1000
    this.backoffMultiplier = options.backoffMultiplier || 2
  }

  /**
   * 执行带重试的请求
   */
  async execute<T>(
    requestFn: () => Promise<T>,
    shouldRetry?: (error: any) => boolean
  ): Promise<T> {
    let lastError: any

    for (let attempt = 0; attempt <= this.maxRetries; attempt++) {
      try {
        return await requestFn()
      } catch (error) {
        lastError = error

        // 检查是否应该重试
        if (attempt === this.maxRetries || (shouldRetry && !shouldRetry(error))) {
          throw error
        }

        // 等待后重试
        await this.delay(this.retryDelay * Math.pow(this.backoffMultiplier, attempt))
      }
    }

    throw lastError
  }

  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms))
  }
}

/**
 * 请求队列管理器
 */
export class RequestQueue {
  private queue: Array<() => Promise<any>> = []
  private processing = false
  private concurrency: number
  private running = 0

  constructor(concurrency = 3) {
    this.concurrency = concurrency
  }

  /**
   * 添加请求到队列
   */
  async add<T>(requestFn: () => Promise<T>): Promise<T> {
    return new Promise((resolve, reject) => {
      this.queue.push(async () => {
        try {
          const result = await requestFn()
          resolve(result)
        } catch (error) {
          reject(error)
        }
      })

      this.process()
    })
  }

  /**
   * 处理队列
   */
  private async process(): Promise<void> {
    if (this.processing || this.running >= this.concurrency) {
      return
    }

    this.processing = true

    while (this.queue.length > 0 && this.running < this.concurrency) {
      const requestFn = this.queue.shift()
      if (requestFn) {
        this.running++
        requestFn().finally(() => {
          this.running--
          this.process()
        })
      }
    }

    this.processing = false
  }

  /**
   * 清空队列
   */
  clear(): void {
    this.queue = []
  }

  /**
   * 获取队列状态
   */
  getStatus(): { queueLength: number; running: number; concurrency: number } {
    return {
      queueLength: this.queue.length,
      running: this.running,
      concurrency: this.concurrency
    }
  }
}

// 创建默认请求队列实例
export const requestQueue = new RequestQueue(5) 