<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { RouterLink } from 'vue-router'
import AppIcon from '@/components/AppIcon.vue'
import Pager from '@/components/ui/Pager.vue'
import { api } from '@/api/client'
import { useSessionStore } from '@/stores/session'
import {
  clearLocalCart,
  readLocalCart,
  removeLocalCartItem,
  updateLocalCartCount,
} from '@/utils/localCart'
import type { Address, Purchase, ShoppingCart, TradeOrder } from '@/types/domain'

const session = useSessionStore()
const carts = ref<ShoppingCart[]>([])
const orders = ref<TradeOrder[]>([])
const purchases = ref<Purchase[]>([])
const addresses = ref<Address[]>([])
const address = ref('湘西州吉首市收货点')
const selectedAddressId = ref<number | null>(null)
const loading = ref(true)
const submitting = ref(false)
const message = ref('')
const error = ref('')
const localShoppingIds = ref<Set<number>>(new Set())
const expandedPurchaseId = ref<number | null>(null)
const cancelReason = ref('')
const purchasePage = ref(1)
const purchasePageSize = 5
const addressForm = reactive({
  consignee: '',
  phone: '',
  addressDetail: '',
  isDefault: 0,
})

const fallbackOrders: TradeOrder[] = [
  { orderId: 1, title: '富硒猕猴桃 20kg', type: '水果', price: 8.6, ownName: '吉首合作社', address: '湘西州' },
  { orderId: 2, title: '高山生态大米 50kg', type: '粮油', price: 5.2, ownName: '龙山农户', address: '龙山县' },
]

const fallbackCarts: ShoppingCart[] = [
  { shoppingId: 1001, orderId: 1, count: 2, ownName: 'buyer-demo' },
  { shoppingId: 1002, orderId: 2, count: 1, ownName: 'buyer-demo' },
]

const orderMap = computed(() => new Map(orders.value.map((order) => [order.orderId, order])))
const cartRows = computed(() =>
  carts.value.map((cart) => {
    const order = orderMap.value.get(cart.orderId)
    return {
      ...cart,
      order,
      title: order?.title ?? `货源 #${cart.orderId}`,
      price: order?.price ?? 0,
      sum: (order?.price ?? 0) * cart.count,
    }
  }),
)
const totalPrice = computed(() => cartRows.value.reduce((sum, row) => sum + row.sum, 0))
const cartItemCount = computed(() => cartRows.value.reduce((sum, row) => sum + row.count, 0))
const activePurchaseCount = computed(() =>
  purchases.value.filter((purchase) => (purchase.purchaseStatus ?? 0) !== 2 && (purchase.purchaseStatus ?? 0) !== 5).length,
)
const procurementStats = computed(() => [
  { label: '待采购商品', value: cartRows.value.length },
  { label: '采购件数', value: cartItemCount.value },
  { label: '预计金额', value: `¥${totalPrice.value.toFixed(2)}` },
  { label: '进行中订单', value: activePurchaseCount.value },
])
const pagedPurchases = computed(() => {
  const start = (purchasePage.value - 1) * purchasePageSize
  return purchases.value.slice(start, start + purchasePageSize)
})
const purchasePageCount = computed(() => Math.max(1, Math.ceil(purchases.value.length / purchasePageSize)))

function mergeOrders(remoteOrders: TradeOrder[], localOrders: TradeOrder[]) {
  const nextOrderMap = new Map<number, TradeOrder>()
  for (const order of [...remoteOrders, ...localOrders]) {
    nextOrderMap.set(order.orderId, order)
  }
  return [...nextOrderMap.values()]
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
  if (status === 1 || status === 3 || status === 4 || status === 5) return 'tag tag--green'
  if (status === 2) return 'tag tag--red'
  return 'tag tag--amber'
}

function purchaseTimeline(status?: number) {
  return [
    { label: '已提交', active: true, done: true },
    { label: '已确认', active: (status ?? 0) >= 1 && status !== 2, done: (status ?? 0) >= 1 && status !== 2 },
    { label: '已发货', active: (status ?? 0) >= 3, done: (status ?? 0) >= 3 },
    { label: '已收货', active: (status ?? 0) >= 4, done: (status ?? 0) >= 4 },
    { label: status === 2 ? '已取消' : '已完成', active: status === 2 || status === 5, done: status === 5 },
  ]
}

