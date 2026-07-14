<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import AppIcon from '@/components/AppIcon.vue'
import Pager from '@/components/ui/Pager.vue'
import { api, downloadFile } from '@/api/client'
import type { AdminOverview, Finance, Purchase, TradeOrder, User, UserRole } from '@/types/domain'

interface KnowledgeItem {
  id: number
  title: string
  category: string
  summary: string
  status?: number
  createTime?: string
}

type AdminIconName = 'home' | 'user' | 'leaf' | 'bank' | 'shield' | 'cart'
type AdminTabKey = 'overview' | 'users' | 'trade' | 'finance' | 'knowledge'

const overview = ref<AdminOverview | null>(null)
const users = ref<User[]>([])
const orders = ref<TradeOrder[]>([])
const purchases = ref<Purchase[]>([])
const finances = ref<Finance[]>([])
const knowledgeItems = ref<KnowledgeItem[]>([])
const loading = ref(true)
const message = ref('')
const error = ref('')
const userKeyword = ref('')
const orderStatusFilter = ref<'all' | '0' | '1' | '2'>('all')
const purchaseStatusFilter = ref<'all' | '0' | '1' | '2'>('all')
const financeStatusFilter = ref<'all' | '0' | '1' | '2'>('all')
const pageSize = 5
const userPage = ref(1)
const orderPage = ref(1)
const purchasePage = ref(1)
const financePage = ref(1)
const knowledgePage = ref(1)
const selectedOrderIds = ref<number[]>([])
const selectedPurchaseIds = ref<number[]>([])
const editingKnowledgeId = ref<number | null>(null)
const editingUserName = ref<string | null>(null)
const activeAdminTab = ref<AdminTabKey>('overview')

const knowledgeForm = reactive({
  title: '春耕金融服务专区上线',
  category: '平台资讯',
  summary: '围绕种子、农资和农机采购场景，提供额度匹配和申请进度管理。',
})

const knowledgeEditForm = reactive({
  title: '',
  category: '',
  summary: '',
})

const userEditForm = reactive({
  nickName: '',
  phone: '',
  identityNum: '',
  realName: '',
  address: '',
})

const roles: Array<{ value: UserRole; label: string }> = [
  { value: 'FARMER', label: '农户' },
  { value: 'BUYER', label: '买家' },
  { value: 'EXPERT', label: '专家' },
  { value: 'BANK', label: '银行' },
  { value: 'SYSTEM_ADMIN', label: '管理员' },
]

const moduleLinks: Array<{ key: AdminTabKey; label: string; icon: AdminIconName }> = [
  { key: 'overview', label: '核心指标', icon: 'home' },
  { key: 'users', label: '用户管理', icon: 'user' },
  { key: 'trade', label: '交易监管', icon: 'leaf' },
  { key: 'finance', label: '融资监管', icon: 'bank' },
  { key: 'knowledge', label: '内容管理', icon: 'shield' },
]

const fallbackOverview: AdminOverview = {
  userCount: 5,
  orderCount: 3,
  purchaseCount: 1,
  financeApplicationCount: 2,
  financingIntentionCount: 0,
  knowledgeCount: 0,
  usersByRole: { FARMER: 2, BUYER: 1, EXPERT: 1, BANK: 1 },
}

const fallbackUsers: User[] = [
  { userName: 'farmer-demo', nickName: '示范农户', role: 'FARMER', phone: '13800000001' },
  { userName: 'buyer-demo', nickName: '采购买家', role: 'BUYER', phone: '13800000002' },
  { userName: 'expert-rice', nickName: '周明', role: 'EXPERT', phone: '13800000003' },
]

const fallbackKnowledge: KnowledgeItem[] = [
  {
    id: 1,
    title: '春耕金融服务专区上线',
    category: '平台资讯',
    summary: '围绕种子、农资和农机采购场景，提供额度匹配和申请进度管理。',
    status: 1,
  },
  {
    id: 2,
    title: '绿色农产品产销对接',
    category: '交易指南',
    summary: '支持合作社发布稳定货源，买家可按品类、产地和价格快速筛选采购。',
    status: 0,
  },
]

