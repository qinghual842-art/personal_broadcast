<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserArticles, deleteUserArticle, updateArticleStatus } from '@/api/article'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const articles = ref([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)
const keyword = ref('')

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  fetchArticles()
})

async function fetchArticles() {
  loading.value = true
  try {
    const params = { page: page.value, size: 10 }
    if (keyword.value) params.keyword = keyword.value
    const res = await getUserArticles(params)
    articles.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchArticles()
}

function goCreate() {
  router.push('/editor')
}

function goEdit(id) {
  router.push(`/editor?id=${id}`)
}

async function handleDelete(article) {
  try {
    await ElMessageBox.confirm(`确定删除文章「${article.title}」？`, '提示', { type: 'warning' })
    await deleteUserArticle(article.id)
    ElMessage.success('已删除')
    fetchArticles()
  } catch { /* cancelled */ }
}

function handlePageChange(p) {
  page.value = p
  fetchArticles()
}

function statusText(status) {
  return status === 1 ? '已发布' : '草稿'
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}
</script>

<template>
  <div class="my-articles-page">
    <div class="page-header">
      <h2>我的文章</h2>
      <el-button type="primary" :icon="Plus" @click="goCreate">写文章</el-button>
    </div>

    <div class="page-toolbar">
      <el-input v-model="keyword" placeholder="搜索文章标题..." :prefix-icon="Search" class="search-input" clearable @keyup.enter="handleSearch" @clear="handleSearch" />
      <el-button @click="handleSearch">搜索</el-button>
    </div>

    <div v-loading="loading" class="article-table-wrap">
      <el-table v-if="articles.length" :data="articles" style="width:100%" stripe>
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <span class="article-title">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读" width="80" />
        <el-table-column label="发布时间" width="120">
          <template #default="{ row }">
            <span class="article-date">{{ formatDate(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="goEdit(row.id)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-else class="empty-state">
        <p>还没有文章，快去写第一篇吧</p>
      </div>

      <div v-if="total > 10" class="pagination-wrap">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="10"
          v-model:current-page="page"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.my-articles-page {
  max-width: 960px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease-out;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-5);
}

.page-header h2 {
  font-family: var(--font-serif);
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.page-toolbar {
  display: flex;
  gap: var(--space-3);
  margin-bottom: var(--space-5);
}

.search-input {
  width: 300px;
}

.article-table-wrap {
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  box-shadow: var(--shadow-card);
}

.article-title {
  font-weight: 600;
  color: var(--text-primary);
}

.article-date {
  color: var(--text-tertiary);
  font-size: var(--text-xs);
}

.empty-state {
  text-align: center;
  padding: var(--space-12) 0;
  color: var(--text-tertiary);
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: var(--space-5);
}
</style>
