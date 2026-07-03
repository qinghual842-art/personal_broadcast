import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, getProfile } from '@/api/auth'

// Safe JSON parsing helper
function safeParseJSON(str, fallback = null) {
  try {
    return JSON.parse(str)
  } catch {
    return fallback
  }
}

export const useAuthStore = defineStore('auth', () => {
  // 使用 sessionStorage：关闭浏览器即失效，每次打开需重新登录
  const token = ref(sessionStorage.getItem('token') || '')
  const admin = ref(safeParseJSON(sessionStorage.getItem('admin')))

  const isLoggedIn = computed(() => !!token.value)

  async function login(credentials) {
    const res = await loginApi(credentials)
    token.value = res.data.token
    admin.value = res.data.admin
    sessionStorage.setItem('token', res.data.token)
    sessionStorage.setItem('admin', JSON.stringify(res.data.admin))
    return { token: res.data.token, admin: res.data.admin }
  }

  async function logout() {
    try { await logoutApi() } catch (e) { /* ignore */ }
    token.value = ''
    admin.value = null
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('admin')
  }

  async function refreshProfile() {
    try {
      const res = await getProfile()
      admin.value = res.data
      sessionStorage.setItem('admin', JSON.stringify(res.data))
    } catch (e) { /* ignore */ }
  }

  return { token, admin, isLoggedIn, login, logout, refreshProfile }
})
