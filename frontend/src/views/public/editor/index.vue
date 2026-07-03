<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { createUserArticle, updateUserArticle, getUserArticle } from '@/api/article'
import { getCategories } from '@/api/category'
import { getTags } from '@/api/tag'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isEdit = ref(false)

const form = ref({
  title: '', summary: '', content: '', categoryId: null,
  coverImage: '', status: 1, tagIds: []
})
const categories = ref([])
const tags = ref([])
const saving = ref(false)

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  const editId = route.query.id
  isEdit.value = !!editId

  const [catRes, tagRes] = await Promise.all([getCategories(), getTags()])
  categories.value = catRes.data
  tags.value = tagRes.data

  if (editId) {
    try {
      const res = await getUserArticle(editId)
      form.value.title = res.data.title || ''
      form.value.summary = res.data.summary || ''
      form.value.content = res.data.content || ''
      form.value.categoryId = res.data.categoryId || null
      form.value.status = res.data.status ?? 0
      form.value.tagIds = (res.data.tags || []).map(Number)
    } catch {
      ElMessage.error('文章不存在')
      router.push('/my-articles')
    }
  }
})

async function handleSave() {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('标题和内容不能为空')
    return
  }
  saving.value = true
  try {
    const data = { ...form.value }
    data.tagIds = data.tagIds.map(Number)
    if (isEdit.value) {
      await updateUserArticle(route.query.id, data)
      ElMessage.success('更新成功')
    } else {
      await createUserArticle(data)
      ElMessage.success('发布成功')
    }
    router.push('/my-articles')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="editor-page">
    <div class="edit-header">
      <h3>{{ isEdit ? '编辑文章' : '写文章' }}</h3>
      <div class="header-btns">
        <el-button @click="router.push('/my-articles')">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">{{ isEdit ? '保存修改' : '发布文章' }}</el-button>
      </div>
    </div>

    <el-form label-width="80px">
      <el-form-item label="标题">
        <el-input v-model="form.title" placeholder="文章标题" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="form.categoryId" placeholder="选择分类" clearable>
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="标签">
        <el-select v-model="form.tagIds" multiple placeholder="选择标签">
          <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="摘要">
        <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="文章摘要（可选）" />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :value="0">草稿</el-radio>
          <el-radio :value="1">发布</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="内容">
        <el-input v-model="form.content" type="textarea" :rows="18" placeholder="支持 Markdown 格式" />
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.editor-page {
  max-width: 960px;
  margin: 0 auto;
  background: var(--bg-surface);
  padding: var(--space-8);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-card);
  animation: fadeInUp 0.5s ease-out;
}

.edit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
  padding-bottom: var(--space-5);
  border-bottom: 1px solid var(--border-light);
}

.edit-header h3 {
  margin: 0;
  font-size: var(--text-xl);
  font-weight: 700;
  color: var(--text-primary);
  font-family: var(--font-display);
}

.header-btns {
  display: flex;
  gap: var(--space-2);
}
</style>
