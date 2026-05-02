<template>
  <view
    class="empty-page"
    :style="{
      padding: padding,
      minHeight: minHeight,
    }"
  >
    <!-- 图标区域 -->
    <view
      class="icon-wrapper"
      :style="{
        width: iconSize,
        height: iconSize,
      }"
    >
      <view
        class="empty-icon i-solar:running-2-outline"
        :style="{
          width: iconSize,
          height: iconSize,
        }"
      />
    </view>

    <!-- 空状态信息 -->
    <text
      class="empty-title"
      :style="{
        fontSize: titleSize,
        marginTop: titleMargin,
      }"
    >{{ title }}</text>
    <text
      class="empty-desc"
      :style="{
        fontSize: descSize,
      }"
    >{{ description }}</text>

    <!-- 操作按钮（可选） -->
    <button v-if="buttonText" class="empty-btn" @click="handleClick">
      <text>{{ buttonText }}</text>
    </button>
  </view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  /** 标题 */
  title?: string;
  /** 描述 */
  description?: string;
  /** 按钮文字 */
  buttonText?: string;
  /** 图标大小 */
  iconSize?: string;
  /** 容器内边距 */
  padding?: string;
  /** 最小高度 */
  minHeight?: string;
  /** 标题字号 */
  titleSize?: string;
  /** 标题上方间距 */
  titleMargin?: string;
  /** 描述字号 */
  descSize?: string;
}>(), {
  title: '暂无内容',
  description: '',
  iconSize: '120rpx',
  padding: '200rpx 64rpx',
  minHeight: '400rpx',
  titleSize: '32rpx',
  titleMargin: '32rpx',
  descSize: '26rpx',
});

const emit = defineEmits<{
  (e: 'click'): void;
}>();

/**
 * 处理点击
 */
const handleClick = () => {
  emit('click');
};
</script>

<style lang="scss">
.empty-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  box-sizing: border-box;
}

.icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon {
  color: $tf-gray-500;
  width: 120rpx;
  height: 120rpx;
}

.empty-title {
  display: block;
  color: $tf-gray-900;
  font-weight: 500;
  text-align: center;
}

.empty-desc {
  display: block;
  color: $tf-gray-500;
  margin-top: 16rpx;
  text-align: center;
}

.empty-btn {
  margin-top: 48rpx;
  width: 320rpx;
  height: 80rpx;
  background: $tf-primary-color;
  border-radius: $tf-radius-3xl;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.empty-btn text {
  font-size: 28rpx;
  color: $tf-surface;
  font-weight: 500;
}

.empty-btn::after {
  border: none;
}
</style>
