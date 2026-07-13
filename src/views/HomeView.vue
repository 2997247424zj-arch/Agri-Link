<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import AppImage from '@/components/AppImage.vue'
import { api } from '@/api/client'
import { useSessionStore } from '@/stores/session'
import type { Bank, Expert, Knowledge, TradeOrder, UserRole } from '@/types/domain'

type HomeIconName = 'leaf' | 'bank' | 'expert' | 'cart' | 'user' | 'shield' | 'check'

const session = useSessionStore()
const router = useRouter()
const banks = ref<Bank[]>([])
const experts = ref<Expert[]>([])
const orders = ref<TradeOrder[]>([])
const loading = ref(true)
const notice = ref('')

const fallbackBanks: Bank[] = [
  {
    bankId: 1001,
    bankName: '乡村振兴信用贷',
    introduce: '面向农户经营周转，支持按季付息，审批材料简化。',
    money: 200000,
    rate: 3.8,
    repayment: '按季付息',
  },
  {
    bankId: 1002,
    bankName: '农产品订单贷',
    introduce: '结合采购订单和信用记录快速授信，适合稳定供货农户。',
    money: 500000,
    rate: 4.2,
    repayment: '到期还本',
  },
  {
    bankId: 1003,
    bankName: '农机设备贷',
    introduce: '支持采购农机、仓储、冷链等生产经营设备。',
    money: 300000,
    rate: 4.6,
    repayment: '等额本息',
  },
]

const fallbackExperts: Expert[] = [
  { userName: 'expert01', realName: '周明', profession: '水稻病虫害防治', position: '高级农艺师', belong: '湘西农业技术站' },
  { userName: 'expert02', realName: '陈岚', profession: '果树栽培与冷链采后', position: '研究员', belong: '农业科技推广中心' },
  { userName: 'expert03', realName: '李清', profession: '农产品品牌运营', position: '产业顾问', belong: '数字农业服务中心' },
  { userName: 'expert04', realName: '赵宁', profession: '土壤改良与施肥', position: '农技专家', belong: '县农技推广站' },
]

const fallbackOrders: TradeOrder[] = [
  { orderId: 1, title: '富硒猕猴桃 20kg', type: '水果', price: 8.6, ownName: '吉首合作社', address: '湘西州', picture: 'watermelon_20250513154759.png' },
  { orderId: 2, title: '高山生态大米 50kg', type: '粮油', price: 5.2, ownName: '龙山农户', address: '龙山县', picture: 'tea.png' },
  { orderId: 3, title: '紫皮洋葱 10kg', type: '蔬菜', price: 2.8, ownName: '保靖基地', address: '保靖县', picture: 'yangcong_20250513154843.png' },
  { orderId: 4, title: '精品黄桃礼盒', type: '水果', price: 12.8, ownName: '凤凰果园', address: '凤凰县', picture: 'W020230811400645740814_ORIGIN.jpg' },
]

const serviceCards: Array<{
  title: string
  desc: string
  to: string
  icon: HomeIconName
  metric: string
  roles: UserRole[]
}> = [
  { title: '发布与管理货源', desc: '农户维护产品、库存、价格和交易状态，形成可采购货源。', to: '/trade', icon: 'leaf', metric: '农户专属', roles: ['FARMER'] },
  { title: '融资申请', desc: '农户选择银行产品，提交资料、补充材料并跟踪审批进度。', to: '/finance', icon: 'bank', metric: '农户专属', roles: ['FARMER'] },
  { title: '专家咨询', desc: '农户发起问答和预约，专家负责答复、指导和知识沉淀。', to: '/experts', icon: 'expert', metric: '农户/专家', roles: ['FARMER', 'EXPERT'] },
  { title: '农产品采购', desc: '买家浏览货源、比较产地价格，并生成可追踪采购单。', to: '/trade', icon: 'cart', metric: '买家专属', roles: ['BUYER'] },
  { title: '购物车', desc: '买家集中维护待采购产品、数量和下单前确认信息。', to: '/cart', icon: 'cart', metric: '买家专属', roles: ['BUYER'] },
  { title: '融资审批', desc: '银行维护贷款产品，查看申请资料，匹配农户并更新审批状态。', to: '/finance', icon: 'bank', metric: '银行专属', roles: ['BANK'] },
  { title: '后台管理', desc: '管理员管理用户角色、监管交易和融资状态、维护平台内容。', to: '/admin', icon: 'shield', metric: '管理员专属', roles: ['SYSTEM_ADMIN'] },
] as const