const statCards = computed<Array<{ label: string; value: number; icon: AdminIconName; trend: string }>>(() => [
  { label: '平台用户', value: overview.value?.userCount ?? users.value.length, icon: 'user', trend: '角色统一管理' },
  { label: '货源审核', value: overview.value?.orderCount ?? orders.value.length, icon: 'leaf', trend: '农户发布监管' },
  { label: '采购订单', value: overview.value?.purchaseCount ?? purchases.value.length, icon: 'cart', trend: '买家采购流转' },
  { label: '融资申请', value: overview.value?.financeApplicationCount ?? finances.value.length, icon: 'bank', trend: '银行审批监管' },
  { label: '资讯知识', value: overview.value?.knowledgeCount ?? knowledgeItems.value.length, icon: 'shield', trend: '平台内容维护' },
])

const roleStats = computed(() =>
  roles.map((role) => ({
    ...role,
    count:
      overview.value?.usersByRole?.[role.value] ??
      users.value.filter((user) => user.role === role.value).length,
  })),
)

const filteredUsers = computed(() => {
  const text = userKeyword.value.trim().toLowerCase()
  if (!text) return users.value
  return users.value.filter((user) =>
    [user.userName, user.nickName, user.realName, user.phone, user.role].some((value) =>
      String(value ?? '').toLowerCase().includes(text),
    ),
  )
})

const filteredOrders = computed(() => {
  if (orderStatusFilter.value === 'all') return orders.value
  return orders.value.filter((order) => String(order.orderStatus ?? 0) === orderStatusFilter.value)
})

const filteredPurchases = computed(() => {
  if (purchaseStatusFilter.value === 'all') return purchases.value
  return purchases.value.filter((purchase) => String(purchase.purchaseStatus ?? 0) === purchaseStatusFilter.value)
})

const filteredFinances = computed(() => {
  if (financeStatusFilter.value === 'all') return finances.value
  return finances.value.filter((finance) => String(finance.status ?? 0) === financeStatusFilter.value)
})

const pagedUsers = computed(() => paginate(filteredUsers.value, userPage.value))
const pagedOrders = computed(() => paginate(filteredOrders.value, orderPage.value))
const pagedPurchases = computed(() => paginate(filteredPurchases.value, purchasePage.value))
const pagedFinances = computed(() => paginate(filteredFinances.value, financePage.value))
const pagedKnowledge = computed(() => paginate(knowledgeItems.value, knowledgePage.value))

function paginate<T>(items: T[], page: number) {
  const start = (page - 1) * pageSize
  return items.slice(start, start + pageSize)
}

function pageCount(total: number) {
  return Math.max(1, Math.ceil(total / pageSize))
}

function toggleSelection(list: number[], id: number) {
  return list.includes(id) ? list.filter((item) => item !== id) : [...list, id]
}

function changeUserPage(delta: number) {
  userPage.value = Math.min(Math.max(1, userPage.value + delta), pageCount(filteredUsers.value.length))
}

function changeOrderPage(delta: number) {
  orderPage.value = Math.min(Math.max(1, orderPage.value + delta), pageCount(filteredOrders.value.length))
}

function changePurchasePage(delta: number) {
  purchasePage.value = Math.min(Math.max(1, purchasePage.value + delta), pageCount(filteredPurchases.value.length))
}

function changeFinancePage(delta: number) {
  financePage.value = Math.min(Math.max(1, financePage.value + delta), pageCount(filteredFinances.value.length))
}

function changeKnowledgePage(delta: number) {
  knowledgePage.value = Math.min(Math.max(1, knowledgePage.value + delta), pageCount(knowledgeItems.value.length))
}

function approvalStatusLabel(status?: number) {
  if (status === 1) return '已通过'
  if (status === 2) return '已拒绝'
  return '待审批'
}

function purchaseStatusLabel(status?: number) {
  if (status === 1) return '已确认'
  if (status === 2) return '已取消'
  return '待确认'
}

function statusTagClass(status?: number) {
  if (status === 1) return 'tag tag--green'
  if (status === 2) return 'tag tag--red'
  return 'tag tag--amber'
}

function maskIdNum(id?: string) {
  if (!id || id.length < 8) return id ?? '-'
  return id.slice(0, 4) + '****' + id.slice(-4)
}

function confirmAction(text: string) {
  return typeof window === 'undefined' || window.confirm(text)
}

