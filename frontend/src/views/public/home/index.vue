<script setup>
import { ref, onMounted } from 'vue'
import { getArticles } from '@/api/article'
import { getCategories } from '@/api/category'
import { getTags } from '@/api/tag'
import ArticleCard from '@/components/article/ArticleCard.vue'
import Sidebar from '@/components/site/Sidebar.vue'
import Pagination from '@/components/common/Pagination.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const articles = ref([])
const total = ref(0)
const page = ref(1)
const categoryCount = ref(0)
const tagCount = ref(0)

async function fetchArticles() {
  const res = await getArticles({ page: page.value, size: 10 })
  articles.value = res.data.records
  total.value = res.data.total
}

async function fetchStats() {
  const [catRes, tagRes] = await Promise.all([
    getCategories(),
    getTags()
  ])
  categoryCount.value = catRes.data?.length || 0
  tagCount.value = tagRes.data?.length || 0
}

function handlePageChange(p) {
  page.value = p
  fetchArticles()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  fetchArticles()
  fetchStats()
})
</script>

<template>
  <div class="home-page">
    <div class="home-main">
      <!-- Hero Banner -->
      <section class="hero-banner">
        <div class="hero-bg-pattern"></div>
        <div class="hero-body">
          <div class="hero-avatar-wrap">
            <div class="avatar-ring"></div>
            <el-avatar
              :size="80"
              :src="authStore.admin?.avatar"
              icon="UserFilled"
              class="hero-avatar"
            />
          </div>
          <div class="hero-text">
            <h1 class="hero-name">{{ authStore.admin?.nickname || '博主' }}</h1>
            <p class="hero-signature">
              在代码与文字之间，记录思考的轨迹。这里是我的数字花园，每一篇文章都是一次安静的对话。
            </p>
          </div>
          <div class="hero-stats">
            <div class="stat-card">
              <span class="stat-value">{{ total }}</span>
              <span class="stat-desc">篇文章</span>
            </div>
            <div class="stat-card">
              <span class="stat-value">{{ categoryCount }}</span>
              <span class="stat-desc">个分类</span>
            </div>
            <div class="stat-card">
              <span class="stat-value">{{ tagCount }}</span>
              <span class="stat-desc">个标签</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Article List -->
      <section class="articles-section">
        <div class="section-header">
          <h2 class="section-heading">
            <span class="heading-deco"></span>
            最新文章
          </h2>
          <span class="section-count">共 {{ total }} 篇</span>
        </div>

        <div v-if="articles.length" class="article-list stagger-list">
          <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
        </div>

        <div v-else class="empty-state">
          <svg width="64" height="64" viewBox="0 0 64 64" fill="none" class="empty-icon">
            <rect x="8" y="8" width="48" height="48" rx="12" stroke="currentColor" stroke-width="2"/>
            <path d="M24 24h16M24 32h12M24 40h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <p>还没有文章，期待第一篇分享</p>
        </div>

        <Pagination :total="total" :page="page" :size="10" @change="handlePageChange" />
      </section>
    </div>

    <Sidebar />
  </div>
</template>

<style scoped>
.home-page {
  display: flex;
  gap: var(--space-8);
  animation: fadeInUp 0.5s ease-out;
  align-items: flex-start;
}

.home-main {
  flex: 1;
  min-width: 0;
}

/* ═══════════════════════════════════════════
   Hero Banner
   ═══════════════════════════════════════════ */
.hero-banner {
  position: relative;
  background: var(--gradient-warm);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-xl);
  padding: var(--space-10) var(--space-10);
  margin-bottom: var(--space-8);
  overflow: hidden;
  box-shadow: var(--shadow-md);
}

.hero-bg-pattern {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 0% 0%, rgba(212, 168, 83, 0.06) 0%, transparent 50%),
    radial-gradient(ellipse at 100% 100%, rgba(180, 83, 60, 0.04) 0%, transparent 40%);
  pointer-events: none;
}

.hero-bg-pattern::after {
  content: '';
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg width='40' height='40' viewBox='0 0 40 40' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='%23a89060' fill-opacity='0.03'%3E%3Cpath d='M0 0h20v20H0zM20 20h20v20H20z'/%3E%3C/g%3E%3C/svg%3E");
  pointer-events: none;
}

.hero-body {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: var(--space-10);
}

/* Avatar with breathing amber ring */
.hero-avatar-wrap {
  position: relative;
  flex-shrink: 0;
}

.avatar-ring {
  position: absolute;
  inset: -6px;
  border-radius: 50%;
  border: 2px solid rgba(212, 168, 83, 0.18);
  animation: warmBreath 4s ease-in-out infinite;
}

.hero-avatar {
  border: 3px solid rgba(212, 168, 83, 0.25);
}

/* Name & Signature */
.hero-text {
  flex: 1;
  min-width: 0;
}

.hero-name {
  font-family: var(--font-serif);
  font-size: var(--text-3xl);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 var(--space-2);
  letter-spacing: -0.03em;
}

.hero-signature {
  color: var(--text-secondary);
  font-size: var(--text-md);
  line-height: 1.7;
  margin: 0;
}

/* Stats cards */
.hero-stats {
  display: flex;
  gap: var(--space-4);
  flex-shrink: 0;
}

.stat-card {
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--space-4) var(--space-6);
  text-align: center;
  box-shadow: var(--shadow-sm);
  min-width: 96px;
  transition: all var(--transition-normal);
}

.stat-card:hover {
  border-color: var(--border-accent);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-value {
  display: block;
  font-family: var(--font-serif);
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--color-amber-600);
  line-height: 1.2;
}

.stat-desc {
  display: block;
  font-size: 12px;
  color: var(--text-tertiary);
  font-weight: 500;
  margin-top: 2px;
}

/* ═══════════════════════════════════════════
   Article Section
   ═══════════════════════════════════════════ */
.articles-section {
  /* container for articles + pagination */
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-6);
}

.section-heading {
  font-family: var(--font-serif);
  font-size: var(--text-xl);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.heading-deco {
  display: inline-block;
  width: 3px;
  height: 20px;
  background: linear-gradient(180deg, var(--color-amber-400), var(--color-amber-600));
  border-radius: 2px;
  flex-shrink: 0;
}

.section-count {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  font-weight: 500;
}

/* Staggered list animation */
.article-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

/* Empty state */
.empty-state {
  text-align: center;
  padding: var(--space-12) 0;
  color: var(--text-tertiary);
  font-size: var(--text-base);
  background: var(--bg-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-default);

  .empty-icon {
    margin: 0 auto var(--space-4);
    display: block;
    color: var(--border-default);
  }

  p {
    margin: 0;
  }
}

/* ═══════════════════════════════════════════
   Responsive
   ═══════════════════════════════════════════ */
@media (max-width: 1200px) {
  .hero-body {
    gap: var(--space-6);
  }

  .hero-stats {
    gap: var(--space-3);
  }

  .stat-card {
    min-width: 80px;
    padding: var(--space-3) var(--space-4);
  }
}

@media (max-width: 768px) {
  .home-page {
    flex-direction: column;
  }

  .hero-banner {
    padding: var(--space-6);
    border-radius: var(--radius-lg);
  }

  .hero-body {
    flex-direction: column;
    text-align: center;
    gap: var(--space-5);
  }

  .hero-stats {
    flex-direction: row;
    width: 100%;
    justify-content: center;
  }

  .hero-name {
    font-size: var(--text-2xl);
  }

  .hero-signature {
    font-size: var(--text-sm);
  }

  .stat-card {
    min-width: 70px;
    flex: 1;
  }

  .stat-value {
    font-size: var(--text-xl);
  }
}
</style>
