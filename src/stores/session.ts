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

  async function login(payload: { userName: string; password: string; role: UserRole | string }) {
    const data = await api.post<AuthResponse>('/api/auth/login', payload)
    const returnedUser = data.user
    persist(
      returnedUser?.userName ?? data.userName ?? payload.userName,
      returnedUser?.role ?? data.role ?? payload.role,
      returnedUser?.nickName ?? returnedUser?.realName ?? data.nickName,
    )
  }

  async function register(payload: Partial<User> & { userName: string; password: string; role: string }) {
    const data = await api.post<AuthResponse>('/api/auth/register', payload)
    const returnedUser = data.user
    persist(
      returnedUser?.userName ?? data.userName ?? payload.userName,
      returnedUser?.role ?? data.role ?? payload.role,
      returnedUser?.nickName ?? returnedUser?.realName ?? payload.nickName,
    )
  }

  function setRole(nextRole: UserRole) {
    role.value = nextRole
    writeStorage('agri-link-role', nextRole)
  }

  function logout() {
    persist('', 'FARMER', '访客')
  }

  return { userName, role, displayName, roleLabel, isLoggedIn, login, register, setRole, logout }
})
