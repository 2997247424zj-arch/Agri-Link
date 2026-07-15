<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { RouterLink } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import AppImage from '@/components/AppImage.vue'
import PageHeader from '@/components/ui/PageHeader.vue'
import SummaryStrip from '@/components/ui/SummaryStrip.vue'
import { api, resolveAssetUrl, uploadImage } from '@/api/client'
import { useSessionStore } from '@/stores/session'
import type { Address, Bank, Expert, Finance, Knowledge, Purchase, TradeOrder, User } from '@/types/domain'

const session = useSessionStore()
const loading = ref(true)
const saving = ref(false)
const uploadingAvatar = ref(false)
const message = ref('')
const error = ref('')
const myOrders = ref<TradeOrder[]>([])
const myPurchases = ref<Purchase[]>([])
const myFinances = ref<Finance[]>([])
const myKnowledge = ref<Knowledge[]>([])
const banks = ref<Bank[]>([])
const addresses = ref<Address[]>([])
const newPassword = ref('')

// 农户货源编辑
const editingOrderId = ref<number | null>(null)
const savingOrder = ref(false)
const orderForm = reactive({
  title: '',
  price: 0,
  content: '',
  type: '',
  address: '',
  stock: 0,
  spec: '',
  unit: '',
  minPurchase: 1,
})

// 专家知识编辑
const editingKnowledgeId = ref<number | null>(null)
const savingKnowledge = ref(false)
const knowledgeForm = reactive({
  title: '',
  content: '',
  picPath: '',
})

// 银行审批
const approvingFinanceId = ref<number | null>(null)
const financeRemark = ref('')

// 专家资料通过 /api/experts 独立持久化，登记后可被农户检索到。
const savingExpert = ref(false)
const expertExists = ref(false)
const expertForm = reactive({
  realName: '',
  phone: '',
  profession: '',
  position: '',
  belong: '',
})

// 银行贷款产品维护，与融资页共用后端 /api/finance/banks。
const savingBank = ref(false)
const editingBankId = ref<number | null>(null)
const bankForm = reactive({
  bankName: '',
  introduce: '',
  bankPhone: '',
  money: 100000,
  rate: 4.2,
  repayment: '按季付息',
})

// 收货地址簿，买家与农户均可维护。
const savingAddress = ref(false)
const addressForm = reactive({
  consignee: '',
  phone: '',
  addressDetail: '',
  isDefault: 0,
})

type ProfileActionIcon = 'leaf' | 'bank' | 'expert' | 'cart' | 'user' | 'shield' | 'plus'

// 个人中心按角色展示资料、交易、采购和融资记录。
const profile = reactive<Partial<User>>({
  userName: session.userName || 'farmer-demo',
  nickName: session.displayName || '',
  realName: '',
  phone: '',
  identityNum: '',
  address: '',
  role: session.role,
  avatar: '',
})

const fallbackOrders: TradeOrder[] = [
  { orderId: 1, title: '高山生态大米 50kg', type: '粮油', price: 5.2, ownName: 'farmer-demo', address: '龙山县', orderStatus: 0 },
]

const isFarmer = computed(() => profile.role === 'FARMER')
const isBuyer = computed(() => profile.role === 'BUYER')
const isExpert = computed(() => profile.role === 'EXPERT')
const isBank = computed(() => profile.role === 'BANK')
const canLoadOrders = computed(() => isFarmer.value)
const canLoadPurchases = computed(() => isBuyer.value || isFarmer.value)
const canLoadFinances = computed(() => isFarmer.value || isBank.value)
const canUseAddressBook = computed(() => isBuyer.value || isFarmer.value)
const totalAssets = computed(() => myOrders.value.length + myPurchases.value.length + myFinances.value.length)
const purchaseAmount = computed(() =>
  myPurchases.value.reduce((sum, purchase) => sum + Number(purchase.totalPrice ?? 0), 0),
)
const myBanks = computed(() => banks.value)

const profileSummaryCards = computed(() => {
  if (isBuyer.value) {
    return [
      { value: profile.nickName || profile.userName || '买家', label: '买家账户' },
      { value: myPurchases.value.length, label: loading.value ? '正在加载采购记录' : '采购订单' },
      { value: `¥${purchaseAmount.value.toFixed(2)}`, label: '累计采购金额' },
      { value: addresses.value.length, label: '收货地址' },
    ]
  }
  if (isExpert.value) {
    return [
      { value: profile.realName || profile.nickName || profile.userName || '专家', label: '专家账户' },
      { value: expertForm.profession || '待完善', label: '认证专业' },
      { value: myKnowledge.value.length, label: loading.value ? '正在加载知识' : '已发布知识' },
      { value: expertExists.value ? '已登记' : '待登记', label: '专家资料状态' },
    ]
  }
  if (isBank.value) {
    return [
      { value: profile.nickName || profile.userName || '机构', label: '机构账户' },
      { value: myBanks.value.length, label: loading.value ? '正在加载产品' : '在售贷款产品' },
      { value: myFinances.value.length, label: '融资申请' },
      { value: myFinances.value.filter((item) => (item.status ?? 0) === 0).length, label: '待审批' },
    ]
  }
  return [
    { value: profile.nickName || profile.userName || '-', label: session.roleLabel },
    { value: totalAssets.value, label: loading.value ? '正在加载业务记录' : '关联业务记录' },
    { value: myOrders.value.length, label: '我的货源' },
    { value: myFinances.value.length, label: '融资申请' },
  ]
})

