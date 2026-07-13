<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import AppImage from '@/components/AppImage.vue'
import PageHeader from '@/components/ui/PageHeader.vue'
import ModuleTabs from '@/components/ui/ModuleTabs.vue'
import SummaryStrip from '@/components/ui/SummaryStrip.vue'
import Pager from '@/components/ui/Pager.vue'
import { api } from '@/api/client'
import { useSessionStore } from '@/stores/session'
import { upsertLocalCart } from '@/utils/localCart'
import type { ShoppingCart, TradeOrder } from '@/types/domain'

const session = useSessionStore()
const route = useRoute()
const router = useRouter()
const orders = ref<TradeOrder[]>([])
const keyword = ref('')
const loading = ref(true)
const submitting = ref(false)
const message = ref('')
const error = ref('')
const cartCounts = reactive<Record<number, number>>({})
const editingOrderId = ref<number | null>(null)
const orderPage = ref(1)
const orderPageSize = 9
type TradeTab = 'browse' | 'publish'
const allTradeTabs: Array<{ value: TradeTab; label: string; farmerOnly?: boolean }> = [
  { value: 'browse', label: '货源浏览' },
  { value: 'publish', label: '发布与管理', farmerOnly: true },
]
// 「发布与管理」仅农户可见；买家只做浏览与采购。
const tradeTabs = computed(() =>
  allTradeTabs.filter((tab) => !tab.farmerOnly || session.role === 'FARMER'),
)
// 买家查看的商品详情。
const detailOrder = ref<TradeOrder | null>(null)
function openOrderDetail(order: TradeOrder) {
  detailOrder.value = order
}

// 货源发布表单字段与后端 TradeOrderRequest 保持一致。
const form = reactive({
  title: '高山生态大米 50kg',
  price: 5.2,
  content: '当季新米，合作社统一烘干入库，可按需分批发货。',
  type: '粮油',
  picture: '',
  ownName: session.userName || 'farmer-demo',
  address: '湘西州吉首市',
  stock: 1200,
  spec: '50kg/袋',
  unit: '斤',
  minPurchase: 10,
})

const editForm = reactive({
  title: '',
  price: 0,
  content: '',
  type: '',
  picture: '',
  ownName: '',
  address: '',
  stock: 0,
  spec: '',
  unit: '斤',
  minPurchase: 1,
})

const fallbackOrders: TradeOrder[] = [
  {
    orderId: 1,
    title: '富硒猕猴桃 20kg',
    type: '水果',
    price: 8.6,
    content: '冷链分拣后发货，适合商超和社区团购采购。',
    ownName: '吉首合作社',
    address: '湘西州',
    picture: 'watermelon_20250513154759.png',
    stock: 800,
    spec: '20kg/箱',
    unit: '斤',
    minPurchase: 20,
  },
  {
    orderId: 2,
    title: '高山生态大米 50kg',
    type: '粮油',
    price: 5.2,
    content: '稻谷自然晾晒，低温仓储，支持批量采购。',
    ownName: 'farmer-demo',
    address: '龙山县',
    picture: 'tea.png',
    stock: 1200,
    spec: '50kg/袋',
    unit: '斤',
    minPurchase: 10,
  },
  {
    orderId: 3,
    title: '新鲜紫皮洋葱 10kg',
    type: '蔬菜',
    price: 2.8,
    content: '产地直供，规格稳定，可对接餐饮和批发市场。',
    ownName: '保靖种植基地',
    address: '保靖县',
    picture: 'yangcong_20250513154843.png',
    stock: 600,
    spec: '10kg/袋',
    unit: '斤',
    minPurchase: 10,
  },
]

// 首页货源不可用时使用演示数据兜底。
const filteredOrders = computed(() => {
  const text = keyword.value.trim().toLowerCase()
  if (!text) return orders.value
  return orders.value.filter((order) =>
    [order.title, order.type, order.ownName, order.address].some((value) =>
      String(value ?? '').toLowerCase().includes(text),
    ),
  )
})