function detailTitle(detail: { orderId: number }) {
  return orderMap.value.get(detail.orderId)?.title ?? `货源 #${detail.orderId}`
}

function changePurchasePage(delta: number) {
  purchasePage.value = Math.min(Math.max(1, purchasePage.value + delta), purchasePageCount.value)
}

async function loadCart() {
  loading.value = true
  error.value = ''
  message.value = ''
  const ownName = session.userName || 'buyer-demo'
  const localEntries = readLocalCart(ownName)
  let cartFailed = false
  try {
    const [cartData, orderData, purchaseData, addressData] = await Promise.all([
      api.get<ShoppingCart[]>(`/api/trade/shopping-cart/owners/${encodeURIComponent(ownName)}`).catch(() => {
        cartFailed = true
        return [] as ShoppingCart[]
      }),
      api.get<TradeOrder[]>('/api/trade/orders').catch(() => fallbackOrders),
      api.get<Purchase[]>(`/api/trade/purchases/owners/${encodeURIComponent(ownName)}`).catch(() => []),
      api.get<Address[]>(`/api/addresses/owners/${encodeURIComponent(ownName)}`).catch(() => []),
    ])
    const localCarts = localEntries.map((entry) => entry.cart)
    carts.value = cartData?.length || localCarts.length ? [...(cartData ?? []), ...localCarts] : fallbackCarts
    orders.value = mergeOrders(orderData?.length ? orderData : fallbackOrders, localEntries.map((entry) => entry.order))
    purchases.value = purchaseData ?? []
    addresses.value = addressData ?? []
    const defaultAddress = addresses.value.find((item) => item.isDefault === 1) ?? addresses.value[0]
    if (defaultAddress) selectAddress(defaultAddress)
    localShoppingIds.value = new Set(localCarts.map((cart) => cart.shoppingId))
    if (cartFailed && localCarts.length) {
      message.value = '后端购物车暂不可用，已读取本地保存的加购记录。'
    }
  } catch (err) {
    const localCarts = localEntries.map((entry) => entry.cart)
    carts.value = localCarts.length ? localCarts : fallbackCarts
    orders.value = mergeOrders(fallbackOrders, localEntries.map((entry) => entry.order))
    localShoppingIds.value = new Set(localCarts.map((cart) => cart.shoppingId))
    error.value = err instanceof Error ? `后端暂不可用：${err.message}` : '后端暂不可用，已显示演示购物车。'
  } finally {
    loading.value = false
  }
}

function selectAddress(item: Address) {
  selectedAddressId.value = item.id
  address.value = `${item.consignee} ${item.phone} ${item.addressDetail}`
}

async function createAddress() {
  message.value = ''
  error.value = ''
  try {
    const created = await api.post<Address>('/api/addresses', {
      ownName: session.userName || 'buyer-demo',
      ...addressForm,
    }, { role: 'BUYER' })
    if (created.isDefault === 1) {
      addresses.value.forEach((item) => { item.isDefault = 0 })
    }
    addresses.value = [created, ...addresses.value]
    selectAddress(created)
    Object.assign(addressForm, { consignee: '', phone: '', addressDetail: '', isDefault: 0 })
    message.value = '收货地址已新增并选中。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '收货地址新增失败。'
  }
}

async function setDefaultAddress(item: Address) {
  message.value = ''
  error.value = ''
  try {
    const updated = await api.put<Address>(`/api/addresses/${item.id}`, { ...item, isDefault: 1 }, { role: 'BUYER' })
    addresses.value.forEach((addressItem) => { addressItem.isDefault = addressItem.id === item.id ? 1 : 0 })
    Object.assign(item, updated)
    selectAddress(item)
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
    await api.delete<void>(`/api/addresses/${item.id}`, { role: 'BUYER' })
    addresses.value = addresses.value.filter((addressItem) => addressItem.id !== item.id)
    if (selectedAddressId.value === item.id) {
      selectedAddressId.value = null
      address.value = ''
    }
    message.value = '收货地址已删除。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '收货地址删除失败。'
  }
}