const fallbackNews = [
  { title: '春耕金融服务专区上线', desc: '围绕种子、农资和农机采购场景，提供额度匹配和申请进度管理。', image: '/file/info/20e7a0d77ecf4731b28ebc1d6ca22587.jpg' },
  { title: '绿色农产品产销对接', desc: '支持合作社发布稳定货源，买家可按品类、产地和价格快速筛选采购。', image: '/file/info/b62d1d12d2bc4940956c92b79509efee.jpg' },
]
const knowledgeList = ref<Knowledge[]>([])

// 资讯无自带配图时，按序轮换占位图，避免所有卡片显示同一张
const newsFallbackImages = [
  '/file/info/20e7a0d77ecf4731b28ebc1d6ca22587.jpg',
  '/file/info/3c26336725224041b2a2f4542020b018.jpg',
  '/file/info/47fc92e1068d4c20833e4e197aec0b0d.jpg',
  '/file/info/b62d1d12d2bc4940956c92b79509efee.jpg',
]

const visibleBanks = computed(() => (banks.value.length ? banks.value : fallbackBanks).slice(0, 4))
const visibleExperts = computed(() => (experts.value.length ? experts.value : fallbackExperts).slice(0, 5))
const visibleOrders = computed(() => (orders.value.length ? orders.value : fallbackOrders).slice(0, 5))
const visibleNews = computed(() => {
  if (knowledgeList.value.length) {
    return knowledgeList.value.slice(0, 4).map((k, index) => ({
      title: k.title,
      desc: k.content?.slice(0, 80) || k.category || '平台资讯',
      image: k.picture?.startsWith('/') || k.picture?.startsWith('http')
        ? k.picture
        : k.picture ? `/file/info/${k.picture}` : newsFallbackImages[index % newsFallbackImages.length],
    }))
  }
  return fallbackNews
})

const roleHome = computed(() => {
  if (!session.isLoggedIn) {
    return {
      title: '助力农企互联，帮扶金融服务',
      desc: '连接农户、买家、专家与银行，让农产品交易、融资申请和技术服务在一个平台内闭环。',
      badge: '数字农业 · 融销协同 · 可信服务',
    }
  }
  return {
    FARMER: { title: '农户经营工作台', desc: '聚合货源发布、融资申请、材料补充和专家咨询，帮助经营信息进入交易与金融流程。', badge: '农户角色' },
    BUYER: { title: '买家采购工作台', desc: '聚焦农产品浏览、购物车、收货信息和采购订单，让采购过程清晰可追踪。', badge: '买家角色' },
    EXPERT: { title: '专家服务工作台', desc: '集中处理农技问答、预约咨询、个人资料和知识发布，支撑农户生产决策。', badge: '专家角色' },
    BANK: { title: '银行融资工作台', desc: '面向贷款产品维护、农户意向匹配和融资申请审批，银行承担最终审批责任。', badge: '银行角色' },
    SYSTEM_ADMIN: { title: '系统管理员控制台', desc: '独立后台负责用户角色、交易状态、融资进度监管和内容维护，不直接代替银行审批。', badge: '系统管理员' },
  }[session.role]
})

