<script setup lang="ts">
import { computed, onActivated, onBeforeUnmount, onDeactivated, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import AppImage from '@/components/AppImage.vue'
import { api } from '@/api/client'
import { useLocaleStore } from '@/stores/locale'
import { useSessionStore } from '@/stores/session'
import type { Bank, Expert, Knowledge, TradeOrder, UserRole } from '@/types/domain'

type HomeIconName = 'leaf' | 'bank' | 'expert' | 'cart' | 'user' | 'shield' | 'check'

const session = useSessionStore()
const locale = useLocaleStore()
const router = useRouter()
const banks = ref<Bank[]>([])
const experts = ref<Expert[]>([])
const orders = ref<TradeOrder[]>([])
const selectedProduct = ref<TradeOrder | null>(null)
const loading = ref(true)
const notice = ref('')
const activeHeroSlide = ref(0)
const heroPaused = ref(false)
let heroTimer = 0

const heroSlides = computed(() => [
  {
    image: '/file/order/12be19984e374bcfbf06561571365d07.jpg',
    alt: locale.t('绿色农产品种植基地', 'Green agricultural production base'),
    label: locale.t('产地直连 · 品质可追溯', 'Source-direct · Traceable quality'),
  },
  {
    image: '/file/info/3c26336725224041b2a2f4542020b018.jpg',
    alt: locale.t('田间新鲜农产品', 'Fresh produce from the field'),
    label: locale.t('时令鲜品 · 稳定供给', 'Seasonal produce · Reliable supply'),
  },
  {
    image: '/file/order/tea.png',
    alt: locale.t('湘西特色茶叶', 'Xiangxi speciality tea'),
    label: locale.t('特色产业 · 品牌增值', 'Local speciality · Brand value'),
  },
])

const currentHeroSlide = computed(() => heroSlides.value[activeHeroSlide.value] ?? heroSlides.value[0]!)

function stopHeroCarousel() {
  if (!heroTimer) return
  window.clearInterval(heroTimer)
  heroTimer = 0
}

function startHeroCarousel() {
  stopHeroCarousel()
  if (heroPaused.value || window.matchMedia('(prefers-reduced-motion: reduce)').matches) return
  heroTimer = window.setInterval(() => {
    activeHeroSlide.value = (activeHeroSlide.value + 1) % heroSlides.value.length
  }, 6000)
}

function selectHeroSlide(index: number) {
  activeHeroSlide.value = (index + heroSlides.value.length) % heroSlides.value.length
  startHeroCarousel()
}

function pauseHeroCarousel() {
  heroPaused.value = true
  stopHeroCarousel()
}

function resumeHeroCarousel() {
  heroPaused.value = false
  startHeroCarousel()
}

function handleHeroVisibility() {
  if (document.visibilityState === 'hidden') stopHeroCarousel()
  else startHeroCarousel()
}

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
  { orderId: 1, title: '黄瓤西瓜 20kg', type: '水果', price: 6.8, ownName: '吉首合作社', address: '湘西州', picture: 'watermelon_20250513154759.png' },
  { orderId: 2, title: '明前碧螺春 500g', type: '茶叶', price: 68, ownName: '保靖茶园', address: '保靖县', picture: 'tea.png' },
  { orderId: 3, title: '紫皮洋葱 10kg', type: '蔬菜', price: 2.8, ownName: '保靖基地', address: '保靖县', picture: 'yangcong_20250513154843.png' },
  { orderId: 4, title: '新鲜水蜜桃 5kg', type: '水果', price: 12.8, ownName: '凤凰果园', address: '凤凰县', picture: 'ff485f0e71684f6fb48c23021ebf1408.jpg' },
  { orderId: 5, title: '湘西折耳根 5kg', type: '蔬菜', price: 9.9, ownName: '永顺种植户', address: '永顺县', picture: 'zheergen_20250513155020.png' },
  { orderId: 6, title: '高山甜玉米 10kg', type: '粮食', price: 4.6, ownName: '花垣合作社', address: '花垣县', picture: '71ea0e08a7ce4bb697b1d6b87a113379.webp' },
  { orderId: 7, title: '大棚鲜草莓 3kg', type: '水果', price: 38, ownName: '泸溪农场', address: '泸溪县', picture: '75e4ef70b5a64dbd9a8736446014ce27.jpg' },
  { orderId: 8, title: '高山生态大米 50kg', type: '粮油', price: 5.2, ownName: '龙山农户', address: '龙山县', picture: '02d69a4b9ad5439e9840a357fb509734.webp' },
  { orderId: 9, title: '时令鲜蔬组合 10kg', type: '蔬菜', price: 6.5, ownName: '吉首鲜蔬基地', address: '吉首市', picture: '新鲜蔬菜.png' },
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
  { title: '平台运营管理', desc: '集中处理账号与权限、交易履约、融资流程、内容审核和运营数据。', to: '/admin', icon: 'shield', metric: '管理员工作台', roles: ['SYSTEM_ADMIN'] },
] as const

