<script setup>
import { useSiteStore } from '@/stores/site'
import { onMounted } from 'vue'
import PublicHeader from './components/PublicHeader.vue'
import PublicFooter from './components/PublicFooter.vue'

const siteStore = useSiteStore()

onMounted(() => {
  siteStore.fetchConfig()
})
</script>

<template>
  <div class="public-layout">
    <PublicHeader />
    <main class="main-content">
      <div class="container">
        <router-view />
      </div>
    </main>
    <PublicFooter />
  </div>
</template>

<style scoped>
.public-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-page);
  position: relative;

  &::before {
    content: '';
    position: fixed;
    inset: 0;
    background-image:
      radial-gradient(ellipse at 20% 50%, rgba(212, 168, 83, 0.03) 0%, transparent 50%),
      radial-gradient(ellipse at 80% 20%, rgba(61, 90, 150, 0.02) 0%, transparent 50%);
    pointer-events: none;
    z-index: 0;
  }
}

.main-content {
  flex: 1;
  padding: var(--space-6) 0;
  position: relative;
  z-index: 1;

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 60px;
    background: linear-gradient(180deg, transparent, rgba(212, 168, 83, 0.02));
    pointer-events: none;
  }
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--space-8);
  width: 100%;
}

@media (max-width: 1200px) {
  .container {
    max-width: 100%;
    padding: 0 var(--space-6);
  }
}

@media (max-width: 768px) {
  .container { padding: 0 var(--space-4); }
  .main-content { padding: var(--space-5) 0; }
}
</style>