const businessActions = computed<Array<{ to: string; label: string; icon: ProfileActionIcon; primary?: boolean }>>(() => {
  if (isFarmer.value) {
    return [
      { to: '/trade?tab=publish', label: '商品发布与管理', icon: 'leaf', primary: true },
      { to: '/trade?tab=browse', label: '货源浏览', icon: 'leaf' },
      { to: '/finance?tab=apply', label: '融资申请', icon: 'bank' },
      { to: '/experts?tab=experts', label: '专家咨询', icon: 'expert' },
    ]
  }
  if (isBuyer.value) {
    return [
      { to: '/trade', label: '继续选品', icon: 'leaf', primary: true },
      { to: '/cart', label: '采购中心', icon: 'cart' },
    ]
  }
  if (isExpert.value) {
    return [
      { to: '/experts?tab=records', label: '咨询工单', icon: 'expert', primary: true },
      { to: '/experts?tab=knowledge', label: '知识发布', icon: 'plus' },
    ]
  }
  if (isBank.value) {
    return [
      { to: '/finance?tab=result', label: '融资审核', icon: 'bank', primary: true },
      { to: '/finance?tab=products', label: '贷款产品维护', icon: 'plus' },
    ]
  }
  return [{ to: '/admin', label: '后台管理', icon: 'shield', primary: true }]
})

function approvalStatusLabel(status?: number) {
  if (status === 1) return '已通过'
  if (status === 2) return '已拒绝'
  return '待审批'
}

function orderStatusLabel(status?: number) {
  if (status === 1) return '已上架'
  if (status === 2) return '已下架'
  return '待审核'
}

function purchaseStatusLabel(status?: number) {
  if (status === 1) return '已确认'
  if (status === 2) return '已取消'
  if (status === 3) return '已发货'
  if (status === 4) return '已收货'
  if (status === 5) return '已完成'
  return '待确认'
}

function statusTagClass(status?: number) {
  if (status === 1) return 'tag tag--green'
  if (status === 2) return 'tag tag--red'
  return 'tag tag--amber'
}

function avatarSrc(src?: string) {
  if (!src) return ''
  return resolveAssetUrl(src.startsWith('/') || src.startsWith('http') || src.startsWith('data:') ? src : `/file/avatar/${src}`)
}

