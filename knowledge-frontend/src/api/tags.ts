import { api } from './auth'

export interface Tag {
  id?: number
  name: string
  color?: string
  heat?: number
  articleCount?: number
  createdAt?: string
  updatedAt?: string
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 标签API接口
export const tagApi = {
  // 获取所有标签
  getAllTags(): Promise<ApiResponse<Tag[]>> {
    return api.get('/tags')
  },

  // 获取热门标签
  getPopularTags(limit: number = 20): Promise<ApiResponse<Tag[]>> {
    return api.get('/tags/popular', {
      params: { limit }
    })
  },

  // 根据文章ID获取标签
  getTagsByArticleId(articleId: number): Promise<ApiResponse<Tag[]>> {
    return api.get(`/tags/article/${articleId}`)
  },

  // 搜索标签
  searchTags(name: string): Promise<ApiResponse<Tag[]>> {
    return api.get('/tags/search', {
      params: { name }
    })
  },

  // 根据ID查询标签详情
  getTagById(id: number): Promise<ApiResponse<Tag>> {
    return api.get(`/tags/${id}`)
  },

  // 创建标签
  createTag(name: string, color?: string): Promise<ApiResponse<Tag>> {
    return api.post('/tags', null, {
      params: { name, color }
    })
  },

  // 更新标签
  updateTag(id: number, tag: Tag): Promise<ApiResponse<Tag>> {
    return api.put(`/tags/${id}`, tag)
  },

  // 删除标签
  deleteTag(id: number): Promise<ApiResponse<void>> {
    return api.delete(`/tags/${id}`)
  }
} 