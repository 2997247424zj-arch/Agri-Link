import type { ShoppingCart, TradeOrder } from '@/types/domain'

// ??????????????????????????????
const LOCAL_CART_KEY = 'agri-link-local-cart'

export interface LocalCartEntry {
  cart: ShoppingCart
  order: TradeOrder
}

function canUseStorage() {
  return typeof localStorage !== 'undefined'
}

export function readLocalCart(owner?: string): LocalCartEntry[] {
  if (!canUseStorage()) return []

  try {
    const parsed = JSON.parse(localStorage.getItem(LOCAL_CART_KEY) || '[]') as LocalCartEntry[]
    const entries = Array.isArray(parsed) ? parsed : []
    return owner ? entries.filter((entry) => entry.cart.ownName === owner) : entries
  } catch {
    return []
  }
}

function writeLocalCart(entries: LocalCartEntry[]) {
  if (canUseStorage()) {
    localStorage.setItem(LOCAL_CART_KEY, JSON.stringify(entries))
  }
}

// ????????????????????? NaN???????
function normalizeCount(count: number) {
  return Math.max(1, Math.floor(Number.isFinite(count) ? count : 1))
}

export function upsertLocalCart(order: TradeOrder, count: number, ownName: string) {
  const entries = readLocalCart()
  const nextCount = normalizeCount(count)
  const existing = entries.find((entry) => entry.cart.orderId === order.orderId && entry.cart.ownName === ownName)

  if (existing) {
    existing.cart.count += nextCount
    existing.order = order
    writeLocalCart(entries)
    return existing
  }

  const created: LocalCartEntry = {
    cart: {
      shoppingId: Date.now(),
      orderId: order.orderId,
      count: nextCount,
      ownName,
    },
    order,
  }
  entries.unshift(created)
  writeLocalCart(entries)
  return created
}

export function updateLocalCartCount(shoppingId: number, count: number) {
  const entries = readLocalCart()
  const entry = entries.find((item) => item.cart.shoppingId === shoppingId)
  if (entry) {
    entry.cart.count = normalizeCount(count)
    writeLocalCart(entries)
  }
}

export function removeLocalCartItem(shoppingId: number) {
  writeLocalCart(readLocalCart().filter((entry) => entry.cart.shoppingId !== shoppingId))
}

export function clearLocalCart(owner?: string) {
  if (!owner) {
    writeLocalCart([])
    return
  }

  writeLocalCart(readLocalCart().filter((entry) => entry.cart.ownName !== owner))
}
