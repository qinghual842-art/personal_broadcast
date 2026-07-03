<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useUserStore } from '@/stores/user'

const emit = defineEmits(['close'])
const router = useRouter()
const authStore = useAuthStore()
const userStore = useUserStore()
const isAdminLoggedIn = ref(!!sessionStorage.getItem('token'))

// ─── Font size (50%-200%) ────────────────────────────
const FONT_KEY = 'blog_font_scale'
const fontScale = ref(Number(localStorage.getItem(FONT_KEY)) || 100)

function setFontSize(val) {
  fontScale.value = val
  localStorage.setItem(FONT_KEY, val)
  document.documentElement.style.fontSize = (16 * val / 100).toFixed(1) + 'px'
}

// ─── Theme (light/dark) ──────────────────────────────
const THEME_KEY = 'blog_theme'
const currentTheme = ref(localStorage.getItem(THEME_KEY) || 'light')

function setTheme(mode) {
  currentTheme.value = mode
  localStorage.setItem(THEME_KEY, mode)
  applyTheme()
}

function applyTheme() {
  const html = document.documentElement
  if (currentTheme.value === 'dark') {
    html.dataset.theme = 'dark'
  } else {
    delete html.dataset.theme
  }
}

// ── Ctrl + Scroll zoom ──────────────────────────────
function onWheel(e) {
  if (e.ctrlKey) {
    e.preventDefault()
    const delta = e.deltaY > 0 ? -2 : 2
    const next = Math.min(200, Math.max(50, fontScale.value + delta))
    setFontSize(next)
  }
}

// ─── Account ─────────────────────────────────────────
function handleUserLogout() {
  userStore.logout()
  emit('close')
}
function handleAdminLogout() {
  authStore.logout()
  isAdminLoggedIn.value = false
  emit('close')
  router.push('/admin/login')
}
function goAdmin() {
  emit('close')
  router.push('/admin/login')
}
function goLogin() {
  emit('close')
  router.push('/login')
}

onMounted(() => {
  setFontSize(fontScale.value)
  applyTheme()
  window.addEventListener('wheel', onWheel, { passive: false })
})
onUnmounted(() => window.removeEventListener('wheel', onWheel))
</script>

