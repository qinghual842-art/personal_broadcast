<script setup>
import { ref, onMounted } from 'vue'
import { getAdminCategories, createCategory, updateCategory, deleteCategory } from '@/api/category'
import { ElMessage, ElMessageBox } from 'element-plus'

const categories = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ name: '', description: '', sortOrder: 0 })
const editId = ref(null)

async function fetch() {
  const res = await getAdminCategories()
  categories.value = res.data
}

function handleCreate() {
  isEdit.value = false; editId.value = null
  form.value = { name: '', description: '', sortOrder: 0 }
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true; editId.value = row.id
  form.value = { name: row.name, description: row.description, sortOrder: row.sortOrder }
  dialogVisible.value = true
}

async function handleSave() {
  if (isEdit.value) {
    await updateCategory(editId.value, form.value)
    ElMessage.success('更新成功')
  } else {
    await createCategory(form.value)
    ElMessage.success('创建成功')
  }
  dialogVisible.value = false
  fetch()
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该分类？', '确认', { type: 'warning' })
  await deleteCategory(id)
  ElMessage.success('删除成功')
  fetch()
}

onMounted(fetch)
</script>

<template>
  <div class="manage-page">
    <el-button type="primary" @click="handleCreate">新建分类</el-button>
    <el-table :data="categories" style="width:100%;margin-top:16px">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="articleCount" label="文章数" width="80" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新建分类'" width="500px">
      <el-form>
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.manage-page {
  background: var(--bg-surface);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-card);
}
</style>
