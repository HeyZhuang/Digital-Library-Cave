<template>
  <div class="luxury-accessibility-showcase">
    <!-- 奢华英雄区 -->
    <section class="hero-section">
      <div class="gold-particles-bg"></div>
      <div class="hero-content">
        <div class="logo-constellation">
          <div 
            v-for="n in 12" 
            :key="n" 
            :class="['constellation-node', { active: n <= 8 }]"
          ></div>
        </div>
        <h1 class="hero-title">
          <span class="title-line">数字藏金阁</span>
          <span class="subtitle-line">无障碍优化展示</span>
        </h1>
        <p class="hero-description">
          融合古典美学与现代无障碍标准的 
          <span class="highlight-text">WCAG 2.1 AA</span> 级知识平台
        </p>
        <div class="hero-stats">
          <div class="stat-item">
            <div class="stat-number">12.6:1</div>
            <div class="stat-label">对比度</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-number">AAA</div>
            <div class="stat-label">WCAG等级</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-number">100%</div>
            <div class="stat-label">键盘访问</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 精品文章展示区 -->
    <section class="articles-section">
      <div class="section-header">
        <div class="section-icon">📚</div>
        <h2 class="section-title">精选文章</h2>
        <div class="section-subtitle">高质量知识内容推荐</div>
      </div>
      
      <div class="articles-grid">
        <article 
          v-for="article in articles" 
          :key="article.id"
          class="article-card"
          :class="{ 'urgent-glow': article.isUrgent }"
        >
          <div class="article-header">
            <h3 class="article-title">{{ article.title }}</h3>
            <span 
              :class="[
                'time-badge',
                article.isUrgent ? 'urgent' : 'recent'
              ]"
            >
              {{ article.timeAgo }}
            </span>
          </div>
          
          <p class="article-excerpt">{{ article.excerpt }}</p>
          
          <div class="article-tags">
            <span 
              v-for="tag in article.tags" 
              :key="tag"
              class="luxury-tag"
            >
              {{ tag }}
            </span>
          </div>
          
          <div class="article-actions">
            <button class="action-btn secondary">
              <BookmarkIcon class="btn-icon" />
              收藏
            </button>
            <button class="action-btn primary">
              <ArrowRightIcon class="btn-icon" />
              阅读全文
            </button>
          </div>
        </article>
      </div>
    </section>

    <!-- 学习计划管理区 -->
    <section class="planner-section">
      <div class="section-header">
        <div class="section-icon">✅</div>
        <h2 class="section-title">学习计划</h2>
        <div class="section-subtitle">智能任务管理系统</div>
      </div>
      
      <div class="planner-container">
        <div class="task-list">
          <div 
            v-for="todo in todos" 
            :key="todo.id"
            :class="[
              'task-item',
              { 'completed': todo.completed }
            ]"
            @click="toggleTodo(todo)"
            tabindex="0"
            role="checkbox"
            :aria-checked="todo.completed"
            @keyup.enter="toggleTodo(todo)"
            @keyup.space="toggleTodo(todo)"
          >
            <div class="task-checkbox"></div>
            <div class="task-content">
              <div class="task-header">
                <span class="task-title">{{ todo.title }}</span>
                <span class="task-estimate">{{ todo.estimate }}</span>
              </div>
              <div v-if="todo.description" class="task-description">
                {{ todo.description }}
              </div>
            </div>
            <div class="task-indicator"></div>
          </div>
        </div>
        
        <div class="task-input-section">
          <div class="input-group">
            <input 
              v-model="newTodoTitle"
              type="text" 
              placeholder="添加新的学习任务..."
              class="luxury-input"
              @keyup.enter="addTodo"
            />
            <button 
              @click="addTodo"
              class="add-task-btn"
              :disabled="!newTodoTitle.trim()"
            >
              <PlusIcon class="btn-icon" />
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- 古籍智慧区 -->
    <section class="wisdom-section">
      <div class="scroll-container">
        <div class="wisdom-header">
          <h2 class="wisdom-title">📜 古籍智慧</h2>
          <div class="golden-line"></div>
        </div>
        
        <blockquote class="wisdom-quote">
          <div class="quote-content">
            "学而时习之，不亦说乎？有朋自远方来，不亦乐乎？人不知而不愠，不亦君子乎？"
          </div>
          <footer class="quote-author">—— 孔子《论语》</footer>
        </blockquote>
        
        <div class="wisdom-content">
          <p class="wisdom-paragraph">
            在这个数字化的时代，我们将古老的智慧与现代的技术相结合，
            创造出既保持传统文化内涵，又符合现代无障碍标准的知识平台。
          </p>
          
          <p class="wisdom-paragraph">
            每一个设计元素都经过精心考虑，确保所有用户都能平等地获取知识，
            无论他们的能力如何，都能在这座数字藏金阁中找到属于自己的宝藏。
          </p>
        </div>
      </div>
    </section>

    <!-- 交互元素展示区 -->
    <section class="interaction-section">
      <div class="section-header">
        <div class="section-icon">🎨</div>
        <h2 class="section-title">交互元素</h2>
        <div class="section-subtitle">高端用户界面组件</div>
      </div>
      
      <div class="interaction-grid">
        <div class="button-group">
          <h4 class="group-title">主要操作</h4>
          <div class="button-list">
            <button class="luxury-btn primary">
              <BookOpenIcon class="btn-icon" />
              开始学习
            </button>
            <button class="luxury-btn secondary">
              <FolderIcon class="btn-icon" />
              浏览文档
            </button>
            <button class="luxury-btn danger">
              <TrashIcon class="btn-icon" />
              删除项目
            </button>
          </div>
        </div>
        
        <div class="button-group">
          <h4 class="group-title">古籍风格</h4>
          <div class="button-list">
            <button class="ancient-btn">
              <span class="btn-emoji">📖</span>
              阅览典籍
            </button>
            <button class="ancient-btn">
              <span class="btn-emoji">✍️</span>
              撰写心得
            </button>
            <button class="ancient-btn">
              <span class="btn-emoji">🔍</span>
              搜索古籍
            </button>
          </div>
        </div>
        
        <div class="button-group">
          <h4 class="group-title">状态标签</h4>
          <div class="status-tags">
            <div class="status-tag urgent">🔥 紧急</div>
            <div class="status-tag recent">⏰ 最近更新</div>
            <div class="status-tag normal">📅 昨天</div>
            <div class="status-tag completed">✅ 已完成</div>
            <div class="status-tag learning">📚 学习中</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 无障碍特性说明 -->
    <section class="accessibility-section">
      <div class="section-header">
        <div class="section-icon">♿</div>
        <h2 class="section-title">无障碍特性</h2>
        <div class="section-subtitle">WCAG 2.1 合规性展示</div>
      </div>
      
      <div class="features-grid">
        <div class="feature-category">
          <h4 class="category-title">视觉优化</h4>
          <ul class="feature-list">
            <li class="feature-item">
              <span class="feature-check">✓</span>
              <span class="feature-text">文本对比度达到WCAG AA标准(4.5:1)</span>
            </li>
            <li class="feature-item">
              <span class="feature-check">✓</span>
              <span class="feature-text">增加行高至1.7提升可读性</span>
            </li>
            <li class="feature-item">
              <span class="feature-check">✓</span>
              <span class="feature-text">按钮具有明确的视觉焦点指示</span>
            </li>
            <li class="feature-item">
              <span class="feature-check">✓</span>
              <span class="feature-text">支持高对比度和暗黑模式</span>
            </li>
          </ul>
        </div>
        
        <div class="feature-category">
          <h4 class="category-title">交互优化</h4>
          <ul class="feature-list">
            <li class="feature-item">
              <span class="feature-check">✓</span>
              <span class="feature-text">键盘导航支持(Tab, Enter, Space)</span>
            </li>
            <li class="feature-item">
              <span class="feature-check">✓</span>
              <span class="feature-text">扩大触摸目标区域(最小44px)</span>
            </li>
            <li class="feature-item">
              <span class="feature-check">✓</span>
              <span class="feature-text">语义化标签和ARIA属性</span>
            </li>
            <li class="feature-item">
              <span class="feature-check">✓</span>
              <span class="feature-text">减少动画偏好支持</span>
            </li>
          </ul>
        </div>
      </div>
    </section>

    <!-- 颜色对比度测试 -->
    <section class="contrast-section">
      <div class="section-header">
        <div class="section-icon">🎨</div>
        <h2 class="section-title">颜色对比度测试</h2>
        <div class="section-subtitle">WCAG标准验证</div>
      </div>
      
      <div class="contrast-grid">
        <div 
          v-for="colorTest in colorTests" 
          :key="colorTest.name"
          class="contrast-card"
          :style="{ 
            backgroundColor: colorTest.bg, 
            color: colorTest.text,
            borderColor: colorTest.border 
          }"
        >
          <h5 class="contrast-title">{{ colorTest.name }}</h5>
          <p class="contrast-description">{{ colorTest.description }}</p>
          <div class="contrast-ratio">
            对比度: {{ colorTest.ratio }} ({{ colorTest.level }})
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { 
  BookmarkIcon, 
  ArrowRightIcon, 
  PlusIcon,
  BookOpenIcon,
  FolderIcon,
  TrashIcon
} from '@heroicons/vue/24/outline'