const pagedOrders = computed(() => {
  const start = (orderPage.value - 1) * orderPageSize
  return filteredOrders.value.slice(start, start + orderPageSize)
})

const orderPageCount = computed(() => Math.max(1, Math.ceil(filteredOrders.value.length / orderPageSize)))

const selectedCartCount = computed(() =>
  Object.entries(cartCounts).reduce((sum, [, count]) => sum + Math.max(1, Number(count) || 1), 0),
)

const ownOrders = computed(() => {
  const ownName = session.userName || 'farmer-demo'
  return orders.value.filter((order) => order.ownName === ownName || order.ownName === form.ownName)
})
const managedOrders = computed(() => (session.role === 'FARMER' ? ownOrders.value : []))
const activeTab = computed<TradeTab>(() =>
  // 非农户即使带 ?tab=publish 也强制回到浏览页。
  route.query.tab === 'publish' && session.role === 'FARMER' ? 'publish' : 'browse',
)
const ownOrderIds = computed(() => new Set(ownOrders.value.map((order) => order.orderId)))
const canManageOrder = (order: TradeOrder) => session.role === 'FARMER' && ownOrderIds.value.has(order.orderId)

function setTradeTab(tab: TradeTab) {
  router.replace({ query: { ...route.query, tab } })
}

function orderStatusLabel(status?: number) {
  if (status === 1) return '已上架'
  if (status === 2) return '已下架'
  return '待审核'
}

function orderStatusClass(status?: number) {
  if (status === 1) return 'tag tag--green'
  if (status === 2) return 'tag tag--red'
  return 'tag tag--amber'
}

function imageSrc(picture?: string) {
  const first = picture?.split(/\s+/)[0]
  if (!first) return ''
  return first.startsWith('http') || first.startsWith('/') ? first : `/file/order/${first}`
}

// 图片预览先转为 data URL，便于无文件服务时演示。
function handleImageFile(event: Event, target: typeof form | typeof editForm) {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = () => {
    target.picture = String(reader.result || '')
  }
  reader.readAsDataURL(file)
}

async function loadOrders() {
  loading.value = true
  error.value = ''
  try {
    const data = await api.get<TradeOrder[]>('/api/trade/orders')
    orders.value = data?.length ? data : fallbackOrders
  } catch (err) {
    orders.value = fallbackOrders
    error.value = err instanceof Error ? `后端暂不可用：${err.message}` : '后端暂不可用，已显示演示货源。'
  } finally {
    for (const order of orders.value) {
      if (order.orderId && !cartCounts[order.orderId]) {
        cartCounts[order.orderId] = 1
      }
    }
    loading.value = false
  }
}

function updateCartCount(orderId: number, value: number) {
  cartCounts[orderId] = Math.max(1, Math.floor(Number.isFinite(value) ? value : 1))
}

function changeOrderPage(delta: number) {
  orderPage.value = Math.min(Math.max(1, orderPage.value + delta), orderPageCount.value)
}

// 游客加入购物车时先写入本地存储。
async function addToCart(order: TradeOrder) {
  message.value = ''
  error.value = ''
  const currentCount = cartCounts[order.orderId]
  const count = Math.max(1, Math.floor(typeof currentCount === 'number' && Number.isFinite(currentCount) ? currentCount : 1))
  const ownName = session.userName || 'buyer-demo'
  try {
    await api.post<ShoppingCart>(
      '/api/trade/shopping-cart',
      { orderId: order.orderId, count, ownName },
      { role: 'BUYER' },
    )
    message.value = `已将「${order.title}」加入购物车，数量 ${count}。`
  } catch {
    upsertLocalCart(order, count, ownName)
    message.value = `后端购物车暂不可用，已将「${order.title}」保存到本地购物车，数量 ${count}。`
  }
}