async function handleAvatarFile(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  uploadingAvatar.value = true
  message.value = ''
  error.value = ''
  try {
    const uploaded = await uploadImage(file, session.role)
    profile.avatar = uploaded.url
    message.value = `头像「${uploaded.originalName}」已上传，请保存个人资料。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '头像上传失败。'
  } finally {
    uploadingAvatar.value = false
    input.value = ''
  }
}

function applyUser(user?: User) {
  if (!user) return
  profile.userName = user.userName
  profile.nickName = user.nickName
  profile.realName = user.realName
  profile.phone = user.phone
  profile.identityNum = user.identityNum
  profile.address = user.address
  profile.role = user.role
  profile.avatar = user.avatar
}

function applyExpert(expert?: Expert | null) {
  if (!expert) {
    expertExists.value = false
    return
  }
  expertExists.value = true
  expertForm.realName = expert.realName ?? ''
  expertForm.phone = expert.phone ?? ''
  expertForm.profession = expert.profession ?? ''
  expertForm.position = expert.position ?? ''
  expertForm.belong = expert.belong ?? ''
}

// 先读取用户资料，再按角色补充业务数据。
async function loadProfile() {
  loading.value = true
  error.value = ''
  const userName = profile.userName || 'farmer-demo'
  try {
    const user = await api.get<User>(`/api/users/${encodeURIComponent(userName)}`)
    applyUser(user)
    const resolvedUserName = user.userName || userName
    const [orders, purchases, finances, addressList, bankList, knowledge, expert] = await Promise.all([
      canLoadOrders.value
        ? api.get<TradeOrder[]>(`/api/trade/orders/owners/${encodeURIComponent(resolvedUserName)}`).catch(() => [])
        : Promise.resolve([]),
      canLoadPurchases.value
        ? api.get<Purchase[]>(`/api/trade/purchases/owners/${encodeURIComponent(resolvedUserName)}`).catch(() => [])
        : Promise.resolve([]),
      canLoadFinances.value ? api.get<Finance[]>('/api/finance/applications').catch(() => []) : Promise.resolve([]),
      canUseAddressBook.value
        ? api.get<Address[]>(`/api/addresses/owners/${encodeURIComponent(resolvedUserName)}`).catch(() => [])
        : Promise.resolve([]),
      isBank.value ? api.get<Bank[]>('/api/finance/banks').catch(() => []) : Promise.resolve([]),
      isExpert.value ? api.get<Knowledge[]>('/api/knowledge').catch(() => []) : Promise.resolve([]),
      isExpert.value ? api.get<Expert>(`/api/experts/${encodeURIComponent(resolvedUserName)}`).catch(() => null) : Promise.resolve(null),
    ])
    myOrders.value = orders?.length ? orders : canLoadOrders.value ? fallbackOrders : []
    myPurchases.value = purchases ?? []
    // 银行看全部融资申请，农户只看本人申请。
    myFinances.value = isBank.value
      ? finances ?? []
      : (finances ?? []).filter((item) => item.ownName === resolvedUserName)
    addresses.value = addressList ?? []
    banks.value = bankList ?? []
    myKnowledge.value = (knowledge ?? []).filter((item) => (item.ownName || item.userName) === resolvedUserName)
    applyExpert(expert)
    if (isExpert.value && !expertForm.realName) expertForm.realName = user.realName || user.nickName || ''
    if (isExpert.value && !expertForm.phone) expertForm.phone = user.phone || ''
  } catch (err) {
    myOrders.value = canLoadOrders.value ? fallbackOrders : []
    myPurchases.value = []
    myFinances.value = []
    error.value = err instanceof Error ? `后端暂不可用：${err.message}` : '后端暂不可用，已显示演示资料。'
  } finally {
    loading.value = false
  }
}

async function saveProfile() {
  saving.value = true
  message.value = ''
  error.value = ''
  try {
    const userName = String(profile.userName)
    // 后端 updateUser 会保留原密码与原角色，普通资料更新不再需要重复填写当前密码。
    const saved = await api.put<User>(`/api/users/${encodeURIComponent(userName)}`, {
      userName,
      // password 为 UserRequest 的 @NotBlank 字段，占位值仅用于通过校验，后端会忽略并保留原密码。
      password: 'unchanged',
      nickName: profile.nickName,
      phone: profile.phone,
      identityNum: profile.identityNum,
      address: profile.address,
      role: profile.role,
      avatar: profile.avatar,
      realName: profile.realName,
    })
    applyUser(saved)
    if (newPassword.value.trim()) {
      await api.patch<User>(`/api/users/${encodeURIComponent(userName)}/password`, {
        password: newPassword.value.trim(),
      })
      newPassword.value = ''
      message.value = '个人资料与登录密码已更新。'
    } else {
      message.value = '个人资料已保存。'
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : '个人资料保存失败。'
  } finally {
    saving.value = false
  }
}

// ---- 专家资料维护（新增：可被农户检索到的公开专家档案）----
async function saveExpertProfile() {
  savingExpert.value = true
  message.value = ''
  error.value = ''
  const userName = String(profile.userName)
  const payload = {
    userName,
    realName: expertForm.realName,
    phone: expertForm.phone,
    profession: expertForm.profession,
    position: expertForm.position,
    belong: expertForm.belong,
  }
  try {
    const saved = expertExists.value
      ? await api.put<Expert>(`/api/experts/${encodeURIComponent(userName)}`, payload, { role: 'EXPERT' })
      : await api.post<Expert>('/api/experts', payload, { role: 'EXPERT' })
    applyExpert(saved)
    message.value = '专家资料已保存，农户端可在专家列表检索到您。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '专家资料保存失败。'
  } finally {
    savingExpert.value = false
  }
}

// ---- 银行贷款产品维护（新增）----
function resetBankForm() {
  editingBankId.value = null
  Object.assign(bankForm, { bankName: '', introduce: '', bankPhone: '', money: 100000, rate: 4.2, repayment: '按季付息' })
}

function startEditBank(bank: Bank) {
  editingBankId.value = bank.bankId
  bankForm.bankName = bank.bankName
  bankForm.introduce = bank.introduce ?? ''
  bankForm.bankPhone = bank.bankPhone ?? ''
  bankForm.money = Number(bank.money ?? 0)
  bankForm.rate = Number(bank.rate ?? 0)
  bankForm.repayment = bank.repayment ?? '按季付息'
}

async function saveBank() {
  savingBank.value = true
  message.value = ''
  error.value = ''
  try {
    if (editingBankId.value) {
      const updated = await api.put<Bank>(`/api/finance/banks/${editingBankId.value}`, { ...bankForm }, { role: 'BANK' })
      const target = banks.value.find((item) => item.bankId === editingBankId.value)
      if (target) Object.assign(target, updated)
      message.value = `贷款产品「${updated.bankName}」已更新。`
    } else {
      const created = await api.post<Bank>('/api/finance/banks', { ...bankForm }, { role: 'BANK' })
      banks.value = [created, ...banks.value]
      message.value = `贷款产品「${created.bankName}」已发布。`
    }
    resetBankForm()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '贷款产品保存失败。'
  } finally {
    savingBank.value = false
  }
}

async function deleteBank(bank: Bank) {
  if (typeof window !== 'undefined' && !window.confirm(`确认删除贷款产品「${bank.bankName}」？`)) return
  message.value = ''
  error.value = ''
  try {
    await api.delete<void>(`/api/finance/banks/${bank.bankId}`, { role: 'BANK' })
    banks.value = banks.value.filter((item) => item.bankId !== bank.bankId)
    if (editingBankId.value === bank.bankId) resetBankForm()
    message.value = `贷款产品「${bank.bankName}」已删除。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '贷款产品删除失败。'
  }
}

