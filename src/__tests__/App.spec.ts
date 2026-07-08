import { describe, it, expect } from 'vitest'

import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import { createMemoryHistory, createRouter } from 'vue-router'
import App from '../App.vue'

describe('App', () => {
  it('mounts renders properly', () => {
    const router = createRouter({
      history: createMemoryHistory(),
      routes: [{ path: '/', component: { template: '<div />' } }],
    })

    const wrapper = mount(App, {
      global: {
        plugins: [createPinia(), router],
        stubs: {
          RouterView: true,
        },
      },
    })
    expect(wrapper.text()).toContain('Agri-Link')
  })
})
