import { onBeforeUnmount, onMounted, ref } from 'vue'

interface Particle {
  x: number
  y: number
  vx: number
  vy: number
  r: number
  alpha: number
}

export function useParticles() {
  const canvasRef = ref<HTMLCanvasElement | null>(null)
  const cursor = { x: -9999, y: -9999 }
  let ctx: CanvasRenderingContext2D | null = null
  let particles: Particle[] = []
  let frame = 0
  let width = 0
  let height = 0
  let dpr = 1
  let lastDraw = 0

  const reduceMotion =
    typeof window !== 'undefined' &&
    typeof window.matchMedia === 'function' &&
    window.matchMedia('(prefers-reduced-motion: reduce)').matches

  function resize() {
    const canvas = canvasRef.value
    if (!canvas) return
    dpr = Math.min(window.devicePixelRatio || 1, 1.5)
    width = window.innerWidth
    height = window.innerHeight
    canvas.width = Math.floor(width * dpr)
    canvas.height = Math.floor(height * dpr)
    canvas.style.width = `${width}px`
    canvas.style.height = `${height}px`
    try {
      ctx = canvas.getContext('2d')
    } catch {
      ctx = null
    }
    if (!ctx) return
    ctx?.setTransform(dpr, 0, 0, dpr, 0, 0)

    const isSmall = typeof window.matchMedia === 'function' && window.matchMedia('(max-width: 780px)').matches
    const count = isSmall ? 12 : 24
    particles = Array.from({ length: count }, () => ({
      x: Math.random() * width,
      y: Math.random() * height,
      vx: (Math.random() - 0.5) * 0.28,
      vy: (Math.random() - 0.5) * 0.22,
      r: 1.2 + Math.random() * 2.4,
      alpha: 0.14 + Math.random() * 0.24,
    }))
  }

  function moveParticle(particle: Particle) {
    const dx = particle.x - cursor.x
    const dy = particle.y - cursor.y
    const distance = Math.hypot(dx, dy)
    if (distance < 120) {
      const force = (120 - distance) / 120
      particle.vx += (dx / Math.max(distance, 1)) * force * 0.035
      particle.vy += (dy / Math.max(distance, 1)) * force * 0.035
    }

    particle.vx *= 0.992
    particle.vy *= 0.992
    particle.x += particle.vx
    particle.y += particle.vy

    if (particle.x < -20) particle.x = width + 20
    if (particle.x > width + 20) particle.x = -20
    if (particle.y < -20) particle.y = height + 20
    if (particle.y > height + 20) particle.y = -20
  }

  function draw(timestamp = 0) {
    if (!ctx) return
    if (timestamp - lastDraw < 33) {
      frame = window.requestAnimationFrame(draw)
      return
    }
    lastDraw = timestamp
    ctx.clearRect(0, 0, width, height)

    particles.forEach((particle, index) => {
      moveParticle(particle)

      for (let next = index + 1; next < particles.length; next += 2) {
        const other = particles[next]
        if (!other) continue
        const dx = particle.x - other.x
        const dy = particle.y - other.y
        const distanceSq = dx * dx + dy * dy
        if (distanceSq > 21025) continue
        ctx!.strokeStyle = `rgba(34, 197, 94, ${0.08 * (1 - Math.sqrt(distanceSq) / 145)})`
        ctx!.lineWidth = 1
        ctx!.beginPath()
        ctx!.moveTo(particle.x, particle.y)
        ctx!.lineTo(other.x, other.y)
        ctx!.stroke()
      }

      // 之前每个粒子每帧都 createRadialGradient + fill，开销大且是卡顿来源之一。
      // 改为一次性设定 fillStyle + globalAlpha 的实心圆点，观感接近但主线程绘制成本大幅下降。
      ctx!.globalAlpha = particle.alpha
      ctx!.fillStyle = 'rgba(34, 197, 94, 1)'
      ctx!.beginPath()
      ctx!.arc(particle.x, particle.y, particle.r * 2.2, 0, Math.PI * 2)
      ctx!.fill()
      ctx!.globalAlpha = 1
    })

    frame = window.requestAnimationFrame(draw)
  }

  function start() {
    if (!frame && ctx && document.visibilityState !== 'hidden') {
      frame = window.requestAnimationFrame(draw)
    }
  }

  function stop() {
    if (!frame) return
    window.cancelAnimationFrame(frame)
    frame = 0
  }

  function handleVisibility() {
    if (document.visibilityState === 'hidden') stop()
    else start()
  }

  function updateCursor(event: PointerEvent) {
    cursor.x = event.clientX
    cursor.y = event.clientY
  }

  onMounted(() => {
    if (import.meta.env.MODE === 'test' || reduceMotion) return
    resize()
    window.addEventListener('resize', resize, { passive: true })
    window.addEventListener('pointermove', updateCursor, { passive: true })
    document.addEventListener('visibilitychange', handleVisibility)
    start()
  })

  onBeforeUnmount(() => {
    window.removeEventListener('resize', resize)
    window.removeEventListener('pointermove', updateCursor)
    document.removeEventListener('visibilitychange', handleVisibility)
    stop()
  })

  return { canvasRef }
}
