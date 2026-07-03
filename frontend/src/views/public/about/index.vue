<script setup>
import { useSiteStore } from '@/stores/site'
import MarkdownRenderer from '@/components/article/MarkdownRenderer.vue'

const siteStore = useSiteStore()
</script>

<template>
  <div class="statement-page">
    <div class="statement-card">
      <h2 class="statement-title">博客声明</h2>
      <MarkdownRenderer :content="siteStore.config.about_me || '此博客服务于每一个热爱交流的技术选手。'" />
    </div>
  </div>
</template>

<style scoped>
.statement-page {
  max-width: 960px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease-out;
}

.statement-card {
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  padding: var(--space-8);
  box-shadow: var(--shadow-card);
}

.statement-title {
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
