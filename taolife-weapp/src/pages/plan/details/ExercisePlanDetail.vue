<template>
  <view class="page-container">

    <!-- 加载状态 -->
    <view v-if="isLoading" class="loading-state">
      <TLLoading />
    </view>

    <!-- 主内容区 -->
    <view v-if="!isLoading && planDetail" class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 运动重点卡片 -->
      <view class="focus-card">
        <view class="focus-header">
          <view class="i-solar:map-point-linear focus-icon" />
          <text class="focus-label">运动重点</text>
          <text class="focus-text">{{ getExerciseFocus() }}</text>
        </view>
        <view class="focus-info">
          <view class="info-item">
            <view class="i-solar:flag-linear info-icon" />
            <text class="info-label">运动目标：</text>
            <text class="info-value">{{ getExerciseGoal() }}</text>
          </view>
          <view class="info-item">
            <view class="i-solar:calendar-linear info-icon" />
            <text class="info-label">运动频率：</text>
            <text class="info-value">{{ planDetail.seasonName }}</text>
          </view>
          <view class="info-item">
            <view class="i-solar:clock-circle-linear info-icon" />
            <text class="info-label">最佳时间：</text>
            <text class="info-value">上午8-9点，下午5-6点</text>
          </view>
        </view>
      </view>

      <!-- 运动内容列表 -->
      <view class="exercises-section">
        <chatMarkdown :content="planDetail.exercisePlanContent || ''" class="markdown-content" />
      </view>

      <!-- 今日运动统计 -->
      <view class="stats-section">
        <view class="section-title">
          <view class="title-line"></view>
          <view class="i-solar:chart-linear section-icon" />
          <text>今日运动统计</text>
        </view>
        <view class="stats-card">
          <view class="stat-row">
            <text class="stat-label">方案周期：</text>
            <text class="stat-value">{{ planDetail.cycleDays }}天</text>
          </view>
          <view class="stat-row">
            <view class="i-solar:check-circle-broken stat-icon" />
            <text class="stat-label">完成进度：</text>
            <text class="stat-value completed">{{ planDetail.completionRate }}%</text>
          </view>
          <view class="stat-row">
            <view class="i-solar:calendar-linear stat-icon" />
            <text class="stat-label">方案时间：</text>
            <text class="stat-value">{{ planDetail.startDate }} ~ {{ planDetail.endDate }}</text>
          </view>
        </view>
      </view>

    </view>

  </view>

  <!-- 顶部导航栏 -->
  <TLTopBar title="运动方案" :show-back="true" />

  <!-- 底部导航栏 -->
  <TLNavBar
    ref="navBarRef"
    title="运动方案"
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
    :plan-type="2"
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

// 加载状态
const isLoading = ref(true)

// 方案详情
const planDetail = ref<HealthPlanDetail | null>(null)

// 方案ID
const planId = ref('')

// 收藏状态（targetType=5 方案）
const { isCollected, toggleCollect: handleCollect } = useInteraction(
  { toggle: toggleInteraction, getStatus: getInteractionStatus },
  5,
  () => planId.value,
)

// AI调整抽屉
const showAdjustDrawer = ref(false)

onMounted(() => {
  const options = getPageOptions()

  planId.value = options.id || ''

  if (planId.value) {
    loadPlanDetail()
  } else {
    uni.showToast({
      title: '方案ID不存在',
      icon: 'none'
    })
    uni.navigateBack()
  }
})

// 加载方案详情
const loadPlanDetail = async () => {
  isLoading.value = true
  try {
    const detail = await getHealthPlanDetail(planId.value)
    planDetail.value = detail
  } catch (error) {
    console.error('加载方案详情失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  } finally {
    isLoading.value = false
  }
}

// 获取运动重点
const getExerciseFocus = () => {
  const constitution = planDetail.value?.constitutionName || ''

  if (constitution.includes('气虚')) {
    return '温和运动，避免大汗淋漓'
  } else if (constitution.includes('阴虚')) {
    return '适度运动，避免剧烈运动'
  } else if (constitution.includes('阳虚')) {
    return '有氧运动，增强体质'
  } else if (constitution.includes('痰湿')) {
    return '有氧运动，祛湿利水'
  } else if (constitution.includes('湿热')) {
    return '清热利湿，适度运动'
  } else if (constitution.includes('血瘀')) {
    return '有氧运动，活血化瘀'
  } else if (constitution.includes('气郁')) {
    return '舒缓运动，疏肝解郁'
  }
  return '适量运动，保持健康'
}

// 获取运动目标
const getExerciseGoal = () => {
  const constitution = planDetail.value?.constitutionName || ''

  if (constitution.includes('气虚')) {
    return '补气养阳，增强体质'
  } else if (constitution.includes('阴虚')) {
    return '滋阴降火，养心安神'
  } else if (constitution.includes('阳虚')) {
    return '温补阳气，散寒止痛'
  } else if (constitution.includes('痰湿')) {
    return '健脾化痰，祛湿利水'
  } else if (constitution.includes('湿热')) {
    return '清热利湿，调理脾胃'
  } else if (constitution.includes('血瘀')) {
    return '活血化瘀，通络止痛'
  } else if (constitution.includes('气郁')) {
    return '疏肝解郁，调理气机'
  }
  return '保持健康，增强体质'
}

// 分享方案
const handleShare = () => {
  uni.showToast({
    title: '分享功能开发中',
    icon: 'none'
  })
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

// 内容区域
.content-area {
  padding-bottom: 32rpx;
}
.focus-card {
  background: linear-gradient(135deg, #DBEAFE 0%, #BFDBFE 100%);
  border-radius: 20rpx;
  padding: 32rpx;
  margin: 24rpx 24rpx;
  border-left: 8rpx solid #3B82F6;
}

.focus-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.focus-icon {
  font-size: 24rpx;
  color: #1E40AF;
  margin-right: 8rpx;
}

.focus-label {
  font-size: 24rpx;
  color: #1E40AF;
  font-weight: bold;
}

.focus-text {
  font-size: 28rpx;
  color: $tf-gray-900;
  font-weight: bold;
}

.focus-info {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-icon {
  font-size: 22rpx;
  color: #1E40AF;
  margin-right: 8rpx;
}

.info-label {
  font-size: 22rpx;
  color: $tf-gray-600;
}

.info-value {
  font-size: 22rpx;
  color: $tf-gray-900;
  font-weight: 500;
}

// 运动区域
.exercises-section {
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

.section-title {
  font-size: 26rpx;
  color: $tf-gray-700;
  line-height: 1.8;
  white-space: pre-wrap;
}

// 统计区域
.stats-section {
  padding: 0 24rpx;
  margin-bottom: 24rpx;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.section-icon {
  font-size: 30rpx;
  color: #3B82F6;
  margin-right: 8rpx;
}

.title-line {
  width: 8rpx;
  height: 32rpx;
  background: #3B82F6;
  border-radius: 4rpx;
}

.section-title text {
  font-size: 30rpx;
  color: $tf-gray-900;
  font-weight: bold;
}

.stats-card {
  background: $tf-surface;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.06);
}

.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: $tf-border-light;
}

.stat-row:last-child {
  border-bottom: none;
}

.stat-icon {
  font-size: 24rpx;
  color: #3B82F6;
  margin-right: 8rpx;
}

.stat-label {
  font-size: 24rpx;
  color: $tf-gray-600;
}

.stat-value {
  font-size: 26rpx;
  color: $tf-gray-900;
  font-weight: bold;
}

.stat-value.completed {
  color: $tf-brand;
}

// 加载状态
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 600rpx;
}

</style>