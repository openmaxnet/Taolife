<template>
  <view class="page-container">

    <!-- 顶部导航栏 -->
    <TLTopBar title="体质辨识" :show-back="false">
      <template #left>
        <view class="left-actions">
          <view class="default-back" @click="goBack">
            <view class="back-icon i-solar:alt-arrow-left-outline" />
          </view>
          <view v-if="hasResult" class="left-btn" @click="goToHistory">
            <view class="left-btn-icon i-solar:clock-circle-outline" />
          </view>
        </view>
      </template>
    </TLTopBar>

    <!-- 加载失败组件 -->
    <TLReload
      v-if="loadFailed"
      title="加载失败"
      description="无法获取测评结果，请稍后重试"
      @reload="loadResult"
    />

    <!-- 正在加载 -->
    <view v-if="isLoading && !loadFailed" class="loading-state">
      <TLLoading />
    </view>

    <!-- 主内容区 -->
    <view v-if="!isLoading && !loadFailed" class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 页面头部 -->
      <view class="page-header">
        <text class="page-title">体质辨识</text>
        <text class="page-subtitle">基于《体质分类与判定》标准</text>
      </view>

      <!-- 介绍卡片 -->
      <view class="intro-card">
        <text class="intro-title">什么是体质？</text>
        <text class="intro-text">体质是人体在先天禀赋和后天调养基础上形成的相对稳定的生理特性和病理倾向。中医将体质分为九种类型，不同体质有不同的养生方法。</text>
      </view>

      <!-- 结果展示区 -->
      <view v-if="hasResult" class="result-content">
        <view class="result-header">
          <text class="result-label">您的体质类型是</text>
          <text class="result-name">{{ result?.constitutionName }}</text>
          <view class="confidence-badge">
            <text class="confidence-text">置信度 {{ result?.confidence }}%</text>
          </view>
        </view>

        <view class="result-card">
          <view
            v-for="section in resultSections"
            :key="section.key"
            class="result-section"
          >
            <text class="section-title">{{ section.title }}</text>
            <text class="section-content">{{ section.content }}</text>
          </view>
        </view>

        <view class="result-actions">
          <button class="btn-retest" @click="goToQuestionnaire">重新测评</button>
        </view>
      </view>

      <!-- 空状态 -->
      <TLEmpty
        v-else
        title="暂无测评结果"
        description="完成测评后可查看结果"
        button-text="开始测评"
        @click="goToQuestionnaire"
      />

    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLReload from '@/components/TLReload/index.vue'
import TLEmpty from '@/components/TLEmpty/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { useDetailLoader } from '@/composables/useDetailLoader'
import { enAuth } from '@/utils/authManager'
import { getLatestResult } from '@/api/wisdom/constitution'
import type { ConstitutionResult } from '@/types/biz/ai/constitution'


const { contentPaddingTop } = usePageLayout()

const { data: result, isLoading, loadFailed, load: loadResult } = useDetailLoader<ConstitutionResult>(
  () => getLatestResult()
)

// 判断是否有有效结果
const hasResult = computed(() => {
  return result.value && result.value.recordId
})

// 结果展示 sections
const resultSections = computed(() => {
  if (!result.value) return []

  const sections = [
    { key: 'characteristics', title: '体质特征', content: result.value?.characteristics },
    { key: 'description', title: '体质描述', content: result.value?.description },
    { key: 'dietGuidance', title: '饮食指导', content: result.value?.dietGuidance },
    { key: 'exerciseGuidance', title: '运动建议', content: result.value?.exerciseGuidance },
    { key: 'emotionGuidance', title: '情志调节', content: result.value?.emotionGuidance },
    { key: 'acupointGuidance', title: '穴位保健', content: result.value?.acupointGuidance },
  ]

  return sections.filter(s => s.content)
})

// 页面加载
onMounted(async () => {
  await enAuth()
  await loadResult()
})

