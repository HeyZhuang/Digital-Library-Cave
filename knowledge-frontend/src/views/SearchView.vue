<template>
  <div class="luxury-search-view">
    <!-- 搜索英雄区域 -->
    <section class="search-hero">
      <div class="search-particles"></div>
      <div class="search-hero-content">
        <div class="search-icon-wrapper">
          <div class="search-icon-circle">
            <svg class="search-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
            </svg>
          </div>
        </div>
        
        <h1 class="search-title">知识搜索</h1>
        <p class="search-subtitle">探索知识宝库，发现技术真理</p>
        
        <!-- 增强搜索框 -->
        <div class="enhanced-search-container">
          <div class="search-input-wrapper">
            <div class="search-input-icon">
              <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
              </svg>
            </div>
            <input
              v-model="localSearchQuery"
              type="text"
              placeholder="搜索文章标题、内容、标签..."
              class="enhanced-search-input"
              @keyup.enter="performSearch"
              @input="handleInput"
            />
            <button
              v-if="localSearchQuery"
              @click="clearSearch"
              class="clear-search-btn"
            >
              <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
            <button @click="performSearch" class="search-submit-btn">
              <span>搜索</span>
            </button>
          </div>
        </div>

        <!-- 搜索筛选器 -->
        <div class="search-filters">
          <div class="filter-group">
            <label class="filter-label">分类</label>
            <select v-model="selectedCategory" @change="applyFilters" class="filter-select">
              <option value="">全部分类</option>
              <option v-for="category in categories" :key="category" :value="category">{{ category }}</option>
            </select>
          </div>
          
          <div class="filter-group">
            <label class="filter-label">排序</label>
            <select v-model="sortBy" @change="applySorting" class="filter-select">
              <option value="relevance">相关度</option>
              <option value="date">发布时间</option>
              <option value="views">阅读量</option>
              <option value="likes">点赞数</option>
            </select>
          </div>
          
          <div class="filter-group">
            <label class="filter-label">时间范围</label>
            <select v-model="timeRange" @change="applyFilters" class="filter-select">
              <option value="">全部时间</option>
              <option value="week">最近一周</option>
              <option value="month">最近一月</option>
              <option value="year">最近一年</option>
            </select>
          </div>
        </div>

        <!-- 搜索状态和统计 -->
        <div class="search-stats">
          <div v-if="searchQuery" class="stats-content">
            <div class="stats-main">
              关键词 "<span class="highlight">{{ searchQuery }}</span>" 的搜索结果
            </div>
            <div class="stats-details">
              <span class="stat-item">共找到 <strong>{{ filteredResults.length }}</strong> 篇文章</span>
              <span class="stat-item">用时 <strong>{{ searchTime }}ms</strong></span>
              <span v-if="selectedCategory" class="stat-item">分类: <strong>{{ selectedCategory }}</strong></span>
            </div>
          </div>
          <div v-else class="no-search-prompt">
            <span class="prompt-text">请输入关键词开始搜索</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 搜索结果区域 -->
    <section class="search-results-section">
      <!-- 结果列表 -->
      <div v-if="filteredResults.length > 0" class="results-container">
        <!-- 结果概览 -->
        <div class="results-overview">
          <div class="results-count">
            <span class="count-number">{{ filteredResults.length }}</span>
            <span class="count-label">个搜索结果</span>
          </div>
          <div class="results-actions">
            <button @click="toggleViewMode" class="view-toggle-btn" :class="{ active: viewMode === 'grid' }">
              <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path>
              </svg>
            </button>
            <button @click="toggleViewMode" class="view-toggle-btn" :class="{ active: viewMode === 'list' }">
              <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 10h16M4 14h16M4 18h16"></path>
              </svg>
            </button>
          </div>
        </div>

        <!-- 搜索结果列表 -->
        <div class="results-grid" :class="viewMode">
          <article
            v-for="(article, index) in paginatedResults"
            :key="article.id"
            class="result-card"
            :class="{ 'featured': index < 3 }"
            @click="navigateToArticle(article.id)"
          >
            <!-- 文章排名 -->
            <div class="article-rank" v-if="index < 3">{{ index + 1 }}</div>
            
            <!-- 文章内容 -->
            <div class="article-content">
              <!-- 分类标签 -->
              <div class="article-category" v-if="article.categoryName">
                <span class="category-tag">{{ article.categoryName }}</span>
              </div>

              <!-- 标题 -->
              <h3 class="article-title" v-html="highlightText(article.title)"></h3>

              <!-- 摘要 -->
              <p class="article-summary" v-html="highlightText(article.summary || truncateContent(article.content, 200))"></p>

              <!-- 文章元数据 -->
              <div class="article-meta">
                <div class="meta-group">
                  <div class="meta-item" v-if="article.views">
                    <svg class="meta-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                    </svg>
                    <span>{{ formatNumber(article.views) }} 阅读</span>
                  </div>
                  <div class="meta-item" v-if="article.createdAt">
                    <svg class="meta-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                    </svg>
                    <span>{{ formatDate(article.createdAt) }}</span>
                  </div>
                  <div class="meta-item" v-if="article.likes">
                    <svg class="meta-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
                    </svg>
                    <span>{{ formatNumber(article.likes) }} 点赞</span>
                  </div>
                </div>
              </div>

              <!-- 相关度评分 -->
              <div class="relevance-score" v-if="getRelevanceScore(article)">
                <span class="score-label">相关度</span>
                <div class="score-bar">
                  <div class="score-fill" :style="{ width: getRelevanceScore(article) + '%' }"></div>
                </div>
                <span class="score-value">{{ getRelevanceScore(article) }}%</span>
              </div>
            </div>

            <!-- 阅读更多按钮 -->
            <div class="read-more-btn">
              <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
              </svg>
            </div>
          </article>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="totalPages > 1">
          <button
            @click="goToPage(currentPage - 1)"
            :disabled="currentPage === 1"
            class="pagination-btn prev"
          >
            <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
            </svg>
            <span>上一页</span>
          </button>
          
          <div class="pagination-numbers">
            <button
              v-for="page in paginationRange"
              :key="page"
              @click="goToPage(page)"
              :class="['pagination-number', { active: page === currentPage }]"
            >
              {{ page }}
            </button>
          </div>
          
          <button
            @click="goToPage(currentPage + 1)"
            :disabled="currentPage === totalPages"
            class="pagination-btn next"
          >
            <span>下一页</span>
            <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>
        </div>
      </div>

    <!-- 无搜索结果 -->
    <div v-else-if="searchQuery" class="no-results">
      <div class="no-results-content">
        <div class="no-results-icon">
          <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
          </svg>
        </div>
        <h3 class="no-results-title">未找到相关文章</h3>
        <p class="no-results-description">
          没有找到包含 "<span class="highlight">{{ searchQuery }}</span>" 的文章
        </p>
        <div class="no-results-suggestions">
          <h4 class="suggestions-title">搜索建议：</h4>
          <ul class="suggestions-list">
            <li>检查关键词拼写是否正确</li>
            <li>尝试使用更简单的关键词</li>
            <li>使用不同的关键词组合</li>
            <li>清除筛选条件重新搜索</li>
          </ul>
        </div>
        <div class="suggested-searches">
          <h4 class="suggestions-title">热门搜索：</h4>
          <div class="search-tags">
            <button
              v-for="suggestion in searchSuggestions"
              :key="suggestion"
              @click="quickSearch(suggestion)"
              class="search-tag"
            >
              {{ suggestion }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索建议 -->
    <div v-else class="search-suggestions">
      <div class="suggestions-content">
        <div class="suggestions-icon">
          <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
          </svg>
        </div>
        <h3 class="suggestions-title">开始探索知识宝库</h3>
        <p class="suggestions-description">在上方输入关键词搜索相关文章</p>
        
        <div class="popular-searches">
          <h4 class="section-title">热门搜索</h4>
          <div class="search-tags">
            <button
              v-for="suggestion in searchSuggestions"
              :key="suggestion"
              @click="quickSearch(suggestion)"
              class="search-tag popular"
            >
              {{ suggestion }}
            </button>
          </div>
        </div>
        
        <div class="recent-articles">
          <h4 class="section-title">最新文章</h4>
          <div class="recent-list">
            <div
              v-for="article in recentArticles"
              :key="article.id"
              class="recent-item"
              @click="navigateToArticle(article.id)"
            >
              <h5 class="recent-title">{{ article.title }}</h5>
              <div class="recent-meta">
                <span class="recent-category" v-if="article.categoryName">{{ article.categoryName }}</span>
                <span class="recent-date">{{ formatDate(article.createdAt) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useArticlesStore } from '../stores/articles'

const router = useRouter()
const articlesStore = useArticlesStore()

const localSearchQuery = ref('')
const selectedCategory = ref('')
const sortBy = ref('relevance')
const timeRange = ref('')
const viewMode = ref<'grid' | 'list'>('grid')
const currentPage = ref(1)
const pageSize = ref(10)
const searchTime = ref(0)

const searchQuery = computed(() => articlesStore.searchQuery)
const searchResults = computed(() => articlesStore.filteredArticles)

// 新增的数据
const categories = ref(['Vue.js', 'React', 'TypeScript', 'Node.js', 'Python', 'Java', '工程化', '性能优化'])
const searchSuggestions = ['Vue3', 'TypeScript', 'React Hooks', '性能优化', 'Webpack', 'Vite', 'Node.js', 'Python']
const recentArticles = computed(() => articlesStore.articles.slice(0, 5))

// 计算属性
const filteredResults = computed(() => {
  const startTime = Date.now()
  let results = [...searchResults.value]
  
  // 分类筛选
  if (selectedCategory.value) {
    results = results.filter(article => article.categoryName === selectedCategory.value)
  }
  
  // 时间筛选
  if (timeRange.value) {
    const now = new Date()
    const filterDate = new Date()
    
    switch (timeRange.value) {
      case 'week':
        filterDate.setDate(now.getDate() - 7)
        break
      case 'month':
        filterDate.setMonth(now.getMonth() - 1)
        break
      case 'year':
        filterDate.setFullYear(now.getFullYear() - 1)
        break
    }
    
    results = results.filter(article => {
      const articleDate = new Date(article.createdAt || '')
      return articleDate >= filterDate
    })
  }
  
  // 排序
  results.sort((a, b) => {
    switch (sortBy.value) {
      case 'date':
        return new Date(b.createdAt || '').getTime() - new Date(a.createdAt || '').getTime()
      case 'views':
        return (b.views || 0) - (a.views || 0)
      case 'likes':
        return (b.likes || 0) - (a.likes || 0)
      default: // relevance
        return getRelevanceScore(b) - getRelevanceScore(a)
    }
  })
  
  searchTime.value = Date.now() - startTime
  return results
})

const totalPages = computed(() => Math.ceil(filteredResults.value.length / pageSize.value))

const paginatedResults = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredResults.value.slice(start, end)
})

