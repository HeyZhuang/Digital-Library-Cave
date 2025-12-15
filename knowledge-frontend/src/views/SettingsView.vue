<template>
  <div class="settings-view min-h-screen bg-gradient-to-br from-deepSpace-900 via-deepSpace-800 to-deepSpace-900">
    <!-- 背景装饰 -->
    <div class="absolute inset-0 overflow-hidden">
      <!-- 金色粒子背景 -->
      <div class="absolute inset-0 opacity-10">
        <div v-for="i in 50" :key="i" 
             class="absolute w-1 h-1 bg-gold-400 rounded-full animate-pulse"
             :style="{ 
               top: Math.random() * 100 + '%', 
               left: Math.random() * 100 + '%',
               animationDelay: Math.random() * 3 + 's' 
             }">
        </div>
      </div>
      
      <!-- 古典纹样 -->
      <div class="absolute top-0 left-0 w-full h-32 bg-gradient-to-b from-gold-600/5 to-transparent"></div>
      <div class="absolute bottom-0 left-0 w-full h-32 bg-gradient-to-t from-gold-600/5 to-transparent"></div>
    </div>

    <!-- 主内容 -->
    <div class="relative z-10 container mx-auto px-4 py-8">
      <!-- 页面标题 -->
      <div class="text-center mb-12">
        <div class="inline-flex items-center space-x-4 mb-6">
          <div class="w-12 h-12 bg-gradient-gold rounded-lg flex items-center justify-center shadow-lg">
            <svg class="w-7 h-7 text-deepSpace-900" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
          </div>
          <h1 class="text-4xl font-title text-parchment-100 font-bold tracking-wide">设置偏好</h1>
        </div>
        <p class="text-parchment-400 text-lg font-body max-w-2xl mx-auto">
          个性化定制您的数字藏金阁，打造专属的知识管理体验
        </p>
      </div>

      <!-- 设置选项卡 -->
      <div class="max-w-6xl mx-auto">
        <div class="flex flex-wrap justify-center mb-8 border-b border-gold-600/20">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            @click="activeTab = tab.id"
            :class="[
              'px-6 py-3 text-sm font-medium transition-all duration-300 border-b-2 mx-2',
              activeTab === tab.id
                ? 'text-gold-400 border-gold-400 bg-gold-600/10'
                : 'text-parchment-300 border-transparent hover:text-gold-300 hover:border-gold-600/50'
            ]"
          >
            <component :is="tab.icon" class="w-5 h-5 inline-block mr-2" />
            {{ tab.label }}
          </button>
        </div>

        <!-- 设置内容区域 -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <!-- 主要设置面板 -->
          <div class="lg:col-span-2 space-y-6">
            <!-- 个人信息设置 -->
            <div v-if="activeTab === 'profile'" class="settings-panel">
              <div class="panel-header">
                <h3 class="text-xl font-semibold text-parchment-100 mb-2">个人信息</h3>
                <p class="text-parchment-400 text-sm">管理您的个人资料和基本信息</p>
              </div>
              
              <div class="panel-content space-y-6">
                <!-- 头像设置 -->
                <div class="flex items-center space-x-6">
                  <div class="relative group">
                    <div class="w-20 h-20 rounded-full border-2 border-gold-600/30 overflow-hidden bg-gradient-to-br from-gold-400/20 to-gold-600/20">
                      <img 
                        :src="settings.profile.avatar || 'https://via.placeholder.com/100/1e293b/d4af37?text=' + (settings.profile.nickname?.charAt(0) || 'U')" 
                        alt="头像"
                        class="w-full h-full object-cover"
                      >
                    </div>
                    <input 
                      ref="avatarInput"
                      type="file" 
                      accept="image/*" 
                      class="hidden"
                      @change="handleAvatarChange"
                    >
                    <div 
                      @click="$refs.avatarInput?.click()"
                      class="absolute inset-0 bg-black/50 rounded-full opacity-0 group-hover:opacity-100 transition-all duration-300 cursor-pointer flex items-center justify-center transform group-hover:scale-105"
                    >
                      <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"></path>
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z"></path>
                      </svg>
                    </div>
                    <div v-if="uploadingAvatar" class="absolute inset-0 bg-black/70 rounded-full flex items-center justify-center">
                      <div class="w-6 h-6 border-2 border-gold-400 border-t-transparent rounded-full animate-spin"></div>
                    </div>
                  </div>
                  <div class="flex-1">
                    <input 
                      v-model="settings.profile.nickname"
                      type="text" 
                      placeholder="昵称"
                      class="form-input w-full mb-3"
                    >
                    <input 
                      v-model="settings.profile.email"
                      type="email" 
                      placeholder="邮箱地址"
                      class="form-input w-full"
                    >
                  </div>
                </div>

                <!-- 个人简介 -->
                <div>
                  <label class="form-label">个人简介</label>
                  <textarea 
                    v-model="settings.profile.bio"
                    placeholder="介绍一下自己..."
                    rows="3"
                    class="form-input w-full"
                  ></textarea>
                </div>
              </div>
            </div>

            <!-- 外观设置 -->
            <div v-if="activeTab === 'appearance'" class="settings-panel">
              <div class="panel-header">
                <h3 class="text-xl font-semibold text-parchment-100 mb-2">外观设置</h3>
                <p class="text-parchment-400 text-sm">自定义界面外观和主题</p>
              </div>
              
              <div class="panel-content space-y-6">
                <!-- 主题选择 -->
                <div>
                  <label class="form-label">主题模式</label>
                  <div class="grid grid-cols-3 gap-4">
                    <div 
                      v-for="theme in themes"
                      :key="theme.id"
                      @click="settings.appearance.theme = theme.id"
                      :class="[
                        'theme-card cursor-pointer p-4 rounded-lg border-2 transition-all duration-300',
                        settings.appearance.theme === theme.id
                          ? 'border-gold-400 bg-gold-600/10'
                          : 'border-gold-600/20 hover:border-gold-600/40'
                      ]"
                    >
                      <div :class="theme.preview" class="w-full h-16 rounded mb-3"></div>
                      <h4 class="text-parchment-100 font-medium text-center">{{ theme.name }}</h4>
                    </div>
                  </div>
                </div>

                <!-- 字体大小 -->
                <div>
                  <label class="form-label">字体大小</label>
                  <div class="flex items-center space-x-4">
                    <span class="text-parchment-400 text-sm">小</span>
                    <input 
                      v-model="settings.appearance.fontSize"
                      type="range" 
                      min="12" 
                      max="20" 
                      step="1"
                      class="flex-1 slider"
                    >
                    <span class="text-parchment-400 text-sm">大</span>
                    <span class="text-gold-400 font-medium w-8">{{ settings.appearance.fontSize }}</span>
                  </div>
                </div>

                <!-- 动画效果 -->
                <div class="flex items-center justify-between">
                  <div>
                    <label class="form-label">动画效果</label>
                    <p class="text-parchment-400 text-sm">启用界面过渡动画</p>
                  </div>
                  <toggle-switch v-model="settings.appearance.animations" />
                </div>
              </div>
            </div>

            <!-- 通知设置 -->
            <div v-if="activeTab === 'notifications'" class="settings-panel">
              <div class="panel-header">
                <h3 class="text-xl font-semibold text-parchment-100 mb-2">通知设置</h3>
                <p class="text-parchment-400 text-sm">管理您的通知偏好</p>
              </div>
              
              <div class="panel-content space-y-6">
                <div v-for="notification in notificationTypes" :key="notification.id" class="flex items-center justify-between">
                  <div>
                    <h4 class="text-parchment-100 font-medium">{{ notification.title }}</h4>
                    <p class="text-parchment-400 text-sm">{{ notification.description }}</p>
                  </div>
                  <toggle-switch v-model="(settings.notifications as any)[notification.id]" />
                </div>
              </div>
            </div>

            <!-- 隐私设置 -->
            <div v-if="activeTab === 'privacy'" class="settings-panel">
              <div class="panel-header">
                <h3 class="text-xl font-semibold text-parchment-100 mb-2">隐私与安全</h3>
                <p class="text-parchment-400 text-sm">保护您的隐私和账户安全</p>
              </div>
              
              <div class="panel-content space-y-6">
                <!-- 密码修改 -->
                <div>
                  <label class="form-label">修改密码</label>
                  <div class="space-y-3">
                    <input type="password" placeholder="当前密码" class="form-input w-full">
                    <input type="password" placeholder="新密码" class="form-input w-full">
                    <input type="password" placeholder="确认新密码" class="form-input w-full">
                  </div>
                  <button class="btn-secondary mt-3">更新密码</button>
                </div>

                <!-- 隐私选项 -->
                <div class="space-y-4">
                  <div class="flex items-center justify-between">
                    <div>
                      <h4 class="text-parchment-100 font-medium">公开个人资料</h4>
                      <p class="text-parchment-400 text-sm">允许其他用户查看您的个人资料</p>
                    </div>
                    <toggle-switch v-model="settings.privacy.publicProfile" />
                  </div>
                  
                  <div class="flex items-center justify-between">
                    <div>
                      <h4 class="text-parchment-100 font-medium">显示在线状态</h4>
                      <p class="text-parchment-400 text-sm">让其他用户看到您是否在线</p>
                    </div>
                    <toggle-switch v-model="settings.privacy.showOnlineStatus" />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 侧边栏预览 -->
          <div class="lg:col-span-1">
            <div class="settings-panel sticky top-8">
              <div class="panel-header">
                <h3 class="text-lg font-semibold text-parchment-100 mb-2">预览效果</h3>
                <p class="text-parchment-400 text-sm">实时预览您的设置效果</p>
              </div>
              
              <div class="panel-content">
                <!-- 预览区域 -->
                <div class="preview-area bg-deepSpace-800 rounded-lg p-4 border border-gold-600/20">
                  <div class="flex items-center space-x-3 mb-4">
                    <img 
                      :src="settings.profile.avatar || '/api/placeholder/40/40'" 
                      alt="预览头像"
                      class="w-10 h-10 rounded-full border border-gold-600/30"
                    >
                    <div>
                      <h4 class="text-parchment-100 font-medium" :style="{ fontSize: settings.appearance.fontSize + 'px' }">
                        {{ settings.profile.nickname || '未设置昵称' }}
                      </h4>
                      <p class="text-parchment-400 text-xs">{{ settings.profile.email || '未设置邮箱' }}</p>
                    </div>
                  </div>
                  
                  <div class="text-parchment-300 text-sm" :style="{ fontSize: (settings.appearance.fontSize - 2) + 'px' }">
                    {{ settings.profile.bio || '这里是您的个人简介预览...' }}
                  </div>
                </div>

                <!-- 快速统计 -->
                <div class="mt-6 grid grid-cols-2 gap-4">
                  <div class="stat-card">
                    <div class="text-2xl font-bold text-gold-400">{{ stats.articles }}</div>
                    <div class="text-parchment-400 text-sm">文章数量</div>
                  </div>
                  <div class="stat-card">
                    <div class="text-2xl font-bold text-gold-400">{{ stats.views }}</div>
                    <div class="text-parchment-400 text-sm">总浏览量</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 保存按钮 -->
        <div class="mt-12 flex justify-center space-x-4">
          <button @click="resetSettings" class="btn-secondary">重置设置</button>
          <button @click="saveSettings" class="btn-primary">
            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
            </svg>
            保存设置
          </button>
        </div>
      </div>
    </div>

    <!-- 快捷操作按钮 -->
    <div class="fixed bottom-6 right-6 z-20">
      <!-- 主FAB按钮 -->
      <div class="relative">
        <button 
          @click="toggleQuickMenu"
          :class="[
            'w-14 h-14 bg-gradient-gold rounded-full shadow-2xl flex items-center justify-center transition-all duration-300 hover:scale-110 focus:outline-none focus:ring-4 focus:ring-gold-400/50',
            showQuickMenu ? 'rotate-45' : 'rotate-0'
          ]"
        >
          <svg class="w-6 h-6 text-deepSpace-900" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
          </svg>
        </button>
        
        <!-- 快捷菜单 -->
        <div 
          v-if="showQuickMenu"
          class="absolute bottom-16 right-0 space-y-3 transition-all duration-300"
        >
          <!-- 导出设置 -->
          <button 
            @click="exportSettings"
            class="w-12 h-12 bg-blue-600 rounded-full shadow-lg flex items-center justify-center text-white hover:scale-110 transition-all duration-200 tooltip"
            title="导出设置"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
            </svg>
          </button>
          
          <!-- 导入设置 -->
          <button 
            @click="importSettings"
            class="w-12 h-12 bg-green-600 rounded-full shadow-lg flex items-center justify-center text-white hover:scale-110 transition-all duration-200 tooltip"
            title="导入设置"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M9 19l3 3m0 0l3-3m-3 3V10"></path>
            </svg>
          </button>
          
          <!-- 重置所有 -->
          <button 
            @click="resetAllSettings"
            class="w-12 h-12 bg-red-600 rounded-full shadow-lg flex items-center justify-center text-white hover:scale-110 transition-all duration-200 tooltip"
            title="重置所有设置"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- 保存提示 -->
    <notification-toast 
      :show="showToast"
      :type="toastType"
      :title="toastTitle"
      :message="toastMessage"
      @close="showToast = false"
    />

    <!-- 导入文件选择器 -->
    <input 
      ref="importInput"
      type="file" 
      accept=".json"
      class="hidden"
      @change="handleImportFile"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import NotificationToast from '../components/NotificationToast.vue'