async function updateCount(cart: ShoppingCart, count: number) {
  const nextCount = Math.max(1, Math.floor(Number.isFinite(count) ? count : 1))
  message.value = ''
  error.value = ''
  if (localShoppingIds.value.has(cart.shoppingId)) {
    updateLocalCartCount(cart.shoppingId, nextCount)
    cart.count = nextCount
    message.value = '本地购物车数量已更新。'
    return
  }

  try {
    await api.put<ShoppingCart>(
      `/api/trade/shopping-cart/${cart.shoppingId}`,
      { orderId: cart.orderId, count: nextCount, ownName: cart.ownName },
      { role: 'BUYER' },
    )
    cart.count = nextCount
    message.value = '购物车数量已更新。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '购物车更新失败。'
  }
}

async function removeItem(cart: ShoppingCart) {
  message.value = ''
  error.value = ''
  if (localShoppingIds.value.has(cart.shoppingId)) {
    removeLocalCartItem(cart.shoppingId)
    carts.value = carts.value.filter((item) => item.shoppingId !== cart.shoppingId)
    localShoppingIds.value.delete(cart.shoppingId)
    message.value = '已移除本地购物车记录。'
    return
  }

  try {
    await api.delete<void>(`/api/trade/shopping-cart/${cart.shoppingId}`, { role: 'BUYER' })
    carts.value = carts.value.filter((item) => item.shoppingId !== cart.shoppingId)
    message.value = '已移除购物车记录。'
  } catch (err) {
    error.value = err instanceof Error ? err.message : '移除失败。'
  }
}

async function createPurchase() {
  submitting.value = true
  message.value = ''
  error.value = ''
  const ownName = session.userName || 'buyer-demo'
  const details = cartRows.value.map((row) => ({ orderId: row.orderId, unitPrice: row.price, count: row.count }))
  try {
    const purchase = await api.post<Purchase>(
      '/api/trade/purchases',
      {
        ownName,
        purchaseType: 1,
        address: address.value,
        details,
      },
      { role: 'BUYER' },
    )
    const remoteCarts = carts.value.filter((cart) => !localShoppingIds.value.has(cart.shoppingId))
    await Promise.allSettled(remoteCarts.map((cart) => api.delete<void>(`/api/trade/shopping-cart/${cart.shoppingId}`, { role: 'BUYER' })))
    clearLocalCart(ownName)
    carts.value = []
    localShoppingIds.value = new Set()
    purchases.value = [purchase, ...purchases.value]
    purchase.details = details
    message.value = '采购订单已生成，购物车已清空。'
  } catch {
    const purchase: Purchase = {
      purchaseId: Date.now(),
      ownName,
      purchaseType: 1,
      totalPrice: Number(totalPrice.value.toFixed(2)),
      address: address.value,
      purchaseStatus: 0,
      details,
    }
    clearLocalCart(ownName)
    carts.value = []
    localShoppingIds.value = new Set()
    purchases.value = [purchase, ...purchases.value]
    message.value = '后端采购接口暂不可用，已生成本地采购记录并清空本地购物车。'
  } finally {
    submitting.value = false
  }
}

async function updatePurchaseStatus(purchase: Purchase, purchaseStatus: number) {
  message.value = ''
  error.value = ''
  const payload = {
    purchaseStatus,
    cancelReason: purchaseStatus === 2 ? cancelReason.value || '买家取消采购' : purchase.cancelReason,
  }
  try {
    const updated = await api.patch<Purchase>(
      `/api/trade/purchases/${purchase.purchaseId}/status`,
      payload,
      { role: 'BUYER' },
    )
    purchase.purchaseStatus = updated.purchaseStatus
    purchase.cancelReason = updated.cancelReason ?? payload.cancelReason
    message.value = `采购订单 #${purchase.purchaseId} 状态已更新。`
  } catch {
    purchase.purchaseStatus = purchaseStatus
    purchase.cancelReason = payload.cancelReason
    message.value = `后端采购状态接口暂不可用，已在当前页面更新订单 #${purchase.purchaseId}。`
  } finally {
    cancelReason.value = ''
  }
}

