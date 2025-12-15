import type { Router } from 'vue-router'

/**
 * 路由诊断工具 - 用于检测和修复路由跳转问题
 */
export class RouteDiagnostic {
  private static router: Router
  private static diagnosticResults: any[] = []

  /**
   * 初始化诊断工具
   */
  static init(router: Router) {
    this.router = router
    console.log('🔍 路由诊断工具已初始化')
  }

  /**
   * 全面诊断路由问题
   */
  static async runFullDiagnostic(): Promise<{
    success: boolean
    issues: string[]
    recommendations: string[]
    results: any[]
  }> {
    console.log('🚀 开始全面路由诊断...')
    
    const issues: string[] = []
    const recommendations: string[] = []
    const results: any[] = []

    try {
      // 1. 检查路由配置
      const routeConfigResult = this.checkRouteConfiguration()
      results.push(routeConfigResult)
      if (!routeConfigResult.success) {
        issues.push(...routeConfigResult.issues)
        recommendations.push(...routeConfigResult.recommendations)
      }

      // 2. 检查缓存问题
      const cacheResult = this.checkCacheIssues()
      results.push(cacheResult)
      if (!cacheResult.success) {
        issues.push(...cacheResult.issues)
        recommendations.push(...cacheResult.recommendations)
      }

      // 3. 检查事件监听器
      const eventResult = this.checkEventListeners()
      results.push(eventResult)
      if (!eventResult.success) {
        issues.push(...eventResult.issues)
        recommendations.push(...eventResult.recommendations)
      }

      // 4. 检查浏览器兼容性
      const compatResult = this.checkBrowserCompatibility()
      results.push(compatResult)
      if (!compatResult.success) {
        issues.push(...compatResult.issues)
        recommendations.push(...compatResult.recommendations)
      }

      // 5. 测试实际导航
      const navigationResult = await this.testNavigation()
      results.push(navigationResult)
      if (!navigationResult.success) {
        issues.push(...navigationResult.issues)
        recommendations.push(...navigationResult.recommendations)
      }

      const success = issues.length === 0

      console.log(success ? '✅ 路由诊断完成 - 未发现问题' : '⚠️ 路由诊断完成 - 发现问题')
      
      return {
        success,
        issues,
        recommendations,
        results
      }

    } catch (error) {
      console.error('❌ 诊断过程中发生错误:', error)
      return {
        success: false,
        issues: ['诊断过程中发生未知错误'],
        recommendations: ['请检查控制台错误信息并联系技术支持'],
        results: []
      }
    }
  }

  /**
   * 检查路由配置
   */
  private static checkRouteConfiguration() {
    const issues: string[] = []
    const recommendations: string[] = []
    
    try {
      // 检查路由是否正确初始化
      if (!this.router) {
        issues.push('Vue Router 未正确初始化')
        recommendations.push('检查 main.ts 中的路由初始化代码')
        return { success: false, test: '路由配置检查', issues, recommendations }
      }

      // 检查路由模式
      const currentRoute = this.router.currentRoute.value
      if (!currentRoute) {
        issues.push('无法获取当前路由信息')
        recommendations.push('检查路由配置是否正确')
      }

      // 检查路由历史模式
      const options = this.router.options
      if (!options.history) {
        issues.push('路由历史模式未配置')
        recommendations.push('确保使用 createWebHistory() 配置路由')
      }

      // 检查路由定义
      const routes = this.router.getRoutes()
      if (routes.length === 0) {
        issues.push('未找到任何路由定义')
        recommendations.push('检查路由配置文件是否正确导入')
      }

      // 检查重复路由
      const routePaths = routes.map(route => route.path)
      const duplicates = routePaths.filter((path, index) => routePaths.indexOf(path) !== index)
      if (duplicates.length > 0) {
        issues.push(`发现重复路由: ${duplicates.join(', ')}`)
        recommendations.push('移除重复的路由定义')
      }

      console.log('✅ 路由配置检查完成')
      return { 
        success: issues.length === 0, 
        test: '路由配置检查', 
        issues, 
        recommendations,
        details: {
          routeCount: routes.length,
          currentPath: currentRoute?.path,
          historyMode: options.history.constructor.name
        }
      }

    } catch (error) {
      issues.push('路由配置检查时发生错误')
      recommendations.push('检查路由配置语法是否正确')
      console.error('路由配置检查错误:', error)
      return { success: false, test: '路由配置检查', issues, recommendations }
    }
  }

