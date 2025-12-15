<template>
  <div class="relative">
    <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
      <svg class="h-5 w-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
      </svg>
    </div>
    <input
      v-model="searchQuery"
      type="text"
      placeholder="搜索文章、标签..."
      class="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg text-sm placeholder-gray-500 focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-colors"
      @keyup.enter="handleSearch"
    />
    <button
      v-if="searchQuery"
      @click="clearSearch"
      class="absolute inset-y-0 right-0 pr-3 flex items-center"
    >
              <svg class="h-4 w-4 text-gray-500 hover:text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
      </svg>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useArticlesStore } from '../stores/articles'

const router = useRouter()
const articlesStore = useArticlesStore()

const searchQuery = ref('')

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    articlesStore.searchArticles(searchQuery.value)
    router.push('/search')
  }
}

const clearSearch = () => {
  searchQuery.value = ''
  articlesStore.searchArticles('')
}
</script> 