<template>
  <div class="article-detail min-h-screen bg-gray-50">
    <!-- 阅读进度条 -->
    <div class="reading-progress-bar">
      <div class="progress-fill" :style="{ width: readingProgress + '%' }"></div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center min-h-screen">
      <LoadingSpinner />
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="flex items-center justify-center min-h-screen">
      <div class="text-center">
        <svg class="w-16 h-16 text-red-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L4.35 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
        </svg>
        <h2 class="text-2xl font-bold text-gray-900 mb-2">文章加载失败</h2>
        <p class="text-gray-600 mb-4">{{ error }}</p>
        <button
          @click="fetchArticle"
          class="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition"
        >
          重新加载
        </button>
      </div>
    </div>

    <!-- 文章内容 -->
    <div v-else class="luxury-article-container">
      <div class="article-grid">
        <!-- 主要内容区域 -->
        <main class="article-main">
          <!-- 返回导航 -->
          <nav class="article-navigation">
            <button
              @click="goBack"
              class="nav-button back-btn"
            >
              <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path>
              </svg>
              <span>返回</span>
            </button>
            
            <div class="breadcrumb">
              <RouterLink to="/" class="breadcrumb-link">首页</RouterLink>
              <span class="breadcrumb-separator">→</span>
              <RouterLink to="/articles" class="breadcrumb-link">文章</RouterLink>
              <span class="breadcrumb-separator">→</span>
              <span class="breadcrumb-current">{{ article?.title || '文章详情' }}</span>
            </div>
          </nav>

          <!-- 文章主体 -->
          <article class="luxury-article">
            <!-- 文章头部 -->
            <header class="article-header">
              <!-- 分类和标签 -->
              <div class="article-meta-top">
                <div class="category-wrapper">
                  <span class="category-badge">{{ article?.categoryName || '未分类' }}</span>
                </div>
                <div class="reading-time">
                  <svg class="time-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                  </svg>
                  <span>{{ estimatedReadingTime }} 分钟阅读</span>
                </div>
              </div>

              <!-- 标题 -->
              <h1 class="article-title">{{ article?.title }}</h1>

              <!-- 副标题/摘要 -->
              <div v-if="article?.summary" class="article-subtitle">
                {{ article.summary }}
              </div>

              <!-- 文章元信息 -->
              <div class="article-meta">
                <!-- 作者信息 -->
                <div class="author-info">
                  <div class="author-avatar">
                    <span>{{ article?.authorName?.charAt(0) || 'A' }}</span>
                  </div>
                  <div class="author-details">
                    <div class="author-name">{{ article?.authorName || '匿名' }}</div>
                    <div class="publish-date">{{ formatDate(article?.publishTime || article?.createdAt) }}</div>
                  </div>
                </div>

                <!-- 统计信息 -->
                <div class="article-stats">
                  <div class="stat-item">
                    <svg class="stat-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                    </svg>
                    <span>{{ formatNumber(article?.views || 0) }}</span>
                  </div>
                  <div class="stat-item">
                    <svg class="stat-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
                    </svg>
                    <span>{{ formatNumber(article?.likes || 0) }}</span>
                  </div>
                  <div class="stat-item">
                    <svg class="stat-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
                    </svg>
                    <span>{{ formatNumber(article?.commentsCount || 0) }}</span>
                  </div>
                </div>
              </div>
            </header>

            <!-- 文章内容 -->
            <div class="article-content" ref="articleContentRef">
              <div 
                class="prose luxury-prose"
                v-html="renderedContent"
              ></div>
            </div>

            <!-- 文章底部操作 -->
            <footer class="article-footer">
              <div class="article-actions">
                <!-- 点赞和收藏 -->
                <div class="action-buttons">
                  <button
                    @click="toggleLike"
                    :class="[
                      'action-btn like-btn',
                      { 'active': isLiked }
                    ]"
                  >
                    <svg class="action-icon" :fill="isLiked ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
                    </svg>
                    <span>{{ isLiked ? '已点赞' : '点赞' }}</span>
                    <span class="count">({{ article?.likes || 0 }})</span>
                  </button>

                  <button
                    @click="toggleBookmark"
                    :class="[
                      'action-btn bookmark-btn',
                      { 'active': isBookmarked }
                    ]"
                  >
                    <svg class="action-icon" :fill="isBookmarked ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"></path>
                    </svg>
                    <span>{{ isBookmarked ? '已收藏' : '收藏' }}</span>
                  </button>

                  <button @click="shareArticle" class="action-btn share-btn">
                    <svg class="action-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.367 2.684 3 3 0 00-5.367-2.684z"></path>
                    </svg>
                    <span>分享</span>
                  </button>
                </div>

                <!-- 分享选项 -->
                <div v-if="showShareOptions" class="share-options">
                  <h4 class="share-title">分享到</h4>
                  <div class="share-buttons">
                    <button @click="copyLink" class="share-option">
                      <svg class="share-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"></path>
                      </svg>
                      <span>复制链接</span>
                    </button>
                    <button @click="shareToWeChat" class="share-option">
                      <div class="share-icon wechat">微</div>
                      <span>微信</span>
                    </button>
                    <button @click="shareToWeibo" class="share-option">
                      <div class="share-icon weibo">微</div>
                      <span>微博</span>
                    </button>
                  </div>
                </div>
              </div>

              <!-- 文章标签 -->
              <div v-if="articleTags.length" class="article-tags">
                <h4 class="tags-title">文章标签</h4>
                <div class="tags-list">
                  <span v-for="tag in articleTags" :key="tag" class="article-tag">
                    {{ tag }}
                  </span>
                </div>
              </div>
            </footer>
          </article>

          <!-- 评论区域 -->
          <section v-if="article?.allowComments" class="comments-section">
            <CommentSection :article-id="articleId.toString()" />
          </section>
        </main>

        <!-- 增强侧边栏 -->
        <aside class="article-sidebar">
          <!-- 目录导航 -->
          <div class="sidebar-card toc-card">
            <h3 class="sidebar-title">文章目录</h3>
            <nav class="table-of-contents">
              <ul class="toc-list">
                <li v-for="heading in tableOfContents" :key="heading.id" :class="['toc-item', `toc-level-${heading.level}`]">
                  <a 
                    :href="`#${heading.id}`" 
                    @click.prevent="scrollToHeading(heading.id)"
                    class="toc-link"
                    :class="{ active: activeHeading === heading.id }"
                  >
                    {{ heading.text }}
                  </a>
                </li>
              </ul>
            </nav>
          </div>

          <!-- 作者信息卡片 -->
          <div class="sidebar-card author-card">
            <h3 class="sidebar-title">关于作者</h3>
            <div class="author-profile">
              <div class="author-avatar large">
                <span>{{ article?.authorName?.charAt(0) || 'A' }}</span>
              </div>
              <h4 class="author-name">{{ article?.authorName || '匿名' }}</h4>
              <p class="author-bio">专注技术分享，记录学习点滴，分享实战经验与思考</p>
              <div class="author-stats">
                <div class="author-stat">
                  <div class="stat-number">{{ authorStats.articles }}</div>
                  <div class="stat-label">文章</div>
                </div>
                <div class="author-stat">
                  <div class="stat-number">{{ authorStats.followers }}</div>
                  <div class="stat-label">关注者</div>
                </div>
                <div class="author-stat">
                  <div class="stat-number">{{ authorStats.likes }}</div>
                  <div class="stat-label">获赞</div>
                </div>
              </div>
              <button class="follow-btn">
                <svg class="follow-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                </svg>
                <span>关注作者</span>
              </button>
            </div>
          </div>

          <!-- 相关文章 -->
          <div class="sidebar-card related-articles">
            <h3 class="sidebar-title">相关推荐</h3>
            <div class="related-list">
              <article
                v-for="relatedArticle in relatedArticles"
                :key="relatedArticle.id"
                class="related-item"
                @click="navigateToArticle(relatedArticle.id)"
              >
                <h4 class="related-title">{{ relatedArticle.title }}</h4>
                <div class="related-meta">
                  <span class="related-date">{{ formatDate(relatedArticle.publishTime || relatedArticle.createdAt) }}</span>
                  <div class="related-stats">
                    <span class="related-views">
                      <svg class="related-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                      </svg>
                      {{ formatNumber(relatedArticle.views || 0) }}
                    </span>
                  </div>
                </div>
              </article>
            </div>
          </div>

          <!-- 热门标签 -->
          <div class="sidebar-card popular-tags">
            <h3 class="sidebar-title">热门标签</h3>
            <div class="tags-cloud">
              <button
                v-for="tag in popularTags"
                :key="tag.name"
                @click="searchByTag(tag.name)"
                class="tag-cloud-item"
                :style="{ fontSize: getTagSize(tag.count) + 'px' }"
              >
                {{ tag.name }}
              </button>
            </div>
          </div>
        </aside>
      </div>
    </div>

    <!-- 浮动工具栏 -->
    <div class="floating-toolbar" :class="{ visible: showFloatingToolbar }">
      <button @click="scrollToTop" class="toolbar-btn" title="回到顶部">
        <svg class="toolbar-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"></path>
        </svg>
      </button>
      
      <button @click="toggleLike" class="toolbar-btn" :class="{ active: isLiked }" title="点赞">
        <svg class="toolbar-icon" :fill="isLiked ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
        </svg>
      </button>
      
      <button @click="toggleBookmark" class="toolbar-btn" :class="{ active: isBookmarked }" title="收藏">
        <svg class="toolbar-icon" :fill="isBookmarked ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"></path>
        </svg>
      </button>
      
      <button @click="shareArticle" class="toolbar-btn" title="分享">
        <svg class="toolbar-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.367 2.684 3 3 0 00-5.367-2.684z"></path>
        </svg>
      </button>
    </div>

    <!-- 通知提示 -->
    <NotificationToast
      :show="notification.show"
      :title="notification.message"
      :type="notification.type"
      @close="notification.show = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useArticlesStore } from '../stores/articles'