// ---- 收货地址簿（新增到个人中心，买家与农户共用）----
async function createAddress() {
  savingAddress.value = true
  message.value = ''
  error.value = ''
  try {
    const created = await api.post<Address>(
      '/api/addresses',
      { ownName: profile.userName || session.userName, ...addressForm },
      { role: session.role },
    )
    if (created.isDefault === 1) addresses.value.forEach((item) => { item.isDefault = 0 })
    addresses.value = [created, ...addresses.value]
    Object.assign(addressForm, { consignee: '', phone: '', addressDetail: '', isDefault: 0 })
    message.value = '收货地址已新增。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '收货地址新增失败。'
  } finally {
    savingAddress.value = false
  }
}

async function setDefaultAddress(item: Address) {
  message.value = ''
  error.value = ''
  try {
    const updated = await api.put<Address>(`/api/addresses/${item.id}`, { ...item, isDefault: 1 }, { role: session.role })
    addresses.value.forEach((addressItem) => { addressItem.isDefault = addressItem.id === item.id ? 1 : 0 })
    Object.assign(item, updated)
    message.value = '默认收货地址已更新。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '默认地址更新失败。'
  }
}

async function deleteAddress(item: Address) {
  if (typeof window !== 'undefined' && !window.confirm(`确认删除「${item.addressDetail}」？`)) return
  message.value = ''
  error.value = ''
  try {
    await api.delete<void>(`/api/addresses/${item.id}`, { role: session.role })
    addresses.value = addresses.value.filter((addressItem) => addressItem.id !== item.id)
    message.value = '收货地址已删除。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '收货地址删除失败。'
  }
}

// ---- 专家知识管理 ----
async function deleteKnowledge(item: Knowledge) {
  if (typeof window !== 'undefined' && !window.confirm(`确认删除知识「${item.title}」？`)) return
  message.value = ''
  error.value = ''
  try {
    await api.delete<void>(`/api/knowledge/${item.knowledgeId}`, { role: 'EXPERT' })
    myKnowledge.value = myKnowledge.value.filter((knowledge) => knowledge.knowledgeId !== item.knowledgeId)
    message.value = '农业知识已删除。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '农业知识删除失败。'
  }
}

function startEditKnowledge(item: Knowledge) {
  editingKnowledgeId.value = item.knowledgeId
  knowledgeForm.title = item.title
  knowledgeForm.content = item.content || ''
  knowledgeForm.picPath = item.picPath || ''
}

function cancelEditKnowledge() {
  editingKnowledgeId.value = null
  Object.assign(knowledgeForm, { title: '', content: '', picPath: '' })
}

async function saveKnowledge() {
  savingKnowledge.value = true
  message.value = ''
  error.value = ''
  try {
    const updated = await api.put<Knowledge>(
      `/api/knowledge/${editingKnowledgeId.value}`,
      { ...knowledgeForm, ownName: profile.userName },
      { role: 'EXPERT' },
    )
    const target = myKnowledge.value.find((item) => item.knowledgeId === editingKnowledgeId.value)
    if (target) Object.assign(target, updated)
    message.value = `知识「${updated.title}」已更新。`
    cancelEditKnowledge()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '知识更新失败。'
  } finally {
    savingKnowledge.value = false
  }
}

// ---- 农户货源管理（个人中心内联编辑/删除/上下架）----
function startEditOrder(order: TradeOrder) {
  editingOrderId.value = order.orderId
  orderForm.title = order.title
  orderForm.price = order.price ?? 0
  orderForm.content = order.content || ''
  orderForm.type = order.type || ''
  orderForm.address = order.address || ''
  orderForm.stock = order.stock ?? 0
  orderForm.spec = order.spec || ''
  orderForm.unit = order.unit || ''
  orderForm.minPurchase = order.minPurchase ?? 1
}

function cancelEditOrder() {
  editingOrderId.value = null
  Object.assign(orderForm, { title: '', price: 0, content: '', type: '', address: '', stock: 0, spec: '', unit: '', minPurchase: 1 })
}

async function saveOrder() {
  savingOrder.value = true
  message.value = ''
  error.value = ''
  try {
    const updated = await api.put<TradeOrder>(
      `/api/trade/orders/${editingOrderId.value}`,
      { ...orderForm, ownName: profile.userName },
      { role: 'FARMER' },
    )
    const target = myOrders.value.find((item) => item.orderId === editingOrderId.value)
    if (target) Object.assign(target, updated)
    message.value = `货源「${updated.title}」已更新。`
    cancelEditOrder()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '货源更新失败。'
  } finally {
    savingOrder.value = false
  }
}

async function deleteOrder(order: TradeOrder) {
  if (typeof window !== 'undefined' && !window.confirm(`确认删除货源「${order.title}」？`)) return
  message.value = ''
  error.value = ''
  try {
    await api.delete<void>(`/api/trade/orders/${order.orderId}`, { role: 'FARMER' })
    myOrders.value = myOrders.value.filter((item) => item.orderId !== order.orderId)
    message.value = `货源「${order.title}」已删除。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '货源删除失败。'
  }
}

async function updateOrderStatus(order: TradeOrder, newStatus: number) {
  message.value = ''
  error.value = ''
  try {
    await api.patch<void>(`/api/trade/orders/${order.orderId}/status`, { orderStatus: newStatus }, { role: 'FARMER' })
    order.orderStatus = newStatus
    message.value = `货源「${order.title}」状态已更新为${orderStatusLabel(newStatus)}。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '状态更新失败。'
  }
}

