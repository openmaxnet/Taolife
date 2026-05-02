<template>
  <view class="page-container">

      <!-- 自定义导航栏 -->
      <TLTopBar title="方案广场" :show-back="true" />

      <!-- 加载失败组件 -->
      <TLReload
        v-if="loadFailed"
        title="加载失败"
        description="无法获取方案广场数据，请稍后重试"
        @reload="fetchSquareList(true)"
      />

      <!-- 主内容区 -->
      <view v-if="!loadFailed" class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

        <!-- 排序Tab -->
        <TLFilterBar mode="tabs" :dimensions="sortDimensions" @change="handleSortChange" />

        <!-- 空状态 -->
        <TLEmpty
          v-if="!loading && squareList.length === 0"
          title="暂无方案"
          description="还没有人分享养生方案，快来成为第一个吧"
        />

        <!-- 方案列表 -->
        <view v-else class="square-list">
          <view
            v-for="item in squareList"
            :key="item.id"
            class="square-card"
            @click="goToDetail(item.id)"
          >
            <!-- 用户信息行 -->
            <view class="card-header">
              <image class="user-avatar" :src="item.avatarUrl" mode="aspectFill" />
              <view class="user-info">
                <view class="user-name-row">
                  <text class="user-nickname">{{ item.nickname }}</text>
                  <!-- 官方徽章 -->
                  <view v-if="item.isOfficial === 1" class="official-badge">
                    <view class="i-solar:medal-star-square-line-duotone official-badge-icon" />
                    <text class="official-badge-text">官方</text>
                  </view>
                </view>
                <view class="constitution-badge">
                  <text class="constitution-badge-text">{{ item.constitutionName }}</text>
                </view>
              </view>
            </view>

            <!-- 方案标题 -->
            <text v-if="item.planTitle" class="plan-title">{{ item.planTitle }}</text>
            <!-- 方案摘要 -->
            <text class="plan-summary">{{ truncateText(item.planSummary, 80) }}</text>

            <!-- 标签 -->
            <view v-if="item.planTags" class="plan-tags">
              <view
                v-for="(tag, tagIndex) in parseTagsJson(item.planTags)"
                :key="tagIndex"
                class="plan-tag"
              >
                <text class="plan-tag-text">{{ tag }}</text>
              </view>
            </view>

            <!-- 底部操作栏 -->
            <view class="card-footer">
              <view class="action-item" @click.stop="handleLike(item)">
                <view
                  class="action-icon"
                  :class="[item._isLiked ? 'i-solar:heart-bold liked' : 'i-solar:heart-outline']"
                />
                <text class="action-count">{{ item._likeCount }}</text>
              </view>
              <view class="action-item" @click.stop="handleCollect(item)">
                <view
                  class="action-icon"
                  :class="[item._isCollected ? 'i-solar:star-bold collected' : 'i-solar:star-outline']"
                />
                <text class="action-count">{{ item._collectCount }}</text>
              </view>
              <view class="action-item action-item-disabled">
                <view class="action-icon i-solar:eye-outline action-icon-muted" />
                <text class="action-count">{{ formatCount(item.viewCount) }}</text>
              </view>
              <view class="action-item action-item-disabled">
                <view class="action-icon i-solar:chat-round-outline action-icon-muted" />
                <text class="action-count">{{ formatCount(item.commentCount) }}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 加载更多提示 -->
        <view v-if="loading && squareList.length > 0" class="loading-tip">
          <text>加载中...</text>
        </view>
        <view v-else-if="noMore && squareList.length > 0" class="loading-tip">
          <text>没有更多了</text>
        </view>

      </view>

  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import TLReload from '@/components/TLReload/index.vue'
import TLEmpty from '@/components/TLEmpty/index.vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLFilterBar from '@/components/TLFilterBar/index.vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { formatCount, parseTagsJson } from '@/utils/format'
import { getSquarePage } from '@/api/plan/plan'
import { toggleInteraction } from '@/api/identity/interaction'
import type { PlanSquareItem, PlanSquareQueryParam } from '@/types/biz/plan/plan'
import type { FilterDimension } from '@/types/components/filterBar'


// 页面布局（TopBar 页面只需要 contentPaddingTop）
const { contentPaddingTop } = usePageLayout()

// 排序选项
const sortDimensions: FilterDimension[] = [
  {
    key: 'sortBy',
    options: [
      { label: '最新', value: 1 },
      { label: '最热', value: 2 },
      { label: '最多浏览', value: 3 },
    ],
    defaultValue: 1,
  },
]
const currentSort = ref(1)

// 列表数据
const squareList = ref<(PlanSquareItem & { _isLiked?: boolean; _isCollected?: boolean; _likeCount?: number; _collectCount?: number })[]>([])

// 分页参数
const pageNo = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)
const noMore = ref(false)
const loadFailed = ref(false)

/**
 * 切换排序
 */
const handleSortChange = (params: Record<string, string | number | null>) => {
  currentSort.value = (params.sortBy as number) || 1
  fetchSquareList(true)
}

/**
 * 获取方案广场列表
 */
