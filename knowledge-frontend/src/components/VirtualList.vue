<template>
  <div 
    ref="containerRef" 
    class="virtual-list-container"
    :style="{ height: `${height}px` }"
    @scroll="handleScroll"
  >
    <div 
      class="virtual-list-phantom"
      :style="{ height: `${totalHeight}px` }"
    ></div>
    <div 
      class="virtual-list-content"
      :style="{ transform: `translateY(${offsetY}px)` }"
    >
      <div
        v-for="item in visibleItems"
        :key="item.index"
        class="virtual-list-item"
        :style="{ height: `${itemHeight}px` }"
      >
        <slot 
          :item="item.data" 
          :index="item.index"
          :isVisible="true"
        ></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'

interface VirtualListProps {
  items: any[]
  itemHeight: number
  height: number
  overscan?: number
}

interface VirtualListItem {
  data: any
  index: number
}

const props = withDefaults(defineProps<VirtualListProps>(), {
  overscan: 5
})

const emit = defineEmits<{
  'scroll': [event: Event]
  'visible-change': [visibleItems: VirtualListItem[]]
}>()

const containerRef = ref<HTMLElement>()
const scrollTop = ref(0)
const containerHeight = ref(0)

// 计算总高度
const totalHeight = computed(() => props.items.length * props.itemHeight)

// 计算可见区域的起始和结束索引
const visibleRange = computed(() => {
  const start = Math.floor(scrollTop.value / props.itemHeight)
  const end = Math.min(
    start + Math.ceil(containerHeight.value / props.itemHeight) + props.overscan,
    props.items.length
  )
  
  return {
    start: Math.max(0, start - props.overscan),
    end
  }
})

// 计算可见项目
const visibleItems = computed(() => {
  const { start, end } = visibleRange.value
  const items: VirtualListItem[] = []
  
  for (let i = start; i < end; i++) {
    if (props.items[i]) {
      items.push({
        data: props.items[i],
        index: i
      })
    }
  }
  
  return items
})

// 计算偏移量
const offsetY = computed(() => visibleRange.value.start * props.itemHeight)

// 处理滚动事件
const handleScroll = (event: Event) => {
  const target = event.target as HTMLElement
  scrollTop.value = target.scrollTop
  emit('scroll', event)
}

// 滚动到指定索引
const scrollToIndex = (index: number, behavior: ScrollBehavior = 'smooth') => {
  if (!containerRef.value) return
  
  const scrollTop = index * props.itemHeight
  containerRef.value.scrollTo({
    top: scrollTop,
    behavior
  })
}

// 滚动到指定元素
const scrollToItem = (item: any, behavior: ScrollBehavior = 'smooth') => {
  const index = props.items.indexOf(item)
  if (index !== -1) {
    scrollToIndex(index, behavior)
  }
}

// 获取可见项目的索引范围
const getVisibleRange = () => visibleRange.value

// 更新容器高度
const updateContainerHeight = () => {
  if (containerRef.value) {
    containerHeight.value = containerRef.value.clientHeight
  }
}

// 监听可见项目变化
watch(visibleItems, (newItems) => {
  emit('visible-change', newItems)
}, { deep: true })

// 监听项目数量变化，重新计算
watch(() => props.items.length, () => {
  nextTick(() => {
    updateContainerHeight()
  })
})

// 监听容器高度变化
watch(() => props.height, () => {
  nextTick(() => {
    updateContainerHeight()
  })
})

onMounted(() => {
  updateContainerHeight()
  
  // 监听窗口大小变化
  const resizeObserver = new ResizeObserver(() => {
    updateContainerHeight()
  })
  
  if (containerRef.value) {
    resizeObserver.observe(containerRef.value)
  }
  
  // 清理函数
  onUnmounted(() => {
    resizeObserver.disconnect()
  })
})

// 暴露方法
defineExpose({
  scrollToIndex,
  scrollToItem,
  getVisibleRange,
  updateContainerHeight
})
</script>

<style scoped>
.virtual-list-container {
  position: relative;
  overflow-y: auto;
  overflow-x: hidden;
}

.virtual-list-phantom {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: -1;
}

.virtual-list-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
}

.virtual-list-item {
  width: 100%;
}
</style> 