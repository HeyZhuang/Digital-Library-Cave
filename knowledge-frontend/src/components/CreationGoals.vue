<template>
  <div class="bg-gradient-to-br from-gray-800 via-gray-900 to-gray-950 rounded-2xl p-6 shadow-lg border border-gray-700 hover:border-cyan-600/50 transition-all duration-300">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-bold text-cyan-300 flex items-center gap-2">
        <svg class="w-6 h-6 text-green-400" fill="currentColor" viewBox="0 0 24 24">
          <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        创作目标
      </h3>
      
      <!-- 设置目标按钮 -->
      <button 
        @click="showGoalModal = true"
        class="px-3 py-1.5 bg-cyan-600/20 hover:bg-cyan-600/30 border border-cyan-600/50 rounded-lg text-sm text-cyan-300 hover:text-cyan-200 transition-all duration-200 flex items-center gap-1"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
        </svg>
        设置目标
      </button>
    </div>

    <!-- 目标列表 -->
    <div v-if="goals.length > 0" class="space-y-4">
      <div 
        v-for="goal in goals" 
        :key="goal.id"
        class="bg-gray-800/30 rounded-xl p-4 border border-gray-700/50 hover:border-gray-600/50 transition-all duration-200"
      >
        <!-- 目标标题和操作 -->
        <div class="flex items-center justify-between mb-3">
          <div>
            <h4 class="text-cyan-200 font-medium">{{ goal.title }}</h4>
            <p class="text-gray-400 text-sm mt-1">{{ goal.description }}</p>
          </div>
          <div class="flex items-center gap-2">
            <!-- 目标状态 -->
            <span 
              :class="goalStatusClass(goal.status)"
              class="px-2 py-1 rounded-full text-xs font-medium"
            >
              {{ goalStatusText(goal.status) }}
            </span>
            
            <!-- 操作按钮 -->
            <div class="flex items-center gap-1">
              <button 
                @click="editGoal(goal)"
                class="p-1 text-gray-400 hover:text-cyan-400 transition-colors"
                title="编辑目标"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
                </svg>
              </button>
              <button 
                @click="deleteGoal(goal.id)"
                class="p-1 text-gray-400 hover:text-red-400 transition-colors"
                title="删除目标"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- 进度条 -->
        <div class="mb-3">
          <div class="flex items-center justify-between text-sm mb-2">
            <span class="text-gray-400">进度</span>
            <span class="text-cyan-200 font-medium">
              {{ goal.current }} / {{ goal.target }} {{ goal.unit }}
              ({{ Math.round(goal.current / goal.target * 100) }}%)
            </span>
          </div>
          
          <div class="w-full bg-gray-700/50 rounded-full h-3 overflow-hidden">
            <div 
              class="h-full rounded-full transition-all duration-500 ease-out"
              :class="progressBarClass(goal.current / goal.target)"
              :style="{ width: `${Math.min(goal.current / goal.target * 100, 100)}%` }"
            ></div>
          </div>
        </div>

        <!-- 目标详情 -->
        <div class="flex items-center justify-between text-sm">
          <div class="flex items-center gap-4">
            <span class="text-gray-400">
              📅 {{ formatDate(goal.startDate) }} - {{ formatDate(goal.endDate) }}
            </span>
            <span class="text-gray-400">
              ⏰ 剩余 {{ getRemainingDays(goal.endDate) }} 天
            </span>
          </div>
          
          <!-- 快速操作 -->
          <div class="flex items-center gap-2">
            <button 
              v-if="goal.status === 'active' && goal.type === 'articles'"
              @click="updateGoalProgress(goal, 1)"
              class="px-2 py-1 bg-green-600/20 hover:bg-green-600/30 border border-green-600/50 rounded text-green-300 hover:text-green-200 transition-all duration-200 text-xs"
            >
              +1 文章
            </button>
            <button 
              v-if="goal.status === 'active' && goal.type === 'words'"
              @click="updateGoalProgress(goal, 500)"
              class="px-2 py-1 bg-blue-600/20 hover:bg-blue-600/30 border border-blue-600/50 rounded text-blue-300 hover:text-blue-200 transition-all duration-200 text-xs"
            >
              +500 字
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-8">
      <div class="w-16 h-16 mx-auto mb-4 bg-gray-700/50 rounded-full flex items-center justify-center">
        <svg class="w-8 h-8 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
      </div>
      <p class="text-gray-400 text-sm">暂无创作目标</p>
      <p class="text-gray-500 text-xs mt-1">设置目标，让创作更有动力</p>
    </div>

    <!-- 目标设置模态框 -->
    <div v-if="showGoalModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50" @click.self="closeGoalModal">
      <div class="bg-gray-900 rounded-2xl p-6 w-full max-w-md mx-4 border border-gray-700 shadow-2xl">
        <h3 class="text-xl font-bold text-cyan-300 mb-4">
          {{ editingGoal ? '编辑目标' : '设置新目标' }}
        </h3>
        
        <form @submit.prevent="saveGoal" class="space-y-4">
          <!-- 目标标题 -->
          <div>
            <label class="block text-sm font-medium text-gray-300 mb-2">目标标题</label>
            <input 
              v-model="goalForm.title"
              type="text" 
              class="w-full px-3 py-2 bg-gray-800 border border-gray-600 rounded-lg text-cyan-100 focus:outline-none focus:border-cyan-500 transition-colors"
              placeholder="输入目标标题"
              required
            >
          </div>

          <!-- 目标描述 -->
          <div>
            <label class="block text-sm font-medium text-gray-300 mb-2">目标描述</label>
            <textarea 
              v-model="goalForm.description"
              class="w-full px-3 py-2 bg-gray-800 border border-gray-600 rounded-lg text-cyan-100 focus:outline-none focus:border-cyan-500 transition-colors resize-none"
              rows="3"
              placeholder="描述你的目标"
            ></textarea>
          </div>

          <!-- 目标类型 -->
          <div>
            <label class="block text-sm font-medium text-gray-300 mb-2">目标类型</label>
            <select 
              v-model="goalForm.type"
              class="w-full px-3 py-2 bg-gray-800 border border-gray-600 rounded-lg text-cyan-100 focus:outline-none focus:border-cyan-500 transition-colors"
            >
              <option value="articles">文章数量</option>
              <option value="words">字数统计</option>
              <option value="views">浏览量</option>
              <option value="custom">自定义</option>
            </select>
          </div>

          <!-- 目标数值 -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-300 mb-2">目标数值</label>
              <input 
                v-model.number="goalForm.target"
                type="number" 
                min="1"
                class="w-full px-3 py-2 bg-gray-800 border border-gray-600 rounded-lg text-cyan-100 focus:outline-none focus:border-cyan-500 transition-colors"
                required
              >
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-300 mb-2">单位</label>
              <input 
                v-model="goalForm.unit"
                type="text" 
                class="w-full px-3 py-2 bg-gray-800 border border-gray-600 rounded-lg text-cyan-100 focus:outline-none focus:border-cyan-500 transition-colors"
                :placeholder="getUnitPlaceholder(goalForm.type)"
                required
              >
            </div>
          </div>

          <!-- 时间范围 -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-300 mb-2">开始日期</label>
              <input 
                v-model="goalForm.startDate"
                type="date" 
                class="w-full px-3 py-2 bg-gray-800 border border-gray-600 rounded-lg text-cyan-100 focus:outline-none focus:border-cyan-500 transition-colors"
                required
              >
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-300 mb-2">结束日期</label>
              <input 
                v-model="goalForm.endDate"
                type="date" 
                class="w-full px-3 py-2 bg-gray-800 border border-gray-600 rounded-lg text-cyan-100 focus:outline-none focus:border-cyan-500 transition-colors"
                required
              >
            </div>
          </div>

          <!-- 按钮组 -->
          <div class="flex gap-3 pt-4">
            <button 
              type="button"
              @click="closeGoalModal"
              class="flex-1 px-4 py-2 bg-gray-700 hover:bg-gray-600 text-gray-300 rounded-lg transition-colors"
            >
              取消
            </button>
            <button 
              type="submit"
              class="flex-1 px-4 py-2 bg-cyan-600 hover:bg-cyan-500 text-white rounded-lg transition-colors"
            >
              {{ editingGoal ? '更新' : '创建' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useArticlesStore } from '../stores/articles'

interface Goal {
  id: string
  title: string
  description: string
  type: 'articles' | 'words' | 'views' | 'custom'
  target: number
  current: number
  unit: string
  startDate: string
  endDate: string
  status: 'active' | 'completed' | 'paused' | 'expired'
  createdAt: string
}

const articlesStore = useArticlesStore()
const goals = ref<Goal[]>([])
const showGoalModal = ref(false)
const editingGoal = ref<Goal | null>(null)

// 目标表单
const goalForm = reactive({
  title: '',
  description: '',
  type: 'articles' as Goal['type'],
  target: 10,
  unit: '篇',
  startDate: '',
  endDate: ''
})

// 重置表单
const resetForm = () => {
  goalForm.title = ''
  goalForm.description = ''
  goalForm.type = 'articles'
  goalForm.target = 10
  goalForm.unit = '篇'
  goalForm.startDate = new Date().toISOString().split('T')[0]
  
  // 默认设置30天后的日期
  const futureDate = new Date()
  futureDate.setDate(futureDate.getDate() + 30)
  goalForm.endDate = futureDate.toISOString().split('T')[0]
}

// 获取单位占位符
const getUnitPlaceholder = (type: string): string => {
  switch (type) {
    case 'articles': return '篇'
    case 'words': return '字'
    case 'views': return '次'
    default: return '个'
  }
}

// 日期格式化
const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    month: 'numeric',
    day: 'numeric'
  })
}

