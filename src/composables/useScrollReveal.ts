import { nextTick, onBeforeUnmount, onMounted } from 'vue'

const revealSelector = [
  '.portal-section',
  '.panel',
  '.card',
  '.auth-card',
  '.metric',
  '.finance-card',
  '.service-card',
  '.expert-card',
  '.product-tile',
  '.dashboard-card',
  '.workflow-card',
  '.admin-stat-card',
].join(',')

export function useScrollReveal() {
  let observer: IntersectionObserver | null = null
  let mutationObserver: MutationObserver | null = null
  let refreshFrame = 0

  function observe() {
    if (!observer) return
    const root = document.querySelector<HTMLElement>('.main-content') ?? document.body
    root.querySelectorAll<HTMLElement>(revealSelector).forEach((element) => {
      if (element.dataset.revealObserved) return
      element.dataset.revealObserved = 'true'
      element.classList.add('reveal-ready')
      observer?.observe(element)
    })
  }

  function scheduleObserve() {
    if (refreshFrame) return
    refreshFrame = window.requestAnimationFrame(() => {
      refreshFrame = 0
      observe()
    })
  }

  async function refreshScrollReveal() {
    await nextTick()
    scheduleObserve()
  }

  onMounted(() => {
    if (typeof IntersectionObserver !== 'function') {
      document.querySelectorAll<HTMLElement>(revealSelector).forEach((element) => {
        element.classList.add('is-revealed')
      })
      return
    }

    observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) return
          entry.target.classList.add('is-revealed')
          observer?.unobserve(entry.target)
        })
      },
      { rootMargin: '0px 0px -8% 0px', threshold: 0.12 },
    )

    mutationObserver = new MutationObserver(scheduleObserve)
    mutationObserver.observe(document.querySelector('.main-content') ?? document.body, { childList: true, subtree: true })
    void refreshScrollReveal()
  })

  onBeforeUnmount(() => {
    observer?.disconnect()
    mutationObserver?.disconnect()
    if (refreshFrame) window.cancelAnimationFrame(refreshFrame)
  })

  return { refreshScrollReveal }
}