import CommentSection from '../components/CommentSection.vue'
import LoadingSpinner from '../components/LoadingSpinner.vue'
import NotificationToast from '../components/NotificationToast.vue'

const route = useRoute()
const router = useRouter()
const articlesStore = useArticlesStore()

// 响应式数据
const loading = ref(true)
const error = ref('')
const isLiked = ref(false)
const isBookmarked = ref(false)
const showBackToTop = ref(false)
const showFloatingToolbar = ref(false)
const showShareOptions = ref(false)
const notification = ref({ show: false, message: '', type: 'success' as 'success' | 'error' | 'info' | 'warning' })
const readingProgress = ref(0)
const activeHeading = ref('')
const articleContentRef = ref<HTMLElement | null>(null)

// 计算属性
const articleId = computed(() => Number(route.params.id))
const article = computed(() => articlesStore.articles.find(a => a.id === articleId.value))

// 新增计算属性
const estimatedReadingTime = computed(() => {
  if (!article.value?.content) return 0
  const wordsPerMinute = 200
  const words = article.value.content.length / 3 // 中文字符估算
  return Math.ceil(words / wordsPerMinute)
})

const articleTags = computed(() => {
  // 从文章内容或分类中提取标签（或者从其他地方获取）
  const tags: string[] = []
  
  // 如果有分类，添加分类作为标签
  if (article.value?.categoryName) {
    tags.push(article.value.categoryName)
  }
  
  // 可以从文章内容中提取更多标签（这里先返回基本标签）
  return tags.length > 0 ? tags : ['技术分享', '学习笔记']
})

