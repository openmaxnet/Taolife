<template>
  <view v-if="visible" class="ad-banner">
    <ad :unit-id="adUnitId" ad-intervals="30" @error="onError" @close="onClose" @load="onLoad" />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAd } from '@/composables/useAd'

const props = defineProps<{
  configKey: string
}>()

const { getConfig, shouldShowAd, reportAction } = useAd()
const visible = ref(false)
const adUnitId = ref('')

onMounted(() => {
  if (!shouldShowAd(props.configKey)) return
  const config = getConfig(props.configKey)
  if (!config || config.adType !== 1) return
  adUnitId.value = config.adUnitId
  visible.value = true
  reportAction(props.configKey, 1)
})

const onError = () => {
  visible.value = false
}

const onClose = () => {
  reportAction(props.configKey, 3)
}

const onLoad = () => {
  // 广告加载成功
}
</script>

<style lang="scss" scoped>
.ad-banner {
  width: 100%;
  min-height: 100rpx;
  margin-top: 20rpx;
}
</style>