const heroActions = computed<Array<{ to: string; label: string; icon: HomeIconName; theme: 'green' | 'light' }>>(() => {
  if (!session.isLoggedIn) return [{ to: '/auth', label: '登录选择角色', icon: 'user', theme: 'green' }]
  if (session.role === 'SYSTEM_ADMIN') return [{ to: '/admin', label: '进入后台管理', icon: 'shield', theme: 'green' }]
  if (session.role === 'FARMER') return [{ to: '/trade', label: '发布货源', icon: 'leaf', theme: 'green' }, { to: '/finance', label: '申请融资', icon: 'bank', theme: 'light' }]
  if (session.role === 'BUYER') return [{ to: '/trade', label: '浏览农产品', icon: 'leaf', theme: 'green' }, { to: '/cart', label: '查看购物车', icon: 'cart', theme: 'light' }]
  if (session.role === 'EXPERT') return [{ to: '/experts', label: '处理专家服务', icon: 'expert', theme: 'green' }]
  return [{ to: '/finance', label: '处理融资业务', icon: 'bank', theme: 'green' }]
})

const visibleServiceCards = computed(() =>
  serviceCards.filter((item) => !session.isLoggedIn || item.roles.includes(session.role)),
)

const dashboardStats = computed(() => {
  if (session.role === 'SYSTEM_ADMIN' && session.isLoggedIn) {
    return [
      { label: '监管角色', value: 5, desc: '五类角色统一隔离', icon: 'shield' },
      { label: '货源监管', value: visibleOrders.value.length, desc: '审核农户发布内容', icon: 'leaf' },
      { label: '融资监管', value: visibleBanks.value.length, desc: '查看银行产品与申请进度', icon: 'bank' },
      { label: '内容监管', value: visibleExperts.value.length, desc: '专家与知识服务概览', icon: 'expert' },
    ] as const
  }
  return [
    { label: '当前角色', value: session.isLoggedIn ? 1 : 5, desc: session.isLoggedIn ? session.roleLabel : '登录后隔离功能', icon: 'user' },
    { label: '可见服务', value: visibleServiceCards.value.length, desc: '只展示可访问入口', icon: 'shield' },
    { label: '在架农产品', value: visibleOrders.value.length, desc: '产地直供，支持采购流转', icon: 'leaf' },
    { label: '入驻专家', value: visibleExperts.value.length, desc: '覆盖种植、植保、品牌运营', icon: 'expert' },
  ] as const
})

const workflowSteps: Array<{
  title: string
  desc: string
  to: string
  icon: HomeIconName
  roles: UserRole[]
}> = [
  { title: '农户发布', desc: '维护货源、登记融资意向、提交专家问题和申请材料', to: '/trade', icon: 'leaf', roles: ['FARMER'] },
  { title: '买家采购', desc: '筛选农产品、加入购物车、确认地址并生成采购订单', to: '/cart', icon: 'cart', roles: ['BUYER'] },
  { title: '专家服务', desc: '处理问题、预约、专家资料和农业知识服务', to: '/experts', icon: 'expert', roles: ['EXPERT', 'FARMER'] },
  { title: '银行审批', desc: '查看融资申请、匹配潜在农户、填写审批意见并更新状态', to: '/finance', icon: 'bank', roles: ['BANK'] },
  { title: '平台监管', desc: '统一管理用户、交易、融资进度和平台内容数据', to: '/admin', icon: 'shield', roles: ['SYSTEM_ADMIN'] },
]

// 项目能力速览：面向未登录 / 所有角色都展示，让访客一眼看到平台能做什么
// 每张卡带 roles 白名单，登录后按角色隐藏无权入口（例如 BUYER 不看到"融资撮合"）
const capabilityHighlights: Array<{
  title: string
  desc: string
  tag: string
  icon: HomeIconName
  to: string
  roles: UserRole[]
}> = [
  { title: '农产品直采直销', desc: '产地货源直连买家，省去中间环节，价格与来源可追溯。', tag: '融销核心', icon: 'leaf', to: '/trade', roles: ['FARMER', 'BUYER', 'EXPERT', 'BANK', 'SYSTEM_ADMIN'] },
  { title: '涉农金融撮合', desc: '对接银行贷款产品，按额度、利率、还款方式一站式申请。', tag: '融资加速', icon: 'bank', to: '/finance', roles: ['FARMER', 'BANK', 'SYSTEM_ADMIN'] },
  { title: '专家远程指导', desc: '在线问答 + 预约咨询，覆盖种植、植保、品牌运营多领域。', tag: '技术护航', icon: 'expert', to: '/experts', roles: ['FARMER', 'EXPERT', 'SYSTEM_ADMIN'] },
  { title: '栽培小技巧', desc: '按季节 / 作物汇总的实用栽培要点，帮助农户快速上手。', tag: '知识赋能', icon: 'check', to: '/#home-tips', roles: ['FARMER', 'BUYER', 'EXPERT', 'BANK', 'SYSTEM_ADMIN'] },
  { title: '采购车与订单', desc: '买家可批量维护待采购清单，一键下单并跟踪物流进度。', tag: '交易闭环', icon: 'cart', to: '/cart', roles: ['BUYER'] },
  { title: '平台合规监管', desc: '管理员统一监管用户、交易和融资进度，保障数据可信。', tag: '可信服务', icon: 'shield', to: '/admin', roles: ['SYSTEM_ADMIN'] },
]

