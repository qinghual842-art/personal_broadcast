<script setup>
import { useAuthStore } from '@/stores/auth'
import { useRouter, useRoute } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

async function handleLogout() {
  await authStore.logout()
  router.push('/admin/login')
}

const breadcrumbMap = {
  '/admin/dashboard': '仪表盘',
  '/admin/articles': '文章管理',
  '/admin/categories': '分类管理',
  '/admin/tags': '标签管理',
  '/admin/comments': '评论管理',
  '/admin/agents': '智能体管理',
  '/admin/site': '站点设置',
  '/admin/profile': '个人资料',
}

const currentTitle = breadcrumbMap[route.path] || ''
</script>

<template>
  <div class="admin-topbar">
    <div class="topbar-left">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理后台</el-breadcrumb-item>
        <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="topbar-right">
      <el-dropdown trigger="click">
        <span class="user-info">
          <el-avatar :size="32" :src="authStore.admin?.avatar || 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif'" />
          <span class="user-name">{{ authStore.admin?.nickname || 'Admin' }}</span>
          <el-icon :size="14" class="dropdown-icon"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="router.push('/admin/profile')">
              <el-icon><User /></el-icon>个人资料
            </el-dropdown-item>
            <el-dropdown-item @click="router.push('/')">
              <el-icon><View /></el-icon>查看站点
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style scoped>
.admin-topbar {
  height: 56px;
  background: var(--bg-surface);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--space-6);
  border-bottom: 1px solid var(--border-default);
  flex-shrink: 0;
  box-shadow: var(--shadow-sm);
}

.topbar-left {
  display: flex;
  align-items: center;
}

.topbar-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  cursor: pointer;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-default);
  background: var(--bg-subtle);
  transition: all var(--transition-fast);

  &:hover {
    background: var(--bg-accent-light);
    border-color: var(--color-amber-400);
    box-shadow: 0 0 0 2px rgba(212, 168, 83, 0.10);
  }
}

.user-name {
  color: var(--text-primary);
  font-weight: 500;
  font-size: var(--text-sm);
}

.dropdown-icon {
  color: var(--text-tertiary);
  transition: transform var(--transition-fast);
}

.user-info:hover .dropdown-icon {
  color: var(--color-amber-600);
}

:deep(.el-breadcrumb) {
  .el-breadcrumb__item {
    .el-breadcrumb__inner {
      color: var(--text-tertiary);
      font-weight: 500;

      &:hover {
        color: var(--color-amber-700);
      }
    }

    &:last-child .el-breadcrumb__inner {
      color: var(--text-primary);
      font-weight: 600;
    }

    .el-breadcrumb__separator {
      color: var(--color-amber-400);
      opacity: 0.6;
    }
  }
}
</style>
