/**
 * 前端性能监控工具
 * 监控页面加载性能、API请求性能、用户交互性能等
 */

interface PerformanceMetrics {
  // 页面加载性能
  pageLoadTime: number
  domContentLoaded: number
  firstContentfulPaint: number
  largestContentfulPaint: number
  firstInputDelay: number
  
  // API请求性能
  apiRequestCount: number
  apiRequestTime: number
  apiErrorCount: number
  
  // 内存使用
  memoryUsage: {
    usedJSHeapSize: number
    totalJSHeapSize: number
    jsHeapSizeLimit: number
  }
  
  // 用户交互
  userInteractions: {
    clicks: number
    scrolls: number
    keypresses: number
  }
}

interface PerformanceConfig {
  enableConsoleLog?: boolean
  enableMetricsCollection?: boolean
  sampleRate?: number
  maxMetricsHistory?: number
}

class PerformanceMonitor {
  private config: PerformanceConfig
  private metrics: PerformanceMetrics
  private metricsHistory: PerformanceMetrics[] = []
  private observers: Map<string, PerformanceObserver> = new Map()
  private isInitialized = false

  constructor(config: PerformanceConfig = {}) {
    this.config = {
      enableConsoleLog: true,
      enableMetricsCollection: true,
      sampleRate: 1.0, // 100%采样
      maxMetricsHistory: 100,
      ...config
    }

    this.metrics = this.initializeMetrics()
  }

  /**
   * 初始化性能指标
   */
  private initializeMetrics(): PerformanceMetrics {
    return {
      pageLoadTime: 0,
      domContentLoaded: 0,
      firstContentfulPaint: 0,
      largestContentfulPaint: 0,
      firstInputDelay: 0,
      apiRequestCount: 0,
      apiRequestTime: 0,
      apiErrorCount: 0,
      memoryUsage: {
        usedJSHeapSize: 0,
        totalJSHeapSize: 0,
        jsHeapSizeLimit: 0
      },
      userInteractions: {
        clicks: 0,
        scrolls: 0,
        keypresses: 0
      }
    }
  }

  /**
   * 初始化性能监控
   */
  init(): void {
    if (this.isInitialized) return

    this.setupPageLoadMetrics()
    this.setupUserInteractionMetrics()
    this.setupMemoryMonitoring()
    this.setupPerformanceObservers()

    this.isInitialized = true
    
    if (this.config.enableConsoleLog) {
      console.log('性能监控已初始化')
    }
  }

  /**
   * 设置页面加载性能指标
   */
  private setupPageLoadMetrics(): void {
    window.addEventListener('load', () => {
      const navigation = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming
      if (navigation) {
        this.metrics.pageLoadTime = navigation.loadEventEnd - navigation.loadEventStart
        this.metrics.domContentLoaded = navigation.domContentLoadedEventEnd - navigation.domContentLoadedEventStart
      }
    })
  }

  /**
   * 设置用户交互监控
   */
  private setupUserInteractionMetrics(): void {
    // 点击监控
    document.addEventListener('click', () => {
      this.metrics.userInteractions.clicks++
    })

    // 滚动监控（节流）
    let scrollTimeout: NodeJS.Timeout
    document.addEventListener('scroll', () => {
      if (scrollTimeout) clearTimeout(scrollTimeout)
      scrollTimeout = setTimeout(() => {
        this.metrics.userInteractions.scrolls++
      }, 100)
    })

    // 键盘输入监控
    document.addEventListener('keypress', () => {
      this.metrics.userInteractions.keypresses++
    })
  }

  /**
   * 设置内存监控
   */
  private setupMemoryMonitoring(): void {
    if ('memory' in performance) {
      setInterval(() => {
        const memory = (performance as any).memory
        this.metrics.memoryUsage = {
          usedJSHeapSize: memory.usedJSHeapSize,
          totalJSHeapSize: memory.totalJSHeapSize,
          jsHeapSizeLimit: memory.jsHeapSizeLimit
        }
      }, 5000) // 每5秒检查一次
    }
  }

