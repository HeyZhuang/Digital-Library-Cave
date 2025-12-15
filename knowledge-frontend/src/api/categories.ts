import { request } from '../config/api'

export interface Category {
  id: number
  name: string
  description?: string
  parentId?: number
  sortOrder: number
  createdAt: string
  updatedAt: string
  articleCount?: number
}

export interface CreateCategoryRequest {
  name: string
  description?: string
  parentId?: number
  sortOrder?: number
}

export interface UpdateCategoryRequest {
  id: number
  name: string
  description?: string
  parentId?: number
  sortOrder?: number
}

// 获取所有分类
export const getCategories = async (): Promise<Category[]> => {
  try {
    const response = await request.get('/categories')
    return response.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
    throw error
  }
}

// 根据ID获取分类
export const getCategoryById = async (id: number): Promise<Category> => {
  try {
    const response = await request.get(`/categories/${id}`)
    return response.data
  } catch (error) {
    console.error('获取分类详情失败:', error)
    throw error
  }
}

// 创建分类
export const createCategory = async (data: CreateCategoryRequest): Promise<Category> => {
  try {
    const response = await request.post('/categories', data)
    return response.data
  } catch (error) {
    console.error('创建分类失败:', error)
    throw error
  }
}

// 更新分类
export const updateCategory = async (data: UpdateCategoryRequest): Promise<Category> => {
  try {
    const response = await request.put(`/categories/${data.id}`, data)
    return response.data
  } catch (error) {
    console.error('更新分类失败:', error)
    throw error
  }
}

// 删除分类
export const deleteCategory = async (id: number): Promise<void> => {
  try {
    await request.delete(`/categories/${id}`)
  } catch (error) {
    console.error('删除分类失败:', error)
    throw error
  }
}

// 获取分类统计数据
export interface CategoryStats {
  id: number
  name: string
  description?: string
  icon?: string
  color?: string
  articleCount: number
  progress: number
  topics: string[]
  totalViews?: number
  lastUpdated?: string
}

export const getCategoryStats = async (): Promise<CategoryStats[]> => {
  try {
    const response = await request.get('/categories/stats')
    return response.data
  } catch (error) {
    console.error('获取分类统计失败:', error)
    throw error
  }
} 