// ---- 采购订单状态管理（买家取消/确认收货，农户确认/发货）----
async function updatePurchaseStatus(purchase: Purchase, newStatus: number, extra?: { cancelReason?: string; deliveryNo?: string }) {
  message.value = ''
  error.value = ''
  try {
    await api.patch<void>(
      `/api/trade/purchases/${purchase.purchaseId}/status`,
      { purchaseStatus: newStatus, ...extra },
      { role: session.role },
    )
    purchase.purchaseStatus = newStatus
    message.value = `采购单 #${purchase.purchaseId} 状态已更新为${purchaseStatusLabel(newStatus)}。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '采购单状态更新失败。'
  }
}

async function cancelPurchase(purchase: Purchase) {
  const reason = typeof window !== 'undefined' ? window.prompt('请填写取消原因（可选）：') : ''
  if (reason === null) return
  await updatePurchaseStatus(purchase, 2, { cancelReason: reason || undefined })
}

async function confirmPurchase(purchase: Purchase) {
  await updatePurchaseStatus(purchase, 1)
}

async function shipPurchase(purchase: Purchase) {
  const deliveryNo = typeof window !== 'undefined' ? window.prompt('请输入物流单号：') : ''
  if (!deliveryNo) return
  await updatePurchaseStatus(purchase, 3, { deliveryNo })
}

async function confirmReceipt(purchase: Purchase) {
  await updatePurchaseStatus(purchase, 4)
}

// ---- 银行审批融资申请（个人中心内联审批）----
async function approveFinance(finance: Finance) {
  approvingFinanceId.value = finance.financeId
  message.value = ''
  error.value = ''
  try {
    await api.patch<void>(
      `/api/finance/applications/${finance.financeId}/status`,
      { status: 1, remark: financeRemark.value || undefined },
      { role: 'BANK' },
    )
    finance.status = 1
    finance.remark = financeRemark.value || finance.remark
    message.value = `融资申请 #${finance.financeId} 已通过。`
    financeRemark.value = ''
    approvingFinanceId.value = null
  } catch (err) {
    error.value = err instanceof Error ? err.message : '审批操作失败。'
  }
}

async function rejectFinance(finance: Finance) {
  const remark = typeof window !== 'undefined' ? window.prompt('请填写拒绝原因：') : ''
  if (!remark) return
  message.value = ''
  error.value = ''
  try {
    await api.patch<void>(
      `/api/finance/applications/${finance.financeId}/status`,
      { status: 2, remark },
      { role: 'BANK' },
    )
    finance.status = 2
    finance.remark = remark
    message.value = `融资申请 #${finance.financeId} 已拒绝。`
    approvingFinanceId.value = null
  } catch (err) {
    error.value = err instanceof Error ? err.message : '审批操作失败。'
  }
}

async function deleteFinance(finance: Finance) {
  if (typeof window !== 'undefined' && !window.confirm(`确认删除融资申请 #${finance.financeId}？`)) return
  message.value = ''
  error.value = ''
  try {
    await api.delete<void>(`/api/finance/applications/${finance.financeId}`, { role: session.role })
    myFinances.value = myFinances.value.filter((item) => item.financeId !== finance.financeId)
    message.value = `融资申请 #${finance.financeId} 已删除。`
  } catch (err) {
    error.value = err instanceof Error ? err.message : '融资申请删除失败。'
  }
}

onMounted(loadProfile)
</script>

