<template>
  <view class="page-container">

    <!-- 自定义 tabbar -->
    <TLTabBar :current="3" />

    <!-- 加载失败组件 -->
    <TLReload
      v-if="loadFailed"
      title="加载失败"
      description="无法获取养生方案信息，请稍后重试"
      @reload="loadData"
    />

    <!-- 正在加载 -->
    <view v-if="isLoading && !loadFailed" class="loading-state">
      <TLLoading />
    </view>

    <!-- 主内容区 -->
    <view v-if="!isLoading && !loadFailed" class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 方案生成进度（替代无方案提示区域） -->
      <view v-if="showGenerateModal" class="generate-section">
        <view class="generate-icon-wrapper i-solar:atom-outline" />
        <text class="generate-status-text">{{ generationStatusText }}</text>
        <view class="progress-bar">
          <view
            class="progress-bar-inner"
            :class="{ 'progress-error': generationStatus === 'error' }"
            :style="{ width: generationProgress + '%' }"
          >
            <text v-if="generationProgress > 8" class="progress-label">{{ generationProgress }}%</text>
          </view>
          <text v-if="generationProgress <= 8" class="progress-label-outside">{{ generationProgress }}%</text>
        </view>
      </view>

      <!-- 无方案提示 -->
      <view v-else-if="!planDetail" class="no-plan-section">
        <view class="no-plan-icon i-solar:cart-outline" />
        <text class="no-plan-title">暂无养生方案</text>
        <text class="no-plan-desc">根据您的体质测评结果，为您生成专属养生方案</text>
        <view class="no-plan-btn" @click="handleGeneratePlan">
          <text class="no-plan-btn-text">生成养生方案</text>
        </view>
      </view>

      <!-- 有方案时显示以下内容 -->
      <template v-else>

        <!-- 今日重点 -->
        <view class="today-focus-section">
          <text class="today-focus-label">今日重点：</text>
          <text class="today-focus-text">{{ getTodayFocus() }}</text>
        </view>

        <!-- 入口卡片区 1x3 -->
        <view class="entrance-grid">
          <view
            v-for="card in entranceCards"
            :key="card.key"
            class="entrance-card"
            @click="card.onClick"
          >
            <view :class="card.icon" class="entrance-icon" />
            <text class="entrance-title">{{ card.title }}</text>
            <text class="entrance-subtitle">{{ card.subtitle }}</text>
          </view>
        </view>

        <!-- 方案面板容器 -->
        <view class="plan-collapse-container">
          <!-- 顶部标题和操作按钮 -->
          <view class="plan-header">
            <view class="plan-title-area">
              <text class="plan-title">{{ planDetail.planTitle || '综合方案' }}</text>
            </view>
            <view class="plan-actions">
              <view class="plan-action-item" v-for="(action, index) in planActions" :key="index" @click="action.onClick">
                <view :class="action.icon" :style="{ color: action.iconColor }" />
              </view>
            </view>
          </view>

          <!-- 方案列表 -->
          <view class="plan-list">
            <view
              class="plan-item"
              v-for="item in planItems"
              :key="item.type"
              @click="navigateToPlanDetail(item)"
            >
              <view class="plan-item-left">
                <view :class="item.icon" />
                <view class="plan-item-content">
                  <text class="plan-item-title">{{ item.title }}</text>
                  <view class="plan-item-tags">
                    <view
                      v-if="item.tags.length > 0"
                      v-for="(tag, index) in item.tags"
                      :key="index"
                      class="tag-chip"
                      :style="{ background: getTagColor(tag) }"
                    >
                      <text class="tag-chip-text">{{ tag }}</text>
                    </view>
                    <text v-else class="plan-item-desc-text">{{ item.fallback }}</text>
                  </view>
                </view>
              </view>
              <view class="plan-item-arrow i-solar:alt-arrow-right-outline" />
            </view>
          </view>
        </view>

        <!-- AI调整入口 -->
        <view class="ai-adjust-section">
          <view class="ai-adjust-title">
            <view class="i-solar:chat-round-dots-outline ai-icon" />
            <text class="ai-title-text">想要调整方案？告诉AI你的需求</text>
          </view>
          <view class="ai-adjust-options">
            <view class="ai-option" v-for="(option, index) in aiOptions" :key="index" @click="handleAiOption(option)">
              <text class="ai-option-text">{{ option }}</text>
            </view>
          </view>
          <view class="ai-custom-input" @click="handleCustomAiInput">
            <text class="custom-input-placeholder">自定义输入...</text>
            <view class="i-solar:arrow-right-outline custom-input-icon" />
          </view>
        </view>

        <!-- 批量调整抽屉 -->
        <BatchAdjustDrawer
          v-model:visible="showBatchDrawer"
          :user-plan-id="planDetail?.id || ''"
          :initial-text="batchInitialText"
          @confirmed="onBatchConfirmed"
        />

      </template>

    </view>

    <!-- 周期选择弹窗 -->
    <view v-if="showCyclePickerModal" class="cycle-picker-mask" @click="closeCyclePicker">
      <view class="cycle-picker-container" @click.stop>
        <text class="cycle-picker-title">选择方案周期</text>
        <view class="cycle-picker-options">
          <view
            v-for="opt in cycleOptions"
            :key="opt.value"
            class="cycle-option"
            :class="{ 'cycle-option-active': selectedCycleDays === opt.value }"
            @click="selectedCycleDays = opt.value"
          >
            <text class="cycle-option-label" :class="{ 'cycle-option-label-active': selectedCycleDays === opt.value }">{{ opt.label }}</text>
            <text class="cycle-option-desc">{{ opt.desc }}</text>
          </view>
        </view>
        <view class="cycle-picker-btn" @click="confirmCycle">
          <text class="cycle-picker-btn-text">确认生成</text>
        </view>
      </view>
    </view>

  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import TLTabBar from '@/components/TLTabBar/index.vue'
