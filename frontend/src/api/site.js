import request from './request'

export function getSiteConfig() {
  return request.get('/site/config')
}

export function getAdminSiteConfig() {
  return request.get('/admin/site/config')
}

export function updateSiteConfig(data) {
  return request.put('/admin/site/config', data)
}

export function getOwnerInfo() {
  return request.get('/site/owner')
}