async function loadAdmin() {
  loading.value = true
  error.value = ''
  try {
    const [overviewData, userData, orderData, purchaseData, financeData, knowledgeData] = await Promise.all([
      api.get<AdminOverview>('/api/admin/overview', { role: 'SYSTEM_ADMIN' }).catch(() => fallbackOverview),
      api.get<User[]>('/api/admin/users', { role: 'SYSTEM_ADMIN' }).catch(() => fallbackUsers),
      api.get<TradeOrder[]>('/api/admin/trade/orders', { role: 'SYSTEM_ADMIN' }).catch(() => []),
      api.get<Purchase[]>('/api/admin/trade/purchases', { role: 'SYSTEM_ADMIN' }).catch(() => []),
      api.get<Finance[]>('/api/admin/finance/applications', { role: 'SYSTEM_ADMIN' }).catch(() => []),
      api.get<KnowledgeItem[]>('/api/admin/knowledge', { role: 'SYSTEM_ADMIN' }).catch(() => fallbackKnowledge),
    ])
    overview.value = overviewData
    users.value = userData?.length ? userData : fallbackUsers
    orders.value = orderData ?? []
    purchases.value = purchaseData ?? []
    finances.value = financeData ?? []
    knowledgeItems.value = knowledgeData?.length ? knowledgeData : fallbackKnowledge
  } catch (err) {
    overview.value = fallbackOverview
    users.value = fallbackUsers
    knowledgeItems.value = fallbackKnowledge
    error.value = err instanceof Error ? `后台接口暂不可用：${err.message}` : '后台接口暂不可用，已显示演示数据。'
  } finally {
    loading.value = false
  }
}

// ---- 用户管理 ----

async function toggleUserEnabled(user: User) {
  const currentlyEnabled = user.enabled !== false
  const nextEnabled = !currentlyEnabled
  const label = nextEnabled ? '启用' : '禁用'
  if (!confirmAction(`确认${label}用户 ${user.userName}？`)) return
  message.value = ''
  error.value = ''
  try {
    const updated = await api.patch<User>(
      `/api/admin/users/${encodeURIComponent(user.userName)}/enabled`,
      { enabled: nextEnabled },
      { role: 'SYSTEM_ADMIN' },
    )
    user.enabled = updated.enabled
    message.value = `已${label} ${user.userName}。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '操作失败。'
  }
}

function startEditUser(user: User) {
  editingUserName.value = user.userName
  userEditForm.nickName = user.nickName ?? ''
  userEditForm.phone = user.phone ?? ''
  userEditForm.identityNum = user.identityNum ?? ''
  userEditForm.realName = user.realName ?? ''
  userEditForm.address = user.address ?? ''
}

async function saveUser(user: User) {
  message.value = ''
  error.value = ''
  try {
    const updated = await api.put<User>(
      `/api/admin/users/${encodeURIComponent(user.userName)}`,
      { ...userEditForm },
      { role: 'SYSTEM_ADMIN' },
    )
    user.nickName = updated.nickName
    user.phone = updated.phone
    user.identityNum = updated.identityNum
    user.realName = updated.realName
    user.address = updated.address
    message.value = `已更新 ${user.userName} 的信息。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '保存失败。'
  } finally {
    editingUserName.value = null
  }
}

async function deleteUser(user: User) {
  if (!confirmAction(`确认删除用户 ${user.userName}？此操作不可恢复。`)) return
  message.value = ''
  error.value = ''
  try {
    await api.delete<void>(`/api/admin/users/${encodeURIComponent(user.userName)}`, { role: 'SYSTEM_ADMIN' })
    users.value = users.value.filter((u) => u.userName !== user.userName)
    message.value = `已删除用户 ${user.userName}。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '删除失败。'
  }
}

async function updateUserRole(user: User, role: UserRole) {
  if (!confirmAction(`确认将 ${user.userName} 的角色调整为 ${roles.find((item) => item.value === role)?.label ?? role}？`)) {
    return
  }
  message.value = ''
  error.value = ''
  try {
    const updated = await api.patch<User>(
      `/api/admin/users/${encodeURIComponent(user.userName)}/role`,
      { role },
      { role: 'SYSTEM_ADMIN' },
    )
    user.role = updated.role
    message.value = `已更新 ${user.userName} 的角色。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '角色更新失败。'
  }
}

