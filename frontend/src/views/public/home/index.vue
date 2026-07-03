<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getArticles } from '@/api/article'
import { getCategories } from '@/api/category'
import { getTags } from '@/api/tag'
import { getOwnerInfo } from '@/api/site'
import ArticleCard from '@/components/article/ArticleCard.vue'
import Sidebar from '@/components/site/Sidebar.vue'
import ChatPanel from '@/components/site/ChatPanel.vue'
import Pagination from '@/components/common/Pagination.vue'

const DEFAULT_AVATAR = 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif'

const router = useRouter()
const articles = ref([])
const total = ref(0)
const page = ref(1)
const categoryCount = ref(0)
const tagCount = ref(0)
const owner = ref({ nickname: '博主', avatar: DEFAULT_AVATAR })

async function fetchArticles() {
  const res = await getArticles({ page: page.value, size: 10 })
  articles.value = res.data.records
  total.value = res.data.total
}

async function fetchStats() {
  const [catRes, tagRes, ownerRes] = await Promise.all([
    getCategories(),
    getTags(),
    getOwnerInfo().catch(() => ({ data: null }))
  ])
  categoryCount.value = catRes.data?.length || 0
  tagCount.value = tagRes.data?.length || 0
  if (ownerRes.data) {
    owner.value = ownerRes.data
  }
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
    <Sidebar />

    <div class="home-main">
      <!-- Hero Banner -->
      <section class="hero-banner">
        <div class="hero-bg-pattern"></div>
        <div class="hero-body">
          <div class="hero-left">
            <div class="hero-avatar-wrap">
              <div class="avatar-ring"></div>
              <el-avatar
                :size="72"
                :src="owner.avatar || DEFAULT_AVATAR"
                class="hero-avatar"
              />
            </div>
          </div>
          <div class="hero-center">
            <h1 class="hero-name">{{ owner.nickname || '博主' }}</h1>
            <p class="hero-signature">
              在代码与文字之间，记录思考的轨迹。这里是我的数字花园，每一篇文章都是一次安静的对话。
            </p>
          </div>
          <div class="hero-right">
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

    <div class="right-column">
      <div class="ai-entry-card" @click="router.push('/agents')">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" class="ai-spark-icon">
          <circle cx="12" cy="12" r="3" fill="currentColor" opacity="0.3"/>
          <path d="M12 2v4M12 18v4M2 12h4M18 12h4M5.64 5.64l2.83 2.83M15.54 15.54l2.83 2.83M5.64 18.36l2.83-2.83M15.54 8.46l2.83-2.83" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <div class="ai-entry-text">
          <span class="ai-entry-title">AI 智能助手</span>
          <span class="ai-entry-desc">与智能体对话，探索无限可能</span>
        </div>
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" class="ai-entry-arrow">
          <path d="M8 5l7 7-7 7" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>

      <ChatPanel />
    </div>
  </div>
</template>

<style scoped>
.home-page {
  display: flex;
  gap: var(--space-8);
  animation: fadeInUp 0.5s ease-out;
  align-items: flex-start;
  width: 100vw;
  position: relative;
  left: 50%;
  right: 50%;
  margin-left: -50vw;
  margin-right: -50vw;
  padding: 0 var(--space-8);
  box-sizing: border-box;
}

.home-main {
  flex: 1;
  min-width: 0;
}

.right-column {
  width: 340px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
  position: sticky;
  top: 80px;
  align-self: flex-start;
}

/* ═══ AI Entry Card ═══ */
.ai-entry-card {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--space-5) var(--space-6);
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;

  &:hover {
    border-color: var(--border-accent);
    box-shadow: var(--shadow-md);
    transform: translateY(-2px);
  }
}

.ai-spark-icon {
  flex-shrink: 0;
  color: var(--color-amber-500);
}

.ai-entry-text {
  flex: 1;
  min-width: 0;
}

.ai-entry-title {
  display: block;
  font-size: var(--text-sm);
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.ai-entry-desc {
  display: block;
  font-size: 12px;
  color: var(--text-tertiary);
}

.ai-entry-arrow {
  flex-shrink: 0;
  color: var(--color-amber-500);
  transition: transform var(--transition-fast);
}

.ai-entry-card:hover .ai-entry-arrow {
  transform: translateX(3px);
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

.hero-left {
  flex-shrink: 0;
}

.hero-avatar-wrap {
  position: relative;
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

.hero-center {
  flex: 1;
  min-width: 0;
}

.hero-name {
  font-family: var(--font-serif);
  font-size: var(--text-3xl);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 var(--space-3);
  letter-spacing: -0.03em;
}

.hero-signature {
  color: var(--text-secondary);
  font-size: var(--text-md);
  line-height: 1.7;
  margin: 0;
}

.hero-right {
  flex-shrink: 0;
}

.hero-stats {
  display: flex;
  gap: var(--space-4);
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
  background: var(--bg-surface);
  border-radius: var(--radius-xl);
  padding: var(--space-6) var(--space-8);
  border: 1px solid var(--border-default);
}
  /* container for articles + pagination */


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

.article-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

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
@media (max-width: 1400px) {
  .right-column {
    width: 300px;
  }
}

@media (max-width: 1200px) {
  .right-column {
    display: none;
  }

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

  .hero-name {
    font-size: var(--text-2xl);
  }

  .hero-signature {
    font-size: var(--text-sm);
  }

  .hero-stats {
    flex-direction: row;
    width: 100%;
    justify-content: center;
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