// 文章数据
const articles = ref([
  {
    id: 1,
    title: 'Vue 3 组合式API深度解析',
    excerpt: '探索Vue 3组合式API的设计理念和最佳实践，学习如何构建更加模块化和可复用的代码。',
    timeAgo: '2小时前',
    isUrgent: true,
    tags: ['Vue', '前端', '教程']
  },
  {
    id: 2,
    title: 'TypeScript高级类型技巧',
    excerpt: '掌握TypeScript的高级类型特性，包括条件类型、映射类型和工具类型的使用方法。',
    timeAgo: '昨天',
    isUrgent: false,
    tags: ['TypeScript', '进阶']
  },
  {
    id: 3,
    title: '无障碍Web设计指南',
    excerpt: '学习如何设计符合WCAG标准的无障碍Web应用，让所有用户都能平等地访问您的网站。',
    timeAgo: '3天前',
    isUrgent: false,
    tags: ['无障碍', '设计', 'WCAG']
  }
])

// 待办事项数据
const todos = ref([
  {
    id: 1,
    title: '完成Vue 3项目重构',
    description: '将现有项目从Vue 2升级到Vue 3，使用组合式API',
    estimate: '2小时',
    completed: true
  },
  {
    id: 2,
    title: '学习TypeScript装饰器',
    description: '深入了解装饰器模式在TypeScript中的应用',
    estimate: '1.5小时',
    completed: false
  },
  {
    id: 3,
    title: '阅读无障碍设计规范',
    description: '研读WCAG 2.1指南，提升产品的可访问性',
    estimate: '3小时',
    completed: false
  }
])

