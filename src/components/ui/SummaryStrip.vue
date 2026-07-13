<script setup lang="ts">
// 顶部指标条：4 格 strong + span。items 为 { value, label } 数组。
// 复用既有 .summary-strip / .metric 样式。extraClass 用于 Profile 的变体。
defineProps<{
  items: ReadonlyArray<{ value: string | number; label: string }>
  extraClass?: string
}>()
</script>

<template>
  <div class="summary-strip" :class="extraClass">
    <div v-for="(item, index) in items" :key="index" class="metric">
      <strong>{{ item.value }}</strong>
      <span>{{ item.label }}</span>
    </div>
  </div>
</template>