const tableOfContents = computed(() => {
  if (!article.value?.content) return []
  
  const headings = []
  const content = article.value.content
  const headingRegex = /(#{1,6})\s+(.+)/g
  let match
  let index = 0
  
  while ((match = headingRegex.exec(content)) !== null) {
    const level = match[1].length
    const text = match[2].trim()
    const id = `heading-${index++}`
    
    headings.push({
      id,
      level,
      text,
      anchor: text.toLowerCase().replace(/[^\w\u4e00-\u9fa5]+/g, '-')
    })
  }
  
  return headings
})

const renderedContent = computed(() => {
  if (!article.value?.content) return ''
  
  let content = article.value.content
  
  // 为标题添加 id
  tableOfContents.value.forEach((heading, index) => {
    const headingPattern = new RegExp(`(#{${heading.level}})\\s+${heading.text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}`, 'g')
    content = content.replace(headingPattern, `$1 <span id="${heading.id}">${heading.text}</span>`)
  })
  
  // 简单的 Markdown 解析
  content = content
    .replace(/^### (.*$)/gim, '<h3>$1</h3>')
    .replace(/^## (.*$)/gim, '<h2>$1</h2>')
    .replace(/^# (.*$)/gim, '<h1>$1</h1>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    .replace(/`(.*?)`/g, '<code>$1</code>')
    .replace(/\n/g, '<br>')
  
  return content
})

// 作者统计数据
const authorStats = computed(() => ({
  articles: 42,
  followers: 1.2,
  likes: 3.8
}))

// 相关文章
const relatedArticles = computed(() => {
  if (!article.value) return []
  
  return articlesStore.articles
    .filter(a => 
      a.id !== article.value!.id && 
      (a.categoryName === article.value!.categoryName || 
       a.authorName === article.value!.authorName)
    )
    .slice(0, 5)
})

// 热门标签
const popularTags = ref([
  { name: 'Vue.js', count: 45 },
  { name: 'TypeScript', count: 38 },
  { name: 'React', count: 32 },
  { name: 'JavaScript', count: 56 },
  { name: '前端开发', count: 28 },
  { name: 'Node.js', count: 24 },
  { name: 'CSS', count: 22 },
  { name: '性能优化', count: 18 },
])

// 监听滚动事件
const handleScroll = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight
  
  // 计算阅读进度
  const progress = (scrollTop / (documentHeight - windowHeight)) * 100
  readingProgress.value = Math.min(Math.max(progress, 0), 100)
  
  // 显示/隐藏回到顶部按钮
  showBackToTop.value = scrollTop > 300
  
  // 显示/隐藏浮动工具栏
  showFloatingToolbar.value = scrollTop > 500
  
  // 更新当前活跃的标题
  updateActiveHeading()
}

// 更新活跃标题
const updateActiveHeading = () => {
  const headings = tableOfContents.value
  if (!headings.length) return
  
  for (let i = headings.length - 1; i >= 0; i--) {
    const heading = headings[i]
    const element = document.getElementById(heading.id)
    if (element) {
      const rect = element.getBoundingClientRect()
      if (rect.top <= 100) {
        activeHeading.value = heading.id
        break
      }
    }
  }
}

// 方法
const goBack = () => {
  router.go(-1)
}

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

const scrollToHeading = (headingId: string) => {
  const element = document.getElementById(headingId)
  if (element) {
    const offset = 80 // 偏移量，避免被固定头部遮挡
    const elementPosition = element.getBoundingClientRect().top + window.pageYOffset
    window.scrollTo({
      top: elementPosition - offset,
      behavior: 'smooth'
    })
    activeHeading.value = headingId
  }
}

const toggleLike = async () => {
  try {
    isLiked.value = !isLiked.value
    if (article.value) {
      article.value.likes = (article.value.likes || 0) + (isLiked.value ? 1 : -1)
    }
    showNotification(isLiked.value ? '已点赞' : '已取消点赞', 'success')
  } catch (error) {
    console.error('点赞失败:', error)
    showNotification('操作失败，请重试', 'error')
  }
}

const toggleBookmark = async () => {
  try {
    isBookmarked.value = !isBookmarked.value
    showNotification(isBookmarked.value ? '已收藏' : '已取消收藏', 'success')
  } catch (error) {
    console.error('收藏失败:', error)
    showNotification('操作失败，请重试', 'error')
  }
}

const shareArticle = () => {
  showShareOptions.value = !showShareOptions.value
}

const copyLink = async () => {
  try {
    const url = window.location.href
    await navigator.clipboard.writeText(url)
    showNotification('链接已复制到剪贴板', 'success')
    showShareOptions.value = false
  } catch (error) {
    console.error('复制失败:', error)
    showNotification('复制失败，请手动复制', 'error')
  }
}

const shareToWeChat = () => {
  // 微信分享逻辑
  showNotification('微信分享功能开发中', 'info')
  showShareOptions.value = false
}

const shareToWeibo = () => {
  // 微博分享逻辑
  const url = window.location.href
  const title = article.value?.title || '分享文章'
  const shareUrl = `https://service.weibo.com/share/share.php?url=${encodeURIComponent(url)}&title=${encodeURIComponent(title)}`
  window.open(shareUrl, '_blank', 'width=600,height=400')
  showShareOptions.value = false
}

const searchByTag = (tagName: string) => {
  router.push({
    path: '/search',
    query: { q: tagName }
  })
}

const fetchArticle = async () => {
  try {
    loading.value = true
    error.value = ''
    await articlesStore.initializeData()
    if (!article.value) {
      error.value = '文章不存在'
    }
  } catch (err) {
    console.error('加载文章失败:', err)
    error.value = '加载文章失败，请重试'
  } finally {
    loading.value = false
  }
}

const navigateToArticle = (id: number | undefined) => {
  if (id) {
    router.push(`/article/${id}`)
  }
}

const getTagSize = (count: number): number => {
  const maxCount = Math.max(...popularTags.value.map(tag => tag.count))
  const minSize = 12
  const maxSize = 18
  return minSize + ((count / maxCount) * (maxSize - minSize))
}

const showNotification = (message: string, type: 'success' | 'error' | 'info' = 'success') => {
  notification.value = { show: true, message, type }
  setTimeout(() => {
    notification.value.show = false
  }, 3000)
}

const formatDate = (dateString: string | undefined): string => {
  if (!dateString) return '未知日期'
  
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return '无效日期'
    
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  } catch (error) {
    console.error('日期格式化错误:', error)
    return '日期错误'
  }
}

const formatNumber = (num: number | undefined): string => {
  if (num === undefined || num === null || isNaN(num)) return '0'
  
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 生命周期
onMounted(async () => {
  try {
    await articlesStore.initializeData()
    loading.value = false
    
    // 添加滚动监听
    window.addEventListener('scroll', handleScroll, { passive: true })
    
    // 初始化阅读状态（从本地存储或API获取）
    const savedLikes = localStorage.getItem('likedArticles')
    const savedBookmarks = localStorage.getItem('bookmarkedArticles')
    
    if (savedLikes) {
      const likedArticles = JSON.parse(savedLikes)
      isLiked.value = likedArticles.includes(articleId.value)
    }
    
    if (savedBookmarks) {
      const bookmarkedArticles = JSON.parse(savedBookmarks)
      isBookmarked.value = bookmarkedArticles.includes(articleId.value)
    }
    
    // 增加阅读量
    if (article.value && article.value.views !== undefined) {
      article.value.views = (article.value.views || 0) + 1
    }
    
  } catch (err) {
    console.error('加载文章失败:', err)
    error.value = '加载文章失败，请重试'
    loading.value = false
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.luxury-article-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a2e 50%, #16213e 100%);
  color: white;
}

/* 阅读进度条 */
.reading-progress-bar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.1);
  z-index: 1000;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #ffd700, #ffed4e);
  transition: width 0.3s ease;
}

/* 文章网格布局 */
.article-grid {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 3rem;
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

/* 主要内容区域 */
.article-main {
  min-width: 0; /* 防止内容溢出 */
}

/* 导航样式 */
.article-navigation {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
  padding: 1rem 0;
}

.nav-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.8rem 1.5rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  color: white;
  text-decoration: none;
  transition: all 0.3s ease;
  cursor: pointer;
}

.nav-button:hover {
  background: rgba(255, 215, 0, 0.2);
  border-color: rgba(255, 215, 0, 0.4);
}

.nav-icon {
  width: 1.2rem;
  height: 1.2rem;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.breadcrumb-link {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  transition: color 0.3s ease;
}

.breadcrumb-link:hover {
  color: #ffd700;
}

.breadcrumb-separator {
  color: rgba(255, 255, 255, 0.4);
}

.breadcrumb-current {
  color: #ffd700;
  font-weight: 500;
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 文章样式 */
.luxury-article {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 25px;
  overflow: hidden;
  position: relative;
}

.luxury-article::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #ffd700, transparent);
}

/* 文章头部 */
.article-header {
  padding: 3rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.article-meta-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.category-badge {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #1a1a2e;
  padding: 0.5rem 1.5rem;
  border-radius: 25px;
  font-size: 0.9rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.reading-time {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
}

.time-icon {
  width: 1rem;
  height: 1rem;
}

.article-title {
  font-size: 3rem;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 1.5rem;
  background: linear-gradient(135deg, #ffffff, #f0f0f0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.article-subtitle {
  font-size: 1.3rem;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
  margin-bottom: 2rem;
  font-weight: 300;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.author-avatar {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1a1a2e;
  font-weight: 700;
  font-size: 1.2rem;
}

.author-avatar.large {
  width: 80px;
  height: 80px;
  font-size: 2rem;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.author-name {
  font-weight: 600;
  color: white;
  font-size: 1.1rem;
}

.publish-date {
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.9rem;
}

.article-stats {
  display: flex;
  gap: 2rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
}

.stat-icon {
  width: 1.2rem;
  height: 1.2rem;
}

/* 文章内容 */
.article-content {
  padding: 3rem;
}

.luxury-prose {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.8;
  font-size: 1.1rem;
}

.luxury-prose h1,
.luxury-prose h2,
.luxury-prose h3 {
  color: white;
  margin: 2rem 0 1rem;
  font-weight: 600;
}

.luxury-prose h1 {
  font-size: 2.2rem;
  border-bottom: 2px solid rgba(255, 215, 0, 0.3);
  padding-bottom: 0.5rem;
}

.luxury-prose h2 {
  font-size: 1.8rem;
  color: #ffd700;
}

.luxury-prose h3 {
  font-size: 1.4rem;
}

.luxury-prose code {
  background: rgba(255, 215, 0, 0.1);
  color: #ffd700;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-family: 'Fira Code', monospace;
}

.luxury-prose strong {
  color: #ffd700;
  font-weight: 600;
}

.luxury-prose em {
  color: rgba(255, 255, 255, 0.8);
  font-style: italic;
}

/* 文章底部 */
.article-footer {
  padding: 3rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.article-actions {
  margin-bottom: 2rem;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.action-btn:hover {
  background: rgba(255, 215, 0, 0.2);
  border-color: rgba(255, 215, 0, 0.4);
}

.action-btn.active {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #1a1a2e;
  border-color: transparent;
}

.action-icon {
  width: 1.2rem;
  height: 1.2rem;
}

.count {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.8rem;
}

.action-btn.active .count {
  color: rgba(26, 26, 46, 0.7);
}

/* 分享选项 */
.share-options {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  padding: 1.5rem;
  margin-top: 1rem;
}

.share-title {
  color: white;
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.share-buttons {
  display: flex;
  gap: 1rem;
}

.share-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 80px;
}

.share-option:hover {
  background: rgba(255, 215, 0, 0.2);
  border-color: rgba(255, 215, 0, 0.4);
}

.share-icon {
  width: 1.5rem;
  height: 1.5rem;
}

.share-icon.wechat,
.share-icon.weibo {
  width: 30px;
  height: 30px;
  background: #ffd700;
  color: #1a1a2e;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.8rem;
}

/* 文章标签 */
.article-tags {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 2rem;
}

.tags-title {
  color: white;
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.8rem;
}

.article-tag {
  background: rgba(255, 215, 0, 0.1);
  color: #ffd700;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.8rem;
  border: 1px solid rgba(255, 215, 0, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
}

.article-tag:hover {
  background: rgba(255, 215, 0, 0.2);
  border-color: rgba(255, 215, 0, 0.4);
}

/* 评论区域 */
.comments-section {
  margin-top: 3rem;
}

/* 侧边栏 */
.article-sidebar {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  position: sticky;
  top: 2rem;
  height: fit-content;
}

.sidebar-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 2rem;
  transition: all 0.3s ease;
}

.sidebar-card:hover {
  border-color: rgba(255, 215, 0, 0.3);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.sidebar-title {
  color: white;
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.sidebar-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  border-radius: 2px;
}

/* 目录导航 */
.table-of-contents {
  max-height: 400px;
  overflow-y: auto;
}

.toc-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.toc-item {
  margin-bottom: 0.5rem;
}

.toc-level-1 { margin-left: 0; }
.toc-level-2 { margin-left: 1rem; }
.toc-level-3 { margin-left: 2rem; }
.toc-level-4 { margin-left: 3rem; }
.toc-level-5 { margin-left: 4rem; }
.toc-level-6 { margin-left: 5rem; }

.toc-link {
  display: block;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  padding: 0.5rem 0.8rem;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  line-height: 1.4;
  border-left: 2px solid transparent;
}

.toc-link:hover,
.toc-link.active {
  color: #ffd700;
  background: rgba(255, 215, 0, 0.1);
  border-left-color: #ffd700;
}

/* 作者信息 */
.author-profile {
  text-align: center;
}

.author-bio {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
  line-height: 1.5;
  margin: 1rem 0;
}

.author-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin: 1.5rem 0;
}

.author-stat {
  text-align: center;
}

.stat-number {
  color: #ffd700;
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 0.2rem;
}

.stat-label {
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.follow-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #1a1a2e;
  border: none;
  border-radius: 50px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.follow-btn:hover {
  background: linear-gradient(135deg, #ffed4e, #ffd700);
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(255, 215, 0, 0.3);
}

.follow-icon {
  width: 1rem;
  height: 1rem;
}

/* 相关文章 */
.related-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.related-item {
  padding: 1rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.related-item:hover {
  background: rgba(255, 215, 0, 0.1);
  border-color: rgba(255, 215, 0, 0.3);
}

.related-title {
  color: white;
  font-size: 0.9rem;
  font-weight: 500;
  margin-bottom: 0.5rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.related-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.6);
}

.related-views {
  display: flex;
  align-items: center;
  gap: 0.3rem;
}

.related-icon {
  width: 0.8rem;
  height: 0.8rem;
}

/* 热门标签 */
.tags-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 0.8rem;
  align-items: center;
}

.tag-cloud-item {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 0.5rem 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.tag-cloud-item:hover {
  background: rgba(255, 215, 0, 0.2);
  color: #ffd700;
  border-color: rgba(255, 215, 0, 0.4);
}

/* 浮动工具栏 */
.floating-toolbar {
  position: fixed;
  right: 2rem;
  bottom: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.3s ease;
  z-index: 100;
}

.floating-toolbar.visible {
  opacity: 1;
  transform: translateY(0);
}

.toolbar-btn {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toolbar-btn:hover {
  background: rgba(255, 215, 0, 0.2);
  border-color: rgba(255, 215, 0, 0.4);
  color: #ffd700;
  transform: translateY(-2px);
}

.toolbar-btn.active {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #1a1a2e;
  border-color: transparent;
}

.toolbar-icon {
  width: 1.5rem;
  height: 1.5rem;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .article-grid {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .article-sidebar {
    position: static;
    order: -1;
  }
}

@media (max-width: 768px) {
  .article-grid {
    padding: 1rem;
  }
  
  .article-header {
    padding: 2rem;
  }
  
  .article-content {
    padding: 2rem;
  }
  
  .article-footer {
    padding: 2rem;
  }
  
  .article-title {
    font-size: 2rem;
  }
  
  .article-meta {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }
  
  .article-stats {
    gap: 1rem;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .floating-toolbar {
    right: 1rem;
    bottom: 1rem;
  }
  
  .sidebar-card {
    padding: 1.5rem;
  }
}

@media (max-width: 480px) {
  .article-navigation {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }
  
  .breadcrumb {
    order: -1;
  }
  
  .article-header {
    padding: 1.5rem;
  }
  
  .article-content {
    padding: 1.5rem;
  }
  
  .article-footer {
    padding: 1.5rem;
  }
  
  .article-title {
    font-size: 1.5rem;
  }
}
</style> 