async function publishOrder() {
  submitting.value = true
  message.value = ''
  error.value = ''
  try {
    const created = await api.post<TradeOrder>('/api/trade/orders', form, { role: 'FARMER' })
    orders.value = [created, ...orders.value]
    message.value = '货源已发布，买家端可在农产品交易中浏览。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '货源发布失败。'
  } finally {
    submitting.value = false
  }
}

function startEditOrder(order: TradeOrder) {
  editingOrderId.value = order.orderId
  editForm.title = order.title
  editForm.price = order.price ?? 0
  editForm.content = order.content ?? ''
  editForm.type = order.type ?? ''
  editForm.picture = order.picture ?? ''
  editForm.ownName = order.ownName ?? session.userName ?? 'farmer-demo'
  editForm.address = order.address ?? ''
  editForm.stock = order.stock ?? 0
  editForm.spec = order.spec ?? ''
  editForm.unit = order.unit ?? '斤'
  editForm.minPurchase = order.minPurchase ?? 1
}

function cancelEditOrder() {
  editingOrderId.value = null
}

async function saveOrder() {
  if (!editingOrderId.value) return

  submitting.value = true
  message.value = ''
  error.value = ''
  const payload = { ...editForm }
  const target = orders.value.find((order) => order.orderId === editingOrderId.value)

  try {
    const updated = await api.put<TradeOrder>(`/api/trade/orders/${editingOrderId.value}`, payload, {
      role: 'FARMER',
    })
    if (target) Object.assign(target, updated)
    message.value = `货源「${updated.title || payload.title}」已保存。`
    editingOrderId.value = null
  } catch {
    if (target) Object.assign(target, payload)
    message.value = `后端货源保存接口暂不可用，已在当前页面更新「${payload.title}」。`
    editingOrderId.value = null
  } finally {
    submitting.value = false
  }
}

async function changeOrderStatus(order: TradeOrder, orderStatus: number) {
  message.value = ''
  error.value = ''
  try {
    const updated = await api.patch<TradeOrder>(
      `/api/trade/orders/${order.orderId}/status`,
      { orderStatus, cooperationName: order.cooperationName || order.ownName },
      { role: 'FARMER' },
    )
    order.orderStatus = updated.orderStatus
    message.value = `货源「${order.title}」已${orderStatus === 1 ? '上架' : '下架'}。`
  } catch {
    order.orderStatus = orderStatus
    message.value = `后端状态接口暂不可用，已在当前页面将「${order.title}」标记为${orderStatus === 1 ? '已上架' : '已下架'}。`
  }
}

async function deleteOrder(order: TradeOrder) {
  if (typeof window !== 'undefined' && !window.confirm(`确认删除货源「${order.title}」？`)) return

  message.value = ''
  error.value = ''
  try {
    await api.delete<void>(`/api/trade/orders/${order.orderId}`, { role: 'FARMER' })
    orders.value = orders.value.filter((item) => item.orderId !== order.orderId)
    message.value = `货源「${order.title}」已删除。`
  } catch {
    orders.value = orders.value.filter((item) => item.orderId !== order.orderId)
    message.value = `后端删除接口暂不可用，已在当前页面移除「${order.title}」。`
  }
}

onMounted(loadOrders)

watch(keyword, () => {
  orderPage.value = 1
})

watch(orderPageCount, () => {
  orderPage.value = Math.min(orderPage.value, orderPageCount.value)
})
</script>

