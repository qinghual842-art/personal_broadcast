<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div class="not-found">
    <div class="nf-glow"></div>
    <span class="nf-code">404</span>
    <h1>页面不存在</h1>
    <p>你访问的页面可能已被移动或删除</p>
    <button class="nf-btn" @click="router.push('/')">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
        <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M9 22V12h6v10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      返回首页
    </button>
  </div>
</template>

<style scoped>
.not-found {
  text-align: center;
  padding: 100px var(--space-6);
  min-height: 60vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  animation: fadeInUp 0.6s ease-out;
  position: relative;
}

.nf-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(217, 168, 83, 0.08) 0%, transparent 70%);
  pointer-events: none;
}

.nf-code {
  font-size: 120px;
  font-weight: 800;
  background: linear-gradient(135deg, var(--color-amber-400), var(--color-primary-700));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
  letter-spacing: -6px;
  position: relative;
  z-index: 1;
}

.nf-code::after {
  content: '';
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  width: 60%;
  height: 4px;
  border-radius: 2px;
  background: linear-gradient(90deg, transparent, var(--color-amber-400), transparent);
}

h1 {
  font-size: var(--text-2xl);
  color: var(--text-primary);
  margin: var(--space-4) 0 var(--space-2);
  font-weight: 700;
  position: relative;
  z-index: 1;
}

p {
  color: var(--text-tertiary);
  font-size: var(--text-base);
  margin: 0 0 var(--space-8);
  position: relative;
  z-index: 1;
}

.nf-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: 12px 28px;
  border: none;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--color-amber-500), var(--color-amber-600));
  color: #fff;
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-normal);
  font-family: inherit;
  box-shadow: 0 4px 16px rgba(217, 168, 83, 0.3);
  position: relative;
  z-index: 1;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(217, 168, 83, 0.4);
  }
}
</style>
