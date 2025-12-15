<template>
  <div class="luxury-home-view">
    <!-- 奢华英雄区域 -->
    <section class="hero-section">
      <div class="gold-particles-bg"></div>
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <div class="constellation-logo">
          <div v-for="n in 8" :key="n" class="constellation-star" :style="{ animationDelay: n * 0.2 + 's' }"></div>
        </div>
        <h1 class="hero-title">
          <span class="title-main">陈壮壮的数字藏金阁</span>
          <span class="title-subtitle">Chen Zhuangzhuang's Digital Treasury</span>
        </h1>
        <p class="hero-motto">
          专注 & 洞察 & 分享
        </p>
        <p class="hero-description">
          个人技术知识库，记录 & 分享个人碎片化、结构化、体系化的技术知识内容
        </p>
        <div class="hero-stats">
          <div class="stat-card">
            <div class="stat-number">{{ stats.totalArticles }}</div>
            <div class="stat-label">技术文章</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ stats.totalViews }}</div>
            <div class="stat-label">总阅读量</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ stats.totalTags }}</div>
            <div class="stat-label">知识标签</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ stats.totalCategories }}</div>
            <div class="stat-label">技术领域</div>
          </div>
        </div>
        <div class="hero-actions">
          <RouterLink to="/articles" class="primary-btn">
            <span>探索知识</span>
            <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6"></path>
            </svg>
          </RouterLink>
          <RouterLink v-if="authStore.isAuthenticated" to="/article/edit" class="secondary-btn">
            <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
            </svg>
            <span>创作文章</span>
          </RouterLink>
        </div>
        
        <!-- 快捷功能导航 -->
        <div class="quick-nav-section" v-if="authStore.isAuthenticated">
          <h3 class="quick-nav-title">快捷功能</h3>
          <div class="quick-nav-grid">
            <RouterLink to="/articles" class="quick-nav-item">
              <div class="nav-icon articles">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
                </svg>
              </div>
              <span class="nav-label">文章管理</span>
            </RouterLink>
            
            <RouterLink to="/categories" class="quick-nav-item">
              <div class="nav-icon categories">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"></path>
                </svg>
              </div>
              <span class="nav-label">分类管理</span>
            </RouterLink>
            
            <RouterLink to="/tags" class="quick-nav-item">
              <div class="nav-icon tags">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"></path>
                </svg>
              </div>
              <span class="nav-label">标签管理</span>
            </RouterLink>
            
            <RouterLink to="/workshop" class="quick-nav-item">
              <div class="nav-icon workshop">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                </svg>
              </div>
              <span class="nav-label">创作工坊</span>
            </RouterLink>
            
            <RouterLink to="/search" class="quick-nav-item">
              <div class="nav-icon search">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                </svg>
              </div>
              <span class="nav-label">搜索探索</span>
            </RouterLink>
            
            <RouterLink to="/article/edit" class="quick-nav-item">
              <div class="nav-icon create">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                </svg>
              </div>
              <span class="nav-label">新建文章</span>
            </RouterLink>
          </div>
        </div>
      </div>
    </section>

    <!-- 实时数据控制面板 -->
    <section v-if="authStore.isAuthenticated" class="realtime-control-panel">
      <div class="control-panel-container">
        <div class="panel-header">
          <h3 class="panel-title">📊 实时数据控制中心</h3>
          <div class="panel-status">
            <div class="status-indicator" :class="{ 'active': !isLoading, 'loading': isLoading }"></div>
            <span class="status-text">{{ isLoading ? '数据更新中...' : '数据已同步' }}</span>
          </div>
        </div>
        
        <div class="panel-content">
          <!-- 数据统计 -->
          <div class="data-stats">
            <div class="stat-item">
              <span class="stat-label">最后更新</span>
              <span class="stat-value">{{ lastRefreshTime ? formatTimeAgo(lastRefreshTime) : '未知' }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">自动刷新</span>
              <span class="stat-value" :class="{ 'enabled': autoRefreshEnabled, 'disabled': !autoRefreshEnabled }">
                {{ autoRefreshEnabled ? '已启用' : '已禁用' }}
              </span>
            </div>
            <div class="stat-item">
              <span class="stat-label">数据状态</span>
              <span class="stat-value" :class="{ 'fresh': !needsRefresh, 'stale': needsRefresh }">
                {{ needsRefresh ? '需要刷新' : '数据新鲜' }}
              </span>
            </div>
          </div>
          
          <!-- 控制按钮 -->
          <div class="control-buttons">
            <button @click="refreshAllData" :disabled="isLoading" class="control-btn refresh">
              <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
              </svg>
              刷新数据
            </button>
            
            <button @click="toggleAutoRefresh" class="control-btn toggle">
              <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
              {{ autoRefreshEnabled ? '停止自动刷新' : '启动自动刷新' }}
            </button>
            
            <button @click="simulateDataChange" class="control-btn simulate">
              <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
              </svg>
              模拟数据变化
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- 动态知识分类展示 -->
    <section class="knowledge-categories-section">
      <div class="section-header">
        <h2 class="section-title">知识分类统计</h2>
        <div class="golden-divider"></div>
        <p class="section-subtitle">实时更新的技术分类和学习进度</p>
      </div>
      
      <div class="categories-loading" v-if="isLoading">
        <div class="loading-spinner"></div>
        <p>正在加载分类数据...</p>
      </div>
      
      <div v-else class="categories-grid">
        <div 
          v-for="category in knowledgeCategories" 
          :key="category.id" 
          class="category-card"
          :class="category.color"
        >
          <!-- 分类图标和标题 -->
          <div class="category-header">
            <div class="category-icon">{{ category.icon || '📚' }}</div>
            <h3 class="category-name">{{ category.name }}</h3>
          </div>
          
          <!-- 统计数据 -->
          <div class="category-stats">
            <div class="stat-row">
              <span class="stat-label">文章数量</span>
              <span class="stat-number">{{ category.articleCount }}</span>
            </div>
            <div class="stat-row">
              <span class="stat-label">总阅读量</span>
              <span class="stat-number">{{ formatNumber(category.totalViews || 0) }}</span>
            </div>
            <div class="stat-row">
              <span class="stat-label">完成进度</span>
              <div class="progress-container">
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: category.progress + '%' }"></div>
                </div>
                <span class="progress-text">{{ category.progress }}%</span>
              </div>
            </div>
          </div>
          
          <!-- 热门主题标签 -->
          <div class="category-topics">
            <span 
              v-for="topic in category.topics.slice(0, 4)" 
              :key="topic" 
              class="topic-tag"
            >
              {{ topic }}
            </span>
          </div>
          
          <!-- 最后更新时间 -->
          <div class="category-footer">
            <span class="update-time">
              {{ category.lastUpdated ? formatTimeAgo(new Date(category.lastUpdated)) : '最近更新' }}
            </span>
          </div>
        </div>
      </div>
      
      <!-- 分类操作 -->
      <div class="categories-actions">
        <RouterLink to="/categories" class="action-btn primary">
          <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
          </svg>
          管理分类
        </RouterLink>
        
        <button @click="refreshAllData" :disabled="isLoading" class="action-btn secondary">
          <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
          </svg>
          刷新统计
        </button>
      </div>
    </section>

    <!-- 核心理念区域 -->
    <section class="philosophy-section">
      <div class="section-header">
        <h2 class="section-title">核心理念</h2>
        <div class="golden-divider"></div>
        <p class="section-subtitle">以匠心雕琢知识，用技艺传承智慧</p>
      </div>
      
      <div class="philosophy-grid">
        <div class="philosophy-card focus">
          <div class="card-icon">
            <div class="icon-circle green">
              <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
          </div>
          <h3 class="card-title">保持专注</h3>
          <p class="card-quote">
            "简单比复杂更难，你必须努力让你的想法变得清晰明了，让它变得简单，你就能撼动大山。"
          </p>
          <div class="quote-author">—— 史蒂夫·乔布斯</div>
          <div class="card-content">
            在信息爆炸的时代，专注成为稀缺品质。深入钻研技术本质，拒绝浮躁，追求精益求精的工匠精神。
          </div>
        </div>

        <div class="philosophy-card insight">
          <div class="card-icon">
            <div class="icon-circle blue">
              <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
              </svg>
            </div>
          </div>
          <h3 class="card-title">善于洞察</h3>
          <p class="card-quote">
            "我现在没有突出的理解力，也没有过人的机智，只在觉察那些稍纵即逝的事物并对其进行精细观察的能力上，我可能在普通人之上。"
          </p>
          <div class="quote-author">—— 查尔斯·达尔文</div>
          <div class="card-content">
            技术的本质在于洞察规律，发现问题背后的本质。保持好奇心，用批判性思维审视每一个技术细节。
          </div>
        </div>

        <div class="philosophy-card share">
          <div class="card-icon">
            <div class="icon-circle purple">
              <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.367 2.684 3 3 0 00-5.367-2.684z"></path>
              </svg>
            </div>
          </div>
          <h3 class="card-title">乐于分享</h3>
          <p class="card-quote">
            "关于分享，有形的物品越分越少，无形的知识越分越多。"
          </p>
          <div class="quote-author">—— 个人感悟</div>
          <div class="card-content">
            在记录与分享的过程中，梳理所学，交流所得，必有所获。知识的价值在于传播和应用。
          </div>
        </div>
      </div>
    </section>

    <!-- 技术栈展示 -->
    <section class="tech-stack-section">
      <div class="section-header">
        <h2 class="section-title">技术栈</h2>
        <div class="golden-divider"></div>
        <p class="section-subtitle">深耕前端技术，拥抱新技术趋势</p>
      </div>
      
      <div class="tech-categories">
        <div class="tech-category">
          <h3 class="category-title">前端框架</h3>
          <div class="tech-items">
            <div class="tech-item vue" data-tech="Vue 3">
              <div class="tech-icon">V</div>
              <span class="tech-name">Vue 3</span>
            </div>
            <div class="tech-item react" data-tech="React">
              <div class="tech-icon">R</div>
              <span class="tech-name">React</span>
            </div>
            <div class="tech-item angular" data-tech="Angular">
              <div class="tech-icon">A</div>
              <span class="tech-name">Angular</span>
            </div>
          </div>
        </div>
        
        <div class="tech-category">
          <h3 class="category-title">开发语言</h3>
          <div class="tech-items">
            <div class="tech-item typescript" data-tech="TypeScript">
              <div class="tech-icon">TS</div>
              <span class="tech-name">TypeScript</span>
            </div>
            <div class="tech-item javascript" data-tech="JavaScript">
              <div class="tech-icon">JS</div>
              <span class="tech-name">JavaScript</span>
            </div>
            <div class="tech-item java" data-tech="Java">
              <div class="tech-icon">J</div>
              <span class="tech-name">Java</span>
            </div>
          </div>
        </div>
        
        <div class="tech-category">
          <h3 class="category-title">工程化工具</h3>
          <div class="tech-items">
            <div class="tech-item vite" data-tech="Vite">
              <div class="tech-icon">V</div>
              <span class="tech-name">Vite</span>
            </div>
            <div class="tech-item webpack" data-tech="Webpack">
              <div class="tech-icon">W</div>
              <span class="tech-name">Webpack</span>
            </div>
            <div class="tech-item docker" data-tech="Docker">
              <div class="tech-icon">D</div>
              <span class="tech-name">Docker</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 最新文章 -->
    <section class="latest-articles-section">
      <div class="section-header">
        <h2 class="section-title">最新文章</h2>
        <div class="golden-divider"></div>
        <p class="section-subtitle">记录技术成长历程，分享实战经验心得</p>
      </div>

      <div class="articles-grid">
        <ArticleCard
          v-for="article in latestArticles"
          :key="article.id"
          :article="article"
          class="enhanced-article-card"
        />
      </div>
      
      <div class="articles-actions">
        <RouterLink to="/articles" class="view-all-btn">
          <span>查看全部文章</span>
          <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
          </svg>
        </RouterLink>
      </div>
    </section>

    <!-- 最新动态时间线 -->
    <section class="timeline-section">
      <div class="section-header">
        <h2 class="section-title">最新动态</h2>
        <div class="golden-divider"></div>
        <p class="section-subtitle">记录学习历程和技术思考</p>
      </div>
      
      <div class="timeline">
        <div v-for="(activity, index) in recentActivities" :key="index" class="timeline-item">
          <div class="timeline-marker"></div>
          <div class="timeline-content">
            <div class="activity-time">{{ activity.time }}</div>
            <div class="activity-title">{{ activity.title }}</div>
            <div class="activity-description">{{ activity.description }}</div>
            <div class="activity-tags">
              <span v-for="tag in activity.tags" :key="tag" class="activity-tag">{{ tag }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 知识图谱 -->
    <section class="knowledge-map-section">
      <div class="section-header">
        <h2 class="section-title">知识图谱</h2>
        <div class="golden-divider"></div>
        <p class="section-subtitle">构建系统化的技术知识体系</p>
      </div>
      
      <div class="knowledge-categories">
        <div v-for="category in knowledgeCategories" :key="category.name" class="knowledge-category" :class="category.color">
          <div class="category-header">
            <div class="category-icon">{{ category.icon }}</div>
            <h3 class="category-name">{{ category.name }}</h3>
          </div>
          <div class="category-stats">
            <div class="stat">
              <span class="stat-number">{{ category.articleCount }}</span>
              <span class="stat-label">文章</span>
            </div>
            <div class="stat">
              <span class="stat-number">{{ category.progress }}%</span>
              <span class="stat-label">完成度</span>
            </div>
          </div>
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: category.progress + '%' }"></div>
          </div>
          <div class="category-topics">
            <span v-for="topic in category.topics" :key="topic" class="topic-tag">{{ topic }}</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useArticlesStore } from '../stores/articles'
