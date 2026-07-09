import { onBeforeUnmount, onMounted } from 'vue'

export function useSpotlight() {
  let frame = 0

  function update(event: PointerEvent) {
    if (frame) return
    frame = window.requestAnimationFrame(() => {
      document.documentElement.style.setProperty('--spotlight-x', `${event.clientX}px`)
      document.documentElement.style.setProperty('--spotlight-y', `${event.clientY}px`)
      frame = 0
    })
  }

  onMounted(() => {
    window.addEventListener('pointermove', update, { passive: true })
  })

  onBeforeUnmount(() => {
    window.removeEventListener('pointermove', update)
    if (frame) window.cancelAnimationFrame(frame)
  })
}