// 获取剩余天数
const getRemainingDays = (endDate: string): number => {
  const today = new Date()
  const end = new Date(endDate)
  const diffTime = end.getTime() - today.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return Math.max(0, diffDays)
}

// 目标状态样式
const goalStatusClass = (status: string): string => {
  switch (status) {
    case 'active': return 'bg-green-600/20 text-green-300 border border-green-600/30'
    case 'completed': return 'bg-blue-600/20 text-blue-300 border border-blue-600/30'
    case 'paused': return 'bg-yellow-600/20 text-yellow-300 border border-yellow-600/30'
    case 'expired': return 'bg-red-600/20 text-red-300 border border-red-600/30'
    default: return 'bg-gray-600/20 text-gray-300 border border-gray-600/30'
  }
}

// 目标状态文本
const goalStatusText = (status: string): string => {
  switch (status) {
    case 'active': return '进行中'
    case 'completed': return '已完成'
    case 'paused': return '已暂停'
    case 'expired': return '已过期'
    default: return '未知'
  }
}

// 进度条样式
const progressBarClass = (progress: number): string => {
  if (progress >= 1) return 'bg-gradient-to-r from-green-500 to-emerald-500'
  if (progress >= 0.8) return 'bg-gradient-to-r from-blue-500 to-cyan-500'
  if (progress >= 0.5) return 'bg-gradient-to-r from-yellow-500 to-orange-500'
  return 'bg-gradient-to-r from-purple-500 to-pink-500'
}

