import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 30000
})

// ─── Request interceptor ──────────────────────────────────────
request.interceptors.request.use(config => {
  const adminToken = sessionStorage.getItem('token')
  const userToken = sessionStorage.getItem('user_token')
  // Admin endpoints use admin token, everything else prefers user token
  if (config.url.startsWith('/admin/') && adminToken) {
    config.headers.Authorization = `Bearer ${adminToken}`
  } else if (userToken) {
    config.headers.Authorization = `Bearer ${userToken}`
  } else if (adminToken) {
    config.headers.Authorization = `Bearer ${adminToken}`
  }
  return config
})

// ─── Response interceptors ────────────────────────────────────
request.interceptors.response.use(
  response => {
    const data = response.data
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      if (data.code === 401) {
        clearAuthAndRedirect()
      }
      return Promise.reject(new Error(data.message))
    }
    return data
  },
  error => {
    // Handle HTTP-level errors (network, CORS, server errors)
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        clearAuthAndRedirect()
      } else if (status >= 500) {
        ElMessage.error('服务器内部错误，请稍后重试')
      } else if (status === 403) {
        ElMessage.error('没有权限执行此操作')
      } else if (status === 404) {
        // Don't show toast for 404 - let the page handle it
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请检查网络连接')
    } else {
      ElMessage.error('网络连接失败，请检查网络')
    }
    return Promise.reject(error)
  }
)

/**
 * Clear auth state consistently across all storage layers.
 * Uses both sessionStorage (direct clear) and authStore (Pinia state sync).
 */
function clearAuthAndRedirect() {
  sessionStorage.removeItem('token')
  sessionStorage.removeItem('admin')
  sessionStorage.removeItem('user_token')
  sessionStorage.removeItem('user_info')

  import('@/stores/auth').then(({ useAuthStore }) => {
    const authStore = useAuthStore()
    authStore.token = ''
    authStore.admin = null
  })

  // Navigate to login
  const path = router.currentRoute.value.path
  if (!path.startsWith('/admin/login') && !path.startsWith('/login')) {
    router.push('/login').catch(() => {})
  }
}

export default request