import TLReload from '@/components/TLReload/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { usePageLayout } from '@/composables/usePageLayout'
import { parseTagsJson } from '@/utils/format'
import { enAuth } from '@/utils/authManager'
import { getHealthPlan, generatePlan, getGenerationStatus } from '@/api/plan/plan'
import type { PlanSummary } from '@/types/biz/plan/plan'
import BatchAdjustDrawer from './BatchAdjustDrawer.vue'

// 页面布局
const { contentPaddingTop } = usePageLayout()

// 加载状态
const isLoading = ref(true)

// 加载失败状态
const loadFailed = ref(false)


// 方案详情（null表示无活跃方案）
const planDetail = ref<PlanSummary | null>(null)

// 方案生成状态
const showGenerateModal = ref(false)
const generationProgress = ref(0)
const generationStatusText = ref('正在准备生成...')
const generationStatus = ref<'active' | 'success' | 'error' | 'warning'>('active')
const taskId = ref('')

// 周期选择弹窗
const showCyclePickerModal = ref(false)
const selectedCycleDays = ref(7)
const cycleOptions = [
  { label: '7天', desc: '推荐初次体验', value: 7 },
  { label: '14天', desc: '深度调理', value: 14 },
  { label: '21天', desc: '完整周期', value: 21 },
]

// 完成率 - 使用 taskSummary 计算
const completionRate = computed(() => {
  if (!planDetail.value?.taskSummary) return 0
  const { todayTaskCount, completedCount } = planDetail.value.taskSummary
  if (todayTaskCount <= 0) return 0
  return Math.round((completedCount / todayTaskCount) * 100)
})

// 入口卡片数据
const entranceCards = computed(() => [
  {
    key: 'square',
    icon: 'i-solar:global-outline',
    title: '方案广场',
    subtitle: '发现好方案',
    onClick: navigateToSquare
  },
  {
    key: 'tasks',
    icon: 'i-solar:checklist-minimalistic-outline',
    title: '每日任务',
    subtitle: `${completionRate.value}%`,
    onClick: navigateToTasks
  },
  {
    key: 'constitution',
    icon: 'i-solar:user-outline',
    title: planDetail.value?.constitutionName || '体质',
    subtitle: planDetail.value?.constitutionCode || '',
    onClick: () => {}
  }
])

// AI调整选项
const aiOptions = ref([
  '最近没时间运动',
  '睡眠质量不好',
  '想吃清淡点',
  '最近胃不舒服',
  '想减肥',
  '压力大'
])

// 获取今日重点
const getTodayFocus = () => {
  if (planDetail.value?.todayFocus) {
    return planDetail.value.todayFocus
  }
  return '均衡调理'
}

