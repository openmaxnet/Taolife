<template>
  <view class="page-container">
    <TLNavBar ref="navBarRef" title="运动详情" :buttons="['back', 'like', 'collect']" :is-liked="isLiked" :is-collected="isCollected" @like="handleLike" @collect="handleCollect" />

    <TLReload v-if="loadFailed" @reload="loadDetail" />
    <TLLoading v-else-if="loading" />
    <template v-else-if="detail">
      <!-- 顶部图片 -->
      <view class="hero-section">
        <image class="hero-image" :src="detail.imageUrl || defaultImage" mode="aspectFill" />
        <view class="hero-overlay">
          <text class="hero-name">{{ detail.name }}</text>
          <text v-if="detail.namePinyin" class="hero-pinyin">{{ detail.namePinyin }}</text>
        </view>
      </view>

      <!-- 信息卡片 -->
      <view class="info-card">
        <view class="info-row">
          <view class="info-item">
            <text class="info-value">{{ detail.categoryName || '-' }}</text>
            <text class="info-label">分类</text>
          </view>
          <view class="info-item">
            <text class="info-value">{{ detail.intensityName || '-' }}</text>
            <text class="info-label">强度</text>
          </view>
          <view class="info-item">
            <text class="info-value">{{ difficultyMap[detail.difficultyLevel] || '-' }}</text>
            <text class="info-label">难度</text>
          </view>
          <view class="info-item">
            <text class="info-value">{{ detail.durationMin ? detail.durationMin + '分钟' : '-' }}</text>
            <text class="info-label">时长</text>
          </view>
        </view>
        <view v-if="detail.caloriesConsumption" class="calories-row">
          <text class="calories-text">预计消耗 {{ detail.caloriesConsumption }} 千卡</text>
        </view>
      </view>

      <!-- 视频区域 -->
      <view v-if="detail.videoUrl" class="section-card">
        <text class="section-title">演示视频</text>
        <view class="video-wrapper">
          <video
            class="exercise-video"
            :src="detail.videoUrl"
            :poster="detail.imageUrl"
            controls
            show-center-play-btn
          />
        </view>
      </view>

      <!-- 描述 -->
      <view v-if="detail.description" class="section-card">
        <text class="section-title">运动简介</text>
        <text class="section-text">{{ detail.description }}</text>
      </view>

      <!-- 动作步骤 -->
      <view v-if="detail.steps" class="section-card">
        <text class="section-title">动作步骤</text>
        <text class="section-text">{{ detail.steps }}</text>
      </view>

      <!-- 功效 -->
      <view v-if="detail.efficacy" class="section-card">
        <text class="section-title">养生功效</text>
        <text class="section-text">{{ detail.efficacy }}</text>
      </view>

      <!-- 适宜症状 -->
      <view v-if="detail.indications" class="section-card">
        <text class="section-title">适宜症状</text>
        <text class="section-text">{{ detail.indications }}</text>
      </view>

      <!-- 禁忌 -->
      <view v-if="detail.contraindications" class="section-card section-warning">
        <text class="section-title title-warning">注意事项</text>
        <text class="section-text">{{ detail.contraindications }}</text>
      </view>

      <!-- 适宜体质/季节 -->
      <view v-if="constitutionTags.length || seasonTags.length" class="section-card">
        <text class="section-title">适宜人群</text>
        <view v-if="constitutionTags.length" class="tag-row">
          <text class="tag-label">体质：</text>
          <view class="tag-list">
            <text v-for="t in constitutionTags" :key="t" class="tag-item tag-brand">{{ t }}</text>
          </view>
        </view>
        <view v-if="seasonTags.length" class="tag-row">
          <text class="tag-label">季节：</text>
          <view class="tag-list">
            <text v-for="t in seasonTags" :key="t" class="tag-item tag-green">{{ t }}</text>
          </view>
        </view>
      </view>

      <!-- 相关运动 -->
      <view v-if="detail.relatedExercises?.length" class="section-card">
        <text class="section-title">相关运动</text>
        <scroll-view scroll-x class="related-scroll">
          <view class="related-list">
            <view
              v-for="item in detail.relatedExercises"
              :key="item.id"
              class="related-card"
              @click="goToDetail(item.id)"
            >
              <image class="related-image" :src="item.imageUrl || defaultImage" mode="aspectFill" />
              <text class="related-name">{{ item.name }}</text>
              <text class="related-tag">{{ item.categoryName }}</text>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 问AI浮动按钮 -->
      <view class="fab-ai" @click="askAi">
        <view class="i-solar:chat-round-dots-bold fab-icon" />
      </view>
    </template>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import TLNavBar from '@/components/TLNavBar/index.vue'
