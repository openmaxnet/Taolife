<template>
  <view class="page-container">

    <!-- 加载状态 -->
    <view v-if="isLoading" class="loading-state">
      <TLLoading />
    </view>

    <!-- 主内容区 -->
    <view v-if="!isLoading && planDetail" class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 生活起居重点卡片 -->
      <view class="focus-card">
        <view class="focus-header">
          <view class="i-solar:moon-outline focus-icon" />
          <text class="focus-label">生活起居重点</text>
        </view>
        <view class="focus-sub">{{ getLifestyleFocus() }}</view>
        <view class="focus-info">
          <view class="info-item">
            <view class="i-solar:clock-circle-linear info-icon" />
            <text class="info-label">建议睡眠：</text>
            <text class="info-value">{{ getSleepTime() }}</text>
          </view>
          <view class="info-item">
            <view class="i-solar:sun-outline info-icon" />
            <text class="info-label">季节适配：</text>
            <text class="info-value">{{ planDetail.seasonName }}</text>
          </view>
        </view>
      </view>

      <!-- 生活起居内容 -->
      <view class="lifestyle-section">
        <chatMarkdown :content="planDetail.lifestylePlanContent || ''" class="markdown-content" />
      </view>

      <!-- 起居统计 -->
      <view class="stats-section">
        <view class="section-title">
          <view class="title-line"></view>
          <view class="i-solar:chart-linear section-icon" />
          <text>执行统计</text>
        </view>
        <view class="stats-card">
          <view class="stat-row">
            <text class="stat-label">方案周期：</text>
            <text class="stat-value">{{ planDetail.cycleDays }}天</text>
          </view>
          <view class="stat-row">
            <view class="i-solar:check-circle-bold stat-icon" />
            <text class="stat-label">完成进度：</text>
            <text class="stat-value completed">{{ planDetail.completionRate }}%</text>
          </view>
          <view class="stat-row">
            <view class="i-solar:settings-linear stat-icon" />
            <text class="stat-label">AI调整次数：</text>
            <text class="stat-value">{{ planDetail.aiAdjustmentCount || 0 }}次</text>
          </view>
        </view>
      </view>

    </view>

  </view>

  <!-- 顶部导航栏 -->
  <TLTopBar title="生活起居" :show-back="true" />

  <!-- 底部导航栏 -->
  <TLNavBar
    ref="navBarRef"
    title="生活起居"
    :buttons="['back', 'share', 'collect', 'adjust']"
    :is-collected="isCollected"
    @share="handleShare"
    @collect="handleCollect"
    @adjust="handleAdjust"
  />

  <!-- AI调整抽屉 -->
  <AdjustDrawer
    v-model:visible="showAdjustDrawer"
    :user-plan-id="planId"
    :plan-type="5"
    @confirmed="onAdjustConfirmed"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLNavBar from '@/components/TLNavBar/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { useInteraction } from '@/composables/useInteraction'
import { toggleInteraction, getInteractionStatus } from '@/api/identity/interaction'
import { getPageOptions } from '@/utils/router'
import { getHealthPlanDetail } from '@/api/plan/plan'
import type { HealthPlanDetail } from '@/types/biz/plan/plan'
import chatMarkdown from '@tdesign/uniapp-chat/chat-markdown/chat-markdown.vue'
import AdjustDrawer from './AdjustDrawer.vue'

// 页面布局
const { contentPaddingTop, navBarRef } = usePageLayout()

const isLoading = ref(true)
const planDetail = ref<HealthPlanDetail | null>(null)
const planId = ref('')

// 收藏状态（targetType=5 方案）
const { isCollected, toggleCollect: handleCollect } = useInteraction(
  { toggle: toggleInteraction, getStatus: getInteractionStatus },
  5,
  () => planId.value,
)

// AI调整抽屉
const showAdjustDrawer = ref(false)

