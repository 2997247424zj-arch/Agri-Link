import { onBeforeUnmount, onMounted } from 'vue'

/**
 * 环境聚光背景。
 *
 * 曾按 pointermove 实时更新 --spotlight-x/y，但该聚光层是 fixed 全屏 + mix-blend-mode:multiply，
 * 每次移动都会触发整屏重新合成，是页面卡顿的主要来源。
 * 现改为仅保留 CSS 中的静态默认位置（50vw/22vh），不再随指针重绘 —— 观感几乎一致，滚动/拖拽明显更顺。
 * 保留空的挂载/卸载钩子以维持调用点稳定，未来若需低频更新可在此接入 IntersectionObserver 等惰性方案。
 */
export function useSpotlight() {
  onMounted(() => {})
  onBeforeUnmount(() => {})
}
