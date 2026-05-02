import { ref } from 'vue'
import { getAdConfigs, reportAdAction } from '@/api/fee/ad'
import type { AdConfigVO } from '@/types/biz/fee/ad'

const adConfigs = ref<AdConfigVO[]>([])
const loaded = ref(false)
const splashShownToday = ref(false)

// Composable 入口使用 function 声明，符合 Vue 社区命名约定
export function useAd() {
  const loadAdConfigs = async () => {
    if (loaded.value) return
    try {
      const res = await getAdConfigs()
      adConfigs.value = res ?? []
      loaded.value = true
    } catch {
      adConfigs.value = []
    }
  }

  const getConfig = (key: string): AdConfigVO | undefined =>
    adConfigs.value.find(c => c.configKey === key)

  const shouldShowAd = (configKey: string): boolean => {
    const config = getConfig(configKey)
    return !!config
  }

  const reportAction = async (configKey: string, action: number, duration?: number) => {
    try {
      await reportAdAction({ adConfigKey: configKey, action, duration })
    } catch {
      // 静默失败
    }
  }

  const markSplashShown = () => {
    splashShownToday.value = true
    uni.setStorageSync('ad_splash_shown_date', new Date().toDateString())
  }

  const canShowSplash = (): boolean => {
    const lastDate = uni.getStorageSync('ad_splash_shown_date')
    const today = new Date().toDateString()
    return lastDate !== today
  }

  return {
    adConfigs,
    loadAdConfigs,
    getConfig,
    shouldShowAd,
    reportAction,
    splashShownToday,
    markSplashShown,
    canShowSplash,
  }
}
