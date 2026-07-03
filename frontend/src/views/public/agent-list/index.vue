<script setup>
import { ref, onMounted } from 'vue'
import { getAgents } from '@/api/agent'
import { useRouter } from 'vue-router'

const agents = ref([])
const router = useRouter()

onMounted(async () => {
  const res = await getAgents()
  agents.value = res.data
})

function goChat(agent) {
  router.push(`/agents/${agent.id}/chat`)
}
</script>

<template>
  <div class="agent-list-page">
    <div class="page-header">
      <h2>AI 智能助手</h2>
      <p>选择一个智能体，开启对话探索</p>
    </div>

    <div class="agent-grid" v-if="agents.length">
      <div class="agent-card" v-for="agent in agents" :key="agent.id" @click="goChat(agent)">
        <div class="agent-avatar">
          <el-avatar :size="56" :src="agent.avatar">
            <el-icon :size="28"><Cpu /></el-icon>
          </el-avatar>
          <div class="avatar-ring"></div>
        </div>
        <h3>{{ agent.name }}</h3>
        <p>{{ agent.description || '点击开始对话' }}</p>
        <div class="agent-footer">
          <span class="provider-tag" v-if="agent.provider">{{ agent.provider }}</span>
          <span class="enter-hint">
            开始对话
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
              <path d="M5 12h14M12 5l7 7-7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </span>
        </div>
      </div>
    </div>

    <el-empty v-else description="暂无可用智能体" />
  </div>
</template>

<style scoped>
.agent-list-page {
  max-width: 1200px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease-out;
}

.page-header {
  text-align: center;
  margin-bottom: var(--space-10);

  h2 {
    font-size: var(--text-3xl);
    font-weight: 700;
    font-family: var(--font-serif);
    color: var(--text-primary);
    margin: 0 0 var(--space-2);
    letter-spacing: -0.02em;
  }

  p {
    margin: 0;
    color: var(--text-tertiary);
    font-size: var(--text-base);
  }
}

.agent-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--space-6);
}

.agent-card {
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  padding: var(--space-8) var(--space-6);
  text-align: center;
  cursor: pointer;
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-card);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, var(--color-amber-300), var(--color-amber-500), var(--color-amber-300));
    opacity: 0;
    transition: opacity var(--transition-normal);
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
    border-color: var(--color-amber-200);

    &::before {
      opacity: 1;
    }

    .avatar-ring {
      border-color: var(--color-amber-400);
      transform: scale(1.1);
    }
  }
}

.agent-avatar {
  position: relative;
  display: inline-block;
  margin-bottom: var(--space-4);
}

.avatar-ring {
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  border: 2px solid var(--border-default);
  transition: all var(--transition-normal);
}

.agent-card h3 {
  margin: 0 0 var(--space-2);
  font-size: var(--text-lg);
  font-family: var(--font-serif);
  color: var(--text-primary);
  font-weight: 600;
}

.agent-card p {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  margin: 0 0 var(--space-5);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.agent-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--space-4);
  border-top: 1px solid var(--border-light);
}

.provider-tag {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-tertiary);
  background: var(--bg-subtle);
  padding: 2px 8px;
  border-radius: var(--radius-sm);
}

.enter-hint {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--color-amber-500);
  font-weight: 600;
  opacity: 0;
  transform: translateX(-4px);
  transition: all var(--transition-normal);
}

.agent-card:hover .enter-hint {
  opacity: 1;
  transform: translateX(0);
}
</style>