import { useTagsStore } from '../stores/tags'
import { useAuthStore } from '../stores/auth'
import { useRealTimeData } from '../composables/useRealTimeData'
import ArticleCard from '../components/ArticleCard.vue'

const articlesStore = useArticlesStore()
const tagsStore = useTagsStore()
const authStore = useAuthStore()

// 使用实时数据管理
const {
  realtimeStats,
  knowledgeCategories,
  isLoading,
  lastRefreshTime,
  autoRefreshEnabled,
  needsRefresh,
  refreshAllData,
  toggleAutoRefresh,
  triggerDataUpdate,
  simulateDataChange,
  getPopularCategories,
  getActiveCategories,
  formatTimeAgo
} = useRealTimeData()

const latestArticles = computed(() => 
  articlesStore.articles.slice(0, 6)
)

// 统计数据（现在使用实时数据）
const stats = computed(() => realtimeStats.value)

// 最新动态
const recentActivities = ref([
  {
    time: '2 小时前',
    title: '发布新文章：Vue 3 性能优化实战指南',
    description: '深入探讨 Vue 3 应用的性能优化策略，包括组件懒加载、虚拟滚动等技术。',
    tags: ['Vue3', '性能优化', '前端']
  },
  {
    time: '1 天前',
    title: '更新技术栈：引入 Vite 4.0',
    description: '升级项目构建工具到 Vite 4.0，享受更快的开发体验和更好的 TypeScript 支持。',
    tags: ['Vite', '构建工具', '升级']
  },
  {
    time: '3 天前',
    title: '学习笔记：TypeScript 高级类型',
    description: '整理 TypeScript 中条件类型、映射类型等高级特性的使用方法和最佳实践。',
    tags: ['TypeScript', '学习笔记', '高级特性']
  },
  {
    time: '1 周前',
    title: '开源项目：Vue 3 组件库',
    description: '开源了一套基于 Vue 3 + TypeScript 的企业级组件库，包含 50+ 常用组件。',
    tags: ['开源', 'Vue3', '组件库']
  }
])

