import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userLogin, userRegister, userLogout, getUserProfile } from '@/api/user'

function safeParseJSON(str, fallback = null) {
  try { return JSON.parse(str) } catch { return fallback }
}

export const useUserStore = defineStore('user', () => {
  const token = ref(sessionStorage.getItem('user_token') || '')
  const user = ref(safeParseJSON(sessionStorage.getItem('user_info')))

  const isLoggedIn = computed(() => !!token.value)

  async function login(credentials) {
    const res = await userLogin(credentials)
    token.value = res.data.token
    user.value = res.data.user
    sessionStorage.setItem('user_token', res.data.token)
    sessionStorage.setItem('user_info', JSON.stringify(res.data.user))
    return res.data
  }

  async function register(data) {
    const res = await userRegister(data)
    token.value = res.data.token
    user.value = res.data.user
    sessionStorage.setItem('user_token', res.data.token)
    sessionStorage.setItem('user_info', JSON.stringify(res.data.user))
    return res.data
  }

  async function logout() {
    try { await userLogout() } catch {}
    clearState()
  }

  async function refreshProfile() {
    try {
      const res = await getUserProfile()
      user.value = res.data
      sessionStorage.setItem('user_info', JSON.stringify(res.data))
    } catch { /* ignore */ }
  }

  function clearState() {
    token.value = ''
    user.value = null
    sessionStorage.removeItem('user_token')
    sessionStorage.removeItem('user_info')
  }

  return { token, user, isLoggedIn, login, register, logout, refreshProfile, clearState }
})
