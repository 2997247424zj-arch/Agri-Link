import { nextTick, onBeforeUnmount, onMounted, watch } from 'vue'
import { useLocaleStore } from '@/stores/locale'

const exactMessages: Record<string, string> = {
  'Agri-Link 首页': 'Agri-Link home',
  '进入个人中心': 'Open profile',
  '登录注册切换': 'Sign-in and registration tabs',
  '账号类型': 'Account type',
  '注册方式': 'Registration method',
  '跳到主要内容': 'Skip to main content',
  '展开主菜单': 'Open main menu',
  '关闭主菜单': 'Close main menu',
  '主导航': 'Main navigation',
  '平台服务保障': 'Platform service guarantees',
  '融销协同业务示意': 'Finance and trade workflow preview',
  '平台数据概览': 'Platform overview',
  '项目能力速览': 'Platform capabilities',
  '平台协同流程': 'Platform workflow',
  '登录后隔离功能': 'Role-based access after sign-in',
  '可见服务': 'Available services',
  '只展示可访问入口': 'Only accessible services are shown',
  '在架农产品': 'Listed products',
  '产地直供，支持采购流转': 'Source-direct products ready for purchasing',
  '入驻专家': 'Registered experts',
  '覆盖种植、植保、品牌运营': 'Cultivation, crop protection and branding expertise',
  '融销核心': 'Trade core',
  '农产品直采直销': 'Direct agricultural trade',
  '产地货源直连买家，省去中间环节，价格与来源可追溯。': 'Connect buyers directly with producers for traceable products and pricing.',
  '融资加速': 'Faster finance',
  '涉农金融撮合': 'Agricultural finance matching',
  '对接银行贷款产品，按额度、利率、还款方式一站式申请。': 'Compare bank products and apply by amount, rate and repayment method.',
  '技术护航': 'Expert support',
  '专家远程指导': 'Remote expert guidance',
  '在线问答 + 预约咨询，覆盖种植、植保、品牌运营多领域。': 'Online Q&A and bookings across cultivation, protection and branding.',
  '知识赋能': 'Knowledge support',
  '栽培小技巧': 'Cultivation tips',
  '按季节 / 作物汇总的实用栽培要点，帮助农户快速上手。': 'Practical seasonal crop guidance for farmers.',
  '交易闭环': 'Trade workflow',
  '采购车与订单': 'Cart and orders',
  '买家可批量维护待采购清单，一键下单并跟踪物流进度。': 'Buyers can manage cart items, place orders and track delivery.',
  '可信服务': 'Trusted service',
  '平台合规监管': 'Platform compliance',
  '管理员统一监管用户、交易和融资进度，保障数据可信。': 'Administrators supervise users, trade and finance progress.',
  '登录后按五类角色隔离业务入口，状态统一留痕': 'Sign in to access role-specific services with traceable status.',
  '当前角色只展示允许访问的业务入口': 'Only services available to the current role are shown.',
  '农户发布': 'Farmer publishing',
  '维护货源、登记融资意向、提交专家问题和申请材料': 'Manage products, finance intentions, expert questions and application materials.',
  '买家采购': 'Buyer purchasing',
  '筛选农产品、加入购物车、确认地址并生成采购订单': 'Browse products, add to cart, confirm delivery and place orders.',
  '专家服务': 'Expert services',
  '处理问题、预约、专家资料和农业知识服务': 'Handle questions, bookings, expert profiles and knowledge.',
  '银行审批': 'Bank approval',
  '查看融资申请、匹配潜在农户、填写审批意见并更新状态': 'Review applications, match farmers and update approval status.',
  '平台监管': 'Platform supervision',
  '统一管理用户、交易、融资进度和平台内容数据': 'Manage users, trade, finance progress and platform content.',
  '发布与管理货源': 'Publish and manage products',
  '农户维护产品、库存、价格和交易状态，形成可采购货源。': 'Farmers manage products, stock, prices and listing status.',
  '农户专属': 'Farmer only',
  '融资申请': 'Finance application',
  '农户选择银行产品，提交资料、补充材料并跟踪审批进度。': 'Farmers choose products, submit materials and track approval.',
  '农户发起问答和预约，专家负责答复、指导和知识沉淀。': 'Farmers ask questions and book consultations with experts.',
  '农户/专家': 'Farmers / experts',
  '农产品采购': 'Agricultural purchasing',
  '买家浏览货源、比较产地价格，并生成可追踪采购单。': 'Buyers compare products and create traceable purchase orders.',
  '买家专属': 'Buyer only',
  '购物车': 'Cart',
  '买家集中维护待采购产品、数量和下单前确认信息。': 'Buyers manage products, quantities and order details.',
  '融资审批': 'Finance approval',
  '银行维护贷款产品，查看申请资料，匹配农户并更新审批状态。': 'Banks manage loan products, review applications and update status.',
  '银行专属': 'Bank only',
  '平台运营管理': 'Platform operations',
  '集中处理账号与权限、交易履约、融资流程、内容审核和运营数据。': 'Manage access, trade fulfilment, finance workflows, content review and operational data.',
  '管理员工作台': 'Administrator workspace',
  '平台运营控制台': 'Platform operations workspace',
  '集中处理账号权限、交易履约监督、融资流程监管、内容审核和运营数据。': 'Manage access, trade fulfilment, finance workflows, content review and operational data.',
  '进入管理控制台': 'Open management console',
  '角色体系': 'Role system',
  '覆盖五类平台身份': 'Five platform roles',
  '货源与交易': 'Products and trade',
  '跟踪货源发布与订单履约': 'Track listings and order fulfilment',
  '融资业务': 'Finance operations',
  '监督产品、申请与审批进度': 'Monitor products, applications and approvals',
  '内容与专家': 'Content and experts',
  '管理专家服务与平台内容': 'Manage expert services and platform content',
  '平台运营与风险管理': 'Platform operations and risk management',
  '统一处理账号权限、交易履约、融资流程、内容审核与运营数据。': 'Manage access, trade fulfilment, finance workflows, content review and operational data.',
  '覆盖农产品交易、涉农融资、农技服务与平台运营': 'Agricultural trade, finance, expert services and platform operations',
  '按额度、利率和还款方式快速选择适合的融资产品': 'Compare finance products by amount, rate and repayment method.',
  '查看': 'View',
  '农作物栽培小技巧': 'Crop cultivation tips',
  '按作物 / 季节汇总的实用要点，帮农户少走弯路': 'Practical crop guidance organised by crop and season.',
  '栽培小技巧列表': 'Cultivation tips list',
  '向专家提问 →': 'Ask an expert →',
  '专家问答与预约指导': 'Expert Q&A and booking guidance',
  '农户可提交作物、土壤、病虫害和销售运营问题，专家端可在线答复或处理预约。': 'Farmers can ask about crops, soil, pests and sales, then receive expert guidance.',
  '进入专家服务': 'Open expert services',
  '平台资讯': 'Platform news',
  '农业服务、金融产品和产销协同动态': 'Updates on agriculture, finance and trade collaboration.',
  '查看详情': 'View details',
  '正在加载': 'Loading',
  '待审核': 'Pending review',
  '规格待补充': 'Specification not provided',
  '上一页': 'Previous page',
  '下一页': 'Next page',
  '农产品交易': 'Agricultural marketplace',
  '农产品交易操作': 'Marketplace actions',
  '搜索货源': 'Search products',
  '商品、类型、产地': 'Product, category or origin',
  '货源浏览与农户发布': 'Browse products and manage farmer listings',
  '流程：农户发布货源，买家浏览下单，平台跟踪采购状态。': 'Farmers publish products, buyers place orders, and the platform tracks fulfilment.',
  '融资服务': 'Finance services',
  '融资产品、意向匹配与审批': 'Finance products, matching and approval',
  '对接银行产品、融资申请、融资意向和银行端审批接口。': 'Manage bank products, applications, finance intentions and bank approvals.',
  '专家助力': 'Expert services',
  '专家展示、在线问答与咨询预约': 'Experts, online Q&A and consultation booking',
  '对接专家资料、问答发布、专家答复和预约处理接口。': 'Connect expert profiles, questions, answers and consultation bookings.',
  '个人中心': 'Profile',
  '资料维护与业务记录': 'Profile and business records',
  '基础资料': 'Basic information',
  '用于登录、融资申请、交易发布和专家服务识别。': 'Used for sign-in, finance, marketplace and expert services.',
  '角色专属资料': 'Role-specific information',
  '业务入口': 'Business shortcuts',
  '按当前角色快速进入对应业务。': 'Open the services available to the current role.',
  '选品、核价、下单和跟踪集中处理': 'Select, price, order and track in one place',
  '把购物车、收货信息、订单状态放在同一个工作台里，减少买家在多个页面之间来回切换。': 'Manage the cart, delivery details and order status in one workspace.',
  '收货地址簿': 'Delivery addresses',
  '选择已有地址，或新增常用收货信息。': 'Choose an existing address or add a new one.',
  '待采购清单': 'Cart items',
  '提交采购单': 'Submit purchase order',
  '确认账号、地址和金额后生成采购订单。': 'Confirm the account, address and total before placing the order.',
  '采购订单': 'Purchase orders',
  '查看当前买家的采购订单、流转状态和明细。': 'Review the buyer’s purchase orders, status and details.',
  '提交融资申请': 'Submit finance application',
  '农户侧提交申请资料，后续由银行端受理并给出审批意见。': 'Farmers submit the application and the bank reviews it.',
  '融资意向登记': 'Finance intention',
  '让银行端基于额度、用途和经营信息主动匹配农户。': 'Let banks match farmers by amount, purpose and business profile.',
  '发布农业知识': 'Publish agricultural knowledge',
  '将咨询经验沉淀为农技文章，发布后同步展示在平台资讯。': 'Turn consultation experience into articles shown in platform news.',
  '我的知识': 'My articles',
  '咨询预约': 'Consultation booking',
  '在线问答': 'Online Q&A',
  '交易流程': 'Trade workflow',
  '围绕商品、购物车、采购订单和状态审批形成闭环。': 'Connect products, carts, purchase orders and status approval.',
  '我的商品管理': 'My product management',
  '已发布商品可在这里编辑、上架、下架或删除。': 'Edit, publish, unpublish or delete listed products here.',
  '平台管理后台': 'Platform administration',
  '商品详情': 'Product details',
  '专家咨询': 'Expert consultation',
  '暂无更多内容。': 'No more content.',
  '暂无货源详情': 'No product details',
  '暂无银行产品介绍': 'No product introduction',
  '后端暂不可用，已显示演示数据。': 'The backend is unavailable; demo data is displayed.',
  '直接维护商品名称、价格、库存、产地和图文说明。': 'Manage the product name, price, stock, origin, images and description.',
  '农户端创建货源，后台可审核状态，买家端可下单。': 'Farmers create listings, administrators review them, and buyers place orders.',
  '专家可直接答复农户问题。': 'Experts can reply directly to farmer questions.',
  '查看已提交问题和专家回复。': 'Review submitted questions and expert replies.',
  '专家处理线下或线上咨询预约。': 'Experts handle online and on-site consultation bookings.',
  '查看预约状态和专家回复。': 'Review booking status and expert replies.',
  '提交后等待专家回复即可。': 'Submit and wait for the expert response.',
  '农户提交的融资申请和审批状态。': 'Finance applications and approval status submitted by farmers.',
  '农户发布的商品货源，可在交易页继续维护。': 'Products published by farmers can be managed in the marketplace.',
  '买家提交的采购订单，管理员可在后台处理状态。': 'Purchase orders submitted by buyers and supervised by administrators.',
}