// 知识分类现在通过useRealTimeData获取
// 删除了静态数据，改为动态数据

// 工具函数
const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}
</script>

<style scoped>
.luxury-home-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a2e 50%, #16213e 100%);
  position: relative;
  overflow-x: hidden;
}

/* 英雄区域样式 */
.hero-section {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  overflow: hidden;
}

.gold-particles-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(255, 215, 0, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 215, 0, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(255, 215, 0, 0.08) 0%, transparent 50%);
  animation: floating 20s ease-in-out infinite;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, rgba(0,0,0,0.3) 0%, rgba(255,215,0,0.05) 100%);
}

.hero-content {
  position: relative;
  z-index: 10;
  text-align: center;
  max-width: 1200px;
  padding: 2rem;
}

.constellation-logo {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto 3rem;
}

.constellation-star {
  position: absolute;
  width: 8px;
  height: 8px;
  background: linear-gradient(45deg, #ffd700, #ffed4e);
  border-radius: 50%;
  box-shadow: 0 0 20px rgba(255, 215, 0, 0.8);
  animation: twinkle 3s ease-in-out infinite;
}

.constellation-star:nth-child(1) { top: 10px; left: 50px; }
.constellation-star:nth-child(2) { top: 30px; left: 20px; }
.constellation-star:nth-child(3) { top: 30px; left: 80px; }
.constellation-star:nth-child(4) { top: 60px; left: 40px; }
.constellation-star:nth-child(5) { top: 60px; left: 70px; }
.constellation-star:nth-child(6) { top: 90px; left: 30px; }
.constellation-star:nth-child(7) { top: 90px; left: 90px; }
.constellation-star:nth-child(8) { top: 110px; left: 60px; }

.hero-title {
  margin-bottom: 2rem;
}

.title-main {
  display: block;
  font-size: 4rem;
  font-weight: 900;
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 50%, #ffd700 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 1rem;
  text-shadow: 0 4px 20px rgba(255, 215, 0, 0.3);
}

.title-subtitle {
  display: block;
  font-size: 1.2rem;
  font-weight: 300;
  color: rgba(255, 255, 255, 0.8);
  letter-spacing: 2px;
  text-transform: uppercase;
}

.hero-motto {
  font-size: 2rem;
  font-weight: 300;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 1.5rem;
  letter-spacing: 1px;
}

.hero-description {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.7);
  max-width: 600px;
  margin: 0 auto 3rem;
  line-height: 1.6;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 2rem;
  margin: 3rem 0;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}

