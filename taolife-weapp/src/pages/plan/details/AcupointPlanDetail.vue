<template>
  <view class="page-container">

    <!-- 加载状态 -->
    <view v-if="isLoading" class="loading-state">
      <TLLoading />
    </view>

    <!-- 主内容区 -->
    <view v-if="!isLoading && planDetail" class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 穴位按摩重点卡片 -->
      <view class="focus-card">
        <view class="focus-header">
          <view class="i-solar:map-point-linear focus-icon" />
          <text class="focus-label">按摩重点</text>
          <text class="focus-text">{{ getAcupointFocus() }}</text>
        </view>
        <view class="focus-info">
          <view class="info-item">
            <view class="i-solar:map-point-linear info-icon" />
            <text class="info-label">按摩目标：</text>
            <text class="info-value">{{ getAcupointGoal() }}</text>
          </view>
          <view class="info-item">
            <view class="i-solar:calendar-linear info-icon" />
            <text class="info-label">按摩频率：</text>
            <text class="info-value">每日2次，每次3-5分钟</text>
          </view>
          <view class="info-item">
            <view class="i-solar:clock-circle-linear info-icon" />
            <text class="info-label">最佳时间：</text>
            <text class="info-value">晨起7:00，晚间21:00</text>
          </view>
        </view>
      </view>

      <!-- 穴位按摩内容列表 -->
      <view class="acupoints-section">
        <chatMarkdown :content="planDetail.acupointPlanContent || ''" class="markdown-content" />
      </view>

    </view>

  </view>

  <!-- 顶部导航栏 -->
  <TLTopBar title="穴位按摩" :show-back="true" />

  <!-- 底部导航栏 -->
  <TLNavBar
    ref="navBarRef"
    title="穴位按摩"
    :buttons="['back', 'share', 'collect', 'adjust']"
    :is-collected="isCollected"
    @share="handleShare"
    @collect="handleCollect"
    @adjust="handleAdjust"
  />

  <!-- 悬浮快捷操作按钮 -->
  <view v-if="planDetail" class="fab-group">
    <view class="fab-item" @click="goToMeridianLibrary">
      <view class="i-solar:health-outline fab-item-icon" />
      <text class="fab-item-text">穴位库</text>
    </view>
    <view class="fab-item ai" @click="askAiAboutAcupoint">
      <view class="i-solar:chat-round-dots-outline fab-item-icon" />
      <text class="fab-item-text">问AI</text>
    </view>
  </view>

  <!-- AI调整抽屉 -->
  <AdjustDrawer
    v-model:visible="showAdjustDrawer"
    :user-plan-id="planId"
    :plan-type="3"
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

// 获取穴位按摩重点
const getAcupointFocus = () => {
  const constitution = planDetail.value?.constitutionName || ''

  if (constitution.includes('气虚')) {
    return '温补阳气，健脾益胃'
  } else if (constitution.includes('阴虚')) {
    return '滋阴降火，宁心安神'
  } else if (constitution.includes('阳虚')) {
    return '温补肾阳，培元固本'
  } else if (constitution.includes('痰湿')) {
    return '健脾化痰，祛湿利水'
  } else if (constitution.includes('湿热')) {
    return '清热利湿，健脾和胃'
  } else if (constitution.includes('血瘀')) {
    return '活血化瘀，通络止痛'
  } else if (constitution.includes('气郁')) {
    return '疏肝解郁，调理气机'
  }
  return '调和气血，平衡阴阳'
}

// 获取穴位按摩目标
const getAcupointGoal = () => {
  const constitution = planDetail.value?.constitutionName || ''

  if (constitution.includes('气虚')) {
    return '调理脾胃，增强体质'
  } else if (constitution.includes('阴虚')) {
    return '滋阴降火，养心安神'
  } else if (constitution.includes('阳虚')) {
    return '温补肾阳，培元固本'
  } else if (constitution.includes('痰湿')) {
    return '健脾化痰，祛湿利水'
  } else if (constitution.includes('湿热')) {
    return '清热利湿，健脾和胃'
  } else if (constitution.includes('血瘀')) {
    return '活血化瘀，通络止痛'
  } else if (constitution.includes('气郁')) {
    return '疏肝解郁，调理气机'
  }
  return '调和气血，平衡阴阳'
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

// 跳转经络穴位库
const goToMeridianLibrary = () => {
  uni.navigateTo({ url: '/pages/wisdom/meridian/index' })
}

// 问AI穴位按摩
const askAiAboutAcupoint = () => {
  const constitution = planDetail.value?.constitutionName || ''
  const question = constitution
    ? `我是${constitution}，想了解适合我的穴位按摩方法`
    : '请推荐一些日常保健穴位按摩方法'
  uni.navigateTo({
    url: `/pages/ai/chat/index?presetQuestion=${encodeURIComponent(question)}`
  })
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
  background: linear-gradient(135deg, #FCE7F3 0%, #FDBA74 100%);
  border-radius: 20rpx;
  padding: 32rpx;
  margin: 24rpx 24rpx;
  border-left: 8rpx solid $tf-warning;
}

.focus-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.focus-icon {
  font-size: 24rpx;
  color: #B45309;
  margin-right: 8rpx;
}

.focus-label {
  font-size: 24rpx;
  color: #B45309;
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
  color: #B45309;
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

// 穴位区域
.acupoints-section {
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
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 16rpx;
  padding-bottom: 16rpx;
  border-bottom: $tf-border-light;
}

.time-icon {
  font-size: 36rpx;
  color: #B45309;
}

.title-text {
  font-size: 28rpx;
  color: $tf-gray-900;
  font-weight: bold;
}

.section-content {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.content-text {
  font-size: 26rpx;
  color: $tf-gray-700;
  line-height: 1.8;
  white-space: pre-wrap;
}

// 加载状态
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
  color: $tf-warning;

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