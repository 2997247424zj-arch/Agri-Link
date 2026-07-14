<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import { useLocaleStore } from '@/stores/locale'
import { useNotificationStore } from '@/stores/notification'
import type { NavItem } from './navigation'

const route = useRoute()
const router = useRouter()
const locale = useLocaleStore()
const notifStore = useNotificationStore()

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

const notifOpen = ref(false)

function toggleNotifications() {
  notifOpen.value = !notifOpen.value
  if (notifOpen.value) notifStore.fetchNotifications()
}

function closeNotifications() {
  notifOpen.value = false
}

function formatTime(time?: string) {
  if (!time) return ''
  const d = new Date(time)
  if (isNaN(d.getTime())) return time
  return `${d.getMonth() + 1}/${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const publicNavItems = computed(() => [
  { to: '/', label: locale.t('首页', 'Home') },
  { to: '/#home-capability', label: locale.t('平台能力', 'Capabilities') },
  { to: '/#home-finance', label: locale.t('金融服务', 'Finance') },
  { to: '/#home-experts', label: locale.t('专家支持', 'Experts') },
  { to: '/#home-products', label: locale.t('产品市场', 'Marketplace') },
])

const englishNavLabels: Record<NavItem['name'], string> = {
  home: 'Home',
  trade: 'Marketplace',
  finance: 'Finance',
  experts: 'Experts',
  cart: 'Cart',
  profile: 'Profile',
  admin: 'Administration',
}

const emit = defineEmits<{
  (e: 'toggle-sidebar'): void
  (e: 'logout'): void
}>()

function navLabel(item: NavItem) {
  return locale.isEnglish ? englishNavLabels[item.name] : item.label
}

function isPublicNavActive(to: string) {
  if (to === '/') return route.path === '/' && !route.hash
  return route.path === '/' && route.hash === to.slice(1)
}

async function navigatePublic(to: string) {
  await router.push(to)
  await nextTick()
  const hash = to.includes('#') ? to.slice(to.indexOf('#')) : ''
  if (hash) document.querySelector(hash)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  else document.getElementById('main-content')?.scrollTo({ top: 0, behavior: 'smooth' })
}
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
          <small>{{ locale.t('农产品融销一体平台', 'Agricultural finance & trade') }}</small>
        </span>
      </RouterLink>

      <nav v-if="!isAuth" class="topbar__nav" aria-label="主导航">
        <template v-if="!isLoggedIn">
          <a
            v-for="item in publicNavItems"
            :key="item.to"
            :class="{ 'router-link-active': isPublicNavActive(item.to) }"
            :href="item.to"
            @click.prevent="navigatePublic(item.to)"
          >{{ item.label }}</a>
        </template>
        <template v-else>
          <RouterLink v-for="item in props.navItems" :key="item.to" :to="item.to">
            {{ navLabel(item) }}
          </RouterLink>
        </template>
      </nav>

      <div class="topbar__actions">
        <div v-if="isLoggedIn && !isAuth" class="notification-area">
          <button
            class="notification-bell"
            type="button"
            :aria-label="locale.t('通知', 'Notifications')"
            @click="toggleNotifications"
          >
            <AppIcon name="bell" />
            <span v-if="notifStore.unreadCount > 0" class="notification-badge">
              {{ notifStore.unreadCount > 99 ? '99+' : notifStore.unreadCount }}
            </span>
          </button>
          <div v-if="notifOpen" class="notification-dropdown">
            <div class="notification-dropdown__header">
              <strong>{{ locale.t('消息通知', 'Notifications') }}</strong>
              <button
                v-if="notifStore.unreadCount > 0"
                class="button button--ghost button--small"
                type="button"
                @click="notifStore.markAllAsRead()"
              >
                {{ locale.t('全部已读', 'Mark all read') }}
              </button>
            </div>
            <div class="notification-dropdown__list">
              <div
                v-for="n in notifStore.notifications.slice(0, 30)"
                :key="n.notificationId"
                class="notification-item"
                :class="{ 'notification-item--unread': !n.isRead }"
                @click="notifStore.markAsRead(n.notificationId)"
              >
                <strong>{{ n.title }}</strong>
                <small>{{ n.content }}</small>
                <em>{{ formatTime(n.createTime) }}</em>
              </div>
              <div v-if="!notifStore.notifications.length" class="notification-empty">
                {{ locale.t('暂无通知', 'No notifications') }}
              </div>
            </div>
          </div>
          <div v-if="notifOpen" class="notification-backdrop" @click="closeNotifications"></div>
        </div>
        <button
          class="language-toggle"
          type="button"
          :aria-label="locale.t('切换为英文', 'Switch to Chinese')"
          :title="locale.t('切换为英文', 'Switch to Chinese')"
          @click="locale.toggle"
        >
          {{ locale.isEnglish ? '中' : 'EN' }}
        </button>
        <RouterLink v-if="isAuth" class="button button--ghost" to="/">
          <AppIcon name="home" />
          {{ locale.t('返回首页', 'Back home') }}
        </RouterLink>
        <RouterLink v-else-if="!isLoggedIn" class="button button--ghost" to="/auth">
          <AppIcon name="user" />
          {{ locale.t('登录', 'Sign in') }}
        </RouterLink>
        <RouterLink v-else-if="profileItem" :to="profileItem.to" class="session-user" aria-label="进入个人中心">
          <span class="session-user__avatar">{{ avatarText }}</span>
          <span class="session-user__text">
            <strong>{{ locale.isEnglish ? roleCode.replace('Dev ', '') : roleTitle }}</strong>
            <small>{{ locale.isEnglish ? 'Workspace' : `${roleTitle}端` }}</small>
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
          {{ locale.t('退出', 'Sign out') }}
        </button>
      </div>
    </div>
  </header>
</template>
