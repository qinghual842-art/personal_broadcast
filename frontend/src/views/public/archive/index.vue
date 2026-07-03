<script setup>
import { ref, onMounted } from 'vue'
import { getArchive } from '@/api/article'
import { useRouter } from 'vue-router'
import { formatDate } from '@/utils/date'

const archives = ref([])
const router = useRouter()

onMounted(async () => {
  const res = await getArchive()
  archives.value = res.data
})
</script>

<template>
  <div class="archive-page">
    <div class="page-header">
      <h2 class="page-title">文章归档</h2>
      <p class="page-desc">按时间线浏览所有文章</p>
    </div>

    <div class="archive-timeline" v-if="archives.length">
      <div class="archive-group" v-for="group in archives" :key="group.yearMonth">
        <div class="timeline-dot"></div>
        <div class="timeline-content">
          <h3 class="group-year">{{ group.yearMonth }}</h3>
          <div class="group-articles">
            <div
              class="archive-item"
              v-for="a in group.articles"
              :key="a.id"
              @click="router.push(`/article/${a.id}`)"
            >
              <span class="item-date">{{ formatDate(a.createTime, 'MM-DD') }}</span>
              <span class="item-title">{{ a.title }}</span>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" class="item-arrow">
                <path d="M5 12h14M12 5l7 7-7 7" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-else description="暂无文章" />
  </div>
</template>

<style scoped>
.archive-page {
  max-width: 960px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease-out;
}

.page-header {
  margin-bottom: var(--space-8);
}

.page-title {
  font-size: var(--text-3xl);
  font-weight: 700;
  font-family: var(--font-serif);
  margin: 0 0 var(--space-1);
  letter-spacing: -0.02em;
  color: var(--text-primary);
}

.page-desc {
  margin: 0;
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

/* Timeline */
.archive-timeline {
  position: relative;
  padding-left: 24px;
}

.archive-timeline::before {
  content: '';
  position: absolute;
  left: 7px;
  top: 8px;
  bottom: 8px;
  width: 2px;
  background: linear-gradient(180deg, var(--color-amber-400), var(--border-default));
}

.archive-group {
  position: relative;
  margin-bottom: var(--space-8);
}

.timeline-dot {
  position: absolute;
  left: -20px;
  top: 8px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: var(--color-amber-500);
  border: 2px solid var(--bg-page);
  box-shadow: 0 0 0 3px rgba(217, 168, 83, 0.15);
  z-index: 1;
}

.timeline-content {
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  box-shadow: var(--shadow-card);
}

.group-year {
  font-size: var(--text-lg);
  font-weight: 700;
  font-family: var(--font-serif);
  color: var(--color-amber-600);
  margin: 0 0 var(--space-3);
  letter-spacing: -0.01em;
}

.group-articles {
  display: flex;
  flex-direction: column;
}

.archive-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-2);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);

  &:hover {
    background: var(--color-amber-50);

    .item-title {
      color: var(--color-amber-600);
    }

    .item-arrow {
      opacity: 1;
      transform: translateX(2px);
    }
  }
}

.item-date {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-tertiary);
  background: var(--bg-subtle);
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  white-space: nowrap;
}

.item-title {
  flex: 1;
  font-size: var(--text-sm);
  color: var(--text-primary);
  font-weight: 500;
  transition: color var(--transition-fast);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-arrow {
  opacity: 0;
  flex-shrink: 0;
  transition: all var(--transition-fast);
}
</style>
