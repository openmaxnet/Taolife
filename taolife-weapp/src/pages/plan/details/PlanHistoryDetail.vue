<template>
  <view class="page-container">

    <!-- 顶部导航栏 -->
    <TLTopBar :title="planDetail?.planTitle || '方案详情'" :show-back="true" />

    <!-- 加载状态 -->
    <view v-if="isLoading" class="loading-state" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <TLLoading />
    </view>

    <!-- 主内容区 -->
    <view v-if="!isLoading && planDetail" class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 方案信息卡片 -->
      <view class="info-card">
        <view class="info-row">
          <view class="info-item">
            <view class="i-solar:body-outline info-icon" />
            <text class="info-label">体质：</text>
            <text class="info-value">{{ planDetail.constitutionName || '平和质' }}</text>
          </view>
          <view class="info-item">
            <view class="i-solar:sun-outline info-icon" />
            <text class="info-label">季节：</text>
            <text class="info-value">{{ planDetail.seasonName }}</text>
          </view>
        </view>
        <view class="info-row">
          <view class="info-item">
            <view class="i-solar:calendar-linear info-icon" />
            <text class="info-label">周期：</text>
            <text class="info-value">{{ planDetail.cycleDays }}天</text>
          </view>
          <view class="info-item">
            <view class="i-solar:check-circle-broken info-icon" />
            <text class="info-label">完成率：</text>
            <text class="info-value">{{ planDetail.completionRate }}%</text>
          </view>
        </view>
        <view v-if="planDetail.startDate" class="info-row">
          <view class="info-item">
            <view class="i-solar:clock-circle-linear info-icon" />
            <text class="info-value">{{ planDetail.startDate }} ~ {{ planDetail.endDate }}</text>
          </view>
        </view>
      </view>

      <!-- 周期报告 -->
      <view v-if="cycleReport" class="section-panel">
        <view class="section-header" @click="toggleSection('report')">
          <view class="i-solar:document-text-outline section-icon report-icon" />
          <text class="section-title">周期调理报告</text>
          <view class="section-arrow" :class="{ expanded: expandedSections.report }">
            <view class="i-solar:alt-arrow-down-outline arrow-icon" />
          </view>
        </view>
        <view v-if="expandedSections.report" class="section-body">
          <chatMarkdown :content="cycleReport.reportContent" />
        </view>
      </view>

      <!-- 饮食方案 -->
      <view v-if="planDetail.foodPlanContent" class="section-panel">
        <view class="section-header" @click="toggleSection('food')">
          <view class="i-solar:chef-hat-outline section-icon food-icon" />
          <text class="section-title">饮食方案</text>
          <view class="section-arrow" :class="{ expanded: expandedSections.food }">
            <view class="i-solar:alt-arrow-down-outline arrow-icon" />
          </view>
        </view>
        <view v-if="expandedSections.food" class="section-body">
          <chatMarkdown :content="planDetail.foodPlanContent" />
        </view>
      </view>

      <!-- 运动方案 -->
      <view v-if="planDetail.exercisePlanContent" class="section-panel">
        <view class="section-header" @click="toggleSection('exercise')">
          <view class="i-solar:running-round-outline section-icon exercise-icon" />
          <text class="section-title">运动方案</text>
          <view class="section-arrow" :class="{ expanded: expandedSections.exercise }">
            <view class="i-solar:alt-arrow-down-outline arrow-icon" />
          </view>
        </view>
        <view v-if="expandedSections.exercise" class="section-body">
          <chatMarkdown :content="planDetail.exercisePlanContent" />
        </view>
      </view>

      <!-- 穴位按摩 -->
      <view v-if="planDetail.acupointPlanContent" class="section-panel">
        <view class="section-header" @click="toggleSection('acupoint')">
          <view class="i-solar:health-outline section-icon acupoint-icon" />
          <text class="section-title">穴位按摩</text>
          <view class="section-arrow" :class="{ expanded: expandedSections.acupoint }">
            <view class="i-solar:alt-arrow-down-outline arrow-icon" />
          </view>
        </view>
        <view v-if="expandedSections.acupoint" class="section-body">
          <chatMarkdown :content="planDetail.acupointPlanContent" />
        </view>
      </view>

      <!-- 经络调理 -->
      <view v-if="planDetail.meridianPlanContent" class="section-panel">
        <view class="section-header" @click="toggleSection('meridian')">
          <view class="i-solar:bolt-outline section-icon meridian-icon" />
          <text class="section-title">经络调理</text>
          <view class="section-arrow" :class="{ expanded: expandedSections.meridian }">
            <view class="i-solar:alt-arrow-down-outline arrow-icon" />
          </view>
        </view>
        <view v-if="expandedSections.meridian" class="section-body">
          <chatMarkdown :content="planDetail.meridianPlanContent" />
        </view>
      </view>

      <!-- 生活起居 -->
      <view v-if="planDetail.lifestylePlanContent" class="section-panel">
        <view class="section-header" @click="toggleSection('lifestyle')">
          <view class="i-solar:moon-outline section-icon lifestyle-icon" />
          <text class="section-title">生活起居</text>
          <view class="section-arrow" :class="{ expanded: expandedSections.lifestyle }">
            <view class="i-solar:alt-arrow-down-outline arrow-icon" />
          </view>
        </view>
        <view v-if="expandedSections.lifestyle" class="section-body">
          <chatMarkdown :content="planDetail.lifestylePlanContent" />
        </view>
      </view>

    </view>

  </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import chatMarkdown from '@tdesign/uniapp-chat/chat-markdown/chat-markdown.vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { getPageOptions } from '@/utils/router'
