import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { articleApi, type Article, type ApiResponse, type PageData, type CreateArticleRequest, type UpdateArticleRequest } from '@/api/articles'

// 事件发射器用于实时更新活动动态
interface ActivityEvent {
  type: 'article' | 'update' | 'achievement'
  content: string
  articleId?: number
  metadata?: {
    title?: string
    category?: string
    status?: number
  }
}

// 简单的事件总线
class EventBus {
  private listeners: { [key: string]: Function[] } = {}

  on(event: string, callback: Function) {
    if (!this.listeners[event]) {
      this.listeners[event] = []
    }
    this.listeners[event].push(callback)
  }

  emit(event: string, data: any) {
    if (this.listeners[event]) {
      this.listeners[event].forEach(callback => callback(data))
    }
  }

  off(event: string, callback: Function) {
    if (this.listeners[event]) {
      this.listeners[event] = this.listeners[event].filter(cb => cb !== callback)
    }
  }
}

export const activityEventBus = new EventBus()

export const useArticlesStore = defineStore('articles', () => {
  const articles = ref<Article[]>([])
  const loading = ref(false)
  const searchQuery = ref('')
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(0)

  // 发射活动事件
  const emitActivityEvent = (event: ActivityEvent) => {
    activityEventBus.emit('activity', event)
  }

  // 初始化数据
  const initializeData = async () => {
    try {
      await fetchArticles()
    } catch (error) {
      console.error('初始化数据失败:', error)
      throw error // 重新抛出错误，让组件能够捕获
    }
  }

  // 计算属性
  const filteredArticles = computed(() => {
    if (!searchQuery.value) return articles.value
    return articles.value.filter(article =>
      article.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      (article.summary || '').toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  })

  const articlesByYear = computed(() => {
    const grouped: { [year: string]: Article[] } = {}
    articles.value.forEach(article => {
      if (article.createdAt) {
        const year = new Date(article.createdAt).getFullYear().toString()
        if (!grouped[year]) grouped[year] = []
        grouped[year].push(article)
      }
    })
    // 对每一年内的文章按 updatedAt（或 createdAt）倒序排列
    Object.keys(grouped).forEach(year => {
      grouped[year].sort((a, b) => {
        const aTime = new Date(a.updatedAt || a.createdAt).getTime()
        const bTime = new Date(b.updatedAt || b.createdAt).getTime()
        return bTime - aTime
      })
    })
    return grouped
  })

  const totalArticles = computed(() => total.value)

  // Actions - 查询操作
  const fetchArticles = async (page = 1, size = 10, status?: number) => {
    loading.value = true
    try {
      const response = await articleApi.getArticles(page, size, status)
      if (response.code === 200) {
        // 确保数据完整性
        const records = response.data.records || []
        articles.value = records.map(article => ({
          ...article,
          content: article.content || '', // 确保content字段存在
          summary: article.summary || '',
          title: article.title || '无标题',
          createdAt: article.createdAt || new Date().toISOString(),
          views: article.views || 0
        }))
        total.value = response.data.total || 0
        currentPage.value = response.data.current || page
        pageSize.value = response.data.size || size
      } else {
        throw new Error(response.message || '获取文章列表失败')
      }
    } catch (error) {
      console.error('Failed to fetch articles:', error)
      // 重置数据状态
      articles.value = []
      total.value = 0
      throw error
    } finally {
      loading.value = false
    }
  }

  const getArticleById = async (id: number) => {
    try {
      const response = await articleApi.getArticleById(id)
      if (response.code === 200) {
        return response.data
      }
    } catch (error) {
      console.error('Failed to fetch article:', error)
      throw error
    }
    return null
  }

  // Actions - 创建和更新操作
  const createArticle = async (articleData: CreateArticleRequest): Promise<Article> => {
    loading.value = true
    try {
      const response = await articleApi.createArticle(articleData)
      if (response.code === 200) {
        const newArticle = response.data
        // 将新文章添加到本地列表的前面
        articles.value.unshift(newArticle)
        total.value += 1

        // 发射创建文章活动事件
        emitActivityEvent({
          type: 'article',
          content: `创建了文章《${newArticle.title}》`,
          articleId: newArticle.id,
          metadata: {
            title: newArticle.title,
            category: newArticle.categoryName,
            status: newArticle.status
          }
        })

        return newArticle
      } else {
        throw new Error(response.message || '创建文章失败')
      }
    } catch (error) {
      console.error('Failed to create article:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateArticle = async (articleData: UpdateArticleRequest): Promise<Article> => {
    loading.value = true
    try {
      const response = await articleApi.updateArticle(articleData)
      if (response.code === 200) {
        const updatedArticle = response.data
        // 更新本地列表中的文章
        const index = articles.value.findIndex(a => a.id === updatedArticle.id)
        if (index !== -1) {
          articles.value[index] = updatedArticle
        }

        // 发射更新文章活动事件
        emitActivityEvent({
          type: 'update',
          content: `更新了文章《${updatedArticle.title}》`,
          articleId: updatedArticle.id,
          metadata: {
            title: updatedArticle.title,
            category: updatedArticle.categoryName,
            status: updatedArticle.status
          }
        })

        return updatedArticle
      } else {
        throw new Error(response.message || '更新文章失败')
      }
    } catch (error) {
      console.error('Failed to update article:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const deleteArticle = async (id: number): Promise<boolean> => {
    loading.value = true
    try {
      const response = await articleApi.deleteArticle(id)
      if (response.code === 200) {
        // 从本地列表中移除文章
        const index = articles.value.findIndex(a => a.id === id)
        if (index !== -1) {
          const deletedArticle = articles.value[index]
          articles.value.splice(index, 1)
          total.value -= 1

          // 发射删除文章活动事件
          emitActivityEvent({
            type: 'update',
            content: `删除了文章《${deletedArticle.title}》`,
            metadata: {
              title: deletedArticle.title
            }
          })
        }
        return true
      } else {
        throw new Error(response.message || '删除文章失败')
      }
    } catch (error) {
      console.error('Failed to delete article:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const publishArticle = async (id: number): Promise<boolean> => {
    try {
      const response = await articleApi.publishArticle(id)
      if (response.code === 200) {
        // 更新本地文章状态
        const article = articles.value.find(a => a.id === id)
        if (article) {
          article.status = 1 // 假设1表示已发布
          article.publishTime = new Date().toISOString()

          // 发射发布文章活动事件
          emitActivityEvent({
            type: 'article',
            content: `发布了文章《${article.title}》`,
            articleId: article.id,
            metadata: {
              title: article.title,
              category: article.categoryName,
              status: article.status
            }
          })

          // 检查成就
          const publishedCount = articles.value.filter(a => a.status === 1).length
          const milestones = [1, 5, 10, 20, 50, 100]
          if (milestones.includes(publishedCount)) {
            emitActivityEvent({
              type: 'achievement',
              content: `解锁成就：发布了第 ${publishedCount} 篇文章！`,
              metadata: {
                title: `发布 ${publishedCount} 篇文章`
              }
            })
          }
        }
        return true
      } else {
        throw new Error(response.message || '发布文章失败')
      }
    } catch (error) {
      console.error('Failed to publish article:', error)
      throw error
    }
  }

  const draftArticle = async (id: number): Promise<boolean> => {
    try {
      const response = await articleApi.draftArticle(id)
      if (response.code === 200) {
        // 更新本地文章状态
        const article = articles.value.find(a => a.id === id)
        if (article) {
          article.status = 0 // 假设0表示草稿

          // 发射草稿活动事件
          emitActivityEvent({
            type: 'update',
            content: `将文章《${article.title}》设为草稿`,
            articleId: article.id,
            metadata: {
              title: article.title,
              category: article.categoryName,
              status: article.status
            }
          })
        }
        return true
      } else {
        throw new Error(response.message || '设为草稿失败')
      }
    } catch (error) {
      console.error('Failed to draft article:', error)
      throw error
    }
  }

  const searchArticles = async (query: string, page = 1, size = 10) => {
    searchQuery.value = query
    loading.value = true
    try {
      const response = await articleApi.searchArticles(query, page, size)
      if (response.code === 200) {
        articles.value = response.data.records
        total.value = response.data.total
      }
    } catch (error) {
      console.error('Failed to search articles:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const getPopularArticles = async (limit = 10) => {
    try {
      const response = await articleApi.getPopularArticles(limit)
      if (response.code === 200) {
        return response.data
      }
    } catch (error) {
      console.error('Failed to fetch popular articles:', error)
      throw error
    }
    return []
  }

  const getLatestArticles = async (limit = 10) => {
    try {
      const response = await articleApi.getLatestArticles(limit)
      if (response.code === 200) {
        return response.data
      }
    } catch (error) {
      console.error('Failed to fetch latest articles:', error)
      throw error
    }
    return []
  }

  return {
    // 状态
    articles,
    loading,
    searchQuery,
    currentPage,
    pageSize,
    total,
    // 计算属性
    filteredArticles,
    articlesByYear,
    totalArticles,
    // 查询方法
    fetchArticles,
    getArticleById,
    searchArticles,
    getPopularArticles,
    getLatestArticles,
    initializeData,
    // 写操作方法
    createArticle,
    updateArticle,
    deleteArticle,
    publishArticle,
    draftArticle,
    // 事件相关
    emitActivityEvent
  }
}) 