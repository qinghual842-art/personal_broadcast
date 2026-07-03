<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticleDetail } from '@/api/article'
import { formatDate } from '@/utils/date'
import MarkdownRenderer from '@/components/article/MarkdownRenderer.vue'
import CommentSection from '@/components/article/CommentSection.vue'

const route = useRoute()
const router = useRouter()
const article = ref(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await getArticleDetail(route.params.slugOrId)
    article.value = res.data
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="article-detail" v-loading="loading">
    <template v-if="article">
      <!-- 文章头部 -->
      <div class="detail-card">
        <header class="detail-header">
          <h1 class="detail-title">{{ article.title }}</h1>
          <div class="detail-author" v-if="article.authorName">
            <el-avatar :size="28" :src="article.authorAvatar || 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif'" />
            <span class="author-name">{{ article.authorName }}</span>
          </div>
          <div class="detail-meta">
            <span class="meta-item">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                <rect x="3" y="4" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/>
                <path d="M16 2v4M8 2v4M3 10h18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
              {{ formatDate(article.createTime) }}
            </span>
            <span class="meta-sep"></span>
            <span class="meta-item meta-views">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2"/>
                <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
              </svg>
              {{ article.viewCount }} 次阅读
            </span>
            <span class="meta-sep"></span>
            <span v-if="article.categoryName" class="meta-item">
              <el-tag size="small" type="primary">{{ article.categoryName }}</el-tag>
            </span>
          </div>
        </header>

        <!-- 文章正文 -->
        <div class="detail-body">
          <MarkdownRenderer :content="article.content" />
        </div>

        <!-- 标签 -->
        <div class="detail-tags" v-if="article.tags?.length">
          <span class="tags-label">标签：</span>
          <el-tag
            v-for="tag in article.tags"
            :key="tag"
            size="small"
            class="tag-item"
          >{{ tag }}</el-tag>
        </div>
      </div>

      <!-- 上一篇 / 下一篇 -->
      <nav class="detail-nav" v-if="article.prev || article.next">
        <router-link
          v-if="article.prev"
          :to="`/article/${article.prev.id}`"
          class="nav-link nav-prev"
        >
          <span class="nav-arrow">&larr;</span>
          <span class="nav-text">
            <span class="nav-label">上一篇</span>
            <span class="nav-title">{{ article.prev.title }}</span>
          </span>
        </router-link>
        <router-link
          v-if="article.next"
          :to="`/article/${article.next.id}`"
          class="nav-link nav-next"
        >
          <span class="nav-text">
            <span class="nav-label">下一篇</span>
            <span class="nav-title">{{ article.next.title }}</span>
          </span>
          <span class="nav-arrow">&rarr;</span>
        </router-link>
      </nav>

      <!-- 评论 -->
      <CommentSection v-if="article.id" :article-id="article.id" />
    </template>

    <!-- AI 漂浮按钮 -->
    <div class="ai-float-btn" @click="router.push('/agents')" title="AI 智能助手">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
        <circle cx="12" cy="12" r="2" fill="white"/>
        <path d="M12 4v2M12 18v2M4 12h2M18 12h2M7.05 7.05l1.41 1.41M15.54 15.54l1.41 1.41M7.05 16.95l1.41-1.41M15.54 8.46l1.41-1.41" stroke="white" stroke-width="2" stroke-linecap="round"/>
      </svg>
    </div>
  </div>
</template>

<style scoped>
.article-detail {
  max-width: 960px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease-out;
}

/* Unified card wrapper */
.detail-card {
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  margin-bottom: var(--space-5);
  overflow: hidden;
  border-top: 3px solid transparent;
  border-image: linear-gradient(90deg, var(--color-amber-400), var(--color-amber-600), var(--color-amber-400)) 1;
  border-top-left-radius: var(--radius-xl);
  border-top-right-radius: var(--radius-xl);
}

/* Header */
.detail-header {
  padding: var(--space-10) var(--space-10) var(--space-6);
  border-bottom: 1px solid var(--border-light);
}

.detail-title {
  font-size: var(--text-3xl);
  font-weight: 700;
  font-family: var(--font-serif);
  color: var(--text-primary);
  line-height: 1.35;
  margin: 0 0 var(--space-4);
  letter-spacing: 0.01em;
}

.detail-author {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-5);
}

.author-name {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-weight: 600;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  font-weight: 500;
}

.meta-views {
  color: var(--color-amber-500);
}

.meta-sep {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--border-default);
}

/* Body */
.detail-body {
  padding: var(--space-10);
  line-height: 1.9;
  overflow-x: auto;
}

/* Tags */
.detail-tags {
  padding: 0 var(--space-8) var(--space-6);
  display: flex;
  align-items: center;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.tags-label {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  font-weight: 500;
}

.tag-item {
  cursor: pointer;
}

/* Navigation */
.detail-nav {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.nav-link {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--space-4) var(--space-5);
  text-decoration: none;
  transition: all var(--transition-fast);
  box-shadow: var(--shadow-card);

  &:hover {
    border-color: var(--color-amber-300);
    box-shadow: var(--shadow-md);

    .nav-title {
      color: var(--color-amber-600);
    }
  }
}

.nav-next {
  text-align: right;

  .nav-arrow {
    order: 1;
  }
}

.nav-arrow {
  font-size: var(--text-lg);
  color: var(--color-amber-500);
  flex-shrink: 0;
}

.nav-text {
  min-width: 0;
}

.nav-label {
  display: block;
  font-size: 11px;
  color: var(--text-tertiary);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 2px;
}

.nav-title {
  display: block;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color var(--transition-fast);
}

/* AI Float Button */
.ai-float-btn {
  position: fixed;
  bottom: 40px;
  right: 40px;
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, var(--color-amber-500), var(--color-amber-600));
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(217, 168, 83, 0.35);
  transition: all var(--transition-normal);
  z-index: 999;
  animation: warmBreath 4s ease-in-out infinite;

  &:hover {
    transform: scale(1.08);
    box-shadow: 0 6px 24px rgba(217, 168, 83, 0.45);
  }
}

@media (max-width: 768px) {
  .detail-header {
    padding: var(--space-5) var(--space-5) var(--space-4);
  }

  .detail-body {
    padding: var(--space-5);
  }

  .detail-tags {
    padding: 0 var(--space-5) var(--space-4);
  }

  .detail-title {
    font-size: var(--text-2xl);
  }

  .detail-nav {
    grid-template-columns: 1fr;
  }

  .ai-float-btn {
    bottom: 20px;
    right: 16px;
    width: 44px;
    height: 44px;
  }
}
</style>
