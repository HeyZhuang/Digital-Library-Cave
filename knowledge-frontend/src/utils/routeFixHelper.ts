import type { Router } from 'vue-router'

/**
 * 路由修复助手 - 解决点击链接需要刷新才能跳转的问题
 * 这个工具类专门处理单页应用中常见的路由跳转问题
 */
export class RouteFixHelper {
  private static router: Router
  private static isInitialized = false
  private static navigationInProgress = false

  /**
   * 初始化路由修复助手
   */
  static init(router: Router) {
    if (this.isInitialized) {
      console.warn('RouteFixHelper 已经初始化过了')
      return
    }

    this.router = router
    this.setupErrorHandling()
    this.setupCacheManagement()
    this.setupNavigationInterception()
    this.setupEventListeners()
    this.isInitialized = true
    
    console.log('✅ RouteFixHelper 初始化完成')
  }

  /**
   * 安全的路由跳转 - 确保不需要刷新就能正常跳转
   */
  static async safeNavigate(path: string, options?: { 
    replace?: boolean
    query?: Record<string, any>
    force?: boolean
  }) {
    if (this.navigationInProgress && !options?.force) {
      console.warn('导航正在进行中，跳过此次导航')
      return false
    }

    try {
      this.navigationInProgress = true
      
      // 1. 清除缓存
      this.clearAllCache()
      
      // 2. 确保DOM已准备好
      await this.waitForDOMReady()
      
      // 3. 执行导航
      const navigationPromise = options?.replace 
        ? this.router.replace({ path, query: options.query })
        : this.router.push({ path, query: options.query })
      
      await navigationPromise
      
      // 4. 导航后处理
      await this.postNavigationCleanup()
      
      console.log(`✅ 导航成功: ${path}`)
      return true
      
    } catch (error) {
      console.error('路由跳转失败:', error)
      
      // 降级方案：使用 location.href
      console.log('🔄 使用降级方案：直接跳转')
      return this.fallbackNavigation(path)
      
    } finally {
      this.navigationInProgress = false
    }
  }

  /**
   * 设置错误处理
   */
  private static setupErrorHandling() {
    // 路由错误处理
    this.router.onError((error) => {
      console.error('Vue Router 错误:', error)
      this.handleRouterError(error)
    })

    // 全局错误处理
    window.addEventListener('error', (event) => {
      if (event.message.includes('router') || event.message.includes('navigation')) {
        console.error('全局路由相关错误:', event.error)
        this.handleNavigationError(event.error)
      }
    })

    // Promise 拒绝处理
    window.addEventListener('unhandledrejection', (event) => {
      if (event.reason?.message?.includes('navigation')) {
        console.error('未处理的导航 Promise 拒绝:', event.reason)
        event.preventDefault()
      }
    })
  }

  /**
   * 设置缓存管理
   */
  private static setupCacheManagement() {
    // 页面加载时清除缓存
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', () => this.clearAllCache())
    } else {
      this.clearAllCache()
    }

    // 页面隐藏/显示时管理缓存
    document.addEventListener('visibilitychange', () => {
      if (document.visibilityState === 'visible') {
        this.clearAllCache()
      }
    })

