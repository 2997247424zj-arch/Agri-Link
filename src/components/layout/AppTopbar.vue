<script setup lang="ts">
import { RouterLink } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'

defineProps<{
  isAuth: boolean
  isLoggedIn: boolean
  roleTitle: string
  roleCode: string
  avatarText: string
  displayName: string
}>()

const emit = defineEmits<{
  (e: 'toggle-sidebar'): void
  (e: 'logout'): void
}>()
</script>

<template>
  <header class="topbar">
    <div class="topbar__inner">
      <button
        v-if="!isAuth"
        class="sidebar-toggle"
        type="button"
        aria-label="展开主菜单"
        @click="emit('toggle-sidebar')"
      >
        <AppIcon name="menu" />
      </button>

      <RouterLink to="/" class="brand" aria-label="Agri-Link 首页">
        <span class="brand__mark"><AppIcon name="leaf" /></span>
        <span>
          <strong>Agri-Link</strong>
          <small>农产品融销一体平台</small>
        </span>
      </RouterLink>

      <div class="topbar__actions">
        <RouterLink v-if="isAuth" class="button button--ghost" to="/">
          <AppIcon name="home" />
          返回首页
        </RouterLink>
        <RouterLink v-else-if="!isLoggedIn" class="button button--ghost" to="/auth">
          <AppIcon name="user" />
          登录
        </RouterLink>
        <div v-else class="session-user">
          <span class="session-user__avatar">{{ avatarText }}</span>
          <span class="session-user__text">
            <strong>{{ roleTitle }}</strong>
            <small>{{ roleCode }}</small>
          </span>
          <span class="session-user__name">{{ displayName }}</span>
        </div>
        <button
          v-if="isLoggedIn && !isAuth"
          class="button button--ghost logout-button"
          type="button"
          @click="emit('logout')"
        >
          <AppIcon name="logout" />
          退出
        </button>
      </div>
    </div>
  </header>
</template>