const paginationRange = computed(() => {
  const range = []
  const total = totalPages.value
  const current = currentPage.value
  
  if (total <= 7) {
    for (let i = 1; i <= total; i++) {
      range.push(i)
    }
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) {
        range.push(i)
      }
      range.push('...')
      range.push(total)
    } else if (current >= total - 3) {
      range.push(1)
      range.push('...')
      for (let i = total - 4; i <= total; i++) {
        range.push(i)
      }
    } else {
      range.push(1)
      range.push('...')
      for (let i = current - 1; i <= current + 1; i++) {
        range.push(i)
      }
      range.push('...')
      range.push(total)
    }
  }
  
  return range
})

// 监听store中的搜索查询变化
watch(searchQuery, (newQuery) => {
  localSearchQuery.value = newQuery
}, { immediate: true })

// 方法
const performSearch = () => {
  articlesStore.searchArticles(localSearchQuery.value)
  currentPage.value = 1
}

const handleInput = () => {
  performSearch()
}

const clearSearch = () => {
  localSearchQuery.value = ''
  articlesStore.searchArticles('')
  selectedCategory.value = ''
  timeRange.value = ''
  currentPage.value = 1
}

const quickSearch = (query: string) => {
  localSearchQuery.value = query
  performSearch()
}

const applyFilters = () => {
  currentPage.value = 1
}

