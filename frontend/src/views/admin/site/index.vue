<script setup>
import { ref, onMounted } from 'vue'
import { getAdminSiteConfig, updateSiteConfig } from '@/api/site'
import { ElMessage } from 'element-plus'

const form = ref({})

onMounted(async () => {
  const res = await getAdminSiteConfig()
  form.value = res.data || {}
})

async function handleSave() {
  await updateSiteConfig(form.value)
  ElMessage.success('设置已保存')
}
</script>

<template>
  <div class="settings-page">
    <h3>站点设置</h3>
    <el-form label-width="100px">
      <el-form-item label="站点名称"><el-input v-model="form.site_name" /></el-form-item>
      <el-form-item label="站点描述"><el-input v-model="form.site_desc" type="textarea" :rows="2" /></el-form-item>
      <el-form-item label="关键词"><el-input v-model="form.keywords" /></el-form-item>
      <el-form-item label="版权信息"><el-input v-model="form.copyright" /></el-form-item>
      <el-form-item label="关于我"><el-input v-model="form.about_me" type="textarea" :rows="6" placeholder="支持 Markdown" /></el-form-item>
      <el-form-item label="社交链接"><el-input v-model="form.social_links" type="textarea" :rows="3" placeholder='JSON格式' /></el-form-item>
      <el-form-item><el-button type="primary" @click="handleSave">保存设置</el-button></el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.settings-page {
  background: var(--bg-surface);
  padding: var(--space-8);
  border-radius: var(--radius-xl);
  max-width: 700px;
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-card);
}
.settings-page h3 {
  margin: 0 0 var(--space-5);
  font-size: var(--text-xl);
  font-weight: 700;
  color: var(--text-primary);
  font-family: var(--font-display);
}
</style>
