<script setup>
import { ref, onMounted } from 'vue'
import { getAdminComments, updateCommentStatus, deleteComment, batchDeleteComments } from '@/api/comment'
import { formatDate } from '@/utils/date'
import { ElMessage, ElMessageBox } from 'element-plus'

const comments = ref([])
const total = ref(0)
const page = ref(1)
const selectedIds = ref([])

async function fetch() {
  const res = await getAdminComments({ page: page.value, size: 10 })
  comments.value = res.data.records
  total.value = res.data.total
  selectedIds.value = []
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.id)
}

async function handleApprove(id) {
  await updateCommentStatus(id, 1)
  ElMessage.success('已通过')
  fetch()
}

async function handleReject(id) {
  await updateCommentStatus(id, 2)
  ElMessage.success('已拒绝')
  fetch()
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该评论？', '确认', { type: 'warning' })
  await deleteComment(id)
  ElMessage.success('删除成功')
  fetch()
}

async function handleBatchDelete() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择要删除的评论')
    return
  }
  await ElMessageBox.confirm(
    `确定删除选中的 ${selectedIds.value.length} 条评论？`,
    '批量删除确认',
    { type: 'warning' }
  )
  await batchDeleteComments(selectedIds.value)
  ElMessage.success('批量删除成功')
  fetch()
}

onMounted(fetch)
</script>

<template>
  <div class="manage-page">
    <!-- 批量操作工具栏 -->
    <div class="toolbar" v-if="selectedIds.length > 0">
      <span class="selected-info">已选 {{ selectedIds.length }} 项</span>
      <el-button type="danger" size="small" @click="handleBatchDelete">批量删除</el-button>
    </div>

    <el-table
      :data="comments"
      style="width:100%"
      @selection-change="handleSelectionChange"
      ref="tableRef"
    >
      <el-table-column type="selection" width="45" />
      <el-table-column prop="authorName" label="评论者" width="100" />
      <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'info'" size="small">
            {{ row.status === 1 ? '通过' : row.status === 2 ? '拒绝' : '待审' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="时间" width="160">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="handleApprove(row.id)" v-if="row.status !== 1">通过</el-button>
          <el-button size="small" type="warning" @click="handleReject(row.id)" v-if="row.status !== 2">拒绝</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap" v-if="total > 10">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="10"
        v-model:current-page="page"
        @current-change="fetch"
      />
    </div>
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

.toolbar {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
  padding: var(--space-3) var(--space-4);
  background: var(--bg-accent-light);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-accent);
}

.selected-info {
  font-size: var(--text-sm);
  color: var(--color-primary-600);
  font-weight: 600;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding-top: var(--space-6);
}
</style>
