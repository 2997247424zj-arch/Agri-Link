import { createRouter, createWebHistory } from 'vue-router'
import type { UserRole } from '@/types/domain'
import { markRouteStart } from '@/utils/performance'

// 前端路由权限与后端 SecurityConfig 保持一致。
const routeRoles: Partial<Record<string, UserRole[]>> = {
  trade: ['FARMER', 'BUYER', 'SYSTEM_ADMIN'],
  cart: ['BUYER', 'SYSTEM_ADMIN'],
  finance: ['FARMER', 'BANK', 'SYSTEM_ADMIN'],
  experts: ['FARMER', 'EXPERT', 'SYSTEM_ADMIN'],
  profile: ['FARMER', 'BUYER', 'EXPERT', 'BANK', 'SYSTEM_ADMIN'],
  admin: ['SYSTEM_ADMIN'],
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: () => import('@/views/HomeView.vue') },
    { path: '/auth', name: 'auth', component: () => import('@/views/AuthView.vue') },
    { path: '/finance', name: 'finance', component: () => import('@/views/FinanceView.vue') },
    { path: '/experts', name: 'experts', component: () => import('@/views/ExpertView.vue') },
    { path: '/trade', name: 'trade', component: () => import('@/views/TradeView.vue') },
    { path: '/cart', name: 'cart', component: () => import('@/views/CartView.vue') },
    { path: '/profile', name: 'profile', component: () => import('@/views/ProfileView.vue') },
    { path: '/admin', name: 'admin', component: () => import('@/views/AdminView.vue') },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

router.beforeEach((to) => {
  markRouteStart(to)

  // 测试或 SSR 环境没有 localStorage 时直接放行。
  if (typeof localStorage === 'undefined' || typeof to.name !== 'string') return true

  const allowedRoles = routeRoles[to.name]
  if (!allowedRoles) return true

  const userName = localStorage.getItem('agri-link-user')
  const role = localStorage.getItem('agri-link-role')
  if (!userName) return { name: 'auth', query: { redirect: to.fullPath, reason: 'login' } }
  if (allowedRoles.includes(role as UserRole)) return true

  return { name: 'auth', query: { redirect: to.fullPath, reason: 'role' } }
})

export default router
