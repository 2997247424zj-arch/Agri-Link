<script setup lang="ts" generic="T extends string | number">
// 模块切换 Tab 条。v-model 绑定当前值，options 为 { label, value } 数组。
// 复用既有 .tabs.module-switcher / .tab 样式与 role=tablist 语义。
defineProps<{
  modelValue: T
  options: ReadonlyArray<{ label: string; value: T }>
  ariaLabel?: string
}>()
const emit = defineEmits<{ 'update:modelValue': [value: T] }>()
</script>

<template>
  <div class="tabs module-switcher" role="tablist" :aria-label="ariaLabel">
    <button
      v-for="tab in options"
      :key="String(tab.value)"
      class="tab"
      type="button"
      role="tab"
      :aria-selected="modelValue === tab.value"
      @click="emit('update:modelValue', tab.value)"
    >
      {{ tab.label }}
    </button>
  </div>
</template>
