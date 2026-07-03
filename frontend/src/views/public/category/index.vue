<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticles } from '@/api/article'
import { getCategories } from '@/api/category'
import ArticleCard from '@/components/article/ArticleCard.vue'
import Pagination from '@/components/common/Pagination.vue'

const route = useRoute()
const articles = ref([])
const total = ref(0)
const page = ref(1)
const currentCategory = ref(null)

async function fetch() {
  const categoryId = Number(route.params.id)
  const cats = await getCategories()
  currentCategory.value = cats.data.find(c => c.id === categoryId)
  const res = await getArticles({ categoryId, page: page.value, size: 10 })
  articles.value = res.data.records
  total.value = res.data.total
}

onMounted(fetch)

function handlePageChange(p) {
  page.value = p
  fetch()
}
</script>

<template>
  <div class="category-page">
    <div class="page-header">
      <h2>分类：{{ currentCategory?.name || '' }}</h2>
      <p>共 {{ total }} 篇文章</p>
    </div>
    <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    <Pagination :total="total" :page="page" :size="10" @change="handlePageChange" />
    <el-empty v-if="!articles.length" description="该分类下暂无文章" />
  </div>
</template>

<style scoped>
.category-page {
  animation: fadeInUp 0.5s ease-out;
}
.page-header {
  margin-bottom: var(--space-6);
}
.page-header h2 {
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 var(--space-1);
  letter-spacing: -0.02em;
}
.page-header p {
  margin: 0;
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}
</style>
