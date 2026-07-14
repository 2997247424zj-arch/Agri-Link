import { afterEach, describe, expect, it, vi } from 'vitest'

afterEach(() => {
  vi.useRealTimers()
  vi.restoreAllMocks()
  vi.unstubAllGlobals()
  vi.resetModules()
  localStorage.clear()
})

describe('performance maintenance', () => {
  it('trims temporary metrics every 30 minutes without touching persisted business data', async () => {
    vi.useFakeTimers()
    vi.spyOn(console, 'info').mockImplementation(() => undefined)

    const fakePerformance = {
      clearMeasures: vi.fn(),
      clearResourceTimings: vi.fn(),
      getEntriesByType: vi.fn(() => []),
      now: vi.fn(() => 0),
      setResourceTimingBufferSize: vi.fn(),
    }
    vi.stubGlobal('performance', fakePerformance)

    localStorage.setItem('agri-link-user', 'farmer01')
    localStorage.setItem('agri-link-local-cart', '[{"shoppingId":1}]')

    const { getPerformanceMetrics, installPerformanceMonitoring, measureApi } = await import('@/utils/performance')
    installPerformanceMonitoring()

    for (let index = 0; index < 30; index += 1) {
      measureApi(`/api/test/${index}`, index)
    }

    expect(fakePerformance.setResourceTimingBufferSize).toHaveBeenCalledWith(150)
    vi.advanceTimersByTime(30 * 60 * 1000)

    expect(fakePerformance.clearResourceTimings).toHaveBeenCalledOnce()
    expect(fakePerformance.clearMeasures).toHaveBeenCalledOnce()
    expect(getPerformanceMetrics()).toHaveLength(20)
    expect(localStorage.getItem('agri-link-user')).toBe('farmer01')
    expect(localStorage.getItem('agri-link-local-cart')).toBe('[{"shoppingId":1}]')
  })
})