const newTodoTitle = ref('')

// 颜色对比度测试数据
const colorTests = ref([
  {
    name: '主要文本',
    description: '正文内容使用的颜色组合',
    bg: '#f7fafc',
    text: '#1a202c',
    border: '#e2e8f0',
    ratio: '12.6:1',
    level: 'AAA'
  },
  {
    name: '次要文本',
    description: '辅助信息和说明文字',
    bg: '#f7fafc',
    text: '#4a5568',
    border: '#cbd5e0',
    ratio: '7.2:1',
    level: 'AA'
  },
  {
    name: '操作按钮',
    description: '主要操作按钮的颜色方案',
    bg: '#3182ce',
    text: '#ffffff',
    border: '#2c5282',
    ratio: '5.9:1',
    level: 'AA'
  }
])

// 方法
const toggleTodo = (todo: any) => {
  todo.completed = !todo.completed
}

const addTodo = () => {
  if (newTodoTitle.value.trim()) {
    todos.value.push({
      id: Date.now(),
      title: newTodoTitle.value.trim(),
      description: '',
      estimate: '1小时',
      completed: false
    })
    newTodoTitle.value = ''
  }
}
</script>

<style scoped>
/* ===== 奢华无障碍展示页面样式 ===== */

.luxury-accessibility-showcase {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f172a, #1e293b, #334155);
  color: #f8f5f0;
  font-family: 'LXGW WenKai', sans-serif;
}

