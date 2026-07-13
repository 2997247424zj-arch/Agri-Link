<script setup lang="ts">
import AppIcon from '@/components/AppIcon.vue'
import type { IconName } from '@/components/icons'

// 统一的页头：eyebrow 徽标 + 标题 + 说明，右侧 actions 插槽放搜索/按钮工具条。
// 复用既有 .section-title / .eyebrow / .toolbar 样式，迁移零视觉变化。
defineProps<{
  eyebrow?: string
  icon?: IconName
  title: string
  desc?: string
}>()
</script>

<template>
  <div class="section-title">
    <div>
      <span v-if="eyebrow" class="eyebrow"><AppIcon v-if="icon" :name="icon" />{{ eyebrow }}</span>
      <h2>{{ title }}</h2>
      <p v-if="desc">{{ desc }}</p>
      <slot name="subtitle" />
    </div>
    <div v-if="$slots.actions" class="toolbar">
      <slot name="actions" />
    </div>
  </div>
</template>
