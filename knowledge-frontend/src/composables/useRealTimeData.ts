import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useCategoriesStore } from '../stores/categories'
import { useArticlesStore } from '../stores/articles'
import { useTagsStore } from '../stores/tags'

/**
 * 实时数据管理组合函数
 * 用于首页的动态数据获取和自动刷新
 */
export function useRealTimeData() {
  const categoriesStore = useCategoriesStore()
  const articlesStore = useArticlesStore()
  const tagsStore = useTagsStore()

  // 状态管理
  const isInitialized = ref(false)
  const isLoading = ref(false)
  const lastRefreshTime = ref<Date | null>(null)
  const refreshInterval = ref<number | null>(null)
  const autoRefreshEnabled = ref(true)

  // 实时统计数据
  const realtimeStats = computed(() => ({
    totalArticles: categoriesStore.totalArticles || articlesStore.articles.length,
    totalViews: formatNumber(categoriesStore.totalViews || articlesStore.articles.reduce((sum, article) => sum + (article.views || 0), 0)),
    totalTags: tagsStore.tags.length,
    totalCategories: categoriesStore.categoryStats.length,
    averageProgress: categoriesStore.averageProgress,
    lastUpdated: lastRefreshTime.value?.toLocaleTimeString() || '未知'
  }))

  // 知识分类数据（动态）
  const knowledgeCategories = computed(() => categoriesStore.categoryStats)

  // 数据是否需要刷新
  const needsRefresh = computed(() => {
    return categoriesStore.isDataStale || !isInitialized.value
  })

  // 加载状态
  const loading = computed(() => {
    return isLoading.value || categoriesStore.statsLoading || articlesStore.loading || tagsStore.loading
  })

  // 初始化所有数据
  const initializeAllData = async () => {
    if (isInitialized.value && !needsRefresh.value) return

    isLoading.value = true
    
    try {
      // 并行加载所有数据
      await Promise.all([
        categoriesStore.initializeData(),
        categoriesStore.loadCategoryStats(),
        articlesStore.initializeData(),
        tagsStore.initializeData()
      ])

      isInitialized.value = true
      lastRefreshTime.value = new Date()
      
      console.log('📊 实时数据初始化完成:', {
        categories: categoriesStore.categoryStats.length,
        articles: articlesStore.articles.length,
        tags: tagsStore.tags.length,
        time: lastRefreshTime.value.toLocaleString()
      })
      
    } catch (error) {
      console.error('❌ 数据初始化失败:', error)
    } finally {
      isLoading.value = false
    }
  }

  // 刷新数据
  const refreshAllData = async () => {
    if (loading.value) return

    console.log('🔄 开始刷新实时数据...')
    isLoading.value = true
    
    try {
      // 并行刷新所有数据
      await Promise.all([
        categoriesStore.refreshStats(),
        articlesStore.initializeData(),
        tagsStore.initializeData()
      ])

      lastRefreshTime.value = new Date()
      
      console.log('✅ 实时数据刷新完成:', {
        categories: categoriesStore.categoryStats.length,
        articles: articlesStore.articles.length,
        tags: tagsStore.tags.length,
        time: lastRefreshTime.value.toLocaleString()
      })
      
    } catch (error) {
      console.error('❌ 数据刷新失败:', error)
    } finally {
      isLoading.value = false
    }
  }

  // 启动自动刷新
  const startAutoRefresh = (intervalMs: number = 300000) => { // 默认5分钟
    stopAutoRefresh() // 先停止已有的定时器
    
    if (autoRefreshEnabled.value) {
      refreshInterval.value = setInterval(() => {
        if (!loading.value && autoRefreshEnabled.value) {
          refreshAllData()
        }
      }, intervalMs)
      
      console.log(`⏰ 自动刷新已启动，间隔: ${intervalMs / 1000}秒`)
    }
  }

  // 停止自动刷新
  const stopAutoRefresh = () => {
    if (refreshInterval.value) {
      clearInterval(refreshInterval.value)
      refreshInterval.value = null
      console.log('⏹️ 自动刷新已停止')
    }
  }

  // 切换自动刷新
  const toggleAutoRefresh = () => {
    autoRefreshEnabled.value = !autoRefreshEnabled.value
    
    if (autoRefreshEnabled.value) {
      startAutoRefresh()
    } else {
      stopAutoRefresh()
    }
  }

  // 手动触发数据更新
  const triggerDataUpdate = async () => {
    await refreshAllData()
  }

  // 模拟数据变化（用于演示）
  const simulateDataChange = () => {
    // 这里可以模拟一些数据变化，比如增加阅读量、新增文章等
    console.log('🎲 模拟数据变化...')
    
    // 模拟增加阅读量
    articlesStore.articles.forEach(article => {
      if (Math.random() > 0.7) { // 30%的概率增加阅读量
        article.views = (article.views || 0) + Math.floor(Math.random() * 10) + 1
      }
    })
    
    lastRefreshTime.value = new Date()
  }

  // 获取特定分类的详细信息
  const getCategoryDetails = (categoryId: number) => {
    return categoriesStore.categoryStats.find(cat => cat.id === categoryId)
  }

  // 获取热门分类（按文章数量排序）
  const getPopularCategories = (limit: number = 3) => {
    return [...categoriesStore.categoryStats]
      .sort((a, b) => b.articleCount - a.articleCount)
      .slice(0, limit)
  }

  // 获取活跃分类（按进度排序）
  const getActiveCategories = (limit: number = 3) => {
    return [...categoriesStore.categoryStats]
      .sort((a, b) => b.progress - a.progress)
      .slice(0, limit)
  }

  // 工具函数：格式化数字
  const formatNumber = (num: number): string => {
    if (num >= 1000000) {
      return (num / 1000000).toFixed(1) + 'M'
    } else if (num >= 1000) {
      return (num / 1000).toFixed(1) + 'K'
    }
    return num.toString()
  }

  // 工具函数：格式化时间
  const formatTimeAgo = (date: Date): string => {
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    const minutes = Math.floor(diff / 60000)
    
    if (minutes < 1) return '刚刚'
    if (minutes < 60) return `${minutes}分钟前`
    if (minutes < 1440) return `${Math.floor(minutes / 60)}小时前`
    return `${Math.floor(minutes / 1440)}天前`
  }

  // 生命周期管理
  onMounted(() => {
    initializeAllData()
    startAutoRefresh()
  })

  onUnmounted(() => {
    stopAutoRefresh()
  })

  return {
    // 状态
    isInitialized,
    isLoading: loading,
    lastRefreshTime,
    autoRefreshEnabled,
    needsRefresh,
    
    // 数据
    realtimeStats,
    knowledgeCategories,
    
    // 方法
    initializeAllData,
    refreshAllData,
    startAutoRefresh,
    stopAutoRefresh,
    toggleAutoRefresh,
    triggerDataUpdate,
    simulateDataChange,
    getCategoryDetails,
    getPopularCategories,
    getActiveCategories,
    formatNumber,
    formatTimeAgo
  }
} 