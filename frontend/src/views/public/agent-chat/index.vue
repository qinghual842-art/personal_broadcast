<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { chatWithAgent, getAgent, getChatHistory, clearChatHistory } from '@/api/agent'
import AgentAvatar from '@/components/common/AgentAvatar.vue'
import { renderMarkdown } from '@/utils/markdown'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const agentId = computed(() => Number(route.params.id))
const agent = ref(null)
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const sessionId = ref('')
const messagesContainer = ref(null)

function sessionKey(agentId) {
  const uid = userStore.user?.id || 'anon'
  return `agent_session_${agentId}_${uid}`
}

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  const res = await getAgent(agentId.value)
  agent.value = res.data
  const saved = sessionStorage.getItem(sessionKey(agentId.value))
  if (saved) {
    sessionId.value = saved
    try {
      const history = await getChatHistory(agentId.value, sessionId.value)
      messages.value = history.data || []
    } catch (e) { /* ignore */ }
  }
})

async function send() {
  const text = inputText.value.trim()
  if (!text || sending.value) return
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

async function clearHistory() {
  try {
    await clearChatHistory(agentId.value, sessionId.value)
    messages.value = []
    sessionStorage.removeItem(`agent_session_${agentId.value}`)
    ElMessage.success('对话已清空')
  } catch (e) { /* ignore */ }
}

async function scrollToBottom() {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}
</script>

<template>
  <div class="chat-page">
    <!-- Header -->
    <div class="chat-topbar">
      <div class="chat-agent-info">
        <AgentAvatar :name="agent?.name" :size="40" />
        <div>
          <h3>{{ agent?.name || '智能助手' }}</h3>
          <p>{{ agent?.description || 'AI 对话中...' }}</p>
        </div>
      </div>
      <button class="clear-btn" @click="clearHistory">清空对话</button>
    </div>

    <!-- Messages -->
    <div class="chat-messages" ref="messagesContainer">
      <div v-if="!messages.length && !sending" class="chat-welcome">
        <div class="welcome-icon">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#4F46E5" stroke-width="1.5" stroke-linecap="round">
            <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
          </svg>
        </div>
        <p class="welcome-text">开始一段对话</p>
        <p class="welcome-hint">向 {{ agent?.name || 'AI 助手' }} 提问，获取智能解答</p>
      </div>

      <div v-for="(msg, i) in messages" :key="i" :class="['message-row', msg.role]">
        <div class="message-bubble">
          <div class="bubble-content" v-html="renderMarkdown(msg.content)" />
        </div>
        <div class="message-avatar">
          <el-avatar v-if="msg.role === 'user'" :size="30" icon="UserFilled" />
          <AgentAvatar v-else :name="agent?.name" :size="30" />
        </div>
      </div>

      <div v-if="sending" class="message-row assistant">
        <div class="message-bubble thinking-bubble">
          <span class="dot-pulse"></span>
        </div>
      </div>
    </div>

    <!-- Input -->
    <div class="chat-input-area">
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
          <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-page {
  max-width: 960px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  animation: fadeInUp 0.5s ease-out;
}

/* Top bar */
.chat-topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) var(--space-6);
  border-bottom: 1px solid var(--border-default);
  flex-shrink: 0;
  background: var(--bg-warm);
}

.chat-agent-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);

  h3 {
    margin: 0;
    font-size: var(--text-base);
    font-family: var(--font-display);
    color: var(--text-primary);
    font-weight: 600;
  }

  p {
    margin: 1px 0 0;
    font-size: 12px;
    color: var(--text-tertiary);
  }
}

.clear-btn {
  padding: var(--space-2) var(--space-4);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  background: var(--bg-surface);
  font-size: 12px;
  color: var(--text-tertiary);
  cursor: pointer;
  font-family: inherit;
  transition: all var(--transition-fast);

  &:hover {
    color: var(--color-danger);
    border-color: rgba(239, 68, 68, 0.3);
    background: var(--bg-subtle);
  }
}

/* Messages */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-6);
  background: var(--bg-page);
}

.chat-welcome {
  text-align: center;
  padding: 60px var(--space-5);

  .welcome-icon {
    margin-bottom: var(--space-4);
    display: inline-flex;
    padding: var(--space-4);
    background: var(--color-amber-50);
    border-radius: 50%;
  }
}

.welcome-text {
  font-size: var(--text-lg);
  color: var(--text-primary);
  margin: 0 0 var(--space-2);
  font-weight: 600;
}

.welcome-hint {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  margin: 0;
}

.message-row {
  display: flex;
  gap: var(--space-3);
  margin-bottom: var(--space-5);
  align-items: flex-start;
  animation: fadeInUp 0.3s ease-out;
}

.message-row.user {
  flex-direction: row-reverse;
}

.message-bubble {
  max-width: 70%;
  padding: var(--space-3) var(--space-5);
  border-radius: var(--radius-lg);
  line-height: 1.7;
  font-size: var(--text-sm);
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
  margin: 0 0 var(--space-2);

  &:last-child { margin-bottom: 0; }
}

.message-bubble :deep(pre) {
  background: var(--bg-page);
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-md);
  overflow-x: auto;
  font-size: 13px;
  margin: var(--space-2) 0;
  border: 1px solid var(--border-light);
}

.message-bubble :deep(code) {
  font-size: 13px;
  font-family: var(--font-mono);
}

.message-bubble :deep(p code) {
  background: var(--bg-accent-light);
  padding: 1px 6px;
  border-radius: var(--radius-sm);
  color: var(--color-amber-600);
}

.message-avatar {
  flex-shrink: 0;
  margin-top: 2px;
}

.thinking-bubble {
  padding: var(--space-4) var(--space-5) !important;
  display: flex;
  align-items: center;
  gap: 6px;
}

.dot-pulse {
  width: 6px;
  height: 6px;
  background: var(--color-amber-400);
  border-radius: 50%;
  animation: dotPulse 1.2s ease-in-out infinite;
}

@keyframes dotPulse {
  0%, 100% { opacity: 0.3; transform: scale(0.8); }
  50% { opacity: 1; transform: scale(1.2); }
}

/* Input */
.chat-input-area {
  padding: var(--space-4) var(--space-6);
  border-top: 1px solid var(--border-default);
  flex-shrink: 0;
  background: var(--bg-surface);
}

.input-row {
  display: flex;
  gap: var(--space-3);
  align-items: flex-end;
}

.chat-textarea :deep(.el-input__wrapper) {
  background: var(--bg-page);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  box-shadow: none;
  padding: var(--space-2) var(--space-4);
  height: 46px;

  &:hover {
    border-color: var(--color-amber-300);
  }

  &.is-focus {
    border-color: var(--color-amber-400);
    box-shadow: 0 0 0 3px rgba(217, 168, 83, 0.08);
  }
}

.send-btn {
  width: 46px;
  height: 46px;
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
</style>
