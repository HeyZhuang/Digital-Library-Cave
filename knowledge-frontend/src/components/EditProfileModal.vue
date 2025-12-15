<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/60">
    <div class="bg-gradient-to-br from-gray-800 via-gray-900 to-gray-950 rounded-2xl shadow-2xl p-8 w-full max-w-lg border border-cyan-700 relative">
      <button @click="$emit('close')" class="absolute top-4 right-4 text-gray-400 hover:text-cyan-400">
        <svg class="w-6 h-6" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>
      </button>
      <h2 class="text-2xl font-bold text-cyan-300 mb-6">编辑个人资料</h2>
      <form class="flex flex-col gap-4" @submit.prevent="onSave">
        <div class="flex flex-col items-center mb-4">
          <label class="block text-gray-300 mb-1">头像</label>
          <div class="relative">
            <img :src="avatarPreview || formData.avatar" class="w-24 h-24 rounded-full border-2 border-cyan-400 object-cover shadow mb-2" />
            <input type="file" accept="image/*" class="absolute top-0 left-0 w-full h-full opacity-0 cursor-pointer" @change="onAvatarChange" />
            <div v-if="uploading" class="absolute inset-0 flex items-center justify-center bg-black/50 rounded-full">
              <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-cyan-400"></div>
            </div>
          </div>
        </div>
        <div>
          <label class="block text-gray-300 mb-1">昵称</label>
          <input v-model="formData.nickname" class="w-full rounded bg-gray-800 text-cyan-200 px-3 py-2 border border-cyan-700 focus:outline-none focus:ring-2 focus:ring-cyan-500" />
        </div>
        <div>
          <label class="block text-gray-300 mb-1">简介</label>
          <textarea v-model="formData.bio" class="w-full rounded bg-gray-800 text-cyan-200 px-3 py-2 border border-cyan-700 focus:outline-none focus:ring-2 focus:ring-cyan-500" rows="2" />
        </div>
        <div class="flex gap-4">
          <div class="flex-1">
            <label class="block text-gray-300 mb-1">邮箱</label>
            <input v-model="formData.email" class="w-full rounded bg-gray-800 text-cyan-200 px-3 py-2 border border-cyan-700 focus:outline-none focus:ring-2 focus:ring-cyan-500" />
          </div>
          <div class="flex-1">
            <label class="block text-gray-300 mb-1">电话</label>
            <input v-model="formData.phone" class="w-full rounded bg-gray-800 text-cyan-200 px-3 py-2 border border-cyan-700 focus:outline-none focus:ring-2 focus:ring-cyan-500" />
          </div>
        </div>
        <div>
          <label class="block text-gray-300 mb-1">所在地</label>
          <input v-model="formData.location" class="w-full rounded bg-gray-800 text-cyan-200 px-3 py-2 border border-cyan-700 focus:outline-none focus:ring-2 focus:ring-cyan-500" />
        </div>
        <div class="flex justify-end mt-6">
          <button type="submit" :disabled="saving" class="bg-cyan-600 hover:bg-cyan-500 disabled:bg-gray-600 text-white px-6 py-2 rounded-full font-bold shadow transition-all">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, reactive, onMounted } from 'vue'
import { profileApi } from '../api/profile'
import type { ProfileData, UpdateProfileData } from '../api/profile'

const props = defineProps<{
  profile: ProfileData
}>()

const emits = defineEmits<{
  close: []
  save: [profile: ProfileData]
}>()

const avatarPreview = ref<string | null>(null)
const uploading = ref(false)
const saving = ref(false)

const formData = reactive<UpdateProfileData>({
  nickname: '',
  email: '',
  bio: '',
  phone: '',
  location: '',
  skills: [],
  achievements: [],
  activities: [],
  socials: []
})

// 初始化表单数据
onMounted(() => {
  if (props.profile) {
    formData.nickname = props.profile.nickname || ''
    formData.email = props.profile.email || ''
    formData.bio = props.profile.bio || ''
    formData.phone = props.profile.phone || ''
    formData.location = props.profile.location || ''
    formData.skills = props.profile.skills || []
    formData.achievements = props.profile.achievements || []
    formData.activities = props.profile.activities || []
    formData.socials = props.profile.socials || []
  }
})

async function onAvatarChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return

  // 本地预览
  const reader = new FileReader()
  reader.onload = ev => {
    avatarPreview.value = ev.target?.result as string
  }
  reader.readAsDataURL(file)

  // 上传到服务器
  try {
    uploading.value = true
    const response = await profileApi.uploadAvatar(file)
    
    if (response.code === 200) {
      // 上传成功，更新头像URL
      avatarPreview.value = response.data.url
      console.log('头像上传成功:', response.data.url)
    } else {
      console.error('头像上传失败:', response.message)
      avatarPreview.value = null
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    avatarPreview.value = null
  } finally {
    uploading.value = false
  }
}

async function onSave() {
  try {
    saving.value = true
    
    // 如果有新头像，使用新头像URL
    const updateData: UpdateProfileData = {
      ...formData,
      avatar: avatarPreview.value || props.profile.avatar
    }
    
    const response = await profileApi.updateProfile(updateData)
    
    if (response.code === 200) {
      emits('save', response.data)
      console.log('个人资料更新成功')
    } else {
      console.error('个人资料更新失败:', response.message)
    }
  } catch (error) {
    console.error('个人资料更新失败:', error)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped></style> 