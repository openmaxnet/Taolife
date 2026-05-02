<template>
  <view class="page-container">
    <!-- 顶部导航栏 -->
    <TLTopBar title="分享方案" :show-back="true" @back="handleBack" />

    <!-- 主内容区 -->
    <view class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 方案摘要输入区（无外包围） -->
      <textarea
        v-model="planSummary"
        class="summary-input"
        placeholder="描述一下你的方案要点，让更多人了解..."
        :maxlength="2000"
      />
      <text class="char-count">{{ planSummary.length }} / 2000</text>

      <!-- 方案标题展示区 -->
      <view class="today-focus-card">
        <view class="i-solar:bookmark-opened-outline focus-icon" />
        <text class="focus-content">{{ planTitle }}</text>
      </view>

      <!-- 固定底部区域：标签 + 提交按钮 -->
      <view class="fixed-bottom-area">
        <!-- 标签选择区 -->
        <view class="tags-section">
          <text class="tags-title">方案标签</text>
          <view class="tags-container">
            <view
              v-for="(tag, index) in selectedTags"
              :key="index"
              class="tag-item"
              @click="removeTag(index)"
            >
              <text class="tag-text">{{ tag }}</text>
              <view class="i-solar:close-circle-outline tag-remove-icon" />
            </view>
            <text v-if="selectedTags.length === 0" class="tags-empty">已移除所有标签</text>
          </view>
        </view>

        <!-- 提交按钮 -->
        <view class="submit-btn" :class="{ 'submit-btn-disabled': isSubmitting }" @click="handleSubmit">
          <text class="submit-btn-text">{{ isSubmitting ? '提交中...' : '确认分享' }}</text>
        </view>
      </view>

    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { getPageOptions } from '@/utils/router'
import { shareToSquare } from '@/api/plan/plan'

// 页面布局
const { contentPaddingTop } = usePageLayout()

// 页面参数
const planId = ref('')
const planTitle = ref('')
const selectedTags = ref<string[]>([])

// 表单数据
const planSummary = ref('')
const isSubmitting = ref(false)

/**
 * 移除标签
 */
const removeTag = (index: number) => {
  selectedTags.value.splice(index, 1)
}

/**
 * 提交分享
 */
const handleSubmit = async () => {
  if (isSubmitting.value) return
  if (!planId.value) {
    uni.showToast({ title: '方案信息缺失', icon: 'none' })
    return
  }

  isSubmitting.value = true
  try {
    await shareToSquare({
      userPlanId: planId.value,
      planSummary: planSummary.value,
      planTags: JSON.stringify(selectedTags.value)
    })
    uni.showToast({ title: '分享成功', icon: 'success' })
    setTimeout(() => {
      uni.redirectTo({ url: '/pages/plan/index' })
    }, 1500)
  } catch (error: any) {
    uni.showToast({ title: error?.message || '分享失败', icon: 'none' })
  } finally {
    isSubmitting.value = false
  }
}

/**
 * 返回上一页
 */
const handleBack = () => {
  uni.navigateBack()
}

// 页面加载
onMounted(() => {
  // 获取页面参数
  const options = getPageOptions()

  planId.value = options.planId || ''
  planTitle.value = decodeURIComponent(options.planTitle || '我的养生方案')

  // 解析标签
  try {
    const tags = decodeURIComponent(options.planTags || '[]')
    selectedTags.value = JSON.parse(tags)
  } catch {
    selectedTags.value = []
  }
})
</script>

<script lang="ts">
import { onMounted } from 'vue'
export default {}
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
}

.content-area {
  padding: 0 24rpx 24rpx 24rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

// 摘要输入（无外包围）
.summary-input {
  width: 100%;
  min-height: 160rpx;
  font-size: 28rpx;
  color: $tf-gray-900;
  line-height: 1.6;
  background: $tf-gray-50;
  border-radius: 12rpx;
  padding: 20rpx;
  box-sizing: border-box;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 22rpx;
  color: $tf-gray-500;
  margin-top: -12rpx;
}

// 今日重点卡片
.today-focus-card {
  background: $tf-gradient-brand;
  border-radius: 20rpx;
  padding: 28rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.focus-icon {
  font-size: 32rpx;
  color: rgba(255, 255, 255, 0.9);
}

.focus-content {
  font-size: 28rpx;
  color: $tf-surface;
  font-weight: 600;
  line-height: 1.4;
}

// 固定底部区域
.fixed-bottom-area {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: $tf-surface;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

// 标签区
.tags-section {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.tags-title {
  font-size: 26rpx;
  color: $tf-gray-600;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.tag-item {
  display: inline-flex;
  align-items: center;
  gap: 6rpx;
  padding: 8rpx 16rpx;
  background: $tf-brand-bg-light;
  border: 1rpx solid $tf-gradient-from;
  border-radius: 999rpx;
}

.tag-text {
  font-size: 24rpx;
  color: $tf-primary-color;
}

.tag-remove-icon {
  font-size: 18rpx;
  color: $tf-primary-color;
}

.tags-empty {
  font-size: 24rpx;
  color: $tf-gray-500;
}

// 提交按钮
.submit-btn {
  background: $tf-gradient-brand;
  border-radius: 40rpx;
  padding: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-btn-disabled {
  opacity: 0.6;
}

.submit-btn-text {
  font-size: 30rpx;
  color: $tf-surface;
  font-weight: 600;
}
</style>
