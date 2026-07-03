<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories } from '@/api/category'

const router = useRouter()
const categories = ref([])

onMounted(async () => {
  const res = await getCategories()
  categories.value = res.data
})

function goCategory(cat) {
  router.push(`/category/${cat.id}`)
}
</script>

<template>
  <div class="category-list-page">
    <div class="page-header">
      <h2>文章分类</h2>
      <p>探索不同领域的技术文章</p>
    </div>

    <div class="category-grid" v-if="categories.length">
      <div class="category-card" v-for="cat in categories" :key="cat.id" @click="goCategory(cat)">
        <div class="cat-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M22 19a2 2 0 01-2 2H4a2 2 0 01-2-2V5a2 2 0 012-2h5l2 3h9a2 2 0 012 2v11z" stroke="#4F46E5" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h3>{{ cat.name }}</h3>
        <p>{{ cat.description || '暂无描述' }}</p>
        <div class="cat-footer">
          <span class="article-count">{{ cat.articleCount }} 篇文章</span>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" class="arrow-icon">
            <path d="M5 12h14M12 5l7 7-7 7" stroke="#4F46E5" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
      </div>
    </div>

    <el-empty v-else description="暂无分类" />
  </div>
</template>

<style scoped>
.category-list-page {
  max-width: 1200px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease-out;
}

.page-header {
  margin-bottom: var(--space-8);
  text-align: center;
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

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--space-6);
}

.category-card {
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  cursor: pointer;
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-card);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0; left: 0; right: 0;
    height: 3px;
    background: linear-gradient(90deg, var(--color-primary-300), var(--color-primary-500));
    opacity: 0;
    transition: opacity var(--transition-normal);
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
    border-color: var(--color-primary-200);

    &::before { opacity: 1; }

    .arrow-icon {
      opacity: 1;
      transform: translateX(2px);
    }
  }
}

.cat-icon {
  margin-bottom: var(--space-4);
}

.category-card h3 {
  margin: 0 0 var(--space-2);
  color: var(--text-primary);
  font-size: var(--text-lg);
  font-family: var(--font-display);
  font-weight: 600;
}

.category-card p {
  margin: 0 0 var(--space-5);
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  line-height: 1.6;
  min-height: 40px;
}

.cat-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--space-4);
  border-top: 1px solid var(--border-light);
}

.article-count {
  font-size: var(--text-xs);
  color: var(--color-primary-600);
  font-weight: 600;
}

.arrow-icon {
  opacity: 0;
  transition: all var(--transition-normal);
}
</style>