const applySorting = () => {
  currentPage.value = 1
}

const toggleViewMode = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const goToPage = (page: number | string) => {
  if (typeof page === 'number' && page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const navigateToArticle = (id: number | undefined) => {
  if (id) {
    router.push(`/article/${id}`)
  }
}

const getRelevanceScore = (article: any): number => {
  if (!searchQuery.value) return 0
  
  const query = searchQuery.value.toLowerCase()
  const title = (article.title || '').toLowerCase()
  const content = (article.content || '').toLowerCase()
  const summary = (article.summary || '').toLowerCase()
  
  let score = 0
  
  // 标题匹配权重最高
  if (title.includes(query)) {
    score += 60
  }
  
  // 摘要匹配
  if (summary.includes(query)) {
    score += 30
  }
  
  // 内容匹配
  if (content.includes(query)) {
    score += 10
  }
  
  return Math.min(score, 100)
}

const highlightText = (text: string): string => {
  if (!searchQuery.value || !text) return text
  
  const regex = new RegExp(`(${searchQuery.value})`, 'gi')
  return text.replace(regex, '<mark class="search-highlight">$1</mark>')
}

const truncateContent = (content: string, maxLength: number): string => {
  if (!content || content.length <= maxLength) return content || ''
  return content.substring(0, maxLength) + '...'
}

const formatNumber = (num: number | undefined): string => {
  if (num === undefined || num === null || isNaN(num)) return '0'
  
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

const formatDate = (dateString: string | undefined): string => {
  if (!dateString) return '未知日期'
  
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return '无效日期'
    
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'numeric',
      day: 'numeric'
    })
  } catch (error) {
    console.error('日期格式化错误:', error)
    return '日期错误'
  }
}

