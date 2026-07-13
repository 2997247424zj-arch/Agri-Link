import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import { resolveAssetUrl } from '@/api/client'
import AppImage from '@/components/AppImage.vue'

describe('resolveAssetUrl', () => {
  it('points backend uploads to the API host without rewriting bundled or external images', () => {
    expect(resolveAssetUrl('/files/example.png')).toBe('http://localhost:9091/files/example.png')
    expect(resolveAssetUrl('/file/order/example.png')).toBe('/file/order/example.png')
    expect(resolveAssetUrl('data:image/png;base64,abc')).toBe('data:image/png;base64,abc')
  })
})

describe('AppImage', () => {
  it('retries with the supplied fallback before showing the placeholder', async () => {
    const wrapper = mount(AppImage, {
      props: { src: '/missing.jpg', fallbackSrc: '/file/order/fresh-eggs.webp', alt: '鸡蛋' },
    })

    await wrapper.get('img').trigger('error')

    expect(wrapper.get('img').attributes('src')).toBe('/file/order/fresh-eggs.webp')
  })
})
