<template>
  <view class="page-container">
    <!-- 加载失败时显示 -->
    <TLReload v-if="loadFailed" @reload="handleReload" />

    <!-- 主内容区 -->
    <view v-else-if="foodDetail" class="content-area">
      <!-- 顶部图片区域 -->
      <view class="hero-section">
        <image
          class="hero-image"
          :src="foodDetail.imageUrl || defaultImage"
          mode="aspectFill"
        />
        <view class="hero-overlay">
          <text class="hero-name">{{ foodDetail.name }}</text>
          <text class="hero-pinyin">{{ foodDetail.namePinyin }}</text>
        </view>
      </view>

      <!-- 基础信息卡片 -->
      <view class="info-card">
        <view class="info-row">
          <view class="info-item">
            <text class="info-label">分类</text>
            <text class="info-value">{{ foodDetail.categoryName }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">性质</text>
            <text
              class="info-value nature-tag"
              :class="'nature-' + foodDetail.nature"
            >
              {{ foodDetail.natureName }}
            </text>
          </view>
        </view>
        <view class="info-row">
          <view class="info-item">
            <text class="info-label">味道</text>
            <text class="info-value">{{ foodDetail.flavor || '暂无' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">归经</text>
            <text class="info-value">{{ foodDetail.meridianEntry || '暂无' }}</text>
          </view>
        </view>
      </view>

      <!-- 功效 -->
      <view class="detail-section">
        <view class="section-header">
          <text class="section-title">功效</text>
        </view>
        <view class="section-content">
          <text class="efficacy-text">{{ foodDetail.efficacy || '暂无' }}</text>
        </view>
      </view>

      <!-- 适宜人群 -->
      <view class="detail-section" v-if="foodDetail.indications">
        <view class="section-header">
          <text class="section-title">适宜人群</text>
        </view>
        <view class="section-content">
          <text class="content-text">{{ foodDetail.indications }}</text>
        </view>
      </view>

      <!-- 禁忌人群 -->
      <view class="detail-section" v-if="foodDetail.contraindications">
        <view class="section-header">
          <text class="section-title">禁忌人群</text>
        </view>
        <view class="section-content">
          <text class="content-text warning">{{ foodDetail.contraindications }}</text>
        </view>
      </view>

      <!-- 用法用量 -->
      <view class="detail-section" v-if="foodDetail.usage">
        <view class="section-header">
          <text class="section-title">用法用量</text>
        </view>
        <view class="section-content">
          <text class="content-text">{{ foodDetail.usage }}</text>
        </view>
      </view>

      <!-- 养生食谱 -->
      <view class="detail-section" v-if="foodDetail.recipes">
        <view class="section-header">
          <text class="section-title">养生食谱</text>
        </view>
        <view class="section-content">
          <text class="content-text">{{ foodDetail.recipes }}</text>
        </view>
      </view>

      <!-- 底部占位 -->
      <view class="bottom-space"></view>
    </view>

    <!-- 加载状态 -->
    <TLLoading v-else />

  </view>

  <!-- 底部导航栏 -->
  <TLNavBar
    ref="navBarRef"
    title="食材详情"
    :buttons="['back', 'like', 'collect']"
    :is-liked="isLiked"
    :is-collected="isCollected"
    @like="handleLike"
    @collect="handleCollect"
  />

  <!-- 悬浮问AI按钮 -->
  <view v-if="foodDetail" class="fab-ask-ai" @click="askAiAboutFood">
    <view class="i-solar:chat-round-dots-outline fab-icon" />
  </view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app';
import TLNavBar from '@/components/TLNavBar/index.vue';
import TLReload from '@/components/TLReload/index.vue';
import TLLoading from '@/components/TLLoading/index.vue';
import { getFoodDetail } from '@/api/wisdom/food';
import { usePageLayout } from '@/composables/usePageLayout';
import { useDetailLoader } from '@/composables/useDetailLoader';
import { useInteraction } from '@/composables/useInteraction';
import { toggleInteraction, getInteractionStatus } from '@/api/identity/interaction';
import type { FoodDetail } from '@/types/biz/wisdom/food';
import { useUserProfile } from '@/composables/useUserProfile';

const { constitution, fetchConstitution } = useUserProfile();

const { navBarRef } = usePageLayout();

// 默认图片
const defaultImage = 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d362c3036e7c4995ac1af6ed687c80e3.jpg';

let id = ''

onLoad((options) => {
  id = options?.id || ''
})

const { data: foodDetail, loadFailed, load: _loadDetail } = useDetailLoader<FoodDetail>(
  () => getFoodDetail(id)
);

const { isLiked, isCollected, loadStatus, toggleLike: handleLike, toggleCollect: handleCollect } = useInteraction(
  { toggle: toggleInteraction, getStatus: getInteractionStatus },
  3,
  () => foodDetail.value?.id || '',
);

const fetchFoodDetail = async () => {
  if (!id) {
    uni.showToast({ title: '参数错误', icon: 'none' })
    return
  }
  await _loadDetail()
}

/**
 * 重新加载
 */
const handleReload = () => {
  fetchFoodDetail();
};

/**
 * 下拉刷新
 */
onPullDownRefresh(async () => {
  await fetchFoodDetail();
  uni.stopPullDownRefresh();
});

// 问AI这个食材适不适合自己
const askAiAboutFood = () => {
  const name = foodDetail.value?.name || '这个食材'
  const nature = foodDetail.value?.natureName || ''
  const constitutionName = constitution.value?.constitutionName || ''
  const question = constitutionName
    ? `我是${constitutionName}，${name}（${nature}）适合我吃吗？有什么注意事项？`
    : `${name}有什么养生功效？适合什么体质的人食用？`
  uni.navigateTo({
    url: `/pages/ai/chat/index?presetQuestion=${encodeURIComponent(question)}`
  })
}

// 页面加载
onMounted(async () => {
  await fetchFoodDetail();
  loadStatus();
  fetchConstitution();
});
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
  padding: 40rpx $tf-space-6 100rpx;
  background: linear-gradient(transparent, rgba(0,0,0,0.6));
}

.hero-name {
  display: block;
  font-size: $tf-text-4xl;
  color: $tf-surface;
  font-weight: bold;
}

.hero-pinyin {
  display: block;
  font-size: $tf-text-sm;
  color: rgba(255,255,255,0.8);
  margin-top: $tf-space-2;
}

// 悬浮问AI按钮
.fab-ask-ai {
  position: fixed;
  right: 32rpx;
  bottom: calc(240rpx + constant(safe-area-inset-bottom));
  bottom: calc(240rpx + env(safe-area-inset-bottom));
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background: $tf-gradient-brand;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx var(--tf-brand-alpha-35);
  z-index: 100;

  &:active {
    transform: scale(0.92);
  }
}

.fab-icon {
  font-size: $tf-text-3xl;
  color: $tf-surface;
}

// 基础信息卡片
.info-card {
  margin: -36rpx $tf-space-5 $tf-space-5;
  background: $tf-surface;
  border-radius: $tf-radius-2xl;
  padding: $tf-space-5;
  position: relative;
  z-index: 10;
}

.info-row {
  display: flex;
  gap: $tf-space-6;
}

.info-row + .info-row {
  margin-top: $tf-space-5;
}

.info-item {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
  margin-bottom: $tf-space-2;
}

.info-value {
  font-size: $tf-text-md;
  color: $tf-gray-900;
  font-weight: 500;
}

.nature-tag {
  display: inline-block;
  padding: $tf-space-1 $tf-space-3;
  border-radius: $tf-radius-sm;
  font-size: $tf-text-sm;
}

.nature-1 {
  background: #DBEAFE;
  color: #2563EB;
}

.nature-2 {
  background: $tf-brand-bg;
  color: $tf-brand;
}

.nature-3 {
  background: #FEF3C7;
  color: #D97706;
}

.nature-4 {
  background: #FFE4E6;
  color: #E11D48;
}

.nature-5 {
  background: #FEE2E2;
  color: #DC2626;
}

// 详情区域
.detail-section {
  margin: 0 $tf-space-5 $tf-space-5;
  background: $tf-surface;
  border-radius: $tf-radius-2xl;
  overflow: hidden;
}

.section-header {
  padding: $tf-space-5 $tf-space-6;
  border-bottom: $tf-border-normal;
}

.section-title {
  font-size: $tf-text-lg;
  color: $tf-gray-900;
  font-weight: 600;
}

.section-content {
  padding: $tf-space-5 $tf-space-6;
}

.efficacy-text {
  font-size: $tf-text-md;
  color: $tf-primary-color;
  font-weight: 500;
  line-height: 1.6;
}

.content-text {
  font-size: $tf-text-md;
  color: $tf-gray-700;
  line-height: 1.8;
}

.content-text.warning {
  color: #DC2626;
}

// 底部占位
.bottom-space {
  height: 48rpx;
}
</style>
