// 前端领域模型，与后端 DTO/实体字段保持一致。
export type UserRole = 'BUYER' | 'FARMER' | 'EXPERT' | 'BANK' | 'SYSTEM_ADMIN'

export interface ApiResponse<T> {
  success: boolean
  code: number
  message: string
  data: T
  timestamp: string
}

export interface FileUploadResponse {
  fileName: string
  originalName: string
  contentType: string
  size: number
  url: string
}

export interface User {
  userName: string
  password?: string
  nickName?: string
  phone?: string
  identityNum?: string
  address?: string
  role: UserRole | string
  integral?: number
  credit?: number
  avatar?: string
  realName?: string
}

export interface AuthResponse {
  user?: User
  userName?: string
  nickName?: string
  role?: UserRole | string
}

export interface Bank {
  bankId: number
  bankName: string
  introduce?: string
  bankPhone?: string
  money?: number
  rate?: number
  repayment?: string
}

export interface Address {
  id: number
  ownName: string
  consignee: string
  phone: string
  addressDetail: string
  isDefault: number
}

// 交易、购物车与采购订单相关类型。
export interface Finance {
  financeId: number
  bankId: number
  ownName: string
  realName?: string
  phone?: string
  idNum?: string
  status?: number
  remark?: string
  money?: number
  rate?: number
  repayment?: string
  materials?: string[]
  repaymentPlan?: Array<{
    period: string
    amount: number
    dueDate?: string
    status?: string
  }>
  createTime?: string
  updateTime?: string
}

export interface FinancingIntention {
  id: number
  userName: string
  realName: string
  address: string
  amount: number
  application?: string
  item?: string
  repaymentPeriod?: string
  area?: string
  phone?: string
  createTime?: string
  updateTime?: string
}

export interface Expert {
  userName: string
  realName?: string
  phone?: string
  profession?: string
  position?: string
  belong?: string
}

export interface Question {
  id?: number
  questionId?: number
  title?: string
  content?: string
  question?: string
  answer?: string
  ownName?: string
  questioner?: string
  expertName?: string
  phone?: string
  plantName?: string
  attachments?: string[]
  status?: number
  createTime?: string
}

export interface Reserve {
  id: number
  expertName: string
  questioner: string
  area: string
  address: string
  plantName: string
  soilCondition: string
  plantCondition: string
  plantDetail: string
  phone: string
  message?: string
  answer?: string
  appointmentTime?: string
  serviceMode?: string
  status?: number
}

// 后台统计与管理视图相关类型。
export interface TradeOrder {
  orderId: number
  title: string
  price?: number
  content?: string
  orderStatus?: number
  type?: string
  picture?: string
  ownName?: string
  cooperationName?: string
  address?: string
  stock?: number
  spec?: string
  unit?: string
  minPurchase?: number
  createTime?: string
}

export interface ShoppingCart {
  shoppingId: number
  orderId: number
  count: number
  ownName: string
  createTime?: string
  updateTime?: string
}

export interface PurchaseDetailRequest {
  orderId: number
  unitPrice: number
  count: number
}

export interface Purchase {
  purchaseId: number
  ownName: string
  purchaseType: number
  totalPrice?: number
  address: string
  purchaseStatus?: number
  details?: PurchaseDetailRequest[]
  cancelReason?: string
  deliveryNo?: string
  createTime?: string
  updateTime?: string
}

export interface AdminOverview {
  userCount: number
  orderCount: number
  purchaseCount: number
  financeApplicationCount: number
  financingIntentionCount: number
  knowledgeCount: number
  usersByRole?: Partial<Record<UserRole | string, number>>
}

export interface Knowledge {
  knowledgeId: number
  title: string
  content?: string
  picPath?: string
  ownName?: string
  picture?: string
  userName?: string
  category?: string
  status?: number
  createTime?: string
  updateTime?: string
}