const fetchSquareList = async (reset = false) => {
  if (loading.value) return

  loading.value = true
  loadFailed.value = false

  try {
    if (reset) {
      pageNo.value = 1
      noMore.value = false
      squareList.value = []
    }

    const params: PlanSquareQueryParam = {
      pageNo: pageNo.value,
      pageSize,
      sortBy: currentSort.value,
    }

    const result = await getSquarePage(params)

    const newList = (result.list || []).map(item => ({
      ...item,
      _isLiked: false,
      _isCollected: false,
      _likeCount: item.likeCount,
      _collectCount: item.collectCount,
    }))

    if (reset) {
      squareList.value = newList
    } else {
      squareList.value = [...squareList.value, ...newList]
    }

    total.value = result.total || 0

    // 判断是否还有更多
    if (squareList.value.length >= total.value) {
      noMore.value = true
    }
  } catch (error: any) {
    console.error('获取方案广场列表失败:', error)
    loadFailed.value = true
  } finally {
    loading.value = false
  }
}

/**
 * 处理点赞
 */
const handleLike = async (item: any) => {
  try {
    const status = await toggleInteraction({ targetType: 1, targetId: item.id, interactionType: 1 })
    item._isLiked = status.isLiked
    item._likeCount = item._isLiked ? (item._likeCount || 0) + 1 : Math.max(0, (item._likeCount || 0) - 1)
  } catch (error: any) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

/**
 * 处理收藏
 */
const handleCollect = async (item: any) => {
  try {
    const status = await toggleInteraction({ targetType: 1, targetId: item.id, interactionType: 2 })
    item._isCollected = status.isCollected
    item._collectCount = item._isCollected ? (item._collectCount || 0) + 1 : Math.max(0, (item._collectCount || 0) - 1)
  } catch (error: any) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

/**
 * 跳转到详情页
 */
const goToDetail = (id: string) => {
  uni.navigateTo({
    url: `/pages/plan/square/SquareShare?id=${id}`,
  })
}

/**
 * 截断文本
 */
const truncateText = (text: string, maxLen: number): string => {
  if (!text) return ''
  return text.length > maxLen ? text.slice(0, maxLen) + '...' : text
}

/**
 * 下拉刷新
 */
onPullDownRefresh(async () => {
  await fetchSquareList(true)
  uni.stopPullDownRefresh()
})

/**
 * 上拉加载更多
 */
onReachBottom(() => {
  if (!noMore.value && !loading.value) {
    pageNo.value++
    fetchSquareList()
  }
})

/**
 * 页面加载
 */
onMounted(() => {
  fetchSquareList(true)
})
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
}

// 内容区域
.content-area {
  padding-bottom: 32rpx;
}

// 排序筛选
.filter-bar {
  margin: 16rpx 24rpx 0;
}

// 方案列表
.square-list {
  padding: 0 24rpx;
}

// 方案卡片
.square-card {
  background: $tf-surface;
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
}

// 用户信息行
.card-header {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.user-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  flex-shrink: 0;
  background: $tf-page-bg-color;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.user-nickname {
  font-size: 28rpx;
  color: $tf-gray-900;
  font-weight: 600;
  max-width: 320rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

// 官方徽章
.official-badge {
  display: flex;
  align-items: center;
  gap: 4rpx;
  background: $tf-gradient-brand-alt;
  padding: 2rpx 12rpx;
  border-radius: 16rpx;
}

.official-badge-icon {
  width: 22rpx;
  height: 22rpx;
  color: $tf-surface;
}

.official-badge-text {
  font-size: 18rpx;
  color: $tf-surface;
  font-weight: 500;
}

// 体质徽章
.constitution-badge {
  display: inline-flex;
}

.constitution-badge-text {
  font-size: 20rpx;
  color: $tf-brand;
  background: $tf-brand-bg-light;
  padding: 4rpx 16rpx;
  border-radius: 16rpx;
}

// 方案标题
.plan-title {
  display: block;
  font-size: 28rpx;
  color: $tf-gray-900;
  font-weight: 600;
  margin-bottom: 8rpx;
}

// 方案摘要
.plan-summary {
  display: block;
  font-size: 26rpx;
  color: $tf-gray-700;
  line-height: 1.6;
  margin-bottom: 16rpx;
}

// 标签
.plan-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.plan-tag {
  padding: 6rpx 16rpx;
  background: $tf-page-bg-color;
  border-radius: 16rpx;
}

.plan-tag-text {
  font-size: 20rpx;
  color: $tf-gray-600;
}

// 底部操作栏
.card-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 32rpx;
  padding-top: 20rpx;
  border-top: $tf-border-light;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.action-item-disabled {
  pointer-events: none;
}

.action-icon {
  width: 32rpx;
  height: 32rpx;
  color: #9CA3AF;

  &.liked {
    color: #EF4444;
  }

  &.collected {
    color: #F59E0B;
  }
}

.action-icon-muted {
  color: $tf-gray-500;
}

.action-count {
  font-size: 22rpx;
  color: $tf-gray-500;
}

// 加载提示
.loading-tip {
  text-align: center;
  padding: 32rpx;
  color: $tf-gray-500;
  font-size: 24rpx;
}
</style>
