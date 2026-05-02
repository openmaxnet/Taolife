<template>
  <view class="top-bar" :style="{ paddingTop: capsulePosition.top + 'px', paddingBottom: 18 + 'px', height: navbarHeight + 'px' }">
    <view class="top-bar-container" :style="{ height: capsulePosition.height + 'px' }">
      <!-- 搜索模式：Logo + 搜索框都在左侧 -->
      <template v-if="showSearch">
        <slot name="left">
          <view v-if="showLogo" class="top-bar-logo">
            <image class="logo-image" src="/static/logo.png" mode="aspectFit" />
          </view>
        </slot>
        <view class="search-box" @click="handleSearchClick">
          <view class="i-solar:magnifer-linear search-icon" />
          <input
            class="search-input"
            :placeholder="searchPlaceholder"
            :value="searchValue"
            confirm-type="search"
            @input="handleSearchInput"
            @confirm="handleSearchConfirm"
          />
        </view>
        <view class="top-bar-right">
          <slot name="right" />
        </view>
      </template>

      <!-- 默认模式：左中右布局 -->
      <template v-else>
        <view class="top-bar-left">
          <slot name="left">
            <view v-if="showBack" class="default-back" @click="handleBack">
              <view class="back-icon i-solar:alt-arrow-left-outline" />
            </view>
          </slot>
        </view>

        <view class="top-bar-center">
          <slot>
            <text class="default-title">{{ title }}</text>
          </slot>
        </view>

        <view class="top-bar-right">
          <slot name="right" />
        </view>
      </template>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { getCapsulePosition, getNavbarHeight } from '@/utils/capsule';

/**
 * 组件属性
 */
withDefaults(
  defineProps<{
    /** 页面标题 */
    title?: string;
    /** 是否显示返回按钮 */
    showBack?: boolean;
    /** 是否显示 Logo */
    showLogo?: boolean;
    /** 是否显示搜索框 */
    showSearch?: boolean;
    /** 搜索框占位文字 */
    searchPlaceholder?: string;
    /** 搜索框值（受控） */
    searchValue?: string;
  }>(),
  {
    title: '',
    showBack: true,
    showLogo: false,
    showSearch: false,
    searchPlaceholder: '搜索',
    searchValue: '',
  }
);

/**
 * 组件事件
 */
const emit = defineEmits<{
  (e: 'back'): void;
  (e: 'search', keyword: string): void;
  (e: 'searchInput', keyword: string): void;
  (e: 'searchClick'): void;
}>();

// 胶囊位置信息
const capsulePosition = ref(getCapsulePosition());

// 导航栏高度
const navbarHeight = computed(() => getNavbarHeight());

/**
 * 处理返回点击
 */
const handleBack = () => {
  // 获取当前页面栈
  const pages = getCurrentPages();
  
  // 如果页面栈长度大于1，说明不是第一页，可以返回
  if (pages.length > 1) {
    uni.navigateBack();
  } else {
    // 如果是第一页，可以选择跳转到首页或提示用户
    uni.switchTab({
      url: '/pages/index/index',
      fail: () => {
        // 如果跳转失败，尝试使用 navigateTo
        uni.navigateTo({
          url: '/pages/index/index',
        });
      },
    });
  }
  
  emit('back');
};

const handleSearchClick = () => emit('searchClick');

const handleSearchInput = (e: any) => emit('searchInput', e.detail?.value ?? '');

const handleSearchConfirm = (e: any) => emit('search', e.detail?.value ?? '');

// 页面加载时更新胶囊位置
onMounted(() => {
  capsulePosition.value = getCapsulePosition();
});
</script>

<style lang="scss">
.top-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: $tf-page-bg-color;
  z-index: 1000;
}

.top-bar-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24rpx;
}

.top-bar-left,
.top-bar-right {
  flex: 1;
  display: flex;
  align-items: center;
}

.top-bar-left {
  justify-content: flex-start;
}

.top-bar-right {
  justify-content: flex-end;
}

.top-bar-center {
  flex: 2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.default-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  width: 44rpx;
  height: 44rpx;
  color: $tf-gray-900;
}

.action-item {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 8rpx;
}

.action-icon {
  width: 44rpx;
  height: 44rpx;
  color: $tf-gray-900;
}

.default-title {
  font-size: 34rpx;
  color: $tf-gray-900;
  font-weight: bold;
  max-width: 400rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.top-bar-logo {
  display: flex;
  align-items: center;
  margin-right: 16rpx;
}

.logo-image {
  width: 60rpx;
  height: 60rpx;
  border-radius: 12rpx;
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  background: $tf-surface;
  border-radius: 36rpx;
  padding: 0 24rpx;
  height: 64rpx;
  gap: 12rpx;
}

.search-icon {
  font-size: 28rpx;
  color: $tf-gray-400;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  font-size: 26rpx;
  color: $tf-gray-900;
  height: 100%;
}
</style>
