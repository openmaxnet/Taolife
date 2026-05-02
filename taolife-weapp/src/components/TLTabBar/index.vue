<template>
  <view class="tabbar">
    <view class="tabbar-container">
      <view
        v-for="(tab, index) in tabs"
        :key="index"
        class="tabbar-item"
        :class="{ 'tabbar-item-center': index === 2, 'tabbar-item-active': current === index }"
        @click="handleTabClick(index)"
      >
        <view v-if="index === 2" class="center-button">
          <view class="wave-layer wave-1" />
          <view class="wave-layer wave-2" />
          <view class="center-icon-img" :class="tab.icon" />
        </view>
        <view v-else class="tabbar-icon-wrap">
          <view class="tabbar-icon-img" :class="tab.icon" />
        </view>
        <text v-if="index === 2" class="tabbar-center-text">{{ tab.text }}</text>
        <text v-else class="tabbar-text" :class="{ 'text-active': current === index }">{{ tab.text }}</text>
      </view>
    </view>
    <view class="tabbar-safe-area"></view>
  </view>
</template>

<script setup lang="ts">
defineProps<{
  current: number
}>()

const emit = defineEmits(['change'])

const tabs = [
  { pagePath: '/pages/index/index', text: '探索', icon: 'i-solar:home-smile-angle-outline' },
  { pagePath: '/pages/wisdom/food/index', text: '食材', icon: 'i-solar:tea-cup-outline' },
  { pagePath: '/pages/ai/chat/index', text: '', icon: 'i-solar:incognito-outline' },
  { pagePath: '/pages/plan/index', text: '方案', icon: 'i-solar:notebook-minimalistic-outline' },
  { pagePath: '/pages/identity/user/index', text: '我的', icon: 'i-solar:user-outline' }
]

const handleTabClick = (index: number) => {
  emit('change', index)
  uni.reLaunch({ url: tabs[index].pagePath })
}
</script>

<style lang="scss">
.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: $tf-surface;
  z-index: 1000;
}

.tabbar-container {
  display: flex;
  justify-content: space-around;
  align-items: flex-start;
  padding: 16rpx 0;
  padding-bottom: 0;
  border-top: $tf-border-normal;
}

.tabbar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8rpx 0;
}

.tabbar-item-center {
  position: relative;
  margin-top: -30rpx;
}

.tabbar-icon-wrap {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tabbar-icon-img {
  width: 48rpx;
  height: 48rpx;
  color: $tf-gray-500;
}

.tabbar-item-active .tabbar-icon-img {
  color: $tf-primary-color;
}

.tabbar-text {
  font-size: $tf-text-xs;
  color: $tf-gray-500;
  margin-top: 4rpx;
}

.text-active {
  color: $tf-primary-color;
}

// 中间突出按钮
.center-button {
  width: 112rpx;
  height: 112rpx;
  background: $tf-primary-color;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(45, 90, 39, 0.3);
  border: 8rpx solid $tf-surface;
  overflow: hidden;
  position: relative;
  animation: breath 4s ease-in-out infinite;
}

.wave-layer {
  position: absolute;
  width: 200%;
  height: 200%;
  top: -50%;
  left: -50%;
  pointer-events: none;
}

.wave-1 {
  background: radial-gradient(ellipse at 30% 40%, rgba(79, 121, 66, 0.8) 0%, transparent 55%);
  animation: wave-flow-1 3s ease-in-out infinite;
}

.wave-2 {
  background: radial-gradient(ellipse at 70% 60%, rgba(5, 150, 105, 0.6) 0%, transparent 45%);
  animation: wave-flow-2 2.5s ease-in-out infinite;
}

@keyframes breath {
  0%, 100% {
    box-shadow: 0 8rpx 24rpx rgba(45, 90, 39, 0.3);
  }
  50% {
    box-shadow: 0 12rpx 32rpx rgba(45, 90, 39, 0.5);
  }
}

@keyframes wave-flow-1 {
  0%, 100% {
    transform: translate(-12%, -12%) rotate(0deg);
  }
  33% {
    transform: translate(10%, -5%) rotate(120deg);
  }
  66% {
    transform: translate(-5%, 10%) rotate(240deg);
  }
}

@keyframes wave-flow-2 {
  0%, 100% {
    transform: translate(8%, 5%) rotate(0deg);
  }
  50% {
    transform: translate(-12%, -8%) rotate(-180deg);
  }
}

.center-icon-img {
  width: 56rpx;
  height: 56rpx;
  color: $tf-surface;
}

.tabbar-center-text {
  position: absolute;
  bottom: -36rpx;
  font-size: $tf-text-xs;
  color: $tf-primary-color;
  font-weight: bold;
  white-space: nowrap;
}

.tabbar-safe-area {
  height: constant(safe-area-inset-bottom);
  height: env(safe-area-inset-bottom);
  background: $tf-surface;
}
</style>

