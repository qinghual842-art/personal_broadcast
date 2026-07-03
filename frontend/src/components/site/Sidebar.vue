<script setup>
import { ref, onMounted } from 'vue'
import { getCategories } from '@/api/category'
import { getTags } from '@/api/tag'
import { getHotArticles } from '@/api/article'
import { getOwnerInfo } from '@/api/site'
import { useRouter } from 'vue-router'

const DEFAULT_AVATAR = 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif'

const categories = ref([])
const tags = ref([])
const hotArticles = ref([])
const owner = ref({ nickname: '博主', avatar: DEFAULT_AVATAR })
const router = useRouter()

onMounted(async () => {
  const [catRes, tagRes, hotRes, ownerRes] = await Promise.all([
    getCategories(),
    getTags(),
    getHotArticles(5),
    getOwnerInfo().catch(() => ({ data: null }))
  ])
  categories.value = catRes.data
  tags.value = tagRes.data
  hotArticles.value = hotRes.data
  if (ownerRes.data) {
    owner.value = ownerRes.data
  }
})

function goArticle(id) {
  router.push(`/article/${id}`)
}

function goCategory(id) {
  router.push(`/category/${id}`)
}

function goTag(id) {
  router.push(`/tag/${id}`)
}
</script>

<template>
  <aside class="sidebar">
    <!-- Profile Widget -->
    <div class="widget profile-widget">
      <div class="profile-avatar">
        <el-avatar :size="64" :src="owner.avatar || DEFAULT_AVATAR" />
      </div>
      <div class="profile-info">
        <strong>{{ owner.nickname || '博主' }}</strong>
        <span>技术博客作者</span>
      </div>
      <div class="profile-stats">
        <div class="stat-item">
          <span class="stat-num">{{ hotArticles.length || 0 }}</span>
          <span class="stat-label">热门文章</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-num">{{ categories.length || 0 }}</span>
          <span class="stat-label">分类</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-num">{{ tags.length || 0 }}</span>
          <span class="stat-label">标签</span>
        </div>
      </div>
    </div>

    <!-- Hot Articles -->
    <div class="widget" v-if="hotArticles.length">
      <h4 class="widget-head">热门文章</h4>
      <div class="hot-list">
        <div
          v-for="(a, i) in hotArticles"
          :key="a.id"
          class="hot-item"
          @click="goArticle(a.id)"
        >
          <span class="hot-rank" :class="{ 'hot-rank--top': i < 3 }">{{ i + 1 }}</span>
          <span class="hot-title">{{ a.title }}</span>
        </div>
      </div>
    </div>

    <!-- Categories -->
    <div class="widget" v-if="categories.length">
      <h4 class="widget-head">文章分类</h4>
      <div class="cat-list">
        <div
          v-for="cat in categories"
          :key="cat.id"
          class="cat-row"
          @click="goCategory(cat.id)"
        >
          <span class="cat-name">{{ cat.name }}</span>
          <span class="cat-badge">{{ cat.articleCount }}</span>
        </div>
      </div>
    </div>

    <!-- Tag Cloud -->
    <div class="widget" v-if="tags.length">
      <h4 class="widget-head">热门标签</h4>
      <div class="tag-cloud">
        <span
          v-for="tag in tags"
          :key="tag.id"
          class="tag-pill"
          @click="goTag(tag.id)"
        >{{ tag.name }}</span>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 340px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
  position: sticky;
  top: 80px;
  align-self: flex-start;
}

/* Widget base */
.widget {
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
  transition: all var(--transition-normal);

  &:hover {
    border-color: var(--border-accent);
    box-shadow: var(--shadow-md);
  }
}

.widget-head {
  font-family: var(--font-serif);
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-secondary);
  margin: 0 0 var(--space-4);
  display: flex;
  align-items: center;
  gap: var(--space-2);

  &::before {
    content: '';
    width: 3px;
    height: 16px;
    background: linear-gradient(180deg, var(--color-amber-400), var(--color-amber-600));
    border-radius: 2px;
  }
}

/* ═══ Profile Widget ═══ */
.profile-widget {
  text-align: center;
  padding: var(--space-8) var(--space-6);
  background: linear-gradient(180deg, var(--bg-warm), var(--bg-surface));
  border-color: rgba(212, 168, 83, 0.15);
}

.profile-avatar {
  margin-bottom: var(--space-4);
}

.profile-avatar :deep(.el-avatar) {
  border: 3px solid rgba(212, 168, 83, 0.30);
  box-shadow: 0 0 0 6px rgba(212, 168, 83, 0.06);
}

.profile-info {
  margin-bottom: var(--space-5);

  strong {
    display: block;
    font-family: var(--font-serif);
    font-size: var(--text-lg);
    color: var(--text-primary);
    font-weight: 700;
    margin-bottom: var(--space-1);
  }

  span {
    font-size: var(--text-sm);
    color: var(--text-tertiary);
  }
}

.profile-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-4);
}

.stat-item {
  text-align: center;
  min-width: 56px;
}

.stat-num {
  display: block;
  font-family: var(--font-serif);
  font-size: var(--text-xl);
  font-weight: 700;
  color: var(--color-amber-600);
  line-height: 1.2;
}

.stat-label {
  font-size: 11px;
  color: var(--text-tertiary);
  font-weight: 500;
}

.stat-divider {
  width: 1px;
  height: 28px;
  background: var(--border-default);
}

/* ═══ Hot Articles ═══ */
.hot-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.hot-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);

  &:hover {
    background: rgba(212, 168, 83, 0.06);

    .hot-title {
      color: var(--color-amber-600);
    }
  }
}

.hot-rank {
  width: 22px;
  height: 22px;
  border-radius: var(--radius-sm);
  background: var(--bg-subtle);
  color: var(--text-tertiary);
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &--top {
    background: linear-gradient(135deg, var(--color-amber-400), var(--color-amber-600));
    color: #fff;
  }
}

.hot-title {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color var(--transition-fast);
  font-weight: 500;
}

/* ═══ Categories ═══ */
.cat-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.cat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-2) var(--space-3);
  cursor: pointer;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);

  &:hover {
    background: rgba(212, 168, 83, 0.06);

    .cat-name {
      color: var(--color-amber-600);
    }
  }
}

.cat-name {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-weight: 500;
  transition: color var(--transition-fast);
}

.cat-badge {
  font-size: 11px;
  color: var(--text-tertiary);
  background: var(--bg-subtle);
  padding: 2px 10px;
  border-radius: 12px;
  font-weight: 600;
  min-width: 24px;
  text-align: center;
}

/* ═══ Tag Cloud ═══ */
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.tag-pill {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  background: var(--bg-subtle);
  padding: 5px 12px;
  border-radius: 999px;
  cursor: pointer;
  transition: all var(--transition-fast);
  font-weight: 500;
  border: 1px solid transparent;
  user-select: none;

  &:hover {
    color: var(--color-amber-700);
    background: rgba(212, 168, 83, 0.08);
    border-color: rgba(212, 168, 83, 0.25);
    transform: translateY(-1px);
  }
}

/* ═══ Responsive ═══ */
@media (max-width: 1200px) {
  .sidebar {
    width: 300px;
  }
}

@media (max-width: 768px) {
  .sidebar {
    width: 100%;
    position: static;
  }
}
</style>
