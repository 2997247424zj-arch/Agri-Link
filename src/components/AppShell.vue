<script setup lang="ts">
import { computed, nextTick, watch } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import { useSessionStore } from '@/stores/session'
import type { UserRole } from '@/types/domain'
import { measureRouteReady } from '@/utils/performance'

const session = useSessionStore()
const route = useRoute()
const router = useRouter()

// 顶部导航按角色过滤，避免展示不可访问入口。
const navItems = [
  { to: '/', label: '首页', icon: 'home' },
  { to: '/trade', label: '农产品交易', icon: 'leaf' },
  { to: '/finance', label: '融资服务', icon: 'bank' },
  { to: '/experts', label: '专家助力', icon: 'expert' },
  { to: '/cart', label: '购物车', icon: 'cart' },
  { to: '/profile', label: '个人中心', icon: 'user' },
  { to: '/admin', label: '后台管理', icon: 'shield' },
] as const

const roles: Array<{ value: UserRole; label: string }> = [
  { value: 'FARMER', label: '农户' },
  { value: 'BUYER', label: '买家' },
  { value: 'EXPERT', label: '专家' },
  { value: 'BANK', label: '银行' },
  { value: 'SYSTEM_ADMIN', label: '管理员' },
]

const roleHints: Record<UserRole, string> = {
  FARMER: '发布货源、申请融资、咨询专家',
  BUYER: '浏览货源、维护购物车、生成采购单',
  EXPERT: '处理问答、查看预约、沉淀农技服务',
  BANK: '匹配农户、审批融资申请',
  SYSTEM_ADMIN: '统筹用户、交易、融资与内容',
}

const isAuth = computed(() => route.name === 'auth')
const roleHint = computed(() => roleHints[session.role] ?? '请选择业务角色')

function logout() {
  session.logout()
  router.push('/auth')
}

watch(
  () => route.fullPath,
  async () => {
    await nextTick()
    measureRouteReady(route)
  },
  { immediate: true },
)
</script>

<template>
  <div class="app-shell" :class="{ 'app-shell--auth': isAuth }">
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
          <RouterLink v-for="item in navItems" :key="item.to" :to="item.to">
            <AppIcon :name="item.icon" />
            {{ item.label }}
          </RouterLink>
        </nav>

        <div class="topbar__actions">
          <span class="role-current">
            <strong>{{ session.roleLabel }}</strong>
            <small>{{ roleHint }}</small>
          </span>
          <label class="role-switcher">
            <span>角色</span>
            <select :value="session.role" @change="session.setRole(($event.target as HTMLSelectElement).value as UserRole)">
              <option v-for="role in roles" :key="role.value" :value="role.value">{{ role.label }}</option>
            </select>
          </label>
          <RouterLink class="button button--ghost" to="/auth">
            <AppIcon name="user" />
            {{ session.isLoggedIn ? session.displayName : '登录' }}
          </RouterLink>
          <button v-if="session.isLoggedIn" class="button button--ghost" type="button" @click="logout">退出</button>
        </div>
      </div>
    </header>

    <main class="main-content" aria-live="polite">
      <RouterView v-slot="{ Component, route: activeRoute }">
        <Transition name="page-slide" mode="out-in">
          <component :is="Component" :key="activeRoute.fullPath" />
        </Transition>
      </RouterView>
    </main>

    <footer v-if="!isAuth" class="site-footer">
      <div class="site-footer__inner">
        <span>Agri-Link 农产品融销一体平台</span>
        <span>交易 · 融资 · 专家 · 后台协同</span>
      </div>
    </footer>
  </div>
</template>