const getLifestyleFocus = () => {
  const constitution = planDetail.value?.constitutionName || ''
  if (constitution.includes('气虚')) return '规律作息，充足睡眠，避免过度劳累'
  if (constitution.includes('阴虚')) return '早睡养阴，避免熬夜，适度午休'
  if (constitution.includes('阳虚')) return '防寒保暖，适度晒太阳，早睡早起'
  if (constitution.includes('痰湿')) return '规律作息，适度运动，避免久坐'
  if (constitution.includes('湿热')) return '规律作息，清热祛湿，保持干燥'
  if (constitution.includes('血瘀')) return '规律作息，适度运动，促进循环'
  if (constitution.includes('气郁')) return '规律作息，调节情志，适度社交'
  return '规律作息，起居有常，恬淡虚无'
}

const getSleepTime = () => {
  return '22:00入睡，6:00起床'
}

onMounted(() => {
  const options = getPageOptions()
  planId.value = options.id || ''
  if (planId.value) {
    loadPlanDetail()
  } else {
    uni.showToast({ title: '方案ID不存在', icon: 'none' })
    uni.navigateBack()
  }
})

const loadPlanDetail = async () => {
  isLoading.value = true
  try {
    const detail = await getHealthPlanDetail(planId.value)
    planDetail.value = detail
  } catch (error) {
    console.error('加载方案详情失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    isLoading.value = false
  }
}

const handleShare = () => {
  uni.showToast({ title: '分享功能开发中', icon: 'none' })
}

// 调整方案
const handleAdjust = () => {
  showAdjustDrawer.value = true
}

// AI调整确认后刷新
const onAdjustConfirmed = () => {
  loadPlanDetail()
}

</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
}

.content-area {
  padding-bottom: 32rpx;
}

.focus-card {
  background: linear-gradient(135deg, #1E3A5F 0%, #2563EB 100%);
  border-radius: 20rpx;
  padding: 32rpx;
  margin: 24rpx 24rpx;
  border-left: 8rpx solid #2563EB;
}

.focus-header {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}

.focus-icon {
  font-size: 24rpx;
  color: #BFDBFE;
  margin-right: 8rpx;
}

.focus-label {
  font-size: 24rpx;
  color: #BFDBFE;
  font-weight: bold;
}

.focus-sub {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: bold;
  margin-bottom: 20rpx;
  padding-left: 32rpx;
}

.focus-info {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.info-icon {
  font-size: 22rpx;
  color: #93C5FD;
}

.info-label {
  font-size: 22rpx;
  color: #93C5FD;
}

.info-value {
  font-size: 24rpx;
  color: #FFFFFF;
}

.lifestyle-section {
  padding: 0 24rpx;
}

:deep(.t-chat-markdown) {
  background: $tf-surface;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.06);

  h1, h2, h3, h4, h5, h6 {
    font-weight: bold;
    color: $tf-gray-900;
    margin-top: 0;
    margin-bottom: 16rpx;
  }
  h1 { font-size: 32rpx; }
  h2 { font-size: 28rpx; }
  h3 { font-size: 26rpx; }
  h4, h5, h6 { font-size: 24rpx; }

  p { font-size: 24rpx; line-height: 1.8; color: $tf-gray-700; }
  ul, ol { font-size: 24rpx; color: $tf-gray-700; padding-left: 32rpx; }
  li { margin-bottom: 8rpx; }
  strong { color: $tf-gray-900; }
  em { color: $tf-gray-600; }
}

.stats-section {
  padding: 0 24rpx;
  margin-top: 24rpx;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.title-line {
  width: 8rpx;
  height: 32rpx;
  background: $tf-brand;
  border-radius: 4rpx;
}

.section-icon {
  font-size: 28rpx;
  color: $tf-brand;
}

.section-title text {
  font-size: 30rpx;
  color: $tf-gray-900;
  font-weight: bold;
}

.stats-card {
  background: $tf-surface;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.stat-row {
  display: flex;
  align-items: center;
  padding: 12rpx 0;
  border-bottom: $tf-border-light;
  &:last-child {
    border-bottom: none;
  }
}

.stat-icon {
  font-size: 24rpx;
  color: $tf-brand;
  margin-right: 8rpx;
}

.stat-label {
  font-size: 24rpx;
  color: $tf-gray-600;
  flex: 1;
}

.stat-value {
  font-size: 24rpx;
  color: $tf-gray-900;
  font-weight: bold;
}

.stat-value.completed {
  color: $tf-brand;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 600rpx;
}

</style>
