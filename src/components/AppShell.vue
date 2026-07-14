<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppTopbar from '@/components/layout/AppTopbar.vue'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import { navByRole, profileNavItem, roleCodes, roleHints, type NavItem, type NavRouteName } from '@/components/layout/navigation'
import { useUiScale } from '@/composables/useUiScale'
import { useSessionStore } from '@/stores/session'
import { useNotificationStore } from '@/stores/notification'
import { routeRoles } from '@/router'

const session = useSessionStore()
const notifStore = useNotificationStore()
const route = useRoute()
const router = useRouter()
const { shellRef } = useUiScale()
const sidebarOpen = ref(false)
const sidebarPinned = ref(false)
const expandedNavName = ref<NavRouteName | null>(null)

let pollTimer: ReturnType<typeof setInterval> | null = null

function startPoll() {
  notifStore.fetchUnreadCount()
  pollTimer = setInterval(() => notifStore.fetchUnreadCount(), 30_000)
}

function stopPoll() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

onMounted(() => {
  if (session.isLoggedIn) startPoll()
})

onUnmounted(() => stopPoll())

watch(() => session.isLoggedIn, (loggedIn) => {
  if (loggedIn) startPoll()
  else stopPoll()
})

function canAccess(item: NavItem) {
  const allowedRoles = routeRoles[item.name]
  if (!allowedRoles) return true
  return session.isLoggedIn && allowedRoles.includes(session.role)
}

const isAuth = computed(() => route.name === 'auth')
const visibleNavItems = computed(() => navByRole[session.role].filter(canAccess))
const visibleProfileItem = computed(() => (canAccess(profileNavItem) ? profileNavItem : null))
const roleTitle = computed(() => (session.isLoggedIn ? session.roleLabel : '访客'))
const roleHint = computed(() => (session.isLoggedIn ? roleHints[session.role] : '登录后按角色开放功能'))
const roleCode = computed(() => (session.isLoggedIn ? roleCodes[session.role] : 'Guest'))
const avatarText = computed(() => (session.isLoggedIn ? session.roleLabel.slice(0, 1) : '访'))
const activeRouteName = computed(() => (typeof route.name === 'string' ? route.name : ''))

function logout() {
  session.logout()
  sidebarOpen.value = false
  sidebarPinned.value = false
  router.push('/auth')
}

function toggleSidebar() {
  sidebarPinned.value = !sidebarPinned.value
  sidebarOpen.value = sidebarPinned.value
}

function closeMobileSidebar() {
  sidebarOpen.value = false
  sidebarPinned.value = false
}

function toggleNavGroup(item: NavItem) {
  expandedNavName.value = expandedNavName.value === item.name ? null : item.name
  sidebarOpen.value = true
}

function previewSidebar() {
  sidebarOpen.value = true
}

function closeSidebarPreview() {
  if (!sidebarPinned.value) sidebarOpen.value = false
}
</script>

<template>
  <div ref="shellRef" class="app-shell" :class="{ 'app-shell--auth': isAuth, 'app-shell--sidebar-open': sidebarOpen }">
    <a class="skip-link" href="#main-content">跳到主要内容</a>

    <AppTopbar
      :is-auth="isAuth"
      :is-logged-in="session.isLoggedIn"
      :role-title="roleTitle"
      :role-code="roleCode"
      :avatar-text="avatarText"
      :display-name="session.displayName"
      :nav-items="visibleNavItems"
      :profile-item="visibleProfileItem"
      @toggle-sidebar="toggleSidebar"
      @logout="logout"
    />

    <div v-if="!isAuth" class="layout-body">
      <AppSidebar
        :nav-items="visibleNavItems"
        :profile-item="visibleProfileItem"
        :role-title="roleTitle"
        :role-hint="roleHint"
        :expanded-name="expandedNavName"
        :active-name="activeRouteName"
        @close="closeMobileSidebar"
        @toggle-group="toggleNavGroup"
        @preview="previewSidebar"
        @close-preview="closeSidebarPreview"
      />

      <button
        v-if="sidebarOpen"
        class="sidebar-scrim"
        type="button"
        aria-label="关闭主菜单"
        @click="closeMobileSidebar"
      ></button>

      <main id="main-content" class="main-content layout-content" aria-live="polite">
        <slot />
      </main>
    </div>

    <main v-else id="main-content" class="main-content auth-content" aria-live="polite">
      <slot />
    </main>
  </div>
</template>
