<template>
  <view class="page-container">

    <!-- 加载失败时显示 -->
    <TLReload v-if="loadFailed" @reload="handleReload" />

    <!-- 主内容区 -->
    <view v-else class="main-content" :style="{ paddingTop: capsulePosition.bottom + 12 + 'px' }">
      <!-- 穴位信息卡片 -->
      <view class="acupoint-card">
        <view class="acupoint-header">
          <view class="acupoint-icon" :class="'marker-' + acupointDetail?.markerType">
            <text class="icon-text">{{ acupointDetail?.name?.charAt(0) }}</text>
          </view>
          <view class="acupoint-info">
            <text class="acupoint-name">{{ acupointDetail?.name }}</text>
            <text class="acupoint-pinyin">{{ acupointDetail?.namePinyin }}</text>
          </view>
        </view>
        
        <view class="tags-row">
          <view class="tag-meridian">{{ acupointDetail?.meridianName }}</view>
          <view class="tag-category">{{ acupointDetail?.categoryName }}</view>
          <view class="tag-marker" :class="'marker-' + acupointDetail?.markerType">{{ acupointDetail?.markerTypeName }}</view>
        </view>
      </view>

      <!-- 详细信息 -->
      <view class="detail-section">
        <view class="info-item">
          <text class="info-label">定位描述</text>
          <text class="info-content">{{ acupointDetail?.locationDescription }}</text>
        </view>
        
        <view class="info-item">
          <text class="info-label">功效作用</text>
          <text class="info-content">{{ acupointDetail?.efficacy }}</text>
        </view>
        
        <view class="info-item">
          <text class="info-label">主治病症</text>
          <text class="info-content">{{ acupointDetail?.indications }}</text>
        </view>
        
        <view class="info-item">
          <text class="info-label">操作方法</text>
          <text class="info-content">{{ acupointDetail?.operationMethod }}</text>
        </view>
        
        <view class="info-item">
          <text class="info-label">按摩提示</text>
          <text class="info-content massage-tips">{{ acupointDetail?.massageTips }}</text>
        </view>
      </view>

      <!-- 底部提示 -->
      <view class="footer-tip">
        <text>以上信息仅供参考，如有不适请咨询专业医师</text>
      </view>
    </view>
    
    <!-- 底部导航栏 -->
    <TLNavBar
      ref="navBarRef"
      :title="acupointDetail?.name || '穴位详情'"
    />

    <!-- 悬浮问AI按钮 -->
    <view v-if="acupointDetail" class="fab-ask-ai" @click="askAiAboutAcupoint">
      <view class="i-solar:chat-round-dots-outline fab-icon" />
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import TLNavBar from '@/components/TLNavBar/index.vue';
import TLReload from '@/components/TLReload/index.vue';
import { getAcupointDetail } from '@/api/wisdom/meridian';
import { usePageLayout } from '@/composables/usePageLayout';
import { useDetailLoader } from '@/composables/useDetailLoader';
import type { AcupointDetail } from '@/types/biz/wisdom/meridian';
import { useUserProfile } from '@/composables/useUserProfile';

const { constitution, fetchConstitution } = useUserProfile();

const { navBarRef, capsulePosition } = usePageLayout();

let id = ''

onLoad((options) => {
  id = options?.id || ''
})

const { data: acupointDetail, loadFailed, load: fetchAcupointDetail } = useDetailLoader<AcupointDetail>(
  () => getAcupointDetail(id)
);

/**
 * 重新加载
 */
const handleReload = () => {
  fetchAcupointDetail();
};

// 页面加载
onMounted(() => {
  fetchAcupointDetail();
  fetchConstitution();
});

// 问AI穴位按摩方法
const askAiAboutAcupoint = () => {
  const name = acupointDetail.value?.name || '这个穴位'
  const meridianName = acupointDetail.value?.meridianName || ''
  const constitutionName = constitution.value?.constitutionName || ''
  const question = constitutionName
    ? `我是${constitutionName}，${name}（${meridianName}）怎么按摩？有什么功效？`
    : `${name}（${meridianName}）怎么按摩？有什么功效？`
  uni.navigateTo({
    url: `/pages/ai/chat/index?presetQuestion=${encodeURIComponent(question)}`
  })
}
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
  padding-bottom: 32rpx;
}

.main-content {
  min-height: 100vh;
}

// 悬浮问AI按钮
.fab-ask-ai {
  position: fixed;
  right: 32rpx;
  bottom: calc(240rpx + constant(safe-area-inset-bottom));
  bottom: calc(240rpx + env(safe-area-inset-bottom));
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background: $tf-gradient-brand;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx var(--tf-brand-alpha-35);
  z-index: 100;

  &:active {
    transform: scale(0.92);
  }
}

.fab-icon {
  font-size: $tf-text-3xl;
  color: $tf-surface;
}

// 穴位信息卡片
.acupoint-card {
  background: #FFFFFF;
  margin: 24rpx;
  border-radius: 24rpx;
  padding: 32rpx;
  // box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.acupoint-header {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 24rpx;
}

.acupoint-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $tf-brand-bg;
}

.acupoint-icon.marker-2 {
  background: #FEF3C7;
}

.acupoint-icon.marker-3 {
  background: #FFE4E6;
}

.icon-text {
  font-size: 48rpx;
  color: $tf-primary-color;
  font-weight: bold;
}

.marker-2 .icon-text {
  color: #D97706;
}

.marker-3 .icon-text {
  color: #E11D48;
}

.acupoint-info {
  flex: 1;
}

.acupoint-name {
  display: block;
  font-size: 40rpx;
  color: $tf-gray-900;
  font-weight: 700;
}

.acupoint-pinyin {
  display: block;
  font-size: 26rpx;
  color: $tf-gray-500;
  margin-top: 8rpx;
}

.tags-row {
  display: flex;
  gap: 16rpx;
}

.tag-meridian {
  font-size: 24rpx;
  color: $tf-primary-color;
  padding: 8rpx 20rpx;
  background: $tf-brand-bg;
  border-radius: 16rpx;
}

.tag-category {
  font-size: 24rpx;
  color: $tf-gray-700;
  padding: 8rpx 20rpx;
  background: $tf-page-bg-color;
  border-radius: 16rpx;
}

.tag-marker {
  font-size: 24rpx;
  color: $tf-gray-600;
  padding: 8rpx 20rpx;
  background: $tf-page-bg-color;
  border-radius: 16rpx;
}

.tag-marker.marker-2 {
  color: #D97706;
  background: #FEF3C7;
}

.tag-marker.marker-3 {
  color: #E11D48;
  background: #FFE4E6;
}

// 详细信息
.detail-section {
  background: #FFFFFF;
  margin: 0 24rpx 24rpx 24rpx;
  border-radius: 24rpx;
  padding: 32rpx;
  // box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.info-item {
  margin-bottom: 32rpx;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  display: block;
  font-size: 28rpx;
  color: $tf-primary-color;
  font-weight: 600;
  margin-bottom: 16rpx;
}

.info-content {
  display: block;
  font-size: 26rpx;
  color: $tf-gray-700;
  line-height: 1.8;
}

.massage-tips {
  color: $tf-brand;
  background: $tf-brand-bg;
  padding: 24rpx;
  border-radius: 16rpx;
  margin-top: 8rpx;
}

// 底部提示
.footer-tip {
  text-align: center;
  padding: 32rpx;
  color: $tf-gray-500;
  font-size: 22rpx;
}
</style>