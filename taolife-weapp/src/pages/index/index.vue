<template>
  <view class="page-container">
    <!-- 开屏广告 -->
    <TLAdSplash config-key="splash_home" />

    <!-- 自定义 tabbar -->
    <TLTabBar :current="0" />

    <!-- 主内容区 -->
    <view class="content-area">
      <!-- 全宽轮播（顶屏幕顶部） -->
      <swiper
        v-if="bannerList.length > 0"
        class="home-banner-swiper"
        :autoplay="autoplay"
        :duration="duration"
        :interval="interval"
        :circular="true"
        :current="currentBannerIndex"
        @change="onBannerChange"
      >
        <swiper-item
          v-for="banner in bannerList"
          :key="banner.id"
        >
          <view class="home-banner-card" @click="onBannerClick(banner)">
            <image class="home-banner-bg" :src="banner.imageUrl" mode="aspectFill" @load="onBannerImageLoad(banner, $event)" />
            <view class="home-banner-overlay-bottom" />
            <view class="home-banner-overlay-top" />
            <view class="home-banner-content" :class="bannerTextColorMap[banner.id] === 'dark' ? 'text-dark' : 'text-light'">
              <view class="home-banner-title">{{ banner.title }}</view>
              <view v-if="banner.subtitle" class="home-banner-subtitle">{{ banner.subtitle }}</view>
            </view>
          </view>
        </swiper-item>
      </swiper>
      <!-- 隐藏 Canvas，用于采样 Banner 图片像素亮度 -->
      <canvas canvas-id="bannerLuminanceCanvas" style="position: fixed; left: -9999px; width: 1px; height: 1px;" />

      <!-- 核心入口宫格 -->
      <view class="entry-grid-wrapper">
        <view class="entry-grid">
          <view
            v-for="item in gridItems"
            :key="item.id"
            class="entry-card"
            @click="item.onClick"
          >
            <view :class="item.icon" class="entry-icon" />
            <text class="entry-title">{{ item.text }}</text>
            <text class="entry-desc">{{ item.description }}</text>
          </view>
        </view>
      </view>

      <!-- 今日养生（签到 + 任务并排） -->
      <view class="daily-wellness-wrapper" v-if="showDailyWellness">
        <view class="daily-wellness-card" @click="handleCheckinClick">
          <view class="daily-wellness-icon-wrapper" :class="{ 'checked': !checkinStatus.canCheckin }">
            <view :class="!checkinStatus.canCheckin ? 'i-solar:check-square-broken' : 'i-solar:calendar-add-outline'" class="daily-wellness-icon" />
          </view>
          <view class="daily-wellness-info">
            <text class="daily-wellness-label">{{ checkinStatus.canCheckin ? '每日签到' : '已签到' }}</text>
            <text class="daily-wellness-sub">连续{{ checkinStatus.consecutiveDays }}天</text>
          </view>
        </view>
        <view class="daily-wellness-divider" />
        <view class="daily-wellness-card" @click="goToTasks">
          <view class="daily-wellness-icon-wrapper task">
            <view class="i-solar:checklist-minimalistic-outline daily-wellness-icon" />
          </view>
          <view class="daily-wellness-info">
            <text class="daily-wellness-label">今日任务</text>
            <text class="daily-wellness-sub">{{ taskProgressText }}</text>
          </view>
        </view>
      </view>

      <!-- 资讯精选 -->
      <view class="article-section">
        <view class="section-header" v-if="activePlan && constitution">
          <view class="section-line" />
          <view class="section-tag">
            <view class="i-solar:hand-stars-outline section-tag-icon" />
            <text>{{ constitution.constitutionName }}体质推荐</text>
          </view>
          <view class="section-line" />
        </view>
        <view class="article-grid">
          <view
            v-for="item in articles"
            :key="item.id"
            class="article-card"
            @click="goToArticleDetail(item.id)"
          >
            <image class="article-cover" :src="item.coverImageUrl" mode="aspectFill" />
            <view class="article-info">
              <text class="article-title">{{ item.title }}</text>
              <view class="article-meta">
                <text class="article-tag"># {{ item.categoryName }}</text>
                <text class="article-read">{{ formatCount(item.readCount) }} 阅读</text>
              </view>
            </view>
          </view>
        </view>
        <!-- 空状态 -->
        <TLEmpty v-if="!loading && articles.length === 0" title="暂无精选文章" description="精彩内容正在准备中~" icon-size="80rpx"
          padding="60rpx 32rpx" min-height="200rpx" title-size="26rpx" title-margin="24rpx" desc-size="22rpx" />

        <!-- 底部Banner广告 -->
        <TLAdBanner config-key="banner_home" />
      </view>

    </view>

    <!-- 签到成功弹窗 -->
    <t-dialog
      v-model:visible="checkinSuccessDialogVisible"
      class="checkin-dialog"
      :confirm-btn="{ content: '好的', theme: 'primary' }"
      @confirm="closeCheckinSuccessDialog"
    >
      <template #top>
        <view class="checkin-dialog-icon">
          <view class="i-solar:check-circle-bold" />
        </view>
      </template>
      <template #content>
        <view class="checkin-dialog-body">
          <text class="checkin-dialog-title">签到成功</text>
          <view class="checkin-dialog-metrics">
            <view class="checkin-dialog-metric">
              <text class="checkin-dialog-metric-value">+{{ checkinStatus.points }}</text>
              <text class="checkin-dialog-metric-label">获得积分</text>
            </view>
            <view class="checkin-dialog-metric">
              <text class="checkin-dialog-metric-value">{{ checkinStatus.consecutiveDays }}天</text>
              <text class="checkin-dialog-metric-label">连续签到</text>
            </view>
          </view>
          <view v-if="checkinStatus.levelUp" class="checkin-level-up">
            <text class="i-solar:star-bold checkin-level-up-icon" />
            <text class="checkin-level-up-text">恭喜升级！</text>
          </view>
        </view>
      </template>
    </t-dialog>

  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import TLTabBar from '@/components/TLTabBar/index.vue'