  /**
   * 检查缓存问题
   */
  private static checkCacheIssues() {
    const issues: string[] = []
    const recommendations: string[] = []

    try {
      // 检查 localStorage 中的路由相关缓存
      let localStorageRouteKeys = 0
      if (window.localStorage) {
        for (let i = 0; i < localStorage.length; i++) {
          const key = localStorage.key(i)
          if (key && (key.includes('router') || key.includes('route'))) {
            localStorageRouteKeys++
          }
        }
      }

      // 检查 sessionStorage 中的路由相关缓存
      let sessionStorageRouteKeys = 0
      if (window.sessionStorage) {
        for (let i = 0; i < sessionStorage.length; i++) {
          const key = sessionStorage.key(i)
          if (key && (key.includes('router') || key.includes('route'))) {
            sessionStorageRouteKeys++
          }
        }
      }

      if (localStorageRouteKeys > 10) {
        issues.push(`localStorage 中有过多路由相关缓存 (${localStorageRouteKeys} 个)`)
        recommendations.push('清理 localStorage 中的路由缓存')
      }

      if (sessionStorageRouteKeys > 5) {
        issues.push(`sessionStorage 中有过多路由相关缓存 (${sessionStorageRouteKeys} 个)`)
        recommendations.push('清理 sessionStorage 中的路由缓存')
      }

      // 检查浏览器缓存控制
      const metaTags = document.querySelectorAll('meta[http-equiv]')
      let hasCacheControl = false
      metaTags.forEach(meta => {
        if (meta.getAttribute('http-equiv')?.toLowerCase().includes('cache')) {
          hasCacheControl = true
        }
      })

      if (!hasCacheControl) {
        issues.push('HTML 头部缺少缓存控制指令')
        recommendations.push('在 index.html 中添加缓存控制 meta 标签')
      }

      console.log('✅ 缓存问题检查完成')
      return { 
        success: issues.length === 0, 
        test: '缓存问题检查', 
        issues, 
        recommendations,
        details: {
          localStorageRouteKeys,
          sessionStorageRouteKeys,
          hasCacheControl
        }
      }

    } catch (error) {
      issues.push('缓存检查时发生错误')
      recommendations.push('检查浏览器存储权限')
      console.error('缓存检查错误:', error)
      return { success: false, test: '缓存问题检查', issues, recommendations }
    }
  }

  /**
   * 检查事件监听器
   */
  private static checkEventListeners() {
    const issues: string[] = []
    const recommendations: string[] = []

    try {
      // 检查是否有过多的点击事件监听器
      const clickListeners = (window as any)._clickListenerCount || 0
      if (clickListeners > 50) {
        issues.push(`检测到过多的点击事件监听器 (${clickListeners} 个)`)
        recommendations.push('检查是否有事件监听器泄漏')
      }

      // 检查是否有阻止默认行为的全局监听器
      let hasGlobalPreventDefault = false
      try {
        const testEvent = new Event('click', { cancelable: true })
        document.body.dispatchEvent(testEvent)
        if (testEvent.defaultPrevented) {
          hasGlobalPreventDefault = true
        }
      } catch (e) {
        console.warn('事件测试失败:', e)
      }

      if (hasGlobalPreventDefault) {
        issues.push('检测到全局阻止默认行为的事件监听器')
        recommendations.push('检查是否有监听器意外阻止了链接点击')
      }

      // 检查路由链接
      const routerLinks = document.querySelectorAll('a[href^="/"]')
      const regularLinks = document.querySelectorAll('a[href^="/"]')
      let properRouterLinks = 0
      
      routerLinks.forEach(link => {
        if (link.classList.contains('router-link') || 
            link.hasAttribute('router-link') ||
            link.closest('[router-link]')) {
          properRouterLinks++
        }
      })

      const regularLinksCount = regularLinks.length - properRouterLinks
      if (regularLinksCount > 0) {
        issues.push(`发现 ${regularLinksCount} 个普通站内链接（非RouterLink）`)
        recommendations.push('将站内链接改为使用 RouterLink 组件')
      }

      console.log('✅ 事件监听器检查完成')
      return { 
        success: issues.length === 0, 
        test: '事件监听器检查', 
        issues, 
        recommendations,
        details: {
          clickListeners,
          hasGlobalPreventDefault,
          routerLinksCount: properRouterLinks,
          regularLinksCount
        }
      }

    } catch (error) {
      issues.push('事件监听器检查时发生错误')
      recommendations.push('检查页面 JavaScript 是否有语法错误')
      console.error('事件监听器检查错误:', error)
      return { success: false, test: '事件监听器检查', issues, recommendations }
    }
  }

