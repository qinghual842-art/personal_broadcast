import request from './request'

export function getTags() {
  return request.get('/tags')
}

export function getAdminTags() {
  return request.get('/admin/tags')
}

export function createTag(data) {
  return request.post('/admin/tags', data)
}

export function updateTag(id, data) {
  return request.put(`/admin/tags/${id}`, data)
}

export function deleteTag(id) {
  return request.delete(`/admin/tags/${id}`)
}