// 注册组件
const ToggleSwitch = {
  props: ['modelValue'],
  emits: ['update:modelValue'],
  template: `
    <button 
      @click="$emit('update:modelValue', !modelValue)"
      :class="[
        'relative inline-flex h-6 w-11 items-center rounded-full transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-gold-500 focus:ring-offset-2 shadow-lg',
        modelValue ? 'bg-gradient-to-r from-gold-500 to-gold-600' : 'bg-gray-600'
      ]"
    >
      <span
        :class="[
          'inline-block h-4 w-4 transform rounded-full bg-white transition-all duration-300 shadow-sm',
          modelValue ? 'translate-x-6 bg-gold-100' : 'translate-x-1'
        ]"
      />
    </button>
  `
}

const components = {
  'toggle-switch': ToggleSwitch,
  'notification-toast': NotificationToast
}

// 图标组件
const UserIcon = {
  template: `<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>`
}

const PaletteIcon = {
  template: `<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zM21 5a2 2 0 00-2-2h-4a2 2 0 00-2 2v12a4 4 0 004 4h4a2 2 0 002-2V5z"></path></svg>`
}

const BellIcon = {
  template: `<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-5 5-5-5h5V3h5v14z"></path></svg>`
}

const ShieldIcon = {
  template: `<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path></svg>`
}

