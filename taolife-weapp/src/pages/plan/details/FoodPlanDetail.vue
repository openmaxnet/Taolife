<template>
  <view class="page-container">

    <!-- 加载状态 -->
    <view v-if="isLoading" class="loading-state">
      <TLLoading />
    </view>

    <!-- 主内容区 -->
    <view v-if="!isLoading && planDetail" class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 饮食重点卡片 -->
      <view class="focus-card">
        <view class="focus-header">
          <view class="i-solar:map-point-linear focus-icon" />
          <text class="focus-label">饮食重点</text>
          <text class="focus-text">{{ getDietFocus() }}</text>
        </view>
        <view class="focus-tags">
          <view class="tag-item">
            <view class="i-solar:temperature-linear tag-icon" />
            <text class="tag-label">体质类型：</text>
            <text class="tag-value">{{ planDetail.constitutionName || '平和质' }}</text>
          </view>
          <view class="tag-item">
            <view class="i-solar:calendar-linear tag-icon" />
            <text class="tag-label">季节建议：</text>
            <text class="tag-value">{{ planDetail.seasonName || '春季' }}</text>
          </view>
        </view>
      </view>

      <!-- 方案内容区域 -->
      <view class="plan-content-section">
        <chatMarkdown :content="planDetail.foodPlanContent || ''" class="markdown-content" />
      </view>

    </view>

  </view>

  <!-- 顶部导航栏 -->
  <TLTopBar title="饮食方案" :show-back="true" />

  <!-- 底部导航栏 -->
  <TLNavBar
    ref="navBarRef"
    title="饮食方案"
    :buttons="['back', 'share', 'collect', 'adjust']"
    :is-collected="isCollected"
    @share="handleShare"
    @collect="handleCollect"
    @adjust="handleAdjust"
  />

  <!-- 悬浮快捷操作按钮 -->
  <view v-if="planDetail" class="fab-group">
    <view class="fab-item" @click="goToFoodLibrary">
      <view class="i-solar:chef-hat-outline fab-item-icon" />
      <text class="fab-item-text">食材库</text>
    </view>
    <view class="fab-item ai" @click="askAiAboutFood">
      <view class="i-solar:chat-round-dots-outline fab-item-icon" />
      <text class="fab-item-text">问AI</text>
    </view>
  </view>

  <!-- AI调整抽屉 -->
  <AdjustDrawer
    v-model:visible="showAdjustDrawer"
    :user-plan-id="planId"
    :plan-type="1"
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

// 收藏状态（targetType=5 方案，getTargetId 动态获取）
const { isCollected, toggleCollect: handleCollect } = useInteraction(
  { toggle: toggleInteraction, getStatus: getInteractionStatus },
  5,
  () => planId.value,
)

// AI调整抽屉
const showAdjustDrawer = ref(false)

// 获取页面参数
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

// 获取饮食重点
const getDietFocus = () => {
  const constitution = planDetail.value?.constitutionName || ''

  if (constitution.includes('气虚')) {
    return '补气养阳，健脾益胃'
  } else if (constitution.includes('阴虚')) {
    return '滋阴润燥，清热降火'
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
  } else if (constitution.includes('特禀')) {
    return '调理体质，平衡阴阳'
  }
  return '平和体质，均衡饮食'
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

// 跳转食材库
const goToFoodLibrary = () => {
  uni.navigateTo({ url: '/pages/wisdom/food/index' })
}

// 问AI饮食建议
const askAiAboutFood = () => {
  const constitution = planDetail.value?.constitutionName || ''
  const question = constitution
    ? `我是${constitution}，当前在执行饮食调理方案，请给我一些饮食建议`
    : '请给我一些中医饮食养生建议'
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
  background: linear-gradient(135deg, #F0FDF4 0%, #DCFCE7 100%);
  border-radius: 20rpx;
  padding: 32rpx;
  margin: 24rpx 24rpx;
  border-left: 8rpx solid $tf-brand;
}

.focus-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.focus-icon {
  font-size: 24rpx;
  color: $tf-brand;
  margin-right: 8rpx;
}

.focus-label {
  font-size: 24rpx;
  color: $tf-brand;
  font-weight: bold;
}

.focus-text {
  font-size: 28rpx;
  color: $tf-gray-900;
  font-weight: bold;
}

.focus-tags {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.tag-item {
  display: flex;
  align-items: center;
}

.tag-icon {
  font-size: 22rpx;
  color: $tf-brand;
  margin-right: 8rpx;
}

.tag-label {
  font-size: 22rpx;
  color: $tf-gray-600;
}

.tag-value {
  font-size: 22rpx;
  color: $tf-gray-900;
  font-weight: 500;
}

// 方案内容区域
.plan-content-section {
  padding: 0 24rpx;
}

// .markdown-content {

// }  
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
  color: $tf-brand;

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