onMounted(() => {
  articlesStore.initializeData()
})
</script>

<style scoped>
.luxury-search-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a2e 50%, #16213e 100%);
  color: white;
}

/* 搜索英雄区域 */
.search-hero {
  position: relative;
  padding: 4rem 2rem;
  text-align: center;
  overflow: hidden;
}

.search-particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(255, 215, 0, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(255, 215, 0, 0.08) 0%, transparent 50%);
  animation: float 15s ease-in-out infinite;
}

.search-hero-content {
  position: relative;
  z-index: 10;
  max-width: 800px;
  margin: 0 auto;
}

.search-icon-wrapper {
  margin-bottom: 2rem;
}

.search-icon-circle {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  box-shadow: 0 10px 30px rgba(255, 215, 0, 0.3);
}

.search-icon {
  width: 36px;
  height: 36px;
  color: #1a1a2e;
}

.search-title {
  font-size: 3rem;
  font-weight: 700;
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 1rem;
}

.search-subtitle {
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 3rem;
}

/* 增强搜索框 */
.enhanced-search-container {
  margin-bottom: 3rem;
}

.search-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 215, 0, 0.2);
  border-radius: 50px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.search-input-wrapper:focus-within {
  border-color: rgba(255, 215, 0, 0.5);
  box-shadow: 0 0 30px rgba(255, 215, 0, 0.2);
}

.search-input-icon {
  padding: 0 1.5rem;
  color: rgba(255, 255, 255, 0.6);
}

.enhanced-search-input {
  flex: 1;
  background: transparent;
  border: none;
  padding: 1rem 0;
  color: white;
  font-size: 1rem;
  outline: none;
}

.enhanced-search-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.clear-search-btn {
  padding: 0.5rem;
  color: rgba(255, 255, 255, 0.6);
  background: none;
  border: none;
  cursor: pointer;
  transition: color 0.3s ease;
}

.clear-search-btn:hover {
  color: white;
}

