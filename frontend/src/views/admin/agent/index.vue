<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAdminAgents, deleteAgent, toggleAgent } from '@/api/agent'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const agents = ref([])

async function fetch() {
  const res = await getAdminAgents()
  agents.value = res.data
}

async function handleToggle(row) {
  await toggleAgent(row.id)
  ElMessage.success(row.isEnabled === 1 ? '已关闭' : '已启用')
  fetch()
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除？', '确认', { type: 'warning' })
  await deleteAgent(id)
  ElMessage.success('删除成功')
  fetch()
}

onMounted(fetch)
</script>

<template>
  <div class="manage-page">
    <el-button type="primary" @click="router.push('/admin/agents/create')">新建智能体</el-button>
    <el-table :data="agents" style="width:100%;margin-top:16px">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="provider" label="提供商" width="100" />
      <el-table-column prop="modelName" label="模型" width="120" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.isEnabled === 1 ? 'success' : 'info'" size="small">
            {{ row.isEnabled === 1 ? '启用' : '关闭' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <el-button size="small" @click="router.push(`/admin/agents/${row.id}/edit`)">编辑</el-button>
          <el-button size="small" @click="handleToggle(row)">{{ row.isEnabled === 1 ? '关闭' : '启用' }}</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
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