const visibleCapabilityHighlights = computed(() =>
  capabilityHighlights.filter((cap) => !session.isLoggedIn || cap.roles.includes(session.role)),
)

// 「农作物栽培小技巧」——先用前端 mock，字段结构与后端 tip 表对齐，方便日后替换
type CultivationTip = {
  id: number
  title: string
  crop: string
  season: string
  summary: string
  points: string[]
}

const cultivationTips: CultivationTip[] = [
  {
    id: 1,
    title: '春季猕猴桃萌芽期水肥管理',
    crop: '猕猴桃',
    season: '春季',
    summary: '萌芽前 15 天完成清园，配合腐熟有机肥 + 少量氮肥促春梢整齐抽发。',
    points: [
      '土壤解冻后立即松土，深度 5–10cm，避免伤及浅层吸收根',
      '按每株 8–12kg 腐熟饼肥 + 复合肥 0.3kg 施底肥',
      '萌芽水一次性浇透，避免后续春旱裂芽',
    ],
  },
  {
    id: 2,
    title: '水稻分蘖期病虫害绿色防控',
    crop: '水稻',
    season: '夏季',
    summary: '分蘖高峰期是二化螟、稻纵卷叶螟高发窗口，优先诱杀 + 生物药剂。',
    points: [
      '每 5 亩挂 1 盏杀虫灯，19:00–24:00 集中开灯',
      '田埂种植香根草作为诱集植物，5–7 天检查一次',
      '蜘蛛密度低于 3 头/丛时启用 Bt 或苏云金杆菌制剂',
    ],
  },
  {
    id: 3,
    title: '高山生态大米育秧要点',
    crop: '水稻',
    season: '春季',
    summary: '海拔 800m 以上区域温差大，重点做好床土消毒与保温揭膜节奏。',
    points: [
      '床土 pH 控制在 4.5–5.5，播前 3 天用敌克松淋透',
      '早晚温差超 12℃ 时白天揭膜 2–3 小时透气',
      '秧龄 25–30 天带 3.5 叶时移栽，秧根不带黄叶',
    ],
  },
  {
    id: 4,
    title: '设施黄桃疏花疏果',
    crop: '黄桃',
    season: '春夏之交',
    summary: '花后 20 天完成一次定果，长中果枝留 2–3 果、短果枝留 1 果。',
    points: [
      '优先保留朝下 / 侧生果，去掉畸形果与病虫果',
      '果距不小于 15cm，避免后期挤压落果',
      '定果后立即喷施 0.3% 磷酸二氢钾 + 钙肥',
    ],
  },
  {
    id: 5,
    title: '洋葱移栽后成活期管理',
    crop: '洋葱',
    season: '秋季',
    summary: '移栽后 10 天内保持土壤湿润但不积水，缓苗后追第一次提苗肥。',
    points: [
      '定植深度 2–3cm，过深易烂心、过浅易倒伏',
      '缓苗期喷 1 次生根粉 + 芸苔素，促发新根',
      '缓苗后追尿素 5–8kg/亩，配合小水勤浇',
    ],
  },
  {
    id: 6,
    title: '茶园秋冬修剪与封园',
    crop: '茶叶',
    season: '秋冬',
    summary: '停采后 20 天内完成轻修剪，配合石硫合剂封园降低越冬虫源。',
    points: [
      '剪去当年新梢基部 3–5cm 及所有病虫枝',
      '清园后喷 0.5 波美度石硫合剂 1 次',
      '行间铺草 3–5cm 保温保墒，翌春开沟施基肥',
    ],
  },
]

