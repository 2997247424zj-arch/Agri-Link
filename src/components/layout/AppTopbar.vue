<script setup lang="ts">
import { RouterLink } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import type { NavItem } from './navigation'

const props = defineProps<{
  isAuth: boolean
  isLoggedIn: boolean
  roleTitle: string
  roleCode: string
  avatarText: string
  displayName: string
  navItems: NavItem[]
  profileItem: NavItem | null
}>()

const publicNavItems = [
  { to: '/', label: '首页' },
  { to: '/#home-capability', label: '平台能力' },
  { to: '/#home-finance', label: '金融服务' },
  { to: '/#home-experts', label: '专家支持' },
  { to: '/#home-products', label: '产品市场' },
]

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

      <nav v-if="!isAuth" class="topbar__nav" aria-label="主导航">
        <RouterLink
          v-for="item in isLoggedIn ? props.navItems : publicNavItems"
          :key="item.to"
          :to="item.to"
        >
          {{ item.label }}
        </RouterLink>
      </nav>

      <div class="topbar__actions">
        <RouterLink v-if="isAuth" class="button button--ghost" to="/">
          <AppIcon name="home" />
          返回首页
        </RouterLink>
        <RouterLink v-else-if="!isLoggedIn" class="button button--ghost" to="/auth">
          <AppIcon name="user" />
          登录
        </RouterLink>
        <RouterLink v-else-if="profileItem" :to="profileItem.to" class="session-user" aria-label="进入个人中心">
          <span class="session-user__avatar">{{ avatarText }}</span>
          <span class="session-user__text">
            <strong>{{ roleTitle }}</strong>
            <small>{{ roleCode }}</small>
          </span>
          <span class="session-user__name">{{ displayName }}</span>
        </RouterLink>
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
