<template>
  <view class="page-container">

    <!-- 加载状态 -->
    <view v-if="isLoading" class="loading-state">
      <TLLoading />
    </view>

    <!-- 主内容区 -->
    <view v-if="!isLoading && planDetail" class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 经络调理重点卡片 -->
      <view class="focus-card">
        <view class="focus-header">
          <view class="i-solar:bolt-outline focus-icon" />
          <text class="focus-label">调理重点</text>
          <text class="focus-text">{{ getMeridianFocus() }}</text>
        </view>
        <view class="focus-info">
          <view class="info-item">
            <view class="i-solar:hourglass-outline info-icon" />
            <text class="info-label">每日次数：</text>
            <text class="info-value">{{ planDetail.seasonName }}</text>
          </view>
          <view class="info-item">
            <view class="i-solar:clock-circle-linear info-icon" />
            <text class="info-label">最佳时间：</text>
            <text class="info-value">早晨或睡前</text>
          </view>
        </view>
      </view>

      <!-- 经络调理内容 -->
      <view class="meridian-section">
        <chatMarkdown :content="planDetail.meridianPlanContent || ''" class="markdown-content" />
      </view>

      <!-- 调理统计 -->
      <view class="stats-section">
        <view class="section-title">
          <view class="title-line"></view>
          <view class="i-solar:chart-linear section-icon" />
          <text>经络调理统计</text>
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
  <TLTopBar title="经络调理" :show-back="true" />

  <!-- 底部导航栏 -->
  <TLNavBar
    ref="navBarRef"
    title="经络调理"
    :buttons="['back', 'share', 'collect', 'adjust']"
    :is-collected="isCollected"
    @share="handleShare"
    @collect="handleCollect"
    @adjust="handleAdjust"
  />

  <!-- 悬浮快捷操作按钮 -->
  <view v-if="planDetail" class="fab-group">
    <view class="fab-item" @click="goToMeridianLibrary">
      <view class="i-solar:body-outline fab-item-icon" />
      <text class="fab-item-text">经络库</text>
    </view>
    <view class="fab-item ai" @click="askAiAboutMeridian">
      <view class="i-solar:chat-round-dots-outline fab-item-icon" />
      <text class="fab-item-text">问AI</text>
    </view>
  </view>

  <!-- AI调整抽屉 -->
  <AdjustDrawer
    v-model:visible="showAdjustDrawer"
    :user-plan-id="planId"
    :plan-type="4"
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

const getMeridianFocus = () => {
  const constitution = planDetail.value?.constitutionName || ''
  if (constitution.includes('气虚')) return '疏通经络，补气行气'
  if (constitution.includes('阴虚')) return '滋阴润燥，调理经络'
  if (constitution.includes('阳虚')) return '温通经络，散寒止痛'
  if (constitution.includes('痰湿')) return '化痰祛湿，通络利水'
  if (constitution.includes('湿热')) return '清热利湿，疏通经络'
  if (constitution.includes('血瘀')) return '活血化瘀，通络止痛'
  if (constitution.includes('气郁')) return '疏肝解郁，调理气机'
  return '疏通经络，调和气血'
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

// 跳转经络穴位库
const goToMeridianLibrary = () => {
  uni.navigateTo({ url: '/pages/wisdom/meridian/index' })
}

// 问AI经络调理
const askAiAboutMeridian = () => {
  const constitution = planDetail.value?.constitutionName || ''
  const question = constitution
    ? `我是${constitution}，想了解适合我的经络疏通和调理方法`
    : '请推荐一些日常经络疏通的方法'
  uni.navigateTo({
    url: `/pages/ai/chat/index?presetQuestion=${encodeURIComponent(question)}`
  })
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
  background: linear-gradient(135deg, #7C3AED 0%, #A78BFA 100%);
  border-radius: 20rpx;
  padding: 32rpx;
  margin: 24rpx 24rpx;
  border-left: 8rpx solid #7C3AED;
}

.focus-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.focus-icon {
  font-size: 24rpx;
  color: #DDD6FE;
  margin-right: 8rpx;
}

.focus-label {
  font-size: 24rpx;
  color: #DDD6FE;
  font-weight: bold;
}

.focus-text {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: bold;
  margin-left: 12rpx;
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
  color: #DDD6FE;
}

.info-label {
  font-size: 22rpx;
  color: #DDD6FE;
}

.info-value {
  font-size: 24rpx;
  color: #FFFFFF;
}

.meridian-section {
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

// 悬浮快捷操作按钮组
.fab-group {
  position: fixed;
  right: 32rpx;
  bottom: calc(240rpx + constant(safe-area-inset-bottom));
  bottom: calc(240rpx + env(safe-area-inset-bottom));
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  z-index: 100;
}

.fab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background: $tf-surface;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.12);

  &.ai {
    background: $tf-gradient-brand;
    box-shadow: 0 4rpx 16rpx var(--tf-brand-alpha-35);
  }

  &:active {
    transform: scale(0.92);
  }
}

.fab-item-icon {
  font-size: $tf-text-2xl;
  color: #7C3AED;

  .ai & {
    color: $tf-surface;
  }
}

.fab-item-text {
  font-size: 18rpx;
  color: $tf-gray-700;
  margin-top: 2rpx;

  .ai & {
    color: $tf-surface;
  }
}
</style>
