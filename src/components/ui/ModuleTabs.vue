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
  <div
    class="tabs module-switcher"
    role="tablist"
    :aria-label="ariaLabel"
    :style="{ '--module-tab-count': options.length }"
  >
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

<style scoped>
.module-switcher {
  grid-template-columns: repeat(var(--module-tab-count), minmax(0, 1fr));
}

@media (max-width: 720px) {
  .module-switcher {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 440px) {
  .module-switcher {
    grid-template-columns: 1fr;
  }
}
</style>
