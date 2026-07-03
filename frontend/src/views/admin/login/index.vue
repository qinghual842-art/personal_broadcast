<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const form = ref({ username: '', password: '' })
const loading = ref(false)
const errorMsg = ref('')

async function handleLogin() {
  errorMsg.value = ''
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await authStore.login(form.value)
    if (res && res.token) {
      ElMessage.success('欢迎回来')
      router.push('/admin/dashboard')
    }
  } catch (e) {
    errorMsg.value = e.message || '登录失败，请检查用户名密码或确认后端服务已启动'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="login-bg">
      <div class="bg-circle bg-circle--1"></div>
      <div class="bg-circle bg-circle--2"></div>
      <div class="bg-circle bg-circle--3"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">
          <svg width="40" height="40" viewBox="0 0 28 28" fill="none">
            <rect width="28" height="28" rx="8" fill="url(#login-logo)"/>
            <path d="M8 20V8l6 6-6 6zM14 20V8l6 6-6 6z" fill="white" opacity="0.9"/>
            <defs>
              <linearGradient id="login-logo" x1="0" y1="0" x2="28" y2="28">
                <stop stop-color="#6366F1"/>
                <stop offset="1" stop-color="#4F46E5"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1>博客管理后台</h1>
        <p>登录以管理你的内容与智能体</p>
      </div>

      <!-- 错误提示 -->
      <el-alert
        v-if="errorMsg"
        :title="errorMsg"
        type="error"
        show-icon
        :closable="true"
        class="login-error"
        @close="errorMsg = ''"
      />

      <!-- 表单 -->
      <el-form @submit.prevent="handleLogin" class="login-form">
        <el-form-item>
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
            class="login-input"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
            class="login-input"
          />
        </el-form-item>
        <button
          type="submit"
          class="login-btn"
          :disabled="loading"
        >
          <span v-if="!loading">登 录</span>
          <span v-else class="btn-loading">
            <span class="loading-dot"></span>
            验证中...
          </span>
        </button>
      </el-form>

      <div class="login-footer">
        <router-link to="/login" class="user-login-link">普通用户登录</router-link>
        <router-link to="/" class="back-link">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          返回博客首页
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: var(--bg-page);
  position: relative;
  overflow: hidden;
}

/* Background decoration */
.login-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.06;

  &--1 {
    width: 500px;
    height: 500px;
    background: var(--color-primary-400);
    top: -150px;
    right: -100px;
  }

  &--2 {
    width: 350px;
    height: 350px;
    background: var(--color-primary-200);
    bottom: -100px;
    left: -80px;
  }

  &--3 {
    width: 200px;
    height: 200px;
    background: var(--color-primary-500);
    top: 50%;
    left: 60%;
    opacity: 0.03;
  }
}

/* Card */
.login-card {
  width: 400px;
  background: linear-gradient(160deg, var(--bg-surface), rgba(99, 102, 241, 0.02) 60%, rgba(5, 150, 105, 0.03));
  border: 1px solid var(--border-default);
  border-radius: var(--radius-2xl);
  padding: var(--space-10) var(--space-8);
  position: relative;
  box-shadow: var(--shadow-xl);
  animation: fadeInUp 0.6s ease-out;
}

.login-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.login-logo {
  display: inline-block;
  margin-bottom: var(--space-4);
}

.login-header h1 {
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 var(--space-2);
  letter-spacing: -0.02em;
  font-family: var(--font-display);
}

.login-header p {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  margin: 0;
}

/* Error */
.login-error {
  margin-bottom: var(--space-5);
  border-radius: var(--radius-md);
}

/* Form */
.login-form {
  margin-top: var(--space-5);
}

.login-input :deep(.el-input__wrapper) {
  border-radius: var(--radius-md);
  height: 44px;
}

/* Button */
.login-btn {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--color-primary-500), var(--color-primary-600));
  color: #fff;
  font-size: var(--text-base);
  font-weight: 600;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: all var(--transition-normal);
  margin-top: var(--space-3);
  font-family: inherit;
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.3);

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(99, 102, 241, 0.4);
  }

  &:active:not(:disabled) {
    transform: translateY(0);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.btn-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
}

.loading-dot {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Footer */
.login-footer {
  text-align: center;
  margin-top: var(--space-6);
  padding-top: var(--space-5);
  border-top: 1px solid var(--border-light);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.user-login-link {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  text-decoration: none;
  font-weight: 500;
  transition: color var(--transition-fast);
}
.user-login-link:hover {
  color: var(--color-primary-600);
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  text-decoration: none;
  font-weight: 500;
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);

  &:hover {
    color: var(--color-primary-600);
    background: var(--bg-accent-light);
  }
}
</style>
