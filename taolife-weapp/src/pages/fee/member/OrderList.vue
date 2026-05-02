<template>
  <view class="page-container">
    <TLTopBar title="我的订单" />

    <view class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <!-- 订单列表 -->
      <view class="order-list">
        <view v-for="order in orders" :key="order.id" class="order-card" @tap="goDetail(order)">
          <view class="order-header">
            <text class="order-no">{{ order.orderNo }}</text>
            <text class="order-status" :class="getStatusClass(order.status)">
              {{ order.statusDesc }}
            </text>
          </view>
          <view class="order-info">
            <text class="plan-name">{{ order.planName || '会员套餐' }}</text>
            <text class="order-time">{{ formatDate(order.createTime) }}</text>
          </view>
          <view class="order-footer">
            <text class="order-amount">¥{{ (order.payAmount / 100).toFixed(2) }}</text>
            <text class="order-action">{{ getActionText(order.status) }}</text>
          </view>
        </view>

        <!-- 空状态 -->
        <TLEmpty
          v-if="orders.length === 0 && !loading"
          title="暂无订单"
          description="购买会员后订单将在这里显示"
        />

        <!-- 加载中 -->
        <TLLoading v-if="loading" />
      </view>

      <!-- 分页加载 -->
      <view v-if="hasMore && !loading" class="load-more" @tap="loadMore">
        <text>加载更多</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLEmpty from '@/components/TLEmpty/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import { getOrderPage } from '@/api/fee/order'
import type { PayOrderVO } from '@/types/biz/fee/member'
import { usePageLayout } from '@/composables/usePageLayout'

const { contentPaddingTop } = usePageLayout()

const orders = ref<PayOrderVO[]>([])
const loading = ref(false)
const hasMore = ref(true)
const pageNo = ref(1)
const pageSize = 10

onMounted(async () => {
  await loadOrders()
})

const loadOrders = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const data = await getOrderPage({ pageNo: pageNo.value, pageSize })
    if (data) {
      if (pageNo.value === 1) {
        orders.value = data.list ?? []
      } else {
        orders.value.push(...(data.list ?? []))
      }
      hasMore.value = data.list?.length === pageSize
    }
  } catch { /* ignore */ } finally {
    loading.value = false
  }
}

const loadMore = async () => {
  if (!hasMore.value) return
  pageNo.value++
  await loadOrders()
}

const goDetail = (order: PayOrderVO) => {
  uni.navigateTo({ url: `/pages/fee/member/OrderDetail?orderNo=${order.orderNo}` })
}

const getStatusClass = (status: number): string => {
  const classes = ['pending', 'paid', 'cancelled', 'refunded', 'closed']
  return classes[status] || ''
}

const getActionText = (status: number): string => {
  const texts = ['去支付', '查看详情', '已取消', '退款详情', '已关闭']
  return texts[status] || '查看详情'
}

const formatDate = (dateStr: string): string => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: $tf-page-bg-color;
  padding-bottom: env(safe-area-inset-bottom);
}

.content-area {
  padding-left: $tf-space-6;
  padding-right: $tf-space-6;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: $tf-space-5;
}

.order-card {
  background: $tf-surface;
  border-radius: $tf-radius-lg;
  padding: $tf-space-7;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $tf-space-4;
}

.order-no {
  font-size: $tf-text-base;
  color: $tf-gray-500;
}

.order-status {
  font-size: $tf-text-base;
  font-weight: 500;

  &.pending {
    color: $tf-warning;
  }

  &.paid {
    color: $tf-brand-light;
  }

  &.cancelled,
  &.closed {
    color: $tf-gray-500;
  }

  &.refunded {
    color: $tf-warning-dark;
  }
}

.order-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $tf-space-4;
}

.plan-name {
  font-size: $tf-text-xl;
  font-weight: 500;
  color: $tf-gray-900;
}

.order-time {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: $tf-space-4;
  border-top: 1rpx solid $tf-gray-200;
}

.order-amount {
  font-size: $tf-text-2xl;
  font-weight: bold;
  color: $tf-warning-dark;
}

.order-action {
  font-size: $tf-text-base;
  color: $tf-brand;
}

.load-more {
  text-align: center;
  padding: $tf-space-8;
  color: $tf-brand;
  font-size: $tf-text-lg;
}
</style>
