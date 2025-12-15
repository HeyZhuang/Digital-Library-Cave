<template>
  <div class="bg-gradient-to-br from-gray-800 via-gray-900 to-gray-950 rounded-2xl p-6 shadow-lg border border-gray-700">
    <h3 class="text-xl font-bold text-cyan-300 mb-4 flex items-center gap-2">
      <svg class="w-6 h-6 text-yellow-400" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2l2.09 6.26L20 9.27l-5 3.64L16.18 20 12 16.77 7.82 20 9 12.91l-5-3.64 5.91-.99z"/></svg>
      成就徽章
    </h3>
    <div class="flex flex-wrap gap-4">
      <div v-for="achievement in achievements" :key="achievement.title" class="flex flex-col items-center bg-gray-800/80 rounded-xl px-4 py-3 shadow border border-cyan-700/40 hover:scale-105 transition-transform">
        <div class="text-3xl mb-1">{{ achievement.icon }}</div>
        <div class="text-cyan-200 font-semibold">{{ achievement.title }}</div>
        <div class="text-gray-400 text-sm">{{ achievement.desc }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Achievement {
  icon: string
  title: string
  desc: string
}

defineProps<{
  achievements: Achievement[]
}>()
</script>

<style scoped></style> 