onMounted(loadCart)

watch(purchasePageCount, () => {
  purchasePage.value = Math.min(purchasePage.value, purchasePageCount.value)
})
</script>

<template>
  <section class="page buyer-procurement-page">
    <div class="buyer-procurement-hero">
      <div>
        <span class="eyebrow"><AppIcon name="cart" />采购中心</span>
        <h2>选品、核价、下单和跟踪集中处理</h2>
        <p>把购物车、收货信息、订单状态放在同一个工作台里，减少买家在多个页面之间来回切换。</p>
      </div>
      <div class="buyer-procurement-actions">
        <RouterLink class="button button--light" to="/trade"><AppIcon name="leaf" />继续选品</RouterLink>
        <button class="button button--ghost" type="button" @click="loadCart">
          <AppIcon name="search" />刷新
        </button>
      </div>
    </div>

    <p v-if="message" class="alert">{{ message }}</p>
    <p v-if="error" class="alert alert--error">{{ error }}</p>

    <div class="buyer-procurement-stats">
      <div v-for="stat in procurementStats" :key="stat.label" class="metric">
        <strong>{{ stat.value }}</strong>
        <span>{{ stat.label }}</span>
      </div>
    </div>

    <section class="panel address-book">
      <div class="section-title">
        <div><h2>收货地址簿</h2><p>选择已有地址，或新增常用收货信息。</p></div>
      </div>
      <div v-if="addresses.length" class="address-grid">
        <article v-for="item in addresses" :key="item.id" class="address-card" :class="{ 'is-selected': selectedAddressId === item.id }">
          <div>
            <strong>{{ item.consignee }} · {{ item.phone }}</strong>
            <p>{{ item.addressDetail }}</p>
            <span v-if="item.isDefault === 1" class="tag tag--green">默认地址</span>
          </div>
          <div class="toolbar">
            <button class="button button--small" type="button" @click="selectAddress(item)">选择</button>
            <button v-if="item.isDefault !== 1" class="button button--ghost button--small" type="button" @click="setDefaultAddress(item)">设为默认</button>
            <button class="button button--danger button--small" type="button" @click="deleteAddress(item)">删除</button>
          </div>
        </article>
      </div>
      <div v-else class="empty">暂无地址，请先新增收货地址。</div>
      <form class="address-form" @submit.prevent="createAddress">
        <label class="field"><span>收货人</span><input v-model.trim="addressForm.consignee" required /></label>
        <label class="field"><span>联系电话</span><input v-model.trim="addressForm.phone" type="tel" required /></label>
        <label class="field address-form__detail"><span>详细地址</span><input v-model.trim="addressForm.addressDetail" required /></label>
        <label class="check-field"><input v-model="addressForm.isDefault" type="checkbox" :true-value="1" :false-value="0" />设为默认地址</label>
        <button class="button button--green" type="submit"><AppIcon name="plus" />新增地址</button>
      </form>
    </section>

    <section class="buyer-procurement-layout">
      <div class="panel buyer-cart-panel">
        <div class="section-title">
          <div>
            <h2>待采购清单</h2>
            <p>{{ loading ? '正在加载' : `共 ${cartRows.length} 条商品记录` }}</p>
          </div>
        </div>

        <div v-if="cartRows.length" class="buyer-cart-list">
          <article v-for="row in cartRows" :key="row.shoppingId" class="buyer-cart-item">
            <div class="buyer-cart-item__main">
              <span class="buyer-cart-item__icon"><AppIcon name="leaf" /></span>
              <div>
                <h3>{{ row.title }}</h3>
                <p>{{ row.order?.ownName || '产地供货方' }} · {{ row.order?.address || '产地待补充' }}</p>
              </div>
            </div>
            <div class="buyer-cart-item__meta">
              <span><small>单价</small><strong>¥{{ row.price || '-' }}</strong></span>
              <label class="compact-field">
                <small>数量</small>
                <input
                  class="inline-input"
                  :value="row.count"
                  type="number"
                  min="1"
                  @change="updateCount(row, Number(($event.target as HTMLInputElement).value))"
                />
              </label>
              <span><small>小计</small><strong>¥{{ row.sum.toFixed(2) }}</strong></span>
              <button class="button button--danger button--small" type="button" @click="removeItem(row)">移除</button>
            </div>
          </article>
        </div>
        <div v-else class="empty">购物车暂无商品，请先到农产品交易页加入货源。</div>
      </div>

      <form class="panel form buyer-checkout-panel" @submit.prevent="createPurchase">
        <div class="section-title">
          <div>
            <h2>提交采购单</h2>
            <p>确认账号、地址和金额后生成采购订单。</p>
          </div>
        </div>
        <div class="buyer-checkout-total">
          <span>预计采购金额</span>
          <strong>¥{{ totalPrice.toFixed(2) }}</strong>
        </div>
        <label class="field"><span>采购账号</span><input :value="session.userName || 'buyer-demo'" disabled /></label>
        <label class="field"><span>收货地址</span><textarea v-model.trim="address" required /></label>
        <button class="button button--green" type="submit" :disabled="submitting || !cartRows.length">
          <AppIcon name="check" />{{ submitting ? '提交中' : '提交采购订单' }}
        </button>
      </form>
    </section>

    <section class="section buyer-order-section">
      <div class="section-title">
        <div>
          <h2>采购订单</h2>
          <p>查看当前买家的采购订单、流转状态和明细。</p>
        </div>
      </div>
      <div v-if="purchases.length" class="buyer-order-list">
        <article v-for="purchase in pagedPurchases" :key="purchase.purchaseId" class="buyer-order-card">
          <header>
            <div>
              <strong>#{{ purchase.purchaseId }}</strong>
              <span :class="statusTagClass(purchase.purchaseStatus)">{{ purchaseStatusLabel(purchase.purchaseStatus) }}</span>
            </div>
            <em>¥{{ purchase.totalPrice ?? '-' }}</em>
          </header>
          <p>{{ purchase.address || '收货地址待补充' }}</p>
          <div class="compact-timeline">
            <span
              v-for="step in purchaseTimeline(purchase.purchaseStatus)"
              :key="step.label"
              :class="{ 'is-active': step.active, 'is-done': step.done }"
            >
              {{ step.label }}
            </span>
          </div>
          <div v-if="expandedPurchaseId === purchase.purchaseId" class="detail-panel">
            <div v-if="purchase.details?.length" class="mini-list">
              <span v-for="detail in purchase.details" :key="detail.orderId">
                {{ detailTitle(detail) }} · ¥{{ detail.unitPrice }}/单位 · {{ detail.count }} 件
              </span>
            </div>
            <p v-else class="empty">当前订单暂无明细，可能来自后端简化返回。</p>
            <label v-if="(purchase.purchaseStatus ?? 0) === 0" class="field compact-field">
              <span>取消原因</span>
              <input v-model.trim="cancelReason" placeholder="可选，填写取消说明" />
            </label>
            <p v-if="purchase.cancelReason">取消原因：{{ purchase.cancelReason }}</p>
          </div>
          <footer class="toolbar">
            <button class="button button--small" type="button" @click="expandedPurchaseId = expandedPurchaseId === purchase.purchaseId ? null : purchase.purchaseId">
              {{ expandedPurchaseId === purchase.purchaseId ? '收起' : '详情' }}
            </button>
            <button v-if="(purchase.purchaseStatus ?? 0) === 0" class="button button--danger button--small" type="button" @click="updatePurchaseStatus(purchase, 2)">
              取消
            </button>
            <button v-if="purchase.purchaseStatus === 3" class="button button--small" type="button" @click="updatePurchaseStatus(purchase, 4)">
              确认收货
            </button>
            <button v-if="purchase.purchaseStatus === 4" class="button button--small" type="button" @click="updatePurchaseStatus(purchase, 5)">
              完成
            </button>
          </footer>
        </article>
      </div>
      <div v-else class="empty">暂无采购记录。</div>
      <Pager :page="purchasePage" :page-count="purchasePageCount" @change="changePurchasePage" />
    </section>
  </section>
</template>