import TLEmpty from '@/components/TLEmpty/index.vue'
import TDialog from '@tdesign/uniapp/dialog/dialog.vue'
import { enAuth } from '@/utils/authManager'
import { getArticlePage } from '@/api/wisdom/article'
import { getBannerList } from '@/api/fee/homeBanner'
import { getTodayCheckinStatus, dailyCheckin } from '@/api/fee/checkin'
import type { ArticleListItem } from '@/types/biz/wisdom/article'
import type { HomeBannerItem } from '@/types/biz/fee/homeBanner'
import type { TodayCheckinStatus } from '@/types/biz/fee/checkin'
import { formatCount } from '@/utils/format'
import { useUserProfile } from '@/composables/useUserProfile'
import { useActivePlan } from '@/composables/useActivePlan'
import { useAd } from '@/composables/useAd'

const { loadAdConfigs } = useAd()
import TLAdSplash from '@/components/TLAdSplash/index.vue'
import TLAdBanner from '@/components/TLAdBanner/index.vue'

const { constitution, fetchConstitution } = useUserProfile()
const { plan: activePlan, fetchPlan } = useActivePlan()

// 轮播列表
const bannerList = ref<HomeBannerItem[]>([])
const currentBannerIndex = ref(0)
const autoplay = ref(true)
const duration = ref(500)
const interval = ref(5000)

// 资讯列表
const articles = ref<ArticleListItem[]>([])

// 签到状态
const checkinStatus = ref<TodayCheckinStatus>({
  canCheckin: true,
  consecutiveDays: 0,
  totalDays: 0,
  userLevel: 1,
  points: 0,
  levelUp: false,
  rewards: []
})
const checkinLoading = ref(false)
const checkinSuccessDialogVisible = ref(false)

// 宫格入口数据
const gridItems = computed(() => [
  {
    id: 1,
    text: '全身经络',
    description: '经络穴位知识',
    icon: 'i-solar:meditation-round-outline',
    onClick: () => goToMeridian()
  },
  {
    id: 2,
    text: '养生资讯',
    description: '精选养生文章',
    icon: 'i-solar:document-text-outline',
    onClick: () => viewMore()
  },
  {
    id: 3,
    text: '节气养生',
    description: '传统节气知识',
    icon: 'i-solar:sun-fog-outline',
    onClick: () => goToSolarTerm()
  },
  {
    id: 4,
    text: '运动养生',
    description: '运动知识库',
    icon: 'i-solar:running-round-outline',
    onClick: () => goToExercise()
  },
  {
    id: 5,
    text: '体质测评',
    description: 'AI 辩证分析',
    icon: 'i-solar:stretching-round-outline',
    onClick: () => goToTest()
  },
  {
    id: 6,
    text: '我的方案',
    description: activePlan.value ? `${activePlan.value.taskSummary.completedCount}/${activePlan.value.taskSummary.todayTaskCount} 已完成` : '查看调理方案',
    icon: 'i-solar:notebook-minimalistic-outline',
    onClick: () => goToPlan()
  }
])

