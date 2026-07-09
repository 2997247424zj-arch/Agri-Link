<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import { useParticles } from '@/composables/useParticles'
import { useSpotlight } from '@/composables/useSpotlight'
import { useSessionStore } from '@/stores/session'
import type { UserRole } from '@/types/domain'
import { preloadRouteComponent, routeRoles } from '@/router'

const session = useSessionStore()
const route = useRoute()
const router = useRouter()
const { canvasRef } = useParticles()
useSpotlight()

// 顶部导航按角色过滤，避免展示不可访问入口。
const navItems = [
  { to: '/', name: 'home', label: '首页', icon: 'home' },
  { to: '/trade', name: 'trade', label: '农产品交易', icon: 'leaf' },
  { to: '/finance', name: 'finance', label: '融资服务', icon: 'bank' },
  { to: '/experts', name: 'experts', label: '专家助力', icon: 'expert' },
  { to: '/cart', name: 'cart', label: '购物车', icon: 'cart' },
  { to: '/profile', name: 'profile', label: '个人中心', icon: 'user' },
  { to: '/admin', name: 'admin', label: '后台管理', icon: 'shield' },
] as const

const roleHints: Record<UserRole, string> = {
  FARMER: '发布货源、申请融资、咨询专家',
  BUYER: '浏览货源、维护购物车、生成采购单',
  EXPERT: '处理问答、查看预约、沉淀农技服务',
  BANK: '匹配农户、审批融资申请',
  SYSTEM_ADMIN: '统筹用户、交易、融资与内容',
}

const isAuth = computed(() => route.name === 'auth')
const visibleNavItems = computed(() =>
  navItems.filter((item) => {
    const allowedRoles = routeRoles[item.name]
    if (!allowedRoles) return true
    return session.isLoggedIn && allowedRoles.includes(session.role)
  }),
)
const roleTitle = computed(() => (session.isLoggedIn ? session.roleLabel : '访客'))
const roleHint = computed(() => (session.isLoggedIn ? roleHints[session.role] : '登录后按角色开放功能'))

function logout() {
  session.logout()
  router.push('/auth')
}
</script>

<template>
  <div class="app-shell" :class="{ 'app-shell--auth': isAuth }">
    <canvas ref="canvasRef" class="ambient-particles" aria-hidden="true"></canvas>
    <div class="ambient-spotlight" aria-hidden="true"></div>
    <header class="topbar">
      <div class="topbar__inner">
        <RouterLink to="/" class="brand" aria-label="Agri-Link 首页">
          <span class="brand__mark"><AppIcon name="leaf" /></span>
          <span>
            <strong>Agri-Link</strong>
            <small>农产品融销一体平台</small>
          </span>
        </RouterLink>

        <nav class="topbar__nav" aria-label="主导航">
          <RouterLink
            v-for="item in visibleNavItems"
            :key="item.to"
            :to="item.to"
            @focus="preloadRouteComponent(item.name)"
            @mouseenter="preloadRouteComponent(item.name)"
          >
            <AppIcon :name="item.icon" />
            {{ item.label }}
          </RouterLink>
        </nav>

        <div class="topbar__actions">
          <span class="role-current">
            <strong>{{ roleTitle }}</strong>
            <small>{{ roleHint }}</small>
          </span>
          <RouterLink v-if="!session.isLoggedIn" class="button button--ghost" to="/auth">
            <AppIcon name="user" />
            登录
          </RouterLink>
          <span v-else class="session-user"><AppIcon name="user" />{{ session.displayName }}</span>
          <button v-if="session.isLoggedIn" class="button button--ghost" type="button" @click="logout">退出</button>
        </div>
      </div>
    </header>

    <main class="main-content" aria-live="polite">
      <slot />
    </main>

    <footer v-if="!isAuth" class="site-footer">
      <div class="site-footer__inner">
        <span>Agri-Link 农产品融销一体平台</span>
        <span>交易 · 融资 · 专家 · 后台协同</span>
      </div>
    </footer>
  </div>
</template>