// 方案列表数据
const planItems = computed(() => [
  {
    type: 'food',
    icon: 'i-solar:chef-hat-outline',
    title: '饮食方案',
    tags: parseTagsJson(planDetail.value?.foodPlanTags),
    fallback: '查看饮食方案',
    url: `/pages/plan/details/FoodPlanDetail?id=${planDetail.value?.id || ''}`
  },
  {
    type: 'exercise',
    icon: 'i-solar:running-round-outline',
    title: '运动方案',
    tags: parseTagsJson(planDetail.value?.exercisePlanTags),
    fallback: '查看运动方案',
    url: `/pages/plan/details/ExercisePlanDetail?id=${planDetail.value?.id || ''}`
  },
  {
    type: 'acupoint',
    icon: 'i-solar:health-outline',
    title: '穴位按摩',
    tags: parseTagsJson(planDetail.value?.acupointPlanTags),
    fallback: '查看穴位方案',
    url: `/pages/plan/details/AcupointPlanDetail?id=${planDetail.value?.id || ''}`
  },
  {
    type: 'meridian',
    icon: 'i-solar:body-outline',
    title: '经络调理',
    tags: parseTagsJson(planDetail.value?.meridianPlanTags),
    fallback: '查看经络方案',
    url: `/pages/plan/details/MeridianPlanDetail?id=${planDetail.value?.id || ''}`
  },
  {
    type: 'lifestyle',
    icon: 'i-solar:moon-outline',
    title: '睡眠建议',
    tags: parseTagsJson(planDetail.value?.lifestylePlanTags),
    fallback: '查看生活建议',
    url: `/pages/plan/details/LifestylePlanDetail?id=${planDetail.value?.id || ''}`
  }
])

// 导航到方案详情页
const navigateToPlanDetail = (item: { url: string }) => {
  uni.navigateTo({ url: item.url })
}

// 批量调整抽屉状态
const showBatchDrawer = ref(false)
const batchInitialText = ref('')

// 处理AI选项点击 - 打开批量调整抽屉并预填文本
const handleAiOption = (option: string) => {
  batchInitialText.value = option
  showBatchDrawer.value = true
}

// 处理自定义AI输入 - 打开批量调整抽屉
const handleCustomAiInput = () => {
  batchInitialText.value = ''
  showBatchDrawer.value = true
}

// 批量调整确认后刷新数据
const onBatchConfirmed = () => {
  loadData()
}

// 导航到方案历史
const navigateToHistory = () => {
  uni.navigateTo({
    url: '/pages/plan/PlanHistory'
  })
}

// 导航到每日任务
const navigateToTasks = () => {
  if (!planDetail.value) return
  const { taskSummary } = planDetail.value
  uni.navigateTo({
    url: `/pages/plan/Tasks?userPlanId=${planDetail.value.id}&completedCount=${taskSummary?.completedCount || 0}&totalTasks=${taskSummary?.todayTaskCount || 0}`
  })
}

// 导航到方案广场
const navigateToSquare = () => {
  uni.navigateTo({
    url: '/pages/plan/square/index'
  })
}

// 处理分享到广场
const handleShare = () => {
  if (!planDetail.value) return
  const planTitle = encodeURIComponent(planDetail.value.planTitle || '我的养生方案')
  const planTags = encodeURIComponent(planDetail.value.planTags || '[]')
  uni.navigateTo({
    url: `/pages/plan/square/SquareShare?planId=${planDetail.value.id}&planTitle=${planTitle}&planTags=${planTags}`
  })
}

// 生成养生方案
const handleGeneratePlan = async () => {
  // 如果已有进行中的方案，弹出确认框
  if (planDetail.value && planDetail.value.status === 1) {
    uni.showModal({
      title: '重新生成方案',
      content: '当前方案仍有未完成的任务，确定要重新生成吗？',
      confirmText: '重新生成',
      confirmColor: '#F59E0B',
      success: (res) => {
        if (res.confirm) {
          openCyclePicker()
        }
      }
    })
    return
  }

  openCyclePicker()
}

// 显示周期选择弹窗
const openCyclePicker = () => {
  selectedCycleDays.value = 7
  showCyclePickerModal.value = true
}

// 关闭周期选择弹窗
const closeCyclePicker = () => {
  showCyclePickerModal.value = false
}

// 确认选择周期
const confirmCycle = () => {
  showCyclePickerModal.value = false
  doGeneratePlan(selectedCycleDays.value)
}

