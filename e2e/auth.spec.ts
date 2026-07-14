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

test('registration keeps the existing API payload and login flow', async ({ page }) => {
  let registerPayload: Record<string, unknown> = {}

  await page.route('**/api/auth/register', async (route) => {
    registerPayload = route.request().postDataJSON() as Record<string, unknown>
    await route.fulfill({ json: { success: true, data: {} } })
  })
  await page.route('**/api/auth/login', async (route) => {
    await route.fulfill({
      json: {
        success: true,
        data: {
          user: { userName: 'new_farmer', role: 'FARMER', nickName: '新农户' },
        },
      },
    })
  })

  await page.goto('/auth')
  await page.getByRole('button', { name: '注册', exact: true }).click()
  await page.locator('input[autocomplete="username"]').fill('new_farmer')
  await page.locator('input[type="password"]').fill('Secure@123')
  await page.getByRole('button', { name: '创建账号' }).click()

  await expect(page).toHaveURL('/')
  expect(registerPayload).toMatchObject({
    userName: 'new_farmer',
    password: 'Secure@123',
    role: 'FARMER',
  })
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