/* 英雄区域 */
.hero-section {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.gold-particles-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(circle at 20% 30%, rgba(212, 175, 55, 0.1) 1px, transparent 1px),
    radial-gradient(circle at 80% 70%, rgba(212, 175, 55, 0.1) 1px, transparent 1px),
    radial-gradient(circle at 40% 60%, rgba(212, 175, 55, 0.05) 1px, transparent 1px);
  background-size: 100px 100px, 150px 150px, 80px 80px;
  animation: float 20s ease-in-out infinite;
}

.hero-content {
  text-align: center;
  z-index: 2;
  max-width: 800px;
  padding: 2rem;
}

.logo-constellation {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 2rem;
}

.constellation-node {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #d4af37;
  opacity: 0.3;
  animation: pulse 2s ease-in-out infinite;
}

.constellation-node.active {
  opacity: 1;
  box-shadow: 0 0 12px rgba(212, 175, 55, 0.6);
}

.hero-title {
  margin-bottom: 1.5rem;
}

.title-line {
  display: block;
  font-size: 4rem;
  font-weight: 700;
  background: linear-gradient(135deg, #d4af37, #f9e076, #d4af37);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  margin-bottom: 0.5rem;
}

.subtitle-line {
  display: block;
  font-size: 1.5rem;
  color: #cbd5e0;
  font-weight: 400;
}

.hero-description {
  font-size: 1.25rem;
  line-height: 1.7;
  color: #e2e8f0;
  margin-bottom: 3rem;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.highlight-text {
  color: #d4af37;
  font-weight: 600;
  background: rgba(212, 175, 55, 0.1);
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
}

.hero-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 2rem;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: #d4af37;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 0.875rem;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: linear-gradient(180deg, transparent, #475569, transparent);
}

/* 通用章节样式 */
section {
  padding: 6rem 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 4rem;
}

.section-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #f8f5f0;
  margin-bottom: 0.5rem;
}

.section-subtitle {
  font-size: 1.125rem;
  color: #94a3b8;
}

/* 精品文章卡片 */
.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 2rem;
}

.article-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(212, 175, 55, 0.2);
  border-radius: 16px;
  padding: 2rem;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.article-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #d4af37, transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.article-card:hover::before {
  opacity: 1;
}

.article-card:hover {
  transform: translateY(-8px);
  border-color: rgba(212, 175, 55, 0.4);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.urgent-glow {
  box-shadow: 0 0 30px rgba(239, 68, 68, 0.3);
}

.article-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
  gap: 1rem;
}

.article-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #f8f5f0;
  line-height: 1.4;
  flex: 1;
}

.time-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
  white-space: nowrap;
}

.time-badge.urgent {
  background: rgba(239, 68, 68, 0.2);
  color: #fca5a5;
  border: 1px solid rgba(239, 68, 68, 0.3);
}

.time-badge.recent {
  background: rgba(56, 178, 172, 0.2);
  color: #7dd3fc;
  border: 1px solid rgba(56, 178, 172, 0.3);
}

.article-excerpt {
  color: #cbd5e0;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.luxury-tag {
  padding: 0.25rem 0.75rem;
  background: rgba(212, 175, 55, 0.1);
  color: #d4af37;
  border: 1px solid rgba(212, 175, 55, 0.2);
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 500;
}

.article-actions {
  display: flex;
  gap: 1rem;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  min-height: 44px;
}

.action-btn.primary {
  background: #3182ce;
  color: white;
}

.action-btn.primary:hover {
  background: #2c5282;
  transform: translateY(-2px);
}

.action-btn.secondary {
  background: rgba(255, 255, 255, 0.1);
  color: #e2e8f0;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.action-btn.secondary:hover {
  background: rgba(255, 255, 255, 0.2);
}

.btn-icon {
  width: 1rem;
  height: 1rem;
}

/* 学习计划器 */
.planner-container {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(212, 175, 55, 0.2);
  border-radius: 16px;
  padding: 2rem;
}

.task-list {
  margin-bottom: 2rem;
}

.task-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  margin-bottom: 0.75rem;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  min-height: 60px;
}

.task-item:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(212, 175, 55, 0.3);
}

.task-item:focus {
  outline: 2px solid #d4af37;
  outline-offset: 2px;
}

