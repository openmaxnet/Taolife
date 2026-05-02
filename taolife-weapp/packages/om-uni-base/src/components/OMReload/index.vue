<template>
  <view class="reload-page">
    <!-- 图标区域 -->
    <view class="icon-wrapper">
      <view class="airplane-icon i-solar:cloud-download-outline" />
    </view>

    <!-- 错误信息 -->
    <text class="error-title">{{ title }}</text>
    <text class="error-desc">{{ description }}</text>

    <!-- 重新加载按钮 -->
    <button class="reload-btn" @click="handleReload">
      <text>重新加载</text>
    </button>
  </view>
</template>

<script setup lang="ts">
defineProps<{
  /** 错误标题 */
  title?: string;
  /** 错误描述 */
  description?: string;
}>();

const emit = defineEmits<{
  (e: 'reload'): void;
}>();

/**
 * 处理重新加载
 */
const handleReload = () => {
  emit('reload');
};
</script>

<style lang="scss">
.reload-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 300rpx 64rpx;
  min-height: 600rpx;
}

.icon-wrapper {
  width: 200rpx;
  height: 200rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.airplane-icon {
  width: 120rpx;
  height: 120rpx;
  color: $om-gray-500;
}

.error-title {
  display: block;
  font-size: $om-text-4xl;
  color: $om-gray-900;
  font-weight: bold;
  margin-top: 48rpx;
}

.error-desc {
  display: block;
  font-size: 26rpx;
  color: $om-gray-500;
  margin-top: 16rpx;
  margin-bottom: 64rpx;
  text-align: center;
}

.reload-btn {
  width: 480rpx;
  height: 96rpx;
  background: $om-primary-color;
  border-radius: $om-radius-4xl;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.reload-btn text {
  font-size: 32rpx;
  color: $om-surface;
  font-weight: 500;
}

.reload-btn::after {
  border: none;
}
</style>
