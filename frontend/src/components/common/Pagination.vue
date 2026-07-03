<script setup>
const props = defineProps({
  total: { type: Number, default: 0 },
  page: { type: Number, default: 1 },
  size: { type: Number, default: 10 }
})

const emit = defineEmits(['change'])

function handleChange(page) {
  emit('change', page)
}
</script>

<template>
  <div class="pagination-wrap" v-if="total > size">
    <el-pagination
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="size"
      :current-page="page"
      @current-change="handleChange"
    />
  </div>
</template>

<style scoped>
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: var(--space-8) 0;
}
</style>
