<template>
  <div class="bg-gradient-to-br from-gray-800 via-gray-900 to-gray-950 rounded-2xl p-6 shadow-lg border border-gray-700">
    <h3 class="text-xl font-bold text-cyan-300 mb-4 flex items-center gap-2">
      <svg class="w-6 h-6 text-cyan-400" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2a10 10 0 100 20 10 10 0 000-20zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/></svg>
      技能标签
    </h3>
    <div class="flex flex-wrap gap-3">
      <span v-for="skill in skills" :key="skill" class="bg-cyan-700/80 text-cyan-100 px-4 py-1 rounded-full text-base font-mono shadow hover:bg-cyan-500/80 transition-colors">{{ skill }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  skills: string[]
}>()
</script>

<style scoped></style> 