<template>
  <view />
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { useAd } from '@/composables/useAd'

const props = defineProps<{
  configKey: string
}>()

const emit = defineEmits<{
  (e: 'reward'): void
  (e: 'error', msg?: string): void
  (e: 'close', isEnded: boolean): void
}>()

const { getConfig, shouldShowAd, reportAction } = useAd()

let rewardedVideoAd: any = null
let errorHandler: ((err: any) => void) | null = null
let closeHandler: ((res: any) => void) | null = null

onMounted(() => {
  if (!shouldShowAd(props.configKey)) {
    emit('error', '广告配置不存在')
    return
  }

  const config = getConfig(props.configKey)
  if (!config || config.adType !== 3) {
    emit('error', '广告类型不匹配')
    return
  }

  try {
    rewardedVideoAd = uni.createRewardedVideoAd({ adUnitId: config.adUnitId })
  } catch {
    emit('error', '创建广告实例失败')
    return
  }

  errorHandler = (err: any) => {
    console.warn('激励视频广告错误:', err)
    emit('error', err?.errMsg ?? '广告加载失败')
  }

  closeHandler = (res: any) => {
    const isEnded = res?.isEnded ?? res?.detail?.isEnded ?? false
    reportAction(props.configKey, 3)
    if (isEnded) {
      reportAction(props.configKey, 4)
      emit('reward')
    } else {
      emit('close', false)
    }
  }

  rewardedVideoAd.onError(errorHandler)
  rewardedVideoAd.onClose(closeHandler)
})

onUnmounted(() => {
  if (rewardedVideoAd) {
    if (errorHandler) rewardedVideoAd.offError(errorHandler)
    if (closeHandler) rewardedVideoAd.offClose(closeHandler)
    rewardedVideoAd = null
    errorHandler = null
    closeHandler = null
  }
})

const show = async () => {
  if (!rewardedVideoAd) {
    emit('error', '广告实例不存在')
    return
  }
  try {
    await rewardedVideoAd.show()
    reportAction(props.configKey, 1)
  } catch {
    try {
      await rewardedVideoAd.load()
      await rewardedVideoAd.show()
      reportAction(props.configKey, 1)
    } catch {
      emit('error', '广告展示失败')
    }
  }
}

defineExpose({ show })
</script>
