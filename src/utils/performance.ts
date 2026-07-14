import type { RouteLocationNormalizedLoaded } from 'vue-router'

type PerfMetric = {
  name: string
  value: number
  path?: string
  detail?: string
}

const ROUTE_MARK = 'agri-link:route-start'
const RESOURCE_TIMING_BUFFER_SIZE = 150
const MAINTENANCE_INTERVAL_MS = 30 * 60 * 1000
const RETAINED_METRICS = 20
const metrics: PerfMetric[] = []
let currentRoute = ''
let installed = false

function maintainPerformanceBuffers() {
  if (typeof performance === 'undefined') return

  performance.clearResourceTimings()
  performance.clearMeasures()

  if (metrics.length > RETAINED_METRICS) {
    metrics.splice(0, metrics.length - RETAINED_METRICS)
  }
}

function emitMetric(metric: PerfMetric) {
  metrics.push(metric)

  if (metrics.length > 80) {
    metrics.shift()
  }

  if (import.meta.env.DEV) {
    console.info(`[perf] ${metric.name}`, {
      value: `${metric.value.toFixed(1)}ms`,
      path: metric.path,
      detail: metric.detail,
    })
  }

  window.dispatchEvent(new CustomEvent('agri-link:perf', { detail: metric }))
}

export function markRouteStart(to: { fullPath: string }) {
  currentRoute = to.fullPath

  if (typeof performance === 'undefined') return
  performance.mark(ROUTE_MARK)
}

export function measureRouteReady(route: RouteLocationNormalizedLoaded) {
  if (typeof performance === 'undefined') return

  requestAnimationFrame(() => {
    const routeMarks = performance.getEntriesByName(ROUTE_MARK)
    const start = routeMarks[routeMarks.length - 1]
    if (!start || currentRoute !== route.fullPath) return

    emitMetric({
      name: 'route-ready',
      value: performance.now() - start.startTime,
      path: route.fullPath,
    })
    performance.clearMarks(ROUTE_MARK)
  })
}

export function measureApi(path: string, value: number, detail?: string) {
  if (typeof window === 'undefined') return
  emitMetric({ name: 'api-request', value, path, detail })
}

export function installPerformanceMonitoring() {
  if (installed || typeof window === 'undefined') return
  installed = true

  performance.setResourceTimingBufferSize(RESOURCE_TIMING_BUFFER_SIZE)
  window.setInterval(maintainPerformanceBuffers, MAINTENANCE_INTERVAL_MS)
  window.addEventListener('pagehide', maintainPerformanceBuffers)

  window.addEventListener('load', () => {
    requestAnimationFrame(() => {
      const navigation = performance.getEntriesByType('navigation')[0] as
        | PerformanceNavigationTiming
        | undefined

      if (navigation) {
        emitMetric({ name: 'page-load', value: navigation.loadEventEnd, path: location.pathname })
      }

      for (const entry of performance.getEntriesByType('paint')) {
        emitMetric({ name: entry.name, value: entry.startTime, path: location.pathname })
      }
    })
  })

  if ('PerformanceObserver' in window) {
    try {
      const observer = new PerformanceObserver((list) => {
        for (const entry of list.getEntries()) {
          emitMetric({
            name: 'long-task',
            value: entry.duration,
            path: location.pathname,
            detail: entry.name,
          })
        }
      })
      observer.observe({ entryTypes: ['longtask'] })
    } catch {
      // Different browsers expose different performance entry support.
    }
  }
}

export function getPerformanceMetrics() {
  return [...metrics]
}