// 计算当前进度
const calculateCurrentProgress = (goal: Goal): number => {
  if (goal.type === 'articles') {
    const startDate = new Date(goal.startDate)
    const endDate = new Date(goal.endDate)
    const articles = articlesStore.articles.filter(article => {
      const articleDate = new Date(article.createdAt || '')
      return articleDate >= startDate && articleDate <= endDate && article.status === 1
    })
    return articles.length
  } else if (goal.type === 'words') {
    const startDate = new Date(goal.startDate)
    const endDate = new Date(goal.endDate)
    const articles = articlesStore.articles.filter(article => {
      const articleDate = new Date(article.createdAt || '')
      return articleDate >= startDate && articleDate <= endDate && article.status === 1
    })
    return articles.reduce((sum, article) => {
      const content = article.content || ''
      const textContent = content.replace(/<[^>]*>/g, '')
      return sum + textContent.length
    }, 0)
  } else if (goal.type === 'views') {
    const startDate = new Date(goal.startDate)
    const endDate = new Date(goal.endDate)
    const articles = articlesStore.articles.filter(article => {
      const articleDate = new Date(article.createdAt || '')
      return articleDate >= startDate && articleDate <= endDate && article.status === 1
    })
    return articles.reduce((sum, article) => sum + (article.views || 0), 0)
  }
  return goal.current
}

