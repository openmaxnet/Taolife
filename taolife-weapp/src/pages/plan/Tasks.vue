<template>
  <view class="page-container">

    <!-- 加载失败组件 -->
    <TLReload
      v-if="loadFailed"
      title="加载失败"
      description="无法获取任务列表，请稍后重试"
      @reload="loadData"
    />

    <!-- 正在加载 -->
    <view v-if="isLoading && !loadFailed" class="loading-state">
      <TLLoading />
    </view>

    <!-- 主内容区 -->
    <view v-if="!isLoading && !loadFailed" class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 未读周期报告通知 -->
      <view v-if="unreadReport" class="report-notification" @click="viewCycleReport">
        <view class="i-solar:document-text-outline report-notification-icon" />
        <text class="report-notification-text">你的上一周期调理报告已生成，点击查看</text>
        <view class="i-solar:arrow-right-outline report-notification-arrow" />
      </view>

      <!-- 今日完成统计 -->
      <view class="header-stats">
        <view class="stats-info">
          <text class="stats-label">今日完成度</text>
          <text class="stats-numbers">{{ completedCount }} / {{ totalTasks }}</text>
        </view>
        <view class="progress-bar-wrapper">
          <view class="progress-bar-bg">
            <view class="progress-bar-fill" :style="{ width: progressPercent + '%' }" />
          </view>
          <text class="progress-percent">{{ progressPercent }}%</text>
        </view>
      </view>

      <!-- 筛选标签 -->
      <view class="filter-tabs">
        <view
          v-for="tab in filterTabs"
          :key="tab.value"
          class="filter-tab"
          :class="{ active: activeFilter === tab.value }"
          @click="switchFilter(tab.value)"
        >
          <text>{{ tab.label }}</text>
        </view>
      </view>

      <!-- 空状态 -->
      <TLEmpty
        v-if="!loadingMore && taskList.length === 0"
        title="暂无任务"
        description="当前筛选条件下没有任务"
      />

      <!-- 任务列表 -->
      <view v-if="taskList.length > 0" class="task-list">
        <view
          v-for="item in taskList"
          :key="item.id"
          class="task-card"
          :class="{ clickable: item.resourceType }"
          @click="handleTaskClick(item)"
        >
          <!-- 任务内容 -->
          <view class="task-content">
            <view class="task-header">
              <text class="task-name">{{ item.taskName }}</text>
              <view class="task-header-right">
                <view class="plan-type-badge" :style="{ background: getPlanTypeColor(item.planType).bg, color: getPlanTypeColor(item.planType).text }">
                  <text>{{ getPlanTypeLabel(item.planType) }}</text>
                </view>
                <view v-if="item.resourceType" class="task-arrow i-solar:alt-arrow-right-outline" />
              </view>
            </view>
            <text class="task-desc">{{ item.taskDescription }}</text>
          </view>

          <!-- 任务状态与操作 -->
          <view class="task-footer">
            <!-- 状态显示 -->
            <view v-if="item.status === 2" class="task-status completed">
              <view class="i-solar:check-circle-bold task-status-icon" />
              <text class="task-status-text">已完成</text>
            </view>
            <view v-else-if="item.status === 3" class="task-status skipped">
              <text class="task-status-text">已跳过</text>
            </view>
            <view v-else class="task-status pending">
              <text class="task-status-text">待完成</text>
            </view>

            <!-- 操作按钮 -->
            <view v-if="item.status === 1" class="task-actions" @click.stop>
              <view class="action-btn skip-btn" @click="handleSkip(item)">
                <text>跳过</text>
              </view>
              <view class="action-btn complete-btn" @click="handleComplete(item)">
                <text>完成</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多提示 -->
      <view v-if="loadingMore" class="loading-tip">
        <text>加载中...</text>
      </view>
      <view v-else-if="noMore && taskList.length > 0" class="loading-tip">
        <text>没有更多了</text>
      </view>
    </view>

  </view>

  <!-- 顶部导航栏 -->
  <TLTopBar title="每日任务" :show-back="true" />

  <!-- 周期报告弹窗 -->
  <view v-if="showReportModal" class="report-modal-mask" @click="closeReport">
    <view class="report-modal" @click.stop>
      <view class="report-modal-header">
        <text class="report-modal-title">周期调理报告</text>
      </view>
      <scroll-view scroll-y class="report-modal-content">
        <chatMarkdown v-if="unreadReport" :content="unreadReport.reportContent" />
      </scroll-view>
      <view class="report-modal-footer">
        <view class="report-modal-btn" @click="closeReport">
          <text>我知道了</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLReload from '@/components/TLReload/index.vue'
