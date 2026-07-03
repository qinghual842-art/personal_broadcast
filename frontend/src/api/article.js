import request from './request'

export function getArticles(params) {
  return request.get('/articles', { params })
}

export function getArticleDetail(idOrSlug) {
  return request.get(`/articles/${idOrSlug}`)
}

export function searchArticles(params) {
  return request.get('/articles/search', { params })
}

export function getHotArticles(limit = 5) {
  return request.get('/articles/hot', { params: { limit } })
}

export function getArchive() {
  return request.get('/articles/archive')
}

export function getAdminArticles(params) {
  return request.get('/admin/articles', { params })
}

export function getAdminArticle(id) {
  return request.get(`/admin/articles/${id}`)
}

export function createArticle(data) {
  return request.post('/admin/articles', data)
}

export function updateArticle(id, data) {
  return request.put(`/admin/articles/${id}`, data)
}

export function deleteArticle(id) {
  return request.delete(`/admin/articles/${id}`)
}

export function batchDeleteArticles(ids) {
  return request.delete('/admin/articles/batch', { data: ids })
}

export function updateArticleStatus(id, status) {
  return request.put(`/admin/articles/${id}/status?status=${status}`)
}

export function toggleLike(articleId) {
  return request.post(`/articles/${articleId}/like`)
}

export function checkLiked(articleId) {
  return request.get(`/articles/${articleId}/like`)
}
