<template>
  <div :class="containerClass">
    <div :class="spinnerClass">
      <div class="animate-spin rounded-full border-2 border-t-transparent border-current"></div>
    </div>
    <p v-if="text" :class="textClass">{{ text }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  size?: 'sm' | 'md' | 'lg'
  text?: string
  overlay?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 'md',
  overlay: false
})

const containerClass = computed(() => {
  const base = 'flex flex-col items-center justify-center'
  return props.overlay 
    ? `${base} fixed inset-0 bg-white/80 backdrop-blur-sm z-50`
    : `${base} py-8`
})

const spinnerClass = computed(() => {
  const sizes = {
    sm: 'w-6 h-6',
    md: 'w-8 h-8',
    lg: 'w-12 h-12'
  }
  return `${sizes[props.size]} text-primary-600`
})

const textClass = computed(() => {
  const textSizes = {
    sm: 'text-sm',
    md: 'text-base',
    lg: 'text-lg'
  }
  return `${textSizes[props.size]} text-gray-600 mt-2`
})
</script> 