<template>
  <div class="relative bg-gradient-to-tr from-gray-800 via-gray-900 to-gray-950 rounded-3xl shadow-2xl p-8 flex flex-col md:flex-row items-center gap-8 border border-gray-700">
    <div class="relative">
      <img :src="profile.avatar" alt="avatar" class="w-36 h-36 rounded-full border-4 border-cyan-400 shadow-lg object-cover" />
      <button @click="$emit('edit')" class="absolute bottom-2 right-2 bg-cyan-600 hover:bg-cyan-500 text-white rounded-full p-2 shadow-lg transition-all duration-200">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536M9 13l6-6m2 2l-6 6m-2 2h2v2H7v-2z" />
        </svg>
      </button>
    </div>
    <div class="flex-1 flex flex-col gap-3">
      <div class="flex items-center gap-3">
        <h2 class="text-3xl font-extrabold text-cyan-400 tracking-wide">{{ profile.nickname }}</h2>
        <div class="flex gap-2">
          <a v-for="social in profile.socials" :key="social.icon" :href="social.url" target="_blank" class="hover:scale-110 transition-transform">
            <component :is="getIconComponent(social.icon)" class="w-6 h-6 text-gray-300 hover:text-cyan-400" />
          </a>
        </div>
      </div>
      <p class="text-gray-300 text-lg">{{ profile.bio }}</p>
      <div class="flex flex-wrap gap-4 mt-2">
        <div class="flex items-center gap-2 text-gray-400" v-if="profile?.contact?.email">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M16 12a4 4 0 01-8 0V8a4 4 0 018 0v4z" /></svg>
          <span>{{ profile?.contact?.email }}</span>
        </div>
        <div class="flex items-center gap-2 text-gray-400" v-if="profile?.contact?.phone">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M3 5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2H5a2 2 0 01-2-2V5zm16 0a2 2 0 00-2-2h-2a2 2 0 00-2 2v14a2 2 0 002 2h2a2 2 0 002-2V5z" /></svg>
          <span>{{ profile?.contact?.phone }}</span>
        </div>
        <div class="flex items-center gap-2 text-gray-400" v-if="profile?.contact?.location">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 12.414a2 2 0 00-2.828 0l-4.243 4.243A8 8 0 1117.657 16.657z" /></svg>
          <span>{{ profile?.contact?.location }}</span>
        </div>
      </div>
      <div class="flex flex-wrap gap-2 mt-4">
        <span v-for="skill in profile.skills" :key="skill" class="bg-cyan-700/80 text-cyan-100 px-3 py-1 rounded-full text-sm font-mono shadow hover:bg-cyan-500/80 transition-colors">{{ skill }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { h } from 'vue'

interface Social {
  icon: string
  url: string
}

interface Contact {
  email: string
  phone: string
  location: string
}

interface ProfileData {
  avatar: string
  nickname: string
  bio: string
  contact: Contact
  skills: string[]
  socials: Social[]
}

defineProps<{
  profile: ProfileData
}>()

defineEmits<{
  edit: []
}>()

// 极客社交icon映射
function getIconComponent(icon: string) {
  switch (icon) {
    case 'github':
      return {
        render() {
          return h('svg', { class: 'w-6 h-6', fill: 'currentColor', viewBox: '0 0 24 24' }, [
            h('path', { d: 'M12 2C6.48 2 2 6.58 2 12.26c0 4.49 2.87 8.3 6.84 9.64.5.09.68-.22.68-.48 0-.24-.01-.87-.01-1.7-2.78.62-3.37-1.36-3.37-1.36-.45-1.18-1.1-1.5-1.1-1.5-.9-.63.07-.62.07-.62 1 .07 1.53 1.05 1.53 1.05.89 1.56 2.34 1.11 2.91.85.09-.66.35-1.11.63-1.37-2.22-.26-4.56-1.14-4.56-5.07 0-1.12.38-2.03 1-2.75-.1-.26-.44-1.3.1-2.7 0 0 .83-.27 2.75 1.02A9.38 9.38 0 0112 6.84c.84.004 1.68.11 2.47.32 1.92-1.29 2.75-1.02 2.75-1.02.54 1.4.2 2.44.1 2.7.62.72 1 1.63 1 2.75 0 3.94-2.34 4.8-4.57 5.06.36.32.68.94.68 1.9 0 1.37-.01 2.47-.01 2.8 0 .27.18.58.69.48A10.01 10.01 0 0022 12.26C22 6.58 17.52 2 12 2z' })
          ])
        }
      }
    case 'twitter':
      return {
        render() {
          return h('svg', { class: 'w-6 h-6', fill: 'currentColor', viewBox: '0 0 24 24' }, [
            h('path', { d: 'M22.46 6c-.77.35-1.6.58-2.47.69a4.3 4.3 0 001.88-2.37 8.59 8.59 0 01-2.72 1.04A4.28 4.28 0 0016.11 4c-2.37 0-4.29 2-4.29 4.36 0 .34.04.67.1.99C7.69 9.18 4.07 7.38 1.64 4.7c-.37.65-.58 1.4-.58 2.2 0 1.52.76 2.86 1.92 3.65-.7-.02-1.36-.22-1.94-.54v.05c0 2.13 1.48 3.91 3.44 4.31-.36.1-.74.16-1.13.16-.28 0-.54-.03-.8-.08.54 1.7 2.1 2.94 3.95 2.97A8.6 8.6 0 012 19.54c-.63 0-1.25-.04-1.86-.11A12.13 12.13 0 006.29 21c7.55 0 11.68-6.41 11.68-11.98 0-.18-.01-.36-.02-.54A8.7 8.7 0 0024 4.59a8.48 8.48 0 01-2.54.7z' })
          ])
        }
      }
    case 'linkedin':
      return {
        render() {
          return h('svg', { class: 'w-6 h-6', fill: 'currentColor', viewBox: '0 0 24 24' }, [
            h('path', { d: 'M19 0h-14c-2.76 0-5 2.24-5 5v14c0 2.76 2.24 5 5 5h14c2.76 0 5-2.24 5-5v-14c0-2.76-2.24-5-5-5zm-11 19h-3v-10h3v10zm-1.5-11.28c-.97 0-1.75-.79-1.75-1.75s.78-1.75 1.75-1.75 1.75.79 1.75 1.75-.78 1.75-1.75 1.75zm15.5 11.28h-3v-5.6c0-1.34-.03-3.07-1.87-3.07-1.87 0-2.16 1.46-2.16 2.97v5.7h-3v-10h2.89v1.36h.04c.4-.75 1.37-1.54 2.82-1.54 3.01 0 3.57 1.98 3.57 4.56v5.62z' })
          ])
        }
      }
    default:
      return {
        render() {
          return h('span', icon)
        }
      }
  }
}
</script>

<style scoped>
</style> 