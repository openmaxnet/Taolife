<template>
  <view class="page-container">
    <TLTopBar title="兑换记录" />

    <view class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <view v-if="records.length === 0" class="empty">
        <view class="empty-icon-wrap">
          <view class="i-solar:bill-list-outline empty-icon" />
        </view>
        <text class="empty-text">暂无兑换记录</text>
      </view>

      <view v-else class="record-list">
        <view v-for="item in records" :key="item.id" class="record-card">
          <view class="record-header">
            <view class="record-name-row">
              <view :class="getRecordIcon(item.goodsType)" class="record-icon" />
              <text class="record-name">{{ item.goodsName }}</text>
            </view>
            <text class="record-status" :class="getStatusClass(item.status)">
              {{ getStatusText(item.status) }}
            </text>
          </view>
          <view class="record-detail">
            <text class="record-cost">-{{ item.pointsCost }}积分</text>
            <text class="record-time">{{ formatTime(item.createTime) }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { getExchangeRecords } from '@/api/fee/points'
import type { PointsExchangeVO } from '@/types/biz/fee/points'

const { contentPaddingTop } = usePageLayout()

const records = ref<PointsExchangeVO[]>([])

onMounted(async () => {
  try {
    const data = await getExchangeRecords()
    records.value = data ?? []
  } catch { /* ignore */ }
})

const formatTime = (time: string) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

const getRecordIcon = (type: number): string => {
  const icons = ['i-solar:chat-round-dots-outline', 'i-solar:star-bold', 'i-solar:gift-bold']
  return icons[type] || icons[0]
}

const getStatusClass = (status: number): string =>
  status === 1 ? 'status-success' : status === 2 ? 'status-refund' : 'status-pending'

const getStatusText = (status: number): string => {
  const texts = ['处理中', '成功', '已退还']
  return texts[status] || '未知'
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background-color: $tf-page-bg-color;
}

.content-area {
  padding-bottom: env(safe-area-inset-bottom);
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 200rpx;
}

.empty-icon-wrap {
  width: 96rpx;
  height: 96rpx;
  border-radius: $tf-radius-pill;
  background: $tf-brand-bg;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $tf-space-5;
}

.empty-icon {
  width: 48rpx;
  height: 48rpx;
  color: $tf-brand;
}

.empty-text {
  color: $tf-gray-500;
  font-size: $tf-text-lg;
}

.record-list {
  padding: $tf-space-6;
  display: flex;
  flex-direction: column;
  gap: $tf-space-4;
}

.record-card {
  background: $tf-surface;
  border-radius: $tf-radius-xl;
  padding: $tf-space-6;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $tf-space-3;
}

.record-name-row {
  display: flex;
  align-items: center;
  gap: $tf-space-2;
}

.record-icon {
  width: 32rpx;
  height: 32rpx;
  color: $tf-brand;
}

.record-name {
  font-size: $tf-text-lg;
  font-weight: 500;
  color: $tf-gray-900;
}

.record-status {
  font-size: $tf-text-sm;
  font-weight: 500;
  padding: $tf-space-1 $tf-space-3;
  border-radius: $tf-radius-2xl;

  &.status-success {
    color: $tf-brand;
    background: $tf-brand-bg;
  }

  &.status-pending {
    color: $tf-warning-dark;
    background: $tf-warning-bg;
  }

  &.status-refund {
    color: $tf-gray-500;
    background: $tf-gray-100;
  }
}

.record-detail {
  display: flex;
  justify-content: space-between;
}

.record-cost {
  font-size: $tf-text-base;
  color: $tf-warning-dark;
  font-weight: 500;
}

.record-time {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
}
</style>
