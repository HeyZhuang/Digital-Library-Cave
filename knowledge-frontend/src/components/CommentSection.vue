<template>
  <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-lg font-semibold text-gray-900">
        评论 ({{ comments.length }})
      </h3>
      <button
        @click="showCommentForm = !showCommentForm"
        class="flex items-center space-x-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
        </svg>
        <span>写评论</span>
      </button>
    </div>

    <!-- 评论表单 -->
    <div v-if="showCommentForm" class="mb-6 p-4 bg-gray-50 rounded-lg">
      <div class="mb-4">
        <label for="comment-content" class="block text-sm font-medium text-gray-700 mb-2">
          评论内容
        </label>
        <textarea
          id="comment-content"
          v-model="newComment.content"
          rows="4"
          placeholder="写下你的想法..."
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
        ></textarea>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
        <div>
          <label for="comment-author" class="block text-sm font-medium text-gray-700 mb-2">
            昵称
          </label>
          <input
            id="comment-author"
            v-model="newComment.authorName"
            type="text"
            placeholder="你的昵称"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          />
        </div>
        <div>
          <label for="comment-email" class="block text-sm font-medium text-gray-700 mb-2">
            邮箱 (可选)
          </label>
          <input
            id="comment-email"
            v-model="newComment.authorEmail"
            type="email"
            placeholder="your@email.com"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          />
        </div>
      </div>
      <div class="flex items-center justify-between">
        <div class="text-sm text-gray-500">
          支持 Markdown 语法
        </div>
        <div class="flex space-x-2">
          <button
            @click="cancelComment"
            class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition"
          >
            取消
          </button>
          <button
            @click="submitComment"
            :disabled="!canSubmitComment || isSubmitting"
            class="px-4 py-2 text-sm font-medium text-white bg-primary-600 border border-transparent rounded-lg hover:bg-primary-700 transition disabled:opacity-50"
          >
            {{ isSubmitting ? '提交中...' : '提交评论' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 评论列表 -->
    <div v-if="comments.length > 0" class="space-y-6">
      <div
        v-for="comment in sortedComments"
        :key="comment.id"
        class="border-b border-gray-100 pb-6 last:border-b-0"
      >
        <CommentItem
          :comment="comment"
          @reply="handleReply"
          @like="handleLike"
          @report="handleReport"
        />
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!isLoading" class="text-center py-12">
      <svg class="w-12 h-12 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03 8-9 8s9 3.582 9 8z"></path>
      </svg>
      <p class="text-gray-500">还没有评论，来说点什么吧～</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
      <p class="text-gray-500 mt-2">加载评论中...</p>
    </div>

    <!-- 评论统计 -->
    <div v-if="comments.length > 0" class="mt-6 pt-6 border-t border-gray-200">
      <div class="flex items-center justify-between text-sm text-gray-500">
        <span>共 {{ comments.length }} 条评论</span>
        <div class="flex items-center space-x-4">
          <button
            @click="sortBy = 'latest'"
            :class="['hover:text-primary-600 transition', sortBy === 'latest' ? 'text-primary-600 font-medium' : '']"
          >
            最新
          </button>
          <button
            @click="sortBy = 'oldest'"
            :class="['hover:text-primary-600 transition', sortBy === 'oldest' ? 'text-primary-600 font-medium' : '']"
          >
            最早
          </button>
          <button
            @click="sortBy = 'likes'"
            :class="['hover:text-primary-600 transition', sortBy === 'likes' ? 'text-primary-600 font-medium' : '']"
          >
            最热
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import CommentItem from './CommentItem.vue'
import { commentApi, type Comment } from '@/api/comments'

// Props
const props = defineProps<{
  articleId: string | number
}>()

// 评论数据
const comments = ref<Comment[]>([])

// 表单状态
const showCommentForm = ref(false)
const isSubmitting = ref(false)
const isLoading = ref(false)
const sortBy = ref<'latest' | 'oldest' | 'likes'>('latest')

const newComment = ref({
  authorName: '',
  authorEmail: '',
  content: ''
})

// 计算属性
const canSubmitComment = computed(() => {
  return newComment.value.authorName.trim() && newComment.value.content.trim()
})

const sortedComments = computed(() => {
  const sorted = [...comments.value]
  
  switch (sortBy.value) {
    case 'latest':
      return sorted.sort((a, b) => new Date(b.createdAt || '').getTime() - new Date(a.createdAt || '').getTime())
    case 'oldest':
      return sorted.sort((a, b) => new Date(a.createdAt || '').getTime() - new Date(b.createdAt || '').getTime())
    case 'likes':
      return sorted.sort((a, b) => (b.likes || 0) - (a.likes || 0))
    default:
      return sorted
  }
})

// 方法
const loadComments = async () => {
  if (!props.articleId) return
  
  isLoading.value = true
  try {
    const response = await commentApi.getCommentsByArticleId(Number(props.articleId))
    if (response.code === 200) {
      comments.value = response.data || []
      console.log('评论加载成功:', comments.value.length, '条评论')
    } else {
      console.error('加载评论失败:', response.message)
    }
  } catch (error) {
    console.error('加载评论失败:', error)
  } finally {
    isLoading.value = false
  }
}

const submitComment = async () => {
  if (!canSubmitComment.value || !props.articleId) return
  
  isSubmitting.value = true
  try {
    const commentData: Comment = {
      articleId: Number(props.articleId),
      content: newComment.value.content,
      authorName: newComment.value.authorName,
      authorEmail: newComment.value.authorEmail || undefined
    }
    
    const response = await commentApi.createComment(commentData)
    if (response.code === 200) {
      console.log('评论提交成功')
      
      // 重新加载评论列表以获取最新数据
      await loadComments()
      
      // 重置表单
      newComment.value = {
        authorName: '',
        authorEmail: '',
        content: ''
      }
      showCommentForm.value = false
      
      // 可选：显示成功提示
      // ElMessage.success('评论发表成功！')
    } else {
      console.error('评论提交失败:', response.message)
      // ElMessage.error(response.message || '评论提交失败')
    }
  } catch (error) {
    console.error('提交评论失败:', error)
    // ElMessage.error('网络错误，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

const cancelComment = () => {
  newComment.value = {
    authorName: '',
    authorEmail: '',
    content: ''
  }
  showCommentForm.value = false
}

const handleReply = async (commentId: string | number, replyContent: string) => {
  console.log('回复评论:', commentId, replyContent)
  try {
    // 实现回复逻辑 - 使用回复API
    const replyData: Comment = {
      articleId: Number(props.articleId),
      parentId: Number(commentId),
      content: replyContent,
      authorName: '匿名用户', // 这里应该从用户状态获取
    }
    
    const response = await commentApi.createComment(replyData)
    if (response.code === 200) {
      console.log('回复成功')
      await loadComments() // 重新加载评论
    }
  } catch (error) {
    console.error('回复失败:', error)
  }
}

const handleLike = async (commentId: string | number) => {
  console.log('点赞评论:', commentId)
  try {
    const response = await commentApi.likeComment(Number(commentId))
    if (response.code === 200) {
      // 找到对应评论并更新点赞数
      const comment = findCommentById(commentId)
      if (comment) {
        comment.likes = (comment.likes || 0) + 1
      }
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const handleReport = (commentId: string | number) => {
  console.log('举报评论:', commentId)
  // 实现举报逻辑
  if (confirm('确定要举报这条评论吗？')) {
    // 这里可以调用举报API
    console.log('已举报评论:', commentId)
  }
}

const findCommentById = (id: string | number): Comment | null => {
  for (const comment of comments.value) {
    if (comment.id == id) return comment
    // 如果有回复功能，这里需要递归查找子评论
  }
  return null
}

// 生命周期
onMounted(() => {
  loadComments()
})
</script>

<style scoped>
/* 自定义样式 */
.comment-form-enter-active,
.comment-form-leave-active {
  transition: all 0.3s ease;
}

.comment-form-enter-from,
.comment-form-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style> 