// 更新目标状态
const updateGoalStatus = (goal: Goal): Goal => {
  const now = new Date()
  const endDate = new Date(goal.endDate)
  const progress = goal.current / goal.target

  if (progress >= 1) {
    goal.status = 'completed'
  } else if (now > endDate) {
    goal.status = 'expired'
  } else {
    goal.status = 'active'
  }

  return goal
}

// 保存目标
const saveGoal = () => {
  const newGoal: Goal = {
    id: editingGoal.value?.id || `goal-${Date.now()}`,
    title: goalForm.title,
    description: goalForm.description,
    type: goalForm.type,
    target: goalForm.target,
    current: editingGoal.value?.current || 0,
    unit: goalForm.unit,
    startDate: goalForm.startDate,
    endDate: goalForm.endDate,
    status: 'active',
    createdAt: editingGoal.value?.createdAt || new Date().toISOString()
  }

  // 自动计算当前进度
  if (newGoal.type !== 'custom') {
    newGoal.current = calculateCurrentProgress(newGoal)
  }

  // 更新状态
  updateGoalStatus(newGoal)

  if (editingGoal.value) {
    // 更新现有目标
    const index = goals.value.findIndex(g => g.id === editingGoal.value!.id)
    if (index !== -1) {
      goals.value[index] = newGoal
    }
  } else {
    // 添加新目标
    goals.value.push(newGoal)
  }

  // 保存到localStorage
  localStorage.setItem('creationGoals', JSON.stringify(goals.value))
  
  closeGoalModal()
}

// 编辑目标
const editGoal = (goal: Goal) => {
  editingGoal.value = goal
  goalForm.title = goal.title
  goalForm.description = goal.description
  goalForm.type = goal.type
  goalForm.target = goal.target
  goalForm.unit = goal.unit
  goalForm.startDate = goal.startDate
  goalForm.endDate = goal.endDate
  showGoalModal.value = true
}

// 删除目标
const deleteGoal = (goalId: string) => {
  if (confirm('确定要删除这个目标吗？')) {
    goals.value = goals.value.filter(g => g.id !== goalId)
    localStorage.setItem('creationGoals', JSON.stringify(goals.value))
  }
}

// 更新目标进度
const updateGoalProgress = (goal: Goal, increment: number) => {
  goal.current = Math.min(goal.current + increment, goal.target)
  updateGoalStatus(goal)
  localStorage.setItem('creationGoals', JSON.stringify(goals.value))
}

// 关闭模态框
const closeGoalModal = () => {
  showGoalModal.value = false
  editingGoal.value = null
  resetForm()
}

// 刷新所有目标的进度
const refreshGoalsProgress = () => {
  goals.value.forEach(goal => {
    if (goal.type !== 'custom') {
      goal.current = calculateCurrentProgress(goal)
      updateGoalStatus(goal)
    }
  })
  localStorage.setItem('creationGoals', JSON.stringify(goals.value))
}

// 初始化
onMounted(() => {
  // 从localStorage加载目标
  const saved = localStorage.getItem('creationGoals')
  if (saved) {
    goals.value = JSON.parse(saved)
    // 刷新进度
    refreshGoalsProgress()
  }
  
  // 初始化表单默认值
  resetForm()

  // 确保文章数据已加载
  if (articlesStore.articles.length === 0) {
    articlesStore.fetchArticles().then(() => {
      refreshGoalsProgress()
    })
  }
})
</script>

<style scoped>
/* 模态框动画 */
.fixed {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.bg-gray-900 {
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 进度条动画 */
.h-full {
  transition: width 0.5s ease-out;
}

/* 按钮悬浮效果 */
button:hover {
  transform: translateY(-1px);
}

/* 自定义选择框 */
select {
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e");
  background-position: right 8px center;
  background-repeat: no-repeat;
  background-size: 16px;
  padding-right: 32px;
}
</style> 