const fallbackNews = [
  { title: '春耕生产服务专区上线', desc: '围绕种子、农资和农机采购场景，提供货源、融资和技术服务协同。', image: '/file/order/12be19984e374bcfbf06561571365d07.jpg' },
  { title: '绿色农产品产销对接', desc: '支持合作社发布稳定货源，买家可按品类、产地和价格快速筛选采购。', image: '/file/info/3c26336725224041b2a2f4542020b018.jpg' },
  { title: '玉米病害线上诊断提醒', desc: '发现叶片斑枯、长势异常时可上传现场图片，由平台专家辅助判断。', image: '/file/order/W020230811400645740814_ORIGIN.jpg' },
  { title: '湘西特色茶叶进入采购专区', desc: '茶园、合作社可展示产品配图、规格和库存，帮助采购方快速选品。', image: '/file/order/tea.png' },
]
const knowledgeList = ref<Knowledge[]>([])

// 资讯无自带配图时，按序轮换占位图，避免所有卡片显示同一张
const newsFallbackImages = [
  '/file/info/3c26336725224041b2a2f4542020b018.jpg',
  '/file/order/W020230811400645740814_ORIGIN.jpg',
  '/file/order/12be19984e374bcfbf06561571365d07.jpg',
  '/file/order/tea.png',
]

const productFallbackImages = [
  '/file/order/新鲜蔬菜.png',
  '/file/order/02d69a4b9ad5439e9840a357fb509734.webp',
  '/file/order/c43dcae086e34c80900885c11f0a9e4d.jpg',
  '/file/order/75e4ef70b5a64dbd9a8736446014ce27.jpg',
]

function productFallbackImage(order: TradeOrder, index = 0) {
  const text = `${order.title} ${order.type}`.toLowerCase()
  if (text.includes('鸡蛋') || text.includes('egg')) return '/file/order/fresh-eggs.webp'
  if (text.includes('西瓜')) return '/file/order/c43dcae086e34c80900885c11f0a9e4d.jpg'
  if (text.includes('水稻') || text.includes('大米') || text.includes('小麦')) return '/file/order/02d69a4b9ad5439e9840a357fb509734.webp'
  if (text.includes('茶')) return '/file/order/tea.png'
  return productFallbackImages[index % productFallbackImages.length]
}

const productTranslations: Record<string, string> = {
  'Xiangxi Fresh Strawberry': '湘西鲜草莓',
  'Mountain Eco Rice': '高山生态大米',
  'Orange Sorting and Packing Service': '柑橘分选包装服务',
  'Fresh Farm Eggs': '新鲜农家鸡蛋',
  Strawberry: '草莓',
  Kiwi: '猕猴桃',
  Rice: '水稻',
  Orange: '柑橘',
  Eggs: '鸡蛋',
  Fruit: '水果',
  Grain: '粮油',
  Service: '服务',
  Protein: '禽蛋',
}

function translateProductText(value?: string) {
  if (!value) return value
  return Object.entries(productTranslations).reduce(
    (text, [source, target]) => text.split(source).join(target),
    value,
  )
}

function localizedOrder(order: TradeOrder): TradeOrder {
  return {
    ...order,
    title: translateProductText(order.title) || order.title,
    type: translateProductText(order.type),
  }
}

const visibleBanks = computed(() => (banks.value.length ? banks.value : fallbackBanks).slice(0, 4))
const visibleExperts = computed(() => (experts.value.length ? experts.value : fallbackExperts).slice(0, 5))
const visibleOrders = computed(() =>
  (orders.value.length ? orders.value : fallbackOrders).slice(0, 9).map(localizedOrder),
)
const visibleNews = computed(() => {
  if (knowledgeList.value.length) {
    return knowledgeList.value.slice(0, 4).map((k, index) => {
      const picture = k.picPath || k.picture
      const fallbackImage = newsFallbackImages[index % newsFallbackImages.length]
      return {
        title: k.title,
        desc: k.content?.slice(0, 80) || k.category || '平台资讯',
        content: k.content || '暂无更多内容。',
        image: picture?.startsWith('/') || picture?.startsWith('http') ? picture : picture ? `/file/order/${picture}` : fallbackImage,
        fallbackImage,
      }
    })
  }
  return fallbackNews.map((item) => ({ ...item, content: item.desc, fallbackImage: item.image }))
})