import TLEmpty from '@/components/TLEmpty/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import chatMarkdown from '@tdesign/uniapp-chat/chat-markdown/chat-markdown.vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { getPageOptions } from '@/utils/router'
import { enAuth } from '@/utils/authManager'
import { getTaskPage, completeTask, skipTask } from '@/api/plan/task'
import { getUnreadCycleReport, markCycleReportRead } from '@/api/plan/plan'
import type { PlanTaskItem, PlanTaskQueryParam } from '@/types/biz/plan/plan'


// 页面布局
const { contentPaddingTop } = usePageLayout()

// URL 参数
const userPlanId = ref('')
const completedCount = ref(0)
const totalTasks = ref(0)

// 加载状态
const isLoading = ref(true)
const loadFailed = ref(false)
const loadingMore = ref(false)
const noMore = ref(false)

// 任务列表
const taskList = ref<PlanTaskItem[]>([])

// 分页参数
const pageNo = ref(1)
const pageSize = 10

// 当前筛选状态：0=全部, 1=待完成, 2=已完成, 3=已跳过
const activeFilter = ref(0)

// 筛选标签
const filterTabs = ref([
  { label: '全部', value: 0 },
  { label: '待完成', value: 1 },
  { label: '已完成', value: 2 },
  { label: '已跳过', value: 3 }
])

// 未读周期报告
const unreadReport = ref<{ id: string; reportContent: string } | null>(null)
const showReportModal = ref(false)

/** 检查未读周期报告 */
const checkUnreadReport = async () => {
  try {
    const report = await getUnreadCycleReport()
    if (report) {
      unreadReport.value = report
    }
  } catch {
    // 静默失败
  }
}

/** 查看周期报告 */
const viewCycleReport = () => {
  showReportModal.value = true
}

/** 关闭报告并标记已读 */
const closeReport = async () => {
  showReportModal.value = false
  if (unreadReport.value?.id) {
    try {
      await markCycleReportRead(unreadReport.value.id)
      unreadReport.value = null
    } catch {
      // 静默失败
    }
  }
}

// 完成进度百分比
const progressPercent = computed(() => {
  if (totalTasks.value === 0) return 0
  return Math.round((completedCount.value / totalTasks.value) * 100)
})

/**
 * 获取方案类型标签
 */
const getPlanTypeLabel = (planType: number): string => {
  const labelMap: Record<number, string> = {
    1: '饮食',
    2: '运动',
    3: '穴位',
    4: '经络',
    5: '生活'
  }
  return labelMap[planType] || '其他'
}

/**
 * 获取方案类型颜色
 */
const getPlanTypeColor = (planType: number): { bg: string; text: string } => {
  const colorMap: Record<number, { bg: string; text: string }> = {
    1: { bg: '#ECFDF5', text: '#059669' },
    2: { bg: '#DBEAFE', text: '#3B82F6' },
    3: { bg: '#EDE9FE', text: '#8B5CF6' },
    4: { bg: '#FEF3C7', text: '#F59E0B' },
    5: { bg: '#FCE7F3', text: '#EC4899' }
  }
  return colorMap[planType] || { bg: '#F3F4F6', text: '#6B7280' }
}

/**
 * 点击任务卡片跳转到对应方案详情
 */
const handleTaskClick = (item: PlanTaskItem) => {
  if (!item.resourceType || !item.resourceId) return

  // 阻止事件冒泡到操作按钮
  const routeMap: Record<string, string> = {
    food: '/pages/plan/details/FoodPlanDetail',
    exercise: '/pages/plan/details/ExercisePlanDetail',
    acupoint: '/pages/plan/details/AcupointPlanDetail',
    meridian: '/pages/plan/details/MeridianPlanDetail',
    lifestyle: '/pages/plan/details/LifestylePlanDetail'
  }

  const route = routeMap[item.resourceType]
  if (route) {
    uni.navigateTo({ url: `${route}?id=${item.resourceId}` })
  }
}

