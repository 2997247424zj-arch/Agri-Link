<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import AppIcon from '@/components/AppIcon.vue'
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

const totalAssets = computed(() => myOrders.value.length + myPurchases.value.length + myFinances.value.length)

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

async function loadProfile() {
  loading.value = true
  error.value = ''
  const userName = profile.userName || 'farmer-demo'
  try {
    const [user, orders, purchases, finances] = await Promise.all([
      api.get<User>(`/api/users/${encodeURIComponent(userName)}`),
      api.get<TradeOrder[]>(`/api/trade/orders/owners/${encodeURIComponent(userName)}`).catch(() => fallbackOrders),
      api.get<Purchase[]>(`/api/trade/purchases/owners/${encodeURIComponent(userName)}`).catch(() => []),
      api.get<Finance[]>('/api/finance/applications').catch(() => []),
    ])
    applyUser(user)
    myOrders.value = orders?.length ? orders : fallbackOrders
    myPurchases.value = purchases ?? []
    myFinances.value = (finances ?? []).filter((item) => item.ownName === userName)
  } catch (err) {
    myOrders.value = fallbackOrders
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
      newPassword: newPassword.value,
      nickName: profile.nickName,
      phone: profile.phone,
      identityNum: profile.identityNum,
      address: profile.address,
      role: profile.role,
      avatar: profile.avatar,
      realName: profile.realName,
      roleProfile,
    })
    applyUser(saved)
    if (profile.role) session.setRole(profile.role as UserRole)
    message.value = '个人资料已保存。'
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
    <div class="section-title">
      <div>
        <span class="eyebrow"><AppIcon name="user" />个人中心</span>
        <h2>资料维护与业务记录</h2>
        <p>对接 `/api/users/{userName}`、我的货源、采购记录和融资申请。</p>
      </div>
      <button class="button button--ghost" type="button" @click="loadProfile">
        <AppIcon name="search" />刷新
      </button>
    </div>

    <p v-if="message" class="alert">{{ message }}</p>
    <p v-if="error" class="alert alert--error">{{ error }}</p>

    <div class="summary-strip">
      <div class="metric">
        <strong>{{ profile.nickName || profile.userName }}</strong>
        <span>{{ session.roleLabel }}</span>
      </div>
      <div class="metric">
        <strong>{{ totalAssets }}</strong>
        <span>{{ loading ? '正在加载业务记录' : '关联业务记录' }}</span>
      </div>
      <div class="metric">
        <strong>{{ myOrders.length }}</strong>
        <span>我的货源</span>
      </div>
    </div>

    <section class="section grid grid--two">
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

      <div class="panel">
        <div class="section-title">
          <div>
            <h2>业务入口</h2>
            <p>按当前角色快速进入对应业务。</p>
          </div>
        </div>
        <div class="action-grid">
          <RouterLink class="button button--ghost" to="/trade"><AppIcon name="leaf" />发布/浏览货源</RouterLink>
          <RouterLink class="button button--ghost" to="/cart"><AppIcon name="cart" />采购结算</RouterLink>
          <RouterLink class="button button--ghost" to="/finance"><AppIcon name="bank" />融资申请</RouterLink>
          <RouterLink class="button button--ghost" to="/experts"><AppIcon name="expert" />专家问答</RouterLink>
          <RouterLink class="button button--ghost" to="/admin"><AppIcon name="shield" />后台管理</RouterLink>
        </div>
      </div>
    </section>

    <section class="section grid">
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
    </section>
  </section>
</template>
