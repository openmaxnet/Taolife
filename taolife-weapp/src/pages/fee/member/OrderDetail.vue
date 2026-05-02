<template>
  <view class="page-container">
    <TLTopBar title="订单详情" />

    <view class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <!-- 加载中 -->
      <TLLoading v-if="loading" />

      <template v-else>
        <!-- 订单状态 -->
        <view class="status-card" :class="getStatusClass(order.status)">
          <text class="status-icon">{{ getStatusIcon(order.status) }}</text>
          <text class="status-text">{{ order.statusDesc }}</text>
          <text v-if="order.status === 0" class="expire-hint">请在{{ formatExpireTime(order.expireTime) }}前完成支付</text>
        </view>

        <!-- 订单信息 -->
        <view class="info-section">
          <text class="section-title">订单信息</text>
          <view class="info-card">
            <view class="info-row">
              <text class="info-label">订单编号</text>
              <text class="info-value">{{ order.orderNo }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">套餐名称</text>
              <text class="info-value">{{ order.planName || '会员套餐' }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">会员等级</text>
              <text class="info-value">{{ getLevelName(order.memberLevel) }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">有效期</text>
              <text class="info-value">{{ order.durationDays === 0 ? '永久' : `${order.durationDays}天` }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">下单时间</text>
              <text class="info-value">{{ formatDateTime(order.createTime) }}</text>
            </view>
            <view v-if="order.paidTime" class="info-row">
              <text class="info-label">支付时间</text>
              <text class="info-value">{{ formatDateTime(order.paidTime) }}</text>
            </view>
          </view>
        </view>

        <!-- 支付信息 -->
        <view class="info-section">
          <text class="section-title">支付信息</text>
          <view class="info-card">
            <view class="info-row">
              <text class="info-label">订单金额</text>
              <text class="info-value">¥{{ (order.orderAmount / 100).toFixed(2) }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">实付金额</text>
              <text class="info-value highlight">¥{{ (order.payAmount / 100).toFixed(2) }}</text>
            </view>
            <view v-if="order.wxTransactionId" class="info-row">
              <text class="info-label">交易单号</text>
              <text class="info-value">{{ order.wxTransactionId }}</text>
            </view>
          </view>
        </view>
      </template>
    </view>

    <!-- 底部操作 -->
    <view v-if="order.status === 0" class="footer-action">
      <button class="cancel-btn" @tap="handleCancelOrder">取消订单</button>
      <button class="pay-btn" @tap="goPay">去支付</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import { getOrderById, getOrderStatus, cancelOrder, createOrder } from '@/api/fee/order'
import type { PayOrderVO } from '@/types/biz/fee/member'
import { usePageLayout } from '@/composables/usePageLayout'
import { getPageOptions } from '@/utils/router'

const { contentPaddingTop } = usePageLayout()

const loading = ref(true)

const order = ref<PayOrderVO>({
  id: '',
  orderNo: '',
  accountId: '',
  memberPlanId: '',
  planCode: '',
  planName: '',
  memberLevel: 0,
  durationDays: 0,
  orderAmount: 0,
  payAmount: 0,
  status: 0,
  statusDesc: '',
  wxPrepayId: '',
  wxTransactionId: '',
  paidTime: '',
  expireTime: '',
  createTime: '',
})

onMounted(async () => {
  const options = getPageOptions()

  try {
    let result: PayOrderVO | undefined
    if (options.orderNo) {
      result = await getOrderStatus(options.orderNo)
    } else if (options.id) {
      result = await getOrderById(options.id)
    }
    if (result) {
      order.value = result
    }
  } catch {
    uni.showToast({ title: '订单不存在', icon: 'none' })
  } finally {
    loading.value = false
  }
})

const handleCancelOrder = async () => {
  uni.showModal({
    title: '提示',
    content: '确定要取消该订单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await cancelOrder(order.value.orderNo)
          uni.showToast({ title: '已取消', icon: 'success' })
          order.value.status = 2
          order.value.statusDesc = '已取消'
        } catch {
          uni.showToast({ title: '取消失败', icon: 'none' })
        }
      }
    },
  })
}

const goPay = async () => {
  if (!order.value.memberPlanId) return

  try {
    uni.showLoading({ title: '创建支付...' })
    const orderRes = await createOrder({
      planCode: order.value.planCode,
      wxOpenid: '',
    })
    uni.hideLoading()

    const orderData = orderRes
    if (orderData?.prepayId && orderData?.paySign) {
      uni.requestPayment({
        provider: 'wxpay',
        timeStamp: orderData.timeStamp,
        nonceStr: orderData.nonceStr,
        package: orderData.packageStr,
        signType: orderData.signType as 'RSA',
        paySign: orderData.paySign,
        success: () => {
          uni.showToast({ title: '支付成功', icon: 'success' })
          setTimeout(() => uni.navigateBack(), 1500)
        },
        fail: (err) => {
          if (err.errMsg?.includes('cancel')) {
            uni.showToast({ title: '支付取消', icon: 'none' })
          } else {
            uni.showToast({ title: '支付失败', icon: 'none' })
          }
        },
      })
    } else {
      uni.showToast({ title: '支付服务待配置', icon: 'none' })
    }
  } catch {
    uni.hideLoading()
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

const getStatusClass = (status: number): string => {
  const classes = ['pending', 'paid', 'cancelled', 'refunded', 'closed']
  return classes[status] || ''
}

const getStatusIcon = (status: number): string => {
  const icons = ['⏳', '✅', '❌', '💰', '🔒']
  return icons[status] || '📋'
}

const getLevelName = (level: number): string => {
  const names = ['普通用户', '月卡会员', '年卡会员', '终身会员']
  return names[level] || '未知'
}

const formatDateTime = (dateStr: string): string => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatExpireTime = (dateStr: string): string => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: $tf-page-bg-color;
  padding-bottom: 180rpx;
}

.content-area {
  padding-left: $tf-space-6;
  padding-right: $tf-space-6;
}

.status-card {
  padding: $tf-space-12;
  background: $tf-surface;
  border-radius: $tf-radius-xl;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-bottom: $tf-space-6;

  &.pending {
    background: $tf-warning-bg;
  }

  &.paid {
    background: $tf-brand-bg;
  }

  &.cancelled,
  &.closed {
    background: $tf-gray-100;
  }

  &.refunded {
    background: linear-gradient(135deg, #FFF1F2, #FFE4E6);
  }
}

.status-icon {
  font-size: $tf-text-5xl;
  margin-bottom: $tf-space-4;
}

.status-text {
  font-size: $tf-text-2xl;
  font-weight: bold;
  color: $tf-gray-900;
}

.expire-hint {
  font-size: $tf-text-base;
  color: $tf-warning;
  margin-top: $tf-space-2;
}

.info-section {
  margin-bottom: $tf-space-6;
}

.section-title {
  font-size: $tf-text-lg;
  font-weight: bold;
  color: $tf-gray-900;
  margin-bottom: $tf-space-4;
}

.info-card {
  background: $tf-surface;
  border-radius: $tf-radius-lg;
  padding: $tf-space-2 0;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: $tf-space-5 $tf-space-7;
  border-bottom: 1rpx solid $tf-gray-100;

  &:last-child {
    border-bottom: none;
  }
}

.info-label {
  font-size: $tf-text-md;
  color: $tf-gray-500;
}

.info-value {
  font-size: $tf-text-md;
  color: $tf-gray-900;

  &.highlight {
    color: $tf-warning-dark;
    font-weight: bold;
  }
}

.footer-action {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: $tf-surface;
  padding: $tf-space-6;
  padding-bottom: calc(#{$tf-space-6} + constant(safe-area-inset-bottom));
  padding-bottom: calc(#{$tf-space-6} + env(safe-area-inset-bottom));
  display: flex;
  gap: $tf-space-6;
  box-shadow: $tf-shadow-bottom;
}

.cancel-btn {
  flex: 1;
  padding: $tf-space-6;
  background: $tf-gray-100;
  color: $tf-gray-700;
  border-radius: $tf-radius-4xl;
  font-size: $tf-text-lg;
  border: none;
}

.pay-btn {
  flex: 2;
  padding: $tf-space-6;
  background: $tf-gradient-brand;
  color: $tf-surface;
  border-radius: $tf-radius-4xl;
  font-size: $tf-text-lg;
  font-weight: bold;
  border: none;
}
</style>
