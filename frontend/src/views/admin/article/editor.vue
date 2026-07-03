<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createArticle, updateArticle, getAdminArticle } from '@/api/article'
import { getAdminCategories } from '@/api/category'
import { getAdminTags } from '@/api/tag'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

const form = ref({
  title: '', summary: '', content: '', categoryId: null,
  coverImage: '', status: 0, isTop: 0, tagIds: []
})
const categories = ref([])
const tags = ref([])
const saving = ref(false)

onMounted(async () => {
  const [catRes, tagRes] = await Promise.all([getAdminCategories(), getAdminTags()])
  categories.value = catRes.data
  tags.value = tagRes.data

  if (isEdit.value) {
    const res = await getAdminArticle(route.params.id)
    Object.assign(form.value, res.data)
  }
})

async function handleSave() {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('标题和内容不能为空')
    return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await updateArticle(route.params.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createArticle(form.value)
      ElMessage.success('创建成功')
      router.push('/admin/articles')
    }
  } finally { saving.value = false }
}
</script>

<template>
  <div class="article-edit">
    <div class="edit-header">
      <h3>{{ isEdit ? '编辑文章' : '新建文章' }}</h3>
      <div class="header-btns">
        <el-button @click="router.push('/admin/articles')">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </div>
    </div>
    <el-form label-width="80px">
      <el-form-item label="标题">
        <el-input v-model="form.title" placeholder="文章标题" />
      </el-form-item>
      <el-form-item label="摘要">
        <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="文章摘要（可选）" />
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
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :value="0">草稿</el-radio>
          <el-radio :value="1">发布</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="置顶">
        <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
      </el-form-item>
      <el-form-item label="内容">
        <el-input v-model="form.content" type="textarea" :rows="20" placeholder="支持 Markdown 格式" />
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.article-edit {
  background: var(--bg-surface);
  padding: var(--space-8);
  border-radius: var(--radius-xl);
  max-width: 1000px;
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-card);
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
