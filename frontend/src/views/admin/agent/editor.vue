<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createAgent, updateAgent, getAdminAgent } from '@/api/agent'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

const form = ref({
  name: '', description: '', avatar: '', capabilityDesc: '',
  provider: 'openai', apiKey: '', modelName: 'gpt-3.5-turbo',
  temperature: 0.7, maxTokens: 2048, contextLength: 10,
  isEnabled: 1, rateLimitPerMin: 10
})
const saving = ref(false)

const providers = [
  { value: 'openai',   label: 'OpenAI (国外)' },
  { value: 'deepseek', label: 'DeepSeek (国内)' },
  { value: 'qwen',     label: '通义千问 (阿里)' },
  { value: 'zhipu',    label: '智谱GLM (国内)' },
  { value: 'moonshot', label: 'Kimi/Moonshot (国内)' },
  { value: 'doubao',   label: '豆包 (字节跳动)' },
]

onMounted(async () => {
  if (isEdit.value) {
    const res = await getAdminAgent(route.params.id)
    Object.assign(form.value, res.data)
    form.value.apiKey = ''
  }
})

async function handleSave() {
  if (!form.value.name || !form.value.capabilityDesc) {
    ElMessage.warning('请填写名称和能力描述')
    return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await updateAgent(route.params.id, form.value)
      ElMessage.success('更新成功')
    } else {
      if (!form.value.apiKey) { ElMessage.warning('请输入API密钥'); return }
      await createAgent(form.value)
      ElMessage.success('创建成功')
      router.push('/admin/agents')
    }
  } finally { saving.value = false }
}
</script>

<template>
  <div class="agent-edit">
    <h3>{{ isEdit ? '编辑智能体' : '新建智能体' }}</h3>
    <el-form label-width="120px">
      <el-form-item label="名称"><el-input v-model="form.name" placeholder="智能体名称" /></el-form-item>
      <el-form-item label="简介"><el-input v-model="form.description" type="textarea" :rows="2" placeholder="简短描述" /></el-form-item>
      <el-form-item label="头像URL"><el-input v-model="form.avatar" placeholder="头像图片地址" /></el-form-item>
      <el-form-item label="能力描述"><el-input v-model="form.capabilityDesc" type="textarea" :rows="4" placeholder="系统提示词，描述智能体的能力和角色" /></el-form-item>
      <el-form-item label="大模型提供商">
        <el-select v-model="form.provider">
          <el-option v-for="p in providers" :key="p.value" :label="p.label" :value="p.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="API密钥">
        <el-input v-model="form.apiKey" type="password" show-password :placeholder="isEdit ? '留空则不修改' : '输入API密钥'" />
      </el-form-item>
      <el-form-item label="模型名称"><el-input v-model="form.modelName" placeholder="例如 gpt-3.5-turbo" /></el-form-item>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="温度"><el-input-number v-model="form.temperature" :min="0" :max="2" :step="0.1" :precision="1" /></el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最大Token"><el-input-number v-model="form.maxTokens" :min="100" :max="8192" :step="100" /></el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="上下文轮数"><el-input-number v-model="form.contextLength" :min="1" :max="50" /></el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="频率限制/分钟"><el-input-number v-model="form.rateLimitPerMin" :min="1" :max="100" /></el-form-item>
      <el-form-item label="是否启用"><el-switch v-model="form.isEnabled" :active-value="1" :inactive-value="0" /></el-form-item>
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
</style>