<template>
  <section class="page">
    <PageHeader eyebrow="个人中心" icon="user" title="资料维护与业务操作" desc="维护账号资料，并按角色管理货源、地址、专家档案、贷款产品与业务记录。">
      <template #actions>
        <button class="button button--ghost" type="button" @click="loadProfile">
          <AppIcon name="search" />刷新
        </button>
      </template>
    </PageHeader>

    <p v-if="message" class="alert">{{ message }}</p>
    <p v-if="error" class="alert alert--error">{{ error }}</p>

    <SummaryStrip
      :items="profileSummaryCards"
      :extra-class="isBuyer ? 'profile-summary-strip profile-summary-strip--buyer' : 'profile-summary-strip'"
    />

    <section class="section profile-workspace">
      <form class="panel form" @submit.prevent="saveProfile">
        <div class="section-title">
          <div>
            <h2>基础资料</h2>
            <p>用于登录、融资申请、交易发布和专家服务识别。</p>
          </div>
        </div>
        <label class="field"><span>账号</span><input v-model.trim="profile.userName" disabled /></label>
        <label class="field"><span>角色</span><input :value="session.roleLabel" disabled /></label>
        <label class="field"><span>{{ uploadingAvatar ? '头像上传中' : '从电脑上传头像' }}</span><input type="file" accept="image/*" :disabled="uploadingAvatar" @change="handleAvatarFile" /></label>
        <div v-if="profile.avatar" class="avatar-preview">
          <AppImage :src="avatarSrc(profile.avatar)" fallback-src="/file/avatar/avatar.png" alt="头像预览" ratio="1 / 1" icon="user" />
        </div>
        <label class="field"><span>昵称</span><input v-model.trim="profile.nickName" /></label>
        <label class="field"><span>真实姓名</span><input v-model.trim="profile.realName" /></label>
        <label class="field"><span>手机号</span><input v-model.trim="profile.phone" type="tel" /></label>
        <label class="field"><span>身份号码</span><input v-model.trim="profile.identityNum" /></label>
        <label class="field"><span>地址</span><textarea v-model.trim="profile.address" /></label>
        <div class="panel-lite">
          <h3>登录密码</h3>
          <label class="field"><span>新密码</span><input v-model="newPassword" type="password" autocomplete="new-password" placeholder="不修改可留空" /></label>
          <p class="field-hint">仅在填写新密码时更新登录密码，其余资料保存不影响密码。</p>
        </div>
        <button class="button button--green" type="submit" :disabled="saving || uploadingAvatar">
          <AppIcon name="check" />{{ saving ? '保存中' : '保存资料' }}
        </button>
      </form>

      <div class="profile-side">
        <!-- 专家资料维护 -->
        <form v-if="isExpert" class="panel form" @submit.prevent="saveExpertProfile">
          <div class="section-title">
            <div>
              <h2>专家档案维护</h2>
              <p>{{ expertExists ? '已登记，农户可在专家列表检索到您。' : '尚未登记专家档案，填写后农户才能预约咨询。' }}</p>
            </div>
            <span :class="expertExists ? 'tag tag--green' : 'tag tag--amber'">{{ expertExists ? '已登记' : '待登记' }}</span>
          </div>
          <label class="field"><span>真实姓名</span><input v-model.trim="expertForm.realName" required /></label>
          <label class="field"><span>联系电话</span><input v-model.trim="expertForm.phone" type="tel" required /></label>
          <label class="field"><span>专业领域</span><input v-model.trim="expertForm.profession" placeholder="如 水稻病虫害防治" /></label>
          <label class="field"><span>职称/职务</span><input v-model.trim="expertForm.position" placeholder="如 高级农艺师" /></label>
          <label class="field"><span>所属机构</span><input v-model.trim="expertForm.belong" placeholder="如 湘西农业技术站" /></label>
          <button class="button button--green" type="submit" :disabled="savingExpert">
            <AppIcon name="check" />{{ savingExpert ? '保存中' : (expertExists ? '更新专家资料' : '登记专家资料') }}
          </button>
        </form>

        <!-- 银行贷款产品维护 -->
        <form v-if="isBank" class="panel form" @submit.prevent="saveBank">
          <div class="section-title">
            <div>
              <h2>{{ editingBankId ? '编辑贷款产品' : '新增贷款产品' }}</h2>
              <p>维护对外展示的贷款产品，农户可在融资页选择申请。</p>
            </div>
          </div>
          <label class="field"><span>产品名称</span><input v-model.trim="bankForm.bankName" required /></label>
          <label class="field"><span>联系电话</span><input v-model.trim="bankForm.bankPhone" type="tel" required /></label>
          <label class="field"><span>可贷额度(元)</span><input v-model.number="bankForm.money" type="number" min="0.01" step="0.01" required /></label>
          <label class="field"><span>年利率(%)</span><input v-model.number="bankForm.rate" type="number" min="0" step="0.01" required /></label>
          <label class="field"><span>还款方式</span><input v-model.trim="bankForm.repayment" required placeholder="如 按季付息" /></label>
          <label class="field"><span>产品介绍</span><textarea v-model.trim="bankForm.introduce" /></label>
          <div class="toolbar">
            <button class="button button--green" type="submit" :disabled="savingBank">
              <AppIcon name="check" />{{ savingBank ? '保存中' : (editingBankId ? '保存修改' : '发布产品') }}
            </button>
            <button v-if="editingBankId" class="button button--ghost" type="button" @click="resetBankForm">取消</button>
          </div>
        </form>

        <!-- 收货地址簿：买家与农户 -->
        <form v-if="canUseAddressBook" class="panel form" @submit.prevent="createAddress">
          <div class="section-title">
            <div>
              <h2>新增收货地址</h2>
              <p>地址簿用于采购结算，可设置默认地址。</p>
            </div>
          </div>
          <label class="field"><span>收货人</span><input v-model.trim="addressForm.consignee" required /></label>
          <label class="field"><span>联系电话</span><input v-model.trim="addressForm.phone" type="tel" required /></label>
          <label class="field"><span>详细地址</span><input v-model.trim="addressForm.addressDetail" required /></label>
          <label class="check-field"><input v-model="addressForm.isDefault" type="checkbox" :true-value="1" :false-value="0" />设为默认地址</label>
          <button class="button button--green" type="submit" :disabled="savingAddress">
            <AppIcon name="plus" />{{ savingAddress ? '保存中' : '新增地址' }}
          </button>
        </form>

        <div class="panel profile-actions">
          <div class="section-title">
            <div>
              <h2>业务入口</h2>
              <p>按当前角色快速进入对应业务。</p>
            </div>
          </div>
          <div class="action-grid">
            <RouterLink
              v-for="action in businessActions"
              :key="action.to"
              class="button"
              :class="action.primary ? 'button--green' : 'button--ghost'"
              :to="action.to"
            >
              <AppIcon :name="action.icon" />{{ action.label }}
            </RouterLink>
          </div>
        </div>
      </div>
    </section>

    <!-- 收货地址列表 -->
    <section v-if="canUseAddressBook && addresses.length" class="section panel">
      <div class="section-title">
        <div>
          <h2>我的收货地址</h2>
          <p>共 {{ addresses.length }} 条地址，可设为默认或删除。</p>
        </div>
      </div>
      <div class="mini-list">
        <span v-for="item in addresses" :key="item.id" class="stack-row">
          <strong>{{ item.consignee }} · {{ item.phone }}
            <span v-if="item.isDefault === 1" class="tag tag--green">默认</span>
          </strong>
          <small>{{ item.addressDetail }}</small>
          <div class="toolbar">
            <button v-if="item.isDefault !== 1" class="button button--small" type="button" @click="setDefaultAddress(item)">设为默认</button>
            <button class="button button--danger button--small" type="button" @click="deleteAddress(item)">删除</button>
          </div>
        </span>
      </div>
    </section>

    <!-- 银行贷款产品列表 -->
    <section v-if="isBank" class="section panel">
      <div class="section-title">
        <div>
          <h2>我的贷款产品</h2>
          <p>共 {{ myBanks.length }} 款产品，可编辑或下架。</p>
        </div>
      </div>
      <div class="mini-list">
        <span v-for="bank in myBanks" :key="bank.bankId" class="stack-row">
          <strong>{{ bank.bankName }} · 年利率 {{ bank.rate ?? '-' }}%</strong>
          <small>额度 ¥{{ bank.money ?? '-' }} · {{ bank.repayment || '还款方式待定' }} · {{ bank.introduce || '暂无介绍' }}</small>
          <div class="toolbar">
            <button class="button button--small" type="button" @click="startEditBank(bank)">编辑</button>
            <button class="button button--danger button--small" type="button" @click="deleteBank(bank)">删除</button>
          </div>
        </span>
        <span v-if="!myBanks.length">暂无贷款产品，请在上方新增。</span>
      </div>
    </section>

    <!-- 专家知识列表 -->
    <section v-if="isExpert" class="section panel">
      <div class="section-title">
        <div>
          <h2>我发布的农业知识</h2>
          <p>共 {{ myKnowledge.length }} 条，可编辑、删除或前往专家页发布新内容。</p>
        </div>
        <RouterLink class="button button--ghost button--small" to="/experts?tab=knowledge"><AppIcon name="plus" />发布新知识</RouterLink>
      </div>
      <!-- 知识编辑表单 -->
      <form v-if="editingKnowledgeId" class="form inline-edit-form" @submit.prevent="saveKnowledge">
        <label class="field"><span>标题</span><input v-model.trim="knowledgeForm.title" required /></label>
        <label class="field"><span>正文</span><textarea v-model.trim="knowledgeForm.content" required /></label>
        <label class="field"><span>配图路径</span><input v-model.trim="knowledgeForm.picPath" placeholder="/file/order/xxx.jpg" /></label>
        <div class="toolbar">
          <button class="button button--green button--small" type="submit" :disabled="savingKnowledge">{{ savingKnowledge ? '保存中' : '保存修改' }}</button>
          <button class="button button--ghost button--small" type="button" @click="cancelEditKnowledge">取消</button>
        </div>
      </form>
      <div class="mini-list">
        <span v-for="item in myKnowledge" :key="item.knowledgeId" class="stack-row">
          <strong>{{ item.title }}</strong>
          <small>{{ item.content || '暂无正文' }}</small>
          <div class="toolbar">
            <button class="button button--small" type="button" @click="startEditKnowledge(item)">编辑</button>
            <button class="button button--danger button--small" type="button" @click="deleteKnowledge(item)">删除</button>
          </div>
        </span>
        <span v-if="!myKnowledge.length">暂无已发布知识。</span>
      </div>
    </section>

    <!-- 农户货源管理 -->
    <section v-if="canLoadOrders" class="section panel">
      <div class="section-title">
        <div>
          <h3>我的农产品</h3>
          <p>共 {{ myOrders.length }} 条货源，可编辑、上下架或删除。</p>
        </div>
        <RouterLink class="button button--ghost button--small" to="/trade?tab=publish"><AppIcon name="plus" />发布新货源</RouterLink>
      </div>
      <!-- 货源编辑表单 -->
      <form v-if="editingOrderId" class="form inline-edit-form" @submit.prevent="saveOrder">
        <label class="field"><span>标题</span><input v-model.trim="orderForm.title" required /></label>
        <label class="field"><span>单价(元)</span><input v-model.number="orderForm.price" type="number" min="0.01" step="0.01" required /></label>
        <label class="field"><span>类型</span><input v-model.trim="orderForm.type" placeholder="如 粮油、水果" required /></label>
        <label class="field"><span>库存</span><input v-model.number="orderForm.stock" type="number" min="0" /></label>
        <label class="field"><span>规格</span><input v-model.trim="orderForm.spec" placeholder="如 50kg/袋" /></label>
        <label class="field"><span>单位</span><input v-model.trim="orderForm.unit" placeholder="如 斤、箱" /></label>
        <label class="field"><span>起购量</span><input v-model.number="orderForm.minPurchase" type="number" min="1" /></label>
        <label class="field"><span>发货地址</span><input v-model.trim="orderForm.address" /></label>
        <label class="field"><span>描述</span><textarea v-model.trim="orderForm.content" /></label>
        <div class="toolbar">
          <button class="button button--green button--small" type="submit" :disabled="savingOrder">{{ savingOrder ? '保存中' : '保存修改' }}</button>
          <button class="button button--ghost button--small" type="button" @click="cancelEditOrder">取消</button>
        </div>
      </form>
      <div class="mini-list">
        <span v-for="order in myOrders" :key="order.orderId" class="stack-row">
          <strong>{{ order.title }} · ￥{{ order.price ?? '-' }}/{{ order.unit || '斤' }}</strong>
          <small>
            <span :class="statusTagClass(order.orderStatus)">{{ orderStatusLabel(order.orderStatus) }}</span>
            · 库存 {{ order.stock ?? '-' }} · {{ order.type || '未分类' }}
          </small>
          <div class="toolbar">
            <button v-if="order.orderStatus === 0" class="button button--small" type="button" @click="updateOrderStatus(order, 1)">上架</button>
            <button v-if="order.orderStatus === 1" class="button button--small" type="button" @click="updateOrderStatus(order, 2)">下架</button>
            <button v-if="order.orderStatus === 2" class="button button--small" type="button" @click="updateOrderStatus(order, 1)">重新上架</button>
            <button class="button button--small" type="button" @click="startEditOrder(order)">编辑</button>
            <button class="button button--danger button--small" type="button" @click="deleteOrder(order)">删除</button>
          </div>
        </span>
        <span v-if="!myOrders.length">暂无货源记录。</span>
      </div>
    </section>

    <!-- 采购记录管理 -->
    <section v-if="canLoadPurchases" class="section panel">
      <div class="section-title">
        <div>
          <h3>采购记录</h3>
          <p>共 {{ myPurchases.length }} 笔采购，{{ isBuyer ? '可取消或确认收货。' : '可确认或发货。' }}</p>
        </div>
      </div>
      <div class="mini-list">
        <span v-for="purchase in myPurchases" :key="purchase.purchaseId" class="stack-row">
          <strong>#{{ purchase.purchaseId }} · ￥{{ purchase.totalPrice ?? '-' }}</strong>
          <small>
            <span :class="statusTagClass(purchase.purchaseStatus)">{{ purchaseStatusLabel(purchase.purchaseStatus) }}</span>
            <template v-if="purchase.deliveryNo"> · 物流 {{ purchase.deliveryNo }}</template>
            <template v-if="purchase.cancelReason"> · 原因：{{ purchase.cancelReason }}</template>
          </small>
          <div class="toolbar">
            <!-- 买家操作 -->
            <template v-if="isBuyer">
              <button v-if="purchase.purchaseStatus === 0" class="button button--small" type="button" @click="cancelPurchase(purchase)">取消</button>
              <button v-if="purchase.purchaseStatus === 3" class="button button--green button--small" type="button" @click="confirmReceipt(purchase)">确认收货</button>
            </template>
            <!-- 农户操作 -->
            <template v-if="isFarmer">
              <button v-if="purchase.purchaseStatus === 0" class="button button--green button--small" type="button" @click="confirmPurchase(purchase)">确认订单</button>
              <button v-if="purchase.purchaseStatus === 1" class="button button--small" type="button" @click="shipPurchase(purchase)">发货</button>
            </template>
          </div>
        </span>
        <span v-if="!myPurchases.length">暂无采购记录。</span>
      </div>
    </section>

    <!-- 融资申请管理 -->
    <section v-if="canLoadFinances" class="section panel">
      <div class="section-title">
        <div>
          <h3>融资申请</h3>
          <p>{{ isBank ? `共 ${myFinances.length} 条，可审批或删除。` : `共 ${myFinances.length} 条，查看审批状态或删除。` }}</p>
        </div>
      </div>
      <div class="mini-list">
        <span v-for="finance in myFinances" :key="finance.financeId" class="stack-row">
          <strong>#{{ finance.financeId }} · {{ finance.money ?? '-' }} 元 · {{ finance.realName || finance.ownName }}</strong>
          <small>
            <span :class="statusTagClass(finance.status)">{{ approvalStatusLabel(finance.status) }}</span>
            <template v-if="finance.remark"> · {{ finance.remark }}</template>
            <template v-if="finance.repayment"> · {{ finance.repayment }}</template>
          </small>
          <div class="toolbar">
            <!-- 银行审批操作 -->
            <template v-if="isBank && (finance.status ?? 0) === 0">
              <input
                v-if="approvingFinanceId === finance.financeId"
                v-model.trim="financeRemark"
                class="inline-input"
                placeholder="审批备注（可选）"
              />
              <button class="button button--green button--small" type="button" @click="approvingFinanceId === finance.financeId ? approveFinance(finance) : (approvingFinanceId = finance.financeId)">
                {{ approvingFinanceId === finance.financeId ? '确认通过' : '通过' }}
              </button>
              <button class="button button--danger button--small" type="button" @click="rejectFinance(finance)">拒绝</button>
            </template>
            <button class="button button--danger button--small" type="button" @click="deleteFinance(finance)">删除</button>
          </div>
        </span>
        <span v-if="!myFinances.length">暂无融资申请。</span>
      </div>
    </section>
  </section>
</template>