.task-item.completed {
  opacity: 0.7;
  background: rgba(34, 197, 94, 0.1);
  border-color: rgba(34, 197, 94, 0.3);
}

.task-checkbox {
  width: 20px;
  height: 20px;
  border: 2px solid #94a3b8;
  border-radius: 4px;
  position: relative;
  transition: all 0.2s ease;
}

.task-item.completed .task-checkbox {
  background: #22c55e;
  border-color: #22c55e;
}

.task-item.completed .task-checkbox::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 0.75rem;
  font-weight: bold;
}

.task-content {
  flex: 1;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.25rem;
}

.task-title {
  font-weight: 500;
  color: #f8f5f0;
}

.task-estimate {
  font-size: 0.75rem;
  color: #94a3b8;
}

.task-description {
  font-size: 0.875rem;
  color: #cbd5e0;
  line-height: 1.4;
}

.task-indicator {
  width: 4px;
  height: 40px;
  background: linear-gradient(180deg, #d4af37, #f9e076);
  border-radius: 2px;
}

.task-input-section {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 1.5rem;
}

.input-group {
  display: flex;
  gap: 1rem;
}

.luxury-input {
  flex: 1;
  padding: 0.75rem 1rem;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: #f8f5f0;
  font-size: 1rem;
  min-height: 44px;
}

.luxury-input:focus {
  outline: 2px solid #d4af37;
  outline-offset: 2px;
  border-color: #d4af37;
}

.luxury-input::placeholder {
  color: #94a3b8;
}

.add-task-btn {
  padding: 0.75rem;
  background: #d4af37;
  color: #0f172a;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 44px;
  min-height: 44px;
}

.add-task-btn:hover {
  background: #f9e076;
  transform: scale(1.05);
}

.add-task-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

/* 古籍智慧区 */
.wisdom-section {
  background: linear-gradient(135deg, rgba(30, 41, 59, 0.5), rgba(15, 23, 42, 0.5));
  border-radius: 20px;
  overflow: hidden;
}

.scroll-container {
  padding: 3rem;
  position: relative;
}

.scroll-container::before,
.scroll-container::after {
  content: '';
  position: absolute;
  top: 1rem;
  width: 1rem;
  height: calc(100% - 2rem);
  background: linear-gradient(180deg, #d4af37, #f9e076, #d4af37);
  border-radius: 0.5rem;
}

.scroll-container::before {
  left: 1rem;
}

.scroll-container::after {
  right: 1rem;
}

.wisdom-header {
  text-align: center;
  margin-bottom: 2rem;
}

.wisdom-title {
  font-size: 2rem;
  font-weight: 700;
  color: #f8f5f0;
  margin-bottom: 1rem;
}

.golden-line {
  width: 100px;
  height: 2px;
  background: linear-gradient(90deg, transparent, #d4af37, transparent);
  margin: 0 auto;
}

.wisdom-quote {
  text-align: center;
  margin: 3rem 0;
  font-style: italic;
}

.quote-content {
  font-size: 1.5rem;
  line-height: 1.6;
  color: #e2e8f0;
  margin-bottom: 1rem;
  border-left: 4px solid #d4af37;
  padding-left: 2rem;
  text-align: left;
  display: inline-block;
}

.quote-author {
  font-size: 1rem;
  color: #94a3b8;
  font-style: normal;
}

.wisdom-content {
  max-width: 800px;
  margin: 0 auto;
}

.wisdom-paragraph {
  font-size: 1.125rem;
  line-height: 1.7;
  color: #e2e8f0;
  margin-bottom: 1.5rem;
  text-align: justify;
}

/* 交互元素展示 */
.interaction-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 3rem;
}

.button-group {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(212, 175, 55, 0.2);
  border-radius: 16px;
  padding: 2rem;
}

.group-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #f8f5f0;
  margin-bottom: 1.5rem;
  text-align: center;
}

.button-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.luxury-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  min-height: 44px;
  width: 100%;
}

.luxury-btn.primary {
  background: #3182ce;
  color: white;
}

.luxury-btn.primary:hover {
  background: #2c5282;
  transform: translateY(-2px);
}

.luxury-btn.secondary {
  background: rgba(226, 232, 240, 0.1);
  color: #e2e8f0;
  border: 1px solid rgba(226, 232, 240, 0.2);
}

