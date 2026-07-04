<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createAgent, updateAgent, getAdminAgent } from '@/api/agent'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

const personalityMap = [
  { label: '热情', value: 0.9 },
  { label: '温柔', value: 0.7 },
  { label: '冷酷', value: 0.3 },
]

function tempToPersonality(t) {
  const match = personalityMap.find(p => Math.abs(p.value - t) < 0.05)
  return match ? t : 0.7
}

const form = ref({
  name: '', avatar: '', capabilityDesc: '',
  provider: '', apiKey: '', modelName: '',
  temperature: 0.7, maxTokens: 2048, contextLength: 10,
  isEnabled: 1, rateLimitPerMin: 10
})
const saving = ref(false)

const providers = [
  { value: 'openai',   label: 'OpenAI' },
  { value: 'deepseek', label: 'DeepSeek' },
  { value: 'qwen',     label: '通义千问' },
  { value: 'zhipu',    label: '智谱GLM' },
  { value: 'moonshot', label: 'Kimi/Moonshot' },
  { value: 'doubao',   label: '豆包(字节跳动)' },
]

const modelSuggestions = {
  openai:   ['gpt-4o', 'gpt-4o-mini', 'gpt-3.5-turbo'],
  deepseek: ['deepseek-v4-pro', 'deepseek-v4-flash'],
  qwen:     ['qwen-plus', 'qwen-max', 'qwen-turbo'],
  zhipu:    ['glm-4-plus', 'glm-4-flash'],
  moonshot: ['moonshot-v1-8k', 'moonshot-v1-32k', 'moonshot-v1-128k'],
  doubao:   ['doubao-seed-2-1-pro-260628', 'doubao-seed-2-1-lite-260628', 'doubao-seed-1-6-pro', 'doubao-seed-1-6-lite'],
}

const currentSuggestions = computed(() => modelSuggestions[form.value.provider] || [])

const isCustomProvider = computed(() => {
  return form.value.provider && !providers.find(p => p.value === form.value.provider)
})

function selectModel(name) {
  form.value.modelName = name
}

onMounted(async () => {
  if (isEdit.value) {
    const res = await getAdminAgent(route.params.id)
    Object.assign(form.value, res.data)
    form.value.temperature = tempToPersonality(res.data.temperature ?? 0.7)
    form.value.apiKey = ''
  }
})

async function handleSave() {
  if (!form.value.name) {
    ElMessage.warning('请填写名称')
    return
  }
  if (!form.value.capabilityDesc) {
    ElMessage.warning('请填写能力描述')
    return
  }
  if (!form.value.provider) {
    ElMessage.warning('请选择大模型提供商')
    return
  }
  if (!form.value.modelName) {
    ElMessage.warning('请填写或选择模型名称')
    return
  }
  if (!isEdit.value && !form.value.apiKey) {
    ElMessage.warning('请输入API密钥')
    return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await updateAgent(route.params.id, form.value)
      ElMessage.success('更新成功')
      router.push('/admin/agents')
    } else {
      await createAgent(form.value)
      ElMessage.success('创建成功')
      router.push('/admin/agents')
    }
  } catch (e) {
    if (e.response?.data?.message) {
      ElMessage.error(e.response.data.message)
    }
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="agent-edit">
    <h3>{{ isEdit ? '编辑智能体' : '新建智能体' }}</h3>
    <el-form label-width="120px">
      <el-form-item label="名称">
        <el-input v-model="form.name" placeholder="智能体名称" />
      </el-form-item>
      <el-form-item label="能力描述">
        <el-input v-model="form.capabilityDesc" type="textarea" :rows="5" placeholder="系统提示词，描述智能体的能力和角色" />
      </el-form-item>
      <el-form-item label="大模型提供商">
        <el-select v-model="form.provider" placeholder="请选择提供商或自定义" clearable filterable allow-create default-first-option>
          <el-option v-for="p in providers" :key="p.value" :label="p.label" :value="p.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="API 地址" v-if="isCustomProvider">
        <el-input v-model="form.baseUrl" placeholder="自定义提供商的 OpenAI 兼容 API 地址，如 https://api.example.com/v1" />
      </el-form-item>
      <el-form-item label="API密钥">
        <el-input v-model="form.apiKey" type="password" show-password :placeholder="isEdit ? '留空则不修改' : '输入API密钥'" />
      </el-form-item>
      <el-form-item label="模型名称">
        <div class="model-name-wrap">
          <el-input v-model="form.modelName" :placeholder="form.provider ? '输入模型名称' : '请先选择提供商'" />
          <div class="model-suggestions" v-if="currentSuggestions.length">
            <span class="suggest-label">推荐：</span>
            <span
              v-for="m in currentSuggestions" :key="m"
              class="suggest-chip"
              :class="{ active: form.modelName === m }"
              @click="selectModel(m)"
            >{{ m }}</span>
          </div>
          <div class="model-hint" v-if="form.provider === 'doubao'">
            豆包需在<a href="https://console.volcengine.com/ark" target="_blank">火山方舟控制台</a>创建推理端点，填入端点ID
          </div>
        </div>
      </el-form-item>
      <el-form-item label="模型性格">
        <el-select v-model="form.temperature" placeholder="请选择">
          <el-option v-for="p in personalityMap" :key="p.value" :label="p.label" :value="p.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="是否启用">
        <el-switch v-model="form.isEnabled" :active-value="1" :inactive-value="0" />
      </el-form-item>
      <el-form-item>
        <el-button @click="router.push('/admin/agents')">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.agent-edit {
  background: var(--bg-surface);
  padding: var(--space-8);
  border-radius: var(--radius-xl);
  max-width: 800px;
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-card);
}
.agent-edit h3 {
  margin: 0 0 var(--space-6);
  font-size: var(--text-xl);
  font-weight: 700;
  color: var(--text-primary);
  padding-bottom: var(--space-4);
  border-bottom: 1px solid var(--border-light);
  font-family: var(--font-display);
}

.model-name-wrap {
  width: 100%;
}

.model-suggestions {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-top: var(--space-2);
  flex-wrap: wrap;
}

.suggest-label {
  font-size: 12px;
  color: var(--text-tertiary);
  flex-shrink: 0;
}

.suggest-chip {
  font-size: 12px;
  color: var(--color-primary-600);
  background: rgba(61, 90, 150, 0.06);
  padding: 3px 10px;
  border-radius: 999px;
  cursor: pointer;
  transition: all var(--transition-fast);
  border: 1px solid transparent;
  user-select: none;
}

.suggest-chip:hover {
  background: rgba(61, 90, 150, 0.12);
  border-color: var(--color-primary-300);
}

.suggest-chip.active {
  background: var(--color-primary-500);
  color: #fff;
  border-color: var(--color-primary-500);
}

.model-hint {
  margin-top: var(--space-2);
  font-size: 12px;
  color: var(--text-tertiary);
}

.model-hint a {
  color: var(--color-primary-500);
}
</style>