  /**
   * 设置性能观察器
   */
  private setupPerformanceObservers(): void {
    // First Contentful Paint
    if ('PerformanceObserver' in window) {
      try {
        const fcpObserver = new PerformanceObserver((list) => {
          const entries = list.getEntries()
          const fcp = entries[entries.length - 1]
          this.metrics.firstContentfulPaint = fcp.startTime
        })
        fcpObserver.observe({ entryTypes: ['paint'] })

        // Largest Contentful Paint
        const lcpObserver = new PerformanceObserver((list) => {
          const entries = list.getEntries()
          const lcp = entries[entries.length - 1]
          this.metrics.largestContentfulPaint = lcp.startTime
        })
        lcpObserver.observe({ entryTypes: ['largest-contentful-paint'] })

        // First Input Delay
        const fidObserver = new PerformanceObserver((list) => {
          const entries = list.getEntries()
          const fid = entries[entries.length - 1]
          this.metrics.firstInputDelay = fid.processingStart - fid.startTime
        })
        fidObserver.observe({ entryTypes: ['first-input'] })

        this.observers.set('fcp', fcpObserver)
        this.observers.set('lcp', lcpObserver)
        this.observers.set('fid', fidObserver)
      } catch (error) {
        console.warn('性能观察器设置失败:', error)
      }
    }
  }

  /**
   * 记录API请求
   */
  recordApiRequest(duration: number, success: boolean = true): void {
    this.metrics.apiRequestCount++
    this.metrics.apiRequestTime += duration
    
    if (!success) {
      this.metrics.apiErrorCount++
    }
  }

  /**
   * 获取当前性能指标
   */
  getCurrentMetrics(): PerformanceMetrics {
    return { ...this.metrics }
  }

  /**
   * 获取性能指标历史
   */
  getMetricsHistory(): PerformanceMetrics[] {
    return [...this.metricsHistory]
  }

  /**
   * 保存当前指标到历史记录
   */
  saveMetrics(): void {
    if (Math.random() <= this.config.sampleRate!) {
      this.metricsHistory.push({ ...this.metrics })
      
      // 限制历史记录数量
      if (this.metricsHistory.length > this.config.maxMetricsHistory!) {
        this.metricsHistory.shift()
      }
    }
  }

  /**
   * 生成性能报告
   */
  generateReport(): string {
    const avgApiTime = this.metrics.apiRequestCount > 0 
      ? this.metrics.apiRequestTime / this.metrics.apiRequestCount 
      : 0

    const memoryUsagePercent = this.metrics.memoryUsage.jsHeapSizeLimit > 0
      ? (this.metrics.memoryUsage.usedJSHeapSize / this.metrics.memoryUsage.jsHeapSizeLimit) * 100
      : 0

    return `
性能监控报告:
==============

页面加载性能:
- 页面加载时间: ${this.metrics.pageLoadTime.toFixed(2)}ms
- DOM内容加载: ${this.metrics.domContentLoaded.toFixed(2)}ms
- 首次内容绘制: ${this.metrics.firstContentfulPaint.toFixed(2)}ms
- 最大内容绘制: ${this.metrics.largestContentfulPaint.toFixed(2)}ms
- 首次输入延迟: ${this.metrics.firstInputDelay.toFixed(2)}ms

API请求性能:
- 请求总数: ${this.metrics.apiRequestCount}
- 平均请求时间: ${avgApiTime.toFixed(2)}ms
- 错误数量: ${this.metrics.apiErrorCount}
- 成功率: ${this.metrics.apiRequestCount > 0 ? ((this.metrics.apiRequestCount - this.metrics.apiErrorCount) / this.metrics.apiRequestCount * 100).toFixed(2) : 0}%

内存使用:
- 已用堆内存: ${(this.metrics.memoryUsage.usedJSHeapSize / 1024 / 1024).toFixed(2)}MB
- 总堆内存: ${(this.metrics.memoryUsage.totalJSHeapSize / 1024 / 1024).toFixed(2)}MB
- 内存使用率: ${memoryUsagePercent.toFixed(2)}%

用户交互:
- 点击次数: ${this.metrics.userInteractions.clicks}
- 滚动次数: ${this.metrics.userInteractions.scrolls}
- 按键次数: ${this.metrics.userInteractions.keypresses}
    `.trim()
  }

  /**
   * 输出性能报告到控制台
   */
  logReport(): void {
    if (this.config.enableConsoleLog) {
      console.log(this.generateReport())
    }
  }

  /**
   * 清理资源
   */
  destroy(): void {
    this.observers.forEach(observer => {
      observer.disconnect()
    })
    this.observers.clear()
    this.isInitialized = false
  }

  /**
   * 重置指标
   */
  reset(): void {
    this.metrics = this.initializeMetrics()
    this.metricsHistory = []
  }
}

// 创建全局性能监控实例
export const performanceMonitor = new PerformanceMonitor()

// 自动初始化
if (typeof window !== 'undefined') {
  performanceMonitor.init()
}

export default PerformanceMonitor 