// 文章列表加载状态
const loading = ref(false)
const loadFailed = ref(false)

// 今日养生卡片：始终显示
const showDailyWellness = computed(() => true)

// 任务进度文字
const taskProgressText = computed(() => {
  if (!activePlan.value?.taskSummary) return '暂无任务'
  const { completedCount, todayTaskCount } = activePlan.value.taskSummary
  if (todayTaskCount === 0) return '暂无任务'
  return `${completedCount}/${todayTaskCount} 已完成`
})

// 跳转任务页
const goToTasks = () => {
  if (!activePlan.value) {
    uni.navigateTo({ url: '/pages/plan/index' })
    return
  }
  const { completedCount, todayTaskCount } = activePlan.value.taskSummary
  uni.navigateTo({
    url: `/pages/plan/Tasks?userPlanId=${activePlan.value.id}&completedCount=${completedCount}&totalTasks=${todayTaskCount}`
  })
}

/** 获取首页轮播列表 */
const fetchBannerList = async () => {
  try {
    const result = await getBannerList()
    bannerList.value = result || []
  } catch (error: any) {
    console.error('获取轮播列表失败:', error)
    bannerList.value = []
  }
}

// Banner 文字颜色映射：banner.id -> 'light' | 'dark'
const bannerTextColorMap = ref<Record<string, 'light' | 'dark'>>({})
const luminanceCanvasCtx = uni.createCanvasContext('bannerLuminanceCanvas')

/** Banner 图片加载完成，采样底部区域亮度 */
const onBannerImageLoad = (banner: HomeBannerItem, e: any) => {
  const imgUrl = banner.imageUrl
  if (!imgUrl || bannerTextColorMap.value[banner.id]) return

  uni.getImageInfo({
    src: imgUrl,
    success: (info) => {
      const w = info.width
      const h = info.height
      // 采样底部 30% 区域，缩放到小尺寸以提高性能
      const sampleH = Math.floor(h * 0.3)
      const drawW = Math.min(w, 50)
      const drawH = Math.min(sampleH, 20)

      luminanceCanvasCtx.drawImage(info.path, 0, h - sampleH, w, sampleH, 0, 0, drawW, drawH)
      luminanceCanvasCtx.draw(false, () => {
        setTimeout(() => {
          uni.canvasGetImageData({
            canvasId: 'bannerLuminanceCanvas',
            x: 0,
            y: 0,
            width: drawW,
            height: drawH,
            success: (res) => {
              const data = res.data
              let totalLuminance = 0
              const pixelCount = data.length / 4
              for (let i = 0; i < data.length; i += 4) {
                // ITU-R BT.601 亮度公式
                totalLuminance += 0.299 * data[i] + 0.587 * data[i + 1] + 0.114 * data[i + 2]
              }
              const avgLuminance = totalLuminance / pixelCount
              // 亮度 > 140 用深色文字，否则用浅色
              bannerTextColorMap.value[banner.id] = avgLuminance > 140 ? 'dark' : 'light'
            },
            fail: () => {
              bannerTextColorMap.value[banner.id] = 'light'
            }
          })
        }, 100)
      })
    },
    fail: () => {
      bannerTextColorMap.value[banner.id] = 'light'
    }
  })
}

/** 轮播切换事件 */
const onBannerChange = (e: any) => {
  const currentIndex = e?.detail?.current ?? e?.current ?? e ?? 0
  currentBannerIndex.value = currentIndex
}

/** 轮播点击事件 */
const onBannerClick = (banner: HomeBannerItem) => {
  if (!banner.linkUrl) return
  uni.navigateTo({ url: banner.linkUrl })
}

/** 获取精选文章列表 */
const fetchArticleList = async () => {
  if (loading.value) return
  loading.value = true
  loadFailed.value = false

  try {
    const param = { isFeatured: 1, pageNo: 1, pageSize: 10 }
    const result = await getArticlePage(param)
    articles.value = result.list || []
  } catch (error: any) {
    console.error('获取文章列表失败:', error)
    loadFailed.value = true
  } finally {
    loading.value = false
  }
}

/** 跳转文章详情 */
const goToArticleDetail = (id: string) => {
  uni.navigateTo({ url: `/pages/wisdom/article/ArticleDetail?id=${id}` })
}