<template>
  <div class="settings-panel">
    <div class="panel-header">
      <h3>管理功能</h3>
      <span class="panel-close" @click="emit('close')">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
      </span>
    </div>

    <!-- 字体大小 -->
    <div class="panel-section">
      <div class="section-label">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M6 4h12M12 4v16M6 20h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        字体大小 <span class="scale-badge">{{ fontScale }}%</span>
      </div>
      <el-slider v-model="fontScale" :min="50" :max="200" :step="1" :show-tooltip="false" @input="setFontSize" />
      <div class="font-presets">
        <button v-for="p in [80,100,120,150,200]" :key="p" class="preset-btn" :class="{ active: fontScale === p }" @click="setFontSize(p)">{{ p }}%</button>
      </div>
    </div>

    <!-- 主题模式 -->
    <div class="panel-section">
      <div class="section-label">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="4" stroke="currentColor" stroke-width="2"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        主题模式
      </div>
      <div class="theme-toggle">
        <button class="theme-btn" :class="{ active: currentTheme === 'light' }" @click="setTheme('light')">☀ 亮色</button>
        <button class="theme-btn" :class="{ active: currentTheme === 'dark' }" @click="setTheme('dark')">🌙 深色</button>
      </div>
    </div>

    <!-- 账号 -->
    <div class="panel-section">
      <div class="section-label">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="2"/><path d="M6 21v-2a4 4 0 014-4h4a4 4 0 014 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        账号管理
      </div>
      <div class="account-list">
        <div class="account-row">
          <span>{{ userStore.isLoggedIn ? userStore.user?.nickname || userStore.user?.username : '未登录' }}</span>
          <button v-if="userStore.isLoggedIn" class="action-btn" @click="handleUserLogout">退出</button>
          <button v-else class="action-btn primary" @click="goLogin">登录/注册</button>
        </div>
        <div class="account-row" v-if="isAdminLoggedIn">
          <span>管理员: {{ authStore.admin?.nickname || 'Admin' }}</span>
          <div class="account-btns">
            <button class="action-btn" @click="goAdmin">后台</button>
            <button class="action-btn" @click="handleAdminLogout">退出</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.settings-panel {
  width: 300px;
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  box-shadow: 0 16px 48px rgba(0,0,0,0.15);
  overflow: hidden;
  animation: fadeInUp 0.2s ease-out;
}
.panel-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: var(--space-4) var(--space-5); border-bottom: 1px solid var(--border-light);
}
.panel-header h3 { margin: 0; font-size: var(--text-base); color: var(--text-primary); font-family: var(--font-display); font-weight: 700; }
.panel-close { cursor: pointer; color: var(--text-tertiary); padding: 2px; border-radius: 4px; display: flex; }
.panel-close:hover { background: var(--bg-subtle); color: var(--text-primary); }
.panel-section { padding: var(--space-4) var(--space-5); border-bottom: 1px solid var(--border-light); }
.panel-section:last-child { border-bottom: none; }
.section-label {
  display: flex; align-items: center; gap: 6px;
  font-size: var(--text-sm); color: var(--text-secondary); font-weight: 600; margin-bottom: var(--space-3);
}
.scale-badge { margin-left: auto; font-size: 11px; color: var(--color-primary-600); background: var(--bg-accent-light); padding: 2px 8px; border-radius: 4px; font-weight: 700; }
.font-presets { display: flex; gap: 4px; margin-top: 8px; }
.preset-btn {
  flex: 1; border: 1px solid var(--border-default); background: var(--bg-subtle);
  color: var(--text-secondary); font-size: 11px; font-weight: 600; padding: 4px; border-radius: 4px;
  cursor: pointer; font-family: inherit; transition: all .15s;
}
.preset-btn:hover { border-color: var(--color-primary-300); color: var(--color-primary-600); }
.preset-btn.active { background: var(--color-primary-500); border-color: var(--color-primary-500); color: #fff; }

.theme-toggle { display: flex; gap: 8px; }
.theme-btn {
  flex: 1; padding: 8px; border: 1px solid var(--border-default); background: var(--bg-subtle);
  color: var(--text-secondary); font-size: var(--text-sm); font-weight: 600; border-radius: 8px;
  cursor: pointer; font-family: inherit; transition: all .15s;
}
.theme-btn:hover { border-color: var(--color-primary-300); }
.theme-btn.active { background: var(--color-primary-500); border-color: var(--color-primary-500); color: #fff; }

.account-list { display: flex; flex-direction: column; gap: var(--space-3); }
.account-row { display: flex; justify-content: space-between; align-items: center; font-size: var(--text-sm); color: var(--text-tertiary); }
.account-btns { display: flex; gap: 4px; }
.action-btn {
  border: 1px solid var(--border-default); background: var(--bg-subtle);
  color: var(--text-secondary); font-size: 11px; font-weight: 600; padding: 5px 12px;
  border-radius: 6px; cursor: pointer; font-family: inherit; transition: all .15s;
}
.action-btn:hover { border-color: var(--color-primary-300); color: var(--color-primary-600); }
.action-btn.primary { background: var(--color-primary-500); border-color: var(--color-primary-500); color: #fff; }
.action-btn.primary:hover { background: var(--color-primary-600); }

:deep(.el-slider) { height: 28px; }
:deep(.el-slider__runway) { height: 5px; border-radius: 3px; background: var(--bg-subtle); }
:deep(.el-slider__bar) { height: 5px; background: var(--color-primary-500); border-radius: 3px; }
:deep(.el-slider__button) { width: 16px; height: 16px; border: 2px solid var(--color-primary-500); background: var(--bg-surface); }
</style>
