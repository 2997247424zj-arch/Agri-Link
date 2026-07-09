import { expect, test } from '@playwright/test'

const routes = ['/', '/trade', '/finance', '/experts', '/cart', '/profile', '/admin']

test.beforeEach(async ({ page }) => {
  await page.addInitScript(() => {
    localStorage.setItem('agri-link-user', 'perf-admin')
    localStorage.setItem('agri-link-role', 'SYSTEM_ADMIN')
    ;(window as unknown as { __agriPerf: Array<{ name: string; value: number; path?: string }> }).__agriPerf = []
    window.addEventListener('agri-link:perf', (event) => {
      const metric = (event as CustomEvent).detail
      ;(window as unknown as { __agriPerf: unknown[] }).__agriPerf.push(metric)
    })
  })
})

test('core routes render and report route performance', async ({ page }) => {
  const errors: string[] = []
  page.on('pageerror', (error) => errors.push(error.message))
  page.on('console', (message) => {
    if (message.type() === 'error') errors.push(message.text())
  })

  for (const route of routes) {
    await page.goto(route)
    await expect(page.locator('.brand strong')).toHaveText('Agri-Link')
    await expect(page.locator('main')).not.toBeEmpty()

    const routeMetric = await page.waitForFunction(
      (path) => {
        const metrics = (window as unknown as { __agriPerf?: Array<{ name: string; value: number; path?: string }> })
          .__agriPerf ?? []
        return metrics.find((metric) => metric.name === 'route-ready' && metric.path === path)
      },
      route,
      { timeout: 3000 },
    )

    const metric = await routeMetric.jsonValue()
    expect(metric.value).toBeLessThan(3000)
  }

  expect(errors).toEqual([])
})
