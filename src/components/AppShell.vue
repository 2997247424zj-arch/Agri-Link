<script setup lang="ts">
import { computed, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import { useParticles } from '@/composables/useParticles'
import { useSpotlight } from '@/composables/useSpotlight'
import { useSessionStore } from '@/stores/session'
import type { UserRole } from '@/types/domain'
import { preloadRouteComponent, routeRoles } from '@/router'

type NavRouteName = 'home' | 'trade' | 'finance' | 'experts' | 'cart' | 'profile' | 'admin'
type NavItem = {
  to: string
  name: NavRouteName
  label: string
  icon: 'home' | 'leaf' | 'bank' | 'expert' | 'cart' | 'user' | 'shield'
  children?: Array<{ to: string; label: string }>
}

const session = useSessionStore()
const route = useRoute()
const router = useRouter()
const { canvasRef } = useParticles()
const sidebarOpen = ref(false)
const sidebarPinned = ref(false)
const expandedNavName = ref<NavRouteName | null>(null)
useSpotlight()

const navByRole: Record<UserRole, NavItem[]> = {
  FARMER: [
    {
      to: '/',
      name: 'home',
      label: '首页',
      icon: 'home',
      children: [
        { to: '/#home-capability', label: '项目能力速览' },
        { to: '/#home-dashboard', label: '业务工作台' },
        { to: '/#home-finance', label: '金融产品' },
        { to: '/#home-services', label: '核心服务' },
        { to: '/#home-experts', label: '专家团队' },
        { to: '/#home-qa', label: '专家问答' },
        { to: '/#home-tips', label: '栽培小技巧' },
        { to: '/#home-news', label: '平台资讯' },
        { to: '/#home-products', label: '最新农产品' },
      ],
    },
    {
      to: '/trade',
      name: 'trade',
      label: '农产品交易',
      icon: 'leaf',
      children: [
        { to: '/trade?tab=browse', label: '货源浏览' },
        { to: '/trade?tab=publish', label: '农产品发布' },
      ],
    },
    {
      to: '/finance',
      name: 'finance',
      label: '融资申请',
      icon: 'bank',
      children: [
        { to: '/finance?tab=intro', label: '基础介绍' },
        { to: '/finance?tab=apply', label: '申请登记' },
        { to: '/finance?tab=result', label: '审批结果' },
      ],
    },
    {
      to: '/experts',
      name: 'experts',
      label: '专家助力',
      icon: 'expert',
      children: [
        { to: '/experts?tab=experts', label: '专家列表' },
        { to: '/experts?tab=records', label: '我的咨询' },
      ],
    },
    { to: '/profile', name: 'profile', label: '个人中心', icon: 'user' },
  ],
  BUYER: [
    {
      to: '/',
      name: 'home',
      label: '首页',
      icon: 'home',
      children: [
        { to: '/#home-capability', label: '项目能力速览' },
        { to: '/#home-dashboard', label: '业务工作台' },
        { to: '/#home-services', label: '核心服务' },
        { to: '/#home-tips', label: '栽培小技巧' },
        { to: '/#home-news', label: '平台资讯' },
        { to: '/#home-products', label: '最新农产品' },
      ],
    },
    { to: '/trade', name: 'trade', label: '农产品采购', icon: 'leaf' },
    { to: '/cart', name: 'cart', label: '采购车', icon: 'cart' },
    { to: '/profile', name: 'profile', label: '个人中心', icon: 'user' },
  ],
  EXPERT: [
    {
      to: '/',
      name: 'home',
      label: '首页',
      icon: 'home',
      children: [
        { to: '/#home-capability', label: '项目能力速览' },
        { to: '/#home-dashboard', label: '业务工作台' },
        { to: '/#home-services', label: '核心服务' },
        { to: '/#home-experts', label: '专家团队' },
        { to: '/#home-qa', label: '专家问答' },
        { to: '/#home-tips', label: '栽培小技巧' },
        { to: '/#home-news', label: '平台资讯' },
      ],
    },
    { to: '/experts', name: 'experts', label: '咨询工单', icon: 'expert' },
    { to: '/profile', name: 'profile', label: '个人中心', icon: 'user' },
  ],
  BANK: [
    {
      to: '/',
      name: 'home',
      label: '首页',
      icon: 'home',
      children: [
        { to: '/#home-capability', label: '项目能力速览' },
        { to: '/#home-dashboard', label: '业务工作台' },
        { to: '/#home-finance', label: '金融产品' },
        { to: '/#home-services', label: '核心服务' },
        { to: '/#home-tips', label: '栽培小技巧' },
        { to: '/#home-news', label: '平台资讯' },
      ],
    },
    { to: '/finance', name: 'finance', label: '融资审核', icon: 'bank' },
    { to: '/profile', name: 'profile', label: '个人中心', icon: 'user' },
  ],
  SYSTEM_ADMIN: [
    {
      to: '/',
      name: 'home',
      label: '首页',
      icon: 'home',
      children: [
        { to: '/#home-capability', label: '项目能力速览' },
        { to: '/#home-dashboard', label: '业务工作台' },
        { to: '/#home-services', label: '核心服务' },
        { to: '/#home-tips', label: '栽培小技巧' },
      ],
    },
    { to: '/admin', name: 'admin', label: '后台管理', icon: 'shield' },
    { to: '/profile', name: 'profile', label: '个人中心', icon: 'user' },
  ],
}

const roleHints: Record<UserRole, string> = {
  FARMER: '发布货源、提交融资申请、补充材料、咨询专家',
  BUYER: '浏览货源、维护购物车、生成并跟踪采购单',
  EXPERT: '维护专家资料、答复问答、处理预约、发布农技知识',
  BANK: '维护贷款产品、匹配融资农户、审批融资申请',
  SYSTEM_ADMIN: '管理用户角色、监管交易状态、查看融资进度、维护内容',
}

const isAuth = computed(() => route.name === 'auth')
const visibleNavItems = computed(() =>
  navByRole[session.role].filter((item) => {
    const allowedRoles = routeRoles[item.name]
    if (!allowedRoles) return true
    return session.isLoggedIn && allowedRoles.includes(session.role)
  }),
)
const roleTitle = computed(() => (session.isLoggedIn ? session.roleLabel : '访客'))
const roleHint = computed(() => (session.isLoggedIn ? roleHints[session.role] : '登录后按角色开放功能'))
const roleCode = computed(() => {
  const codes: Record<UserRole, string> = {
    FARMER: 'Dev Farmer',
    BUYER: 'Dev Buyer',
    EXPERT: 'Dev Expert',
    BANK: 'Dev Finance',
    SYSTEM_ADMIN: 'Dev Admin',
  }

  return session.isLoggedIn ? codes[session.role] : 'Guest'
})
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

function isNavGroupOpen(item: NavItem) {
  return expandedNavName.value === item.name || activeRouteName.value === item.name
}

function previewSidebar() {
  sidebarOpen.value = true
}

function closeSidebarPreview() {
  if (!sidebarPinned.value) sidebarOpen.value = false
}
</script>

<template>
  <div class="app-shell" :class="{ 'app-shell--auth': isAuth, 'app-shell--sidebar-open': sidebarOpen }">
    <canvas ref="canvasRef" class="ambient-particles" aria-hidden="true"></canvas>
    <div class="ambient-spotlight" aria-hidden="true"></div>
    <header class="topbar">
      <div class="topbar__inner">
        <button v-if="!isAuth" class="sidebar-toggle" type="button" aria-label="展开主菜单" @click="toggleSidebar">
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
          <RouterLink v-else-if="!session.isLoggedIn" class="button button--ghost" to="/auth">
            <AppIcon name="user" />
            登录
          </RouterLink>
          <div v-else class="session-user">
            <span class="session-user__avatar">{{ avatarText }}</span>
            <span class="session-user__text">
              <strong>{{ roleTitle }}</strong>
              <small>{{ roleCode }}</small>
            </span>
            <span class="session-user__name">{{ session.displayName }}</span>
          </div>
          <button v-if="session.isLoggedIn && !isAuth" class="button button--ghost logout-button" type="button" @click="logout">
            <AppIcon name="logout" />
            退出
          </button>
        </div>
      </div>
    </header>

    <div v-if="!isAuth" class="layout-body">
      <aside
        class="sidebar"
        aria-label="角色功能导航"
        @mouseenter="previewSidebar"
        @mouseleave="closeSidebarPreview"
      >
        <nav class="sidebar__nav">
          <div v-for="item in visibleNavItems" :key="item.to" class="sidebar__group">
            <button
              v-if="item.children?.length"
              class="sidebar__item sidebar__item--button"
              :class="{ 'router-link-active': isNavGroupOpen(item) }"
              type="button"
              :aria-expanded="isNavGroupOpen(item)"
              @click="toggleNavGroup(item)"
              @focus="preloadRouteComponent(item.name)"
              @mouseenter="preloadRouteComponent(item.name)"
            >
              <AppIcon :name="item.icon" />
              <span>{{ item.label }}</span>
              <AppIcon name="arrow" class="sidebar__chevron" />
            </button>
            <RouterLink
              v-else
              :to="item.to"
              class="sidebar__item"
              @click="closeMobileSidebar"
              @focus="preloadRouteComponent(item.name)"
              @mouseenter="preloadRouteComponent(item.name)"
            >
              <AppIcon :name="item.icon" />
              <span>{{ item.label }}</span>
            </RouterLink>
            <div v-if="item.children?.length && isNavGroupOpen(item)" class="sidebar__subnav">
              <RouterLink
                v-for="child in item.children"
                :key="child.to"
                :to="child.to"
                class="sidebar__subitem"
                @click="closeMobileSidebar"
              >
                {{ child.label }}
              </RouterLink>
            </div>
          </div>
        </nav>
        <div class="sidebar__role">
          <strong>{{ roleTitle }}</strong>
          <small>{{ roleHint }}</small>
        </div>
      </aside>

      <button
        v-if="sidebarOpen"
        class="sidebar-scrim"
        type="button"
        aria-label="关闭主菜单"
        @click="closeMobileSidebar"
      ></button>

      <main class="main-content layout-content" aria-live="polite">
        <slot />
      </main>
    </div>

    <main v-else class="main-content auth-content" aria-live="polite">
      <slot />
    </main>
  </div>
</template>
