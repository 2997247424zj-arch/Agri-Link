<script setup lang="ts">
import type { IconName } from '@/components/icons'

defineProps<{
  name: IconName
}>()

const paths: Record<IconName, string> = {
  leaf: 'M5 19c8 0 14-6 14-14C11 5 5 11 5 19Zm0 0c0-4 2-7 6-9',
  bank: 'M3 10h18L12 4 3 10Zm3 2v6m4-6v6m4-6v6m4-6v6M4 20h16',
  expert: 'M12 13a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm-7 7a7 7 0 0 1 14 0M18 8l3 2-3 2',
  cart: 'M4 5h2l2 10h9l3-7H7m2 12a1 1 0 1 0 0-2 1 1 0 0 0 0 2Zm8 0a1 1 0 1 0 0-2 1 1 0 0 0 0 2Z',
  user: 'M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm-7 8a7 7 0 0 1 14 0',
  shield: 'M12 21s7-4 7-10V5l-7-3-7 3v6c0 6 7 10 7 10Zm-3-10 2 2 4-4',
  arrow: 'M5 12h14m-6-6 6 6-6 6',
  search: 'M11 18a7 7 0 1 0 0-14 7 7 0 0 0 0 14Zm5-2 4 4',
  plus: 'M12 5v14M5 12h14',
  home: 'M4 11 12 4l8 7v8a1 1 0 0 1-1 1h-5v-6h-4v6H5a1 1 0 0 1-1-1v-8Z',
  check: 'M5 12l4 4L19 6',
  menu: 'M4 6h16M4 12h16M4 18h16',
  logout: 'M10 17l5-5-5-5M15 12H3m6 7h8a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2H9',
}
</script>

<template>
  <svg class="app-icon" viewBox="0 0 24 24" aria-hidden="true">
    <path :d="paths[name]" />
  </svg>
</template>
