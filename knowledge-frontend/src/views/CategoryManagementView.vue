<template>
  <div class="category-management">
    <div class="max-w-6xl mx-auto p-6">
      <!-- 页面标题 -->
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-900 mb-2">分类管理</h1>
        <p class="text-gray-700">管理文章分类，包括创建、编辑和删除分类</p>
      </div>

      <!-- 统计信息 -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <div class="flex items-center">
            <div class="p-2 bg-blue-100 rounded-lg">
              <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-700">总分类</p>
              <p class="text-lg font-semibold text-gray-900">{{ totalCategories }}</p>
            </div>
          </div>
        </div>
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <div class="flex items-center">
            <div class="p-2 bg-green-100 rounded-lg">
              <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-700">活跃分类</p>
              <p class="text-lg font-semibold text-gray-900">{{ activeCategories }}</p>
            </div>
          </div>
        </div>
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <div class="flex items-center">
            <div class="p-2 bg-purple-100 rounded-lg">
              <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-700">总文章</p>
              <p class="text-lg font-semibold text-gray-900">{{ totalArticles }}</p>
            </div>
          </div>
        </div>
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <div class="flex items-center">
            <div class="p-2 bg-orange-100 rounded-lg">
              <svg class="w-6 h-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-700">平均热度</p>
              <p class="text-lg font-semibold text-gray-900">{{ averageHeat }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 工具栏 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4 mb-6">
        <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div class="flex items-center space-x-4">
            <button
              @click="showCreateModal = true"
              class="inline-flex items-center px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition"
            >
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              新建分类
            </button>
            <button
              @click="refreshCategories"
              :disabled="loading"
              class="inline-flex items-center px-4 py-2 bg-gray-600 text-white text-sm font-medium rounded-lg hover:bg-gray-700 transition disabled:opacity-50"
            >
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
              </svg>
              刷新
            </button>
          </div>
          <div class="flex items-center space-x-4">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索分类..."
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm"
            />
          </div>
        </div>
      </div>

      <!-- 分类列表 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200">
          <h2 class="text-lg font-semibold text-gray-900">分类列表</h2>
        </div>
        
        <div v-if="loading" class="p-8 text-center">
          <div class="inline-flex items-center">
            <svg class="animate-spin h-5 w-5 text-blue-600 mr-3" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            加载中...
          </div>
        </div>

        <div v-else-if="filteredCategories.length === 0" class="p-8 text-center text-gray-600">
          <svg class="w-12 h-12 mx-auto text-gray-500 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"></path>
          </svg>
          <p class="text-sm">{{ searchQuery ? '没有找到匹配的分类' : '暂无分类数据' }}</p>
        </div>

        <div v-else class="divide-y divide-gray-200">
          <div
            v-for="category in filteredCategories"
            :key="category.id"
            class="p-6 hover:bg-gray-50 transition"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center space-x-4">
                <div class="flex-shrink-0">
                  <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                    <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"></path>
                    </svg>
                  </div>
                </div>
                <div>
                  <h3 class="text-lg font-medium text-gray-900">{{ category.name }}</h3>
                  <p class="text-sm text-gray-500 mt-1">{{ category.description || '无描述' }}</p>
                  <div class="flex items-center space-x-4 mt-2">
                    <span class="text-xs text-gray-400">ID: {{ category.id }}</span>
                    <span class="text-xs text-gray-400">排序: {{ category.sortOrder }}</span>
                    <span class="text-xs text-gray-400">文章数: {{ category.articleCount || 0 }}</span>
                    <span class="text-xs text-gray-400">创建时间: {{ formatDate(category.createdAt) }}</span>
                  </div>
                </div>
              </div>
              <div class="flex items-center space-x-2">
                <button
                  @click="editCategory(category)"
                  class="p-2 text-gray-500 hover:text-blue-600 transition"
                  title="编辑"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                  </svg>
                </button>
                <button
                  @click="deleteCategory(category)"
                  class="p-2 text-gray-500 hover:text-red-600 transition"
                  title="删除"
                  :disabled="(category.articleCount || 0) > 0"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建/编辑分类模态框 -->
    <div
      v-if="showCreateModal || showEditModal"
      class="fixed inset-0 z-50 overflow-y-auto"
    >
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75" @click="closeModal"></div>
        <div class="relative bg-white rounded-lg p-6 max-w-md w-full">
          <h3 class="text-lg font-bold text-gray-900 mb-4">
            {{ showCreateModal ? '创建分类' : '编辑分类' }}
          </h3>
          <form @submit.prevent="submitForm" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">分类名称</label>
              <input
                v-model="formData.name"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="请输入分类名称"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
              <textarea
                v-model="formData.description"
                rows="3"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="请输入分类描述（可选）"
              ></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">排序</label>
              <input
                v-model="formData.sortOrder"
                type="number"
                min="0"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="排序值（数字越小越靠前）"
              />
            </div>
            <div class="flex space-x-3 pt-4">
              <button
                type="submit"
                :disabled="submitting"
                class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 font-medium"
              >
                {{ submitting ? '提交中...' : (showCreateModal ? '创建' : '更新') }}
              </button>
              <button
                type="button"
                @click="closeModal"
                class="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium"
              >
                取消
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 删除确认模态框 -->
    <div
      v-if="showDeleteModal"
      class="fixed inset-0 z-50 overflow-y-auto"
    >
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75" @click="showDeleteModal = false"></div>
        <div class="relative bg-white rounded-lg p-6 max-w-md w-full">
          <h3 class="text-lg font-bold text-gray-900 mb-4">删除分类</h3>
          <p class="text-sm text-gray-700 mb-6">
            确定要删除分类 "{{ categoryToDelete?.name }}" 吗？此操作不可撤销。
          </p>
          <div class="flex space-x-3">
            <button
              @click="confirmDelete"
              :disabled="submitting"
              class="flex-1 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:opacity-50 font-medium"
            >
              {{ submitting ? '删除中...' : '确认删除' }}
            </button>
            <button
              @click="showDeleteModal = false"
              class="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium"
            >
              取消
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 消息提示 -->
    <div
      v-if="message"
      :class="[
        'fixed bottom-4 right-4 px-6 py-3 rounded-lg shadow-lg z-50 font-medium',
        messageType === 'success' ? 'bg-green-600 text-white' : 'bg-red-600 text-white'
      ]"
    >
      {{ message }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useCategoriesStore } from '../stores/categories'