<template>
  <section class="page">
    <PageHeader eyebrow="农产品交易" icon="leaf" title="货源浏览与农户发布" desc="流程：农户发布货源，买家浏览下单，平台跟踪采购状态。">
      <template #actions>
        <label class="field search-field">
          <span>搜索货源</span>
          <input v-model.trim="keyword" placeholder="商品、类型、产地" />
        </label>
        <button class="button button--ghost" type="button" @click="loadOrders">
          <AppIcon name="search" />刷新
        </button>
      </template>
    </PageHeader>

    <p v-if="message" class="alert">{{ message }}</p>
    <p v-if="error" class="alert alert--error">{{ error }}</p>

    <ModuleTabs
      :model-value="activeTab"
      :options="tradeTabs"
      aria-label="农产品交易操作"
      @update:model-value="setTradeTab"
    />

    <SummaryStrip
      :items="[
        { value: filteredOrders.length, label: loading ? '正在读取货源' : '当前可浏览货源' },
        { value: new Set(orders.map((order) => order.type || '农产品')).size, label: '覆盖品类' },
        { value: session.roleLabel, label: '当前业务角色' },
        {
          value: session.role === 'FARMER' ? ownOrders.length : selectedCartCount,
          label: session.role === 'FARMER' ? '我的货源' : '列表加购数量',
        },
      ]"
    />

    <form v-if="editingOrderId" class="section panel form order-edit-panel" @submit.prevent="saveOrder">
      <div class="section-title">
        <div>
          <h2>编辑货源</h2>
          <p>直接维护商品名称、价格、库存、产地和图文说明。</p>
        </div>
      </div>
      <label class="field"><span>商品名称</span><input v-model.trim="editForm.title" required /></label>
      <label class="field"><span>品类</span><input v-model.trim="editForm.type" required /></label>
      <label class="field"><span>单价</span><input v-model.number="editForm.price" type="number" min="0.01" step="0.01" required /></label>
      <label class="field"><span>单位</span><input v-model.trim="editForm.unit" required /></label>
      <label class="field"><span>规格</span><input v-model.trim="editForm.spec" /></label>
      <label class="field"><span>库存</span><input v-model.number="editForm.stock" type="number" min="0" required /></label>
      <label class="field"><span>最小起订量</span><input v-model.number="editForm.minPurchase" type="number" min="1" required /></label>
      <label class="field"><span>发布账号</span><input v-model.trim="editForm.ownName" required /></label>
      <label class="field"><span>产地地址</span><input v-model.trim="editForm.address" required /></label>
      <label class="field"><span>图片文件名</span><input v-model.trim="editForm.picture" /></label>
      <label class="field"><span>上传图片预览</span><input type="file" accept="image/*" @change="handleImageFile($event, editForm)" /></label>
      <div v-if="imageSrc(editForm.picture)" class="image-preview">
        <img :src="imageSrc(editForm.picture)" alt="货源图片预览" />
      </div>
      <label class="field"><span>货源说明</span><textarea v-model.trim="editForm.content" required /></label>
      <div class="toolbar">
        <button class="button button--green" type="submit" :disabled="submitting">
          <AppIcon name="check" />{{ submitting ? '保存中' : '保存修改' }}
        </button>
        <button class="button button--ghost" type="button" @click="cancelEditOrder">取消</button>
      </div>
    </form>

    <template v-if="activeTab === 'browse'">
      <section class="section grid">
        <article v-for="order in pagedOrders" :key="order.orderId" class="card product-card">
          <AppImage class="product-thumb" :src="imageSrc(order.picture)" :alt="order.title" ratio="16 / 10" icon="leaf" />
          <div class="tag-row">
            <span class="tag tag--green">{{ order.type || '农产品' }}</span>
            <span class="tag">{{ order.address || '产地待补充' }}</span>
            <span :class="orderStatusClass(order.orderStatus)">{{ orderStatusLabel(order.orderStatus) }}</span>
          </div>
          <h3>{{ order.title }}</h3>
          <p>{{ order.content || '暂无货源详情' }}</p>
          <div class="tag-row">
            <span class="tag">{{ order.spec || '规格待补充' }}</span>
            <span class="tag">库存 {{ order.stock ?? '-' }} {{ order.unit || '斤' }}</span>
            <span class="tag tag--amber">起订 {{ order.minPurchase ?? 1 }} {{ order.unit || '斤' }}</span>
          </div>
          <div class="card__footer">
            <strong class="price">￥{{ order.price ?? '-' }}/{{ order.unit || '斤' }}</strong>
            <div v-if="session.role === 'BUYER'" class="cart-control">
              <label class="inline-field">
                <span>数量</span>
                <input
                  class="inline-input"
                  :value="cartCounts[order.orderId] ?? 1"
                  type="number"
                  min="1"
                  @change="updateCartCount(order.orderId, Number(($event.target as HTMLInputElement).value))"
                />
              </label>
              <button class="button button--ghost button--small" type="button" @click="openOrderDetail(order)">
                <AppIcon name="search" />详情
              </button>
              <button class="button button--ghost" type="button" @click="addToCart(order)">
                <AppIcon name="cart" />加入购物车
              </button>
            </div>
            <div v-else-if="canManageOrder(order)" class="toolbar">
              <button class="button button--small" type="button" @click="startEditOrder(order)">编辑</button>
              <button class="button button--small" type="button" @click="changeOrderStatus(order, 1)">上架</button>
              <button class="button button--ghost button--small" type="button" @click="changeOrderStatus(order, 2)">下架</button>
              <button class="button button--danger button--small" type="button" @click="deleteOrder(order)">删除</button>
            </div>
            <button v-else class="button button--ghost button--small" type="button" @click="openOrderDetail(order)">
              <AppIcon name="search" />查看详情
            </button>
          </div>
        </article>
      </section>
      <Pager :page="orderPage" :page-count="orderPageCount" @change="changeOrderPage" />
    </template>

    <section v-else class="section grid grid--two farmer-publish-layout">
      <form class="panel form" @submit.prevent="publishOrder">
        <div class="section-title">
          <div>
            <h2>发布货源</h2>
            <p>农户端创建货源，后台可审核状态，买家端可下单。</p>
          </div>
        </div>
        <label class="field"><span>商品名称</span><input v-model.trim="form.title" required /></label>
        <label class="field"><span>品类</span><input v-model.trim="form.type" required /></label>
        <label class="field"><span>单价</span><input v-model.number="form.price" type="number" min="0.01" step="0.01" required /></label>
        <label class="field"><span>单位</span><input v-model.trim="form.unit" required placeholder="斤/箱/袋" /></label>
        <label class="field"><span>规格</span><input v-model.trim="form.spec" placeholder="如 50kg/袋" /></label>
        <label class="field"><span>库存</span><input v-model.number="form.stock" type="number" min="0" required /></label>
        <label class="field"><span>最小起订量</span><input v-model.number="form.minPurchase" type="number" min="1" required /></label>
        <label class="field"><span>发布账号</span><input v-model.trim="form.ownName" required /></label>
        <label class="field"><span>产地地址</span><input v-model.trim="form.address" required /></label>
        <label class="field"><span>图片文件名</span><input v-model.trim="form.picture" placeholder="可选，如 tea.png" /></label>
        <label class="field"><span>上传图片预览</span><input type="file" accept="image/*" @change="handleImageFile($event, form)" /></label>
        <div v-if="imageSrc(form.picture)" class="image-preview">
          <img :src="imageSrc(form.picture)" alt="货源图片预览" />
        </div>
        <label class="field"><span>货源说明</span><textarea v-model.trim="form.content" required /></label>
        <button class="button button--green" type="submit" :disabled="submitting">
          <AppIcon name="plus" />{{ submitting ? '发布中' : '发布货源' }}
        </button>
      </form>

      <div class="panel farmer-order-manager">
        <div class="section-title">
          <div>
            <h2>我的商品管理</h2>
            <p>已发布商品可在这里编辑、上架、下架或删除。</p>
          </div>
        </div>
        <div class="mini-list">
          <span v-for="order in managedOrders" :key="order.orderId" class="stack-row farmer-order-row">
            <strong>{{ order.title }} · ￥{{ order.price ?? '-' }}/{{ order.unit || '斤' }}</strong>
            <small>
              {{ order.type || '农产品' }} · {{ order.address || '产地待补充' }}
              <span :class="orderStatusClass(order.orderStatus)">{{ orderStatusLabel(order.orderStatus) }}</span>
            </small>
            <div class="toolbar">
              <button class="button button--small" type="button" @click="startEditOrder(order)">编辑</button>
              <button class="button button--small" type="button" @click="changeOrderStatus(order, 1)">上架</button>
              <button class="button button--ghost button--small" type="button" @click="changeOrderStatus(order, 2)">下架</button>
              <button class="button button--danger button--small" type="button" @click="deleteOrder(order)">删除</button>
            </div>
          </span>
          <span v-if="!managedOrders.length">暂无自己的商品，发布后会出现在这里。</span>
        </div>
      </div>

      <div class="panel">
        <div class="section-title">
          <div>
            <h2>交易流程</h2>
            <p>围绕商品、购物车、采购订单和状态审批形成闭环。</p>
          </div>
        </div>
        <div class="timeline-list">
          <div><span class="tag tag--green">1</span> 农户发布货源，填写品类、价格、产地和说明。</div>
          <div><span class="tag tag--green">2</span> 买家浏览商品并加入购物车，维护采购数量。</div>
          <div><span class="tag tag--green">3</span> 买家生成采购订单，管理员或业务方处理状态。</div>
          <div><span class="tag tag--green">4</span> 农户在个人中心查看我的发布和销售关联。</div>
        </div>
      </div>
    </section>

    <Transition name="modal-spring">
      <div v-if="detailOrder" class="modal-overlay" role="presentation" @click.self="detailOrder = null">
        <div class="modal modal--wide" role="dialog" aria-modal="true" aria-label="商品详情">
          <div class="section-title">
            <div>
              <span class="eyebrow"><AppIcon name="leaf" />商品详情</span>
              <h2>{{ detailOrder.title }}</h2>
            </div>
            <button class="button button--ghost button--small" type="button" @click="detailOrder = null">关闭</button>
          </div>
          <div class="grid grid--two order-detail-body">
            <AppImage
              class="order-detail-media"
              :src="imageSrc(detailOrder.picture)"
              :alt="detailOrder.title"
              ratio="4 / 3"
              icon="leaf"
            />
            <div class="order-detail-info">
              <div class="tag-row">
                <span class="tag tag--green">{{ detailOrder.type || '农产品' }}</span>
                <span class="tag">{{ detailOrder.address || '产地待补充' }}</span>
                <span :class="orderStatusClass(detailOrder.orderStatus)">{{ orderStatusLabel(detailOrder.orderStatus) }}</span>
              </div>
              <strong class="price">￥{{ detailOrder.price ?? '-' }}/{{ detailOrder.unit || '斤' }}</strong>
              <dl class="order-detail-specs">
                <div><dt>规格</dt><dd>{{ detailOrder.spec || '待补充' }}</dd></div>
                <div><dt>库存</dt><dd>{{ detailOrder.stock ?? '-' }} {{ detailOrder.unit || '斤' }}</dd></div>
                <div><dt>起订量</dt><dd>{{ detailOrder.minPurchase ?? 1 }} {{ detailOrder.unit || '斤' }}</dd></div>
                <div><dt>供货方</dt><dd>{{ detailOrder.ownName || '产地供货方' }}</dd></div>
              </dl>
              <p class="order-detail-content">{{ detailOrder.content || '暂无货源详情。' }}</p>
              <button
                v-if="session.role === 'BUYER'"
                class="button button--green"
                type="button"
                @click="addToCart(detailOrder); detailOrder = null"
              >
                <AppIcon name="cart" />加入购物车
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </section>
</template>
