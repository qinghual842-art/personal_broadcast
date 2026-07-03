<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const isLogin = ref(true)
const loading = ref(false)
const errorMsg = ref('')

const loginForm = ref({ username: '', password: '' })
const registerForm = ref({ username: '', password: '', nickname: '', email: '' })

async function handleLogin() {
  errorMsg.value = ''
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login(loginForm.value)
    ElMessage.success('欢迎回来')
    router.push('/')
  } catch (e) {
    errorMsg.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  errorMsg.value = ''
  if (!registerForm.value.username || !registerForm.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  if (registerForm.value.password.length < 6) {
    ElMessage.warning('密码长度至少为6位')
    return
  }
  loading.value = true
  try {
    await userStore.register(registerForm.value)
    ElMessage.success('注册成功，已自动登录')
    router.push('/')
  } catch (e) {
    errorMsg.value = e.message || '注册失败'
  } finally {
    loading.value = false
  }
}

function onLoginClick(e) {
  e.preventDefault()
  handleLogin()
}

function onRegisterClick(e) {
  e.preventDefault()
  handleRegister()
}
</script>

<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="bg-circle bg-circle--1"></div>
      <div class="bg-circle bg-circle--2"></div>
      <div class="bg-circle bg-circle--3"></div>
    </div>

    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">
          <svg width="40" height="40" viewBox="0 0 28 28" fill="none">
            <rect width="28" height="28" rx="8" fill="url(#user-logo)"/>
            <path d="M8 20V8l6 6-6 6zM14 20V8l6 6-6 6z" fill="white" opacity="0.9"/>
            <defs>
              <linearGradient id="user-logo" x1="0" y1="0" x2="28" y2="28">
                <stop stop-color="#06b6d4"/><stop offset="1" stop-color="#0891b2"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1>{{ isLogin ? '用户登录' : '注册账号' }}</h1>
        <p>{{ isLogin ? '登录后发表文章、评论、点赞' : '加入我们，分享你的技术心得' }}</p>
      </div>

      <el-alert v-if="errorMsg" :title="errorMsg" type="error" show-icon :closable="true" class="login-error" @close="errorMsg = ''" />

      <!-- Login Form -->
      <el-form v-if="isLogin" @submit.prevent="handleLogin" class="login-form">
        <el-form-item>
          <el-input v-model="loginForm.username" placeholder="用户名" :prefix-icon="User" size="large" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="loginForm.password" type="password" placeholder="密码" :prefix-icon="Lock" size="large" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <button type="submit" class="login-btn" :disabled="loading" @click="onLoginClick">
            <span v-if="!loading">登 录</span><span v-else>验证中...</span>
          </button>
        </el-form-item>
      </el-form>

      <!-- Register Form -->
      <el-form v-else @submit.prevent="handleRegister" class="login-form">
        <el-form-item>
          <el-input v-model="registerForm.username" placeholder="用户名" :prefix-icon="User" size="large" @keyup.enter="handleRegister" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.nickname" placeholder="昵称（选填）" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.email" placeholder="邮箱（选填）" :prefix-icon="Message" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.password" type="password" placeholder="密码（至少6位）" :prefix-icon="Lock" size="large" show-password @keyup.enter="handleRegister" />
        </el-form-item>
        <el-form-item>
          <button type="submit" class="login-btn" :disabled="loading" @click="onRegisterClick">
            <span v-if="!loading">注 册</span><span v-else>提交中...</span>
          </button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <a href="#" class="link" @click.prevent="isLogin = !isLogin; errorMsg = ''">
          {{ isLogin ? '没有账号？立即注册' : '已有账号？去登录' }}
        </a>
        <div class="footer-links">
          <router-link to="/admin/login" class="link admin-link">管理员登录</router-link>
          <router-link to="/" class="link back-link">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
            返回首页
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex; justify-content: center; align-items: center;
  min-height: 100vh; background: var(--bg-page);
  position: relative; overflow: hidden;
}
.login-bg { position: absolute; inset: 0; pointer-events: none; }
.bg-circle { position: absolute; border-radius: 50%; opacity: 0.06; }
.bg-circle--1 { width: 500px; height: 500px; background: var(--color-primary-400); top: -150px; right: -100px; }
.bg-circle--2 { width: 350px; height: 350px; background: var(--color-primary-200); bottom: -100px; left: -80px; }
.bg-circle--3 { width: 200px; height: 200px; background: var(--color-primary-500); top: 50%; left: 60%; opacity: 0.03; }

.login-card {
  width: 400px; background: var(--bg-surface);
  border: 1px solid var(--border-default); border-radius: var(--radius-2xl);
  padding: var(--space-10) var(--space-8); position: relative;
  box-shadow: var(--shadow-xl); animation: fadeInUp 0.6s ease-out;
}
.login-header { text-align: center; margin-bottom: var(--space-8); }
.login-logo { display: inline-block; margin-bottom: var(--space-4); }
.login-header h1 { font-size: var(--text-2xl); font-weight: 700; color: var(--text-primary); margin: 0 0 var(--space-2); letter-spacing: -0.02em; }
.login-header p { font-size: var(--text-sm); color: var(--text-tertiary); margin: 0; }
.login-error { margin-bottom: var(--space-5); border-radius: var(--radius-md); }
.login-form { margin-top: var(--space-5); }
.login-form .el-form-item:last-child { margin-bottom: 0; }
.login-btn {
  width: 100%; padding: 14px; border: none; border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--color-primary-500), var(--color-primary-600));
  color: #fff; font-size: var(--text-base); font-weight: 600;
  cursor: pointer; transition: all var(--transition-normal);
  font-family: inherit;
  box-shadow: 0 4px 16px rgba(8, 145, 178, 0.3);
}
.login-btn:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(8, 145, 178, 0.4); }
.login-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.login-footer {
  text-align: center; margin-top: var(--space-6); padding-top: var(--space-5);
  border-top: 1px solid var(--border-light); display: flex;
  flex-direction: column; align-items: center; gap: var(--space-3);
}
.footer-links { display: flex; gap: var(--space-5); }
.link { font-size: var(--text-sm); color: var(--color-primary-600); text-decoration: none; font-weight: 500; }
.link:hover { color: var(--color-primary-700); }
.back-link { display: flex; align-items: center; gap: 4px; color: var(--text-tertiary); }
.back-link:hover { color: var(--color-primary-600); }
.admin-link { color: var(--text-tertiary); font-weight: 400; }
.admin-link:hover { color: var(--color-primary-600); }
</style>
