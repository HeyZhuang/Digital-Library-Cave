import { api } from './auth'

export interface Comment {
  id?: number
  articleId: number
  userId?: number
  parentId?: number
  content: string
  authorName: string
  authorEmail?: string
  authorIp?: string
  likes?: number
  status?: number
  createdAt?: string
  updatedAt?: string
  articleTitle?: string
  parentAuthorName?: string
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface PageData<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 评论API接口
export const commentApi = {
  // 根据文章ID获取评论列表
  getCommentsByArticleId(articleId: number): Promise<ApiResponse<Comment[]>> {
    return api.get(`/comments/article/${articleId}`)
  },

  // 分页查询评论
  getComments(pageNum: number = 1, pageSize: number = 10, articleId?: number, status?: number): Promise<ApiResponse<PageData<Comment>>> {
    return api.get('/comments', {
      params: { pageNum, pageSize, articleId, status }
    })
  },

  // 获取最新评论
  getLatestComments(limit: number = 10): Promise<ApiResponse<Comment[]>> {
    return api.get('/comments/latest', {
      params: { limit }
    })
  },

  // 根据ID查询评论详情
  getCommentById(id: number): Promise<ApiResponse<Comment>> {
    return api.get(`/comments/${id}`)
  },

  // 创建评论
  createComment(comment: Comment): Promise<ApiResponse<Comment>> {
    return api.post('/comments', comment)
  },

  // 删除评论
  deleteComment(id: number): Promise<ApiResponse<void>> {
    return api.delete(`/comments/${id}`)
  },

  // 审核通过评论
  approveComment(id: number): Promise<ApiResponse<void>> {
    return api.post(`/comments/${id}/approve`)
  },

  // 拒绝评论
  rejectComment(id: number): Promise<ApiResponse<void>> {
    return api.post(`/comments/${id}/reject`)
  },

  // 点赞评论
  likeComment(id: number): Promise<ApiResponse<void>> {
    return api.post(`/comments/${id}/like`)
  },

  // 取消点赞评论
  unlikeComment(id: number): Promise<ApiResponse<void>> {
    return api.delete(`/comments/${id}/like`)
  }
} 