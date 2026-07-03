<script setup>
import { ref, onMounted } from 'vue'
import { getAdminArticles } from '@/api/article'
import { getAdminComments } from '@/api/comment'
import { getAdminAgents } from '@/api/agent'

const articleCount = ref(0)
const commentCount = ref(0)
const agentCount = ref(0)
const publishedCount = ref(0)
const loading = ref(true)

onMounted(async () => {
  try {
    const [aRes, cRes, agRes] = await Promise.all([
      getAdminArticles({ page: 1, size: 1 }),
      getAdminComments({ page: 1, size: 1 }),
      getAdminAgents()
    ])
    articleCount.value = aRes.data.total
    commentCount.value = cRes.data.total
    agentCount.value = agRes.data.length
    publishedCount.value = aRes.data.total
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="dashboard" v-loading="loading">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-heading">仪表盘</h2>
      <p class="page-desc">欢迎回来，这里是你的站点概览</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card stat-card--articles">
        <div class="stat-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
            <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8l-6-6z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M14 2v6h6M16 13H8M16 17H8M10 9H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-body">
          <span class="stat-num">{{ articleCount }}</span>
          <span class="stat-label">文章总数</span>
        </div>
        <div class="stat-trend">
          <span v-if="publishedCount" class="trend-badge">已发布 {{ publishedCount }}</span>
        </div>
      </div>

      <div class="stat-card stat-card--comments">
        <div class="stat-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
            <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2v10z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-body">
          <span class="stat-num">{{ commentCount }}</span>
          <span class="stat-label">评论总数</span>
        </div>
        <div class="stat-trend">
          <span class="trend-badge trend-badge--neutral">需审核</span>
        </div>
      </div>

      <div class="stat-card stat-card--agents">
        <div class="stat-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
            <path d="M12 2v3M12 19v3M3.5 5.5l2.12 2.12M18.36 16.36l2.12 2.12M2 12h3M19 12h3M3.5 18.5l2.12-2.12M18.36 7.64l2.12-2.12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="stat-body">
          <span class="stat-num">{{ agentCount }}</span>
          <span class="stat-label">智能体数量</span>
        </div>
        <div class="stat-trend">
          <span class="trend-badge trend-badge--active">运行中</span>
        </div>
      </div>

      <div class="stat-card stat-card--users">
        <div class="stat-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
            <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            <circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
            <path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="stat-body">
          <span class="stat-num">1</span>
          <span class="stat-label">管理员账户</span>
        </div>
        <div class="stat-trend">
          <span class="trend-badge trend-badge--active">在线</span>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h3 class="section-title">快捷操作</h3>
      <div class="actions-grid">
        <router-link to="/admin/articles/create" class="action-card">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <span>写文章</span>
        </router-link>
        <router-link to="/admin/categories" class="action-card">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <path d="M22 19a2 2 0 01-2 2H4a2 2 0 01-2-2V5a2 2 0 012-2h5l2 3h9a2 2 0 012 2v11z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>管理分类</span>
        </router-link>
        <router-link to="/admin/tags" class="action-card">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <path d="M20.59 13.41l-7.17 7.17a2 2 0 01-2.83 0L2 12V2h10l8.59 8.59a2 2 0 010 2.82zM7 7h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>管理标签</span>
        </router-link>
        <router-link to="/admin/site" class="action-card">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
            <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06A1.65 1.65 0 0019.4 9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z" stroke="currentColor" stroke-width="2"/>
          </svg>
          <span>站点设置</span>
        </router-link>
      </div>
    </div>

    <!-- 底部提示 -->
    <div class="dashboard-footer">
      <p>此仪表盘展示博客站点的基本运行状态。更多数据统计功能正在开发中。</p>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  max-width: 1100px;
}

/* Page Header */
.page-header {
  margin-bottom: var(--space-8);
}

.page-heading {
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 var(--space-1);
  letter-spacing: -0.02em;
  font-family: var(--font-serif);
}

.page-desc {
  margin: 0;
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-5);
  margin-bottom: var(--space-8);
}

.stat-card {
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-top: 3px solid var(--color-primary-500);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;

  &:hover {
    border-color: var(--border-accent);
    box-shadow: var(--shadow-md);
    transform: translateY(-2px);
  }
}

.stat-icon {
  color: var(--color-primary-500);
  opacity: 0.8;
}

.stat-card--articles { border-top-color: var(--color-primary-500); }
.stat-card--articles .stat-icon { color: var(--color-primary-500); }
.stat-card--comments { border-top-color: var(--color-amber-500); }
.stat-card--comments .stat-icon { color: var(--color-amber-500); }
.stat-card--agents { border-top-color: var(--color-jade-500); }
.stat-card--agents .stat-icon { color: var(--color-jade-500); }
.stat-card--users { border-top-color: #8b5cf6; }
.stat-card--users .stat-icon { color: #8b5cf6; }

.stat-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-num {
  font-size: var(--text-3xl);
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  font-weight: 500;
}

.stat-trend {
  padding-top: var(--space-2);
  border-top: 1px solid var(--border-light);
}

.trend-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 10px;
  background: var(--color-amber-50);
  color: var(--color-amber-700);

  &--neutral {
    background: var(--bg-subtle);
    color: var(--text-secondary);
  }

  &--active {
    background: var(--color-amber-50);
    color: var(--color-amber-700);
  }
}

/* Quick Actions */
.quick-actions {
  margin-bottom: var(--space-8);
}

.section-title {
  font-size: var(--text-base);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--space-4);
  font-family: var(--font-serif);
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-4);
}

.action-card {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--space-4) var(--space-5);
  text-decoration: none;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-weight: 500;
  transition: all var(--transition-fast);
  box-shadow: var(--shadow-card);

  &:hover {
    border-color: var(--color-amber-300);
    color: var(--color-amber-600);
    background: var(--color-amber-50);
    transform: translateY(-1px);
  }
}

/* Footer */
.dashboard-footer {
  text-align: center;
  padding: var(--space-6);
  border-top: 1px solid var(--border-light);

  p {
    margin: 0;
    font-size: var(--text-xs);
    color: var(--text-tertiary);
  }
}

@media (max-width: 1024px) {
  .stats-grid,
  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .stats-grid,
  .actions-grid {
    grid-template-columns: 1fr;
  }
}
</style>
