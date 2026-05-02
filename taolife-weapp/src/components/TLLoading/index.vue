<template>
  <view class="loading-page">
    <view class="icon-wrapper">
      <view class="loading-page-outline i-solar:bolt-broken" />
      <view class="loading-page-sharp i-solar:bolt-bold-duotone" />
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * 加载图标组件
 * 使用 ion:flash-outline（空心）和 ion:flash-sharp（实心）图标
 * 通过透明度动画实现空心到实心的过渡效果
 */
</script>

<style lang="scss">
.loading-page {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  position: relative;
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-page-outline,
.loading-page-sharp {
  position: absolute;
  width: 48rpx;
  height: 48rpx;
}

.loading-page-outline {
  color: $tf-primary-color;
  animation: flash-outline 1.5s ease-in-out infinite;
}

.loading-page-sharp {
  color: $tf-primary-color;
  animation: flash-sharp 1.5s ease-in-out infinite;
}

@keyframes flash-outline {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

@keyframes flash-sharp {
  0% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
}
</style>
