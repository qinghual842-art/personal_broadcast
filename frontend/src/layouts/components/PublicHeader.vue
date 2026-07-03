<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useSiteStore } from '@/stores/site'
import { useUserStore } from '@/stores/user'
import SettingsPanel from '@/components/site/SettingsPanel.vue'

const router = useRouter()
const siteStore = useSiteStore()
const userStore = useUserStore()
const searchKeyword = ref('')
const menuOpen = ref(false)
const panelRef = ref(null)

function goSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ name: 'Search', query: { keyword: searchKeyword.value.trim() } })
  }
}

function closePanel() { menuOpen.value = false }

function onClickOutside(e) {
  if (!menuOpen.value) return
  const panel = panelRef.value
  const btn = e.target.closest('.admin-link')
  if (panel && !panel.contains(e.target) && !btn) menuOpen.value = false
}

function handleUserLogout() {
  userStore.logout()
  router.push('/')
}

function handleCommand(command) {
  if (command === 'logout') {
    handleUserLogout()
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'articles') {
    router.push('/my-articles')
  }
}

onMounted(() => document.addEventListener('click', onClickOutside))
onUnmounted(() => document.removeEventListener('click', onClickOutside))
</script>

<template>
  <header class="public-header">
    <div class="header-inner">
      <!-- Logo: 紫色渐变图标 + 站点名称 -->
      <div class="logo" @click="router.push('/')">
        <div class="logo-icon">
          <svg width="30" height="30" viewBox="0 0 30 30" fill="none">
            <rect width="30" height="30" rx="8" fill="url(#logo-purple-grad)"/>
            <path d="M9 21V9l6 6-6 6zM15 21V9l6 6-6 6z" fill="white" opacity="0.92"/>
            <defs>
              <linearGradient id="logo-purple-grad" x1="0" y1="0" x2="30" y2="30">
                <stop stop-color="#7c3aed"/>
                <stop offset="0.5" stop-color="#6366f1"/>
                <stop offset="1" stop-color="#8b5cf6"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1 class="logo-text">{{ siteStore.config.site_name || '个人博客' }}</h1>
      </div>

      <!-- Navigation -->
      <nav class="nav-links">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/categories" class="nav-item">分类</router-link>
        <router-link to="/tags" class="nav-item">标签</router-link>
        <router-link to="/agents" class="nav-item">智能助手</router-link>
        <router-link to="/about" class="nav-item">关于我</router-link>
      </nav>

      <!-- Actions -->
      <div class="header-actions">
        <!-- Search: pill shape, focus expands -->
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索文章..."
            size="default"
            @keyup.enter="goSearch"
            class="search-input"
          >
            <template #prefix>
              <el-icon :size="16"><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <!-- User area -->
        <template v-if="userStore.isLoggedIn">
          <router-link to="/editor" class="write-btn" title="写文章">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <span>写文章</span>
          </router-link>
          <el-dropdown trigger="click" @command="handleCommand">
            <button class="user-btn">
              <el-avatar :size="30" :src="userStore.user?.avatar" icon="UserFilled" />
              <span class="user-name">{{ userStore.user?.nickname || userStore.user?.username }}</span>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人主页</el-dropdown-item>
                <el-dropdown-item command="articles">我的文章</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="login-btn">登录 / 注册</router-link>
        </template>

        <!-- Settings button -->
        <div class="admin-wrapper">
          <button class="settings-btn" @click.stop="menuOpen = !menuOpen" :aria-label="menuOpen ? '关闭设置' : '打开设置'">
            <svg v-if="!menuOpen" width="16" height="16" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="5" r="2" stroke="currentColor" stroke-width="2"/>
              <circle cx="12" cy="12" r="2" stroke="currentColor" stroke-width="2"/>
              <circle cx="12" cy="19" r="2" stroke="currentColor" stroke-width="2"/>
            </svg>
            <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>

          <Transition name="panel-drop">
            <div class="panel-anchor" v-if="menuOpen" ref="panelRef">
              <SettingsPanel @close="closePanel" />
            </div>
          </Transition>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
.public-header {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px) saturate(180%);
  -webkit-backdrop-filter: blur(16px) saturate(180%);
  border-bottom: none;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: var(--shadow-xs);
  transition: background var(--transition-normal);

  /* 底部金色渐变装饰线 */
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg,
      transparent 0%,
      rgba(212, 168, 83, 0.12) 15%,
      rgba(212, 168, 83, 0.35) 50%,
      rgba(212, 168, 83, 0.12) 85%,
      transparent 100%
    );
  }
}

.header-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--space-8);
  display: flex;
  align-items: center;
  height: 64px;
  gap: var(--space-10);
}

/* ═══ Logo ═══ */
.logo {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  cursor: pointer;
  flex-shrink: 0;
  transition: transform var(--transition-fast), filter var(--transition-fast);
  user-select: none;

  &:hover {
    transform: rotate(-2deg) scale(1.03);
    filter: drop-shadow(0 0 10px rgba(139, 92, 246, 0.30));
  }
}

.logo-icon {
  flex-shrink: 0;
}