.luxury-btn.secondary:hover {
  background: rgba(226, 232, 240, 0.2);
}

.luxury-btn.danger {
  background: #e53e3e;
  color: white;
}

.luxury-btn.danger:hover {
  background: #c53030;
}

.ancient-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.08), rgba(248, 250, 252, 0.05));
  border: 2px solid rgba(212, 175, 55, 0.3);
  border-radius: 12px;
  color: #f8f5f0;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  min-height: 44px;
  width: 100%;
}

.ancient-btn:hover {
  border-color: rgba(212, 175, 55, 0.6);
  box-shadow: 0 4px 20px rgba(212, 175, 55, 0.2);
  transform: translateY(-2px);
}

.btn-emoji {
  font-size: 1.125rem;
}

.status-tags {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.status-tag {
  padding: 0.75rem 1rem;
  border-radius: 8px;
  font-weight: 500;
  text-align: center;
  border: 1px solid;
}

.status-tag.urgent {
  background: rgba(239, 68, 68, 0.1);
  color: #fca5a5;
  border-color: rgba(239, 68, 68, 0.3);
}

.status-tag.recent {
  background: rgba(56, 178, 172, 0.1);
  color: #7dd3fc;
  border-color: rgba(56, 178, 172, 0.3);
}

.status-tag.normal {
  background: rgba(148, 163, 184, 0.1);
  color: #cbd5e0;
  border-color: rgba(148, 163, 184, 0.3);
}

.status-tag.completed {
  background: rgba(34, 197, 94, 0.1);
  color: #86efac;
  border-color: rgba(34, 197, 94, 0.3);
}

.status-tag.learning {
  background: rgba(59, 130, 246, 0.1);
  color: #93c5fd;
  border-color: rgba(59, 130, 246, 0.3);
}

/* 无障碍特性 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 3rem;
}

.feature-category {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(212, 175, 55, 0.2);
  border-radius: 16px;
  padding: 2rem;
}

.category-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #f8f5f0;
  margin-bottom: 1.5rem;
  text-align: center;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  margin-bottom: 1rem;
  padding: 0.75rem;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 8px;
}

.feature-check {
  color: #22c55e;
  font-weight: bold;
  font-size: 1.125rem;
  flex-shrink: 0;
}

.feature-text {
  color: #e2e8f0;
  line-height: 1.5;
}

/* 对比度测试 */
.contrast-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.contrast-card {
  padding: 2rem;
  border-radius: 12px;
  border: 2px solid;
  text-align: center;
  transition: transform 0.2s ease;
}

.contrast-card:hover {
  transform: scale(1.02);
}

.contrast-title {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.contrast-description {
  font-size: 0.875rem;
  margin-bottom: 1rem;
  opacity: 0.8;
}

.contrast-ratio {
  font-size: 0.75rem;
  font-weight: 500;
  opacity: 0.7;
}

/* 动画 */
@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .title-line {
    font-size: 2.5rem;
  }
  
  .hero-stats {
    flex-direction: column;
    gap: 1rem;
  }
  
  .stat-divider {
    width: 40px;
    height: 1px;
  }
  
  .articles-grid {
    grid-template-columns: 1fr;
  }
  
  .interaction-grid {
    grid-template-columns: 1fr;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .article-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .input-group {
    flex-direction: column;
  }
  
  section {
    padding: 4rem 1rem;
  }
}

/* 高对比度模式 */
@media (prefers-contrast: high) {
  .article-card,
  .planner-container,
  .button-group,
  .feature-category {
    border-width: 2px;
    background: rgba(255, 255, 255, 0.1);
  }
  
  .luxury-btn,
  .action-btn,
  .ancient-btn {
    border: 2px solid currentColor;
  }
}

/* 减少动画 */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
  
  .article-card:hover,
  .luxury-btn:hover,
  .action-btn:hover {
    transform: none;
  }
}

/* 焦点样式增强 */
*:focus {
  outline: 2px solid #d4af37;
  outline-offset: 2px;
}

/* 确保最小触摸目标 */
button,
[role="button"],
[tabindex="0"] {
  min-height: 44px;
  min-width: 44px;
}
</style> 