import TLReload from '@/components/TLReload/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import { getExerciseDetail } from '@/api/wisdom/exercise'
import { usePageLayout } from '@/composables/usePageLayout'
import { useDetailLoader } from '@/composables/useDetailLoader'
import { useInteraction } from '@/composables/useInteraction'
import { toggleInteraction, getInteractionStatus } from '@/api/identity/interaction'
import { enAuth } from '@/utils/authManager'
import type { ExerciseDetail } from '@/types/biz/wisdom/exercise'

const defaultImage = 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d362c3036e7c4995ac1af6ed687c80e3.jpg'

const difficultyMap: Record<number, string> = { 1: '简单', 2: '中等', 3: '困难' }
const seasonMap: Record<number, string> = { 1: '春季', 2: '夏季', 3: '秋季', 4: '冬季' }
const constitutionCodeMap: Record<string, string> = {
  QX: '气虚', YX: '阴虚', YANG: '阳虚', TS: '痰湿', SR: '湿热',
  XY: '血瘀', QY: '气郁', TB: '特禀', HP: '平和',
}

const { navBarRef } = usePageLayout()

let exerciseId = ''

const { data: detail, isLoading: loading, loadFailed, load: _loadDetail } = useDetailLoader<ExerciseDetail>(
  () => getExerciseDetail(exerciseId),
  { ensureAuth: enAuth }
)

const { isLiked, isCollected, loadStatus, toggleLike: handleLike, toggleCollect: handleCollect } = useInteraction(
  { toggle: toggleInteraction, getStatus: getInteractionStatus },
  4,
  () => detail.value?.id || '',
)

const constitutionTags = computed(() => {
  if (!detail.value?.targetConstitutionCodes) return []
  try {
    const codes: string[] = JSON.parse(detail.value.targetConstitutionCodes)
    return codes.map(c => constitutionCodeMap[c] || c).filter(Boolean)
  } catch { return [] }
})

const seasonTags = computed(() => {
  if (!detail.value?.targetSeason) return []
  try {
    const seasons: number[] = JSON.parse(detail.value.targetSeason)
    return seasons.map(s => seasonMap[s]).filter(Boolean)
  } catch { return [] }
})

const loadDetail = () => _loadDetail()

onLoad(async (options) => {
  exerciseId = options?.id || ''
  if (exerciseId) {
    await loadDetail()
    loadStatus()
  }
})

const goToDetail = (id: string) => {
  uni.redirectTo({ url: `./ExerciseDetail?id=${id}` })
}

const askAi = () => {
  if (!detail.value) return
  const question = `关于「${detail.value.name}」这个运动，请结合我的体质给出详细建议`
  uni.navigateTo({
    url: `/pages/ai/chat/index?question=${encodeURIComponent(question)}`,
  })
}
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.hero-section {
  position: relative;
  height: 480rpx;
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
  padding: $tf-space-6;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
}

.hero-name {
  display: block;
  font-size: 40rpx;
  color: #fff;
  font-weight: bold;
}

.hero-pinyin {
  display: block;
  font-size: $tf-text-sm;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 4rpx;
}