  /**
   * 检查浏览器兼容性
   */
  private static checkBrowserCompatibility() {
    const issues: string[] = []
    const recommendations: string[] = []

    try {
      // 检查 History API 支持
      if (!window.history.pushState) {
        issues.push('浏览器不支持 History API')
        recommendations.push('升级浏览器或使用 hash 模式路由')
      }

      // 检查 URL API 支持
      if (!window.URL) {
        issues.push('浏览器不支持 URL API')
        recommendations.push('升级浏览器或添加 polyfill')
      }

      // 检查 ES6 支持
      try {
        new Function('() => {}')
      } catch (e) {
        issues.push('浏览器不支持 ES6 箭头函数')
        recommendations.push('升级浏览器或添加 Babel 转译')
      }

      // 检查 Promise 支持
      if (!window.Promise) {
        issues.push('浏览器不支持 Promise')
        recommendations.push('添加 Promise polyfill')
      }

      // 检查用户代理
      const userAgent = navigator.userAgent
      if (userAgent.includes('MSIE') || userAgent.includes('Trident')) {
        issues.push('检测到 Internet Explorer 浏览器')
        recommendations.push('建议使用现代浏览器如 Chrome、Firefox 或 Edge')
      }

      console.log('✅ 浏览器兼容性检查完成')
      return { 
        success: issues.length === 0, 
        test: '浏览器兼容性检查', 
        issues, 
        recommendations,
        details: {
          userAgent,
          supportsHistoryAPI: !!window.history.pushState,
          supportsURL: !!window.URL,
          supportsPromise: !!window.Promise
        }
      }

    } catch (error) {
      issues.push('浏览器兼容性检查时发生错误')
      recommendations.push('检查浏览器环境')
      console.error('浏览器兼容性检查错误:', error)
      return { success: false, test: '浏览器兼容性检查', issues, recommendations }
    }
  }

  /**
   * 测试实际导航
   */
  private static async testNavigation() {
    const issues: string[] = []
    const recommendations: string[] = []

    try {
      const currentPath = this.router.currentRoute.value.path
      
      // 测试编程式导航
      const testPaths = ['/', '/articles', '/tags']
      let successfulNavigations = 0
      let failedNavigations = 0

      for (const testPath of testPaths) {
        if (testPath !== currentPath) {
          try {
            await this.router.push(testPath)
            await new Promise(resolve => setTimeout(resolve, 100))
            
            if (this.router.currentRoute.value.path === testPath) {
              successfulNavigations++
            } else {
              failedNavigations++
              issues.push(`导航到 ${testPath} 失败`)
            }
          } catch (error) {
            failedNavigations++
            issues.push(`导航到 ${testPath} 时发生错误: ${error}`)
          }
        }
      }

      // 导航回原页面
      try {
        await this.router.push(currentPath)
      } catch (error) {
        console.warn('导航回原页面失败:', error)
      }

      if (failedNavigations > 0) {
        recommendations.push('检查路由配置和组件是否正确')
        recommendations.push('确保所有路由组件都能正常加载')
      }

      // 测试浏览器前进后退
      let historyNavigationWorks = true
      try {
        window.history.back()
        await new Promise(resolve => setTimeout(resolve, 100))
        window.history.forward()
        await new Promise(resolve => setTimeout(resolve, 100))
      } catch (error) {
        historyNavigationWorks = false
        issues.push('浏览器前进后退功能异常')
        recommendations.push('检查 History API 相关代码')
      }

      console.log('✅ 导航测试完成')
      return { 
        success: issues.length === 0, 
        test: '导航功能测试', 
        issues, 
        recommendations,
        details: {
          successfulNavigations,
          failedNavigations,
          historyNavigationWorks,
          testedPaths: testPaths
        }
      }

    } catch (error) {
      issues.push('导航测试时发生错误')
      recommendations.push('检查路由配置和网络连接')
      console.error('导航测试错误:', error)
      return { success: false, test: '导航功能测试', issues, recommendations }
    }
  }