const phraseMessages: Record<string, string> = {
  '发布与管理': 'Publish & manage',
  '货源浏览': 'Browse products',
  '当前可浏览货源': 'Available products',
  '正在读取货源': 'Loading products',
  '列表加购数量': 'Items added',
  '我的货源': 'My products',
  '覆盖品类': 'Categories',
  '当前业务角色': 'Current role',
  '基础信息介绍': 'Product overview',
  '融资申请及意向登记': 'Applications & intentions',
  '贷款产品维护': 'Loan product management',
  '查看审批结果': 'Approval results',
  '正在加载产品': 'Loading products',
  '银行产品': 'Bank products',
  '我的融资意向': 'My finance intentions',
  '融资意向': 'Finance intentions',
  '待审批申请': 'Pending applications',
  '按金额匹配': 'Match by amount',
  '匹配农户': 'Match farmers',
  '咨询工单': 'Consultation tickets',
  '知识发布': 'Publish knowledge',
  '专家列表': 'Expert list',
  '我的咨询': 'My consultations',
  '认证专家': 'Verified experts',
  '待答复问答': 'Questions awaiting reply',
  '待处理预约': 'Pending bookings',
  '专家筛选': 'Expert filters',
  '我的服务工单': 'My service tickets',
  '问答处理': 'Question handling',
  '我的问答': 'My questions',
  '预约处理': 'Booking handling',
  '我的预约': 'My bookings',
  '待采购商品': 'Products to purchase',
  '采购件数': 'Item count',
  '预计金额': 'Estimated total',
  '进行中订单': 'Active orders',
  '预计采购金额': 'Estimated purchase total',
  '核心指标': 'Key metrics',
  '用户角色': 'User roles',
  '交易监管': 'Trade supervision',
  '融资监管': 'Finance supervision',
  '内容管理': 'Content management',
  '平台用户': 'Platform users',
  '货源审核': 'Product review',
  '资讯知识': 'News & knowledge',
  '当前角色': 'Current role',
  '买家账户': 'Buyer account',
  '累计采购金额': 'Total purchased',
  '默认收货信息': 'Default delivery details',
  '关联业务记录': 'Related business records',
  '经营规模': 'Business scale',
  '主营农产品': 'Main products',
  '采购类型': 'Purchase type',
  '认证专业': 'Verified speciality',
  '机构/产品': 'Institution / product',
  '管理范围': 'Management scope',
  '商品发布与管理': 'Product publishing & management',
  '继续选品': 'Continue shopping',
  '采购中心': 'Purchase centre',
  '咨询处理': 'Handle consultations',
  '融资审核': 'Finance review',
  '后台管理': 'Administration',
  '当前密码': 'Current password',
  '新密码': 'New password',
  '不修改可留空': 'Leave blank to keep unchanged',
  '从电脑上传头像': 'Upload avatar',
  '头像上传中': 'Uploading avatar',
  '真实姓名': 'Full name',
  '手机号': 'Phone',
  '身份号码': 'Identity number',
  '联系地址': 'Contact address',
  '申请人账号': 'Applicant account',
  '身份证号': 'Identity number',
  '申请金额': 'Requested amount',
  '还款方式': 'Repayment method',
  '经营说明': 'Business description',
  '意向金额': 'Target amount',
  '经营地址': 'Business address',
  '联系电话': 'Phone',
  '商品名称': 'Product name',
  '最小起订量': 'Minimum order',
  '发布账号': 'Publisher account',
  '产地地址': 'Origin address',
  '图片文件名': 'Image file name',
  '从电脑上传图片': 'Upload image',
  '图片上传中': 'Uploading image',
  '货源说明': 'Product description',
  '收货人': 'Recipient',
  '详细地址': 'Full address',
  '新增地址': 'Add address',
  '设为默认': 'Set as default',
  '采购账号': 'Buyer account',
  '收货地址': 'Delivery address',
  '取消原因': 'Cancellation reason',
  '专家关键词': 'Expert keyword',
  '记录作物': 'Crop filter',
  '填写专家答复': 'Enter expert reply',
  '填写预约处理意见': 'Enter booking response',
  '配图路径': 'Image path',
  '从电脑上传配图': 'Upload article image',
  '症状图片': 'Symptom images',
  '问题描述': 'Question details',
  '作物名称': 'Crop name',
  '预约时间': 'Booking time',
  '服务方式': 'Service method',
  '地块地址': 'Field address',
  '咨询信息': 'Consultation details',
  '提问人': 'Questioner',
  '已提交': 'Submitted',
  '银行受理': 'Accepted by bank',
  '审批通过': 'Approved',
  '已拒绝': 'Rejected',
  '已确认': 'Confirmed',
  '已发货': 'Shipped',
  '已收货': 'Received',
  '已取消': 'Cancelled',
  '已完成': 'Completed',
  '已发布': 'Published',
  '已下架': 'Unpublished',
  '已上架': 'Published',
  '待完善': 'Not provided',
  '待补充': 'Not provided',
  '时间待确认': 'Time to be confirmed',
  '联系方式待补充': 'Contact not provided',
  '刷新': 'Refresh',
  '搜索': 'Search',
  '选择': 'Select',
  '编辑': 'Edit',
  '上架': 'Publish',
  '下架': 'Unpublish',
  '删除': 'Delete',
  '移除': 'Remove',
  '取消': 'Cancel',
  '关闭': 'Close',
  '保存': 'Save',
  '提交': 'Submit',
  '答复': 'Reply',
  '驳回': 'Reject',
  '处理': 'Process',
  '数量': 'Quantity',
  '单价': 'Unit price',
  '小计': 'Subtotal',
  '单位': 'Unit',
  '规格': 'Specification',
  '库存': 'Stock',
  '品类': 'Category',
  '账号': 'Account',
  '密码': 'Password',
  '昵称': 'Display name',
  '角色': 'Role',
  '地址': 'Address',
  '姓名': 'Name',
  '标题': 'Title',
  '正文': 'Content',
  '农户': 'Farmer',
  '买家': 'Buyer',
  '专家': 'Expert',
  '银行': 'Bank',
  '管理员': 'Administrator',
}

