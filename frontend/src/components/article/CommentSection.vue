<script setup>
import { ref, onMounted } from 'vue'
import { getComments, submitComment } from '@/api/comment'
import { formatDate } from '@/utils/date'
import { ElMessage } from 'element-plus'

const props = defineProps({ articleId: { type: Number, required: true } })
const comments = ref([])
const total = ref(0)
const page = ref(1)
const form = ref({ authorName: '', authorEmail: '', content: '' })
const submitting = ref(false)

async function fetchComments() {
  const res = await getComments(props.articleId, { page: page.value, size: 10 })
  comments.value = res.data.records
  total.value = res.data.total
}

async function handleSubmit() {
  if (!form.value.authorName.trim() || !form.value.content.trim()) {
    ElMessage.warning('请填写昵称和评论内容')
    return
  }
  submitting.value = true
  try {
    await submitComment(props.articleId, form.value)
    ElMessage.success('评论成功，等待审核后展示')
    form.value = { authorName: '', authorEmail: '', content: '' }
    page.value = 1
    await fetchComments()
  } finally {
    submitting.value = false
  }
}

onMounted(fetchComments)
</script>

<template>
  <div class="comment-section">
    <h3 class="comment-heading">评论 ({{ total }})</h3>

    <!-- 评论表单 -->
    <div class="comment-form">
      <div class="form-row">
        <el-input
          v-model="form.authorName"
          placeholder="你的昵称 *"
          size="default"
          class="form-name"
        >
          <template #prefix>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" stroke="#94a3b8" stroke-width="2" stroke-linecap="round"/>
              <circle cx="12" cy="7" r="4" stroke="#94a3b8" stroke-width="2"/>
            </svg>
          </template>
        </el-input>
      </div>
      <div class="form-row">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="3"
          placeholder="写下你的想法..."
        />
      </div>
      <div class="form-actions">
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          发表评论
        </el-button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list" v-if="comments.length">
      <div class="comment-item" v-for="c in comments" :key="c.id">
        <div class="comment-avatar">
          <span class="avatar-letter">{{ c.authorName.charAt(0) }}</span>
        </div>
        <div class="comment-body">
          <div class="comment-header">
            <strong>{{ c.authorName }}</strong>
            <span class="comment-time">{{ formatDate(c.createTime) }}</span>
          </div>
          <p class="comment-content">{{ c.content }}</p>
        </div>
      </div>

      <div class="comment-pagination" v-if="total > 10">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="10"
          v-model:current-page="page"
          @current-change="fetchComments"
        />
      </div>
    </div>

    <el-empty v-else description="还没有评论，快来抢沙发吧" />
  </div>
</template>

<style scoped>
.comment-section {
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  padding: var(--space-8);
  box-shadow: var(--shadow-card);
  animation: fadeInUp 0.5s ease-out;
}

.comment-heading {
  margin: 0 0 var(--space-6);
  font-size: var(--text-lg);
  color: var(--text-primary);
  font-family: var(--font-display);
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: var(--space-2);

  &::before {
    content: '';
    width: 4px;
    height: 20px;
    background: linear-gradient(180deg, var(--color-primary-400), var(--color-primary-600));
    border-radius: 2px;
  }
}

/* Form */
.comment-form {
  margin-bottom: var(--space-8);
  background: var(--bg-subtle);
  padding: var(--space-5);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
}

.form-row {
  margin-bottom: var(--space-3);
}

.form-name {
  max-width: 280px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
}

/* Comment Items */
.comment-list {
  display: flex;
  flex-direction: column;
}

.comment-item {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-5) 0;
  border-bottom: 1px solid var(--border-light);

  &:last-child {
    border-bottom: none;
  }
}

.comment-avatar {
  flex-shrink: 0;
}

.avatar-letter {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary-400), var(--color-primary-600));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: var(--text-md);
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-2);

  strong {
    color: var(--text-primary);
    font-size: var(--text-sm);
    font-weight: 600;
  }
}

.comment-time {
  color: var(--text-tertiary);
  font-size: 12px;
}

.comment-content {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.7;
  font-size: var(--text-sm);
  word-break: break-word;
}

.comment-pagination {
  display: flex;
  justify-content: center;
  padding-top: var(--space-5);
  border-top: 1px solid var(--border-light);
  margin-top: var(--space-3);
}
</style>
