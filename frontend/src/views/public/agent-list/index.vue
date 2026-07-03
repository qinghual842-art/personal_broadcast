<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAgents, getUserAgents, deleteUserAgent } from '@/api/agent'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import AgentAvatar from '@/components/common/AgentAvatar.vue'

const agents = ref([])
const userAgents = ref([])
const router = useRouter()
const userStore = useUserStore()

const defaultAgents = computed(() => agents.value.filter(a => !a.userId))
const hasUserAgents = computed(() => userAgents.value.length > 0)

onMounted(async () => {
  const [res] = await Promise.all([
    getAgents().catch(() => ({ data: [] }))
  ])
  agents.value = res.data
  if (userStore.isLoggedIn) {
    fetchUserAgents()
  }
})

async function fetchUserAgents() {
  try {
    const res = await getUserAgents()
    userAgents.value = res.data || []
  } catch { /* ignore */ }
}

function goChat(agent) {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  router.push(`/agents/${agent.id}/chat`)
}

function goCreate() {
  router.push('/my-agents/create')
}

function goEdit(id) {
  router.push(`/my-agents/create?id=${id}`)
}

async function handleDelete(agent) {
  try {
    await ElMessageBox.confirm(`确定删除「${agent.name}」？`, '提示', { type: 'warning' })
    await deleteUserAgent(agent.id)
    userAgents.value = userAgents.value.filter(a => a.id !== agent.id)
    ElMessage.success('已删除')
  } catch { /* cancelled */ }
}
</script>

<template>
  <div class="agent-list-page">
    <div class="page-header">
      <h2>AI 智能助手</h2>
      <p>选择一个智能体，开启对话探索</p>
    </div>

    <!-- System Default Agents -->
    <div class="section" v-if="defaultAgents.length">
      <div class="section-head">
        <h3>系统智能体</h3>
        <span class="section-badge">官方</span>
      </div>
      <div class="agent-grid">
        <div class="agent-card" v-for="agent in defaultAgents" :key="agent.id" @click="goChat(agent)">
          <div class="agent-avatar">
            <AgentAvatar :name="agent.name" :size="56" />
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
    </div>

    <!-- User Agents -->
    <div class="section" v-if="userStore.isLoggedIn">
      <div class="section-head">
        <h3>我的智能体</h3>
        <el-button size="small" type="primary" :icon="Plus" @click="goCreate">新建</el-button>
      </div>

      <div class="agent-grid" v-if="hasUserAgents">
        <div class="agent-card user-agent" v-for="agent in userAgents" :key="agent.id">
          <div class="agent-avatar" @click="goChat(agent)">
            <AgentAvatar :name="agent.name" :size="56" />
            <div class="avatar-ring"></div>
          </div>
          <h3 @click="goChat(agent)">{{ agent.name }}</h3>
          <p @click="goChat(agent)">{{ agent.description || '点击开始对话' }}</p>
          <div class="agent-footer">
            <span class="provider-tag" v-if="agent.provider">{{ agent.provider }}</span>
            <div class="user-agent-actions">
              <el-button size="small" @click="goEdit(agent.id)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(agent)">删除</el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-hint">
        <p>还没有自己的智能体</p>
        <el-button type="primary" :icon="Plus" @click="goCreate">创建我的第一个智能体</el-button>
      </div>
    </div>

    <div class="login-hint" v-else>
      <p>登录后即可创建自己的智能体</p>
      <el-button type="primary" @click="router.push('/login')">去登录</el-button>
    </div>
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

.section {
  margin-bottom: var(--space-10);
}

.section-head {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-5);

  h3 {
    font-family: var(--font-serif);
    font-size: var(--text-xl);
    font-weight: 700;
    color: var(--text-primary);
    margin: 0;
  }
}

.section-badge {
  font-size: 11px;
  font-weight: 600;
  background: linear-gradient(135deg, var(--color-amber-400), var(--color-amber-600));
  color: #fff;
  padding: 2px 8px;
  border-radius: 999px;
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

    &::before { opacity: 1; }

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

.user-agent-actions {
  display: flex;
  gap: var(--space-1);
}

.empty-hint, .login-hint {
  text-align: center;
  padding: var(--space-8);
  background: var(--bg-surface);
  border: 1px dashed var(--border-default);
  border-radius: var(--radius-lg);

  p {
    margin: 0 0 var(--space-4);
    color: var(--text-tertiary);
    font-size: var(--text-sm);
  }
}
</style>
