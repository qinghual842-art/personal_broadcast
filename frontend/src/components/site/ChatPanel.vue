<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { chatWithAgent, getAgents, getChatHistory } from '@/api/agent'
import { useUserStore } from '@/stores/user'
import AgentAvatar from '@/components/common/AgentAvatar.vue'
import { renderMarkdown } from '@/utils/markdown'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const agents = ref([])
const agent = ref(null)
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const sessionId = ref('')
const messagesContainer = ref(null)

const agentId = computed(() => agent.value?.id || null)

function sessionKey(aid) {
  const uid = userStore.user?.id || 'anon'
  return `agent_session_${aid}_${uid}`
}

function loadAgentHistory(a) {
  agent.value = a
  messages.value = []
  sessionId.value = ''
  if (userStore.isLoggedIn && a) {
    const saved = sessionStorage.getItem(sessionKey(a.id))
    if (saved) {
      sessionId.value = saved
      getChatHistory(a.id, saved).then(res => {
        if (agent.value?.id === a.id) {
          messages.value = res.data || []
        }
      }).catch(() => {})
    }
  }
}

function switchAgent(a) {
  if (agent.value?.id === a.id) return
  loadAgentHistory(a)
}

onMounted(async () => {
  try {
    const res = await getAgents()
    agents.value = res.data || []
    if (agents.value.length > 0) {
      loadAgentHistory(agents.value[0])
    }
  } catch (e) { /* ignore */ }
})

// Reload agents when login state changes
watch(() => userStore.isLoggedIn, (loggedIn) => {
  if (loggedIn && agents.value.length > 0 && agent.value) {
    loadAgentHistory(agent.value)
  }
  if (!loggedIn) {
    messages.value = []
    sessionId.value = ''
  }
})

async function send() {
  const text = inputText.value.trim()
  if (!text || sending.value || !agentId.value || !userStore.isLoggedIn) return
  inputText.value = ''
  messages.value.push({ role: 'user', content: text })
  sending.value = true
  await scrollToBottom()
  try {
    const res = await chatWithAgent(agentId.value, { sessionId: sessionId.value, message: text })
    sessionId.value = res.data.sessionId
    sessionStorage.setItem(sessionKey(agentId.value), sessionId.value)
    messages.value.push({ role: 'assistant', content: res.data.reply })
  } catch (e) {
    ElMessage.error('回复失败，请稍后重试')
  } finally {
    sending.value = false
    await scrollToBottom()
  }
}

async function scrollToBottom() {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}
</script>

<template>
  <aside class="chat-panel">
    <div class="chat-header">
      <div class="chat-header-info">
        <AgentAvatar :name="agent?.name" :size="32" />
        <div class="chat-header-select">
          <span class="chat-header-name">{{ agent?.name || 'AI 智能助手' }}</span>
          <el-dropdown v-if="userStore.isLoggedIn && agents.length > 1" trigger="click" @command="switchAgent">
            <span class="chat-dropdown-trigger">
              <span class="chat-header-desc">{{ agent?.description || '切换智能体' }}</span>
              <el-icon :size="12"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-for="a in agents"
                  :key="a.id"
                  :command="a"
                  :class="{ active: agent?.id === a.id }"
                >
                  <span class="agent-option">
                    <AgentAvatar :name="a.name" :size="20" />
                    <span class="agent-option-name">{{ a.name }}</span>
                  </span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <span v-else class="chat-header-desc">{{ agent?.description || '随时为您解答' }}</span>
        </div>
      </div>
    </div>

    <div class="chat-messages" ref="messagesContainer">
      <div v-if="!messages.length && !sending" class="chat-welcome">
        <div class="welcome-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
            <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
          </svg>
        </div>
        <p class="welcome-text">开始一段对话</p>
        <p class="welcome-hint">向 AI 助手提问，获取智能解答</p>
      </div>

      <div v-for="(msg, i) in messages" :key="i" :class="['message-row', msg.role]">
        <div class="message-avatar">
          <el-avatar v-if="msg.role === 'user'" :size="26" icon="UserFilled" />
          <AgentAvatar v-else :name="agent?.name" :size="26" />
        </div>
        <div class="message-bubble">
          <div class="bubble-content" v-html="renderMarkdown(msg.content)" />
        </div>
      </div>

      <div v-if="sending" class="message-row assistant">
        <div class="message-avatar">
          <AgentAvatar :name="agent?.name" :size="26" />
        </div>
        <div class="message-bubble thinking-bubble">
          <span class="dot-pulse"></span>
        </div>
      </div>
    </div>

    <div class="chat-input-area" v-if="userStore.isLoggedIn">
      <div class="input-row">
        <el-input
          v-model="inputText"
          placeholder="输入你的问题..."
          @keyup.enter.exact="send"
          :disabled="sending"
          class="chat-textarea"
        />
        <button
          class="send-btn"
          :class="{ active: inputText.trim() }"
          :disabled="!inputText.trim() || sending"
          @click="send"
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
          </svg>
        </button>
      </div>
    </div>
    <div class="chat-login-hint" v-else>
      <p>登录后即可使用 AI 助手</p>
      <el-button type="primary" size="small" @click="router.push('/login')">去登录</el-button>
    </div>
  </aside>
</template>

