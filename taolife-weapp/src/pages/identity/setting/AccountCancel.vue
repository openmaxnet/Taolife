<template>
  <view class="page-container">
    <TLTopBar title="账号注销" />

    <view class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <TLReload v-if="loadFailed" @reload="loadStatus" />
      <TLLoading v-else-if="loading" />
      <template v-else-if="status">
        <!-- 状态 0：正常 -->
        <template v-if="status.cancellationStatus === 0">
          <view class="notice-card">
            <text class="notice-title">注销须知</text>
            <view class="notice-list">
              <text class="notice-item">1. 注销后账号数据将无法恢复</text>
              <text class="notice-item">2. 提交后有 7 天静默期，期间可撤回</text>
              <text class="notice-item">3. 静默期结束后账号将被永久注销</text>
              <text class="notice-item">4. 注销后 AI 对话记录、方案、偏好等数据将清除</text>
              <text class="notice-item">5. 会员权益将在注销后失效</text>
            </view>
          </view>

          <view v-if="!status.canCancel" class="block-card">
            <text class="block-text">{{ status.blockReason }}</text>
          </view>

          <view class="action-area">
            <button
              class="btn-cancel"
              :disabled="!status.canCancel"
              @click="handleCancel"
            >
              申请注销
            </button>
          </view>
        </template>

        <!-- 状态 1：申请中 -->
        <template v-else-if="status.cancellationStatus === 1">
          <view class="pending-card">
            <view class="pending-icon i-solar:clock-circle-outline" />
            <text class="pending-title">注销申请已提交</text>
            <text class="pending-desc">
              你的账号将于 {{ formatDate(status.cancellationSchedule) }} 被注销。
              在此之前你可以随时撤回申请。
            </text>
          </view>
          <view class="action-area">
            <button class="btn-revoke" @click="handleRevoke">撤回注销</button>
          </view>
        </template>

        <!-- 状态 2：已注销 -->
        <template v-else-if="status.cancellationStatus === 2">
          <view class="done-card">
            <view class="done-icon i-solar:check-circle-bold" />
            <text class="done-title">账号已注销</text>
            <text class="done-desc">如需恢复使用，请联系客服。</text>
          </view>
        </template>
      </template>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLReload from '@/components/TLReload/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import { getCancelStatus, requestCancel, revokeCancel } from '@/api/identity/account'
import type { CancelCheckVO } from '@/api/identity/account'
import { usePageLayout } from '@/composables/usePageLayout'
import { useDetailLoader } from '@/composables/useDetailLoader'

const { contentPaddingTop } = usePageLayout()

const { data: status, isLoading: loading, loadFailed, load: loadStatus } = useDetailLoader<CancelCheckVO>(
  () => getCancelStatus()
)

const formatDate = (dateStr: string | null): string => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

const handleCancel = () => {
  uni.showModal({
    title: '确认注销',
    content: '确定要申请注销账号吗？提交后有 7 天静默期可撤回。',
    confirmColor: '#EF4444',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '提交中' })
          await requestCancel()
          uni.hideLoading()
          uni.showToast({ title: '申请已提交', icon: 'success' })
          loadStatus()
        } catch (e: any) {
          uni.hideLoading()
          uni.showToast({ title: e.message || '提交失败', icon: 'none' })
        }
      }
    },
  })
}

const handleRevoke = () => {
  uni.showModal({
    title: '撤回注销',
    content: '确定要撤回注销申请吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '撤回中' })
          await revokeCancel()
          uni.hideLoading()
          uni.showToast({ title: '已撤回', icon: 'success' })
          loadStatus()
        } catch (e: any) {
          uni.hideLoading()
          uni.showToast({ title: e.message || '撤回失败', icon: 'none' })
        }
      }
    },
  })
}

onMounted(() => loadStatus())
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
  min-height: 100vh;
}

.notice-card {
  margin: $tf-space-4;
  padding: $tf-space-5;
  background: $tf-surface;
  border-radius: $tf-radius-xl;
}

.notice-title {
  display: block;
  font-size: $tf-text-lg;
  color: $tf-gray-900;
  font-weight: 600;
  margin-bottom: $tf-space-3;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: $tf-space-2;
}

.notice-item {
  font-size: $tf-text-md;
  color: $tf-gray-600;
  line-height: 1.6;
}

.block-card {
  margin: 0 $tf-space-4 $tf-space-4;
  padding: $tf-space-4;
  background: #FEF3C7;
  border-radius: $tf-radius-lg;
}

.block-text {
  font-size: $tf-text-md;
  color: #D97706;
}

.action-area {
  padding: 0 $tf-space-4;
  margin-top: $tf-space-6;
}

.btn-cancel {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  background: #EF4444;
  color: #fff;
  font-size: $tf-text-md;
  font-weight: 600;
  border-radius: $tf-radius-xl;
  border: none;

  &[disabled] {
    background: $tf-gray-200;
    color: $tf-gray-400;
  }
}

.btn-revoke {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  background: $tf-surface;
  color: $tf-brand;
  font-size: $tf-text-md;
  font-weight: 600;
  border-radius: $tf-radius-xl;
  border: 2rpx solid $tf-brand;
}

.pending-card {
  margin: $tf-space-4;
  padding: $tf-space-8 $tf-space-5;
  background: $tf-surface;
  border-radius: $tf-radius-xl;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.pending-icon {
  font-size: 80rpx;
  color: #F59E0B;
  margin-bottom: $tf-space-4;
}

.pending-title {
  font-size: $tf-text-lg;
  color: $tf-gray-900;
  font-weight: 600;
  margin-bottom: $tf-space-3;
}

.pending-desc {
  font-size: $tf-text-md;
  color: $tf-gray-500;
  text-align: center;
  line-height: 1.6;
}

.done-card {
  margin: $tf-space-4;
  padding: $tf-space-8 $tf-space-5;
  background: $tf-surface;
  border-radius: $tf-radius-xl;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.done-icon {
  font-size: 80rpx;
  color: $tf-gray-400;
  margin-bottom: $tf-space-4;
}

.done-title {
  font-size: $tf-text-lg;
  color: $tf-gray-900;
  font-weight: 600;
  margin-bottom: $tf-space-3;
}

.done-desc {
  font-size: $tf-text-md;
  color: $tf-gray-500;
}
</style>
