<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import AppIcon from '@/components/AppIcon.vue'
import { api } from '@/api/client'
import type { Bank, Expert, TradeOrder } from '@/types/domain'

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

const serviceCards = [
  { title: '融资服务', desc: '银行产品、融资申请、意向匹配和审批协同。', to: '/finance', icon: 'bank', metric: '3.8% 起' },
  { title: '专家助力', desc: '专家资料、在线问答、咨询预约和处理反馈。', to: '/experts', icon: 'expert', metric: '4 类服务' },
  { title: '农产品交易', desc: '商品发布、购物车、采购订单和状态流转。', to: '/trade', icon: 'leaf', metric: '产地直供' },
  { title: '后台管理', desc: '用户、货源、采购和融资申请统一管理。', to: '/admin', icon: 'shield', metric: '全流程' },
] as const

const news = [
  { title: '春耕金融服务专区上线', desc: '围绕种子、农资和农机采购场景，提供额度匹配和申请进度管理。', image: '/file/info/20e7a0d77ecf4731b28ebc1d6ca22587.jpg' },
  { title: '绿色农产品产销对接', desc: '支持合作社发布稳定货源，买家可按品类、产地和价格快速筛选采购。', image: '/file/info/b62d1d12d2bc4940956c92b79509efee.jpg' },
]

const visibleBanks = computed(() => (banks.value.length ? banks.value : fallbackBanks).slice(0, 4))
const visibleExperts = computed(() => (experts.value.length ? experts.value : fallbackExperts).slice(0, 5))
const visibleOrders = computed(() => (orders.value.length ? orders.value : fallbackOrders).slice(0, 5))

const dashboardStats = computed(() => [
  {
    label: '可选金融产品',
    value: visibleBanks.value.length,
    desc: '按额度、利率、还款方式匹配',
    icon: 'bank',
  },
  {
    label: '在架农产品',
    value: visibleOrders.value.length,
    desc: '产地直供，支持采购流转',
    icon: 'leaf',
  },
  {
    label: '入驻专家',
    value: visibleExperts.value.length,
    desc: '覆盖种植、植保、品牌运营',
    icon: 'expert',
  },
  {
    label: '协同角色',
    value: 5,
    desc: '农户、买家、专家、银行、管理员',
    icon: 'user',
  },
] as const)

const workflowSteps = [
  { title: '农户发布', desc: '维护货源、登记融资意向、提交专家问题', to: '/trade', icon: 'leaf' },
  { title: '买家采购', desc: '筛选农产品、加入购物车、生成采购订单', to: '/cart', icon: 'cart' },
  { title: '银行审批', desc: '查看融资申请、匹配潜在农户、更新审批状态', to: '/finance', icon: 'bank' },
  { title: '平台监管', desc: '统一管理用户、交易、融资和服务数据', to: '/admin', icon: 'shield' },
] as const

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
})
</script>

<template>
  <section class="home-page">
    <section class="portal-hero">
      <img class="portal-hero__image" src="/file/info/3c26336725224041b2a2f4542020b018.jpg" alt="绿色农产品" />
      <div class="portal-hero__overlay">
        <span>数字农业 · 融销协同 · 可信服务</span>
        <h1>助力农企互联，帮扶金融服务</h1>
        <p>连接农户、买家、专家与银行，让农产品交易、融资申请和技术服务在一个平台内闭环。</p>
        <div class="hero__actions">
          <RouterLink class="button button--green" to="/trade"><AppIcon name="leaf" />浏览农产品</RouterLink>
          <RouterLink class="button button--light" to="/finance"><AppIcon name="bank" />申请融资</RouterLink>
        </div>
      </div>
    </section>

    <p v-if="notice" class="alert portal-alert">{{ notice }}</p>

    <section class="portal-section dashboard-section">
      <div class="portal-heading">
        <h2>业务工作台</h2>
        <p>把交易、融资、专家服务和后台监管压缩成一个可快速进入的业务入口</p>
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
        <RouterLink v-for="step in workflowSteps" :key="step.title" :to="step.to" class="workflow-card">
          <span><AppIcon :name="step.icon" /></span>
          <div>
            <strong>{{ step.title }}</strong>
            <p>{{ step.desc }}</p>
          </div>
          <AppIcon name="arrow" />
        </RouterLink>
      </div>
    </section>

    <section class="portal-section">
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

    <section class="portal-section">
      <div class="portal-heading">
        <h2>核心服务</h2>
        <p>围绕融、销、技、管四类业务建立统一入口</p>
      </div>
      <div class="service-grid">
        <RouterLink v-for="item in serviceCards" :key="item.to" class="service-card" :to="item.to">
          <span><AppIcon :name="item.icon" /></span>
          <strong>{{ item.title }}</strong>
          <p>{{ item.desc }}</p>
          <em>{{ item.metric }}</em>
        </RouterLink>
      </div>
    </section>

    <section class="portal-section">
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

    <section class="portal-section qa-section">
      <div class="qa-badge">Q&A</div>
      <div>
        <h2>专家问答与预约指导</h2>
        <p>农户可提交作物、土壤、病虫害和销售运营问题，专家端可在线答复或处理预约。</p>
      </div>
      <RouterLink class="button button--green" to="/experts"><AppIcon name="arrow" />进入专家服务</RouterLink>
    </section>

    <section class="portal-section">
      <div class="portal-heading">
        <h2>平台资讯</h2>
        <p>农业服务、金融产品和产销协同动态</p>
      </div>
      <div class="news-list">
        <article v-for="item in news" :key="item.title" class="news-item">
          <img :src="item.image" :alt="item.title" loading="lazy" />
          <div>
            <h3>{{ item.title }}</h3>
            <p>{{ item.desc }}</p>
            <RouterLink class="text-link" to="/finance">查看详情</RouterLink>
          </div>
        </article>
      </div>
    </section>

    <section class="portal-section">
      <div class="portal-heading">
        <h2>最新农产品</h2>
        <p>产地直供，支持加入购物车并生成采购订单</p>
      </div>
      <div class="product-row">
        <article v-for="order in visibleOrders" :key="order.orderId" class="product-tile">
          <img :src="imageSrc(order.picture)" :alt="order.title" loading="lazy" />
          <h3>{{ order.title }}</h3>
          <p>{{ order.address || '产地待补充' }} · {{ order.type || '农产品' }}</p>
          <strong>￥{{ order.price ?? '-' }}</strong>
        </article>
      </div>
    </section>
  </section>
</template>
