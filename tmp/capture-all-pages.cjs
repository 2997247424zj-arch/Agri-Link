/**
 * Capture all Agri-Link frontend pages into 前端页面抓取/
 * Seeds localStorage per role so role-gated routes render.
 */
const { chromium } = require('playwright-core')
const fs = require('fs')
const path = require('path')

const BASE = process.env.AGRI_BASE || 'http://127.0.0.1:5173'
const CHROME =
  process.env.CHROME_PATH ||
  'C:/Users/ASUS/AppData/Local/ms-playwright/chromium-1208/chrome-win64/chrome.exe'
const OUT = path.resolve(__dirname, '../../../前端页面抓取')
const VIEWPORT = { width: 1440, height: 900 }

// 使用 docs/dev-login-accounts.md 中的真实测试账号，保证后端有对应数据
const ROLES = {
  guest: null,
  farmer: { user: 'dev_farmer', role: 'FARMER', display: '测试农户' },
  buyer: { user: 'dev_buyer', role: 'BUYER', display: '测试买家' },
  expert: { user: 'dev_expert', role: 'EXPERT', display: '测试专家' },
  bank: { user: 'dev_bank', role: 'BANK', display: '测试银行' },
  admin: { user: 'dev_admin', role: 'SYSTEM_ADMIN', display: '系统管理员' },
}

/** @type {Array<{name:string, role:keyof typeof ROLES, path:string, waitMs?:number, after?:(page:any)=>Promise<void>}>} */
const PAGES = [
  // public
  { name: '01-home-guest', role: 'guest', path: '/' },
  { name: '02-auth-login', role: 'guest', path: '/auth' },
  {
    name: '03-auth-register',
    role: 'guest',
    path: '/auth',
    after: async (page) => {
      const reg = page.getByRole('button', { name: '注册', exact: true })
      if (await reg.count()) await reg.first().click()
      await page.waitForTimeout(400)
    },
  },

  // home per role
  { name: '04-home-farmer', role: 'farmer', path: '/' },
  { name: '05-home-buyer', role: 'buyer', path: '/' },
  { name: '06-home-expert', role: 'expert', path: '/' },
  { name: '07-home-bank', role: 'bank', path: '/' },

  // trade
  { name: '08-trade-browse-farmer', role: 'farmer', path: '/trade' },
  { name: '09-trade-publish-farmer', role: 'farmer', path: '/trade?tab=publish' },
  { name: '10-trade-browse-buyer', role: 'buyer', path: '/trade' },

  // cart
  { name: '11-cart-buyer', role: 'buyer', path: '/cart' },

  // finance
  { name: '12-finance-intro-farmer', role: 'farmer', path: '/finance' },
  { name: '13-finance-apply-farmer', role: 'farmer', path: '/finance?tab=apply' },
  { name: '14-finance-result-farmer', role: 'farmer', path: '/finance?tab=result' },
  { name: '15-finance-products-bank', role: 'bank', path: '/finance?tab=products' },
  { name: '16-finance-result-bank', role: 'bank', path: '/finance?tab=result' },

  // experts
  { name: '17-experts-list-farmer', role: 'farmer', path: '/experts' },
  { name: '18-experts-records-farmer', role: 'farmer', path: '/experts?tab=records' },
  { name: '19-experts-records-expert', role: 'expert', path: '/experts' },
  { name: '20-experts-knowledge-expert', role: 'expert', path: '/experts?tab=knowledge' },

  // profile
  { name: '21-profile-farmer', role: 'farmer', path: '/profile' },
  { name: '22-profile-buyer', role: 'buyer', path: '/profile' },
  { name: '23-profile-expert', role: 'expert', path: '/profile' },
  { name: '24-profile-bank', role: 'bank', path: '/profile' },
  { name: '25-profile-admin', role: 'admin', path: '/profile' },

  // admin tabs
  { name: '26-admin-overview', role: 'admin', path: '/admin' },
  {
    name: '27-admin-users',
    role: 'admin',
    path: '/admin',
    after: async (page) => clickAdminTab(page, '用户管理'),
  },
  {
    name: '28-admin-trade',
    role: 'admin',
    path: '/admin',
    after: async (page) => clickAdminTab(page, '交易监管'),
  },
  {
    name: '29-admin-finance',
    role: 'admin',
    path: '/admin',
    after: async (page) => clickAdminTab(page, '融资监管'),
  },
  {
    name: '30-admin-knowledge',
    role: 'admin',
    path: '/admin',
    after: async (page) => clickAdminTab(page, '内容管理'),
  },
]

