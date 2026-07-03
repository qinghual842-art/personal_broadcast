<script setup>
import { computed } from 'vue'

const props = defineProps({
  name: { type: String, default: '' },
  size:  { type: Number, default: 48 }
})

const initial = computed(() => {
  if (!props.name) return '?'
  return props.name.trim().charAt(0)
})

const gradientColors = computed(() => {
  const pairs = [
    ['#6366f1', '#8b5cf6'],
    ['#0891b2', '#06b6d4'],
    ['#059669', '#10b981'],
    ['#d97706', '#f59e0b'],
    ['#dc2626', '#ef4444'],
    ['#7c3aed', '#a78bfa'],
    ['#0d9488', '#5eead4'],
    ['#9333ea', '#c084fc'],
  ]
  let hash = 0
  for (let i = 0; i < (props.name || '').length; i++) {
    hash = props.name.charCodeAt(i) + ((hash << 5) - hash)
  }
  return pairs[Math.abs(hash) % pairs.length]
})
</script>

<template>
  <div
    class="agent-avatar"
    :style="{
      width: size + 'px',
      height: size + 'px',
      background: `linear-gradient(135deg, ${gradientColors[0]}, ${gradientColors[1]})`
    }"
  >
    <span class="agent-avatar-text" :style="{ fontSize: (size * 0.42) + 'px' }">{{ initial }}</span>
  </div>
</template>

<style scoped>
.agent-avatar {
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}

.agent-avatar-text {
  color: #fff;
  font-weight: 700;
  line-height: 1;
  user-select: none;
}
</style>
