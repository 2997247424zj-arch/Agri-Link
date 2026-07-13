<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import AppIcon from '@/components/AppIcon.vue'
import { resolveAssetUrl } from '@/api/client'

// 统一图片降级：<img> 加载失败或无 src 时，显示带图标的占位块，
// 避免出现「断图绿盒子 + 重叠 alt 文案」。ratio 用 CSS aspect-ratio 保持版位稳定。
const props = withDefaults(
  defineProps<{
    src?: string
    fallbackSrc?: string
    alt?: string
    ratio?: string
    rounded?: boolean
    icon?: 'leaf' | 'bank' | 'expert' | 'cart' | 'user' | 'shield'
  }>(),
  { alt: '', ratio: '16 / 9', rounded: true, icon: 'leaf' },
)

const failed = ref(false)
const usingFallback = ref(false)
const resolvedSrc = computed(() =>
  resolveAssetUrl(usingFallback.value || !props.src ? props.fallbackSrc : props.src),
)
watch(
  () => [props.src, props.fallbackSrc],
  () => {
    failed.value = false
    usingFallback.value = false
  },
)

function handleError() {
  if (!usingFallback.value && props.fallbackSrc && props.fallbackSrc !== props.src) {
    usingFallback.value = true
    return
  }
  failed.value = true
}
</script>

<template>
  <span
    class="app-image"
    :class="{ 'app-image--rounded': rounded, 'app-image--empty': failed || !resolvedSrc }"
    :style="ratio ? { aspectRatio: ratio } : undefined"
  >
    <img v-if="resolvedSrc && !failed" :src="resolvedSrc" :alt="alt" loading="lazy" @error="handleError" />
    <span v-else class="app-image__placeholder" aria-hidden="true">
      <AppIcon :name="icon" />
    </span>
  </span>
</template>
