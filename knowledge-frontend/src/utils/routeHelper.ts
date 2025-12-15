import type { Router } from 'vue-router'

/**
 * 路由辅助工具类
 * 解决单页应用中的路由跳转和缓存问题
 */
export class RouteHelper {
  private static router: Router

  /**
   * 初始化路由助手
   */
  static init(router: Router) {
    this.router = router
    this.setupGlobalHandlers()
  }

  /**
   * 安全的路由跳转
   * 确保跳转正常工作，不需要刷新页面
   */
  static async navigateTo(path: string, options?: { replace?: boolean, query?: Record<string, any> }) {
    try {
      // 清除可能的缓存
      this.clearRouteCache()
      
      // 执行跳转
      if (options?.replace) {
        await this.router.replace({ path, query: options.query })
      } else {
        await this.router.push({ path, query: options.query })
      }
      
      // 确保页面滚动到顶部
      this.scrollToTop()
      
      return true
    } catch (error) {
      console.error('路由跳转失败:', error)
      // 如果路由跳转失败，尝试直接设置location
      window.location.href = path
      return false
    }
  }

  /**
   * 清除路由相关缓存
   */
  static clearRouteCache() {
    try {
      // 清除sessionStorage中的路由缓存
      if (window.sessionStorage) {
        window.sessionStorage.removeItem('vue-router-cache')
        window.sessionStorage.removeItem('vue-router-scroll')
      }
      
      // 清除可能的组件缓存
      if (window.localStorage) {
        const keysToRemove = []
        for (let i = 0; i < window.localStorage.length; i++) {
          const key = window.localStorage.key(i)
          if (key && key.startsWith('vue-router-')) {
            keysToRemove.push(key)
          }
        }
        keysToRemove.forEach(key => window.localStorage.removeItem(key))
      }
    } catch (error) {
      console.warn('清除路由缓存失败:', error)
    }
  }

  /**
   * 滚动到页面顶部
   */
  static scrollToTop(smooth = true) {
    try {
      window.scrollTo({
        top: 0,
        left: 0,
        behavior: smooth ? 'smooth' : 'auto'
      })
    } catch (error) {
      // 兼容性处理
      window.scrollTo(0, 0)
    }
  }

  /**
   * 检查当前路径是否匹配
   */
  static isCurrentPath(path: string): boolean {
    return this.router.currentRoute.value.path === path
  }

  /**
   * 设置全局处理器
   */
  private static setupGlobalHandlers() {
    // 处理浏览器前进/后退按钮
    window.addEventListener('popstate', () => {
      this.clearRouteCache()
    })

    // 处理页面可见性变化
    document.addEventListener('visibilitychange', () => {
      if (document.visibilityState === 'visible') {
        this.clearRouteCache()
      }
    })

    // 拦截所有a标签点击
    document.addEventListener('click', (event) => {
      const target = event.target as HTMLElement
      const link = target.closest('a')
      
      if (link && link.href) {
        const url = new URL(link.href)
        const currentOrigin = window.location.origin
        
        // 如果是站内链接但使用了href属性，转换为路由跳转
        if (url.origin === currentOrigin && !link.getAttribute('router-link')) {
          const path = url.pathname + url.search + url.hash
          
          // 阻止默认的链接跳转
          event.preventDefault()
          event.stopPropagation()
          
          // 使用路由跳转
          this.navigateTo(path)
        }
      }
    })
  }

  /**
   * 强制刷新当前路由
   */
  static async refreshCurrentRoute() {
    const currentRoute = this.router.currentRoute.value
    await this.router.replace({ path: '/redirect' + currentRoute.fullPath })
  }

  /**
   * 预加载路由组件
   */
  static async preloadRoute(path: string) {
    try {
      const route = this.router.resolve(path)
      if (route.matched.length > 0) {
        // 预加载组件
        await Promise.all(
          route.matched.map(record => {
            if (typeof record.components?.default === 'function') {
              return record.components.default()
            }
            return Promise.resolve()
          })
        )
      }
    } catch (error) {
      console.warn('路由预加载失败:', error)
    }
  }
}

/**
 * Vue组合式API的路由助手
 */
export function useRouteHelper() {
  return {
    navigateTo: RouteHelper.navigateTo.bind(RouteHelper),
    clearCache: RouteHelper.clearRouteCache.bind(RouteHelper),
    scrollToTop: RouteHelper.scrollToTop.bind(RouteHelper),
    isCurrentPath: RouteHelper.isCurrentPath.bind(RouteHelper),
    refreshRoute: RouteHelper.refreshCurrentRoute.bind(RouteHelper),
    preloadRoute: RouteHelper.preloadRoute.bind(RouteHelper)
  }
} 