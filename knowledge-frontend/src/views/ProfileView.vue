<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-950 flex flex-col items-center py-12 px-4">
    <div class="w-full max-w-4xl" v-if="profile && adaptedProfile">
      <ProfileCard :profile="adaptedProfile" @edit="onEdit" />
      <!-- 新增活动组件网格布局 -->
      <div class="mt-10 space-y-8">
        <!-- 第一行：核心信息 -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div>
            <Achievements :achievements="profile.achievements || []" />
            <Skills :skills="profile.skills || []" class="mt-8" />
          </div>
          <div>
            <!-- 使用新的ActivityFeed组件，它会自动管理实时活动数据 -->
            <ActivityFeed />
          </div>
        </div>

        <!-- 第二行：活动统计和访客 -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <ActivityStats />
          <RecentVisitors />
        </div>

        <!-- 第三行：活动日历 -->
        <div class="grid grid-cols-1 gap-8">
          <ActivityCalendar />
        </div>

        <!-- 第四行：时间轴和洞察 -->
        <div class="grid grid-cols-1 xl:grid-cols-2 gap-8">
          <ActivityTimeline />
          <ActivityInsights />
        </div>
      </div>
    </div>
    <div v-else class="flex flex-col items-center justify-center h-96">
      <LoadingSpinner />
      <span class="text-cyan-200 mt-4">正在加载个人资料...</span>
    </div>
    <EditProfileModal 
      v-if="showEdit && profile" 
      :profile="profile" 
      @close="showEdit = false" 
      @save="onSave" 
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import ProfileCard from '../components/ProfileCard.vue'
import Achievements from '../components/Achievements.vue'
import Skills from '../components/Skills.vue'
import ActivityFeed from '../components/ActivityFeed.vue'
import ActivityStats from '../components/ActivityStats.vue'
import ActivityCalendar from '../components/ActivityCalendar.vue'
import ActivityTimeline from '../components/ActivityTimeline.vue'
import RecentVisitors from '../components/RecentVisitors.vue'
import ActivityInsights from '../components/ActivityInsights.vue'
import EditProfileModal from '../components/EditProfileModal.vue'
import LoadingSpinner from '../components/LoadingSpinner.vue'
import { profileApi } from '../api/profile'
import { useArticlesStore } from '../stores/articles'
import type { ProfileData as ApiProfileData } from '../api/profile'

// ProfileCard组件期望的数据格式
interface ProfileCardData {
  avatar: string
  nickname: string
  bio: string
  contact: {
    email: string
    phone: string
    location: string
  }
  skills: string[]
  socials: Array<{
    icon: string
    url: string
  }>
}

const profile = ref<ApiProfileData | null>(null)
const showEdit = ref(false)
const articlesStore = useArticlesStore()

// 将API数据适配为ProfileCard期望的格式
const adaptedProfile = computed((): ProfileCardData | null => {
  if (!profile.value) return null
  
  return {
    avatar: profile.value.avatar || '',
    nickname: profile.value.nickname || profile.value.username || '未设置昵称',
    bio: profile.value.bio || '这个人很懒，什么都没写~',
    contact: {
      email: profile.value.email || '',
      phone: profile.value.phone || '',
      location: profile.value.location || ''
    },
    skills: profile.value.skills || [],
    socials: profile.value.socials || []
  }
})

const fetchProfile = async () => {
  try {
    const res = await profileApi.getProfile()
    if (res.code === 200) {
      profile.value = res.data
    } else {
      console.error('获取个人资料失败:', res.message)
    }
  } catch (e) {
    console.error('获取个人资料失败', e)
  }
}

// 初始化数据
onMounted(async () => {
  await Promise.all([
    fetchProfile(),
    // 确保文章数据已加载，用于生成活动动态
    articlesStore.initializeData()
  ])
})

const onEdit = () => {
  showEdit.value = true
}

const onSave = async (newProfile: ApiProfileData) => {
  profile.value = newProfile
  showEdit.value = false
}
</script>

<style scoped>
</style> 