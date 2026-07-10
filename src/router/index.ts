import { createRouter, createWebHistory } from 'vue-router'
import type { UserRole } from '@/types/domain'
import { markRouteStart } from '@/utils/performance'

const routeComponents = {
  home: () => import('@/views/HomeView.vue'),
  auth: () => import('@/views/AuthView.vue'),
  finance: () => import('@/views/FinanceView.vue'),
  experts: () => import('@/views/ExpertView.vue'),
  trade: () => import('@/views/TradeView.vue'),
  cart: () => import('@/views/CartView.vue'),
  profile: () => import('@/views/ProfileView.vue'),
  admin: () => import('@/views/AdminView.vue'),
}

// 前端路由权限与后端 SecurityConfig 保持一致；管理员统一从后台管理页进入业务监管。
export const routeRoles: Partial<Record<string, UserRole[]>> = {
  trade: ['FARMER', 'BUYER'],
  cart: ['BUYER'],
  finance: ['FARMER', 'BANK'],
  experts: ['FARMER', 'EXPERT'],
  profile: ['FARMER', 'BUYER', 'EXPERT', 'BANK', 'SYSTEM_ADMIN'],
  admin: ['SYSTEM_ADMIN'],
}

const roleHomeRoutes: Record<UserRole, string> = {
  FARMER: '/',
  BUYER: '/',
  EXPERT: '/',
  BANK: '/',
  SYSTEM_ADMIN: '/admin',
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: routeComponents.home, meta: { order: 0 } },
    { path: '/auth', name: 'auth', component: routeComponents.auth, meta: { order: 1 } },
    { path: '/trade', name: 'trade', component: routeComponents.trade, meta: { order: 2 } },
    { path: '/cart', name: 'cart', component: routeComponents.cart, meta: { order: 3 } },
    { path: '/finance', name: 'finance', component: routeComponents.finance, meta: { order: 4 } },
    { path: '/experts', name: 'experts', component: routeComponents.experts, meta: { order: 5 } },
    { path: '/profile', name: 'profile', component: routeComponents.profile, meta: { order: 6 } },
    { path: '/admin', name: 'admin', component: routeComponents.admin, meta: { order: 7 } },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
  // 处理带 #hash 的路由：滚动到锚点并预留 topbar 高度
  scrollBehavior(to, _from, savedPosition) {
    if (savedPosition) return savedPosition
    if (to.hash) {
      return { el: to.hash, top: 72, behavior: 'smooth' }
    }
    return { top: 0 }
  },
})

router.beforeEach((to) => {
  markRouteStart(to)

  // 测试或 SSR 环境没有 localStorage 时直接放行。
  if (typeof localStorage === 'undefined' || typeof to.name !== 'string') return true

  const userName = localStorage.getItem('agri-link-user')
  const role = localStorage.getItem('agri-link-role') as UserRole | null
  if (to.name === 'auth' && userName) return roleHomeRoutes[role as UserRole] ?? '/'

  const allowedRoles = routeRoles[to.name]
  if (!allowedRoles) return true

  if (!userName) return { name: 'auth', query: { redirect: to.fullPath, reason: 'login' } }
  if (allowedRoles.includes(role as UserRole)) return true

  return roleHomeRoutes[role as UserRole] ?? '/'
})

export function preloadRouteComponent(name: keyof typeof routeComponents) {
  void routeComponents[name]()
}

export default router
