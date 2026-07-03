<script setup>
import { ref, onMounted } from 'vue'
import { getAdminTags, createTag, updateTag, deleteTag } from '@/api/tag'
import { ElMessage, ElMessageBox } from 'element-plus'

const tags = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ name: '' })
const editId = ref(null)

async function fetch() {
  const res = await getAdminTags()
  tags.value = res.data
}

function handleCreate() {
  isEdit.value = false; editId.value = null; form.value = { name: '' }; dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true; editId.value = row.id; form.value = { name: row.name }; dialogVisible.value = true
}

async function handleSave() {
  if (isEdit.value) { await updateTag(editId.value, form.value) }
  else { await createTag(form.value) }
  ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
  dialogVisible.value = false
  fetch()
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该标签？', '确认', { type: 'warning' })
  await deleteTag(id)
  ElMessage.success('删除成功')
  fetch()
}

onMounted(fetch)
</script>

<template>
  <div class="manage-page">
    <el-button type="primary" @click="handleCreate">新建标签</el-button>
    <el-table :data="tags" style="width:100%;margin-top:16px">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="articleCount" label="文章数" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑标签' : '新建标签'" width="400px">
      <el-input v-model="form.name" placeholder="标签名称" />
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
