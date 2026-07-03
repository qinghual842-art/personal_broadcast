<script setup>
import { useSiteStore } from '@/stores/site'
import MarkdownRenderer from '@/components/article/MarkdownRenderer.vue'

const siteStore = useSiteStore()
</script>

<template>
  <div class="about-page">
    <div class="about-card">
      <h2 class="about-title">关于我</h2>
      <MarkdownRenderer :content="siteStore.config.about_me || '暂无关于信息'" />
    </div>
  </div>
</template>

<style scoped>
.about-page {
  max-width: 960px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease-out;
}

.about-card {
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  padding: var(--space-8);
  box-shadow: var(--shadow-card);
}

.about-title {
  font-size: var(--text-2xl);
  font-weight: 700;
  font-family: var(--font-display);
  color: var(--text-primary);
  margin: 0 0 var(--space-6);
  letter-spacing: -0.02em;
  padding-bottom: var(--space-4);
  border-bottom: 1px solid var(--border-light);
}
</style>
