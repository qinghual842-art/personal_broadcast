<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getComments, submitComment } from '@/api/comment'
import { useUserStore } from '@/stores/user'
import { formatDate } from '@/utils/date'
import { ElMessage } from 'element-plus'

const DEFAULT_AVATAR = 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif'

const props = defineProps({ articleId: { type: Number, required: true } })
const router = useRouter()
const userStore = useUserStore()
const comments = ref([])
const total = ref(0)
const page = ref(1)
const content = ref('')
const submitting = ref(false)

async function fetchComments() {
  const res = await getComments(props.articleId, { page: page.value, size: 10 })
  comments.value = res.data.records
  total.value = res.data.total
}

async function handleSubmit() {
  if (!content.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  submitting.value = true
  try {
    await submitComment(props.articleId, { content: content.value })
    ElMessage.success('评论成功')
    content.value = ''
    page.value = 1
    await fetchComments()
  } catch (e) { /* error shown by interceptor */ }
  finally { submitting.value = false }
}

onMounted(fetchComments)
</script>

<template>
  <div class="comment-section">
    <h3 class="comment-heading">评论 ({{ total }})</h3>

    <!-- Logged-in comment form -->
    <div class="comment-form" v-if="userStore.isLoggedIn">
      <div class="form-user-info">
        <el-avatar :size="36" :src="userStore.user?.avatar || DEFAULT_AVATAR" />
        <strong>{{ userStore.user?.nickname || userStore.user?.username }}</strong>
      </div>
      <div class="form-row">
        <el-input
          v-model="content"
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

    <!-- Login prompt -->
    <div class="comment-login-hint" v-else>
      <p>登录后即可发表评论</p>
      <el-button type="primary" size="small" @click="router.push('/login')">去登录</el-button>
    </div>

    <!-- Comment list -->
    <div class="comment-list" v-if="comments.length">
      <div class="comment-item" v-for="c in comments" :key="c.id">
        <div class="comment-avatar">
          <el-avatar :size="40" :src="c.authorAvatar || DEFAULT_AVATAR" />
        </div>
        <div class="comment-body">
          <div class="comment-header">
            <strong>{{ c.authorName }}</strong>
            <span class="author-badge" v-if="c.isAuthor">作者</span>
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

.form-user-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-3);

  strong {
    font-size: var(--text-sm);
    color: var(--text-primary);
  }
}

.form-row {
  margin-bottom: var(--space-3);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
}

.comment-login-hint {
  margin-bottom: var(--space-8);
  background: var(--bg-subtle);
  padding: var(--space-6);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
  text-align: center;

  p {
    margin: 0 0 var(--space-3);
    color: var(--text-tertiary);
    font-size: var(--text-sm);
  }
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

.author-badge {
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  background: var(--color-amber-500);
  padding: 1px 6px;
  border-radius: 999px;
  line-height: 1.4;
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
