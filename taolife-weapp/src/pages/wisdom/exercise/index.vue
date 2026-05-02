<template>
  <view class="page-container">
    <TLTopBar title="运动养生" show-search search-placeholder="搜索运动..." @search="handleSearch" />

    <view v-if="!loadFailed" class="filter-container" :style="{ top: contentPaddingTop + 'px' }">
      <TLFilterBar mode="dropdown" :dimensions="filterDimensions" @change="handleFilterChange" />
    </view>

    <view class="content-area" :style="{ paddingTop: (contentPaddingTop + filterBarHeight) + 'px' }">
      <TLReload v-if="loadFailed" @reload="handleReload" />
      <template v-else>
        <TLEmpty
          v-if="!loading && list.length === 0"
          title="暂无运动"
          description="该分类下还没有运动项目~"
        />

        <view class="exercise-grid">
          <view
            v-for="item in list"
            :key="item.id"
            class="exercise-card"
            @click="goToDetail(item.id)"
          >
            <image class="exercise-image" :src="item.imageUrl || defaultImage" mode="aspectFill" />
            <view v-if="item.videoUrl" class="video-badge">
              <view class="i-solar:play-bold video-icon" />
            </view>
            <view class="exercise-content">
              <text class="exercise-name">{{ item.name }}</text>
              <view class="exercise-tags">
                <text class="tag tag-category">{{ item.categoryName }}</text>
                <text class="tag tag-intensity" :class="'intensity-' + item.intensity">{{ item.intensityName }}</text>
              </view>
              <view class="exercise-meta">
                <text v-if="item.durationMin" class="meta-item">{{ item.durationMin }}分钟</text>
                <text v-if="item.caloriesConsumption" class="meta-item">{{ item.caloriesConsumption }}千卡</text>
              </view>
            </view>
          </view>
        </view>

        <view v-if="loading" class="loading-tip">
          <text>加载中...</text>
        </view>
        <view v-else-if="noMore && list.length > 0" class="loading-tip">
          <text>没有更多了</text>
        </view>
      </template>
    </view>

    <!-- 底部导航栏（返回） -->
    <TLNavBar ref="navBarRef" title="运动养生" />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLNavBar from '@/components/TLNavBar/index.vue'
import TLReload from '@/components/TLReload/index.vue'
import TLEmpty from '@/components/TLEmpty/index.vue'
import TLFilterBar from '@/components/TLFilterBar/index.vue'
import { getExercisePage } from '@/api/wisdom/exercise'
import { usePageLayout } from '@/composables/usePageLayout'
import type { ExerciseListItem } from '@/types/biz/wisdom/exercise'
import type { FilterDimension, FilterParams } from '@/types/components/filterBar'

const defaultImage = 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d362c3036e7c4995ac1af6ed687c80e3.jpg'

const { contentPaddingTop, navBarRef } = usePageLayout()

const filterBarHeight = 52

const filterDimensions: FilterDimension[] = [
  {
    key: 'category',
    options: [
      { label: '分类', value: 0 },
      { label: '传统功法', value: 1 },
      { label: '有氧运动', value: 2 },
      { label: '力量训练', value: 3 },
      { label: '柔韧训练', value: 4 },
      { label: '休闲运动', value: 5 },
    ],
    defaultValue: 0,
  },
  {
    key: 'intensity',
    options: [
      { label: '强度', value: 0 },
      { label: '温和', value: 1 },
      { label: '轻度', value: 2 },
      { label: '中度', value: 3 },
      { label: '重度', value: 4 },
    ],
    defaultValue: 0,
  },
]

const searchKeyword = ref('')
const activeCategory = ref(0)
const activeIntensity = ref(0)

const list = ref<ExerciseListItem[]>([])
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)
const noMore = ref(false)
const loadFailed = ref(false)

const handleSearch = (keyword: string) => {
  searchKeyword.value = keyword
  fetchList(true)
}

const handleFilterChange = (params: FilterParams) => {
  activeCategory.value = (params.category ?? 0) as number
  activeIntensity.value = (params.intensity ?? 0) as number
  fetchList(true)
}

const fetchList = async (reset = false) => {
  if (loading.value) return
  loading.value = true
  loadFailed.value = false

  try {
    if (reset) {
      page.value = 1
      noMore.value = false
      list.value = []
    }

    const param: Record<string, any> = { pageNo: page.value, pageSize }
    if (activeCategory.value) param.category = activeCategory.value
    if (activeIntensity.value) param.intensity = activeIntensity.value
    if (searchKeyword.value) param.keyword = searchKeyword.value

    const result = await getExercisePage(param)
    if (reset) {
      list.value = result.list || []
    } else {
      list.value = [...list.value, ...(result.list || [])]
    }
    total.value = result.total || 0
    if (list.value.length >= total.value) noMore.value = true
  } catch (e) {
    console.error('获取运动列表失败:', e)
    loadFailed.value = true
  } finally {
    loading.value = false
  }
}

const handleReload = () => { fetchList(true) }

const loadMore = () => {
  if (!noMore.value && !loading.value) {
    page.value++
    fetchList()
  }
}

const goToDetail = (id: string) => {
  uni.navigateTo({ url: `./ExerciseDetail?id=${id}` })
}

onPullDownRefresh(async () => {
  await fetchList(true)
  uni.stopPullDownRefresh()
})

onReachBottom(() => loadMore())

onMounted(() => fetchList(true))
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.content-area {
  padding-bottom: 32rpx;
}

.filter-container {
  position: fixed;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 0 24rpx;
  background: $tf-page-bg-color;
}

.exercise-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
  margin: 24rpx;
}

.exercise-card {
  background: $tf-surface;
  border-radius: $tf-radius-xl;
  overflow: hidden;
  position: relative;
}

.exercise-image {
  width: 100%;
  height: 240rpx;
}

.video-badge {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
  width: 48rpx;
  height: 48rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-icon {
  font-size: 24rpx;
  color: #fff;
  margin-left: 4rpx;
}

.exercise-content {
  padding: 20rpx 24rpx 24rpx;
}

.exercise-name {
  display: block;
  font-size: $tf-text-md;
  color: $tf-gray-900;
  font-weight: 600;
}

.exercise-tags {
  display: flex;
  gap: 12rpx;
  margin-top: 12rpx;
}

.tag {
  padding: 4rpx 16rpx;
  font-size: 20rpx;
  border-radius: $tf-radius-sm;
}

.tag-category {
  background: $tf-brand-bg;
  color: $tf-brand;
}

.tag-intensity {
  background: #F3F4F6;
  color: $tf-gray-600;
}

.intensity-1 { background: #DBEAFE; color: #2563EB; }
.intensity-2 { background: #D1FAE5; color: #059669; }
.intensity-3 { background: #FEF3C7; color: #D97706; }
.intensity-4 { background: #FFE4E6; color: #E11D48; }

.exercise-meta {
  display: flex;
  gap: 16rpx;
  margin-top: 12rpx;
}

.meta-item {
  font-size: 20rpx;
  color: $tf-gray-400;
}

.loading-tip {
  text-align: center;
  padding: 32rpx;
  margin: 0 24rpx;
  color: $tf-gray-500;
  font-size: $tf-text-sm;
}
</style>
