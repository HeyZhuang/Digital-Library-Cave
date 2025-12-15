import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { tagApi, type Tag } from '@/api/tags'

export const useTagsStore = defineStore('tags', () => {
  const tags = ref<Tag[]>([])
  const loading = ref(false)

  // 初始化数据
  const initializeData = async () => {
    await fetchTags()
  }

  // 计算属性
  const popularTags = computed(() => 
    tags.value.slice().sort((a, b) => (b.articleCount || 0) - (a.articleCount || 0)).slice(0, 10)
  )

  const totalTags = computed(() => tags.value.length)

  // Actions
  const fetchTags = async () => {
    loading.value = true
    try {
      const response = await tagApi.getAllTags()
      if (response.code === 200) {
        tags.value = response.data
      }
    } catch (error) {
      console.error('Failed to fetch tags:', error)
    } finally {
      loading.value = false
    }
  }

  const getPopularTags = async (limit = 20) => {
    try {
      const response = await tagApi.getPopularTags(limit)
      if (response.code === 200) {
        return response.data
      }
    } catch (error) {
      console.error('Failed to fetch popular tags:', error)
    }
    return []
  }

  const getTagByName = (name: string) => {
    return tags.value.find(tag => tag.name === name)
  }

  const getTagsByArticleId = async (articleId: number) => {
    try {
      const response = await tagApi.getTagsByArticleId(articleId)
      if (response.code === 200) {
        return response.data
      }
    } catch (error) {
      console.error('Failed to fetch article tags:', error)
    }
    return []
  }

  const searchTags = async (name: string) => {
    try {
      const response = await tagApi.searchTags(name)
      if (response.code === 200) {
        return response.data
      }
    } catch (error) {
      console.error('Failed to search tags:', error)
    }
    return []
  }

  const createTag = async (name: string, color?: string) => {
    try {
      const response = await tagApi.createTag(name, color)
      if (response.code === 200) {
        // 添加到本地缓存
        tags.value.push(response.data)
        return response.data
      }
    } catch (error) {
      console.error('Failed to create tag:', error)
    }
    return null
  }

  const updateTag = async (id: number, tag: Tag) => {
    try {
      const response = await tagApi.updateTag(id, tag)
      if (response.code === 200) {
        // 更新本地缓存
        const index = tags.value.findIndex(t => t.id === id)
        if (index !== -1) {
          tags.value[index] = response.data
        }
        return response.data
      }
    } catch (error) {
      console.error('Failed to update tag:', error)
    }
    return null
  }

  const deleteTag = async (id: number) => {
    try {
      const response = await tagApi.deleteTag(id)
      if (response.code === 200) {
        // 从本地缓存中移除
        const index = tags.value.findIndex(t => t.id === id)
        if (index !== -1) {
          tags.value.splice(index, 1)
        }
        return true
      }
    } catch (error) {
      console.error('Failed to delete tag:', error)
    }
    return false
  }

  return {
    tags,
    loading,
    popularTags,
    totalTags,
    fetchTags,
    getPopularTags,
    getTagByName,
    getTagsByArticleId,
    searchTags,
    createTag,
    updateTag,
    deleteTag,
    initializeData
  }
}) 