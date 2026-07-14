<script setup lang="ts">
import { nextTick, ref, watch } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import AppShell from '@/components/AppShell.vue'
import { useScrollReveal } from '@/composables/useScrollReveal'
import { useGlobalLocale } from '@/i18n/globalLocale'
import { measureRouteReady } from '@/utils/performance'

const route = useRoute()
const routeTransition = ref('page-forward')
const lastRouteOrder = ref(Number(route.meta.order ?? 0))
const { refreshScrollReveal } = useScrollReveal()
useGlobalLocale()

watch(
  () => route.fullPath,
  async () => {
    const nextOrder = Number(route.meta.order ?? lastRouteOrder.value)
    routeTransition.value = nextOrder < lastRouteOrder.value ? 'page-back' : 'page-forward'
    lastRouteOrder.value = nextOrder
    await nextTick()
    measureRouteReady(route)
    await refreshScrollReveal()
  },
  { immediate: true },
)
</script>

<template>
  <AppShell>
    <RouterView v-slot="{ Component, route: activeRoute }">
      <Transition :name="routeTransition">
        <KeepAlive :max="4" exclude="AuthView">
          <component :is="Component" :key="activeRoute.name ?? activeRoute.path" />
        </KeepAlive>
      </Transition>
    </RouterView>
  </AppShell>
</template>

<style scoped></style>
