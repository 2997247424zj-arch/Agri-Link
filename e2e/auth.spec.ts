import { expect, test } from '@playwright/test'

test.beforeEach(async ({ page }) => {
  await page.addInitScript(() => localStorage.clear())
})

test('desktop login and registration fit the first screen and auth state is not kept alive', async ({ page }) => {
  await page.setViewportSize({ width: 1366, height: 768 })
  await page.goto('/auth')

  await expect(page.getByText('示例账号快速体验')).toHaveCount(0)
  await expect(page.getByRole('heading', { name: '欢迎回来' })).toBeVisible()
  await page.locator('input[type="password"]').fill('Temporary@123')

  await page.getByRole('button', { name: '注册', exact: true }).click()
  await expect(page.getByRole('heading', { name: '创建您的账号' })).toBeVisible()
  await expect(page.getByRole('button', { name: '普通账号' })).toBeVisible()
  await expect(page.getByRole('button', { name: '163 邮箱' })).toBeVisible()
  await expect(page.locator('input[type="password"]')).toHaveValue('Temporary@123')

  const hasVerticalOverflow = await page.locator('.auth-content').evaluate(
    (element) => element.scrollHeight > element.clientHeight + 1,
  )
  expect(hasVerticalOverflow).toBe(false)

  await page.locator('.brand').click()
  await expect(page).toHaveURL('/')
  await page.locator('.topbar__actions a[href="/auth"]').click()
  await expect(page).toHaveURL('/auth')
  await expect(page.locator('input[type="password"]')).toHaveValue('')
})

test('ordinary account registration does not request an email code', async ({ page }) => {
  let registerPayload: Record<string, unknown> = {}
  let emailCodeRequestCount = 0

  await page.route('**/api/auth/email-code', async (route) => {
    emailCodeRequestCount += 1
    await route.fulfill({ json: { success: true, data: null } })
  })
  await page.route('**/api/auth/register', async (route) => {
    registerPayload = route.request().postDataJSON() as Record<string, unknown>
    await route.fulfill({
      json: {
        success: true,
        data: {
          userName: 'new_buyer',
          role: 'BUYER',
          nickName: '新买家',
          token: 'e2e.jwt.buyer',
          headerName: 'Authorization',
        },
      },
    })
  })

  await page.goto('/auth')
  await page.getByRole('button', { name: '注册', exact: true }).click()
  await page.locator('input[autocomplete="username"]').fill('new_buyer')
  await page.locator('input[type="password"]').fill('Secure@123')
  await page.locator('select').selectOption('BUYER')
  await page.getByRole('button', { name: '创建账号' }).click()

  await expect(page).toHaveURL('/')
  expect(registerPayload).toMatchObject({ userName: 'new_buyer', password: 'Secure@123', role: 'BUYER' })
  expect(registerPayload.verificationCode).toBeUndefined()
  expect(emailCodeRequestCount).toBe(0)
})

test('email registration verifies the address and signs in from the register response', async ({ page }) => {
  await page.setViewportSize({ width: 1366, height: 768 })
  let registerPayload: Record<string, unknown> = {}
  let emailCodePayload: Record<string, unknown> = {}
  let loginRequestCount = 0

  await page.route('**/api/auth/email-code', async (route) => {
    emailCodePayload = route.request().postDataJSON() as Record<string, unknown>
    await route.fulfill({ json: { success: true, data: null } })
  })

  await page.route('**/api/auth/register', async (route) => {
    registerPayload = route.request().postDataJSON() as Record<string, unknown>
    await route.fulfill({
      json: {
        success: true,
        data: {
          userName: 'new_farmer@163.com',
          role: 'FARMER',
          nickName: '新农户',
          token: 'e2e.jwt.farmer',
          headerName: 'Authorization',
        },
      },
    })
  })
  await page.route('**/api/auth/login', async (route) => {
    loginRequestCount += 1
    await route.fulfill({
      json: {
        success: true,
        data: {
          userName: 'new_farmer',
          role: 'FARMER',
          nickName: '新农户',
          token: 'e2e.jwt.farmer-login',
          headerName: 'Authorization',
        },
      },
    })
  })

  await page.goto('/auth')
  await page.getByRole('button', { name: '注册', exact: true }).click()
  await page.getByRole('button', { name: '163 邮箱' }).click()
  const hasVerticalOverflow = await page.locator('.auth-content').evaluate(
    (element) => element.scrollHeight > element.clientHeight + 1,
  )
  expect(hasVerticalOverflow).toBe(false)
  await page.locator('input[autocomplete="email"]').fill('new_farmer@163.com')
  await page.getByRole('button', { name: '发送验证码' }).click()
  await page.locator('input[autocomplete="one-time-code"]').fill('123456')
  await page.locator('input[type="password"]').fill('Secure@123')
  await page.getByRole('button', { name: '创建账号' }).click()

  await expect(page).toHaveURL('/')
  expect(emailCodePayload).toEqual({ email: 'new_farmer@163.com' })
  expect(registerPayload).toMatchObject({
    userName: 'new_farmer@163.com',
    password: 'Secure@123',
    verificationCode: '123456',
    role: 'FARMER',
  })
  expect(loginRequestCount).toBe(0)
})

test('mobile auth has no horizontal overflow and respects reduced motion', async ({ page }) => {
  await page.setViewportSize({ width: 375, height: 812 })
  await page.emulateMedia({ reducedMotion: 'reduce' })
  await page.goto('/auth')
  await page.getByRole('button', { name: '注册', exact: true }).click()

  const layout = await page.locator('.auth-content').evaluate((element) => ({
    clientWidth: element.clientWidth,
    scrollWidth: element.scrollWidth,
  }))
  expect(layout.scrollWidth).toBeLessThanOrEqual(layout.clientWidth + 1)
  await expect(page.locator('.auth-extra-grid input')).toHaveCount(4)

  const transitionDuration = await page.locator('.auth-card__content').evaluate(
    (element) => getComputedStyle(element).transitionDuration,
  )
  expect(transitionDuration).toBe('0s')
})
