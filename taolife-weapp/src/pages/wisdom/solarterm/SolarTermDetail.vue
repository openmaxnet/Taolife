<template>
  <view class="page-container">
    <!-- 加载失败时显示 -->
    <TLReload v-if="loadFailed" @reload="handleReload" />

    <!-- 主内容区 -->
    <view v-else-if="detail" class="content-area">
      <!-- 顶部背景大图 -->
      <view class="hero-section">
        <image
          class="hero-image"
          :src="detail.backgroundUrl || detail.coverUrl || defaultBg"
          mode="aspectFill"
        />
        <view class="hero-overlay" />
        <view class="hero-content">
          <view v-if="detail.isCurrent" class="current-tag">
            <text class="current-tag-text">当前节气</text>
          </view>
          <text class="hero-name">{{ detail.termName }}</text>
          <text class="hero-sub">{{ detail.seasonName }} · {{ detail.dateRange }}</text>
        </view>
      </view>

      <!-- 描述卡片 -->
      <view v-if="detail.description" class="info-card">
        <text class="info-text">{{ detail.description }}</text>
      </view>

      <!-- 节气简介 -->
      <view v-if="detail.introduction" class="detail-section">
        <view class="section-header">
          <view class="section-icon i-solar:book-2-outline" />
          <text class="section-title">节气简介</text>
        </view>
        <view class="section-content">
          <text class="content-text">{{ detail.introduction }}</text>
        </view>
      </view>

      <!-- 气候特点 -->
      <view v-if="detail.climate" class="detail-section">
        <view class="section-header">
          <view class="section-icon i-solar:cloud-outline" />
          <text class="section-title">气候特点</text>
        </view>
        <view class="section-content">
          <text class="content-text">{{ detail.climate }}</text>
        </view>
      </view>

      <!-- 养生原则 -->
      <view v-if="detail.healthPrinciples" class="detail-section">
        <view class="section-header">
          <view class="section-icon i-solar:heart-pulse-outline" />
          <text class="section-title">养生原则</text>
        </view>
        <view class="section-content">
          <template v-if="parsedHealthPrinciples.length > 0">
            <view v-for="(item, idx) in parsedHealthPrinciples" :key="idx" class="list-item">
              <view class="list-dot" />
              <text class="list-text">{{ item }}</text>
            </view>
          </template>
          <text v-else class="content-text">{{ detail.healthPrinciples }}</text>
        </view>
      </view>

      <!-- 饮食概要 -->
      <view v-if="detail.dietSummary" class="detail-section">
        <view class="section-header">
          <view class="section-icon i-solar:cup-hot-outline" />
          <text class="section-title">饮食概要</text>
        </view>
        <view class="section-content">
          <text class="content-text">{{ detail.dietSummary }}</text>
        </view>
      </view>

      <!-- 传统习俗 -->
      <view v-if="detail.customs" class="detail-section">
        <view class="section-header">
          <view class="section-icon i-solar:fire-outline" />
          <text class="section-title">传统习俗</text>
        </view>
        <view class="section-content">
          <template v-if="parsedCustoms.length > 0">
            <view v-for="(item, idx) in parsedCustoms" :key="idx" class="list-item">
              <view class="list-dot" />
              <text class="list-text">{{ item }}</text>
            </view>
          </template>
          <text v-else class="content-text">{{ detail.customs }}</text>
        </view>
      </view>

      <!-- 节气谚语 -->
      <view v-if="detail.proverbs" class="detail-section">
        <view class="section-header">
          <view class="section-icon i-solar:chat-square-like-outline" />
          <text class="section-title">节气谚语</text>
        </view>
        <view class="section-content">
          <template v-if="parsedProverbs.length > 0">
            <view v-for="(item, idx) in parsedProverbs" :key="idx" class="proverb-item">
              <text class="proverb-text">「{{ item }}」</text>
            </view>
          </template>
          <text v-else class="content-text">{{ detail.proverbs }}</text>
        </view>
      </view>

      <!-- 底部占位 -->
      <view class="bottom-space" />
    </view>

    <!-- 加载状态 -->
    <view v-else class="loading-container">
      <text>加载中...</text>
    </view>

    <!-- 底部导航栏 -->
    <TLNavBar ref="navBarRef" :title="detail?.termName || '节气详情'" :buttons="['back']" />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import TLNavBar from '@/components/TLNavBar/index.vue'