import { getHealthPlanDetail, getCycleReportByPlanId } from '@/api/plan/plan'
import type { HealthPlanDetail } from '@/types/biz/plan/plan'

const { contentPaddingTop } = usePageLayout()

const isLoading = ref(true)
const planDetail = ref<HealthPlanDetail | null>(null)
const cycleReport = ref<{ id: string; planId: string; newPlanId: string; reportContent: string } | null>(null)

const expandedSections = reactive({
  report: true,
  food: false,
  exercise: false,
  acupoint: false,
  meridian: false,
  lifestyle: false,
})

const toggleSection = (key: keyof typeof expandedSections) => {
  expandedSections[key] = !expandedSections[key]
}

onMounted(async () => {
  const options = getPageOptions()
  const planId = options.id || ''

  if (!planId) {
    uni.showToast({ title: '方案ID不存在', icon: 'none' })
    uni.navigateBack()
    return
  }

  isLoading.value = true
  try {
    const [detail, report] = await Promise.all([
      getHealthPlanDetail(planId),
      getCycleReportByPlanId(planId),
    ])
    planDetail.value = detail
    cycleReport.value = report
  } catch (error) {
    console.error('加载方案详情失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    isLoading.value = false
  }
})
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
}

.content-area {
  padding-bottom: calc(32rpx + env(safe-area-inset-bottom));
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 600rpx;
}

// 方案信息卡片
.info-card {
  background: $tf-surface;
  border-radius: 20rpx;
  padding: 28rpx 32rpx;
  margin: 24rpx;
  // box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.info-row {
  display: flex;
  align-items: center;
  gap: 32rpx;
  margin-bottom: 16rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6rpx;
  flex: 1;
}

.info-icon {
  font-size: 24rpx;
  color: $tf-brand;
}

.info-label {
  font-size: 24rpx;
  color: $tf-gray-500;
}

.info-value {
  font-size: 24rpx;
  color: $tf-gray-900;
  font-weight: 500;
}

// 折叠面板
.section-panel {
  background: $tf-surface;
  border-radius: 20rpx;
  margin: 0 24rpx 20rpx;
  overflow: hidden;
  // box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  align-items: center;
  padding: 28rpx 32rpx;
  gap: 12rpx;

  &:active {
    opacity: 0.85;
  }
}

.section-icon {
  font-size: 32rpx;
  flex-shrink: 0;

  &.report-icon { color: #D97706; }
  &.food-icon { color: $tf-brand; }
  &.exercise-icon { color: #3B82F6; }
  &.acupoint-icon { color: #8B5CF6; }
  &.meridian-icon { color: #7C3AED; }
  &.lifestyle-icon { color: #2563EB; }
}

.section-title {
  flex: 1;
  font-size: 28rpx;
  color: $tf-gray-900;
  font-weight: 600;
}

.section-arrow {
  transition: transform 0.2s ease;
  flex-shrink: 0;

  &.expanded {
    transform: rotate(180deg);
  }
}

.arrow-icon {
  font-size: 24rpx;
  color: $tf-gray-400;
}

.section-body {
  padding: 0 32rpx 28rpx;
  overflow-x: hidden;

  :deep(.t-chat-markdown) {
    overflow-wrap: break-word;
    word-break: break-word;

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
}
</style>