// 执行方案生成
const doGeneratePlan = async (cycleDays: number) => {
  try {
    // 显示进度弹窗
    showGenerateModal.value = true
    generationProgress.value = 0
    generationStatus.value = 'active'
    generationStatusText.value = '正在准备生成...'

    // 提交生成任务
    const result = await generatePlan(cycleDays)

    if (!result?.taskId) {
      generationStatusText.value = '生成任务提交失败'
      setTimeout(() => { showGenerateModal.value = false }, 1500)
      return
    }

    taskId.value = result.taskId
    generationStatusText.value = '方案生成中...'

    // 开始轮询状态
    startPolling()
  } catch (error: any) {
    console.error('生成方案失败:', error)
    // 如果是未测评错误，引导用户去测评
    if (error?.code === 'D13002' || error?.message?.includes('暂无测评记录')) {
      generationStatusText.value = '请先完成体质测评'
      setTimeout(() => {
        showGenerateModal.value = false
        uni.navigateTo({ url: '/pages/wisdom/constitution/index' })
      }, 1500)
    } else {
      generationStatusText.value = '生成失败，请重试'
      setTimeout(() => { showGenerateModal.value = false }, 1500)
    }
  }
}

// 轮询定时器
let pollTimer: ReturnType<typeof setInterval> | null = null
let pollCount = 0
const MAX_POLL_COUNT = 20 // 5分钟超时（20次 × 15秒）

// 开始轮询生成状态
const startPolling = () => {
  pollCount = 0
  pollTimer = setInterval(async () => {
    pollCount++
    if (pollCount > MAX_POLL_COUNT) {
      stopPolling()
      generationStatus.value = 'error'
      generationStatusText.value = '方案生成超时，请稍后重试'
      setTimeout(() => { showGenerateModal.value = false }, 2000)
      return
    }
    try {
      const status = await getGenerationStatus(taskId.value)
      generationProgress.value = status.progress || 0
      generationStatusText.value = status.message || getStatusText(status.progress || 0)

      // 完成
      if (status.status === 3) {
        stopPolling()
        generationStatus.value = 'success'
        generationStatusText.value = status.message || '生成完成！'
        setTimeout(async () => {
          showGenerateModal.value = false
          // 刷新方案数据
          await loadData()
        }, 1000)
      }
      // 失败
      else if (status.status === 4) {
        stopPolling()
        generationStatus.value = 'error'
        generationStatusText.value = status.errorMessage || '方案生成失败，请稍后重试'
        setTimeout(() => { showGenerateModal.value = false }, 2000)
      }
    } catch (error) {
      console.error('查询生成状态失败:', error)
    }
  }, 15000)
}

// 停止轮询
const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

// 根据进度获取状态文字
const getStatusText = (progress: number): string => {
  if (progress < 10) return '正在分析体质...'
  if (progress < 40) return '正在生成饮食方案...'
  if (progress < 50) return '正在生成运动方案...'
  if (progress < 60) return '正在生成穴位方案...'
  if (progress < 70) return '正在生成经络方案...'
  if (progress < 80) return '正在生成生活方案...'
  if (progress < 90) return '正在生成方案标题...'
  if (progress < 100) return '正在整理方案...'
  return '生成完成！'
}

// 方案操作按钮
const planActions = ref([
  {
    icon: 'i-solar:refresh-outline',
    text: '重新生成',
    iconColor: '#F59E0B',
    onClick: handleGeneratePlan
  },
  {
    icon: 'i-solar:clock-circle-outline',
    text: '方案历史',
    iconColor: '#3B82F6',
    onClick: navigateToHistory
  },
  {
    icon: 'i-solar:share-outline',
    text: '分享方案',
    iconColor: '#10B981',
    onClick: handleShare
  }
])

// 标签颜色池（柔和和谐）
const tagColors = [
  '#E8F5E9', // 淡绿
  '#FFF8E1', // 淡黄
  '#E3F2FD', // 淡蓝
  '#FCE4EC', // 淡粉
  '#F3E5F5', // 淡紫
  '#E0F2F1', // 淡青
  '#FFF3E0', // 淡橙
  '#F1F8E9', // 淡橄榄
]

// 根据标签内容获取固定颜色（同类标签颜色一致）
const getTagColor = (tag: string): string => {
  let hash = 0
  for (let i = 0; i < tag.length; i++) {
    hash = tag.charCodeAt(i) + ((hash << 5) - hash)
  }
  return tagColors[Math.abs(hash) % tagColors.length]
}

