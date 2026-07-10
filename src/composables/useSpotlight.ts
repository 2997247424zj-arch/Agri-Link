import { onBeforeUnmount, onMounted } from 'vue'

export function useSpotlight() {
  let frame = 0
  const shouldTrack =
    typeof window !== 'undefined' &&
    typeof window.matchMedia === 'function' &&
    window.matchMedia('(pointer: fine) and (min-width: 781px) and (prefers-reduced-motion: no-preference)').matches

  function update(event: PointerEvent) {
    if (frame) return
    frame = window.requestAnimationFrame(() => {
      document.documentElement.style.setProperty('--spotlight-x', `${event.clientX}px`)
      document.documentElement.style.setProperty('--spotlight-y', `${event.clientY}px`)
      frame = 0
    })
  }

  onMounted(() => {
    if (!shouldTrack) return
    window.addEventListener('pointermove', update, { passive: true })
  })

  onBeforeUnmount(() => {
    if (!shouldTrack) return
    window.removeEventListener('pointermove', update)
    if (frame) window.cancelAnimationFrame(frame)
  })
}
