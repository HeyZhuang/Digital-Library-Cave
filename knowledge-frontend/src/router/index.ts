import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import HomeView from '../views/HomeView.vue'

// 路由懒加载优化 - 使用命名chunk进行更好的代码分割
const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login',
      name: 'login',
      component: () => import(/* webpackChunkName: "auth" */ '../views/LoginView.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/articles',
      name: 'articles',
      component: () => import(/* webpackChunkName: "articles" */ '../views/ArticleListView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/article/:id',
      name: 'article-detail',
      component: () => import(/* webpackChunkName: "articles" */ '../views/ArticleDetailView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/tags',
      name: 'tags',
      component: () => import(/* webpackChunkName: "management" */ '../views/TagView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/search',
      name: 'search',
      component: () => import(/* webpackChunkName: "search" */ '../views/SearchView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/article/edit/:id?',
      name: 'article-edit',
      component: () => import(/* webpackChunkName: "editor" */ '../views/ArticleEditView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/workshop',
      name: 'creative-workshop',
      component: () => import(/* webpackChunkName: "workshop" */ '../views/CreativeWorkshopView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/workshop/edit/:id?',
      name: 'workshop-edit',
      component: () => import(/* webpackChunkName: "workshop" */ '../views/CreativeWorkshopView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/test-router',
      name: 'test-router',
      component: () => import(/* webpackChunkName: "test" */ '../views/TestRouterView.vue')
    },
    {
      path: '/test-cors',
      name: 'test-cors',
      component: () => import(/* webpackChunkName: "test" */ '../views/TestCorsView.vue')
    },
    {
      path: '/accessibility-demo',
      name: 'accessibility-demo',
      component: () => import(/* webpackChunkName: "demo" */ '../views/AccessibilityDemoView.vue')
    },
    {
      path: '/categories',
      name: 'categories',
      component: () => import(/* webpackChunkName: "management" */ '../views/CategoryManagementView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import(/* webpackChunkName: "user" */ '../views/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/avatar-test',
      name: 'avatar-test',
      component: () => import(/* webpackChunkName: "user" */ '../views/AvatarTestView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/settings',
      name: 'settings',
      component: () => import(/* webpackChunkName: "user" */ '../views/SettingsView.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

// 路由守卫优化
router.beforeEach(async (to, from, next) => {
  try {
    const authStore = useAuthStore()
    
    // 初始化认证状态（如果还未初始化）
    if (!authStore.isAuthenticated && localStorage.getItem('token')) {
      await authStore.initAuth()
    }

    // 检查需要认证的路由
    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
      next({ name: 'login', query: { redirect: to.fullPath } })
      return
    }

    // 检查访客专用路由（如登录页）
    if (to.meta.requiresGuest && authStore.isAuthenticated) {
      next({ name: 'home' })
      return
    }

    // 预加载相关路由组件
    if (to.name === 'articles' && from.name !== 'article-detail') {
      // 预加载文章详情页
      import(/* webpackChunkName: "articles" */ '../views/ArticleDetailView.vue')
    }

    next()
  } catch (error) {
    console.error('路由守卫错误:', error)
    // 发生错误时允许导航继续
    next()
  }
})

// 添加导航错误处理
router.onError((error) => {
  console.error('路由错误:', error)
})

// 导航后滚动行为优化
router.afterEach((to, from) => {
  // 清除可能的缓存问题
  if (window.sessionStorage) {
    window.sessionStorage.removeItem('vue-router-cache')
  }
  
  // 平滑滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
})

export default router 