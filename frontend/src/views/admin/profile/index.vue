<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { updateProfile, changePassword } from '@/api/auth'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const profileForm = ref({ nickname: '', avatar: '', email: '' })
const passwordForm = ref({ oldPassword: '', newPassword: '' })

onMounted(() => {
  profileForm.value = {
    nickname: authStore.admin?.nickname || '',
    avatar: authStore.admin?.avatar || '',
    email: authStore.admin?.email || ''
  }
})

async function handleUpdateProfile() {
  await updateProfile(profileForm.value)
  await authStore.refreshProfile()
  ElMessage.success('资料已更新')
}

async function handleChangePassword() {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword) {
    ElMessage.warning('请填写密码')
    return
  }
  await changePassword(passwordForm.value)
  ElMessage.success('密码已修改，请重新登录')
  await authStore.logout()
}
</script>

<template>
  <div class="profile-page">
    <h3>个人资料</h3>
    <el-form label-width="80px">
      <el-form-item label="昵称"><el-input v-model="profileForm.nickname" /></el-form-item>
      <el-form-item label="头像URL"><el-input v-model="profileForm.avatar" /></el-form-item>
      <el-form-item label="邮箱"><el-input v-model="profileForm.email" /></el-form-item>
      <el-form-item><el-button type="primary" @click="handleUpdateProfile">更新资料</el-button></el-form-item>
    </el-form>

    <h3 style="margin-top:40px">修改密码</h3>
    <el-form label-width="80px">
      <el-form-item label="旧密码"><el-input v-model="passwordForm.oldPassword" type="password" show-password /></el-form-item>
      <el-form-item label="新密码"><el-input v-model="passwordForm.newPassword" type="password" show-password /></el-form-item>
      <el-form-item><el-button type="danger" @click="handleChangePassword">修改密码</el-button></el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.profile-page {
  background: var(--bg-surface);
  padding: var(--space-8);
  border-radius: var(--radius-xl);
  max-width: 600px;
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-card);
}
.profile-page h3 {
  margin: 0 0 var(--space-5);
  font-size: var(--text-xl);
  font-weight: 700;
  color: var(--text-primary);
}
</style>
