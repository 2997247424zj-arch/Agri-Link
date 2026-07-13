<script setup lang="ts">
import { RouterLink } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import { preloadRouteComponent } from '@/router'
import type { NavItem, NavRouteName } from './navigation'

const props = defineProps<{
  navItems: NavItem[]
  profileItem: NavItem | null
  roleTitle: string
  roleHint: string
  expandedName: NavRouteName | null
  activeName: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'toggle-group', item: NavItem): void
  (e: 'preview'): void
  (e: 'close-preview'): void
}>()

function isGroupOpen(item: NavItem) {
  return props.expandedName === item.name || props.activeName === item.name
}

function preload(name: NavRouteName) {
  preloadRouteComponent(name)
}
</script>

<template>
  <aside
    class="sidebar"
    aria-label="角色功能导航"
    @mouseenter="emit('preview')"
    @mouseleave="emit('close-preview')"
  >
    <nav class="sidebar__nav">
      <div v-for="item in navItems" :key="item.to" class="sidebar__group">
        <button
          v-if="item.children?.length"
          class="sidebar__item sidebar__item--button"
          :class="{ 'router-link-active': isGroupOpen(item) }"
          type="button"
          :aria-expanded="isGroupOpen(item)"
          @click="emit('toggle-group', item)"
          @focus="preload(item.name)"
          @mouseenter="preload(item.name)"
        >
          <AppIcon :name="item.icon" />
          <span>{{ item.label }}</span>
          <AppIcon name="arrow" class="sidebar__chevron" />
        </button>
        <RouterLink
          v-else
          :to="item.to"
          class="sidebar__item"
          @click="emit('close')"
          @focus="preload(item.name)"
          @mouseenter="preload(item.name)"
        >
          <AppIcon :name="item.icon" />
          <span>{{ item.label }}</span>
        </RouterLink>
        <div v-if="item.children?.length && isGroupOpen(item)" class="sidebar__subnav">
          <RouterLink
            v-for="child in item.children"
            :key="child.to"
            :to="child.to"
            class="sidebar__subitem"
            @click="emit('close')"
          >
            {{ child.label }}
          </RouterLink>
        </div>
      </div>
    </nav>

    <!-- 个人中心：独立于业务菜单，固定在侧边栏底部。 -->
    <div v-if="profileItem" class="sidebar__footer-nav">
      <span class="sidebar__divider" aria-hidden="true"></span>
      <RouterLink
        :to="profileItem.to"
        class="sidebar__item sidebar__item--profile"
        @click="emit('close')"
        @focus="preload(profileItem.name)"
        @mouseenter="preload(profileItem.name)"
      >
        <AppIcon :name="profileItem.icon" />
        <span>{{ profileItem.label }}</span>
      </RouterLink>
    </div>

    <div class="sidebar__role">
      <strong>{{ roleTitle }}</strong>
      <small>{{ roleHint }}</small>
    </div>
  </aside>
</template>
