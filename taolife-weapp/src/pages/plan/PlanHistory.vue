<template>
  <view class="page-container">

    <!-- 顶部导航栏 -->
    <TLTopBar title="方案历史" :show-back="true" />

    <!-- 加载状态 -->
    <view v-if="isLoading" class="loading-state" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <TLLoading />
    </view>

    <!-- 加载失败 -->
    <TLReload
      v-if="loadFailed"
      title="加载失败"
      description="无法获取方案历史，请稍后重试"
      @reload="loadHistory(true)"
    />

    <!-- 主内容区 -->
    <view v-if="!isLoading && !loadFailed" class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 筛选栏 -->
      <view class="filter-container">
        <TLFilterBar mode="dropdown" :dimensions="filterDimensions" @change="handleFilterChange" />
      </view>

      <!-- 空状态 -->
      <TLEmpty
        v-if="historyList.length === 0"
        title="暂无方案历史"
        description="生成养生方案后将在这里记录"
      />

      <!-- 方案列表 -->
      <view v-if="historyList.length > 0" class="history-list">
        <view
          class="plan-card"
          v-for="(item, index) in historyList"
          :key="index"
          @click="viewPlanDetail(item)"
        >
          <!-- 标题 -->
          <text class="card-title">{{ item.planTitle || ('方案#' + item.planDate?.slice(0, 10).replace(/-/g, '')) }}</text>

          <!-- 体质 + 标签 -->
          <view class="card-tags">
            <view class="card-constitution">
              <text>{{ item.constitutionName }}</text>
            </view>
            <template v-if="parseTagsJson(item.planTags).length > 0">
              <text
                v-for="(tag, tIdx) in parseTagsJson(item.planTags)"
                :key="tIdx"
                class="card-tag"
              >{{ tag }}</text>
            </template>
            <text v-else class="card-tag">{{ item.seasonName }}</text>
          </view>

          <!-- 进度条 -->
          <view class="card-footer">
            <view class="progress-bar">
              <view class="progress-fill" :style="{ width: item.completionRate + '%' }" />
            </view>
          </view>

          <!-- 日期 + 完成率 + 评分 -->
          <view class="card-bottom">
            <text class="card-date">{{ item.planDate }}</text>
            <text class="card-stat">{{ item.completionRate }}% · {{ item.completedTasks }}/{{ item.totalTasks }}</text>
            <view class="card-rating">
              <view
                v-for="star in 5"
                :key="star"
                class="rating-star"
                :class="{ active: star <= (item.userRating || 0) }"
              >
                <view v-if="star <= (item.userRating || 0)" class="i-solar:star-bold" />
                <view v-else class="i-solar:star-outline" />
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="hasMore && historyList.length > 0" class="load-more" @click="loadMore">
        <text class="load-more-text">加载更多</text>
      </view>
      <view v-else-if="historyList.length > 0" class="no-more">
        <text class="no-more-text">没有更多记录了</text>
      </view>

    </view>

  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onReachBottom } from '@dcloudio/uni-app'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLEmpty from '@/components/TLEmpty/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import TLReload from '@/components/TLReload/index.vue'
import TLFilterBar from '@/components/TLFilterBar/index.vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { parseTagsJson } from '@/utils/format'
import { getPlanHistoryPage } from '@/api/plan/plan'
import type { PlanHistoryItem } from '@/types/biz/plan/plan'
import type { FilterDimension } from '@/types/components/filterBar'

// 页面布局
const { contentPaddingTop } = usePageLayout()

// 加载状态
const isLoading = ref(true)
const loadFailed = ref(false)

// 方案历史
const historyList = ref<PlanHistoryItem[]>([])

// 分页参数
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 筛选维度
const filterDimensions: FilterDimension[] = [
  {
    key: 'timeRange',
    options: [
      { label: '全部', value: 'all' },
      { label: '最近7天', value: '7d' },
      { label: '最近30天', value: '30d' },
      { label: '最近3个月', value: '3m' },
    ],
    defaultValue: 'all',
  },
  {
    key: 'completion',
    options: [
      { label: '全部', value: 'all' },
      { label: '优秀 ≥80%', value: '80' },
      { label: '良好 ≥50%', value: '50' },
      { label: '待提升 <50%', value: '0' },
    ],
    defaultValue: 'all',
  },
]

// 当前筛选值
const filterTimeRange = ref('all')
const filterCompletion = ref('all')

/** 筛选变更 */
const handleFilterChange = (params: Record<string, string | number | null>) => {
  filterTimeRange.value = (params.timeRange as string) || 'all'
  filterCompletion.value = (params.completion as string) || 'all'
  loadHistory(true)
}