.stat-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 215, 0, 0.2);
  border-radius: 15px;
  padding: 1.5rem;
  text-align: center;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  background: rgba(255, 215, 0, 0.1);
  border-color: rgba(255, 215, 0, 0.4);
}

.stat-number {
  display: block;
  font-size: 2.5rem;
  font-weight: 700;
  color: #ffd700;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.8);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.hero-actions {
  display: flex;
  gap: 1.5rem;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 3rem;
}

.primary-btn, .secondary-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  border-radius: 50px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.primary-btn {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #1a1a2e;
  border: none;
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(255, 215, 0, 0.4);
}

.secondary-btn {
  background: transparent;
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.secondary-btn:hover {
  border-color: #ffd700;
  background: rgba(255, 215, 0, 0.1);
}

.btn-icon {
  width: 1.2rem;
  height: 1.2rem;
}

/* 区域通用样式 */
.section-header {
  text-align: center;
  margin-bottom: 4rem;
  padding: 0 2rem;
}

.section-title {
  font-size: 3rem;
  font-weight: 700;
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 1rem;
}

.golden-divider {
  width: 100px;
  height: 4px;
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  margin: 0 auto 1.5rem;
  border-radius: 2px;
}

.section-subtitle {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.8);
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.6;
}

/* 核心理念区域 */
.philosophy-section {
  padding: 6rem 2rem;
  background: rgba(255, 255, 255, 0.02);
}