const activeTipId = ref<number>(cultivationTips[0]!.id)
const activeTip = computed(() => cultivationTips.find((t) => t.id === activeTipId.value) ?? cultivationTips[0]!)

// 能力速览卡片点击：
// - '/#home-xxx' 型：当前就在 home，直接滚到锚点（RouterLink 不处理 same-path hash）
// - '/xxx' 型跨页：交给 router.push，正常跳转
function handleCapabilityClick(cap: (typeof capabilityHighlights)[number], event: MouseEvent) {
  const target = cap.to
  if (target.startsWith('/#')) {
    event.preventDefault()
    const anchorId = target.slice(2)
    const el = document.getElementById(anchorId)
    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'start' })
      // 让 URL 里也带上 hash，方便复制分享
      history.replaceState(null, '', `/#${anchorId}`)
    }
    return
  }
  event.preventDefault()
  void router.push(target)
}

const visibleWorkflowSteps = computed(() =>
  workflowSteps.filter((step) => !session.isLoggedIn || step.roles.includes(session.role)),
)
const showFinanceSection = computed(() => !session.isLoggedIn || ['FARMER', 'BANK'].includes(session.role))
const showExpertSection = computed(() => !session.isLoggedIn || ['FARMER', 'EXPERT'].includes(session.role))
const showProductSection = computed(() => !session.isLoggedIn || ['FARMER', 'BUYER'].includes(session.role))
const showAdminOnly = computed(() => session.isLoggedIn && session.role === 'SYSTEM_ADMIN')

function imageSrc(picture?: string) {
  const first = picture?.split(/\s+/)[0]
  if (!first) return '/file/info/3c26336725224041b2a2f4542020b018.jpg'
  return first.startsWith('http') || first.startsWith('/') ? first : `/file/order/${first}`
}

onMounted(async () => {
  try {
    const [bankData, expertData, orderData] = await Promise.all([
      api.get<Bank[]>('/api/finance/banks'),
      api.get<Expert[]>('/api/experts'),
      api.get<TradeOrder[]>('/api/trade/orders'),
    ])
    banks.value = bankData?.length ? bankData : fallbackBanks
    experts.value = expertData?.length ? expertData : fallbackExperts
    orders.value = orderData?.length ? orderData : fallbackOrders
  } catch (error) {
    banks.value = fallbackBanks
    experts.value = fallbackExperts
    orders.value = fallbackOrders
    notice.value = error instanceof Error ? `后端暂不可用：${error.message}` : '后端暂不可用，已显示演示数据。'
  } finally {
    loading.value = false
  }

  // 平台资讯：从知识库拉取，失败则保留演示资讯，不打断主流程
  try {
    const knowledgeData = await api.get<Knowledge[]>('/api/knowledge')
    if (knowledgeData?.length) knowledgeList.value = knowledgeData
  } catch {
    // 静默降级到 fallbackNews
  }
})
</script>

