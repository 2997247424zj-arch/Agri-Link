<script setup lang="ts">
// 统一分页控件。page/pageCount 只读展示，翻页通过 change 事件回传 -1 / +1。
// 复用既有 .pager 样式，替换 8+ 处重复的上一页/下一页片段。
defineProps<{
  page: number
  pageCount: number
}>()
const emit = defineEmits<{ change: [delta: number] }>()
</script>

<template>
  <div v-if="pageCount > 1" class="pager">
    <button
      class="button button--ghost button--small"
      type="button"
      :disabled="page <= 1"
      @click="emit('change', -1)"
    >
      上一页
    </button>
    <span>第 {{ page }} / {{ pageCount }} 页</span>
    <button
      class="button button--ghost button--small"
      type="button"
      :disabled="page >= pageCount"
      @click="emit('change', 1)"
    >
      下一页
    </button>
  </div>
</template>
