<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { searchArticles } from '@/api/article'
import ArticleCard from '@/components/article/ArticleCard.vue'
import Pagination from '@/components/common/Pagination.vue'

const route = useRoute()
const articles = ref([])
const total = ref(0)
const page = ref(1)
const keyword = ref(route.query.keyword || '')

async function fetch() {
  if (!keyword.value) return
  const res = await searchArticles({ keyword: keyword.value, page: page.value, size: 10 })
  articles.value = res.data.records
  total.value = res.data.total
}

watch(() => route.query.keyword, (val) => {
  keyword.value = val || ''
  page.value = 1
  fetch()
}, { immediate: true })

function handlePageChange(p) {
  page.value = p
  fetch()
}
</script>

<template>
  <div class="search-page">
    <h2>搜索结果: "{{ keyword }}"</h2>
    <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    <Pagination :total="total" :page="page" :size="10" @change="handlePageChange" />
    <el-empty v-if="!articles.length" description="未找到相关文章" />
  </div>
</template>

<style scoped>
.search-page {
  animation: fadeInUp 0.5s ease-out;
}
.search-page h2 {
  margin: 0 0 var(--space-6);
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  font-family: var(--font-serif);
}
</style>