/**
 * 切换筛选标签
 */
const switchFilter = (value: number) => {
  activeFilter.value = value
  fetchTaskList(true)
}

/**
 * 获取任务列表
 */
const fetchTaskList = async (reset = false) => {
  if (loadingMore.value) return

  if (reset) {
    isLoading.value = true
    loadFailed.value = false
  } else {
    loadingMore.value = true
  }

  try {
    if (reset) {
      pageNo.value = 1
      noMore.value = false
      taskList.value = []
    }

    const params: PlanTaskQueryParam = {
      pageNo: pageNo.value,
      pageSize,
      status: activeFilter.value === 0 ? undefined : activeFilter.value
    }

    const result = await getTaskPage(userPlanId.value, params)

    if (reset) {
      taskList.value = result.list || []
    } else {
      taskList.value = [...taskList.value, ...(result.list || [])]
    }

    // 判断是否还有更多
    if (taskList.value.length >= (result.total || 0)) {
      noMore.value = true
    }
  } catch (error) {
    console.error('获取任务列表失败:', error)
    if (reset) {
      loadFailed.value = true
    }
  } finally {
    isLoading.value = false
    loadingMore.value = false
  }
}

/**
 * 加载数据（供 ReloadPage 调用）
 */
const loadData = () => {
  fetchTaskList(true)
}

/**
 * 完成任务
 */
const handleComplete = async (item: PlanTaskItem) => {
  try {
    await completeTask({ taskId: item.id })
    item.status = 2
    // 更新完成数
    completedCount.value++
    uni.showToast({
      title: '任务完成',
      icon: 'success'
    })
  } catch (error) {
    console.error('完成任务失败:', error)
    uni.showToast({
      title: '操作失败',
      icon: 'none'
    })
  }
}

/**
 * 跳过任务
 */
const handleSkip = async (item: PlanTaskItem) => {
  try {
    await skipTask(item.id)
    item.status = 3
    uni.showToast({
      title: '已跳过',
      icon: 'none'
    })
  } catch (error) {
    console.error('跳过任务失败:', error)
    uni.showToast({
      title: '操作失败',
      icon: 'none'
    })
  }
}

/**
 * 下拉刷新
 */
onPullDownRefresh(async () => {
  await fetchTaskList(true)
  uni.stopPullDownRefresh()
})

/**
 * 触底加载更多
 */
onReachBottom(() => {
  if (!noMore.value && !loadingMore.value) {
    pageNo.value++
    fetchTaskList()
  }
})

// 页面加载
onMounted(async () => {
  // 获取路由参数
  const options = getPageOptions()
  userPlanId.value = options.userPlanId || ''
  completedCount.value = Number(options.completedCount) || 0
  totalTasks.value = Number(options.totalTasks) || 0

  await enAuth()
  fetchTaskList(true)
  checkUnreadReport()
})
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
  padding-bottom: 160rpx;
}

.content-area {
  padding-bottom: 32rpx;
}

// 加载状态
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 600rpx;
}

// 今日完成统计
.header-stats {
  background: $tf-gradient-brand;
  border-radius: 24rpx;
  padding: 32rpx;
  margin: 24rpx;
}

.stats-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.stats-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}

.stats-numbers {
  font-size: 32rpx;
  color: $tf-surface;
  font-weight: bold;
}