import TLReload from '@/components/TLReload/index.vue'
import { getSolarTermDetail } from '@/api/wisdom/solarTerm'
import { usePageLayout } from '@/composables/usePageLayout'
import { useDetailLoader } from '@/composables/useDetailLoader'
import { parseTagsJson } from '@/utils/format'
import type { SolarTermDetail } from '@/types/biz/wisdom/solarTerm'

const { navBarRef } = usePageLayout()

const defaultBg = 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg'

let id = ''

onLoad((options) => {
  id = options?.id || ''
})

const { data: detail, loadFailed, load: _loadDetail } = useDetailLoader<SolarTermDetail>(
  () => getSolarTermDetail(id)
)

const parsedHealthPrinciples = computed(() => parseTagsJson(detail.value?.healthPrinciples))
const parsedCustoms = computed(() => parseTagsJson(detail.value?.customs))
const parsedProverbs = computed(() => parseTagsJson(detail.value?.proverbs))

/** 获取节气详情 */
const fetchDetail = async () => {
  if (!id) {
    uni.showToast({ title: '参数错误', icon: 'none' })
    return
  }
  await _loadDetail()
}

/** 重新加载 */
const handleReload = () => {
  fetchDetail()
}

onMounted(() => {
  fetchDetail()
})
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
}

.content-area {
  margin-bottom: 150rpx;
  padding-bottom: 10rpx;
}

// 顶部图片区域
.hero-section {
  position: relative;
  height: 520rpx;
}

.hero-image {
  width: 100%;
  height: 100%;
}

.hero-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 280rpx;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
}

.hero-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40rpx 40rpx 100rpx;
}

.current-tag {
  display: inline-block;
  padding: 6rpx 20rpx;
  background: $tf-brand;
  border-radius: 8rpx;
  margin-bottom: 16rpx;
}

.current-tag-text {
  font-size: $tf-text-sm;
  color: $tf-surface;
}

.hero-name {
  display: block;
  font-size: 52rpx;
  color: $tf-surface;
  font-weight: bold;
}

.hero-sub {
  display: block;
  font-size: $tf-text-base;
  color: rgba(255, 255, 255, 0.8);
  margin-top: $tf-space-2;
}

// 描述卡片
.info-card {
  margin: -36rpx 24rpx 24rpx;
  background: $tf-surface;
  border-radius: $tf-radius-2xl;
  padding: 32rpx;
  position: relative;
  z-index: 10;
}

.info-text {
  font-size: $tf-text-md;
  color: $tf-gray-700;
  line-height: 1.8;
}

// 详情区域
.detail-section {
  margin: 0 24rpx 24rpx;
  background: $tf-surface;
  border-radius: $tf-radius-2xl;
  overflow: hidden;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 28rpx 32rpx;
  border-bottom: $tf-border-light;
}

.section-icon {
  font-size: $tf-text-xl;
  color: $tf-brand;
}

.section-title {
  font-size: $tf-text-lg;
  color: $tf-gray-900;
  font-weight: 600;
}

.section-content {
  padding: 28rpx 32rpx;
}

.content-text {
  font-size: $tf-text-md;
  color: $tf-gray-700;
  line-height: 1.8;
}

// 列表项
.list-item {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  margin-bottom: 16rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.list-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: $tf-brand;
  margin-top: 12rpx;
  flex-shrink: 0;
}

.list-text {
  font-size: $tf-text-md;
  color: $tf-gray-700;
  line-height: 1.8;
}

// 谚语
.proverb-item {
  margin-bottom: 16rpx;
  padding: 20rpx 24rpx;
  background: var(--tf-brand-alpha-6);
  border-radius: $tf-radius-lg;

  &:last-child {
    margin-bottom: 0;
  }
}

.proverb-text {
  font-size: $tf-text-md;
  color: $tf-brand;
  font-style: italic;
  line-height: 1.8;
}

// 加载状态
.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  color: $tf-gray-500;
  font-size: $tf-text-md;
}

// 底部占位
.bottom-space {
  height: 48rpx;
}
</style>
