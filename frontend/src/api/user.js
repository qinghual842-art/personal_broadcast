import request from './request'

export function userRegister(data) {
  return request.post('/user/register', data)
}

export function userLogin(data) {
  return request.post('/user/login', data)
}

export function userLogout() {
  return request.post('/user/logout')
}

export function getUserProfile() {
  return request.get('/user/profile')
}

export function updateUserProfile(data) {
  return request.put('/user/profile', data)
}

export function changeUserPassword(data) {
  return request.put('/user/password', data)
}

export function uploadUserAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/user/upload/avatar', formData)
}