// 数据
const authStore = useAuthStore()
const activeTab = ref('profile')
const showToast = ref(false)
const toastType = ref<'success' | 'error' | 'warning' | 'info'>('success')
const toastTitle = ref('')
const toastMessage = ref('')
const uploadingAvatar = ref(false)
const avatarInput = ref<HTMLInputElement>()
const showQuickMenu = ref(false)
const importInput = ref<HTMLInputElement>()

// 选项卡定义
const tabs = [
  { id: 'profile', label: '个人信息', icon: UserIcon },
  { id: 'appearance', label: '外观设置', icon: PaletteIcon },
  { id: 'notifications', label: '通知设置', icon: BellIcon },
  { id: 'privacy', label: '隐私安全', icon: ShieldIcon }
]

// 主题选项
const themes = [
  { 
    id: 'dark', 
    name: '深邃夜空', 
    preview: 'bg-gradient-to-br from-deepSpace-900 to-deepSpace-800' 
  },
  { 
    id: 'gold', 
    name: '鎏金典雅', 
    preview: 'bg-gradient-to-br from-gold-600 to-gold-800' 
  },
  { 
    id: 'parchment', 
    name: '羊皮纸韵', 
    preview: 'bg-gradient-to-br from-parchment-400 to-parchment-600' 
  }
]

