<template>
  <view v-if="visible" class="ad-splash" @tap="onTapClose">
    <view class="ad-splash-content">
      <ad :unit-id="adUnitId" @error="onError" @load="onLoad" />
    </view>
    <view class="ad-splash-close" @tap.stop="close">
      <text class="close-text">{{ countdown > 0 ? `${countdown}s 跳过` : '跳过' }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useAd } from '@/composables/useAd'

const props = defineProps<{
  configKey: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const { getConfig, shouldShowAd, reportAction, markSplashShown, canShowSplash } = useAd()
const visible = ref(false)
const adUnitId = ref('')
const countdown = ref(5)
let timer: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  if (!canShowSplash() || !shouldShowAd(props.configKey)) return
  const config = getConfig(props.configKey)
  if (!config) return

  adUnitId.value = config.adUnitId
  visible.value = true
  reportAction(props.configKey, 1)

  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      close()
    }
  }, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})

const close = () => {
  visible.value = false
  markSplashShown()
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  emit('close')
}

const onError = () => {
  close()
}

const onLoad = () => {
  // 广告加载成功
}

const onTapClose = () => {
  // 点击背景不关闭
}
</script>

<style lang="scss" scoped>
.ad-splash {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  background-color: rgba(0, 0, 0, 0.85);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.ad-splash-content {
  width: 600rpx;
  min-height: 400rpx;
  border-radius: 16rpx;
  overflow: hidden;
  background: #fff;
}

.ad-splash-close {
  margin-top: 40rpx;
  padding: 12rpx 32rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 32rpx;
}

.close-text {
  color: #fff;
  font-size: 28rpx;
}
</style>
