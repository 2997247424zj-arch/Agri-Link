import { createRouter, createWebHistory } from 'vue-router'
import AuthView from '@/views/AuthView.vue'
import AdminView from '@/views/AdminView.vue'
import CartView from '@/views/CartView.vue'
import ExpertView from '@/views/ExpertView.vue'
import FinanceView from '@/views/FinanceView.vue'
import HomeView from '@/views/HomeView.vue'
import ProfileView from '@/views/ProfileView.vue'
import TradeView from '@/views/TradeView.vue'
import type { UserRole } from '@/types/domain'

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
    { path: '/', name: 'home', component: HomeView },
    { path: '/auth', name: 'auth', component: AuthView },
    { path: '/finance', name: 'finance', component: FinanceView },
    { path: '/experts', name: 'experts', component: ExpertView },
    { path: '/trade', name: 'trade', component: TradeView },
    { path: '/cart', name: 'cart', component: CartView },
    { path: '/profile', name: 'profile', component: ProfileView },
    { path: '/admin', name: 'admin', component: AdminView },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

router.beforeEach((to) => {
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
