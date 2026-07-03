<script setup>
import { useRouter } from 'vue-router'
import { formatDate } from '@/utils/date'

const props = defineProps({
  article: { type: Object, required: true }
})

const router = useRouter()

function goDetail() {
  router.push(`/article/${props.article.id}`)
}
</script>

<template>
  <article class="article-card" @click="goDetail">
    <!-- 左侧金色装饰条 — hover 时显现 -->
    <div class="card-accent"></div>

    <div class="card-body">
      <!-- 顶部分类标签 + 日期 -->
      <div class="card-meta">
        <span v-if="article.categoryName" class="category-tag">{{ article.categoryName }}</span>
        <span class="card-date">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" class="date-icon">
            <rect x="3" y="4" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/>
            <path d="M16 2v4M8 2v4M3 10h18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          {{ formatDate(article.createTime, 'YYYY-MM-DD') }}
        </span>
      </div>

      <!-- 标题 — 衬线字体 -->
      <h3 class="card-title">{{ article.title }}</h3>

      <!-- 摘要 — 两行截断 -->
      <p class="card-summary">{{ article.summary || article.content?.substring(0, 160) }}</p>

      <!-- 底部信息 -->
      <div class="card-footer">
        <div class="card-stats">
          <span class="stat-item" title="阅读量">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2"/>
              <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
            </svg>
            {{ article.viewCount || 0 }}
          </span>
          <span class="stat-item" title="评论数">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
              <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2v10z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            {{ article.commentCount || 0 }}
          </span>
        </div>
        <!-- 阅读全文 — hover 时箭头滑入 -->
        <span class="read-more">
          阅读全文
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" class="read-more-arrow">
            <path d="M5 12h14M12 5l7 7-7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </span>
      </div>
    </div>

    <!-- 封面图 — hover 缩放 -->
    <div v-if="article.coverImage" class="card-cover">
      <img :src="article.coverImage" :alt="article.title" loading="lazy" />
    </div>
  </article>
</template>

<style scoped>
.article-card {
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  cursor: pointer;
  display: flex;
  gap: var(--space-6);
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
  box-shadow: var(--shadow-card);

  &:hover {
    transform: translateY(-2px);
    border-color: var(--color-amber-300);
    box-shadow: var(--shadow-md);
  }
}

/* 左侧金色装饰条 — 默认隐藏，hover 淡入 */
.card-accent {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(180deg, var(--color-amber-400), var(--color-amber-600));
  border-radius: 0 2px 2px 0;
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.article-card:hover .card-accent {
  opacity: 1;
}

.card-body {
  flex: 1;
  min-width: 0;
}

/* 元信息行 */
.card-meta {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-3);
}

.category-tag {
  font-size: 11px;
  font-weight: 600;
  color: var(--color-amber-700);
  background: rgba(212, 168, 83, 0.10);
  padding: 2px 10px;
  border-radius: var(--radius-sm);
  letter-spacing: 0.02em;
  border: 1px solid rgba(212, 168, 83, 0.15);
}

.card-date {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-tertiary);
  font-weight: 500;

  .date-icon {
    flex-shrink: 0;
  }
}

/* 标题 */
.card-title {
  margin: 0 0 var(--space-3);
  font-family: var(--font-serif);
  font-size: var(--text-lg);
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.4;
  transition: color var(--transition-fast);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-card:hover .card-title {
  color: var(--color-primary-600);
}

/* 摘要 */
.card-summary {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  line-height: 1.7;
  margin: 0 0 var(--space-4);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 底部 */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-stats {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-tertiary);
  font-weight: 500;
}

/* 阅读全文 — hover 时滑入 */
.read-more {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--color-amber-500);
  opacity: 0;
  transform: translateX(-8px);
  transition: all var(--transition-normal);

  .read-more-arrow {
    transition: transform var(--transition-fast);
  }
}

.article-card:hover .read-more {
  opacity: 1;
  transform: translateX(0);
}

.article-card:hover .read-more .read-more-arrow {
  transform: translateX(2px);
}

/* 封面图 */
.card-cover {
  flex-shrink: 0;
  width: 200px;
  height: 130px;
  overflow: hidden;
  border-radius: var(--radius-md);
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: var(--radius-md);
  transition: transform 0.4s var(--ease-ink);
}

.article-card:hover .card-cover img {
  transform: scale(1.05);
}

/* ═══ Responsive ═══ */
@media (max-width: 768px) {
  .article-card {
    flex-direction: column-reverse;
    padding: var(--space-5);
    gap: var(--space-4);
  }

  .card-cover {
    width: 100%;
    height: auto;
    max-height: 200px;
  }

  .card-title {
    font-size: var(--text-base);
  }

  /* Always show read more on mobile */
  .read-more {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>