<style scoped>
.chat-panel {
  width: 340px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 160px);
  position: sticky;
  top: 80px;
  align-self: flex-start;
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-md);
}

.chat-header {
  padding: var(--space-3) var(--space-4);
  border-bottom: 1px solid var(--border-default);
  background: var(--bg-warm);
  flex-shrink: 0;
}

.chat-header-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.chat-header-select {
  flex: 1;
  min-width: 0;
}

.chat-header-name {
  display: block;
  font-size: var(--text-sm);
  font-weight: 700;
  color: var(--text-primary);
  font-family: var(--font-serif);
}

.chat-header-desc {
  display: block;
  font-size: 11px;
  color: var(--text-tertiary);
  margin-top: 1px;
}

.chat-dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  transition: background var(--transition-fast);
  margin: -2px -6px;
}

.chat-dropdown-trigger:hover {
  background: rgba(212, 168, 83, 0.08);
}

.chat-dropdown-trigger .el-icon {
  color: var(--text-tertiary);
  transition: transform var(--transition-fast);
}

.chat-dropdown-trigger:hover .el-icon {
  color: var(--color-amber-500);
}

.agent-option {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.agent-option-name {
  font-size: var(--text-sm);
}

:deep(.el-dropdown-menu__item.active) {
  color: var(--color-amber-600);
  font-weight: 600;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-4);
  background: var(--bg-page);
}

.chat-welcome {
  text-align: center;
  padding: 40px var(--space-4);

  .welcome-icon {
    margin-bottom: var(--space-3);
    display: inline-flex;
    padding: var(--space-3);
    background: var(--color-amber-50);
    border-radius: 50%;
    color: var(--color-amber-500);
  }
}

.welcome-text {
  font-size: var(--text-base);
  color: var(--text-primary);
  margin: 0 0 var(--space-1);
  font-weight: 600;
}

.welcome-hint {
  font-size: 12px;
  color: var(--text-tertiary);
  margin: 0;
}

.message-row {
  display: flex;
  gap: var(--space-2);
  margin-bottom: var(--space-4);
  align-items: flex-start;
  animation: fadeInUp 0.3s ease-out;
}

.message-row.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
  margin-top: 2px;
}

.message-bubble {
  max-width: 75%;
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-lg);
  line-height: 1.6;
  font-size: 13px;
  position: relative;
}

.message-row.user .message-bubble {
  background: linear-gradient(135deg, var(--color-amber-500), var(--color-amber-600));
  color: #fff;
  border-bottom-right-radius: var(--radius-sm);
}

.message-row.assistant .message-bubble {
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  color: var(--text-secondary);
  border-bottom-left-radius: var(--radius-sm);
}

.message-bubble :deep(p) {
  margin: 0 0 var(--space-1);
  &:last-child { margin-bottom: 0; }
}

.message-bubble :deep(pre) {
  background: var(--bg-page);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-md);
  overflow-x: auto;
  font-size: 12px;
  margin: var(--space-1) 0;
  border: 1px solid var(--border-light);
}

.message-bubble :deep(code) {
  font-size: 12px;
  font-family: var(--font-mono);
}

.message-bubble :deep(p code) {
  background: var(--bg-accent-light);
  padding: 1px 5px;
  border-radius: var(--radius-sm);
  color: var(--color-amber-600);
}

.thinking-bubble {
  padding: var(--space-3) var(--space-4) !important;
  display: flex;
  align-items: center;
  gap: 5px;
}

.dot-pulse {
  width: 5px;
  height: 5px;
  background: var(--color-amber-400);
  border-radius: 50%;
  animation: dotPulse 1.2s ease-in-out infinite;
}

@keyframes dotPulse {
  0%, 100% { opacity: 0.3; transform: scale(0.8); }
  50% { opacity: 1; transform: scale(1.2); }
}

.chat-input-area {
  padding: var(--space-3) var(--space-4);
  border-top: 1px solid var(--border-default);
  flex-shrink: 0;
  background: var(--bg-surface);
}

.input-row {
  display: flex;
  gap: var(--space-2);
  align-items: flex-end;
}

.chat-textarea :deep(.el-input__wrapper) {
  background: var(--bg-page);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  box-shadow: none;
  padding: var(--space-2) var(--space-3);
  height: 40px;

  &:hover {
    border-color: var(--color-amber-300);
  }

  &.is-focus {
    border-color: var(--color-amber-400);
    box-shadow: 0 0 0 3px rgba(217, 168, 83, 0.08);
  }
}

.send-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: var(--radius-lg);
  background: var(--bg-subtle);
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
  flex-shrink: 0;

  &.active {
    background: linear-gradient(135deg, var(--color-amber-500), var(--color-amber-600));
    color: #fff;
    box-shadow: 0 4px 12px rgba(217, 168, 83, 0.3);

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 6px 20px rgba(217, 168, 83, 0.4);
    }
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
}

.chat-login-hint {
  padding: var(--space-5);
  border-top: 1px solid var(--border-default);
  text-align: center;
  flex-shrink: 0;
  background: var(--bg-surface);
}

.chat-login-hint p {
  margin: 0 0 var(--space-3);
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

@media (max-width: 1400px) {
  .chat-panel {
    width: 300px;
  }
}

@media (max-width: 1200px) {
  .chat-panel {
    display: none;
  }
}
</style>
