<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import AppIcon from '@/components/AppIcon.vue'
import PageHeader from '@/components/ui/PageHeader.vue'
import SummaryStrip from '@/components/ui/SummaryStrip.vue'
import { api } from '@/api/client'
import { useSessionStore } from '@/stores/session'
import type { Finance, Purchase, TradeOrder, User, UserRole } from '@/types/domain'

const session = useSessionStore()
const loading = ref(true)
const saving = ref(false)
const message = ref('')
const error = ref('')
const myOrders = ref<TradeOrder[]>([])
const myPurchases = ref<Purchase[]>([])
const myFinances = ref<Finance[]>([])
const newPassword = ref('')
type ProfileActionIcon = 'leaf' | 'bank' | 'expert' | 'cart' | 'user' | 'shield'
type ProfileRolePanel = {
  title: string
  desc: string
  items: Array<{ label: string; value: string | number }>
  actions: Array<{ to: string; label: string; icon: ProfileActionIcon; primary?: boolean }>
}

// 个人中心按角色展示资料、交易、采购和融资记录。
const profile = reactive<Partial<User> & { password: string }>({
  userName: session.userName || 'farmer-demo',
  password: 'secret',
  nickName: session.displayName || '',
  realName: '',
  phone: '',
  identityNum: '',
  address: '',
  role: session.role,
  avatar: '',
})

const roleProfile = reactive({
  farmScale: '80亩',
  mainProduct: '高山大米',
  expertField: '水稻病虫害防治',
  bankName: '乡村振兴信用贷服务部',
  buyerType: '社区团购采购',
})

const roles: Array<{ value: UserRole; label: string }> = [
  { value: 'FARMER', label: '农户' },
  { value: 'BUYER', label: '买家' },
  { value: 'EXPERT', label: '技术专家' },
  { value: 'BANK', label: '银行' },
  { value: 'SYSTEM_ADMIN', label: '系统管理员' },
]

const fallbackOrders: TradeOrder[] = [
  { orderId: 1, title: '高山生态大米 50kg', type: '粮油', price: 5.2, ownName: 'farmer-demo', address: '龙山县', orderStatus: 0 },
]

const canLoadOrders = computed(() => profile.role === 'FARMER')
const canLoadPurchases = computed(() => profile.role === 'BUYER' || profile.role === 'FARMER')
const canLoadFinances = computed(() => profile.role === 'FARMER' || profile.role === 'BANK')
const totalAssets = computed(() => myOrders.value.length + myPurchases.value.length + myFinances.value.length)
const purchaseAmount = computed(() =>
  myPurchases.value.reduce((sum, purchase) => sum + Number(purchase.totalPrice ?? 0), 0),
)
const profileSummaryCards = computed(() => {
  if (profile.role === 'BUYER') {
    return [
      { value: profile.nickName || profile.userName || '买家', label: '买家账户' },
      { value: myPurchases.value.length, label: loading.value ? '正在加载采购记录' : '采购订单' },
      { value: `¥${purchaseAmount.value.toFixed(2)}`, label: '累计采购金额' },
      { value: profile.address || '待完善', label: '默认收货信息' },
    ]
  }

  return [
    { value: profile.nickName || profile.userName || '-', label: session.roleLabel },
    { value: totalAssets.value, label: loading.value ? '正在加载业务记录' : '关联业务记录' },
    { value: myOrders.value.length, label: '我的货源' },
    { value: myFinances.value.length, label: '融资申请' },
  ]
})