.philosophy-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.philosophy-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 2rem;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.philosophy-card::before {
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

.philosophy-card:hover::before {
  transform: translateX(100%);
}

.philosophy-card:hover {
  transform: translateY(-5px);
  border-color: rgba(255, 215, 0, 0.3);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.card-icon {
  margin-bottom: 1.5rem;
}

.icon-circle {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.icon-circle.green {
  background: linear-gradient(135deg, #10b981, #34d399);
}

.icon-circle.blue {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
}

.icon-circle.purple {
  background: linear-gradient(135deg, #8b5cf6, #a78bfa);
}

.icon {
  width: 24px;
  height: 24px;
  color: white;
}

.card-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: white;
  margin-bottom: 1rem;
  text-align: center;
}

.card-quote {
  font-style: italic;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 0.5rem;
  line-height: 1.6;
  font-size: 0.95rem;
}

.quote-author {
  text-align: right;
  color: #ffd700;
  font-size: 0.8rem;
  margin-bottom: 1rem;
}

.card-content {
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.6;
  font-size: 0.9rem;
}

/* 技术栈区域 */
.tech-stack-section {
  padding: 6rem 2rem;
  background: rgba(0, 0, 0, 0.2);
}

.tech-categories {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  max-width: 1000px;
  margin: 0 auto;
}

.tech-category {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  padding: 1.5rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.category-title {
  color: white;
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 1rem;
  text-align: center;
}

.tech-items {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  justify-content: center;
}

.tech-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  transition: all 0.3s ease;
  cursor: pointer;
  min-width: 80px;
}

.tech-item:hover {
  transform: translateY(-3px);
  background: rgba(255, 215, 0, 0.1);
  border: 1px solid rgba(255, 215, 0, 0.3);
}

.tech-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: white;
  font-size: 1rem;
}

.tech-item.vue .tech-icon { background: linear-gradient(135deg, #4fc08d, #42b883); }
.tech-item.react .tech-icon { background: linear-gradient(135deg, #61dafb, #21d4fd); }
.tech-item.angular .tech-icon { background: linear-gradient(135deg, #dd0031, #c3002f); }
.tech-item.typescript .tech-icon { background: linear-gradient(135deg, #3178c6, #235a97); }
.tech-item.javascript .tech-icon { background: linear-gradient(135deg, #f7df1e, #e8c547); }
.tech-item.java .tech-icon { background: linear-gradient(135deg, #ed8b00, #ff9500); }
.tech-item.vite .tech-icon { background: linear-gradient(135deg, #646cff, #747bff); }
.tech-item.webpack .tech-icon { background: linear-gradient(135deg, #8dd6f9, #2d3748); }
.tech-item.docker .tech-icon { background: linear-gradient(135deg, #0db7ed, #2496ed); }

.tech-name {
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.8rem;
  font-weight: 500;
}

/* 最新文章区域 */
.latest-articles-section {
  padding: 6rem 2rem;
  background: rgba(255, 255, 255, 0.02);
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto 3rem;
}

.enhanced-article-card {
  transition: all 0.3s ease;
  border-radius: 15px;
  overflow: hidden;
}

.enhanced-article-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 17, 255, 0.1);
}

.articles-actions {
  text-align: center;
}

.view-all-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #1a1a2e;
  border-radius: 50px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.view-all-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(255, 215, 0, 0.4);
}

/* 时间线区域 */
.timeline-section {
  padding: 6rem 2rem;
  background: rgba(0, 0, 0, 0.2);
}

.timeline {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 20px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, #ffd700, rgba(255, 215, 0, 0.2));
}

.timeline-item {
  position: relative;
  padding-left: 60px;
  margin-bottom: 3rem;
}

.timeline-marker {
  position: absolute;
  left: 12px;
  top: 8px;
  width: 16px;
  height: 16px;
  background: #ffd700;
  border-radius: 50%;
  box-shadow: 0 0 20px rgba(255, 215, 0, 0.6);
}

.timeline-content {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  padding: 1.5rem;
  transition: all 0.3s ease;
}

.timeline-content:hover {
  border-color: rgba(255, 215, 0, 0.3);
  transform: translateX(5px);
}

.activity-time {
  color: #ffd700;
  font-size: 0.8rem;
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.activity-title {
  color: white;
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.activity-description {
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.5;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.activity-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.activity-tag {
  background: rgba(255, 215, 0, 0.1);
  color: #ffd700;
  padding: 0.2rem 0.8rem;
  border-radius: 15px;
  font-size: 0.7rem;
  border: 1px solid rgba(255, 215, 0, 0.2);
}

/* 知识图谱区域 */
.knowledge-map-section {
  padding: 6rem 2rem;
  background: rgba(255, 255, 255, 0.02);
}

.knowledge-categories {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  max-width: 1000px;
  margin: 0 auto;
}

.knowledge-category {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 1.5rem;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.knowledge-category:hover {
  transform: translateY(-5px);
  border-color: rgba(255, 215, 0, 0.3);
}

.category-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.category-icon {
  font-size: 2rem;
}

.category-name {
  color: white;
  font-size: 1.2rem;
  font-weight: 600;
}

.category-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.stat {
  text-align: center;
}

.stat-number {
  display: block;
  color: #ffd700;
  font-size: 1.5rem;
  font-weight: 700;
}

.stat-label {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.7rem;
  text-transform: uppercase;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
  margin-bottom: 1rem;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #ffd700, #ffed4e);
  border-radius: 3px;
  transition: width 1s ease;
}

.category-topics {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.topic-tag {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.8);
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-size: 0.7rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 快捷功能导航 */
.quick-nav-section {
  margin-top: 3rem;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 215, 0, 0.1);
  border-radius: 20px;
  padding: 2rem;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}

.quick-nav-title {
  color: #ffd700;
  font-size: 1.5rem;
  font-weight: 600;
  text-align: center;
  margin-bottom: 1.5rem;
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.3);
}

.quick-nav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1.5rem;
}

.quick-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1.5rem 1rem;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  text-decoration: none;
  transition: all 0.3s ease;
  color: rgba(255, 255, 255, 0.8);
  position: relative;
  overflow: hidden;
}

.quick-nav-item:hover {
  transform: translateY(-5px);
  border-color: rgba(255, 215, 0, 0.4);
  background: rgba(255, 215, 0, 0.05);
  color: white;
  box-shadow: 0 10px 30px rgba(255, 215, 0, 0.1);
}

.quick-nav-item:before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 215, 0, 0.1), transparent);
  transition: left 0.5s ease;
}

.quick-nav-item:hover:before {
  left: 100%;
}

.nav-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0.8rem;
  transition: all 0.3s ease;
}

.nav-icon svg {
  width: 24px;
  height: 24px;
}

.nav-icon.articles {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.nav-icon.categories {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.nav-icon.tags {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.nav-icon.workshop {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.nav-icon.search {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.nav-icon.create {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.quick-nav-item:hover .nav-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 8px 25px rgba(255, 215, 0, 0.2);
}

.nav-label {
  font-size: 0.9rem;
  font-weight: 500;
  text-align: center;
  line-height: 1.2;
}

/* 动画效果 */
@keyframes floating {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  33% { transform: translateY(-20px) rotate(1deg); }
  66% { transform: translateY(-10px) rotate(-1deg); }
}

@keyframes twinkle {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.3; transform: scale(0.8); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .title-main {
    font-size: 2.5rem;
  }
  
  .hero-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .quick-nav-section {
    margin-top: 2rem;
    padding: 1.5rem;
  }
  
  .quick-nav-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 1rem;
  }
  
  .quick-nav-item {
    padding: 1rem 0.5rem;
  }
  
  .nav-icon {
    width: 40px;
    height: 40px;
    margin-bottom: 0.5rem;
  }
  
  .nav-icon svg {
    width: 20px;
    height: 20px;
  }
  
  .nav-label {
    font-size: 0.8rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .philosophy-grid {
    grid-template-columns: 1fr;
  }
  
  .timeline::before {
    left: 15px;
  }
  
  .timeline-item {
    padding-left: 50px;
  }
  
  .timeline-marker {
    left: 7px;
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: 2rem 1rem;
  }
  
  .title-main {
    font-size: 2rem;
  }
  
  .hero-stats {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .quick-nav-section {
    margin-top: 1.5rem;
    padding: 1rem;
  }
  
  .quick-nav-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.8rem;
  }
  
  .quick-nav-item {
    padding: 0.8rem 0.3rem;
  }
  
  .nav-icon {
    width: 35px;
    height: 35px;
    margin-bottom: 0.4rem;
  }
  
  .nav-icon svg {
    width: 18px;
    height: 18px;
  }
  
  .nav-label {
    font-size: 0.7rem;
  }
  
  .section-header {
    padding: 0 1rem;
  }
  
  .philosophy-section,
  .tech-stack-section,
  .latest-articles-section,
  .timeline-section,
  .knowledge-map-section {
    padding: 4rem 1rem;
  }
}

/* 实时数据控制面板样式 */
.realtime-control-panel {
  margin: 2rem 0;
  padding: 0 2rem;
}

.control-panel-container {
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(212, 175, 55, 0.2);
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(212, 175, 55, 0.1);
}

.panel-title {
  color: #ffd700;
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0;
}

.panel-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #ef4444;
  animation: pulse 2s infinite;
}

.status-indicator.active {
  background: #10b981;
}

.status-indicator.loading {
  background: #f59e0b;
}

.status-text {
  font-size: 0.9rem;
  color: #e2e8f0;
}

.panel-content {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 2rem;
  align-items: center;
}

.data-stats {
  display: flex;
  gap: 2rem;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.stat-label {
  font-size: 0.8rem;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stat-value {
  font-size: 0.9rem;
  font-weight: 600;
  color: #e2e8f0;
}

.stat-value.enabled {
  color: #10b981;
}

.stat-value.disabled {
  color: #ef4444;
}

.stat-value.fresh {
  color: #10b981;
}

.stat-value.stale {
  color: #f59e0b;
}

.control-buttons {
  display: flex;
  gap: 0.75rem;
}

.control-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: 1px solid rgba(212, 175, 55, 0.3);
  border-radius: 8px;
  background: rgba(212, 175, 55, 0.1);
  color: #ffd700;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.control-btn:hover:not(:disabled) {
  background: rgba(212, 175, 55, 0.2);
  border-color: rgba(212, 175, 55, 0.5);
  transform: translateY(-2px);
}

.control-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.control-btn .btn-icon {
  width: 16px;
  height: 16px;
}

/* 知识分类展示样式 */
.knowledge-categories-section {
  margin: 4rem 0;
  padding: 0 2rem;
}

.categories-loading {
  text-align: center;
  padding: 4rem 0;
  color: #e2e8f0;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(212, 175, 55, 0.3);
  border-top: 3px solid #ffd700;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2rem;
  margin: 2rem 0;
}

.category-card {
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(212, 175, 55, 0.2);
  border-radius: 16px;
  padding: 1.5rem;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.category-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #ffd700, #ffed4e);
  opacity: 0.8;
}

.category-card:hover {
  transform: translateY(-8px);
  border-color: rgba(212, 175, 55, 0.4);
  box-shadow: 0 12px 40px rgba(212, 175, 55, 0.2);
}

.category-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.category-icon {
  font-size: 2rem;
  width: 3rem;
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(212, 175, 55, 0.1);
  border-radius: 12px;
}

.category-name {
  color: #ffd700;
  font-size: 1.3rem;
  font-weight: 600;
  margin: 0;
}

.category-stats {
  margin-bottom: 1.5rem;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.stat-row:last-child {
  margin-bottom: 0;
}

.stat-label {
  color: #94a3b8;
  font-size: 0.9rem;
}

.stat-number {
  color: #e2e8f0;
  font-weight: 600;
  font-size: 1.1rem;
}

.progress-container {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex: 1;
  margin-left: 1rem;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: rgba(30, 41, 59, 0.8);
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #ffd700, #ffed4e);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-text {
  color: #ffd700;
  font-size: 0.85rem;
  font-weight: 600;
  min-width: 2.5rem;
}

.category-topics {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.topic-tag {
  padding: 0.25rem 0.75rem;
  background: rgba(212, 175, 55, 0.15);
  border: 1px solid rgba(212, 175, 55, 0.3);
  border-radius: 12px;
  color: #ffd700;
  font-size: 0.8rem;
  font-weight: 500;
}

.category-footer {
  border-top: 1px solid rgba(212, 175, 55, 0.1);
  padding-top: 1rem;
  text-align: center;
}

.update-time {
  color: #64748b;
  font-size: 0.8rem;
}

.categories-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 3rem;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border-radius: 12px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
}

.action-btn.primary {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #1e293b;
}

.action-btn.primary:hover {
  background: linear-gradient(135deg, #ffed4e, #ffd700);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 215, 0, 0.3);
}

.action-btn.secondary {
  background: rgba(212, 175, 55, 0.1);
  border: 1px solid rgba(212, 175, 55, 0.3);
  color: #ffd700;
}

.action-btn.secondary:hover:not(:disabled) {
  background: rgba(212, 175, 55, 0.2);
  border-color: rgba(212, 175, 55, 0.5);
  transform: translateY(-2px);
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none !important;
}

.action-btn .btn-icon {
  width: 18px;
  height: 18px;
}

/* 动画 */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .realtime-control-panel {
    padding: 0 1rem;
  }

  .control-panel-container {
    padding: 1rem;
  }
  
  .panel-content {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .data-stats {
    flex-direction: column;
    gap: 1rem;
  }
  
  .control-buttons {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .knowledge-categories-section {
    padding: 0 1rem;
  }
  
  .categories-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .categories-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>