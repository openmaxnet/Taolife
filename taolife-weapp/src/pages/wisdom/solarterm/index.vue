<template>
  <view class="page-container">
    <!-- 顶部导航栏 -->
    <TLTopBar title="二十四节气" />

    <!-- 内容区域 -->
    <view class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <TLReload v-if="loadFailed" @reload="handleReload" />
      <template v-else>
        <!-- 按季节分组展示 -->
        <view v-for="season in seasonGroups" :key="season.key" class="season-section">
          <view class="season-header">
            <view class="season-dot" :class="season.key" />
            <text class="season-title">{{ season.label }}</text>
          </view>
          <view class="term-grid">
            <view
              v-for="term in season.terms"
              :key="term.id"
              class="term-card"
              :class="{ 'current': term.isCurrent }"
              @click="goToDetail(term.id)"
            >
              <image v-if="term.coverUrl" class="term-cover" :src="term.coverUrl" mode="aspectFill" />
              <view v-else class="term-cover-placeholder" :class="season.key">
                <text class="term-cover-text">{{ term.termName?.charAt(0) }}</text>
              </view>
              <view class="term-info">
                <text class="term-name">{{ term.termName }}</text>
                <text class="term-date">{{ term.dateRange }}</text>
              </view>
              <view v-if="term.isCurrent" class="current-badge">
                <text class="current-badge-text">当前</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <TLEmpty
          v-if="!loading && solarTermList.length === 0"
          title="暂无节气数据"
          description="节气数据正在准备中~"
          icon-size="80rpx"
          padding="60rpx 32rpx"
          min-height="200rpx"
          title-size="26rpx"
          title-margin="24rpx"
          desc-size="22rpx"
        />
      </template>
    </view>

    <!-- 底部导航栏 -->
    <TLNavBar ref="navBarRef" title="二十四节气" />
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLNavBar from '@/components/TLNavBar/index.vue'
import TLReload from '@/components/TLReload/index.vue'
import TLEmpty from '@/components/TLEmpty/index.vue'
import { getSolarTermList } from '@/api/wisdom/solarTerm'
import { usePageLayout } from '@/composables/usePageLayout'
import type { SolarTermListItem } from '@/types/biz/wisdom/solarTerm'

const { contentPaddingTop, navBarRef } = usePageLayout()

const solarTermList = ref<SolarTermListItem[]>([])
const loading = ref(false)
const loadFailed = ref(false)

const seasonConfig: Record<number, { key: string, label: string }> = {
  1: { key: 'spring', label: '春' },
  2: { key: 'summer', label: '夏' },
  3: { key: 'autumn', label: '秋' },
  4: { key: 'winter', label: '冬' },
}

/** 按季节分组 */
const seasonGroups = computed(() => {
  const groups: { key: string, label: string, terms: SolarTermListItem[] }[] = []
  for (const [season, config] of Object.entries(seasonConfig)) {
    const terms = solarTermList.value.filter(t => t.season === Number(season))
    if (terms.length > 0) {
      groups.push({ key: config.key, label: config.label, terms })
    }
  }
  return groups
})

/** 获取节气列表 */
const fetchSolarTermList = async () => {
  loading.value = true
  loadFailed.value = false
  try {
    const result = await getSolarTermList()
    solarTermList.value = result || []
  } catch (error: any) {
    console.error('获取节气列表失败:', error)
    loadFailed.value = true
  } finally {
    loading.value = false
  }
}

/** 重新加载 */
const handleReload = () => {
  fetchSolarTermList()
}

/** 跳转节气详情 */
const goToDetail = (id: string) => {
  uni.navigateTo({ url: `/pages/wisdom/solarterm/SolarTermDetail?id=${id}` })
}

/** 下拉刷新 */
onPullDownRefresh(async () => {
  await fetchSolarTermList()
  uni.stopPullDownRefresh()
})

onMounted(() => {
  fetchSolarTermList()
})
</script>

<style lang="scss">
.page-container {
  padding-bottom: 140rpx;
}

.content-area {
  padding-bottom: 32rpx;
}

// 季节分组
.season-section {
  margin: 0 24rpx 32rpx;
}

.season-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.season-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;

  &.spring { background-color: #4CAF50; }
  &.summer { background-color: #FF5722; }
  &.autumn { background-color: #FF9800; }
  &.winter { background-color: #2196F3; }
}

.season-title {
  font-size: $tf-text-xl;
  color: $tf-gray-900;
  font-weight: 700;
}

// 节气网格
.term-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
}

.term-card {
  position: relative;
  background: $tf-surface;
  border-radius: $tf-radius-lg;
  overflow: hidden;

  &.current {
    box-shadow: 0 0 0 3rpx $tf-brand;
  }
}

.term-cover {
  width: 100%;
  height: 160rpx;
}

.term-cover-placeholder {
  width: 100%;
  height: 160rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &.spring { background-color: rgba(76, 175, 80, 0.15); }
  &.summer { background-color: rgba(255, 87, 34, 0.15); }
  &.autumn { background-color: rgba(255, 152, 0, 0.15); }
  &.winter { background-color: rgba(33, 150, 243, 0.15); }
}

.term-cover-text {
  font-size: 56rpx;
  font-weight: 700;
  color: $tf-brand;
}

.term-info {
  padding: 16rpx;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.term-name {
  font-size: $tf-text-base;
  color: $tf-gray-900;
  font-weight: 600;
}

.term-date {
  font-size: $tf-text-xs;
  color: $tf-gray-500;
}

// 当前节气角标
.current-badge {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  background: $tf-brand;
  padding: 2rpx 12rpx;
  border-radius: 8rpx;
}

.current-badge-text {
  font-size: $tf-text-xs;
  color: $tf-surface;
}
</style>