.info-card {
  margin: -40rpx $tf-space-4 0;
  padding: $tf-space-5;
  background: $tf-surface;
  border-radius: $tf-radius-xl;
  position: relative;
  z-index: 1;
}

.info-row {
  display: flex;
  justify-content: space-around;
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.info-value {
  font-size: $tf-text-md;
  color: $tf-gray-800;
  font-weight: 600;
}

.info-label {
  font-size: 20rpx;
  color: $tf-gray-400;
  margin-top: 4rpx;
}

.calories-row {
  margin-top: $tf-space-3;
  padding-top: $tf-space-3;
  border-top: 1rpx solid $tf-gray-100;
  text-align: center;
}

.calories-text {
  font-size: $tf-text-sm;
  color: #D97706;
}

.section-card {
  margin: $tf-space-4;
  padding: $tf-space-5;
  background: $tf-surface;
  border-radius: $tf-radius-xl;
}

.section-title {
  display: block;
  font-size: $tf-text-md;
  color: $tf-gray-800;
  font-weight: 600;
  margin-bottom: $tf-space-3;
}

.title-warning {
  color: #DC2626;
}

.section-warning {
  background: #FEF2F2;
  border: 1rpx solid #FECACA;
}

.section-text {
  font-size: $tf-text-md;
  color: $tf-gray-600;
  line-height: 1.8;
}

// 视频
.video-wrapper {
  border-radius: $tf-radius-lg;
  overflow: hidden;
}

.exercise-video {
  width: 100%;
  height: 420rpx;
}

.external-video-card {
  position: relative;
  border-radius: $tf-radius-lg;
  overflow: hidden;
  height: 420rpx;
}

.external-video-poster {
  width: 100%;
  height: 100%;
}

.external-video-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.4);
}

.play-btn {
  width: 96rpx;
  height: 96rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.play-icon {
  font-size: 40rpx;
  color: $tf-brand;
  margin-left: 6rpx;
}

.external-video-hint {
  font-size: $tf-text-sm;
  color: rgba(255, 255, 255, 0.9);
  margin-top: $tf-space-3;
}

// 标签
.tag-row {
  display: flex;
  align-items: center;
  margin-bottom: $tf-space-2;
}

.tag-label {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
  white-space: nowrap;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: $tf-space-2;
}

.tag-item {
  padding: 4rpx 16rpx;
  font-size: 20rpx;
  border-radius: $tf-radius-sm;
}

.tag-brand {
  background: $tf-brand-bg;
  color: $tf-brand;
}

.tag-green {
  background: #D1FAE5;
  color: #059669;
}

// 相关运动
.related-scroll {
  white-space: nowrap;
}

.related-list {
  display: inline-flex;
  gap: $tf-space-4;
}

.related-card {
  width: 200rpx;
  flex-shrink: 0;
}

.related-image {
  width: 200rpx;
  height: 150rpx;
  border-radius: $tf-radius-lg;
}

.related-name {
  display: block;
  font-size: $tf-text-sm;
  color: $tf-gray-800;
  font-weight: 500;
  margin-top: 8rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.related-tag {
  display: inline-block;
  font-size: 18rpx;
  color: $tf-brand;
  background: $tf-brand-bg;
  padding: 2rpx 12rpx;
  border-radius: $tf-radius-sm;
  margin-top: 4rpx;
}

// FAB
.fab-ai {
  position: fixed;
  right: $tf-space-6;
  bottom: calc(160rpx + constant(safe-area-inset-bottom));
  bottom: calc(160rpx + env(safe-area-inset-bottom));
  width: 96rpx;
  height: 96rpx;
  background: linear-gradient(135deg, $tf-brand, $tf-brand-700);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx var(--tf-brand-alpha-40);
  z-index: 100;
}

.fab-icon {
  font-size: 44rpx;
  color: #fff;
}
</style>
