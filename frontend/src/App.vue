<script setup>
// No logic needed - root component is a pure routing container
</script>

<template>
  <!-- Route transitions for smooth page switching -->
  <router-view v-slot="{ Component, route }">
    <transition name="page-fade" mode="out-in">
      <component :is="Component" :key="route.path" />
    </transition>
  </router-view>
</template>

<style>
/* ─── Global Reset ─────────────────────────────────────────── */
html, body, #app {
  margin: 0;
  padding: 0;
  height: 100%;
}

/*
  Font stack — Midnight Ink 墨夜书房:
  Playfair Display (serif titles), DM Sans (body), Noto Sans SC (CJK).
  For production, host fonts locally in /public/fonts/ and use @font-face.
*/
body {
  font-family:
    'DM Sans', 'Noto Sans SC',
    -apple-system, BlinkMacSystemFont,
    'SF Pro Display', 'Segoe UI', 'Roboto',
    'PingFang SC', 'Microsoft YaHei',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* ─── Route Transition — 墨水翻页效果 ───────────────────── */
.page-fade-enter-active {
  transition:
    opacity 0.4s cubic-bezier(0.65, 0, 0.35, 1),
    transform 0.4s cubic-bezier(0.65, 0, 0.35, 1),
    filter 0.35s ease-out;
}

.page-fade-leave-active {
  transition:
    opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    filter 0.25s ease-in;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(16px) scale(0.98);
  filter: blur(4px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.99);
  filter: blur(2px);
}
</style>
