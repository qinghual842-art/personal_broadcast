<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAdminArticles, deleteArticle, batchDeleteArticles, updateArticleStatus } from '@/api/article'
import { formatDate } from '@/utils/date'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const articles = ref([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)
const selectedIds = ref([])

async function fetch() {
  loading.value = true
  try {
    const res = await getAdminArticles({ page: page.value, size: 10 })
    articles.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该文章？', '确认', { type: 'warning' })
  await deleteArticle(id)
  ElMessage.success('删除成功')
  fetch()
}

async function handleBatchDelete() {
  if (!selectedIds.value.length) { ElMessage.warning('请选择文章'); return }
  await ElMessageBox.confirm('确定删除选中的文章？', '确认', { type: 'warning' })
  await batchDeleteArticles(selectedIds.value)
  ElMessage.success('删除成功')
  selectedIds.value = []
  fetch()
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await updateArticleStatus(row.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已发布' : '已下架')
  fetch()
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(s => s.id)
}

onMounted(fetch)
</script>

<template>
  <div class="article-list">
    <div class="toolbar">
      <el-button type="primary" @click="router.push('/admin/articles/create')">新建文章</el-button>
      <el-button type="danger" :disabled="!selectedIds.length" @click="handleBatchDelete">批量删除</el-button>
    </div>
    <el-table :data="articles" v-loading="loading" @selection-change="handleSelectionChange" style="width:100%">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="阅读" width="70" />
      <el-table-column prop="commentCount" label="评论" width="70" />
      <el-table-column label="发布时间" width="160">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="router.push(`/admin/articles/${row.id}/edit`)">编辑</el-button>
          <el-button size="small" @click="handleToggleStatus(row)">
            {{ row.status === 1 ? '下架' : '发布' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-wrap" v-if="total > 10">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="10" v-model:current-page="page" @current-change="fetch" />
    </div>
  </div>
</template>

<style scoped>
.article-list {
  background: var(--bg-surface);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-card);
}
.toolbar {
  margin-bottom: var(--space-5);
  display: flex;
  gap: var(--space-3);
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding-top: var(--space-6);
}
</style>
