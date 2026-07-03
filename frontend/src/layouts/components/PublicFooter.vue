<script setup>
import { useSiteStore } from '@/stores/site'
const siteStore = useSiteStore()
</script>

<template>
  <footer class="public-footer">
    <div class="footer-inner">
      <div class="footer-brand">
        <svg width="20" height="20" viewBox="0 0 28 28" fill="none">
          <rect width="28" height="28" rx="8" fill="url(#f-logo)"/>
          <path d="M8 20V8l6 6-6 6zM14 20V8l6 6-6 6z" fill="white" opacity="0.9"/>
          <defs>
            <linearGradient id="f-logo" x1="0" y1="0" x2="28" y2="28">
              <stop stop-color="#6366F1"/>
              <stop offset="1" stop-color="#059669"/>
            </linearGradient>
          </defs>
        </svg>
        <span>{{ siteStore.config.site_name || '个人博客' }}</span>
      </div>
      <p class="footer-copy">{{ siteStore.config.copyright || '专门服务于自己' }}</p>
    </div>
  </footer>
</template>

<style scoped>
.public-footer {
  background: linear-gradient(180deg, var(--bg-warm), var(--bg-page));
  border-top: none;
  padding: var(--space-10) var(--space-6);
  position: relative;

  /* 顶部金色渐变装饰线 */
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg,
      transparent 0%,
      rgba(212, 168, 83, 0.20) 15%,
      rgba(212, 168, 83, 0.50) 50%,
      rgba(212, 168, 83, 0.20) 85%,
      transparent 100%
    );
  }
}

.footer-inner {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
}

.footer-brand {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-family: var(--font-serif);
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-secondary);
  letter-spacing: 0.01em;
}

.footer-copy {
  margin: 0;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  letter-spacing: 0.04em;
  font-variant-numeric: tabular-nums;
}
</style>