// 下拉刷新
onPullDownRefresh(() => {
  uni.stopPullDownRefresh()
})

// 加载数据
const loadData = async () => {
  isLoading.value = true
  loadFailed.value = false
  try {
    // 获取养生方案
    const plan = await getHealthPlan()
    if (plan) {
      planDetail.value = plan
    } else {
      planDetail.value = null
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    loadFailed.value = true
  } finally {
    isLoading.value = false
  }
}

// 页面加载
onMounted(async () => {
  await enAuth()
  await loadData()
})

// 页面卸载时停止轮询
onUnmounted(() => {
  stopPolling()
})
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
  padding-bottom: 140rpx;
  overflow-y: auto;
}

.content-area {
  padding-bottom: 32rpx;
}

// 入口卡片区 1x3
.entrance-grid {
  display: flex;
  gap: 16rpx;
  margin: 0 24rpx 16rpx 24rpx;
  background: $tf-surface;
  border-radius: $tf-radius-xl;
}

.entrance-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20rpx 8rpx;
  border-radius: $tf-radius-lg;
  gap: 8rpx;
}

.entrance-icon {
  font-size: 40rpx;
  color: $tf-primary-color;
}

.entrance-title {
  font-size: 24rpx;
  color: $tf-gray-900;
  font-weight: 600;
}

.entrance-subtitle {
  font-size: $tf-text-xs;
  color: $tf-brand;
}

// 今日重点
.today-focus-section {
  display: flex;
  align-items: center;
  background: $tf-brand-bg-light;
  padding: 16rpx 24rpx;
  margin: 0 24rpx 16rpx 24rpx;
  border-radius: 16rpx;
}

.today-focus-label {
  font-size: 24rpx;
  color: $tf-brand;
  font-weight: 600;
  margin-right: 8rpx;
}

.today-focus-text {
  font-size: $tf-text-base;
  color: $tf-gray-900;
  font-weight: 500;
}

// 方案折叠面板容器
.plan-collapse-container {
  margin: 0 24rpx 24rpx 24rpx;
  background: $tf-surface;
  border-radius: $tf-radius-2xl;
  overflow: hidden;
}

// 方案顶部标题和操作
.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: $tf-border-light;
}

.plan-title {
  font-size: 32rpx;
  color: $tf-primary-color;
  font-weight: bold;
}

.plan-title-area {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  flex: 1;
  min-width: 0;
}

.plan-header-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
}

.plan-actions {
  display: flex;
  gap: 24rpx;
}

.plan-action-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  height: 64rpx;
  cursor: pointer;

  > view {
    font-size: 40rpx;
  }
}

// 方案列表
.plan-list {
  background: $tf-surface;
  border-radius: 0 0 $tf-radius-2xl $tf-radius-2xl;
  overflow: hidden;
}

.plan-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: $tf-border-normal;
  cursor: pointer;

  &:last-child {
    border-bottom: none;
  }
}

.plan-item-left {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.plan-item-icon {
  width: 48rpx;
  height: 48rpx;
  font-size: 48rpx;
  color: $tf-brand;
}

.plan-item-content {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.plan-item-title {
  font-size: 28rpx;
  color: $tf-gray-800;
  font-weight: 600;
}

.plan-item-description {
  font-size: 22rpx;
  color: $tf-gray-500;
}

.plan-item-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  margin-top: 8rpx;
}

.tag-chip {
  padding: 6rpx 16rpx;
  border-radius: 999rpx;
  display: inline-flex;
  align-items: center;
}

.tag-chip-text {
  font-size: 20rpx;
  color: $tf-gray-700;
  line-height: 1;
}

.plan-item-desc-text {
  font-size: 22rpx;
  color: $tf-gray-500;
}

.plan-item-arrow {
  width: 44rpx;
  height: 44rpx;
  color: $tf-gray-400;
}

// AI调整区域
.ai-adjust-section {
  background: $tf-surface;
  border-radius: 20rpx;
  padding: 32rpx;
  margin: 0 24rpx 24rpx 24rpx;
}

.ai-adjust-title {
  margin-bottom: 20rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.ai-icon {
  font-size: 28rpx;
  color: $tf-primary-color;
}

.ai-title-text {
  font-size: 26rpx;
  color: $tf-gray-900;
  font-weight: bold;
}

.ai-adjust-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.ai-option {
  padding: 12rpx 20rpx;
  background: #F0FDF4;
  color: $tf-brand;
  border-radius: 24rpx;
  font-size: 22rpx;
}

.ai-option-text {
  color: $tf-brand;
}

.ai-custom-input {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  background: #F9FAFB;
  border-radius: 12rpx;
  border: 1rpx solid #E5E7EB;
}

.custom-input-placeholder {
  font-size: 24rpx;
  color: $tf-gray-500;
}

.custom-input-icon {
  font-size: 28rpx;
  color: $tf-brand;
}

// 方案生成进度区域
.generate-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 64rpx;
  margin: 24rpx;
  background: $tf-surface;
  border-radius: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(45, 90, 39, 0.08);
}

.generate-icon-wrapper {
  font-size: 80rpx;
  color: $tf-primary-color;
  margin-bottom: 32rpx;
}

.generate-status-text {
  font-size: 28rpx;
  color: $tf-gray-900;
  margin-bottom: 32rpx;
  text-align: center;
}

// 手搓进度条
.progress-bar {
  width: 100%;
  height: 40rpx;
  background: #E8F5E9;
  border-radius: 40rpx;
  overflow: hidden;
  position: relative;
}

.progress-bar-inner {
  height: 100%;
  background: $tf-gradient-progress;
  border-radius: 40rpx;
  transition: width 0.5s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 0;
  position: relative;
  overflow: hidden;
}

.progress-bar-inner::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 200%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.15) 25%,
    rgba(255, 255, 255, 0.3) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 100%
  );
  animation: progressWave 1.5s ease-in-out infinite;
}