const roleHome = computed(() => {
  if (!session.isLoggedIn) {
    return {
      title: locale.t('助力农企互联，帮扶金融服务', 'Connecting agriculture, trade and finance'),
      desc: locale.t('连接农户、买家、专家与银行，让农产品交易、融资申请和技术服务在一个平台内闭环。', 'Connect farmers, buyers, experts and banks through one trusted agricultural service platform.'),
      badge: locale.t('数字农业 · 融销协同 · 可信服务', 'Digital agriculture · Trusted collaboration'),
    }
  }
  return {
    FARMER: { title: locale.t('农户经营工作台', 'Farmer workspace'), desc: locale.t('聚合货源发布、融资申请、材料补充和专家咨询，帮助经营信息进入交易与金融流程。', 'Publish products, apply for finance and consult agricultural experts.'), badge: locale.t('农户角色', 'Farmer') },
    BUYER: { title: locale.t('买家采购工作台', 'Buyer workspace'), desc: locale.t('聚焦农产品浏览、购物车、收货信息和采购订单，让采购过程清晰可追踪。', 'Browse products and manage carts, addresses and purchase orders.'), badge: locale.t('买家角色', 'Buyer') },
    EXPERT: { title: locale.t('专家服务工作台', 'Expert workspace'), desc: locale.t('集中处理农技问答、预约咨询、个人资料和知识发布，支撑农户生产决策。', 'Handle consultations, bookings and agricultural guidance.'), badge: locale.t('专家角色', 'Expert') },
    BANK: { title: locale.t('银行融资工作台', 'Finance workspace'), desc: locale.t('面向贷款产品维护、农户意向匹配和融资申请审批，银行承担最终审批责任。', 'Maintain loan products and review farmer finance applications.'), badge: locale.t('银行角色', 'Bank') },
    SYSTEM_ADMIN: { title: locale.t('平台运营控制台', 'Platform operations workspace'), desc: locale.t('集中处理账号权限、交易履约监督、融资流程监管、内容审核和运营数据。', 'Manage access, trade fulfilment, finance workflows, content review and operational data.'), badge: locale.t('系统管理员', 'Administrator') },
  }[session.role]
})

const heroActions = computed<Array<{ to: string; label: string; icon: HomeIconName; theme: 'green' | 'light' }>>(() => {
  if (!session.isLoggedIn) return [{ to: '/auth', label: locale.t('登录选择角色', 'Sign in'), icon: 'user', theme: 'green' }]
  if (session.role === 'SYSTEM_ADMIN') return [{ to: '/admin', label: locale.t('进入管理控制台', 'Open management console'), icon: 'shield', theme: 'green' }]
  if (session.role === 'FARMER') return [{ to: '/trade', label: locale.t('发布货源', 'Publish products'), icon: 'leaf', theme: 'green' }, { to: '/finance', label: locale.t('申请融资', 'Apply for finance'), icon: 'bank', theme: 'light' }]
  if (session.role === 'BUYER') return [{ to: '/trade', label: locale.t('浏览农产品', 'Browse products'), icon: 'leaf', theme: 'green' }, { to: '/cart', label: locale.t('查看购物车', 'View cart'), icon: 'cart', theme: 'light' }]
  if (session.role === 'EXPERT') return [{ to: '/experts', label: locale.t('处理专家服务', 'Open expert services'), icon: 'expert', theme: 'green' }]
  return [{ to: '/finance', label: locale.t('处理融资业务', 'Open finance services'), icon: 'bank', theme: 'green' }]
})

const visibleServiceCards = computed(() =>
  serviceCards.filter((item) => !session.isLoggedIn || item.roles.includes(session.role)),
)

