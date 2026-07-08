<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import AppIcon from '@/components/AppIcon.vue'
import { api } from '@/api/client'
import { useSessionStore } from '@/stores/session'
import {
  clearLocalCart,
  readLocalCart,
  removeLocalCartItem,
  updateLocalCartCount,
} from '@/utils/localCart'
import type { Purchase, ShoppingCart, TradeOrder } from '@/types/domain'

const session = useSessionStore()
const carts = ref<ShoppingCart[]>([])
const orders = ref<TradeOrder[]>([])
const purchases = ref<Purchase[]>([])
const address = ref('湘西州吉首市收货点')
const loading = ref(true)
const submitting = ref(false)
const message = ref('')
const error = ref('')
const localShoppingIds = ref<Set<number>>(new Set())
const expandedPurchaseId = ref<number | null>(null)
const cancelReason = ref('')

// ???????????????????????????????
const fallbackOrders: TradeOrder[] = [
  { orderId: 1, title: '富硒猕猴桃 20kg', type: '水果', price: 8.6, ownName: '吉首合作社', address: '湘西州' },
  { orderId: 2, title: '高山生态大米 50kg', type: '粮油', price: 5.2, ownName: '龙山农户', address: '龙山县' },
]

const fallbackCarts: ShoppingCart[] = [
  { shoppingId: 1001, orderId: 1, count: 2, ownName: 'buyer-demo' },
  { shoppingId: 1002, orderId: 2, count: 1, ownName: 'buyer-demo' },
]

const orderMap = computed(() => new Map(orders.value.map((order) => [order.orderId, order])))
// ???????????????????????????
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

function mergeOrders(remoteOrders: TradeOrder[], localOrders: TradeOrder[]) {
  const orderMap = new Map<number, TradeOrder>()
  for (const order of [...remoteOrders, ...localOrders]) {
    orderMap.set(order.orderId, order)
  }
  return [...orderMap.values()]
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

// ??????????????????????????????
async function loadCart() {
  loading.value = true
  error.value = ''
  message.value = ''
  const ownName = session.userName || 'buyer-demo'
  const localEntries = readLocalCart(ownName)
  let cartFailed = false
  try {
    const [cartData, orderData, purchaseData] = await Promise.all([
      api.get<ShoppingCart[]>(`/api/trade/shopping-cart/owners/${encodeURIComponent(ownName)}`).catch(() => {
        cartFailed = true
        return [] as ShoppingCart[]
      }),
      api.get<TradeOrder[]>('/api/trade/orders').catch(() => fallbackOrders),
      api.get<Purchase[]>(`/api/trade/purchases/owners/${encodeURIComponent(ownName)}`).catch(() => []),
    ])
    const localCarts = localEntries.map((entry) => entry.cart)
    carts.value = cartData?.length || localCarts.length ? [...(cartData ?? []), ...localCarts] : fallbackCarts
    orders.value = mergeOrders(orderData?.length ? orderData : fallbackOrders, localEntries.map((entry) => entry.order))
    purchases.value = purchaseData ?? []
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

// ??????????????????????????????
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
</script>

<template>
  <section class="page">
    <div class="section-title">
      <div>
        <span class="eyebrow"><AppIcon name="cart" />购物车</span>
        <h2>选购清单与采购结算</h2>
        <p>对接 `/api/trade/shopping-cart` 和 `/api/trade/purchases`。</p>
      </div>
      <button class="button button--ghost" type="button" @click="loadCart">
        <AppIcon name="search" />刷新
      </button>
    </div>

    <p v-if="message" class="alert">{{ message }}</p>
    <p v-if="error" class="alert alert--error">{{ error }}</p>

    <section class="grid grid--two">
      <div class="panel">
        <div class="section-title">
          <div>
            <h2>购物车明细</h2>
            <p>{{ loading ? '正在加载' : `共 ${cartRows.length} 条商品记录` }}</p>
          </div>
        </div>
        <div v-if="cartRows.length" class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>商品</th>
                <th>单价</th>
                <th>数量</th>
                <th>小计</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in cartRows" :key="row.shoppingId">
                <td>{{ row.title }}</td>
                <td>￥{{ row.price || '-' }}</td>
                <td>
                  <input
                    class="inline-input"
                    :value="row.count"
                    type="number"
                    min="1"
                    @change="updateCount(row, Number(($event.target as HTMLInputElement).value))"
                  />
                </td>
                <td>￥{{ row.sum.toFixed(2) }}</td>
                <td>
                  <button class="button button--danger button--small" type="button" @click="removeItem(row)">
                    移除
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else class="empty">购物车暂无商品，请先到农产品交易页加入货源。</div>
      </div>

      <form class="panel form" @submit.prevent="createPurchase">
        <div class="section-title">
          <div>
            <h2>生成采购订单</h2>
            <p>确认收货地址后，按购物车明细创建采购记录。</p>
          </div>
        </div>
        <div class="metric">
          <strong>￥{{ totalPrice.toFixed(2) }}</strong>
          <span>预计采购金额</span>
        </div>
        <label class="field"><span>采购账号</span><input :value="session.userName || 'buyer-demo'" disabled /></label>
        <label class="field"><span>收货地址</span><input v-model.trim="address" required /></label>
        <button class="button button--green" type="submit" :disabled="submitting || !cartRows.length">
          <AppIcon name="check" />{{ submitting ? '提交中' : '提交采购订单' }}
        </button>
      </form>
    </section>

    <section class="section">
      <div class="section-title">
        <div>
          <h2>采购记录</h2>
          <p>展示当前买家已创建的采购订单。</p>
        </div>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>订单号</th>
              <th>买家</th>
              <th>金额</th>
              <th>地址</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="purchase in purchases" :key="purchase.purchaseId">
              <tr>
                <td>{{ purchase.purchaseId }}</td>
                <td>{{ purchase.ownName }}</td>
                <td>￥{{ purchase.totalPrice ?? '-' }}</td>
                <td>{{ purchase.address }}</td>
                <td><span :class="statusTagClass(purchase.purchaseStatus)">{{ purchaseStatusLabel(purchase.purchaseStatus) }}</span></td>
                <td class="toolbar">
                  <button class="button button--small" type="button" @click="expandedPurchaseId = expandedPurchaseId === purchase.purchaseId ? null : purchase.purchaseId">
                    详情
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
                </td>
              </tr>
              <tr v-if="expandedPurchaseId === purchase.purchaseId">
                <td colspan="6">
                  <div class="detail-panel">
                    <div class="compact-timeline">
                      <span
                        v-for="step in purchaseTimeline(purchase.purchaseStatus)"
                        :key="step.label"
                        :class="{ 'is-active': step.active, 'is-done': step.done }"
                      >
                        {{ step.label }}
                      </span>
                    </div>
                    <div v-if="purchase.details?.length" class="mini-list">
                      <span v-for="detail in purchase.details" :key="detail.orderId">
                        {{ detailTitle(detail) }} · ￥{{ detail.unitPrice }}/单位 · {{ detail.count }} 件
                      </span>
                    </div>
                    <p v-else class="empty">当前订单暂无明细，可能来自后端简化返回。</p>
                    <label v-if="(purchase.purchaseStatus ?? 0) === 0" class="field compact-field">
                      <span>取消原因</span>
                      <input v-model.trim="cancelReason" placeholder="可选，填写取消说明" />
                    </label>
                    <p v-if="purchase.cancelReason">取消原因：{{ purchase.cancelReason }}</p>
                  </div>
                </td>
              </tr>
            </template>
            <tr v-if="!purchases.length">
              <td colspan="6">暂无采购记录。</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </section>
</template>