const roleProfilePanel = computed<ProfileRolePanel>(() => {
  const commonItems = [
    { label: '账号', value: profile.userName || '-' },
    { label: '手机号', value: profile.phone || '待完善' },
    { label: '联系地址', value: profile.address || '待完善' },
  ]

  if (profile.role === 'FARMER') {
    return {
      title: '农户个人信息管理',
      desc: '维护经营资料、货源信息和融资申请入口。',
      items: [
        { label: '经营规模', value: roleProfile.farmScale },
        { label: '主营农产品', value: roleProfile.mainProduct },
        { label: '我的货源', value: `${myOrders.value.length} 条` },
        ...commonItems,
      ],
      actions: [
        { to: '/trade?tab=publish', label: '商品发布与管理', icon: 'leaf', primary: true },
        { to: '/finance?tab=apply', label: '融资申请', icon: 'bank' },
      ],
    }
  }

  if (profile.role === 'BUYER') {
    return {
      title: '买家个人信息管理',
      desc: '集中管理采购身份、收货资料、采购记录和结算入口。',
      items: [
        { label: '采购类型', value: roleProfile.buyerType },
        { label: '采购订单', value: `${myPurchases.value.length} 单` },
        { label: '累计金额', value: `¥${purchaseAmount.value.toFixed(2)}` },
        ...commonItems,
      ],
      actions: [
        { to: '/trade', label: '继续选品', icon: 'leaf', primary: true },
        { to: '/cart', label: '采购中心', icon: 'cart' },
      ],
    }
  }

  if (profile.role === 'EXPERT') {
    return {
      title: '专家个人信息管理',
      desc: '维护专家身份、认证专业和咨询服务入口。',
      items: [
        { label: '认证专业', value: roleProfile.expertField },
        { label: '真实姓名', value: profile.realName || '待完善' },
        ...commonItems,
      ],
      actions: [{ to: '/experts', label: '咨询处理', icon: 'expert', primary: true }],
    }
  }

  if (profile.role === 'BANK') {
    return {
      title: '机构信息管理',
      desc: '维护机构资料、融资产品和审核业务入口。',
      items: [
        { label: '机构/产品', value: roleProfile.bankName },
        { label: '融资申请', value: `${myFinances.value.length} 条` },
        ...commonItems,
      ],
      actions: [{ to: '/finance?tab=result', label: '融资审核', icon: 'bank', primary: true }],
    }
  }

  return {
    title: '管理员账号信息管理',
    desc: '维护管理员账号资料，并快速进入平台监管后台。',
    items: [
      { label: '管理范围', value: '用户、交易、融资、内容' },
      { label: '真实姓名', value: profile.realName || '待完善' },
      ...commonItems,
    ],
    actions: [{ to: '/admin', label: '后台管理', icon: 'shield', primary: true }],
  }
})

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

function handleAvatarFile(event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = () => {
    profile.avatar = String(reader.result || '')
  }
  reader.readAsDataURL(file)
}

// 不同角色只加载自身相关的业务记录。
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

