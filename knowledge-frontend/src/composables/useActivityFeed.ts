import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useArticlesStore, activityEventBus } from '../stores/articles'
import { useAuthStore } from '../stores/auth'
import type { Article } from '../api/articles'

export interface Activity {
  id: string
  type: 'article' | 'update' | 'achievement' | 'system' | 'comment' | 'like' | 'share' | 'follow' | 'login' | 'tag' | 'category'
  content: string
  date: string
  articleId?: number
  metadata?: {
    title?: string
    category?: string
    tags?: string[]
    status?: number
    targetUser?: string
    targetType?: string
  }
}

/**
 * 创作动态管理组合函数
 * 根据用户的文章数据实时生成创作动态
 */
export function useActivityFeed() {
  const articlesStore = useArticlesStore()
  const authStore = useAuthStore()
  
  const activities = ref<Activity[]>([])
  const realtimeActivities = ref<Activity[]>([])
  const isLoading = ref(false)

  // 格式化时间显示
  const formatTimeAgo = (dateString: string): string => {
    const date = new Date(dateString)
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    
    const minutes = Math.floor(diff / 60000)
    const hours = Math.floor(diff / 3600000)
    const days = Math.floor(diff / 86400000)
    const weeks = Math.floor(diff / 604800000)
    const months = Math.floor(diff / 2592000000)
    
    if (minutes < 1) return '刚刚'
    if (minutes < 60) return `${minutes} 分钟前`
    if (hours < 24) return `${hours} 小时前`
    if (days < 7) return `${days} 天前`
    if (weeks < 4) return `${weeks} 周前`
    if (months < 12) return `${months} 个月前`
    
    return date.toLocaleDateString('zh-CN')
  }

  // 从文章数据生成活动记录
  const generateActivitiesFromArticles = (articles: Article[]): Activity[] => {
    const generatedActivities: Activity[] = []
    
    // 按时间排序（最新的在前）
    const sortedArticles = [...articles].sort((a, b) => {
      const aTime = new Date(a.updatedAt || a.createdAt || '').getTime()
      const bTime = new Date(b.updatedAt || b.createdAt || '').getTime()
      return bTime - aTime
    })

    sortedArticles.forEach((article) => {
      const createdAt = article.createdAt
      const updatedAt = article.updatedAt
      const publishTime = article.publishTime
      
      // 创建活动记录 ID 生成器
      const generateId = (articleId: number, type: string, time: string) => 
        `${articleId}-${type}-${new Date(time).getTime()}`

      // 发布活动
      if (publishTime && article.status === 1) {
        generatedActivities.push({
          id: generateId(article.id!, 'publish', publishTime),
          type: 'article',
          content: `发布了文章《${article.title}》`,
          date: formatTimeAgo(publishTime),
          articleId: article.id,
          metadata: {
            title: article.title,
            category: article.categoryName,
            status: article.status
          }
        })
      }

      // 更新活动（如果更新时间与创建时间不同，且文章已发布）
      if (updatedAt && createdAt && updatedAt !== createdAt && article.status === 1) {
        const updateTimeDiff = new Date(updatedAt).getTime() - new Date(createdAt).getTime()
        // 只有当更新时间差大于5分钟时才记录更新活动
        if (updateTimeDiff > 300000) {
          generatedActivities.push({
            id: generateId(article.id!, 'update', updatedAt),
            type: 'update',
            content: `更新了文章《${article.title}》`,
            date: formatTimeAgo(updatedAt),
            articleId: article.id,
            metadata: {
              title: article.title,
              category: article.categoryName,
              status: article.status
            }
          })
        }
      }

      // 创建草稿活动（对于草稿状态的文章）
      if (createdAt && article.status === 0) {
        generatedActivities.push({
          id: generateId(article.id!, 'draft', createdAt),
          type: 'update',
          content: `创建了草稿《${article.title}》`,
          date: formatTimeAgo(createdAt),
          articleId: article.id,
          metadata: {
            title: article.title,
            category: article.categoryName,
            status: article.status
          }
        })
      }
    })

    return generatedActivities
  }

  // 生成成就活动
  const generateAchievementActivities = (): Activity[] => {
    const achievementActivities: Activity[] = []
    const totalArticles = articlesStore.articles.length
    const publishedArticles = articlesStore.articles.filter(a => a.status === 1).length
    
    // 文章数量成就
    const milestones = [1, 5, 10, 20, 50, 100]
    milestones.forEach(milestone => {
      if (publishedArticles >= milestone) {
        achievementActivities.push({
          id: `achievement-articles-${milestone}`,
          type: 'achievement',
          content: `解锁成就：发布了第 ${milestone} 篇文章！`,
          date: '已解锁',
          metadata: {
            title: `发布 ${milestone} 篇文章`
          }
        })
      }
    })

    // 总浏览量成就
    const totalViews = articlesStore.articles.reduce((sum, article) => sum + (article.views || 0), 0)
    const viewMilestones = [100, 500, 1000, 5000, 10000]
    viewMilestones.forEach(milestone => {
      if (totalViews >= milestone) {
        achievementActivities.push({
          id: `achievement-views-${milestone}`,
          type: 'achievement',
          content: `解锁成就：文章总浏览量达到 ${milestone.toLocaleString()}！`,
          date: '已解锁',
          metadata: {
            title: `${milestone.toLocaleString()} 浏览量`
          }
        })
      }
    })

    return achievementActivities
  }

  // 生成系统活动
  const generateSystemActivities = (): Activity[] => {
    const systemActivities: Activity[] = []
    
    // 加入系统活动
    if (authStore.userInfo?.createdAt) {
      systemActivities.push({
        id: 'system-join',
        type: 'system',
        content: '加入了知识库系统',
        date: formatTimeAgo(authStore.userInfo.createdAt),
        metadata: {
          title: '系统注册'
        }
      })
    }

    return systemActivities
  }

  // 生成互动活动
  const generateInteractionActivities = (): Activity[] => {
    const interactionActivities: Activity[] = []
    
    // 模拟评论活动
    const commentActivities = [
      {
        id: 'comment-1',
        type: 'comment' as const,
        content: '在《Vue.js组件通信》文章下发表了评论',
        date: '30分钟前',
        articleId: 1,
        metadata: {
          title: 'Vue.js组件通信',
          targetType: 'article'
        }
      },
      {
        id: 'comment-2', 
        type: 'comment' as const,
        content: '回复了李明轩的评论',
        date: '2小时前',
        metadata: {
          targetUser: '李明轩',
          targetType: 'comment'
        }
      }
    ]

    // 模拟点赞活动
    const likeActivities = [
      {
        id: 'like-1',
        type: 'like' as const,
        content: '点赞了《JavaScript异步编程》',
        date: '1小时前',
        articleId: 2,
        metadata: {
          title: 'JavaScript异步编程',
          targetType: 'article'
        }
      },
      {
        id: 'like-2',
        type: 'like' as const,
        content: '喜欢了王小美的评论',
        date: '3小时前',
        metadata: {
          targetUser: '王小美',
          targetType: 'comment'
        }
      }
    ]

    // 模拟分享活动
    const shareActivities = [
      {
        id: 'share-1',
        type: 'share' as const,
        content: '分享了《React Hooks实战》到微信',
        date: '4小时前',
        articleId: 3,
        metadata: {
          title: 'React Hooks实战',
          targetType: 'wechat'
        }
      }
    ]

    // 模拟关注活动
    const followActivities = [
      {
        id: 'follow-1',
        type: 'follow' as const,
        content: '关注了张技术',
        date: '昨天',
        metadata: {
          targetUser: '张技术',
          targetType: 'user'
        }
      }
    ]

    // 模拟登录活动
    const loginActivities = [
      {
        id: 'login-1',
        type: 'login' as const,
        content: '登录了系统',
        date: '刚刚',
        metadata: {
          title: '系统登录'
        }
      }
    ]

    // 模拟标签和分类活动
    const tagCategoryActivities = [
      {
        id: 'tag-1',
        type: 'tag' as const,
        content: '创建了新标签「微服务架构」',
        date: '2天前',
        metadata: {
          title: '微服务架构',
          targetType: 'tag'
        }
      },
      {
        id: 'category-1',
        type: 'category' as const,
        content: '创建了新分类「前端框架」',
        date: '3天前',
        metadata: {
          title: '前端框架',
          targetType: 'category'
        }
      }
    ]

    return [
      ...commentActivities,
      ...likeActivities, 
      ...shareActivities,
      ...followActivities,
      ...loginActivities,
      ...tagCategoryActivities
    ]
  }

  // 计算所有活动
  const allActivities = computed(() => {
    const articleActivities = generateActivitiesFromArticles(articlesStore.articles)
    const achievementActivities = generateAchievementActivities()
    const systemActivities = generateSystemActivities()
    const interactionActivities = generateInteractionActivities()
    
    // 合并所有活动和实时活动
    const combined = [...realtimeActivities.value, ...articleActivities, ...achievementActivities, ...systemActivities, ...interactionActivities]
    
    // 去重（基于ID）
    const uniqueActivities = combined.filter((activity, index, self) => 
      index === self.findIndex(a => a.id === activity.id)
    )
    
    // 按日期排序（最新的在前），但成就类型的放在后面
    return uniqueActivities.sort((a, b) => {
      // 成就类型的活动优先级较低
      if (a.type === 'achievement' && b.type !== 'achievement') return 1
      if (a.type !== 'achievement' && b.type === 'achievement') return -1
      
      // 对于非成就活动，按时间排序
      if (a.type !== 'achievement' && b.type !== 'achievement') {
        // 对于时间格式的处理
        const aTime = a.date === '已解锁' ? 0 : new Date().getTime() - parseTimeAgo(a.date)
        const bTime = b.date === '已解锁' ? 0 : new Date().getTime() - parseTimeAgo(b.date)
        return bTime - aTime
      }
      
      return 0
    }).slice(0, 20) // 限制显示最近20条活动
  })

  // 解析时间字符串到毫秒
  const parseTimeAgo = (timeAgo: string): number => {
    if (timeAgo === '刚刚') return 0
    
    const match = timeAgo.match(/(\d+)\s*(分钟|小时|天|周|个月)前/)
    if (!match) return 0
    
    const [, num, unit] = match
    const value = parseInt(num)
    
    switch (unit) {
      case '分钟': return value * 60000
      case '小时': return value * 3600000
      case '天': return value * 86400000
      case '周': return value * 604800000
      case '个月': return value * 2592000000
      default: return 0
    }
  }

  // 刷新活动数据
  const refreshActivities = async () => {
    isLoading.value = true
    try {
      // 确保文章数据是最新的
      if (articlesStore.articles.length === 0) {
        await articlesStore.fetchArticles()
      }
      
      // 更新活动列表（通过computed自动更新）
      activities.value = allActivities.value
    } catch (error) {
      console.error('刷新活动数据失败:', error)
    } finally {
      isLoading.value = false
    }
  }

  // 添加新活动（用于实时更新）
  const addActivity = (activity: Omit<Activity, 'id' | 'date'>) => {
    const newActivity: Activity = {
      ...activity,
      id: `${activity.type}-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      date: formatTimeAgo(new Date().toISOString())
    }
    
    realtimeActivities.value.unshift(newActivity)
    // 限制实时活动数量
    if (realtimeActivities.value.length > 20) {
      realtimeActivities.value = realtimeActivities.value.slice(0, 20)
    }
  }

  // 处理实时活动事件
  const handleRealtimeActivityEvent = (eventData: any) => {
    const newActivity: Activity = {
      id: `realtime-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      type: eventData.type,
      content: eventData.content,
      date: '刚刚',
      articleId: eventData.articleId,
      metadata: eventData.metadata
    }

    realtimeActivities.value.unshift(newActivity)
    
    // 限制实时活动数量
    if (realtimeActivities.value.length > 20) {
      realtimeActivities.value = realtimeActivities.value.slice(0, 20)
    }

    console.log('新活动添加:', newActivity)
  }

  // 监听文章数据变化
  watch(
    () => articlesStore.articles,
    () => {
      activities.value = allActivities.value
    },
    { deep: true, immediate: true }
  )

  // 初始化和清理
  onMounted(() => {
    refreshActivities()
    // 监听实时活动事件
    activityEventBus.on('activity', handleRealtimeActivityEvent)
  })

  onUnmounted(() => {
    // 清理事件监听
    activityEventBus.off('activity', handleRealtimeActivityEvent)
  })

  return {
    activities: allActivities,
    isLoading,
    refreshActivities,
    addActivity,
    formatTimeAgo
  }
} 