.logo-text {
  font-family: var(--font-serif);
  font-size: var(--text-lg);
  font-weight: 700;
  margin: 0;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  white-space: nowrap;
}

/* ═══ Navigation ═══ */
.nav-links {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.nav-item {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-secondary);
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-md);
  transition: color var(--transition-fast), background var(--transition-fast);
  position: relative;
  text-decoration: none;

  &::after {
    content: '';
    position: absolute;
    bottom: 2px;
    left: 50%;
    width: 0;
    height: 2px;
    background: var(--color-amber-500);
    border-radius: 1px;
    transform: translateX(-50%);
    transition: width var(--transition-fast);
  }

  &:hover {
    color: var(--color-amber-700);
    background: transparent;

    &::after {
      width: 60%;
    }
  }

  &.router-link-active {
    color: var(--color-amber-700);
    font-weight: 600;
    background: transparent;

    &::after {
      width: 60%;
    }
  }
}

/* ═══ Actions ═══ */
.header-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

/* Search box — pill shape + focus expand */
.search-box {
  width: 180px;
  transition: width 0.4s cubic-bezier(0.22, 1.3, 0.36, 1);
}

.search-box:focus-within {
  width: 280px;
}

.search-input :deep(.el-input__wrapper) {
  background: var(--bg-subtle);
  border: 1px solid var(--border-default);
  border-radius: 999px;
  box-shadow: none;
  padding: 0 16px;
  height: 36px;
  transition: all var(--transition-fast);
}

.search-input :deep(.el-input__wrapper):hover {
  border-color: var(--color-amber-400);
  background: var(--bg-accent-light);
}

.search-input :deep(.el-input__wrapper).is-focus {
  border-color: var(--color-amber-500);
  background: var(--bg-surface);
  box-shadow: 0 0 0 3px rgba(212, 168, 83, 0.10);
}

.search-input :deep(.el-input__inner) {
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.search-input :deep(.el-input__prefix) {
  color: var(--text-tertiary);
}

/* Settings button */
.admin-wrapper {
  position: relative;
}

.settings-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  color: var(--text-tertiary);
  background: var(--bg-subtle);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  cursor: pointer;
  font-family: inherit;

  &:hover {
    color: var(--color-amber-700);
    background: var(--bg-accent-light);
    border-color: var(--border-accent);
  }
}

/* Login button — amber gold gradient */
.login-btn {
  font-size: 14px;
  font-weight: 600;
  padding: 7px 20px;
  border-radius: 999px;
  text-decoration: none;
  background: linear-gradient(135deg, var(--color-amber-500), var(--color-amber-600));
  color: #fff;
  transition: all var(--transition-fast);
  box-shadow: 0 2px 8px rgba(212, 168, 83, 0.25);
  white-space: nowrap;

  &:hover {
    background: linear-gradient(135deg, var(--color-amber-400), var(--color-amber-500));
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(212, 168, 83, 0.35);
    color: #fff;
  }
}

/* Write button */
.write-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  background: linear-gradient(135deg, var(--color-amber-500), var(--color-amber-600));
  padding: 7px 14px;
  border-radius: 999px;
  text-decoration: none;
  transition: all var(--transition-fast);
  box-shadow: 0 2px 8px rgba(212, 168, 83, 0.20);

  &:hover {
    background: linear-gradient(135deg, var(--color-amber-400), var(--color-amber-500));
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(212, 168, 83, 0.30);
  }
}

/* User button */
.user-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  cursor: pointer;
  padding: 3px 12px 3px 3px;
  border-radius: 20px;
  border: 1px solid var(--border-default);
  background: var(--bg-subtle);
  font-family: inherit;
  transition: all var(--transition-fast);

  &:hover {
    border-color: var(--color-amber-400);
    background: var(--bg-accent-light);
  }
}

.user-name {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

/* Panel anchor */
.panel-anchor {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  z-index: 9999;
}

/* Panel transition */
.panel-drop-enter-active {
  transition: all 0.2s ease-out;
}

.panel-drop-leave-active {
  transition: all 0.15s ease-in;
}

.panel-drop-enter-from {
  opacity: 0;
  transform: translateY(-8px) scale(0.97);
}

.panel-drop-leave-to {
  opacity: 0;
  transform: translateY(-4px) scale(0.98);
}

/* ═══ Responsive ═══ */
@media (max-width: 1200px) {
  .header-inner {
    gap: var(--space-6);
  }
  .search-box {
    width: 160px;
  }
  .search-box:focus-within {
    width: 220px;
  }
}

@media (max-width: 768px) {
  .header-inner {
    gap: var(--space-4);
    padding: 0 var(--space-4);
    height: 56px;
  }

  .nav-links {
    display: none;
  }

  .search-box {
    width: 130px;
  }

  .search-box:focus-within {
    width: 160px;
  }

  .logo-text {
    font-size: var(--text-base);
  }

  .logo-icon svg {
    width: 24px;
    height: 24px;
  }

  .login-btn {
    font-size: 12px;
    padding: 5px 14px;
  }

  .panel-anchor {
    position: fixed;
    top: 60px;
    right: 8px;
    left: 8px;
    width: auto;
  }
}
</style>