import type { Category } from '../stores/categories'

const categoriesStore = useCategoriesStore()

// 响应式数据
const loading = ref(false)
const searchQuery = ref('')
const showCreateModal = ref(false)
const showEditModal = ref(false)
const showDeleteModal = ref(false)
const submitting = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error'>('success')

const formData = ref({
  name: '',
  description: '',
  sortOrder: 0
})

const editingCategory = ref<Category | null>(null)
const categoryToDelete = ref<Category | null>(null)

// 计算属性
const filteredCategories = computed(() => {
  if (!searchQuery.value.trim()) {
    return categoriesStore.categories
  }
  return categoriesStore.categories.filter(category =>
    category.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    (category.description && category.description.toLowerCase().includes(searchQuery.value.toLowerCase()))
  )
})

const totalCategories = computed(() => categoriesStore.categories.length)
const activeCategories = computed(() => categoriesStore.categories.filter(c => (c.articleCount || 0) > 0).length)
const totalArticles = computed(() => categoriesStore.categories.reduce((sum, c) => sum + (c.articleCount || 0), 0))
const averageHeat = computed(() => {
  const total = totalArticles.value
  return total > 0 ? Math.round(total / totalCategories.value * 10) / 10 : 0
})

// 方法
const showMessage = (msg: string, type: 'success' | 'error' = 'success') => {
  message.value = msg
  messageType.value = type
  setTimeout(() => {
    message.value = ''
  }, 3000)
}

const formatDate = (dateString: string) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const refreshCategories = async () => {
  loading.value = true
  try {
    await categoriesStore.refreshCategories()
    showMessage('分类列表已刷新')
  } catch (err: any) {
    showMessage(err.message || '刷新失败', 'error')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formData.value = {
    name: '',
    description: '',
    sortOrder: 0
  }
  editingCategory.value = null
}

const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  resetForm()
}

const editCategory = (category: Category) => {
  editingCategory.value = category
  formData.value = {
    name: category.name,
    description: category.description || '',
    sortOrder: category.sortOrder
  }
  showEditModal.value = true
}

const deleteCategory = (category: Category) => {
  if ((category.articleCount || 0) > 0) {
    showMessage('该分类下还有文章，无法删除', 'error')
    return
  }
  categoryToDelete.value = category
  showDeleteModal.value = true
}

const submitForm = async () => {
  submitting.value = true
  try {
    if (showCreateModal.value) {
      // 创建分类
      await categoriesStore.createCategory(formData.value)
      showMessage('分类创建成功')
    } else if (showEditModal.value && editingCategory.value) {
      // 更新分类
      await categoriesStore.updateCategory({
        id: editingCategory.value.id,
        ...formData.value
      })
      showMessage('分类更新成功')
    }
    closeModal()
  } catch (err: any) {
    showMessage(err.message || '操作失败', 'error')
  } finally {
    submitting.value = false
  }
}

const confirmDelete = async () => {
  if (!categoryToDelete.value) return
  
  submitting.value = true
  try {
    await categoriesStore.deleteCategory(categoryToDelete.value.id)
    showMessage('分类删除成功')
    showDeleteModal.value = false
    categoryToDelete.value = null
  } catch (err: any) {
    showMessage(err.message || '删除失败', 'error')
  } finally {
    submitting.value = false
  }
}

// 生命周期
onMounted(async () => {
  loading.value = true
  try {
    await categoriesStore.initializeData()
  } catch (err: any) {
    showMessage('加载分类失败', 'error')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.category-management {
  min-height: 100vh;
  background-color: #f9fafb;
}
</style> 