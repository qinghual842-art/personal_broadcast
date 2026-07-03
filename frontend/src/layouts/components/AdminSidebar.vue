<script setup>
import { useRouter, useRoute } from 'vue-router'
import { Odometer, Document, Folder, PriceTag, ChatDotRound, Connection, Setting, User } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const menuItems = [
  { path: '/admin/dashboard', title: '仪表盘', icon: Odometer },
  { path: '/admin/articles', title: '文章管理', icon: Document },
  { path: '/admin/categories', title: '分类管理', icon: Folder },
  { path: '/admin/tags', title: '标签管理', icon: PriceTag },
  { path: '/admin/comments', title: '评论管理', icon: ChatDotRound },
  { path: '/admin/agents', title: '智能体管理', icon: Connection },
  { path: '/admin/site', title: '站点设置', icon: Setting },
  { path: '/admin/profile', title: '个人资料', icon: User },
]
</script>

<template>
  <div class="admin-sidebar">
    <div class="sidebar-brand" @click="router.push('/')">
      <svg width="24" height="24" viewBox="0 0 28 28" fill="none">
        <rect width="28" height="28" rx="8" fill="url(#as-logo)"/>
        <path d="M8 20V8l6 6-6 6zM14 20V8l6 6-6 6z" fill="white" opacity="0.9"/>
        <defs>
          <linearGradient id="as-logo" x1="0" y1="0" x2="28" y2="28">
            <stop stop-color="#6366F1"/>
            <stop offset="1" stop-color="#059669"/>
          </linearGradient>
        </defs>
      </svg>
      <span class="brand-text">后台管理</span>
    </div>

    <div class="sidebar-menu">
      <router-link
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
        class="menu-item"
        :class="{ 'menu-item--active': route.path.startsWith(item.path) }"
      >
        <el-icon :size="18"><component :is="item.icon" /></el-icon>
        <span>{{ item.title }}</span>
      </router-link>
    </div>

    <div class="sidebar-footer">
      <router-link to="/" class="back-site">
        <el-icon :size="16"><View /></el-icon>
        <span>返回站点</span>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.admin-sidebar {
  width: 220px;
  background: var(--bg-surface);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  border-right: 1px solid var(--border-default);
  box-shadow: var(--shadow-xs);
  position: relative;

  /* 左侧金色装饰线 */
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    width: 2px;
    background: linear-gradient(180deg,
      var(--color-amber-400) 0%,
      var(--color-amber-600) 40%,
      rgba(212, 168, 83, 0.10) 100%
    );
  }
}

.sidebar-brand {
  height: 56px;
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: 0 var(--space-5);
  cursor: pointer;
  border-bottom: 1px solid var(--border-light);
  transition: background var(--transition-fast);
  position: relative;

  /* 金色渐变底边 */
  &::after {
    content: '';
    position: absolute;
    bottom: -1px;
    left: var(--space-5);
    right: var(--space-5);
    height: 2px;
    background: var(--gradient-gold);
    border-radius: 1px;
    opacity: 0.5;
    transition: opacity var(--transition-fast);
  }

  &:hover {
    background: var(--bg-accent-light);

    &::after {
      opacity: 0.8;
    }
  }
}

.brand-text {
  font-family: var(--font-serif);
  font-size: var(--text-base);
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.sidebar-menu {
  flex: 1;
  padding: var(--space-3);
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow-y: auto;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: 10px 12px;
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-weight: 500;
  text-decoration: none;
  transition: all var(--transition-fast);
  position: relative;

  &:hover {
    background: rgba(212, 168, 83, 0.06);
    color: var(--color-amber-700);

    .el-icon {
      color: var(--color-amber-600);
    }
  }

  &--active {
    background: linear-gradient(135deg, rgba(212, 168, 83, 0.12), rgba(212, 168, 83, 0.04));
    color: var(--color-amber-700);
    font-weight: 600;
    border: 1px solid rgba(212, 168, 83, 0.18);
    box-shadow: 0 1px 3px rgba(212, 168, 83, 0.06);

    .el-icon {
      color: var(--color-amber-500);
    }
  }

  .el-icon {
    color: var(--text-tertiary);
    flex-shrink: 0;
    transition: color var(--transition-fast);
  }
}

.sidebar-footer {
  padding: var(--space-3);
  border-top: 1px solid var(--border-light);
}

.back-site {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  text-decoration: none;
  transition: all var(--transition-fast);

  &:hover {
    background: rgba(212, 168, 83, 0.06);
    color: var(--color-amber-700);
  }
}
</style>
