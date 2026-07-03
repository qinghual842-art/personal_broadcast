import request from './request'

export function getComments(articleId, params) {
  return request.get(`/articles/${articleId}/comments`, { params })
}

export function submitComment(articleId, data) {
  return request.post(`/articles/${articleId}/comments`, data)
}

export function getAdminComments(params) {
  return request.get('/admin/comments', { params })
}

export function updateCommentStatus(id, status) {
  return request.put(`/admin/comments/${id}/status?status=${status}`)
}

export function deleteComment(id) {
  return request.delete(`/admin/comments/${id}`)
}

export function batchDeleteComments(ids) {
  return request.delete('/admin/comments/batch', { data: ids })
}