<template>
  <section class="home-page">
    <section class="portal-hero">
      <img class="portal-hero__image" src="/file/info/3c26336725224041b2a2f4542020b018.jpg" alt="绿色农产品" />
      <div class="portal-hero__overlay">
        <span>{{ roleHome.badge }}</span>
        <h1>{{ roleHome.title }}</h1>
        <p>{{ roleHome.desc }}</p>
        <div class="hero__actions">
          <RouterLink
            v-for="action in heroActions"
            :key="action.to"
            class="button"
            :class="action.theme === 'light' ? 'button--light' : 'button--green'"
            :to="action.to"
          >
            <AppIcon :name="action.icon" />{{ action.label }}
          </RouterLink>
        </div>
      </div>
    </section>

    <p v-if="notice" class="alert portal-alert">{{ notice }}</p>

    <section class="portal-section capability-section" id="home-capability" aria-label="项目能力速览">
      <div class="portal-heading">
        <h2>项目能做什么</h2>
        <p>覆盖农产品「融、销、技、管」的一站式服务能力速览</p>
      </div>
      <div class="capability-grid">
        <a
          v-for="cap in visibleCapabilityHighlights"
          :key="cap.title"
          class="capability-card"
          :href="cap.to"
          @click="handleCapabilityClick(cap, $event)"
        >
          <span class="capability-card__icon"><AppIcon :name="cap.icon" /></span>
          <div class="capability-card__body">
            <em class="capability-card__tag">{{ cap.tag }}</em>
            <strong>{{ cap.title }}</strong>
            <p>{{ cap.desc }}</p>
          </div>
        </a>
      </div>
    </section>

    <section class="portal-section dashboard-section" id="home-dashboard">
      <div class="portal-heading">
        <h2>业务工作台</h2>
        <p>{{ session.isLoggedIn ? '当前角色只展示允许访问的业务入口' : '登录后按五类角色隔离业务入口' }}</p>
      </div>
      <div class="dashboard-grid">
        <article v-for="item in dashboardStats" :key="item.label" class="dashboard-card">
          <span><AppIcon :name="item.icon" /></span>
          <strong>{{ item.value }}</strong>
          <h3>{{ item.label }}</h3>
          <p>{{ item.desc }}</p>
        </article>
      </div>
      <div class="workflow-board" aria-label="平台协同流程">
        <RouterLink v-for="step in visibleWorkflowSteps" :key="step.title" :to="step.to" class="workflow-card">
          <span><AppIcon :name="step.icon" /></span>
          <div>
            <strong>{{ step.title }}</strong>
            <p>{{ step.desc }}</p>
          </div>
          <AppIcon name="arrow" />
        </RouterLink>
      </div>
    </section>

    <section v-if="showAdminOnly" class="portal-section admin-entry-section">
      <div class="portal-heading">
        <h2>管理员专属模块</h2>
        <p>用户角色、货源状态、采购订单、融资进度、资讯知识统一在后台监管</p>
      </div>
      <RouterLink class="button button--green" to="/admin"><AppIcon name="shield" />进入系统后台</RouterLink>
    </section>

    <section class="portal-section" id="home-services">
      <div class="portal-heading">
        <h2>核心服务</h2>
        <p>围绕融、销、技、管四类业务建立统一入口</p>
      </div>
      <div class="service-grid">
        <RouterLink v-for="item in visibleServiceCards" :key="item.title" class="service-card" :to="item.to">
          <span><AppIcon :name="item.icon" /></span>
          <strong>{{ item.title }}</strong>
          <p>{{ item.desc }}</p>
          <em>{{ item.metric }}</em>
        </RouterLink>
      </div>
    </section>

    <section v-if="showFinanceSection" class="portal-section" id="home-finance">
      <div class="portal-heading">
        <h2>金融产品</h2>
        <p>{{ loading ? '正在读取后端数据' : '按额度、利率和还款方式快速选择适合的融资产品' }}</p>
      </div>
      <div class="finance-card-grid">
        <article v-for="bank in visibleBanks" :key="bank.bankId" class="finance-card">
          <span class="finance-card__icon"><AppIcon name="bank" /></span>
          <h3>{{ bank.bankName }}</h3>
          <p>{{ bank.introduce || '暂无产品介绍' }}</p>
          <strong>{{ bank.money ? `${bank.money} 元` : '额度面议' }}</strong>
          <div class="card__footer">
            <span class="tag tag--green">{{ bank.rate ?? '-' }}%</span>
            <RouterLink class="button button--small button--green" to="/finance">查看</RouterLink>
          </div>
        </article>
      </div>
    </section>

    <section class="portal-section tips-section" id="home-tips" aria-label="农作物栽培小技巧">
      <div class="portal-heading">
        <h2>农作物栽培小技巧</h2>
        <p>按作物 / 季节汇总的实用要点，帮农户少走弯路</p>
      </div>
      <div class="tips-layout">
        <div class="tips-list" role="tablist" aria-label="栽培小技巧列表">
          <button
            v-for="tip in cultivationTips"
            :key="tip.id"
            type="button"
            role="tab"
            :aria-selected="tip.id === activeTipId"
            :class="['tips-item', { 'tips-item--active': tip.id === activeTipId }]"
            @click="activeTipId = tip.id"
            @mouseenter="activeTipId = tip.id"
          >
            <span class="tips-item__meta">
              <em class="tips-item__crop">{{ tip.crop }}</em>
              <em class="tips-item__season">{{ tip.season }}</em>
            </span>
            <strong>{{ tip.title }}</strong>
          </button>
        </div>
        <article class="tips-detail" aria-live="polite">
          <header class="tips-detail__head">
            <span class="tips-item__meta">
              <em class="tips-item__crop">{{ activeTip.crop }}</em>
              <em class="tips-item__season">{{ activeTip.season }}</em>
            </span>
            <h3>{{ activeTip.title }}</h3>
            <p>{{ activeTip.summary }}</p>
          </header>
          <ul class="tips-points">
            <li v-for="(point, idx) in activeTip.points" :key="idx">
              <span class="tips-points__num">{{ idx + 1 }}</span>
              <span>{{ point }}</span>
            </li>
          </ul>
          <footer class="tips-detail__foot">
            <RouterLink class="text-link" to="/experts">向专家提问 →</RouterLink>
          </footer>
        </article>
      </div>
    </section>

    <section v-if="showExpertSection" class="portal-section" id="home-experts">
      <div class="portal-heading">
        <h2>专家团队</h2>
        <p>覆盖种植、植保、品牌和产销运营</p>
      </div>
      <div class="expert-strip">
        <article v-for="(expert, index) in visibleExperts" :key="expert.userName" class="expert-card">
          <img :src="`/file/avatar/expert0${(index % 5) + 1}.png`" :alt="expert.realName || expert.userName" loading="lazy" />
          <h3>{{ expert.realName || expert.userName }}</h3>
          <p>{{ expert.profession || '农业综合服务' }}</p>
          <span>{{ expert.position || '平台专家' }}</span>
        </article>
      </div>
    </section>

    <section v-if="showExpertSection" class="portal-section qa-section" id="home-qa">
      <div class="qa-badge">Q&A</div>
      <div>
        <h2>专家问答与预约指导</h2>
        <p>农户可提交作物、土壤、病虫害和销售运营问题，专家端可在线答复或处理预约。</p>
      </div>
      <RouterLink class="button button--green" to="/experts"><AppIcon name="arrow" />进入专家服务</RouterLink>
    </section>

    <section v-if="!showAdminOnly" class="portal-section" id="home-news">
      <div class="portal-heading">
        <h2>平台资讯</h2>
        <p>农业服务、金融产品和产销协同动态</p>
      </div>
      <div class="news-list">
        <article v-for="item in visibleNews" :key="item.title" class="news-item">
          <AppImage class="news-item__media" :src="item.image" :alt="item.title" ratio="16 / 9" icon="leaf" />
          <div>
            <h3>{{ item.title }}</h3>
            <p>{{ item.desc }}</p>
            <RouterLink class="text-link" to="/finance">查看详情</RouterLink>
          </div>
        </article>
      </div>
    </section>

    <section v-if="showProductSection" class="portal-section" id="home-products">
      <div class="portal-heading">
        <h2>最新农产品</h2>
        <p>产地直供，支持加入购物车并生成采购订单</p>
      </div>
      <div class="product-row">
        <article v-for="order in visibleOrders" :key="order.orderId" class="product-tile">
          <AppImage class="product-tile__media" :src="imageSrc(order.picture)" :alt="order.title" ratio="1 / 0.82" icon="leaf" />
          <h3>{{ order.title }}</h3>
          <p>{{ order.address || '产地待补充' }} · {{ order.type || '农产品' }}</p>
          <strong>￥{{ order.price ?? '-' }}</strong>
        </article>
      </div>
    </section>
  </section>
</template>
