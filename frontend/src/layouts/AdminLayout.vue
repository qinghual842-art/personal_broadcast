<script setup>
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import AdminSidebar from './components/AdminSidebar.vue'
import AdminTopbar from './components/AdminTopbar.vue'

const authStore = useAuthStore()
const router = useRouter()
</script>

<template>
  <div class="admin-layout">
    <AdminSidebar />
    <div class="admin-main">
      <AdminTopbar />
      <div class="admin-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  background: var(--bg-page);
}

.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.admin-content {
  flex: 1;
  padding: var(--space-6);
  overflow-y: auto;
  background: var(--bg-page);
  position: relative;

  /* 纸张纹理背景 */
  &::before {
    content: '';
    position: fixed;
    inset: 0;
    background-image:
      radial-gradient(ellipse at 70% 30%, rgba(212, 168, 83, 0.02) 0%, transparent 50%),
      radial-gradient(ellipse at 20% 80%, rgba(61, 90, 150, 0.02) 0%, transparent 50%);
    pointer-events: none;
    z-index: 0;
  }

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-thumb {
    background: var(--color-gray-300);
    border-radius: 4px;

    &:hover {
      background: var(--color-amber-500);
    }
  }
}
</style>