/** 签到点击 */
const handleCheckinClick = () => {
  if (checkinStatus.value.canCheckin) {
    handleDailyCheckin()
  } else {
    uni.navigateTo({ url: '/pages/fee/checkin/calendar/index' })
  }
}

/** 获取今日签到状态 */
const fetchCheckinStatus = async () => {
  try {
    const result = await getTodayCheckinStatus()
    checkinStatus.value = result
  } catch (error: any) {
    console.error('获取签到状态失败:', error)
  }
}

/** 执行每日签到 */
const handleDailyCheckin = async () => {
  if (checkinLoading.value) return
  checkinLoading.value = true
  try {
    const result = await dailyCheckin()
    checkinStatus.value = result
    checkinSuccessDialogVisible.value = true
  } catch (error: any) {
    console.error('签到失败:', error)
    uni.showToast({ title: '签到失败，请稍后重试', icon: 'none' })
  } finally {
    checkinLoading.value = false
  }
}

/** 关闭签到成功弹窗 */
const closeCheckinSuccessDialog = () => {
  checkinSuccessDialogVisible.value = false
}

// 加载所有数据
const loadAll = () => {
  fetchBannerList()
  fetchCheckinStatus()
  fetchArticleList()
  fetchConstitution()
  fetchPlan()
}

// 页面加载
onMounted(async () => {
  await enAuth()
  loadAll()
  loadAdConfigs()

  uni.$on('authLoginSuccess', loadAll)
  onUnmounted(() => {
    uni.$off('authLoginSuccess', loadAll)
  })
})

// 跳转体质测试
const goToTest = () => {
  uni.navigateTo({ url: '/pages/wisdom/constitution/index' })
}

// 跳转养生方案
const goToPlan = () => {
  uni.navigateTo({ url: '/pages/plan/index' })
}

// 跳转经络知识库
const goToMeridian = () => {
  uni.navigateTo({ url: '/pages/wisdom/meridian/index' })
}

// 查看更多资讯
const viewMore = () => {
  uni.navigateTo({ url: '/pages/wisdom/article/index' })
}

// 跳转节气知识
const goToSolarTerm = () => {
  uni.navigateTo({ url: '/pages/wisdom/solarterm/index' })
}

const goToExercise = () => {
  uni.navigateTo({ url: '/pages/wisdom/exercise/index' })
}
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

// 全宽轮播
.home-banner-swiper {
  width: 100%;
  height: 520rpx;
}

.home-banner-swiper ::v-deep swiper-item {
  overflow: hidden;
}

.home-banner-card {
  position: relative;
  width: 100%;
  height: 520rpx;
  overflow: hidden;
}

.home-banner-bg {
  width: 100%;
  height: 100%;
}

// 底部渐变
.home-banner-overlay-bottom {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 200rpx;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.5) 0%, transparent 100%);
}

// 顶部渐变
.home-banner-overlay-top {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  height: 120rpx;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.3) 0%, transparent 100%);
}

.home-banner-content {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 40rpx 40rpx 80rpx;
}

// 亮色图片 -> 深色文字
.home-banner-content.text-dark .home-banner-title {
  color: rgba(0, 0, 0, 0.85);
  text-shadow: 0 1px 3px rgba(255, 255, 255, 0.3);
}
.home-banner-content.text-dark .home-banner-subtitle {
  color: rgba(0, 0, 0, 0.65);
  text-shadow: 0 1px 3px rgba(255, 255, 255, 0.3);
}

// 暗色图片 -> 浅色文字（默认）
.home-banner-content.text-light .home-banner-title {
  color: rgba(255, 255, 255, 0.95);
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.5);
}
.home-banner-content.text-light .home-banner-subtitle {
  color: rgba(255, 255, 255, 0.8);
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.5);
}

.home-banner-title {
  font-size: $tf-text-3xl;
  font-weight: bold;
  line-height: 1.4;
}

.home-banner-subtitle {
  font-size: $tf-text-base;
  margin-top: $tf-space-2;
}

// 核心入口宫格
.entry-grid-wrapper {
  margin: -40rpx 24rpx 24rpx 24rpx;
  position: relative;
  z-index: 1;
}

.entry-grid {
  display: flex;
  flex-wrap: wrap;
  background: $tf-surface;
  border-radius: $tf-radius-lg;
  padding: 12rpx 0;
}

.entry-card {
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16rpx 4rpx;
  border-radius: 16rpx;
  gap: 6rpx;
  box-sizing: border-box;
}

.entry-icon {
  font-size: 36rpx;
  color: $tf-primary-color;
}