// ---- 内容管理 ----

async function publishKnowledge() {
  message.value = ''
  error.value = ''
  const payload = { ...knowledgeForm, status: 1 }
  try {
    const created = await api.post<KnowledgeItem>('/api/admin/knowledge', payload, {
      role: 'SYSTEM_ADMIN',
    })
    knowledgeItems.value = [created, ...knowledgeItems.value]
    message.value = `资讯「${created.title}」已发布。`
  } catch {
    knowledgeItems.value = [{ id: Date.now(), ...payload }, ...knowledgeItems.value]
    message.value = `后端知识库接口暂不可用，已在当前页面发布「${payload.title}」。`
  }
}

async function updateKnowledgeStatus(item: KnowledgeItem, status: number) {
  message.value = ''
  error.value = ''
  try {
    const updated = await api.patch<KnowledgeItem>(
      `/api/admin/knowledge/${item.id}/status`,
      { status },
      { role: 'SYSTEM_ADMIN' },
    )
    item.status = updated.status
    message.value = `资讯「${item.title}」状态已更新。`
  } catch {
    item.status = status
    message.value = `后端知识库状态接口暂不可用，已在当前页面更新「${item.title}」。`
  }
}

function startEditKnowledge(item: KnowledgeItem) {
  editingKnowledgeId.value = item.id
  knowledgeEditForm.title = item.title
  knowledgeEditForm.category = item.category
  knowledgeEditForm.summary = item.summary
}

async function saveKnowledge(item: KnowledgeItem) {
  message.value = ''
  error.value = ''
  const payload = { ...knowledgeEditForm }
  try {
    const updated = await api.put<KnowledgeItem>(`/api/admin/knowledge/${item.id}`, payload, {
      role: 'SYSTEM_ADMIN',
    })
    Object.assign(item, updated)
    message.value = `资讯「${item.title}」已保存。`
  } catch {
    Object.assign(item, payload)
    message.value = `后端知识库编辑接口暂不可用，已在当前页面保存「${item.title}」。`
  } finally {
    editingKnowledgeId.value = null
  }
}

async function deleteKnowledge(item: KnowledgeItem) {
  if (!confirmAction(`确认删除资讯「${item.title}」？`)) return

  message.value = ''
  error.value = ''
  try {
    await api.delete<void>(`/api/admin/knowledge/${item.id}`, { role: 'SYSTEM_ADMIN' })
    knowledgeItems.value = knowledgeItems.value.filter((entry) => entry.id !== item.id)
    message.value = `资讯「${item.title}」已删除。`
  } catch {
    knowledgeItems.value = knowledgeItems.value.filter((entry) => entry.id !== item.id)
    message.value = `后端知识库删除接口暂不可用，已在当前页面移除「${item.title}」。`
  }
}

// ---- 交易监管 ----