.search-submit-btn {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #1a1a2e;
  border: none;
  padding: 1rem 2rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-submit-btn:hover {
  background: linear-gradient(135deg, #ffed4e, #ffd700);
  transform: translateX(-2px);
}

/* 搜索筛选器 */
.search-filters {
  display: flex;
  gap: 2rem;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 2rem;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  min-width: 150px;
}

.filter-label {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
}

.filter-select {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  padding: 0.5rem 1rem;
  color: white;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-select:focus {
  outline: none;
  border-color: rgba(255, 215, 0, 0.5);
}

.filter-select option {
  background: #1a1a2e;
  color: white;
}

/* 搜索状态 */
.search-stats {
  margin-bottom: 2rem;
}

.stats-content {
  text-align: center;
}

.stats-main {
  font-size: 1.1rem;
  color: white;
  margin-bottom: 0.5rem;
}

.highlight {
  color: #ffd700;
  font-weight: 600;
}

.stats-details {
  display: flex;
  gap: 2rem;
  justify-content: center;
  flex-wrap: wrap;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.7);
}

.stat-item strong {
  color: #ffd700;
}

.no-search-prompt {
  text-align: center;
}

.prompt-text {
  color: rgba(255, 255, 255, 0.6);
  font-size: 1rem;
}

/* 搜索结果区域 */
.search-results-section {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.results-overview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.results-count {
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
}

.count-number {
  font-size: 2rem;
  font-weight: 700;
  color: #ffd700;
}

.count-label {
  color: rgba(255, 255, 255, 0.8);
}

.results-actions {
  display: flex;
  gap: 0.5rem;
}

.view-toggle-btn {
  padding: 0.5rem;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.3s ease;
}

.view-toggle-btn:hover,
.view-toggle-btn.active {
  background: rgba(255, 215, 0, 0.2);
  border-color: rgba(255, 215, 0, 0.4);
  color: #ffd700;
}

.icon {
  width: 1.2rem;
  height: 1.2rem;
}

/* 结果网格 */
.results-grid {
  display: grid;
  gap: 2rem;
  margin-bottom: 3rem;
}

.results-grid.grid {
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
}

.results-grid.list {
  grid-template-columns: 1fr;
}

.result-card {
  position: relative;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 2rem;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.result-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #ffd700, transparent);
  transform: translateX(-100%);
  transition: transform 0.6s ease;
}

.result-card:hover::before {
  transform: translateX(100%);
}

.result-card:hover {
  transform: translateY(-5px);
  border-color: rgba(255, 215, 0, 0.3);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.result-card.featured {
  border-color: rgba(255, 215, 0, 0.4);
  background: rgba(255, 215, 0, 0.05);
}

.article-rank {
  position: absolute;
  top: -10px;
  right: -10px;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #1a1a2e;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.2rem;
  box-shadow: 0 5px 15px rgba(255, 215, 0, 0.4);
}

.article-category {
  margin-bottom: 1rem;
}

.category-tag {
  background: rgba(255, 215, 0, 0.2);
  color: #ffd700;
  padding: 0.3rem 1rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  border: 1px solid rgba(255, 215, 0, 0.3);
}

.article-title {
  font-size: 1.4rem;
  font-weight: 600;
  color: white;
  margin-bottom: 1rem;
  line-height: 1.4;
}

.article-summary {
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
  margin-bottom: 1.5rem;
  font-size: 0.95rem;
}

.article-meta {
  margin-bottom: 1rem;
}

.meta-group {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.8rem;
}

.meta-icon {
  width: 1rem;
  height: 1rem;
}

.relevance-score {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
}

.score-label {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.7);
  min-width: 50px;
}

.score-bar {
  flex: 1;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  overflow: hidden;
}

.score-fill {
  height: 100%;
  background: linear-gradient(90deg, #ffd700, #ffed4e);
  border-radius: 2px;
  transition: width 1s ease;
}

.score-value {
  font-size: 0.8rem;
  color: #ffd700;
  font-weight: 600;
  min-width: 40px;
}

.read-more-btn {
  position: absolute;
  bottom: 1rem;
  right: 1rem;
  width: 40px;
  height: 40px;
  background: rgba(255, 215, 0, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffd700;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s ease;
}

.result-card:hover .read-more-btn {
  opacity: 1;
  transform: translateX(0);
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 3rem;
}

.pagination-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.8rem 1.5rem;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.pagination-btn:hover:not(:disabled) {
  background: rgba(255, 215, 0, 0.2);
  border-color: rgba(255, 215, 0, 0.4);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-numbers {
  display: flex;
  gap: 0.5rem;
}

.pagination-number {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
}

.pagination-number:hover,
.pagination-number.active {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #1a1a2e;
  border-color: transparent;
}

/* 无结果状态 */
.no-results {
  text-align: center;
  padding: 4rem 2rem;
}

.no-results-content {
  max-width: 600px;
  margin: 0 auto;
}

.no-results-icon {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 2rem;
}

.no-results-icon .icon {
  width: 3rem;
  height: 3rem;
  color: rgba(255, 255, 255, 0.6);
}

.no-results-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: white;
  margin-bottom: 1rem;
}

.no-results-description {
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 2rem;
  font-size: 1rem;
}

.no-results-suggestions,
.suggested-searches {
  margin-bottom: 2rem;
}

.suggestions-title {
  font-size: 1rem;
  font-weight: 600;
  color: white;
  margin-bottom: 1rem;
}

.suggestions-list {
  list-style: none;
  padding: 0;
  text-align: left;
  color: rgba(255, 255, 255, 0.7);
}

.suggestions-list li {
  padding: 0.5rem 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.search-tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  justify-content: center;
}

.search-tag {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 0.5rem 1rem;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.search-tag:hover,
.search-tag.popular {
  background: rgba(255, 215, 0, 0.2);
  border-color: rgba(255, 215, 0, 0.4);
  color: #ffd700;
}

/* 搜索建议 */
.search-suggestions {
  text-align: center;
  padding: 4rem 2rem;
}

.suggestions-content {
  max-width: 800px;
  margin: 0 auto;
}

.suggestions-icon {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.2), rgba(255, 215, 0, 0.1));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 2rem;
  border: 2px solid rgba(255, 215, 0, 0.3);
}

.suggestions-icon .icon {
  width: 3rem;
  height: 3rem;
  color: #ffd700;
}

.suggestions-title {
  font-size: 2rem;
  font-weight: 600;
  color: white;
  margin-bottom: 1rem;
}

.suggestions-description {
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 3rem;
  font-size: 1.1rem;
}

.popular-searches,
.recent-articles {
  margin-bottom: 3rem;
}

.section-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: white;
  margin-bottom: 1.5rem;
}

.recent-list {
  display: grid;
  gap: 1rem;
  max-width: 600px;
  margin: 0 auto;
}

.recent-item {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: left;
}

.recent-item:hover {
  border-color: rgba(255, 215, 0, 0.3);
  background: rgba(255, 215, 0, 0.05);
}

.recent-title {
  color: white;
  font-weight: 500;
  margin-bottom: 0.5rem;
  font-size: 0.95rem;
}

.recent-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.6);
}

.recent-category {
  color: #ffd700;
}

/* 高亮样式 */
:deep(.search-highlight) {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #1a1a2e;
  padding: 0.1rem 0.3rem;
  border-radius: 3px;
  font-weight: 600;
}

/* 动画 */
@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-title {
    font-size: 2rem;
  }
  
  .search-filters {
    flex-direction: column;
    align-items: center;
  }
  
  .results-overview {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
  
  .stats-details {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .results-grid.grid {
    grid-template-columns: 1fr;
  }
  
  .pagination {
    flex-wrap: wrap;
  }
  
  .pagination-numbers {
    order: -1;
  }
}

@media (max-width: 480px) {
  .search-hero {
    padding: 2rem 1rem;
  }
  
  .search-title {
    font-size: 1.5rem;
  }
  
  .enhanced-search-container {
    margin-bottom: 2rem;
  }
  
  .search-input-wrapper {
    flex-direction: column;
    border-radius: 15px;
  }
  
  .search-submit-btn {
    width: 100%;
    border-radius: 0 0 15px 15px;
  }
  
  .search-results-section {
    padding: 1rem;
  }
  
  .result-card {
    padding: 1.5rem;
  }
}
</style> 