import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

export type AppLocale = 'zh' | 'en'

function readLocale(): AppLocale {
  if (typeof localStorage === 'undefined') return 'zh'
  return localStorage.getItem('agri-link-locale') === 'en' ? 'en' : 'zh'
}

export const useLocaleStore = defineStore('locale', () => {
  const current = ref<AppLocale>(readLocale())
  const isEnglish = computed(() => current.value === 'en')
  if (typeof document !== 'undefined') document.documentElement.lang = current.value === 'zh' ? 'zh-CN' : 'en'

  function toggle() {
    current.value = current.value === 'zh' ? 'en' : 'zh'
    if (typeof localStorage !== 'undefined') localStorage.setItem('agri-link-locale', current.value)
    if (typeof document !== 'undefined') document.documentElement.lang = current.value === 'zh' ? 'zh-CN' : 'en'
  }

  function t(chinese: string, english: string) {
    return isEnglish.value ? english : chinese
  }

  return { current, isEnglish, toggle, t }
})