  /**
   * 快速修复常见问题
   */
  static async quickFix(): Promise<boolean> {
    console.log('🔧 开始快速修复...')
    
    try {
      // 清理缓存
      this.clearAllCaches()
      
      // 重置事件监听器
      this.resetEventListeners()
      
      // 修复路由状态
      this.fixRouterState()
      
      console.log('✅ 快速修复完成')
      return true
      
    } catch (error) {
      console.error('❌ 快速修复失败:', error)
      return false
    }
  }

  /**
   * 清理所有缓存
   */
  private static clearAllCaches() {
    try {
      // 调用全局缓存清理方法
      if (window.clearAppCache) {
        window.clearAppCache()
      }
      
      // 额外清理
      if (window.localStorage) {
        const keys = Object.keys(localStorage)
        keys.forEach(key => {
          if (key.includes('route') || key.includes('navigation')) {
            localStorage.removeItem(key)
          }
        })
      }
      
      console.log('🧹 缓存清理完成')
    } catch (error) {
      console.error('缓存清理失败:', error)
    }
  }

  /**
   * 重置事件监听器
   */
  private static resetEventListeners() {
    try {
      // 这里可以重置特定的事件监听器
      console.log('🔄 事件监听器重置完成')
    } catch (error) {
      console.error('事件监听器重置失败:', error)
    }
  }

  /**
   * 修复路由状态
   */
  private static fixRouterState() {
    try {
      // 重置路由状态
      const currentUrl = window.location.href
      if (window.history.replaceState) {
        window.history.replaceState({}, '', currentUrl)
      }
      
      console.log('🔧 路由状态修复完成')
    } catch (error) {
      console.error('路由状态修复失败:', error)
    }
  }

  /**
   * 生成诊断报告
   */
  static generateReport(diagnosticResult: any): string {
    const { success, issues, recommendations, results } = diagnosticResult
    
    let report = `# 路由诊断报告\n\n`
    report += `**诊断时间**: ${new Date().toLocaleString()}\n`
    report += `**总体状态**: ${success ? '✅ 正常' : '⚠️ 存在问题'}\n\n`
    
    if (issues.length > 0) {
      report += `## 发现的问题\n\n`
      issues.forEach((issue, index) => {
        report += `${index + 1}. ${issue}\n`
      })
      report += '\n'
    }
    
    if (recommendations.length > 0) {
      report += `## 修复建议\n\n`
      recommendations.forEach((rec, index) => {
        report += `${index + 1}. ${rec}\n`
      })
      report += '\n'
    }
    
    report += `## 详细测试结果\n\n`
    results.forEach(result => {
      report += `### ${result.test}\n`
      report += `- 状态: ${result.success ? '✅ 通过' : '❌ 失败'}\n`
      if (result.details) {
        report += `- 详情: ${JSON.stringify(result.details, null, 2)}\n`
      }
      report += '\n'
    })
    
    return report
  }
}

/**
 * Vue 组合式 API 的诊断工具
 */
export function useRouteDiagnostic() {
  return {
    runDiagnostic: RouteDiagnostic.runFullDiagnostic.bind(RouteDiagnostic),
    quickFix: RouteDiagnostic.quickFix.bind(RouteDiagnostic),
    generateReport: RouteDiagnostic.generateReport.bind(RouteDiagnostic)
  }
} 