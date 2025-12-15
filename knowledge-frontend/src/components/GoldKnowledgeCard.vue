<template>
  <div 
    ref="cardRef"
    class="knowledge-card group relative overflow-hidden rounded-xl cursor-pointer transform transition-all duration-500 hover:scale-105"
    @mouseenter="onMouseEnter"
    @mouseleave="onMouseLeave"
    @click="onClick"
  >
    <!-- 鎏金边框效果 -->
    <div class="gilded-border absolute inset-0 rounded-xl opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
    
    <!-- 磨砂玻璃背景 -->
    <div class="absolute inset-0 bg-gradient-to-br from-deepSpace-800/95 to-deepSpace-900/95 backdrop-blur-md"></div>
    
    <!-- 羊皮纸纹理叠加 -->
    <div class="absolute inset-0 bg-parchment-texture opacity-5"></div>
    
    <!-- 内容区域 -->
    <div class="relative z-10 p-6 h-full flex flex-col">
      <!-- 顶部标签徽章 -->
      <div class="flex items-start justify-between mb-4">
        <div class="flex flex-wrap gap-2">
          <span 
            v-for="tag in tags" 
            :key="tag"
            class="gold-badge px-3 py-1 text-xs font-medium bg-gradient-gold text-deepSpace-900 rounded-full shadow-lg"
          >
            {{ tag }}
          </span>
        </div>
        <div class="flex items-center text-parchment-400 text-sm">
          <ClockIcon class="w-4 h-4 mr-1" />
          {{ timeAgo }}
        </div>
      </div>
      
      <!-- 标题区域 - 古籍风格 -->
      <div class="flex-1 flex items-center justify-center mb-4">
        <h3 
          :class="[
            'text-title font-title text-parchment-100 text-center leading-relaxed transition-all duration-300',
            verticalText ? 'vertical-text text-xl' : 'text-lg'
          ]"
        >
          {{ title }}
        </h3>
      </div>
      
      <!-- 摘要预览 -->
      <div v-if="abstract" class="mb-4">
        <p class="text-parchment-300 text-sm leading-relaxed line-clamp-3 font-body">
          {{ abstract }}
        </p>
      </div>
      
      <!-- 底部元数据 -->
      <div class="flex items-center justify-between">
        <!-- 阅读进度 -->
        <div class="flex-1 mr-4">
          <div class="flex items-center justify-between text-xs text-parchment-400 mb-1">
            <span>阅读进度</span>
            <span>{{ progress }}%</span>
          </div>
          <div class="progress-container relative h-2 bg-deepSpace-700 rounded-full overflow-hidden">
            <div 
              class="gold-bar h-full bg-gradient-gold transition-all duration-1000 ease-out relative"
              :style="{ width: `${progress}%` }"
            >
              <!-- 金沙流动效果 -->
              <div class="absolute inset-0 bg-gradient-to-r from-transparent via-gold-200/50 to-transparent shimmer-effect"></div>
            </div>
          </div>
        </div>
        
        <!-- 阅读时长宝石 -->
        <div class="reading-gem flex items-center justify-center w-10 h-10 bg-gradient-gold rounded-full shadow-lg">
          <span class="text-xs font-bold text-deepSpace-900">{{ readingTime }}m</span>
        </div>
      </div>
    </div>
    
    <!-- 悬浮粒子特效 -->
    <div 
      v-if="showParticles" 
      ref="particlesRef"
      class="absolute inset-0 pointer-events-none overflow-hidden rounded-xl"
    >
      <div 
        v-for="particle in particles" 
        :key="particle.id"
        class="absolute w-1 h-1 bg-gold-400 rounded-full animate-float opacity-70"
        :style="particle.style"
      ></div>
    </div>
    
    <!-- 发光光晕效果 -->
    <div 
      class="absolute inset-0 -z-10 rounded-xl bg-gradient-gold opacity-0 group-hover:opacity-20 transition-opacity duration-500 blur-xl"
    ></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ClockIcon } from '@heroicons/vue/24/outline'

interface Props {
  title: string
  tags?: string[]
  progress?: number
  timeAgo?: string
  abstract?: string
  readingTime?: number
  verticalText?: boolean
  coverImage?: string
}

interface Emits {
  (e: 'click', data: { title: string }): void
}

const props = withDefaults(defineProps<Props>(), {
  tags: () => [],
  progress: 0,
  timeAgo: '刚刚',
  abstract: '',
  readingTime: 5,
  verticalText: false,
  coverImage: ''
})

const emit = defineEmits<Emits>()