.entry-title {
  font-size: $tf-text-sm;
  color: $tf-gray-900;
  font-weight: 600;
}

.entry-desc {
  font-size: $tf-text-xs;
  color: $tf-brand;
}

// 今日养生卡片
.daily-wellness-wrapper {
  display: flex;
  align-items: center;
  margin: 0 24rpx 24rpx 24rpx;
  background: $tf-surface;
  border-radius: $tf-radius-lg;
  padding: 24rpx;
}

.daily-wellness-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.daily-wellness-divider {
  width: 2rpx;
  height: 60rpx;
  background-color: $tf-gray-100;
  margin: 0 16rpx;
}

.daily-wellness-icon-wrapper {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--tf-brand-alpha-10);
  flex-shrink: 0;

  &.checked {
    background-color: var(--tf-brand-alpha-15);
  }

  &.task {
    background-color: var(--tf-warning-alpha-10);
  }
}

.daily-wellness-icon {
  font-size: $tf-text-3xl;
  color: $tf-brand;

  .task & {
    color: $tf-warning;
  }

  .checked & {
    color: $tf-brand;
  }
}

.daily-wellness-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.daily-wellness-label {
  font-size: $tf-text-base;
  color: $tf-gray-900;
  font-weight: 600;
}

.daily-wellness-sub {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
}

// 为你推荐标题
.section-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.section-line {
  width: 120rpx;
  height: 1rpx;
  background: linear-gradient(to right, transparent, $tf-brand, transparent);
}

.section-tag {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: $tf-text-sm;
  color: $tf-brand;
  background-color: var(--tf-brand-alpha-8);
  padding: 8rpx 24rpx;
  border-radius: $tf-radius-2xl;
}

.section-tag-icon {
  font-size: $tf-text-lg;
  color: $tf-brand;
}

// 资讯精选
.article-section {
  margin: 0 24rpx 24rpx 24rpx;
}

.article-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
}

.article-card {
  background: $tf-surface;
  border-radius: $tf-radius-lg;
  overflow: hidden;
}

.article-cover {
  width: 100%;
  height: 256rpx;
}

.article-info {
  padding: 24rpx;
}

.article-title {
  font-size: 24rpx;
  color: $tf-gray-900;
  font-weight: 700;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: 16rpx;
  margin-top: 16rpx;
}

.article-tag {
  font-size: 20rpx;
  color: $tf-gray-600;
}

.article-read {
  font-size: 20rpx;
  color: $tf-gray-500;
}

.checkin-level-up {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $tf-space-2;
  margin-top: $tf-space-4;
  padding: $tf-space-2 $tf-space-6;
  background: linear-gradient(135deg, #FEF3C7, #FDE68A);
  border-radius: $tf-radius-pill;
}

.checkin-level-up-icon {
  font-size: $tf-text-lg;
  color: $tf-warning-dark;
}

.checkin-level-up-text {
  font-size: $tf-text-sm;
  color: $tf-warning-dark;
  font-weight: 600;
}

// 签到弹窗样式覆盖
.checkin-dialog {
  :deep(.t-dialog) {
    border-radius: $tf-radius-2xl;
  }

  :deep(.t-dialog__header) {
    display: none;
  }

  :deep(.t-dialog__body) {
    display: none;
  }

  :deep(.t-dialog__footer) {
    border-top: none;
    padding-top: 0;
  }

  :deep(.t-button--variant-base) {
    background: $tf-gradient-brand;
    border-radius: $tf-radius-3xl;
    color: $tf-surface;
    border: none;
    font-weight: 600;
  }
}

.checkin-dialog-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $tf-space-8 0 $tf-space-4;

  view {
    font-size: 80rpx;
    color: $tf-brand;
  }
}

.checkin-dialog-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 $tf-space-4;
}

.checkin-dialog-title {
  font-size: $tf-text-2xl;
  color: $tf-gray-900;
  font-weight: bold;
}

.checkin-dialog-metrics {
  display: flex;
  justify-content: center;
  gap: $tf-space-4;
  margin-top: $tf-space-6;
}

.checkin-dialog-metric {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: $tf-brand-bg;
  border-radius: $tf-radius-lg;
  padding: $tf-space-3 $tf-space-6;
}

.checkin-dialog-metric-value {
  font-size: $tf-text-lg;
  color: $tf-brand;
  font-weight: bold;
}

.checkin-dialog-metric-label {
  font-size: $tf-text-xs;
  color: $tf-gray-500;
  margin-top: $tf-space-1;
}
</style>
