<template>
  <div class="comment-item">
    <!-- 用户头像和信息 -->
    <div class="flex space-x-3">
      <div class="flex-shrink-0">
        <div class="w-10 h-10 bg-gradient-to-br from-primary-400 to-primary-600 rounded-full flex items-center justify-center text-white font-medium">
          {{ comment.authorName.charAt(0).toUpperCase() }}
        </div>
      </div>
      
      <div class="flex-1 min-w-0">
        <!-- 评论头部 -->
        <div class="flex items-center space-x-2 mb-2">
          <h4 class="text-sm font-medium text-gray-900">{{ comment.authorName }}</h4>
          <span class="text-xs text-gray-500">{{ formatDate(comment.createdAt) }}</span>
          <span v-if="comment.parentId" class="text-xs text-primary-600 bg-primary-50 px-2 py-0.5 rounded">
            回复
          </span>
        </div>
        
        <!-- 评论内容 -->
        <div class="text-sm text-gray-700 mb-3 prose prose-sm max-w-none" v-html="renderedContent"></div>
        
        <!-- 操作按钮 -->
        <div class="flex items-center space-x-4">
          <button
            @click="toggleLike"
            :class="[
              'flex items-center space-x-1 text-xs transition',
              isLiked ? 'text-red-600' : 'text-gray-500 hover:text-red-600'
            ]"
          >
            <svg class="w-4 h-4" :class="isLiked ? 'fill-current' : ''" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
            </svg>
            <span>{{ comment.likes || 0 }}</span>
          </button>
          
          <button
            @click="toggleReply"
            class="flex items-center space-x-1 text-xs text-gray-500 hover:text-primary-600 transition"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6"></path>
            </svg>
            <span>回复</span>
          </button>
          
          <button
            @click="reportComment"
            class="flex items-center space-x-1 text-xs text-gray-500 hover:text-red-600 transition"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L4.082 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
            </svg>
            <span>举报</span>
          </button>
        </div>
        
        <!-- 回复表单 -->
        <div v-if="showReplyForm" class="mt-4 p-3 bg-gray-50 rounded-lg">
          <div class="mb-3">
            <textarea
              v-model="replyContent"
              rows="3"
              placeholder="写下你的回复..."
              class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
            ></textarea>
          </div>
          <div class="flex items-center justify-between">
            <div class="text-xs text-gray-500">
              回复给 @{{ comment.authorName }}
            </div>
            <div class="flex space-x-2">
              <button
                @click="cancelReply"
                class="px-3 py-1 text-xs font-medium text-gray-600 bg-white border border-gray-300 rounded hover:bg-gray-50 transition"
              >
                取消
              </button>
              <button
                @click="submitReply"
                :disabled="!replyContent.trim()"
                class="px-3 py-1 text-xs font-medium text-white bg-primary-600 border border-transparent rounded hover:bg-primary-700 transition disabled:opacity-50"
              >
                回复
              </button>
            </div>
          </div>
        </div>
        
        <!-- 注意：后端Comment实体暂时不支持嵌套回复，这部分功能待完善 -->
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { marked } from 'marked'
import { type Comment } from '@/api/comments'

const props = defineProps<{
  comment: Comment
  isReply?: boolean
}>()

const emit = defineEmits<{
  reply: [commentId: string | number, content: string]
  like: [commentId: string | number]
  report: [commentId: string | number]
}>()

// 状态
const showReplyForm = ref(false)
const replyContent = ref('')
const isLiked = ref(false)

// 计算属性
const renderedContent = computed(() => {
  if (!props.comment.content) return ''
  
  // 简单的markdown渲染
  marked.setOptions({
    breaks: true,
    gfm: true
  })
  
  return marked(props.comment.content) as string
})

// 方法
const formatDate = (dateString: string | undefined) => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN')
}

const toggleLike = () => {
  isLiked.value = !isLiked.value
  if (props.comment.id) {
    emit('like', props.comment.id)
  }
}

const toggleReply = () => {
  showReplyForm.value = !showReplyForm.value
}

const submitReply = () => {
  if (!replyContent.value.trim() || !props.comment.id) return
  
  emit('reply', props.comment.id, replyContent.value)
  replyContent.value = ''
  showReplyForm.value = false
}

const cancelReply = () => {
  replyContent.value = ''
  showReplyForm.value = false
}

const reportComment = () => {
  if (props.comment.id) {
    emit('report', props.comment.id)
  }
}
</script>

<style scoped>
.comment-item {
  transition: all 0.2s ease;
}

.comment-item:hover {
  background-color: rgba(0, 0, 0, 0.01);
}

/* 回复样式 */
.prose {
  max-width: none;
}

.prose p {
  margin: 0.5rem 0;
}

.prose p:first-child {
  margin-top: 0;
}

.prose p:last-child {
  margin-bottom: 0;
}
</style> 