// 通知类型
const notificationTypes = [
  { 
    id: 'comments', 
    title: '评论通知', 
    description: '有人评论您的文章时通知您' 
  },
  { 
    id: 'likes', 
    title: '点赞通知', 
    description: '有人点赞您的内容时通知您' 
  },
  { 
    id: 'follows', 
    title: '关注通知', 
    description: '有新用户关注您时通知您' 
  },
  { 
    id: 'system', 
    title: '系统通知', 
    description: '接收系统重要消息和更新' 
  }
]

// 设置数据
const settings = reactive({
  profile: {
    avatar: '',
    nickname: '',
    email: '',
    bio: ''
  },
  appearance: {
    theme: 'dark',
    fontSize: 14,
    animations: true
  },
  notifications: {
    comments: true,
    likes: true,
    follows: true,
    system: true
  },
  privacy: {
    publicProfile: true,
    showOnlineStatus: true
  }
})

// 统计数据
const stats = reactive({
  articles: 42,
  views: 8756
})

// 方法
const saveSettings = async () => {
  try {
    // 保存到localStorage
    localStorage.setItem('userSettings', JSON.stringify(settings))
    
    // 这里可以调用API保存到服务器
    console.log('保存设置:', settings)
    
    showToast.value = true
    toastType.value = 'success'
    toastTitle.value = '设置已保存'
    toastMessage.value = '您的偏好设置已成功保存'
  } catch (error) {
    showToast.value = true
    toastType.value = 'error'
    toastTitle.value = '保存失败'
    toastMessage.value = '设置保存时出现错误，请重试'
  }
}

