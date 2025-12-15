import { api } from './auth'

export interface ProfileData {
  id: number
  username: string
  nickname: string
  email: string
  avatar: string
  bio: string
  phone: string
  location: string
  skills: string[]
  achievements: Array<{
    icon: string
    title: string
    desc: string
  }>
  activities: Array<{
    type: string
    content: string
    date: string
  }>
  socials: Array<{
    icon: string
    url: string
  }>
}

export interface UpdateProfileData {
  nickname?: string
  email?: string
  bio?: string
  phone?: string
  location?: string
  skills?: string[]
  achievements?: Array<{
    icon: string
    title: string
    desc: string
  }>
  activities?: Array<{
    type: string
    content: string
    date: string
  }>
  socials?: Array<{
    icon: string
    url: string
  }>
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

// Profile API接口
export const profileApi = {
  // 上传头像
  uploadAvatar(file: File): Promise<ApiResponse<{
    url: string
    filename: string
    size: number
    user: ProfileData
  }>> {
    const formData = new FormData()
    formData.append('file', file)
    
    return api.post('/files/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 更新个人资料
  updateProfile(data: UpdateProfileData): Promise<ApiResponse<ProfileData>> {
    return api.put('/auth/me', data)
  },

  // 获取个人资料
  getProfile(): Promise<ApiResponse<ProfileData>> {
    return api.get('/auth/me')
  },

  // 通用文件上传
  uploadFile(file: File, type: string = 'general'): Promise<ApiResponse<{
    url: string
    filename: string
    originalFilename: string
    size: number
    type: string
  }>> {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)
    
    return api.post('/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
} 