const translatedAttributes = ['aria-label', 'placeholder', 'title', 'alt'] as const

function hasChinese(value: string) {
  return /[\u3400-\u9fff]/.test(value)
}

export function translateInterfaceText(value: string) {
  if (!hasChinese(value)) return value

  const leading = value.match(/^\s*/)?.[0] ?? ''
  const trailing = value.match(/\s*$/)?.[0] ?? ''
  const content = value.trim()
  const exact = exactMessages[content] ?? phraseMessages[content]
  if (exact) return `${leading}${exact}${trailing}`

  let translated = content
  let changed = false
  const numericTranslations: Array<[RegExp, string]> = [
    [/共\s*(\d+)\s*篇。?/g, '$1 articles'],
    [/共\s*(\d+)\s*条商品记录/g, '$1 product records'],
    [/(\d+)\s*条/g, '$1 items'],
    [/(\d+)\s*单/g, '$1 orders'],
    [/(\d+(?:\.\d+)?)\s*元/g, 'CNY $1'],
    [/^库存\s*(.+)$/g, 'Stock $1'],
    [/^起订\s*(.+)$/g, 'Minimum order $1'],
    [/^第\s*(\d+)\s*\/\s*(\d+)\s*页$/g, 'Page $1 / $2'],
    [/\/斤$/g, '/jin'],
    [/\s斤$/g, ' jin'],
  ]
  for (const [pattern, replacement] of numericTranslations) {
    const next = translated.replace(pattern, replacement)
    if (next !== translated) changed = true
    translated = next
  }
  return changed ? `${leading}${translated}${trailing}` : value
}

