<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { updateUserProfile, changeUserPassword, uploadUserAvatar } from '@/api/user'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const DEFAULT_AVATAR = 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif'

const router = useRouter()
const userStore = useUserStore()

const profileForm = ref({ nickname: '', avatar: '', email: '', bio: '' })
const passwordForm = ref({ oldPassword: '', newPassword: '' })
const uploading = ref(false)

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  profileForm.value = {
    nickname: userStore.user?.nickname || '',
    avatar: userStore.user?.avatar || '',
    email: userStore.user?.email || '',
    bio: userStore.user?.bio || ''
  }
})

async function handleAvatarUpload(file) {
  uploading.value = true
  try {
    const res = await uploadUserAvatar(file)
    profileForm.value.avatar = res.data.url
    ElMessage.success('头像上传成功')
  } catch {
    ElMessage.error('头像上传失败')
  } finally {
    uploading.value = false
  }
}

function beforeAvatarUpload(file) {
  const isImage = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)
  if (!isImage) {
    ElMessage.error('仅支持 JPG、PNG、GIF、WebP 格式的图片')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB')
    return false
  }
  return true
}

async function handleUpdateProfile() {
  await updateUserProfile(profileForm.value)
  await userStore.refreshProfile()
  ElMessage.success('资料已更新')
}

async function handleChangePassword() {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword) {
    ElMessage.warning('请填写密码')
    return
  }
  await changeUserPassword(passwordForm.value)
  ElMessage.success('密码已修改，请重新登录')
  await userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="profile-page">
    <h3>个人资料</h3>
    <el-form label-width="80px">
      <el-form-item label="头像">
        <div class="avatar-section">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :http-request="({ file }) => handleAvatarUpload(file)"
          >
            <el-avatar :size="100" :src="profileForm.avatar || DEFAULT_AVATAR" class="avatar-preview" />
            <div class="avatar-overlay">
              <el-icon :size="20"><Plus /></el-icon>
              <span>更换头像</span>
            </div>
          </el-upload>
          <p class="avatar-hint">支持 JPG / PNG / GIF / WebP，大小不超过 2MB</p>
        </div>
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="简介">
        <el-input v-model="profileForm.bio" type="textarea" :rows="3" placeholder="介绍一下自己..." />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="uploading" @click="handleUpdateProfile">更新资料</el-button>
      </el-form-item>
    </el-form>

    <h3 style="margin-top:40px">修改密码</h3>
    <el-form label-width="80px">
      <el-form-item label="旧密码">
        <el-input v-model="passwordForm.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="passwordForm.newPassword" type="password" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="danger" @click="handleChangePassword">修改密码</el-button>
      </el-form-item>
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

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
}

.avatar-uploader {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
  transition: transform var(--transition-fast);
}

.avatar-uploader:hover {
  transform: scale(1.04);
}

.avatar-preview {
  display: block;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: var(--text-xs);
  opacity: 0;
  transition: opacity var(--transition-fast);
  border-radius: 50%;
}

.avatar-uploader:hover .avatar-overlay {
  opacity: 1;
}

.avatar-hint {
  margin: 0;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}
</style>