const resetSettings = () => {
  if (confirm('确定要重置所有设置吗？此操作不可撤销。')) {
    // 重置为默认值
    Object.assign(settings, {
      profile: {
        avatar: '',
        nickname: '',
        email: '',
        bio: ''
      },
      appearance: {
        theme: 'dark',
        fontSize: 14,
        animations: true
      },
      notifications: {
        comments: true,
        likes: true,
        follows: true,
        system: true
      },
      privacy: {
        publicProfile: true,
        showOnlineStatus: true
      }
    })
    
    showToast.value = true
    toastType.value = 'info'
    toastTitle.value = '设置已重置'
    toastMessage.value = '所有设置已恢复为默认值'
  }
}

// 头像上传处理
const handleAvatarChange = async (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return

  // 检查文件类型和大小
  if (!file.type.startsWith('image/')) {
    showToast.value = true
    toastType.value = 'error'
    toastTitle.value = '文件类型错误'
    toastMessage.value = '请选择图片文件'
    return
  }

  if (file.size > 5 * 1024 * 1024) { // 5MB限制
    showToast.value = true
    toastType.value = 'error'
    toastTitle.value = '文件过大'
    toastMessage.value = '头像文件不能超过5MB'
    return
  }

  try {
    uploadingAvatar.value = true
    
    // 创建本地预览
    const reader = new FileReader()
    reader.onload = (e) => {
      settings.profile.avatar = e.target?.result as string
    }
    reader.readAsDataURL(file)

    // 模拟上传过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    showToast.value = true
    toastType.value = 'success'
    toastTitle.value = '头像上传成功'
    toastMessage.value = '您的头像已更新'
  } catch (error) {
    showToast.value = true
    toastType.value = 'error'
    toastTitle.value = '上传失败'
    toastMessage.value = '头像上传失败，请重试'
  } finally {
    uploadingAvatar.value = false
  }
}

// 初始化
onMounted(() => {
  // 从用户信息中获取当前设置
  if (authStore.userInfo) {
    settings.profile.avatar = authStore.userInfo.avatar || ''
    settings.profile.nickname = authStore.userInfo.nickname || ''
    settings.profile.email = authStore.userInfo.email || ''
    settings.profile.bio = '' // 暂时设为空，后续可以从其他地方获取
  }

  // 加载用户偏好设置
  loadUserPreferences()
})

// 快捷菜单控制
const toggleQuickMenu = () => {
  showQuickMenu.value = !showQuickMenu.value
}