// 返回上一页
const goBack = () => {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack()
  } else {
    uni.switchTab({
      url: '/pages/index/index',
      fail: () => uni.navigateTo({ url: '/pages/index/index' }),
    })
  }
}

// 跳转到历史记录页面
const goToHistory = () => {
  uni.navigateTo({
    url: '/pages/wisdom/constitution/AssessHistory'
  })
}

// 跳转到问卷页面
const goToQuestionnaire = () => {
  uni.navigateTo({
    url: '/pages/wisdom/constitution/AssessQuestionnaire'
  })
}
</script>

<style lang="scss">
.page-container {
  padding-bottom: $tf-space-20;
}

.page-header {
  padding: $tf-space-12 $tf-space-6 $tf-space-8;
  text-align: center;
}

.page-title {
  display: block;
  font-size: $tf-text-4xl;
  color: $tf-brand-dark;
  font-weight: bold;
}

.page-subtitle {
  display: block;
  font-size: $tf-text-sm;
  color: $tf-gray-500;
  margin-top: $tf-space-3;
}

.content-area {
  padding-bottom: $tf-space-8;
}

.intro-card {
  background: linear-gradient(135deg, $tf-brand-bg 0%, var(--tf-brand-alpha-15) 100%);
  border-radius: $tf-radius-2xl;
  padding: $tf-space-8;
  margin: 0 $tf-space-6 $tf-space-8;
}

.intro-title {
  display: block;
  font-size: $tf-text-lg;
  color: $tf-brand-dark;
  font-weight: bold;
  margin-bottom: $tf-space-4;
}

.intro-text {
  display: block;
  font-size: $tf-text-md;
  color: $tf-brand-700;
  line-height: 1.6;
}

.result-content {
  padding: 0 $tf-space-6;
}

.result-header {
  text-align: center;
  padding: $tf-space-12 $tf-space-8;
  background: linear-gradient(135deg, $tf-brand-bg 0%, var(--tf-brand-alpha-15) 100%);
  border-radius: $tf-radius-2xl;
  margin-bottom: $tf-space-8;
}

.result-label {
  display: block;
  font-size: $tf-text-lg;
  color: $tf-brand-dark;
  margin-bottom: $tf-space-4;
}

.result-name {
  display: block;
  font-size: $tf-text-5xl;
  color: $tf-brand-700;
  font-weight: bold;
  margin-bottom: $tf-space-4;
}

.confidence-badge {
  display: inline-block;
  background: $tf-surface;
  padding: $tf-space-2 $tf-space-6;
  border-radius: $tf-radius-3xl;
}

.confidence-text {
  font-size: $tf-text-sm;
  color: $tf-brand;
}

.result-card {
  background: $tf-surface;
  border-radius: $tf-radius-2xl;
  padding: $tf-space-8;
}

.result-section {
  margin-bottom: $tf-space-8;
}

.result-section:last-child {
  margin-bottom: 0;
}

.section-title {
  display: block;
  font-size: $tf-text-lg;
  color: $tf-brand;
  font-weight: bold;
  margin-bottom: $tf-space-3;
}

.section-content {
  display: block;
  font-size: $tf-text-md;
  color: $tf-gray-700;
  line-height: 1.6;
}

.result-actions {
  display: flex;
  justify-content: center;
  margin-top: $tf-space-8;
}

.btn-retest {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: $tf-radius-3xl;
  font-size: $tf-text-xl;
  font-weight: 500;
  background: $tf-surface;
  color: $tf-brand;
  border: 2rpx solid $tf-brand-light;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 600rpx;
}

.left-actions {
  display: flex;
  align-items: center;
}

.default-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  width: 44rpx;
  height: 44rpx;
  color: $tf-gray-900;
}

.left-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: $tf-space-1;
}

.left-btn-icon {
  width: 44rpx;
  height: 44rpx;
  color: $tf-gray-900;
}

</style>
