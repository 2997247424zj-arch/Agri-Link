const { chromium } = require('playwright-core');
const fs = require('fs');
const path = require('path');

const FE = 'http://127.0.0.1:5173';
const OUT = path.resolve(__dirname, '../../../tmp/docs/test-report/screenshots');
fs.mkdirSync(OUT, { recursive: true });

const chromeCandidates = [
  process.env.PLAYWRIGHT_CHROMIUM_PATH,
  'C:/Users/ASUS/AppData/Local/ms-playwright/chromium-1208/chrome-win64/chrome.exe',
  'C:/Program Files/Google/Chrome/Application/chrome.exe',
].filter(Boolean);

function findChrome() {
  for (const p of chromeCandidates) if (p && fs.existsSync(p)) return p;
  return null;
}

const roles = [
  { user: 'dev_farmer', role: 'FARMER', display: '十八洞农户', pages: [
    { name: 'home-farmer', url: '/' },
    { name: 'trade-farmer', url: '/trade' },
    { name: 'finance-farmer', url: '/finance' },
    { name: 'experts-farmer', url: '/experts' },
    { name: 'profile-farmer', url: '/profile' },
  ]},
  { user: 'dev_buyer', role: 'BUYER', display: '采购买家', pages: [
    { name: 'trade-buyer', url: '/trade' },
    { name: 'cart-buyer', url: '/cart' },
    { name: 'profile-buyer', url: '/profile' },
  ]},
  { user: 'dev_expert', role: 'EXPERT', display: '农技专家', pages: [
    { name: 'experts-expert', url: '/experts' },
    { name: 'profile-expert', url: '/profile' },
  ]},
  { user: 'dev_bank', role: 'BANK', display: '合作银行', pages: [
    { name: 'finance-bank', url: '/finance' },
    { name: 'profile-bank', url: '/profile' },
  ]},
  { user: 'dev_admin', role: 'SYSTEM_ADMIN', display: '系统管理员', pages: [
    { name: 'admin-overview', url: '/admin' },
  ]},
];

(async () => {
  const executablePath = findChrome();
  if (!executablePath) {
    console.error('chrome not found');
    process.exit(2);
  }
  console.log('chrome', executablePath);
  const browser = await chromium.launch({ executablePath, headless: true });
  const results = [];
  for (const r of roles) {
    const context = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const page = await context.newPage();
    await page.addInitScript(({ user, role, display }) => {
      localStorage.setItem('agri-link-user', user);
      localStorage.setItem('agri-link-role', role);
      localStorage.setItem('agri-link-display', display);
    }, r);
    for (const p of r.pages) {
      const item = { role: r.role, name: p.name, url: p.url, ok: false, error: null, file: null };
      try {
        const resp = await page.goto(FE + p.url, { waitUntil: 'domcontentloaded', timeout: 45000 });
        await page.waitForTimeout(1200);
        const finalUrl = page.url();
        const bounced = finalUrl.includes('/auth');
        const file = path.join(OUT, `${p.name}.png`);
        await page.screenshot({ path: file, fullPage: true });
        item.file = file;
        item.ok = !!resp && resp.ok() && !bounced;
        item.finalUrl = finalUrl;
        item.status = resp ? resp.status() : null;
        if (bounced) item.error = 'redirected to auth';
      } catch (e) {
        item.error = String(e);
      }
      results.push(item);
      console.log(`${item.ok ? 'PASS' : 'FAIL'} ${item.name} ${item.finalUrl || ''} ${item.error || ''}`);
    }
    await context.close();
  }
  await browser.close();
  const summary = {
    total: results.length,
    passed: results.filter(x => x.ok).length,
    failed: results.filter(x => !x.ok).length,
    results,
  };
  fs.writeFileSync(path.resolve(OUT, '../ui-smoke-results.json'), JSON.stringify(summary, null, 2));
  console.log(JSON.stringify({ total: summary.total, passed: summary.passed, failed: summary.failed }));
  process.exit(summary.failed ? 1 : 0);
})().catch(err => { console.error(err); process.exit(1); });
