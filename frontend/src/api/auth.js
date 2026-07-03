import request from './request'

export function login(data) {
  return request.post('/admin/auth/login', data)
}

export function logout() {
  return request.post('/admin/auth/logout')
}

export function getProfile() {
  return request.get('/admin/profile')
}

export function updateProfile(data) {
  return request.put('/admin/profile', data)
}

export function changePassword(data) {
  return request.put('/admin/password', data)
}