.progress-bar-wrapper {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.progress-bar-bg {
  flex: 1;
  height: 12rpx;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  background: $tf-surface;
  border-radius: 6rpx;
  transition: width 0.3s ease;
}

.progress-percent {
  font-size: 24rpx;
  color: $tf-surface;
  font-weight: bold;
  min-width: 80rpx;
  text-align: right;
}

// 筛选标签
.filter-tabs {
  display: flex;
  align-items: center;
  gap: 0;
  margin: 0 24rpx 24rpx 24rpx;
  background: $tf-surface;
  border-radius: 16rpx;
  padding: 8rpx;
  // box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.filter-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20rpx 0;
  border-radius: 12rpx;
  transition: all 0.2s ease;
}

.filter-tab text {
  font-size: 26rpx;
  color: $tf-gray-600;
}

.filter-tab.active {
  background: #F0F5EF;
}

.filter-tab.active text {
  color: $tf-primary-color;
  font-weight: 600;
}

// 任务列表
.task-list {
  padding: 0 24rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

// 任务卡片
.task-card {
  background: $tf-surface;
  border-radius: 20rpx;
  padding: 28rpx;
  // box-shadow: 0 2rpx 12rpx rgba(45, 90, 39, 0.06);
  display: flex;
  flex-direction: column;
  gap: 20rpx;

  &.clickable {
    cursor: pointer;

    &:active {
      opacity: 0.85;
    }
  }
}

.task-content {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16rpx;
}

.task-header-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
  flex-shrink: 0;
}

.task-arrow {
  font-size: 24rpx;
  color: $tf-gray-400;
}

.task-name {
  font-size: 28rpx;
  color: $tf-gray-900;
  font-weight: 600;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.plan-type-badge {
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  flex-shrink: 0;
}

.plan-type-badge text {
  font-size: 22rpx;
}

.task-desc {
  font-size: 24rpx;
  color: $tf-gray-500;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.5;
}

// 任务底部
.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16rpx;
  border-top: 1rpx solid $tf-gray-200;
}

.task-status {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.task-status-text {
  font-size: 22rpx;
}

.task-status.completed .task-status-icon {
  color: $tf-brand-light;
  font-size: 28rpx;
}

.task-status.completed .task-status-text {
  color: $tf-brand-light;
}

.task-status.skipped .task-status-text {
  color: $tf-gray-500;
}

.task-status.pending .task-status-text {
  color: $tf-warning;
}

// 操作按钮
.task-actions {
  display: flex;
  gap: 16rpx;
}

.action-btn {
  padding: 8rpx 28rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn text {
  font-size: 24rpx;
}

.skip-btn {
  background: $tf-gray-200;
}

.skip-btn text {
  color: $tf-gray-600;
}

.complete-btn {
  background: $tf-primary-color;
}

.complete-btn text {
  color: $tf-surface;
}

// 加载提示
.loading-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32rpx;
}

.loading-tip text {
  font-size: 24rpx;
  color: $tf-gray-500;
}

// 周期报告通知
.report-notification {
  display: flex;
  align-items: center;
  margin: 24rpx 24rpx 24rpx 24rpx;
  padding: 20rpx 24rpx;
  background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
  border-radius: 16rpx;
  gap: 12rpx;

  &:active {
    opacity: 0.85;
  }
}

.report-notification-icon {
  font-size: 32rpx;
  color: #D97706;
  flex-shrink: 0;
}

.report-notification-text {
  flex: 1;
  font-size: 24rpx;
  color: #92400E;
}

.report-notification-arrow {
  font-size: 24rpx;
  color: #D97706;
  flex-shrink: 0;
}

// 周期报告弹窗
.report-modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1001;
}

.report-modal {
  width: 85%;
  max-height: 70vh;
  background: $tf-surface;
  border-radius: 24rpx;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.report-modal-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 28rpx 32rpx;
  border-bottom: $tf-border-light;
}

.report-modal-title {
  font-size: 30rpx;
  color: $tf-gray-900;
  font-weight: 600;
}

.report-modal-content {
  flex: 1;
  padding: 28rpx 32rpx;
  max-height: 50vh;
  font-size: 26rpx;
  color: $tf-gray-800;
  line-height: 1.8;
  overflow-x: hidden;
  box-sizing: border-box;

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

    p { font-size: 26rpx; line-height: 1.8; color: $tf-gray-700; }
    ul, ol { font-size: 26rpx; color: $tf-gray-700; padding-left: 32rpx; }
    li { margin-bottom: 8rpx; }
    strong { color: $tf-gray-900; }
    em { color: $tf-gray-600; }
  }
}

.report-modal-footer {
  padding: 20rpx 32rpx 28rpx;
  border-top: 1rpx solid $tf-gray-200;
}

.report-modal-btn {
  background: $tf-primary-color;
  border-radius: 16rpx;
  padding: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &:active {
    opacity: 0.85;
  }
}

.report-modal-btn text {
  font-size: 28rpx;
  color: $tf-surface;
  font-weight: 600;
}
</style>