const cardRef = ref<HTMLElement>()
const particlesRef = ref<HTMLElement>()
const showParticles = ref(false)
const particles = ref<Array<{
  id: number
  style: Record<string, string>
}>>([])

// 鼠标悬浮效果
const onMouseEnter = () => {
  showParticles.value = true
  generateParticles()
}

const onMouseLeave = () => {
  showParticles.value = false
  particles.value = []
}

// 点击事件
const onClick = () => {
  // 添加点击涟漪效果
  createRippleEffect()
  emit('click', { title: props.title })
}

// 生成悬浮粒子
const generateParticles = () => {
  particles.value = []
  for (let i = 0; i < 12; i++) {
    particles.value.push({
      id: i,
      style: {
        left: `${Math.random() * 100}%`,
        top: `${Math.random() * 100}%`,
        animationDelay: `${Math.random() * 2}s`,
        animationDuration: `${2 + Math.random() * 2}s`,
      }
    })
  }
}

// 创建点击涟漪效果
const createRippleEffect = () => {
  if (!cardRef.value) return
  
  // 动态导入canvas-confetti
  import('canvas-confetti').then(confetti => {
    const rect = cardRef.value!.getBoundingClientRect()
    const x = (rect.left + rect.width / 2) / window.innerWidth
    const y = (rect.top + rect.height / 2) / window.innerHeight
    
    confetti.default({
      particleCount: 20,
      spread: 40,
      origin: { x, y },
      colors: ['#d4af37', '#f9e076', '#ffd700'],
      scalar: 0.8,
      gravity: 0.5,
      drift: 0,
      ticks: 100,
    })
  })
}

onMounted(() => {
  // 初始化进入动画
  if (cardRef.value) {
    cardRef.value.style.opacity = '0'
    cardRef.value.style.transform = 'translateY(20px)'
    
    setTimeout(() => {
      if (cardRef.value) {
        cardRef.value.style.transition = 'all 0.6s ease-out'
        cardRef.value.style.opacity = '1'
        cardRef.value.style.transform = 'translateY(0)'
      }
    }, 100)
  }
})
</script>

<style scoped>
.knowledge-card {
  aspect-ratio: 1 / 1.618; /* 黄金比例 */
  background: linear-gradient(145deg, 
    rgba(30, 41, 59, 0.95), 
    rgba(15, 23, 42, 0.95)
  );
  box-shadow: 
    0 4px 30px rgba(212, 175, 55, 0.15),
    0 1px 3px rgba(0, 0, 0, 0.12),
    0 1px 2px rgba(0, 0, 0, 0.24);
}

.knowledge-card:hover {
  box-shadow: 
    0 8px 50px rgba(212, 175, 55, 0.25),
    0 4px 20px rgba(0, 0, 0, 0.15),
    0 2px 8px rgba(0, 0, 0, 0.35);
}

.gilded-border {
  background: linear-gradient(135deg, 
    transparent 50%, 
    rgba(212, 175, 55, 0.3) 90%,
    transparent 100%
  );
  border: 1px solid rgba(212, 175, 55, 0.2);
}

.vertical-text {
  writing-mode: vertical-rl;
  text-orientation: mixed;
  letter-spacing: 0.1em;
  line-height: 1.8;
}

.gold-badge {
  background: linear-gradient(135deg, #d4af37, #f9e076, #d4af37);
  box-shadow: 0 2px 8px rgba(212, 175, 55, 0.3);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.gold-badge:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(212, 175, 55, 0.4);
}

.progress-container {
  backdrop-filter: blur(4px);
  border: 1px solid rgba(212, 175, 55, 0.1);
}

.gold-bar {
  background: linear-gradient(90deg, #d4af37, #f9e076, #d4af37);
  box-shadow: 
    0 0 10px rgba(212, 175, 55, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.shimmer-effect {
  animation: shimmer 2s linear infinite;
}

.reading-gem {
  background: linear-gradient(135deg, #d4af37, #f9e076);
  box-shadow: 
    0 4px 15px rgba(212, 175, 55, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.3),
    inset 0 -1px 0 rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.reading-gem:hover {
  transform: rotate(15deg) scale(1.1);
  box-shadow: 
    0 6px 20px rgba(212, 175, 55, 0.6),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
}

/* 行数限制 */
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

@keyframes float {
  0%, 100% { 
    transform: translateY(0px) rotate(0deg); 
    opacity: 0.7;
  }
  25% { 
    transform: translateY(-5px) rotate(90deg); 
    opacity: 1;
  }
  50% { 
    transform: translateY(-10px) rotate(180deg); 
    opacity: 0.8;
  }
  75% { 
    transform: translateY(-5px) rotate(270deg); 
    opacity: 1;
  }
}
</style> 