async function clickAdminTab(page, label) {
  const tab = page.getByRole('button', { name: label }).or(page.locator('button, a, .module-tabs button, .admin-nav button, [class*="tab"]').filter({ hasText: label }))
  if (await tab.count()) {
    await tab.first().click()
    await page.waitForTimeout(500)
  } else {
    // fallback: text click
    const el = page.locator(`text=${label}`).first()
    if (await el.count()) {
      await el.click()
      await page.waitForTimeout(500)
    }
  }
}

async function seedRole(page, roleKey) {
  const info = ROLES[roleKey]
  await page.addInitScript((payload) => {
    try {
      localStorage.clear()
      if (payload) {
        localStorage.setItem('agri-link-user', payload.user)
        localStorage.setItem('agri-link-role', payload.role)
        localStorage.setItem('agri-link-display', payload.display)
      }
    } catch (_) {}
  }, info)
}

async function settle(page) {
  await page.waitForLoadState('domcontentloaded')
  // 等 API 返回，尽量消掉“后端暂不可用”提示
  for (let i = 0; i < 15; i++) {
    await page.waitForTimeout(400)
    const hasBackendError = await page.evaluate(() => {
      const text = document.body?.innerText || ''
      return text.includes('后端暂不可用') || text.includes('无法连接后端服务') || text.includes('Failed to fetch')
    })
    if (!hasBackendError) break
  }
  await page.waitForLoadState('networkidle', { timeout: 8000 }).catch(() => {})
  await page.evaluate(() => {
    const kill = (sel) => document.querySelectorAll(sel).forEach((n) => (n.style.display = 'none'))
    kill('#vue-devtools-container')
    kill('.vue-devtools__panel')
    kill('[data-v-inspector]')
    const main = document.querySelector('.main-content')
    if (main) main.scrollTop = 0
    window.scrollTo(0, 0)
  })
  await page.waitForTimeout(300)
}

async function captureFull(page, filePath) {
  // Prefer full page; if app uses internal scroll container, expand it temporarily
  await page.evaluate(() => {
    const main = document.querySelector('.main-content')
    if (main) {
      main.dataset._prevOverflow = main.style.overflow
      main.dataset._prevHeight = main.style.height
      main.dataset._prevMaxHeight = main.style.maxHeight
      main.style.overflow = 'visible'
      main.style.height = 'auto'
      main.style.maxHeight = 'none'
    }
    const app = document.querySelector('#app')
    if (app) {
      app.dataset._prevOverflow = app.style.overflow
      app.style.overflow = 'visible'
    }
    document.documentElement.style.overflow = 'visible'
    document.body.style.overflow = 'visible'
  })
  await page.waitForTimeout(150)
  await page.screenshot({ path: filePath, fullPage: true })
  await page.evaluate(() => {
    const main = document.querySelector('.main-content')
    if (main) {
      main.style.overflow = main.dataset._prevOverflow || ''
      main.style.height = main.dataset._prevHeight || ''
      main.style.maxHeight = main.dataset._prevMaxHeight || ''
    }
  })
}

async function main() {
  fs.mkdirSync(OUT, { recursive: true })
  const browser = await chromium.launch({
    executablePath: CHROME,
    headless: true,
  })

  const results = []
  for (const item of PAGES) {
    const context = await browser.newContext({
      viewport: VIEWPORT,
      deviceScaleFactor: 1,
      locale: 'zh-CN',
    })
    const page = await context.newPage()
    await seedRole(page, item.role)
    const url = BASE + item.path
    console.log(`→ ${item.name}  ${url}`)
    try {
      await page.goto(url, { waitUntil: 'networkidle', timeout: 30000 }).catch(async () => {
        await page.goto(url, { waitUntil: 'domcontentloaded', timeout: 30000 })
      })
      await settle(page)
      if (item.after) await item.after(page)
      await settle(page)
      const file = path.join(OUT, `${item.name}.png`)
      await captureFull(page, file)
      results.push({ name: item.name, ok: true, file })
      console.log(`  saved ${file}`)
    } catch (err) {
      results.push({ name: item.name, ok: false, error: String(err) })
      console.error(`  FAIL ${item.name}:`, err.message || err)
    }
    await context.close()
  }

  await browser.close()

  const summary = {
    base: BASE,
    out: OUT,
    at: new Date().toISOString(),
    total: results.length,
    ok: results.filter((r) => r.ok).length,
    fail: results.filter((r) => !r.ok).length,
    results,
  }
  fs.writeFileSync(path.join(OUT, 'capture-summary.json'), JSON.stringify(summary, null, 2), 'utf8')
  console.log('\nDone:', summary.ok, '/', summary.total, '→', OUT)
  if (summary.fail) process.exitCode = 1
}

main().catch((e) => {
  console.error(e)
  process.exit(1)
})
