<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTags } from '@/api/tag'

const router = useRouter()
const tags = ref([])

onMounted(async () => {
  const res = await getTags()
  tags.value = res.data
})

function goTag(tag) {
  router.push(`/tag/${tag.id}`)
}
</script>

<template>
  <div class="tag-list-page">
    <div class="page-header">
      <h2>文章标签</h2>
      <p>按标签探索感兴趣的内容</p>
    </div>

    <div class="tag-cloud" v-if="tags.length">
      <span
        v-for="tag in tags" :key="tag.id"
        class="tag-chip"
        @click="goTag(tag)"
      >
        {{ tag.name }}
        <span class="chip-count">{{ tag.articleCount }}</span>
      </span>
    </div>

    <el-empty v-else description="暂无标签" />
  </div>
</template>

<style scoped>
.tag-list-page {
  max-width: 1100px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease-out;
}

.page-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.page-header h2 {
  font-size: var(--text-3xl);
  font-weight: 700;
  font-family: var(--font-display);
  color: var(--text-primary);
  margin: 0 0 var(--space-2);
  letter-spacing: -0.02em;
}

.page-header p {
  margin: 0;
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.tag-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: var(--text-base);
  padding: var(--space-2) var(--space-5);
  border-radius: 24px;
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  color: var(--text-secondary);
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
  box-shadow: var(--shadow-card);

  &:hover {
    transform: translateY(-2px);
    background: var(--bg-accent-light);
    border-color: var(--color-primary-200);
    color: var(--color-primary-600);
    box-shadow: var(--shadow-md);
  }
}

.chip-count {
  font-size: 11px;
  font-weight: 700;
  color: var(--color-primary-500);
  background: var(--bg-accent-light);
  padding: 1px 6px;
  border-radius: 10px;
}
</style>
