import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as categoriesApi from '../api/categories'

export interface Category {
  id: number
  name: string
  description?: string
  parentId?: number
  sortOrder: number
  createdAt: string
  updatedAt: string
  articleCount?: number
}

export const useCategoriesStore = defineStore('categories', () => {
  const categories = ref<Category[]>([])
  const categoryStats = ref<categoriesApi.CategoryStats[]>([])
  const loading = ref(false)
  const statsLoading = ref(false)
  const error = ref<string | null>(null)
  const lastUpdated = ref<Date | null>(null)

  const initializeData = async () => {
    if (categories.value.length > 0) return
    
    loading.value = true
    error.value = null
    
    try {
      const data = await categoriesApi.getCategories()
      categories.value = data
    } catch (err: any) {
      console.error('加载分类失败:', err)
      error.value = err.message || '加载分类失败'
      // 使用默认分类数据
      categories.value = [
        { id: 1, name: '技术分享', description: '技术相关的文章', sortOrder: 1, createdAt: '', updatedAt: '' },
        { id: 2, name: '生活随笔', description: '生活感悟和随笔', sortOrder: 2, createdAt: '', updatedAt: '' },
        { id: 3, name: '学习笔记', description: '学习过程中的笔记和总结', sortOrder: 3, createdAt: '', updatedAt: '' },
        { id: 4, name: '疾病预防', description: '各种疾病的预防知识和方法', sortOrder: 4, createdAt: '', updatedAt: '' },
        { id: 5, name: '健康饮食', description: '营养搭配和健康饮食指南', sortOrder: 5, createdAt: '', updatedAt: '' },
        { id: 6, name: '运动健身', description: '运动锻炼和健身相关知识', sortOrder: 6, createdAt: '', updatedAt: '' },
        { id: 7, name: '心理健康', description: '心理健康和情绪管理', sortOrder: 7, createdAt: '', updatedAt: '' },
        { id: 8, name: '儿童健康', description: '儿童健康成长相关知识', sortOrder: 8, createdAt: '', updatedAt: '' }
      ]
    } finally {
      loading.value = false
    }
  }

  const refreshCategories = async () => {
    categories.value = []
    await initializeData()
  }

  const getCategoryById = (id: number): Category | undefined => {
    return categories.value.find(category => category.id === id)
  }

  const getCategoryName = (id: number): string => {
    const category = getCategoryById(id)
    return category?.name || '未分类'
  }

  const createCategory = async (categoryData: categoriesApi.CreateCategoryRequest): Promise<Category> => {
    try {
      const newCategory = await categoriesApi.createCategory(categoryData)
      categories.value.push(newCategory)
      return newCategory
    } catch (err) {
      console.error('创建分类失败:', err)
      throw err
    }
  }

  const updateCategory = async (categoryData: categoriesApi.UpdateCategoryRequest): Promise<Category> => {
    try {
      const updatedCategory = await categoriesApi.updateCategory(categoryData)
      const index = categories.value.findIndex(c => c.id === updatedCategory.id)
      if (index !== -1) {
        categories.value[index] = updatedCategory
      }
      return updatedCategory
    } catch (err) {
      console.error('更新分类失败:', err)
      throw err
    }
  }

  const deleteCategory = async (id: number): Promise<void> => {
    try {
      await categoriesApi.deleteCategory(id)
      categories.value = categories.value.filter(c => c.id !== id)
      // 删除后刷新统计数据
      await loadCategoryStats()
    } catch (err) {
      console.error('删除分类失败:', err)
      throw err
    }
  }

  // 加载分类统计数据
  const loadCategoryStats = async () => {
    statsLoading.value = true
    error.value = null
    
    try {
      const stats = await categoriesApi.getCategoryStats()
      categoryStats.value = stats
      lastUpdated.value = new Date()
    } catch (err: any) {
      console.error('加载分类统计失败:', err)
      error.value = err.message || '加载分类统计失败'
      
      // 使用默认统计数据
      categoryStats.value = [
        {
          id: 1,
          name: '前端框架',
          icon: '⚡',
          color: 'vue',
          articleCount: Math.floor(Math.random() * 20) + 10,
          progress: Math.floor(Math.random() * 30) + 70,
          topics: ['Vue 3', 'React', 'Angular', 'Svelte'],
          totalViews: Math.floor(Math.random() * 5000) + 1000,
          lastUpdated: new Date().toISOString()
        },
        {
          id: 2,
          name: '编程语言',
          icon: '💻',
          color: 'javascript',
          articleCount: Math.floor(Math.random() * 20) + 8,
          progress: Math.floor(Math.random() * 25) + 65,
          topics: ['JavaScript', 'TypeScript', 'Python', 'Java'],
          totalViews: Math.floor(Math.random() * 4000) + 800,
          lastUpdated: new Date().toISOString()
        },
        {
          id: 3,
          name: '后端技术',
          icon: '🚀',
          color: 'backend',
          articleCount: Math.floor(Math.random() * 15) + 5,
          progress: Math.floor(Math.random() * 35) + 40,
          topics: ['Node.js', 'Express', 'MySQL', 'Redis'],
          totalViews: Math.floor(Math.random() * 3000) + 600,
          lastUpdated: new Date().toISOString()
        },
        {
          id: 4,
          name: '云计算',
          icon: '☁️',
          color: 'cloud',
          articleCount: Math.floor(Math.random() * 12) + 3,
          progress: Math.floor(Math.random() * 40) + 30,
          topics: ['AWS', 'Docker', 'Kubernetes', '微服务'],
          totalViews: Math.floor(Math.random() * 2500) + 400,
          lastUpdated: new Date().toISOString()
        },
        {
          id: 5,
          name: '数据库',
          icon: '🗄️',
          color: 'database',
          articleCount: Math.floor(Math.random() * 10) + 4,
          progress: Math.floor(Math.random() * 30) + 50,
          topics: ['MySQL', 'MongoDB', 'PostgreSQL', 'Redis'],
          totalViews: Math.floor(Math.random() * 2000) + 300,
          lastUpdated: new Date().toISOString()
        },
        {
          id: 6,
          name: '人工智能',
          icon: '🤖',
          color: 'ai',
          articleCount: Math.floor(Math.random() * 8) + 2,
          progress: Math.floor(Math.random() * 20) + 25,
          topics: ['机器学习', '深度学习', 'TensorFlow', 'PyTorch'],
          totalViews: Math.floor(Math.random() * 1800) + 200,
          lastUpdated: new Date().toISOString()
        }
      ]
      lastUpdated.value = new Date()
    } finally {
      statsLoading.value = false
    }
  }

  // 刷新统计数据
  const refreshStats = async () => {
    await loadCategoryStats()
  }

  // 定期刷新数据
  const startAutoRefresh = (intervalMs: number = 300000) => { // 默认5分钟
    const interval = setInterval(() => {
      if (!statsLoading.value) {
        loadCategoryStats()
      }
    }, intervalMs)
    
    return () => clearInterval(interval)
  }

  // 计算属性
  const totalArticles = computed(() => {
    return categoryStats.value.reduce((sum, cat) => sum + cat.articleCount, 0)
  })

  const totalViews = computed(() => {
    return categoryStats.value.reduce((sum, cat) => sum + (cat.totalViews || 0), 0)
  })

  const averageProgress = computed(() => {
    if (categoryStats.value.length === 0) return 0
    const total = categoryStats.value.reduce((sum, cat) => sum + cat.progress, 0)
    return Math.round(total / categoryStats.value.length)
  })

  const isDataStale = computed(() => {
    if (!lastUpdated.value) return true
    const now = new Date()
    const diff = now.getTime() - lastUpdated.value.getTime()
    return diff > 600000 // 10分钟认为数据过期
  })

  return {
    categories,
    categoryStats,
    loading,
    statsLoading,
    error,
    lastUpdated,
    initializeData,
    refreshCategories,
    getCategoryById,
    getCategoryName,
    createCategory,
    updateCategory,
    deleteCategory,
    loadCategoryStats,
    refreshStats,
    startAutoRefresh,
    totalArticles,
    totalViews,
    averageProgress,
    isDataStale
  }
}) 