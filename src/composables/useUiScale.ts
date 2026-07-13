import { onBeforeUnmount, onMounted, ref } from 'vue'

/**
 * 把任意浏览器缩放 / 屏幕宽度都收敛到同一套「110% 缩放」设计视口，
 * 让用户默认的 90% 浏览器缩放呈现出 110% 的观感。
 *
 * 做法：对 .app-shell 施加 CSS `zoom`，使其内部有效布局宽度稳定在 DESIGN_WIDTH 附近。
 *   innerWidth 2133 (90%)  → zoom 1.22 → 有效 1745
 *   innerWidth 1920 (100%) → zoom 1.10 → 有效 1745
 *   innerWidth 1745 (110%) → zoom 1.00 → 有效 1745
 *
 * 外壳是 100dvh 的固定高度网格，放大后会溢出，因此按 zoom 反算像素高度写回，
 * 保证缩放后外壳正好铺满可视区、内部 main-content 继续独立滚动。
 */
const DESIGN_WIDTH = 1745
const MAX_SCALE = 1.35

export function useUiScale() {
  const shellRef = ref<HTMLElement | null>(null)
  let frame = 0

  function apply() {
    const shell = shellRef.value
    if (!shell) return

    const scale = Math.min(MAX_SCALE, Math.max(1, window.innerWidth / DESIGN_WIDTH))

    if (scale <= 1.001) {
      // 窄屏 / 已达目标宽度：交回给原生响应式，不做缩放。
      shell.style.removeProperty('zoom')
      shell.style.removeProperty('height')
      shell.style.removeProperty('min-height')
      document.documentElement.style.setProperty('--ui-scale', '1')
      return
    }

    const boxHeight = `${window.innerHeight / scale}px`
    shell.style.zoom = String(scale)
    shell.style.height = boxHeight
    shell.style.minHeight = boxHeight
    document.documentElement.style.setProperty('--ui-scale', scale.toFixed(4))
  }

  function schedule() {
    if (frame) return
    frame = window.requestAnimationFrame(() => {
      frame = 0
      apply()
    })
  }

  onMounted(() => {
    if (typeof window === 'undefined') return
    apply()
    window.addEventListener('resize', schedule, { passive: true })
  })

  onBeforeUnmount(() => {
    if (typeof window === 'undefined') return
    window.removeEventListener('resize', schedule)
    if (frame) window.cancelAnimationFrame(frame)
  })

  return { shellRef }
}
