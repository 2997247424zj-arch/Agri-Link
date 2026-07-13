import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { AuthResponse, User, UserRole } from '@/types/domain'
import { api } from '@/api/client'

// 角色标签统一在会话层维护，供导航和页面展示复用。
const roleLabels: Record<UserRole, string> = {
  BUYER: '买家',
  FARMER: '农户',
  EXPERT: '技术专家',
  BANK: '银行',
  SYSTEM_ADMIN: '系统管理员',
}

function readStorage(key: string) {
  return typeof localStorage === 'undefined' ? '' : localStorage.getItem(key) || ''
}

function writeStorage(key: string, value: string) {
  if (typeof localStorage !== 'undefined') {
    localStorage.setItem(key, value)
  }
}

function removeStorage(key: string) {
  if (typeof localStorage !== 'undefined') {
    localStorage.removeItem(key)
  }
}

export const useSessionStore = defineStore('session', () => {
  const userName = ref(readStorage('agri-link-user'))
  const role = ref<UserRole>((readStorage('agri-link-role') as UserRole) || 'FARMER')
  const displayName = ref(readStorage('agri-link-display') || userName.value || '访客')

  const roleLabel = computed(() => roleLabels[role.value] ?? role.value)
  const isLoggedIn = computed(() => Boolean(userName.value))

  // 登录状态持久化到 localStorage，刷新后继续可用。
  function persist(nextUserName: string, nextRole: UserRole | string, nextDisplayName?: string) {
    userName.value = nextUserName
    role.value = nextRole as UserRole
    displayName.value = nextDisplayName || nextUserName
    writeStorage('agri-link-user', userName.value)
    writeStorage('agri-link-role', role.value)
    writeStorage('agri-link-display', displayName.value)
  }

  async function login(payload: { userName: string; password: string; role?: UserRole | string }) {
    // 登录默认不带 role：后端按账号识别身份并返回真实角色，前端据此持久化。
    const body = payload.role ? payload : { userName: payload.userName, password: payload.password }
    const data = await api.post<AuthResponse>('/api/auth/login', body)
    const returnedUser = data.user
    persist(
      returnedUser?.userName ?? data.userName ?? payload.userName,
      returnedUser?.role ?? data.role ?? payload.role ?? 'FARMER',
      returnedUser?.nickName ?? returnedUser?.realName ?? data.nickName,
    )
  }

  async function register(payload: Partial<User> & { userName: string; password: string; role: string }) {
    await api.post<AuthResponse>('/api/auth/register', payload)
    await login({ userName: payload.userName, password: payload.password, role: payload.role })
  }

  function setRole(nextRole: UserRole) {
    if (isLoggedIn.value) return false
    role.value = nextRole
    writeStorage('agri-link-role', nextRole)
    return true
  }

  function logout() {
    userName.value = ''
    role.value = 'FARMER'
    displayName.value = '访客'
    removeStorage('agri-link-user')
    removeStorage('agri-link-role')
    removeStorage('agri-link-display')
  }

  return { userName, role, displayName, roleLabel, isLoggedIn, login, register, setRole, logout }
})