export function useGlobalLocale() {
  const locale = useLocaleStore()
  const textOriginals = new WeakMap<Text, string>()
  const attributeOriginals = new WeakMap<Element, Map<string, string>>()
  const pendingNodes = new Set<Node>()
  let observer: MutationObserver | null = null
  let frame = 0

  function processText(node: Text) {
    const parent = node.parentElement
    if (!parent || ['SCRIPT', 'STYLE', 'TEXTAREA'].includes(parent.tagName) || parent.isContentEditable) return

    const current = node.nodeValue ?? ''
    if (locale.isEnglish) {
      if (!hasChinese(current)) return
      textOriginals.set(node, current)
      node.nodeValue = translateInterfaceText(current)
      return
    }

    const original = textOriginals.get(node)
    if (original !== undefined) {
      node.nodeValue = original
      textOriginals.delete(node)
    }
  }

  function processElement(element: Element) {
    for (const attribute of translatedAttributes) {
      const current = element.getAttribute(attribute)
      if (locale.isEnglish) {
        if (!current || !hasChinese(current)) continue
        let originals = attributeOriginals.get(element)
        if (!originals) {
          originals = new Map()
          attributeOriginals.set(element, originals)
        }
        originals.set(attribute, current)
        element.setAttribute(attribute, translateInterfaceText(current))
        continue
      }

      const original = attributeOriginals.get(element)?.get(attribute)
      if (original !== undefined) {
        element.setAttribute(attribute, original)
        attributeOriginals.get(element)?.delete(attribute)
      }
    }
  }

  function processNode(node: Node) {
    if (node.nodeType === Node.TEXT_NODE) {
      processText(node as Text)
      return
    }
    if (!(node instanceof Element)) return

    processElement(node)
    const walker = document.createTreeWalker(node, NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT)
    let child = walker.nextNode()
    while (child) {
      if (child.nodeType === Node.TEXT_NODE) processText(child as Text)
      else processElement(child as Element)
      child = walker.nextNode()
    }
  }

  function observe() {
    const root = document.getElementById('app')
    if (!root || !observer) return
    observer.observe(root, {
      childList: true,
      subtree: true,
      characterData: true,
      attributes: true,
      attributeFilter: [...translatedAttributes],
    })
  }

  function flush() {
    frame = 0
    observer?.disconnect()
    pendingNodes.forEach(processNode)
    pendingNodes.clear()
    observe()
  }

  function schedule(node: Node) {
    pendingNodes.add(node)
    if (!frame) frame = window.requestAnimationFrame(flush)
  }

  async function applyAll() {
    await nextTick()
    const root = document.getElementById('app')
    if (!root) return
    observer?.disconnect()
    processNode(root)
    observe()
  }

  watch(() => locale.current, applyAll, { flush: 'post' })

  onMounted(() => {
    observer = new MutationObserver((mutations) => {
      for (const mutation of mutations) {
        if (mutation.type === 'childList') mutation.addedNodes.forEach(schedule)
        else schedule(mutation.target)
      }
    })
    void applyAll()
  })

  onBeforeUnmount(() => {
    observer?.disconnect()
    if (frame) window.cancelAnimationFrame(frame)
  })
}