@keyframes progressWave {
  0% { transform: translateX(0); }
  100% { transform: translateX(50%); }
}

.progress-bar-inner.progress-error {
  background: linear-gradient(90deg, #D32F2F, #EF5350);
}

.progress-label {
  font-size: 20rpx;
  color: $tf-surface;
  font-weight: 600;
  white-space: nowrap;
}

.progress-label-outside {
  font-size: 20rpx;
  color: $tf-primary-color;
  font-weight: 600;
  position: absolute;
  left: 16rpx;
  top: 50%;
  transform: translateY(-50%);
}

// 加载状态
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 600rpx;
}

// 无方案提示
.no-plan-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 64rpx;
  margin: 0 24rpx;
  background: $tf-surface;
  border-radius: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(45, 90, 39, 0.08);
}

.no-plan-icon {
  font-size: 96rpx;
  color: $tf-primary-color;
  margin-bottom: 24rpx;
}

.no-plan-title {
  font-size: 32rpx;
  color: $tf-gray-900;
  font-weight: bold;
  margin-bottom: 12rpx;
}

.no-plan-desc {
  font-size: 24rpx;
  color: $tf-gray-500;
  text-align: center;
  margin-bottom: 40rpx;
}

.no-plan-btn {
  padding: 20rpx 64rpx;
  background: $tf-gradient-brand;
  border-radius: 40rpx;
}

.no-plan-btn-text {
  font-size: 28rpx;
  color: $tf-surface;
  font-weight: 600;
}

// 周期选择弹窗
.cycle-picker-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cycle-picker-container {
  width: 600rpx;
  background: $tf-surface;
  border-radius: 24rpx;
  padding: 48rpx 40rpx 40rpx;
}

.cycle-picker-title {
  font-size: 32rpx;
  color: $tf-gray-900;
  font-weight: bold;
  text-align: center;
  display: block;
  margin-bottom: 56rpx;
}

.cycle-picker-options {
  display: flex;
  gap: 16rpx;
  margin-bottom: 40rpx;
}

.cycle-option {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 12rpx;
  border-radius: 16rpx;
  border: 2rpx solid #E5E7EB;
  background: #F9FAFB;
  gap: 8rpx;
}

.cycle-option-active {
  border-color: $tf-primary-color;
  background: #F0FDF4;
}

.cycle-option-label {
  font-size: 28rpx;
  color: $tf-gray-800;
  font-weight: 600;
}

.cycle-option-label-active {
  color: $tf-primary-color;
}

.cycle-option-desc {
  font-size: 20rpx;
  color: $tf-gray-500;
}

.cycle-picker-btn {
  display: flex;
  justify-content: center;
  padding: 20rpx 0;
  background: $tf-gradient-brand;
  border-radius: 40rpx;
}

.cycle-picker-btn-text {
  font-size: 28rpx;
  color: $tf-surface;
  font-weight: 600;
}

</style>
