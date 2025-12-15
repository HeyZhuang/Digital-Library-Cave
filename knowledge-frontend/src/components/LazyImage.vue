<template>
  <div 
    ref="imageContainer" 
    class="lazy-image-container"
    :class="{ 'lazy-image-loaded': isLoaded, 'lazy-image-error': hasError }"
  >
    <!-- 占位符 -->
    <div 
      v-if="!isLoaded && !hasError" 
      class="lazy-image-placeholder"
      :style="{ backgroundColor: placeholderColor }"
    >
      <div class="lazy-image-skeleton">
        <div class="skeleton-shimmer"></div>
      </div>
    </div>

    <!-- 错误状态 -->
    <div 
      v-if="hasError" 
      class="lazy-image-error"
      :style="{ backgroundColor: errorBackgroundColor }"
    >
      <svg class="error-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
      </svg>
      <span class="error-text">{{ errorText }}</span>
    </div>

    <!-- 实际图片 -->
    <img
      v-show="isLoaded && !hasError"
      ref="imageElement"
      :src="isInView ? src : ''"
      :alt="alt"
      :class="imageClass"
      :style="imageStyle"
      @load="handleImageLoad"
      @error="handleImageError"
      @click="handleImageClick"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'

interface LazyImageProps {
  src: string
  alt?: string
  width?: string | number
  height?: string | number
  placeholderColor?: string
  errorBackgroundColor?: string
  errorText?: string
  imageClass?: string
  imageStyle?: Record<string, string>
  threshold?: number
  rootMargin?: string
  preload?: boolean
}

const props = withDefaults(defineProps<LazyImageProps>(), {
  alt: '',
  placeholderColor: '#f3f4f6',
  errorBackgroundColor: '#fef2f2',
  errorText: '图片加载失败',
  threshold: 0.1,
  rootMargin: '50px',
  preload: false
})

const emit = defineEmits<{
  'load': [event: Event]
  'error': [event: Event]
  'click': [event: MouseEvent]
}>()

const imageContainer = ref<HTMLElement>()
const imageElement = ref<HTMLImageElement>()
const isInView = ref(false)
const isLoaded = ref(false)
const hasError = ref(false)
const observer = ref<IntersectionObserver>()

// 计算样式
const containerStyle = computed(() => {
  const style: Record<string, string> = {}
  
  if (props.width) {
    style.width = typeof props.width === 'number' ? `${props.width}px` : props.width
  }
  
  if (props.height) {
    style.height = typeof props.height === 'number' ? `${props.height}px` : props.height
  }
  
  return style
})

// 处理图片加载成功
const handleImageLoad = (event: Event) => {
  isLoaded.value = true
  hasError.value = false
  emit('load', event)
}

// 处理图片加载失败
const handleImageError = (event: Event) => {
  hasError.value = true
  isLoaded.value = false
  emit('error', event)
}

// 处理图片点击
const handleImageClick = (event: MouseEvent) => {
  emit('click', event)
}

// 创建 Intersection Observer
const createObserver = () => {
  if (!imageContainer.value) return

  observer.value = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          isInView.value = true
          // 一旦进入视口，就不再观察
          if (observer.value) {
            observer.value.unobserve(entry.target)
          }
        }
      })
    },
    {
      threshold: props.threshold,
      rootMargin: props.rootMargin
    }
  )

  observer.value.observe(imageContainer.value)
}

// 清理 Observer
const cleanupObserver = () => {
  if (observer.value) {
    observer.value.disconnect()
    observer.value = undefined
  }
}

// 预加载图片
const preloadImage = () => {
  if (props.preload && props.src) {
    const img = new Image()
    img.src = props.src
  }
}

// 监听 src 变化
watch(() => props.src, (newSrc, oldSrc) => {
  if (newSrc !== oldSrc) {
    isLoaded.value = false
    hasError.value = false
    isInView.value = false
    
    // 重新创建 observer
    cleanupObserver()
    nextTick(() => {
      createObserver()
    })
  }
})

onMounted(() => {
  createObserver()
  preloadImage()
})

onUnmounted(() => {
  cleanupObserver()
})
</script>

<style scoped>
.lazy-image-container {
  position: relative;
  display: inline-block;
  overflow: hidden;
}

.lazy-image-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  min-height: 100px;
}

.lazy-image-skeleton {
  position: relative;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-shimmer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.4) 50%, transparent 100%);
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.lazy-image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  min-height: 100px;
  color: #ef4444;
}

.error-icon {
  width: 24px;
  height: 24px;
  margin-bottom: 8px;
}

.error-text {
  font-size: 12px;
  text-align: center;
}

.lazy-image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: opacity 0.3s ease;
}

.lazy-image-loaded img {
  opacity: 1;
}

.lazy-image-container:not(.lazy-image-loaded) img {
  opacity: 0;
}
</style> 