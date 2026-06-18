import { defineStore } from 'pinia'
import { ref } from 'vue'
import i18n from '@/i18n'

export const useLocaleStore = defineStore('locale', () => {
  const currentLocale = ref(localStorage.getItem('weblog-locale') || 'zh')

  function setLocale(lang) {
    currentLocale.value = lang
    i18n.global.locale.value = lang
    localStorage.setItem('weblog-locale', lang)
  }

  function toggleLocale() {
    setLocale(currentLocale.value === 'zh' ? 'en' : 'zh')
  }

  return { currentLocale, setLocale, toggleLocale }
}, {
  persist: true
})