// 先读取用户资料，再按角色补充业务数据。
async function loadProfile() {
  loading.value = true
  error.value = ''
  const userName = profile.userName || 'farmer-demo'
  try {
    const user = await api.get<User>(`/api/users/${encodeURIComponent(userName)}`)
    applyUser(user)
    const resolvedUserName = user.userName || userName
    const [orders, purchases, finances] = await Promise.all([
      canLoadOrders.value
        ? api.get<TradeOrder[]>(`/api/trade/orders/owners/${encodeURIComponent(resolvedUserName)}`).catch(() => [])
        : Promise.resolve([]),
      canLoadPurchases.value
        ? api.get<Purchase[]>(`/api/trade/purchases/owners/${encodeURIComponent(resolvedUserName)}`).catch(() => [])
        : Promise.resolve([]),
      canLoadFinances.value ? api.get<Finance[]>('/api/finance/applications').catch(() => []) : Promise.resolve([]),
    ])
    myOrders.value = orders?.length ? orders : canLoadOrders.value ? fallbackOrders : []
    myPurchases.value = purchases ?? []
    myFinances.value = (finances ?? []).filter((item) => item.ownName === resolvedUserName)
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
    const saved = await api.put<User>(`/api/users/${encodeURIComponent(userName)}`, {
      userName,
      password: profile.password,
      nickName: profile.nickName,
      phone: profile.phone,
      identityNum: profile.identityNum,
      address: profile.address,
      role: profile.role,
      avatar: profile.avatar,
      realName: profile.realName,
    })
    applyUser(saved)
    if (profile.role) session.setRole(profile.role as UserRole)

    // 若填写了新密码，走专门的改密码接口（后端 UserRequest 不含 newPassword 字段）
    if (newPassword.value.trim()) {
      await api.patch<User>(`/api/users/${encodeURIComponent(userName)}/password`, {
        password: newPassword.value.trim(),
      })
      profile.password = newPassword.value.trim()
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

onMounted(loadProfile)
</script>

<template>
  <section class="page">
    <PageHeader eyebrow="个人中心" icon="user" title="资料维护与业务记录" desc="对接 `/api/users/{userName}`、我的货源、采购记录和融资申请。">
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
      :extra-class="profile.role === 'BUYER' ? 'profile-summary-strip profile-summary-strip--buyer' : 'profile-summary-strip'"
    />

    <section class="section profile-workspace">
      <form class="panel form" @submit.prevent="saveProfile">
        <div class="section-title">
          <div>
            <h2>基础资料</h2>
            <p>用于登录、融资申请、交易发布和专家服务识别。</p>
          </div>
        </div>
        <label class="field"><span>账号</span><input v-model.trim="profile.userName" required /></label>
        <label class="field"><span>当前密码</span><input v-model="profile.password" type="password" required /></label>
        <label class="field"><span>新密码</span><input v-model="newPassword" type="password" placeholder="不修改可留空" /></label>
        <label class="field"><span>头像上传</span><input type="file" accept="image/*" @change="handleAvatarFile" /></label>
        <div v-if="profile.avatar" class="avatar-preview">
          <img :src="profile.avatar" alt="头像预览" />
        </div>
        <label class="field"><span>昵称</span><input v-model.trim="profile.nickName" /></label>
        <label class="field"><span>真实姓名</span><input v-model.trim="profile.realName" /></label>
        <label class="field"><span>手机号</span><input v-model.trim="profile.phone" type="tel" /></label>
        <label class="field"><span>身份号码</span><input v-model.trim="profile.identityNum" /></label>
        <label class="field"><span>角色</span><select v-model="profile.role"><option v-for="role in roles" :key="role.value" :value="role.value">{{ role.label }}</option></select></label>
        <label class="field"><span>地址</span><textarea v-model.trim="profile.address" /></label>
        <div class="panel-lite">
          <h3>角色专属资料</h3>
          <label v-if="profile.role === 'FARMER'" class="field"><span>经营规模</span><input v-model.trim="roleProfile.farmScale" /></label>
          <label v-if="profile.role === 'FARMER'" class="field"><span>主营农产品</span><input v-model.trim="roleProfile.mainProduct" /></label>
          <label v-if="profile.role === 'BUYER'" class="field"><span>采购类型</span><input v-model.trim="roleProfile.buyerType" /></label>
          <label v-if="profile.role === 'EXPERT'" class="field"><span>认证专业</span><input v-model.trim="roleProfile.expertField" /></label>
          <label v-if="profile.role === 'BANK'" class="field"><span>机构/产品</span><input v-model.trim="roleProfile.bankName" /></label>
          <p v-if="profile.role === 'SYSTEM_ADMIN'">管理员角色用于平台用户、交易、融资和内容监管。</p>
        </div>
        <button class="button button--green" type="submit" :disabled="saving">
          <AppIcon name="check" />{{ saving ? '保存中' : '保存资料' }}
        </button>
      </form>

      <div class="profile-side">
      <div class="panel role-profile-card">
        <div class="section-title">
          <div>
            <h2>{{ roleProfilePanel.title }}</h2>
            <p>{{ roleProfilePanel.desc }}</p>
          </div>
        </div>
        <div class="role-profile-grid">
          <span v-for="item in roleProfilePanel.items" :key="item.label">
            <small>{{ item.label }}</small>
            <strong>{{ item.value }}</strong>
          </span>
        </div>
        <div class="role-profile-actions">
          <RouterLink
            v-for="action in roleProfilePanel.actions"
            :key="action.to"
            class="button"
            :class="action.primary ? 'button--green' : 'button--ghost'"
            :to="action.to"
          >
            <AppIcon :name="action.icon" />{{ action.label }}
          </RouterLink>
        </div>
      </div>

      <div class="panel profile-actions">
        <div class="section-title">
          <div>
            <h2>业务入口</h2>
            <p>按当前角色快速进入对应业务。</p>
          </div>
        </div>
        <div class="action-grid">
          <RouterLink v-if="profile.role === 'FARMER'" class="button button--ghost" to="/trade?tab=browse"><AppIcon name="leaf" />货源浏览</RouterLink>
          <RouterLink v-if="profile.role === 'FARMER'" class="button button--ghost" to="/trade?tab=publish"><AppIcon name="plus" />农产品发布</RouterLink>
          <RouterLink v-if="profile.role === 'FARMER'" class="button button--ghost" to="/finance?tab=apply"><AppIcon name="bank" />融资申请</RouterLink>
          <RouterLink v-if="profile.role === 'FARMER'" class="button button--ghost" to="/experts?tab=experts"><AppIcon name="expert" />专家咨询</RouterLink>
          <RouterLink v-if="profile.role === 'BUYER'" class="button button--ghost" to="/cart"><AppIcon name="cart" />采购结算</RouterLink>
          <RouterLink v-if="profile.role === 'EXPERT'" class="button button--ghost" to="/experts"><AppIcon name="expert" />咨询处理</RouterLink>
          <RouterLink v-if="profile.role === 'BANK'" class="button button--ghost" to="/finance?tab=result"><AppIcon name="bank" />融资审核</RouterLink>
          <RouterLink v-if="profile.role === 'SYSTEM_ADMIN'" class="button button--ghost" to="/admin"><AppIcon name="shield" />后台管理</RouterLink>
        </div>
      </div>

      <div class="profile-records record-grid">
      <article class="card">
        <h3>我的货源</h3>
        <p>农户发布的商品货源，可在交易页继续维护。</p>
        <div class="mini-list">
          <span v-for="order in myOrders" :key="order.orderId" class="stack-row">
            <strong>{{ order.title }} · ￥{{ order.price ?? '-' }}</strong>
            <small><span :class="statusTagClass(order.orderStatus)">{{ approvalStatusLabel(order.orderStatus) }}</span></small>
            <RouterLink class="text-link" to="/trade">查看详情</RouterLink>
          </span>
          <span v-if="!myOrders.length">暂无货源记录</span>
        </div>
      </article>
      <article class="card">
        <h3>采购记录</h3>
        <p>买家提交的采购订单，管理员可在后台处理状态。</p>
        <div class="mini-list">
          <span v-for="purchase in myPurchases" :key="purchase.purchaseId" class="stack-row">
            <strong>#{{ purchase.purchaseId }} · ￥{{ purchase.totalPrice ?? '-' }}</strong>
            <small><span :class="statusTagClass(purchase.purchaseStatus)">{{ purchaseStatusLabel(purchase.purchaseStatus) }}</span></small>
            <RouterLink class="text-link" to="/cart">查看详情</RouterLink>
          </span>
          <span v-if="!myPurchases.length">暂无采购记录</span>
        </div>
      </article>
      <article class="card">
        <h3>融资申请</h3>
        <p>农户提交的融资申请和审批状态。</p>
        <div class="mini-list">
          <span v-for="finance in myFinances" :key="finance.financeId" class="stack-row">
            <strong>#{{ finance.financeId }} · {{ finance.money ?? '-' }} 元</strong>
            <small><span :class="statusTagClass(finance.status)">{{ approvalStatusLabel(finance.status) }}</span></small>
            <RouterLink class="text-link" to="/finance">查看详情</RouterLink>
          </span>
          <span v-if="!myFinances.length">暂无融资申请</span>
        </div>
      </article>
      </div>
      </div>
    </section>
  </section>
</template>
