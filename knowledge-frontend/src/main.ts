import './assets/main.css'
import 'highlight.js/styles/github.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { RouteHelper } from './utils/routeHelper'
import { RouteFixHelper } from './utils/routeFixHelper'
import { RouteDiagnostic } from './utils/routeDiagnostic'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// 初始化路由助手
RouteHelper.init(router)

// 初始化路由修复助手 - 解决导航需要刷新的问题
RouteFixHelper.init(router)

// 初始化路由诊断工具
RouteDiagnostic.init(router)

// 异步初始化认证状态
async function initializeApp() {
  try {
    const { useAuthStore } = await import('./stores/auth')
    const authStore = useAuthStore()
    await authStore.initAuth()
  } catch (error) {
    console.error('应用初始化失败:', error)
  } finally {
    app.mount('#app')
  }
}

initializeApp()
