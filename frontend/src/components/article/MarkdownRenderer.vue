<script setup>
import { computed } from 'vue'
import { renderMarkdown } from '@/utils/markdown'

const props = defineProps({
  content: { type: String, default: '' }
})

const html = computed(() => renderMarkdown(props.content))
</script>

<template>
  <div class="markdown-body" v-html="html" />
</template>

<style scoped>
.markdown-body {
  line-height: 1.85;
  color: var(--text-secondary);
  font-size: 16px;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3),
.markdown-body :deep(h4) {
  margin: var(--space-8) 0 var(--space-4);
  color: var(--text-primary);
  font-family: var(--font-display);
  font-weight: 700;
  letter-spacing: -0.02em;
}

.markdown-body :deep(h1) { font-size: 28px; }
.markdown-body :deep(h2) { font-size: 24px; padding-bottom: 8px; border-bottom: 1px solid var(--border-light); }
.markdown-body :deep(h3) { font-size: 20px; }

.markdown-body :deep(p) {
  margin: 0 0 var(--space-5);
  line-height: 1.85;
}

.markdown-body :deep(pre) {
  background: var(--bg-subtle);
  color: var(--text-primary);
  padding: var(--space-5);
  border-radius: var(--radius-lg);
  overflow-x: auto;
  border: 1px solid var(--border-default);
  margin: var(--space-5) 0;
  font-size: 14px;
}

.markdown-body :deep(code) {
  font-family: var(--font-mono);
  font-size: 13px;
}

.markdown-body :deep(p code) {
  background: var(--bg-accent-light);
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  color: var(--color-primary-700);
  font-weight: 500;
  border: 1px solid var(--border-accent);
}

.markdown-body :deep(blockquote) {
  border-left: 4px solid var(--color-primary-400);
  margin: var(--space-5) 0;
  padding: var(--space-4) var(--space-5);
  background: var(--bg-accent-light);
  border-radius: 0 var(--radius-md) var(--radius-md) 0;
  color: var(--text-secondary);
}

.markdown-body :deep(img) {
  max-width: 100%;
  border-radius: var(--radius-lg);
  margin: var(--space-5) 0;
  box-shadow: var(--shadow-md);
}

.markdown-body :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: var(--space-5) 0;
  border-radius: var(--radius-lg);
  overflow: hidden;
  border: 1px solid var(--border-default);
}

.markdown-body :deep(th),
.markdown-body :deep(td) {
  border: 1px solid var(--border-light);
  padding: var(--space-3) var(--space-4);
  text-align: left;
  color: var(--text-secondary);
}

.markdown-body :deep(th) {
  background: var(--bg-subtle);
  color: var(--text-primary);
  font-weight: 600;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  margin: var(--space-4) 0;
  padding-left: var(--space-6);
}

.markdown-body :deep(li) {
  margin: var(--space-2) 0;
}

.markdown-body :deep(a) {
  color: var(--color-primary-600);
  text-decoration: none;
  border-bottom: 1px solid var(--color-primary-200);
  transition: all var(--transition-fast);

  &:hover {
    border-bottom-color: var(--color-primary-500);
  }
}

.markdown-body :deep(hr) {
  border: none;
  height: 1px;
  background: var(--border-default);
  margin: var(--space-8) 0;
}

.markdown-body :deep(strong) {
  color: var(--text-primary);
  font-weight: 700;
}
</style>
