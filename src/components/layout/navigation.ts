import type { UserRole } from '@/types/domain'

export type NavRouteName = 'home' | 'trade' | 'finance' | 'experts' | 'cart' | 'profile' | 'admin'

export type NavItem = {
  to: string
  name: NavRouteName
  label: string
  icon: 'home' | 'leaf' | 'bank' | 'expert' | 'cart' | 'user' | 'shield'
  children?: Array<{ to: string; label: string }>
}

// 各角色的业务导航（不含个人中心，个人中心统一从侧边栏底部独立区进入）。
export const navByRole: Record<UserRole, NavItem[]> = {
  FARMER: [
    {
      to: '/',
      name: 'home',
      label: '首页',
      icon: 'home',
      children: [
        { to: '/#home-capability', label: '项目能力速览' },
        { to: '/#home-dashboard', label: '业务工作台' },
        { to: '/#home-finance', label: '金融产品' },
        { to: '/#home-services', label: '核心服务' },
        { to: '/#home-experts', label: '专家团队' },
        { to: '/#home-qa', label: '专家问答' },
        { to: '/#home-tips', label: '栽培小技巧' },
        { to: '/#home-news', label: '平台资讯' },
        { to: '/#home-products', label: '最新农产品' },
      ],
    },
    {
      to: '/trade',
      name: 'trade',
      label: '农产品交易',
      icon: 'leaf',
      children: [
        { to: '/trade?tab=browse', label: '货源浏览' },
        { to: '/trade?tab=publish', label: '农产品发布' },
      ],
    },
    {
      to: '/finance',
      name: 'finance',
      label: '融资申请',
      icon: 'bank',
      children: [
        { to: '/finance?tab=intro', label: '基础介绍' },
        { to: '/finance?tab=apply', label: '申请登记' },
        { to: '/finance?tab=result', label: '审批结果' },
      ],
    },
    {
      to: '/experts',
      name: 'experts',
      label: '专家助力',
      icon: 'expert',
      children: [
        { to: '/experts?tab=experts', label: '专家列表' },
        { to: '/experts?tab=records', label: '我的咨询' },
      ],
    },
  ],
  BUYER: [
    {
      to: '/',
      name: 'home',
      label: '首页',
      icon: 'home',
      children: [
        { to: '/#home-capability', label: '项目能力速览' },
        { to: '/#home-dashboard', label: '业务工作台' },
        { to: '/#home-services', label: '核心服务' },
        { to: '/#home-tips', label: '栽培小技巧' },
        { to: '/#home-news', label: '平台资讯' },
        { to: '/#home-products', label: '最新农产品' },
      ],
    },
    { to: '/trade', name: 'trade', label: '农产品采购', icon: 'leaf' },
    { to: '/cart', name: 'cart', label: '采购车', icon: 'cart' },
  ],
  EXPERT: [
    {
      to: '/',
      name: 'home',
      label: '首页',
      icon: 'home',
      children: [
        { to: '/#home-capability', label: '项目能力速览' },
        { to: '/#home-dashboard', label: '业务工作台' },
        { to: '/#home-services', label: '核心服务' },
        { to: '/#home-experts', label: '专家团队' },
        { to: '/#home-qa', label: '专家问答' },
        { to: '/#home-tips', label: '栽培小技巧' },
        { to: '/#home-news', label: '平台资讯' },
      ],
    },
    { to: '/experts', name: 'experts', label: '咨询工单', icon: 'expert' },
  ],
  BANK: [
    {
      to: '/',
      name: 'home',
      label: '首页',
      icon: 'home',
      children: [
        { to: '/#home-capability', label: '项目能力速览' },
        { to: '/#home-dashboard', label: '业务工作台' },
        { to: '/#home-finance', label: '金融产品' },
        { to: '/#home-services', label: '核心服务' },
        { to: '/#home-tips', label: '栽培小技巧' },
        { to: '/#home-news', label: '平台资讯' },
      ],
    },
    { to: '/finance', name: 'finance', label: '融资审核', icon: 'bank' },
  ],
  SYSTEM_ADMIN: [
    {
      to: '/',
      name: 'home',
      label: '首页',
      icon: 'home',
      children: [
        { to: '/#home-capability', label: '项目能力速览' },
        { to: '/#home-dashboard', label: '业务工作台' },
        { to: '/#home-services', label: '核心服务' },
        { to: '/#home-tips', label: '栽培小技巧' },
      ],
    },
    { to: '/admin', name: 'admin', label: '后台管理', icon: 'shield' },
  ],
}

// 个人中心：所有角色共用，作为侧边栏底部独立入口。
export const profileNavItem: NavItem = {
  to: '/profile',
  name: 'profile',
  label: '个人中心',
  icon: 'user',
}

export const roleHints: Record<UserRole, string> = {
  FARMER: '发布货源、提交融资申请、补充材料、咨询专家',
  BUYER: '浏览货源、维护购物车、生成并跟踪采购单',
  EXPERT: '维护专家资料、答复问答、处理预约、发布农技知识',
  BANK: '维护贷款产品、匹配融资农户、审批融资申请',
  SYSTEM_ADMIN: '管理用户角色、监管交易状态、查看融资进度、维护内容',
}

export const roleCodes: Record<UserRole, string> = {
  FARMER: 'Dev Farmer',
  BUYER: 'Dev Buyer',
  EXPERT: 'Dev Expert',
  BANK: 'Dev Finance',
  SYSTEM_ADMIN: 'Dev Admin',
}