/** 获取筛选后的日期范围 */
const getDateRange = () => {
  if (filterTimeRange.value === 'all') return { startDate: undefined, endDate: undefined }
  const now = new Date()
  let start: Date
  switch (filterTimeRange.value) {
    case '7d': start = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000); break
    case '30d': start = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000); break
    case '3m': start = new Date(now.getFullYear(), now.getMonth() - 3, now.getDate()); break
    default: return { startDate: undefined, endDate: undefined }
  }
  const pad = (n: number) => String(n).padStart(2, '0')
  return {
    startDate: `${start.getFullYear()}-${pad(start.getMonth() + 1)}-${pad(start.getDate())}`,
    endDate: `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`,
  }
}

/** 按完成度过滤（前端过滤） */
const filterByCompletion = (list: PlanHistoryItem[]): PlanHistoryItem[] => {
  if (filterCompletion.value === 'all') return list
  const threshold = Number(filterCompletion.value)
  if (threshold === 80) return list.filter(item => item.completionRate >= 80)
  if (threshold === 50) return list.filter(item => item.completionRate >= 50 && item.completionRate < 80)
  if (threshold === 0) return list.filter(item => item.completionRate < 50)
  return list
}

/** 加载方案历史 */
const loadHistory = async (reset = false) => {
  if (!reset && isLoading.value) return
  isLoading.value = true
  loadFailed.value = false
  try {
    if (reset) {
      pageNo.value = 1
      historyList.value = []
    }
    const { startDate, endDate } = getDateRange()
    const result = await getPlanHistoryPage({
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      startDate,
      endDate,
    })

    let list = result?.list || []
    list = filterByCompletion(list)

    if (reset) {
      historyList.value = list
    } else {
      historyList.value = [...historyList.value, ...list]
    }

    total.value = result?.total || 0
  } catch (error) {
    console.error('加载方案历史失败:', error)
    loadFailed.value = true
  } finally {
    isLoading.value = false
  }
}

// 页面加载
onMounted(() => {
  loadHistory(true)
})

// 触底加载更多
onReachBottom(() => {
  if (historyList.value.length < total.value) {
    pageNo.value++
    loadHistory()
  }
})

const loadMore = () => {
  pageNo.value++
  loadHistory()
}

// 是否还有更多
const hasMore = computed(() => historyList.value.length < total.value)

// 查看方案详情
const viewPlanDetail = (item: PlanHistoryItem) => {
  uni.navigateTo({ url: `/pages/plan/details/PlanHistoryDetail?id=${item.id}` })
}
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
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

// 筛选栏
.filter-container {
  padding: 24rpx 24rpx 0;
}

// 单列列表
.history-list {
  padding: 0 24rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

// 方案卡片
.plan-card {
  background: $tf-surface;
  border-radius: 20rpx;
  padding: 24rpx 28rpx;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  // box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);

  &:active {
    opacity: 0.85;
  }
}

// 头部
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-date {
  font-size: 22rpx;
  color: $tf-gray-500;
}

.card-stat {
  font-size: 22rpx;
  color: $tf-gray-500;
}

.card-rating {
  display: flex;
  gap: 2rpx;
}

.rating-star {
  font-size: 20rpx;
  color: $tf-gray-300;

  &.active {
    color: $tf-warning;
  }
}

// 内容区
.card-body {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.card-constitution {
  display: inline-flex;
  flex-shrink: 0;

  text {
    font-size: 20rpx;
    color: $tf-brand;
    background: $tf-brand-bg-light;
    padding: 4rpx 16rpx;
    border-radius: 16rpx;
  }
}

.card-title {
  font-size: 28rpx;
  color: $tf-gray-900;
  font-weight: 600;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

// 标签
.card-tags {
  display: flex;
  gap: 8rpx;
  flex-wrap: wrap;
}

.card-tag {
  font-size: 20rpx;
  color: $tf-gray-600;
  background: $tf-page-bg-color;
  padding: 4rpx 16rpx;
  border-radius: 16rpx;
}

// 底部进度
.card-footer {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  margin-top: 4rpx;
}

.card-bottom {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-top: 4rpx;
}

.progress-bar {
  height: 8rpx;
  border-radius: 4rpx;
  background: $tf-page-bg-color;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 4rpx;
  background: $tf-primary-color;
}

.footer-stat {
  font-size: 20rpx;
  color: $tf-gray-500;
}

// 加载更多
.load-more {
  display: flex;
  justify-content: center;
  padding: 32rpx;
}

.load-more-text {
  font-size: 26rpx;
  color: $tf-primary-color;
}

.no-more {
  display: flex;
  justify-content: center;
  padding: 32rpx;
}

.no-more-text {
  font-size: 22rpx;
  color: $tf-gray-500;
}
</style>
