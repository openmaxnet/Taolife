<template>
  <view class="page-container">
    <!-- 加载失败时显示 -->
    <TLReload v-if="loadFailed" @reload="handleReload" />

    <!-- 主内容区 -->
    <view v-else-if="articleDetail" class="content-area">
      <!-- 顶部图片区域 -->
      <view class="hero-section">
        <image
          class="hero-image"
          :src="articleDetail.coverImageUrl"
          mode="aspectFill"
        />
        <view class="hero-overlay">
          <text class="hero-title">{{ articleDetail.title }}</text>
          <view class="hero-meta">
            <text class="hero-author">{{ articleDetail.author || '道养生活' }}</text>
            <text class="hero-dot">·</text>
            <text class="hero-date">{{ formatDate(articleDetail.publishTime) }}</text>
          </view>
        </view>
      </view>

      <!-- 基础信息卡片 -->
      <view class="info-card">
        <view class="info-row">
          <view class="info-item">
            <text class="info-label">分类</text>
            <text class="info-value category-tag">{{ articleDetail.categoryName }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">阅读</text>
            <text class="info-value">{{ formatCount(articleDetail.readCount) }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">点赞</text>
            <text class="info-value">{{ formatCount(likeCount) }}</text>
          </view>
        </view>
      </view>

      <!-- 摘要 -->
      <view class="detail-section" v-if="articleDetail.summary">
        <view class="section-header">
          <text class="section-title">摘要</text>
        </view>
        <view class="section-content">
          <text class="summary-text">{{ articleDetail.summary }}</text>
        </view>
      </view>

<!-- 正文内容 -->
      <view class="detail-section">
        <chatMarkdown v-if="articleDetail.contentType === 1" :content="articleDetail.content || ''" class="markdown-wrapper" />
        <view v-else class="section-content">
          <text class="content-text">{{ articleDetail.content }}</text>
        </view>
      </view>

      <!-- 标签 -->
      <view class="detail-section" v-if="articleDetail.tags">
        <view class="section-header">
          <text class="section-title">标签</text>
        </view>
        <view class="section-content">
          <view class="tags-container">
            <text class="tag-item" v-for="(tag, index) in parseTagsJson(articleDetail.tags)" :key="index">
              {{ tag }}
            </text>
          </view>
        </view>
      </view>

    </view>

    <!-- 加载状态 -->
    <TLLoading v-else />

  </view>

  <!-- 底部导航栏 -->
  <TLNavBar
    ref="navBarRef"
    title="文章详情"
    :buttons="['back', 'like', 'collect']"
    :is-liked="isLiked"
    :is-collected="isCollected"
    @like="handleLike"
    @collect="handleCollect"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app';
import TLNavBar from '@/components/TLNavBar/index.vue';
import TLReload from '@/components/TLReload/index.vue';
import TLLoading from '@/components/TLLoading/index.vue';
import chatMarkdown from '@tdesign/uniapp-chat/chat-markdown/chat-markdown.vue';
import { getArticleDetail } from '@/api/wisdom/article';
import { usePageLayout } from '@/composables/usePageLayout';
import { useDetailLoader } from '@/composables/useDetailLoader';
import { useInteraction } from '@/composables/useInteraction';
import { toggleInteraction, getInteractionStatus } from '@/api/identity/interaction';
import { formatCount, formatDate, parseTagsJson } from '@/utils/format';
import type { ArticleDetail } from '@/types/biz/wisdom/article';


const { navBarRef } = usePageLayout();

let id = ''

onLoad((options) => {
  id = options?.id || ''
})

const { data: articleDetail, loadFailed, load: _loadDetail } = useDetailLoader<ArticleDetail>(
  () => getArticleDetail(id)
);

const fetchArticleDetail = async () => {
  if (!id) {
    uni.showToast({ title: '参数错误', icon: 'none' })
    return
  }
  await _loadDetail()
}

const likeCount = ref(0);

const { isLiked, isCollected, loadStatus, toggleLike, toggleCollect } = useInteraction(
  { toggle: toggleInteraction, getStatus: getInteractionStatus },
  2,
  () => articleDetail.value?.id || '',
);

/**
 * 处理点赞（含点赞计数）
 */
const handleLike = async () => {
  const wasLiked = isLiked.value
  await toggleLike()
  if (isLiked.value !== wasLiked) {
    likeCount.value += isLiked.value ? 1 : -1
  }
}

/**
 * 处理收藏
 */
const handleCollect = () => {
  toggleCollect()
}

/**
 * 重新加载
 */
const handleReload = () => {
  fetchArticleDetail();
};

/**
 * 下拉刷新
 */
onPullDownRefresh(async () => {
  await fetchArticleDetail();
  uni.stopPullDownRefresh();
});

// 页面加载
onMounted(async () => {
  await fetchArticleDetail();
  if (articleDetail.value) {
    likeCount.value = articleDetail.value.likeCount || 0;
    loadStatus();
  }
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
  padding: 48rpx 24rpx 120rpx;
  background: linear-gradient(transparent, rgba(0,0,0,0.6));
}

.hero-title {
  display: block;
  font-size: 40rpx;
  color: $tf-surface;
  font-weight: bold;
  line-height: 1.4;
}

.hero-meta {
  display: flex;
  align-items: center;
  margin-top: 16rpx;
}

.hero-author {
  font-size: 24rpx;
  color: rgba(255,255,255,0.8);
}

.hero-dot {
  margin: 0 12rpx;
  color: rgba(255,255,255,0.8);
}

.hero-date {
  font-size: 24rpx;
  color: rgba(255,255,255,0.8);
}

// 基础信息卡片
.info-card {
  margin: -40rpx 24rpx 24rpx;
  background: $tf-surface;
  border-radius: 24rpx;
  padding: 32rpx;
  // box-shadow: $uni-card-shadow;
  position: relative;
  z-index: 10;
}

.info-row {
  display: flex;
  gap: 32rpx;
}

.info-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.info-label {
  font-size: 24rpx;
  color: $tf-gray-500;
  margin-bottom: 8rpx;
}

.info-value {
  font-size: 28rpx;
  color: $tf-gray-900;
  font-weight: 500;
}

.category-tag {
  color: $tf-primary-color;
  font-weight: normal;
}

// 详情区域
.detail-section {
  margin: 0 24rpx 24rpx;
  padding: 0 24rpx 24rpx;
  background: $tf-surface;
  border-radius: 24rpx;
  overflow: hidden;
  // box-shadow: $uni-card-shadow;
}

.section-header {
  padding: 24rpx;
  border-bottom: $tf-border-normal;
}

.section-title {
  font-size: 30rpx;
  color: $tf-gray-900;
  font-weight: bold;
}

.section-content {
  padding: 24rpx;
}

.summary-text {
  font-size: 28rpx;
  color: $tf-gray-700;
  line-height: 1.8;
  font-style: italic;
}

.content-text {
  font-size: 28rpx;
  color: $tf-gray-700;
  line-height: 1.8;
  white-space: pre-wrap;
}

// 标签
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.tag-item {
  padding: 8rpx 24rpx;
  background: $tf-brand-bg;
  color: $tf-primary-color;
  font-size: 24rpx;
  border-radius: 24rpx;
}

// markdown样式
.markdown-wrapper {
  :deep(.t-chat-markdown) {
    background: transparent;
    padding: 0;

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

    p { font-size: 28rpx; line-height: 1.8; color: $tf-gray-700; }
    ul, ol { font-size: 28rpx; color: $tf-gray-700; padding-left: 32rpx; }
    li { margin-bottom: 8rpx; }
    strong { color: $tf-gray-900; }
    em { color: $tf-gray-600; }
  }
}

</style>