async function updateOrderStatus(order: TradeOrder, orderStatus: number, shouldConfirm = true) {
  if (shouldConfirm && !confirmAction(`确认${orderStatus === 1 ? '通过' : '拒绝'}货源「${order.title}」？`)) return

  message.value = ''
  error.value = ''
  try {
    const updated = await api.patch<TradeOrder>(
      `/api/admin/trade/orders/${order.orderId}/status`,
      { orderStatus, cooperationName: order.cooperationName || order.ownName },
      { role: 'SYSTEM_ADMIN' },
    )
    order.orderStatus = updated.orderStatus
    message.value = `货源 #${order.orderId} 状态已更新。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '货源状态更新失败。'
  }
}

async function updatePurchaseStatus(purchase: Purchase, purchaseStatus: number, shouldConfirm = true) {
  if (shouldConfirm && !confirmAction(`确认${purchaseStatus === 1 ? '确认' : '取消'}采购订单 #${purchase.purchaseId}？`)) return

  message.value = ''
  error.value = ''
  try {
    const updated = await api.patch<Purchase>(
      `/api/admin/trade/purchases/${purchase.purchaseId}/status`,
      { purchaseStatus },
      { role: 'SYSTEM_ADMIN' },
    )
    purchase.purchaseStatus = updated.purchaseStatus
    message.value = `采购订单 #${purchase.purchaseId} 状态已更新。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '采购状态更新失败。'
  }
}

async function bulkUpdateOrders(status: number) {
  const targets = orders.value.filter((order) => selectedOrderIds.value.includes(order.orderId))
  if (!targets.length || !confirmAction(`确认批量${status === 1 ? '通过' : '拒绝'} ${targets.length} 条货源？`)) return
  for (const order of targets) {
    await updateOrderStatus(order, status, false)
  }
  selectedOrderIds.value = []
}

async function bulkUpdatePurchases(status: number) {
  const targets = purchases.value.filter((purchase) => selectedPurchaseIds.value.includes(purchase.purchaseId))
  if (!targets.length || !confirmAction(`确认批量${status === 1 ? '确认' : '取消'} ${targets.length} 条采购订单？`)) return
  for (const purchase of targets) {
    await updatePurchaseStatus(purchase, status, false)
  }
  selectedPurchaseIds.value = []
}

// ---- 数据导出 ----

async function exportUsers() {
  const params = userKeyword.value ? `?keyword=${encodeURIComponent(userKeyword.value)}` : ''
  try {
    await downloadFile(`/api/admin/users/export${params}`, '用户导出.xlsx')
    message.value = '用户数据已导出。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '导出失败。'
  }
}

async function exportOrders() {
  const params = orderStatusFilter.value !== 'all' ? `?status=${orderStatusFilter.value}` : ''
  try {
    await downloadFile(`/api/admin/trade/orders/export${params}`, '货源导出.xlsx')
    message.value = '货源数据已导出。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '导出失败。'
  }
}

async function exportPurchases() {
  const params = purchaseStatusFilter.value !== 'all' ? `?status=${purchaseStatusFilter.value}` : ''
  try {
    await downloadFile(`/api/admin/trade/purchases/export${params}`, '采购导出.xlsx')
    message.value = '采购数据已导出。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '导出失败。'
  }
}

async function exportFinance() {
  const params = financeStatusFilter.value !== 'all' ? `?status=${financeStatusFilter.value}` : ''
  try {
    await downloadFile(`/api/admin/finance/applications/export${params}`, '融资导出.xlsx')
    message.value = '融资数据已导出。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '导出失败。'
  }
}

onMounted(loadAdmin)
</script>

<template>
  <section class="page admin-page">
    <div id="admin-overview" class="admin-heading">
      <div>
        <span class="eyebrow"><AppIcon name="shield" />系统管理后台</span>
        <h1>农产品融销一体平台管理控制台</h1>
        <p>集中处理用户管理、交易监管、融资状态查看和平台内容维护。</p>
      </div>
      <button class="button button--ghost" type="button" @click="loadAdmin">
        <AppIcon name="search" />刷新
      </button>
    </div>

    <p v-if="message" class="alert">{{ message }}</p>
    <p v-if="error" class="alert alert--error">{{ error }}</p>

    <div class="admin-layout admin-shell-grid">
      <aside class="admin-sidebar" aria-label="管理员模块导航">
        <strong>系统管理</strong>
        <button
          v-for="item in moduleLinks"
          :key="item.key"
          class="admin-tab"
          :class="{ 'admin-tab--active': activeAdminTab === item.key }"
          type="button"
          @click="activeAdminTab = item.key"
        >
          <AppIcon :name="item.icon" />
          {{ item.label }}
        </button>
      </aside>

      <div class="admin-main admin-dashboard-grid">
    <!-- ======== 核心指标 ======== -->
    <section v-if="activeAdminTab === 'overview'" id="admin-overview-panel" class="section admin-card admin-card--overview">
      <div class="section-title">
        <div>
          <h2>核心指标</h2>
          <p>快速查看用户、交易、融资和内容的当前规模。</p>
        </div>
      </div>
      <div class="admin-stat-cards">
        <div v-for="stat in statCards" :key="stat.label" class="metric admin-stat-card">
          <div class="admin-stat-card__header">
            <span><AppIcon :name="stat.icon" /></span>
            <small>{{ loading ? '正在加载' : stat.label }}</small>
          </div>
          <strong>{{ stat.value }}</strong>
          <em>{{ stat.trend }}</em>
        </div>
      </div>
    </section>

    <!-- ======== 用户管理 ======== -->
    <section v-if="activeAdminTab === 'users'" id="admin-users" class="section admin-card">
      <div class="section-title">
        <div>
          <h2>用户管理</h2>
          <p>管理用户身份信息、角色分配和账号启用/禁用。</p>
        </div>
        <div class="toolbar">
          <label class="field compact-field">
            <span>搜索用户</span>
            <input v-model.trim="userKeyword" placeholder="账号/昵称/手机号" />
          </label>
          <button class="button button--ghost button--small" type="button" @click="exportUsers">
            <AppIcon name="download" />导出
          </button>
        </div>
      </div>
      <div class="role-stat-grid">
        <span v-for="role in roleStats" :key="role.value" class="role-stat">
          <strong>{{ role.count }}</strong>
          {{ role.label }}
        </span>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>账号</th>
              <th>昵称</th>
              <th>真实姓名</th>
              <th>身份证号</th>
              <th>手机号</th>
              <th>状态</th>
              <th>角色</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in pagedUsers" :key="user.userName">
              <template v-if="editingUserName === user.userName">
                <td>{{ user.userName }}</td>
                <td><input v-model.trim="userEditForm.nickName" class="inline-input" /></td>
                <td><input v-model.trim="userEditForm.realName" class="inline-input" /></td>
                <td><input v-model.trim="userEditForm.identityNum" class="inline-input" /></td>
                <td><input v-model.trim="userEditForm.phone" class="inline-input" /></td>
                <td><span :class="user.enabled === false ? 'tag tag--red' : 'tag tag--green'">{{ user.enabled === false ? '禁用' : '启用' }}</span></td>
                <td>
                  <select :value="user.role" @change="updateUserRole(user, ($event.target as HTMLSelectElement).value as UserRole)">
                    <option v-for="role in roles" :key="role.value" :value="role.value">{{ role.label }}</option>
                  </select>
                </td>
                <td class="toolbar">
                  <button class="button button--small" type="button" @click="saveUser(user)">保存</button>
                  <button class="button button--ghost button--small" type="button" @click="editingUserName = null">取消</button>
                </td>
              </template>
              <template v-else>
                <td>{{ user.userName }}</td>
                <td>{{ user.nickName || '-' }}</td>
                <td>{{ user.realName || '-' }}</td>
                <td>{{ maskIdNum(user.identityNum) }}</td>
                <td>{{ user.phone || '-' }}</td>
                <td>
                  <button
                    class="button button--small"
                    :class="user.enabled === false ? 'button--danger' : 'button--green'"
                    type="button"
                    @click="toggleUserEnabled(user)"
                  >
                    {{ user.enabled === false ? '禁用' : '启用' }}
                  </button>
                </td>
                <td>
                  <select :value="user.role" @change="updateUserRole(user, ($event.target as HTMLSelectElement).value as UserRole)">
                    <option v-for="role in roles" :key="role.value" :value="role.value">{{ role.label }}</option>
                  </select>
                </td>
                <td class="toolbar">
                  <button class="button button--ghost button--small" type="button" @click="startEditUser(user)">编辑</button>
                  <button class="button button--danger button--small" type="button" @click="deleteUser(user)">删除</button>
                </td>
              </template>
            </tr>
            <tr v-if="!filteredUsers.length">
              <td colspan="8">暂无符合条件的用户。</td>
            </tr>
          </tbody>
        </table>
      </div>
      <Pager :page="userPage" :page-count="pageCount(filteredUsers.length)" @change="changeUserPage" />
    </section>

    <!-- ======== 交易监管 ======== -->
    <section v-if="activeAdminTab === 'trade'" id="admin-trade" class="section admin-card-grid admin-card-grid--two">
      <div class="panel admin-card">
        <div class="section-title">
          <div>
            <h2>货源状态</h2>
            <p>处理农户发布的商品货源。</p>
          </div>
          <div class="toolbar">
            <label class="field compact-field">
              <span>状态筛选</span>
              <select v-model="orderStatusFilter">
                <option value="all">全部货源</option>
                <option value="0">待审批</option>
                <option value="1">已通过</option>
                <option value="2">已拒绝</option>
              </select>
            </label>
            <button class="button button--ghost button--small" type="button" @click="exportOrders">
              <AppIcon name="download" />导出
            </button>
          </div>
        </div>
        <div class="toolbar">
          <button class="button button--small" type="button" :disabled="!selectedOrderIds.length" @click="bulkUpdateOrders(1)">批量通过</button>
          <button class="button button--danger button--small" type="button" :disabled="!selectedOrderIds.length" @click="bulkUpdateOrders(2)">批量拒绝</button>
        </div>
        <div class="table-wrap">
          <table>
            <thead><tr><th>选择</th><th>货源</th><th>发布者</th><th>状态</th><th>操作</th></tr></thead>
            <tbody>
              <tr v-for="order in pagedOrders" :key="order.orderId">
                <td><input type="checkbox" :checked="selectedOrderIds.includes(order.orderId)" @change="selectedOrderIds = toggleSelection(selectedOrderIds, order.orderId)" /></td>
                <td>{{ order.title }}</td>
                <td>{{ order.ownName || '-' }}</td>
                <td><span :class="statusTagClass(order.orderStatus)">{{ approvalStatusLabel(order.orderStatus) }}</span></td>
                <td class="toolbar">
                  <button class="button button--small" type="button" @click="updateOrderStatus(order, 1)">通过</button>
                  <button class="button button--danger button--small" type="button" @click="updateOrderStatus(order, 2)">拒绝</button>
                </td>
              </tr>
              <tr v-if="!filteredOrders.length"><td colspan="5">暂无符合条件的货源记录。</td></tr>
            </tbody>
          </table>
        </div>
        <Pager :page="orderPage" :page-count="pageCount(filteredOrders.length)" @change="changeOrderPage" />
      </div>

      <div class="panel admin-card">
        <div class="section-title">
          <div>
            <h2>采购状态</h2>
            <p>处理买家生成的采购订单。</p>
          </div>
          <div class="toolbar">
            <label class="field compact-field">
              <span>状态筛选</span>
              <select v-model="purchaseStatusFilter">
                <option value="all">全部采购</option>
                <option value="0">待确认</option>
                <option value="1">已确认</option>
                <option value="2">已取消</option>
              </select>
            </label>
            <button class="button button--ghost button--small" type="button" @click="exportPurchases">
              <AppIcon name="download" />导出
            </button>
          </div>
        </div>
        <div class="toolbar">
          <button class="button button--small" type="button" :disabled="!selectedPurchaseIds.length" @click="bulkUpdatePurchases(1)">批量确认</button>
          <button class="button button--danger button--small" type="button" :disabled="!selectedPurchaseIds.length" @click="bulkUpdatePurchases(2)">批量取消</button>
        </div>
        <div class="table-wrap">
          <table>
            <thead><tr><th>选择</th><th>订单</th><th>买家</th><th>金额</th><th>状态</th><th>操作</th></tr></thead>
            <tbody>
              <tr v-for="purchase in pagedPurchases" :key="purchase.purchaseId">
                <td><input type="checkbox" :checked="selectedPurchaseIds.includes(purchase.purchaseId)" @change="selectedPurchaseIds = toggleSelection(selectedPurchaseIds, purchase.purchaseId)" /></td>
                <td>#{{ purchase.purchaseId }}</td>
                <td>{{ purchase.ownName }}</td>
                <td>￥{{ purchase.totalPrice ?? '-' }}</td>
                <td><span :class="statusTagClass(purchase.purchaseStatus)">{{ purchaseStatusLabel(purchase.purchaseStatus) }}</span></td>
                <td class="toolbar">
                  <button class="button button--small" type="button" @click="updatePurchaseStatus(purchase, 1)">确认</button>
                  <button class="button button--danger button--small" type="button" @click="updatePurchaseStatus(purchase, 2)">取消</button>
                </td>
              </tr>
              <tr v-if="!filteredPurchases.length"><td colspan="6">暂无符合条件的采购订单。</td></tr>
            </tbody>
          </table>
        </div>
        <Pager :page="purchasePage" :page-count="pageCount(filteredPurchases.length)" @change="changePurchasePage" />
      </div>
    </section>

    <!-- ======== 融资监管 ======== -->
    <section v-if="activeAdminTab === 'finance'" id="admin-finance" class="section admin-card">
      <div class="section-title">
        <div>
          <h2>融资监管</h2>
          <p>管理员查看银行审批进度和备注，具体通过或拒绝由银行角色处理。</p>
        </div>
        <div class="toolbar">
          <label class="field compact-field">
            <span>状态筛选</span>
            <select v-model="financeStatusFilter">
              <option value="all">全部申请</option>
              <option value="0">待审批</option>
              <option value="1">已通过</option>
              <option value="2">已拒绝</option>
            </select>
          </label>
          <button class="button button--ghost button--small" type="button" @click="exportFinance">
            <AppIcon name="download" />导出
          </button>
        </div>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>编号</th>
              <th>申请人</th>
              <th>金额</th>
              <th>状态</th>
              <th>银行备注</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="finance in pagedFinances" :key="finance.financeId">
              <td>{{ finance.financeId }}</td>
              <td>{{ finance.realName || finance.ownName }}</td>
              <td>{{ finance.money ?? '-' }}</td>
              <td><span :class="statusTagClass(finance.status)">{{ approvalStatusLabel(finance.status) }}</span></td>
              <td>{{ finance.remark || '待银行填写审批意见' }}</td>
            </tr>
            <tr v-if="!filteredFinances.length">
              <td colspan="5">暂无符合条件的融资申请。</td>
            </tr>
          </tbody>
        </table>
      </div>
      <Pager :page="financePage" :page-count="pageCount(filteredFinances.length)" @change="changeFinancePage" />
    </section>

    <!-- ======== 内容管理 ======== -->
    <section v-if="activeAdminTab === 'knowledge'" id="admin-knowledge" class="section admin-card-grid admin-card-grid--two">
      <form class="panel form admin-card" @submit.prevent="publishKnowledge">
        <div class="section-title">
          <div>
            <h2>资讯知识发布</h2>
            <p>用于平台资讯、交易指南和农业知识内容管理。</p>
          </div>
        </div>
        <label class="field"><span>标题</span><input v-model.trim="knowledgeForm.title" required /></label>
        <label class="field"><span>分类</span><input v-model.trim="knowledgeForm.category" required /></label>
        <label class="field"><span>摘要</span><textarea v-model.trim="knowledgeForm.summary" required /></label>
        <button class="button button--green" type="submit">
          <AppIcon name="plus" />发布内容
        </button>
      </form>

      <div class="panel admin-card">
        <div class="section-title">
          <div>
            <h2>内容列表</h2>
            <p>管理员可控制资讯和知识内容是否展示。</p>
          </div>
        </div>
        <div v-if="knowledgeItems.length" class="mini-list">
          <span v-for="item in pagedKnowledge" :key="item.id" class="stack-row">
            <template v-if="editingKnowledgeId === item.id">
              <label class="field"><span>标题</span><input v-model.trim="knowledgeEditForm.title" /></label>
              <label class="field"><span>分类</span><input v-model.trim="knowledgeEditForm.category" /></label>
              <label class="field"><span>摘要</span><textarea v-model.trim="knowledgeEditForm.summary" /></label>
              <div class="toolbar">
                <button class="button button--small" type="button" @click="saveKnowledge(item)">保存</button>
                <button class="button button--ghost button--small" type="button" @click="editingKnowledgeId = null">取消</button>
              </div>
            </template>
            <template v-else>
              <strong>{{ item.title }}</strong>
              <small>
                {{ item.category }}
                <span :class="statusTagClass(item.status)">{{ item.status === 1 ? '已发布' : item.status === 2 ? '已下架' : '草稿' }}</span>
              </small>
              <em>{{ item.summary }}</em>
              <div class="toolbar">
                <button class="button button--small" type="button" @click="startEditKnowledge(item)">编辑</button>
                <button class="button button--small" type="button" @click="updateKnowledgeStatus(item, 1)">发布</button>
                <button class="button button--ghost button--small" type="button" @click="updateKnowledgeStatus(item, 2)">下架</button>
                <button class="button button--danger button--small" type="button" @click="deleteKnowledge(item)">删除</button>
              </div>
            </template>
          </span>
        </div>
        <div v-else class="empty">暂无资讯或知识内容。</div>
        <Pager :page="knowledgePage" :page-count="pageCount(knowledgeItems.length)" @change="changeKnowledgePage" />
      </div>
    </section>
      </div>
    </div>
  </section>
</template>