const dashboardStats = computed(() => {
  if (session.role === 'SYSTEM_ADMIN' && session.isLoggedIn) {
    return [
      { label: '角色体系', value: 5, desc: '覆盖五类平台身份', icon: 'shield' },
      { label: '货源与交易', value: visibleOrders.value.length, desc: '跟踪货源发布与订单履约', icon: 'leaf' },
      { label: '融资业务', value: visibleBanks.value.length, desc: '监督产品、申请与审批进度', icon: 'bank' },
      { label: '内容与专家', value: visibleExperts.value.length, desc: '管理专家服务与平台内容', icon: 'expert' },
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
  { title: '农产品直采直销', desc: '产地货源直连买家，省去中间环节，价格与来源可追溯。', tag: '融销核心', icon: 'leaf', to: '/trade', roles: ['FARMER', 'BUYER'] },
  { title: '涉农金融撮合', desc: '对接银行贷款产品，按额度、利率、还款方式一站式申请。', tag: '融资加速', icon: 'bank', to: '/finance', roles: ['FARMER', 'BANK'] },
  { title: '专家远程指导', desc: '在线问答 + 预约咨询，覆盖种植、植保、品牌运营多领域。', tag: '技术护航', icon: 'expert', to: '/experts', roles: ['FARMER', 'EXPERT'] },
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
  document.addEventListener('visibilitychange', handleHeroVisibility)
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

onActivated(startHeroCarousel)
onDeactivated(stopHeroCarousel)

onBeforeUnmount(() => {
  stopHeroCarousel()
  document.removeEventListener('visibilitychange', handleHeroVisibility)
})
</script>

<template>
  <section class="home-page">
    <section class="portal-hero">
      <div class="portal-hero__overlay">
        <span class="portal-hero__eyebrow">{{ roleHome.badge }}</span>
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
        <ul class="portal-hero__assurances" aria-label="平台服务保障">
          <li><AppIcon name="check" /> {{ locale.t('角色权限清晰', 'Clear role permissions') }}</li>
          <li><AppIcon name="check" /> {{ locale.t('业务进度可追踪', 'Traceable workflows') }}</li>
          <li><AppIcon name="check" /> {{ locale.t('数据统一沉淀', 'Unified business data') }}</li>
        </ul>
      </div>

      <div class="portal-hero__visual" aria-label="融销协同业务示意">
        <div class="portal-preview">
          <header class="portal-preview__header">
            <div>
              <span>{{ locale.t('融销协同 / 业务工作台', 'AGRILINK / WORKSPACE') }}</span>
              <strong>{{ locale.t('融销协同工作台', 'Finance & trade workspace') }}</strong>
            </div>
            <em><i></i> {{ locale.t('服务正常', 'Online') }}</em>
          </header>
          <div
            class="portal-preview__media portal-carousel"
            aria-roledescription="carousel"
            :aria-label="locale.t('首页农业服务轮播图', 'Agricultural service carousel')"
            @mouseenter="pauseHeroCarousel"
            @mouseleave="resumeHeroCarousel"
            @focusin="pauseHeroCarousel"
            @focusout="resumeHeroCarousel"
          >
            <Transition name="hero-slide" mode="out-in">
              <img
                :key="currentHeroSlide.image"
                class="portal-hero__image"
                :src="currentHeroSlide.image"
                :alt="currentHeroSlide.alt"
                width="720"
                height="420"
              />
            </Transition>
            <span class="portal-carousel__caption"><AppIcon name="shield" /> {{ currentHeroSlide.label }}</span>
            <button
              class="portal-carousel__arrow portal-carousel__arrow--prev"
              type="button"
              :aria-label="locale.t('上一张轮播图', 'Previous slide')"
              @click="selectHeroSlide(activeHeroSlide - 1)"
            ><AppIcon name="arrow" /></button>
            <button
              class="portal-carousel__arrow portal-carousel__arrow--next"
              type="button"
              :aria-label="locale.t('下一张轮播图', 'Next slide')"
              @click="selectHeroSlide(activeHeroSlide + 1)"
            ><AppIcon name="arrow" /></button>
            <div class="portal-carousel__dots" role="tablist" :aria-label="locale.t('选择轮播图', 'Choose slide')">
              <button
                v-for="(slide, index) in heroSlides"
                :key="slide.image"
                type="button"
                :class="{ 'is-active': index === activeHeroSlide }"
                :aria-label="`${locale.t('显示第', 'Show slide')} ${index + 1}`"
                :aria-selected="index === activeHeroSlide"
                role="tab"
                @click="selectHeroSlide(index)"
              ></button>
            </div>
          </div>
          <div class="portal-preview__flow">
            <div><AppIcon name="leaf" /><span><small>{{ locale.t('步骤一', 'STEP 01') }}</small>{{ locale.t('货源发布', 'Publish') }}</span></div>
            <AppIcon class="portal-preview__arrow" name="arrow" />
            <div><AppIcon name="cart" /><span><small>{{ locale.t('步骤二', 'STEP 02') }}</small>{{ locale.t('采购成交', 'Purchase') }}</span></div>
            <AppIcon class="portal-preview__arrow" name="arrow" />
            <div><AppIcon name="bank" /><span><small>{{ locale.t('步骤三', 'STEP 03') }}</small>{{ locale.t('融资服务', 'Finance') }}</span></div>
          </div>
          <footer>
            <span>{{ locale.t('农户 · 买家 · 专家 · 银行', 'Farmer · Buyer · Expert · Bank') }}</span>
            <strong>{{ locale.t('一站协同', 'One platform') }}</strong>
          </footer>
        </div>
      </div>
    </section>

    <section class="home-metrics" aria-label="平台数据概览">
      <article v-for="item in dashboardStats" :key="item.label">
        <span><AppIcon :name="item.icon" /></span>
        <div>
          <strong>{{ item.value }}</strong>
          <p>{{ item.label }}</p>
          <small>{{ item.desc }}</small>
        </div>
      </article>
    </section>

    <p v-if="notice" class="alert portal-alert">{{ notice }}</p>

    <section class="portal-section capability-section" id="home-capability" aria-label="项目能力速览">
      <div class="portal-heading">
        <h2>{{ locale.t('项目能做什么', 'What the platform provides') }}</h2>
        <p>{{ locale.t('覆盖农产品「融、销、技、管」的一站式服务能力速览', 'One place for agricultural trade, finance, expertise and management.') }}</p>
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
        <span class="section-kicker">{{ locale.t('协同流程', 'Workflow') }}</span>
        <h2>{{ locale.t('一条链路完成融销协同', 'A connected finance and trade workflow') }}</h2>
        <p>{{ session.isLoggedIn ? '当前角色只展示允许访问的业务入口' : '登录后按五类角色隔离业务入口，状态统一留痕' }}</p>
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
        <h2>{{ locale.t('平台运营与风险管理', 'Platform operations and risk management') }}</h2>
        <p>{{ locale.t('统一处理账号权限、交易履约、融资流程、内容审核与运营数据。', 'Manage access, trade fulfilment, finance workflows, content review and operational data.') }}</p>
      </div>
      <RouterLink class="button button--green" to="/admin"><AppIcon name="shield" />{{ locale.t('进入管理控制台', 'Open management console') }}</RouterLink>
    </section>

    <section class="portal-section" id="home-services">
      <div class="portal-heading">
        <h2>{{ locale.t('核心服务', 'Core services') }}</h2>
        <p>{{ locale.t('覆盖农产品交易、涉农融资、农技服务与平台运营', 'Unified access to agricultural trade, finance, expert services and platform operations.') }}</p>
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
        <h2>{{ locale.t('金融产品', 'Finance products') }}</h2>
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
        <h2>{{ locale.t('专家团队', 'Agricultural experts') }}</h2>
        <p>{{ locale.t('覆盖种植、植保、品牌和产销运营', 'Expertise in cultivation, crop protection, branding and trade.') }}</p>
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
      <div class="qa-badge">{{ locale.t('问答', 'Q&A') }}</div>
      <div>
        <h2>专家问答与预约指导</h2>
        <p>农户可提交作物、土壤、病虫害和销售运营问题，专家端可在线答复或处理预约。</p>
      </div>
      <RouterLink class="button button--green" to="/experts"><AppIcon name="arrow" />进入专家服务</RouterLink>
    </section>

    <section v-if="!showAdminOnly" class="portal-section" id="home-news">
      <div class="portal-heading portal-heading--with-action">
        <div class="portal-heading__copy">
          <h2>平台资讯</h2>
          <p>农业服务、金融产品和产销协同动态</p>
        </div>
        <RouterLink class="button button--ghost" to="/experts">
          <AppIcon name="expert" />立即咨询
        </RouterLink>
      </div>
      <div class="news-list">
        <article v-for="item in visibleNews" :key="item.title" class="news-item">
          <AppImage class="news-item__media" :src="item.image" :fallback-src="item.fallbackImage" :alt="item.title" ratio="16 / 9" icon="leaf" />
          <div>
            <h3>{{ item.title }}</h3>
            <p>{{ item.desc }}</p>
            <details class="news-detail">
              <summary>查看详情</summary>
              <p>{{ item.content }}</p>
            </details>
          </div>
        </article>
      </div>
    </section>

    <section v-if="showProductSection" class="portal-section" id="home-products">
      <div class="portal-heading">
        <h2>{{ locale.t('最新农产品', 'Latest products') }}</h2>
        <p>{{ locale.t('产地直供，支持加入购物车并生成采购订单', 'Source-direct products ready for cart and purchase orders.') }}</p>
      </div>
      <div class="product-row">
        <button
          v-for="(order, index) in visibleOrders"
          :key="order.orderId"
          class="product-tile product-tile--button"
          type="button"
          :aria-label="`查看${order.title}详情`"
          @click="selectedProduct = order"
        >
          <AppImage class="product-tile__media" :src="imageSrc(order.picture)" :fallback-src="productFallbackImage(order, index)" :alt="order.title" ratio="1 / 0.82" icon="leaf" />
          <h3>{{ order.title }}</h3>
          <p>{{ order.address || '产地待补充' }} · {{ order.type || '农产品' }}</p>
          <div class="product-tile__footer">
            <strong>￥{{ order.price ?? '-' }}</strong>
            <span>查看详情 <AppIcon name="arrow" /></span>
          </div>
        </button>
      </div>
    </section>

    <Transition name="modal-spring">
      <div v-if="selectedProduct" class="modal-overlay" role="presentation" @click.self="selectedProduct = null">
        <div class="modal modal--wide" role="dialog" aria-modal="true" aria-label="农产品详情">
          <div class="section-title">
            <div>
              <span class="eyebrow"><AppIcon name="leaf" />农产品详情</span>
              <h2>{{ selectedProduct.title }}</h2>
            </div>
            <button class="button button--ghost button--small" type="button" @click="selectedProduct = null">关闭</button>
          </div>
          <div class="grid grid--two order-detail-body">
            <AppImage
              class="order-detail-media"
              :src="imageSrc(selectedProduct.picture)"
              :fallback-src="productFallbackImage(selectedProduct)"
              :alt="selectedProduct.title"
              ratio="4 / 3"
              icon="leaf"
            />
            <div class="order-detail-info">
              <div class="tag-row">
                <span class="tag tag--green">{{ selectedProduct.type || '农产品' }}</span>
                <span class="tag">{{ selectedProduct.address || '产地待补充' }}</span>
              </div>
              <strong class="price">￥{{ selectedProduct.price ?? '-' }}/{{ selectedProduct.unit || '斤' }}</strong>
              <dl class="order-detail-specs">
                <div><dt>规格</dt><dd>{{ selectedProduct.spec || '待补充' }}</dd></div>
                <div><dt>库存</dt><dd>{{ selectedProduct.stock ?? '-' }} {{ selectedProduct.unit || '斤' }}</dd></div>
                <div><dt>起订量</dt><dd>{{ selectedProduct.minPurchase ?? 1 }} {{ selectedProduct.unit || '斤' }}</dd></div>
                <div><dt>供货方</dt><dd>{{ selectedProduct.ownName || '产地供货方' }}</dd></div>
              </dl>
              <p class="order-detail-content">{{ selectedProduct.content || '暂无更多货源说明。' }}</p>
              <RouterLink
                v-if="session.role === 'BUYER'"
                class="button button--green"
                to="/trade"
                @click="selectedProduct = null"
              >
                <AppIcon name="cart" />前往采购
              </RouterLink>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </section>
</template>

<style scoped>
.portal-heading--with-action {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  max-width: none;
}

.portal-heading__copy {
  max-width: 760px;
}

.product-tile--button {
  width: 100%;
  color: inherit;
  font: inherit;
  text-align: left;
  cursor: pointer;
}

.product-tile--button:focus-visible {
  outline: 3px solid rgba(16, 185, 129, 0.28);
  outline-offset: 3px;
}

.product-tile__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.product-tile__footer span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  color: var(--color-primary);
  font-size: 13px;
  font-weight: 800;
}

.product-tile__footer .app-icon {
  width: 15px;
  height: 15px;
}

@media (max-width: 640px) {
  .portal-heading--with-action {
    align-items: stretch;
    flex-direction: column;
  }

  .portal-heading--with-action .button {
    width: 100%;
  }
}
</style>