// 导出设置
const exportSettings = () => {
  try {
    const settingsData = JSON.stringify(settings, null, 2)
    const blob = new Blob([settingsData], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    
    const a = document.createElement('a')
    a.href = url
    a.download = `settings-${new Date().toISOString().split('T')[0]}.json`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
    
    showToast.value = true
    toastType.value = 'success'
    toastTitle.value = '导出成功'
    toastMessage.value = '设置文件已下载到本地'
    showQuickMenu.value = false
  } catch (error) {
    showToast.value = true
    toastType.value = 'error'
    toastTitle.value = '导出失败'
    toastMessage.value = '导出设置时出现错误'
  }
}

// 导入设置
const importSettings = () => {
  importInput.value?.click()
  showQuickMenu.value = false
}

// 处理导入文件
const handleImportFile = async (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return
  
  try {
    const text = await file.text()
    const importedSettings = JSON.parse(text)
    
    // 验证设置格式
    if (importedSettings.profile && importedSettings.appearance) {
      Object.assign(settings, importedSettings)
      
      showToast.value = true
      toastType.value = 'success'
      toastTitle.value = '导入成功'
      toastMessage.value = '设置已从文件导入'
    } else {
      throw new Error('设置文件格式不正确')
    }
  } catch (error) {
    showToast.value = true
    toastType.value = 'error'
    toastTitle.value = '导入失败'
    toastMessage.value = '设置文件格式不正确或已损坏'
  }
  
  // 清空input值
  if (importInput.value) {
    importInput.value.value = ''
  }
}

// 重置所有设置
const resetAllSettings = () => {
  if (confirm('确定要重置所有设置吗？此操作将清除所有个人偏好设置，不可撤销！')) {
    localStorage.removeItem('userSettings')
    
    // 重置为默认值
    Object.assign(settings, {
      profile: {
        avatar: '',
        nickname: '',
        email: '',
        bio: ''
      },
      appearance: {
        theme: 'dark',
        fontSize: 14,
        animations: true
      },
      notifications: {
        comments: true,
        likes: true,
        follows: true,
        system: true
      },
      privacy: {
        publicProfile: true,
        showOnlineStatus: true
      }
    })
    
    showToast.value = true
    toastType.value = 'warning'
    toastTitle.value = '设置已重置'
    toastMessage.value = '所有设置已恢复为默认值'
    showQuickMenu.value = false
  }
}

// 加载用户偏好设置
const loadUserPreferences = async () => {
  try {
    // 从localStorage或API加载设置
    const savedSettings = localStorage.getItem('userSettings')
    if (savedSettings) {
      const parsed = JSON.parse(savedSettings)
      Object.assign(settings, parsed)
    }
  } catch (error) {
    console.log('加载设置失败:', error)
  }
}
</script>

<style scoped>
.settings-panel {
  @apply bg-deepSpace-800/80 backdrop-blur-lg border border-gold-600/20 rounded-xl p-6 shadow-2xl;
}

.panel-header {
  @apply border-b border-gold-600/20 pb-4 mb-6;
}

.panel-content {
  @apply space-y-4;
}

.form-label {
  @apply block text-parchment-200 font-medium mb-2;
}

.form-input {
  @apply bg-deepSpace-700/80 border border-gold-600/30 rounded-lg px-4 py-3 text-parchment-100 placeholder-parchment-400 focus:outline-none focus:ring-2 focus:ring-gold-500 focus:border-transparent transition-all duration-300;
}

.form-input:focus {
  @apply bg-deepSpace-700 border-gold-500;
}

.btn-primary {
  @apply inline-flex items-center px-6 py-3 bg-gradient-gold text-deepSpace-900 font-semibold rounded-lg shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-300;
}

.btn-secondary {
  @apply inline-flex items-center px-6 py-3 bg-deepSpace-700 text-parchment-100 font-semibold rounded-lg border border-gold-600/30 hover:bg-deepSpace-600 hover:border-gold-500 transition-all duration-300;
}

.theme-card {
  @apply transform hover:scale-105 transition-all duration-300;
}

.slider {
  @apply appearance-none bg-deepSpace-700 h-2 rounded-full outline-none;
}

.slider::-webkit-slider-thumb {
  @apply appearance-none w-5 h-5 bg-gold-500 rounded-full cursor-pointer shadow-lg;
}

.slider::-moz-range-thumb {
  @apply w-5 h-5 bg-gold-500 rounded-full cursor-pointer shadow-lg border-none;
}

.stat-card {
  @apply bg-deepSpace-700/50 rounded-lg p-4 text-center border border-gold-600/10;
}

.preview-area {
  @apply transform transition-all duration-300;
}

/* 渐变背景 */
.bg-gradient-gold {
  background: linear-gradient(135deg, #d4af37, #f1c40f, #d4af37);
}

/* 动画效果 */
@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.settings-panel {
  animation: float 6s ease-in-out infinite;
}

.settings-panel:nth-child(2) {
  animation-delay: -2s;
}

.settings-panel:nth-child(3) {
  animation-delay: -4s;
}

/* Tooltip样式 */
.tooltip {
  position: relative;
}

.tooltip:hover::before {
  content: attr(title);
  position: absolute;
  bottom: 120%;
  right: 50%;
  transform: translateX(50%);
  background: rgba(0, 0, 0, 0.9);
  color: white;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 1000;
  opacity: 1;
  pointer-events: none;
}

.tooltip:hover::after {
  content: '';
  position: absolute;
  bottom: 110%;
  right: 50%;
  transform: translateX(50%);
  border: 5px solid transparent;
  border-top: 5px solid rgba(0, 0, 0, 0.9);
  z-index: 1000;
}

/* 高级渐变背景 */
.bg-gradient-gold {
  background: linear-gradient(135deg, #d4af37 0%, #f1c40f 25%, #d4af37 50%, #b8860b 75%, #d4af37 100%);
  background-size: 200% 200%;
  animation: gradientShift 4s ease infinite;
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* 粒子动画 */
@keyframes particleFloat {
  0%, 100% { 
    transform: translateY(0px) rotate(0deg);
    opacity: 0.3;
  }
  25% { 
    transform: translateY(-20px) rotate(90deg);
    opacity: 0.6;
  }
  50% { 
    transform: translateY(-10px) rotate(180deg);
    opacity: 0.9;
  }
  75% { 
    transform: translateY(-30px) rotate(270deg);
    opacity: 0.4;
  }
}

.settings-view .absolute.w-1.h-1 {
  animation: particleFloat 6s ease-in-out infinite;
}

/* 面板进入动画 */
.settings-panel {
  animation: panelSlideIn 0.6s ease-out;
}

@keyframes panelSlideIn {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 卡片悬浮效果 */
.settings-panel:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(212, 175, 55, 0.3);
}

/* 快捷菜单动画 */
.absolute.bottom-16 > button {
  animation: menuItemSlide 0.3s ease-out;
}

.absolute.bottom-16 > button:nth-child(1) { animation-delay: 0.1s; }
.absolute.bottom-16 > button:nth-child(2) { animation-delay: 0.2s; }
.absolute.bottom-16 > button:nth-child(3) { animation-delay: 0.3s; }

@keyframes menuItemSlide {
  from {
    opacity: 0;
    transform: translateX(50px) scale(0.8);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

/* 滑块增强样式 */
.slider {
  background: linear-gradient(to right, #374151, #d4af37);
  background-size: 100% 100%;
}

.slider:hover {
  background: linear-gradient(to right, #4b5563, #f1c40f);
}

/* 输入框增强效果 */
.form-input:focus {
  box-shadow: 0 0 0 3px rgba(212, 175, 55, 0.1), 0 0 20px rgba(212, 175, 55, 0.2);
  transform: translateY(-1px);
}

/* 按钮悬浮效果 */
.btn-primary:hover {
  background: linear-gradient(135deg, #f1c40f 0%, #d4af37 50%, #b8860b 100%);
  box-shadow: 0 10px 30px rgba(212, 175, 55, 0.4);
}

.btn-secondary:hover {
  background: linear-gradient(135deg, #374151, #4b5563);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

/* 选项卡增强效果 */
.flex.flex-wrap.justify-center.mb-8 button {
  position: relative;
  overflow: hidden;
}

.flex.flex-wrap.justify-center.mb-8 button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(212, 175, 55, 0.2), transparent);
  transition: left 0.5s;
}

.flex.flex-wrap.justify-center.mb-8 button:hover::before {
  left: 100%;
}

/* 统计卡片动画 */
.stat-card {
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: scale(1.05);
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.1), rgba(212, 175, 55, 0.05));
}

/* 响应式设计 */
@media (max-width: 768px) {
  .settings-view {
    @apply px-2;
  }
  
  .fixed.bottom-6.right-6 {
    @apply bottom-4 right-4;
  }
  
  .w-14.h-14 {
    @apply w-12 h-12;
  }
  
  .w-12.h-12 {
    @apply w-10 h-10;
  }
}

@media (max-width: 640px) {
  .text-4xl {
    @apply text-2xl;
  }
  
  .px-6.py-3 {
    @apply px-3 py-2;
  }
  
  .settings-panel {
    @apply p-4;
  }
}
</style> 