    // 浏览器前进/后退时清除缓存
    window.addEventListener('popstate', () => {
      this.clearAllCache()
    })
  }

  /**
   * 设置导航拦截
   */
  private static setupNavigationInterception() {
    // 拦截所有链接点击
    document.addEventListener('click', async (event) => {
      const target = event.target as HTMLElement
      const link = target.closest('a')
      
      if (!link) return
      
      // 检查是否是站内链接
      if (this.isInternalLink(link)) {
        const href = link.getAttribute('href')
        
        // 如果不是 RouterLink (没有 router-link 类或属性)
        if (!this.isRouterLink(link) && href) {
          event.preventDefault()
          event.stopPropagation()
          
          console.log(`🔗 拦截普通链接点击: ${href}`)
          await this.safeNavigate(href)
        }
      }
    }, true) // 使用捕获阶段

    // 拦截表单提交导航
    document.addEventListener('submit', (event) => {
      const form = event.target as HTMLFormElement
      if (form.method.toLowerCase() === 'get') {
        // 可以在这里处理表单导航
      }
    })
  }

  /**
   * 设置事件监听器
   */
  private static setupEventListeners() {
    // 监听路由变化
    this.router.beforeEach(async (to, from, next) => {
      console.log(`🚀 路由即将从 ${from.path} 跳转到 ${to.path}`)
      
      // 清除缓存
      this.clearAllCache()
      
      next()
    })

    this.router.afterEach((to, from) => {
      console.log(`✅ 路由已从 ${from.path} 跳转到 ${to.path}`)
      
      // 导航后清理
      this.postNavigationCleanup()
    })
  }

  /**
   * 清除所有可能的缓存
   */
  private static clearAllCache() {
    try {
      // 清除 sessionStorage
      if (window.sessionStorage) {
        const sessionKeys = ['vue-router-cache', 'vue-router-scroll', 'navigation-cache']
        sessionKeys.forEach(key => {
          try {
            sessionStorage.removeItem(key)
          } catch (e) {
            console.warn(`清除 sessionStorage 键 ${key} 失败:`, e)
          }
        })
      }

      // 清除 localStorage 中的路由相关项
      if (window.localStorage) {
        const keysToRemove = []
        for (let i = 0; i < localStorage.length; i++) {
          const key = localStorage.key(i)
          if (key && (
            key.startsWith('vue-router-') || 
            key.startsWith('route-cache-') ||
            key.includes('navigation-')
          )) {
            keysToRemove.push(key)
          }
        }
        keysToRemove.forEach(key => {
          try {
            localStorage.removeItem(key)
          } catch (e) {
            console.warn(`清除 localStorage 键 ${key} 失败:`, e)
          }
        })
      }

      // 清除可能的组件缓存
      if (window.Vue && (window.Vue as any).__v_cache) {
        (window.Vue as any).__v_cache.clear?.()
      }

    } catch (error) {
      console.warn('清除缓存时发生错误:', error)
    }
  }

  /**
   * 等待DOM准备就绪
   */
  private static async waitForDOMReady(): Promise<void> {
    return new Promise((resolve) => {
      if (document.readyState === 'complete' || document.readyState === 'interactive') {
        resolve()
      } else {
        document.addEventListener('DOMContentLoaded', () => resolve(), { once: true })
      }
    })
  }

  /**
   * 导航后清理
   */
  private static async postNavigationCleanup() {
    // 滚动到顶部
    try {
      window.scrollTo({ top: 0, left: 0, behavior: 'smooth' })
    } catch {
      window.scrollTo(0, 0)
    }

    // 清除可能的定时器
    for (let i = 1; i < 10000; i++) {
      clearTimeout(i)
    }

    // 等待组件渲染完成
    await this.nextTick()
  }

  /**
   * 下一个微任务
   */
  private static async nextTick(): Promise<void> {
    return new Promise(resolve => {
      Promise.resolve().then(resolve)
    })
  }

  /**
   * 检查是否是站内链接
   */
  private static isInternalLink(link: HTMLAnchorElement): boolean {
    try {
      const linkUrl = new URL(link.href)
      const currentUrl = new URL(window.location.href)
      return linkUrl.origin === currentUrl.origin
    } catch {
      return false
    }
  }

  /**
   * 检查是否是 RouterLink
   */
  private static isRouterLink(link: HTMLAnchorElement): boolean {
    return link.classList.contains('router-link') || 
           link.hasAttribute('router-link') ||
           link.closest('[router-link]') !== null
  }

  /**
   * 处理路由错误
   */
  private static handleRouterError(error: any) {
    if (error.name === 'NavigationDuplicated') {
      // 重复导航错误，忽略
      return
    }

    console.error('处理路由错误:', error)
    
    // 尝试重新导航到当前页面
    const currentPath = this.router.currentRoute.value.fullPath
    setTimeout(() => {
      this.fallbackNavigation(currentPath)
    }, 100)
  }

  /**
   * 处理导航错误
   */
  private static handleNavigationError(error: any) {
    console.error('处理导航错误:', error)
    
    // 可以在这里添加用户提示
    this.showNavigationError()
  }

  /**
   * 降级导航方案
   */
  private static fallbackNavigation(path: string): boolean {
    try {
      console.log(`🔄 使用降级方案导航到: ${path}`)
      window.location.href = path
      return true
    } catch (error) {
      console.error('降级导航也失败了:', error)
      return false
    }
  }

  /**
   * 显示导航错误提示
   */
  private static showNavigationError() {
    // 可以集成到项目的通知系统
    console.error('导航出现问题，请刷新页面重试')
  }

  /**
   * 强制刷新当前页面
   */
  static forceRefresh() {
    window.location.reload()
  }

  /**
   * 检查导航是否正常工作
   */
  static async testNavigation(): Promise<boolean> {
    try {
      const testPath = '/test-router'
      const result = await this.safeNavigate(testPath, { force: true })
      
      // 导航回原页面
      setTimeout(() => {
        window.history.back()
      }, 100)
      
      return result
    } catch {
      return false
    }
  }
}

/**
 * Vue 组合式 API 的路由修复助手
 */
export function useRouteFixHelper() {
  return {
    safeNavigate: RouteFixHelper.safeNavigate.bind(RouteFixHelper),
    clearCache: RouteFixHelper.clearAllCache.bind(RouteFixHelper),
    testNavigation: RouteFixHelper.testNavigation.bind(RouteFixHelper),
    forceRefresh: RouteFixHelper.forceRefresh.bind(RouteFixHelper)
  }
}

// 导出类型
export interface NavigationOptions {
  replace?: boolean
  query?: Record<string, any>
  force?: boolean
} 