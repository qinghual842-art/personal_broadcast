import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getSiteConfig } from '@/api/site'

export const useSiteStore = defineStore('site', () => {
  const config = ref({})

  async function fetchConfig() {
    try {
      const res = await getSiteConfig()
      config.value = res.data
    } catch (e) { /* ignore */ }
  }

  return { config, fetchConfig }
})
