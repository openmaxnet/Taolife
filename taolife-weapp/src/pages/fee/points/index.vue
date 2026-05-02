<template>
  <view class="page-container">
    <TLTopBar title="积分商城" />

    <view class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <!-- 积分余额卡片 -->
      <view class="points-card">
        <button class="record-btn" @tap="goExchangeRecord">兑换记录</button>
        <view class="points-info">
          <view class="points-row">
            <text class="points-value">{{ summary.availablePoints ?? 0 }}</text>
            <text class="points-unit">可用</text>
          </view>
        </view>
        <view class="points-stats">
          <view class="stat-item">
            <text class="stat-value">{{ summary.todayEarned ?? 0 }}</text>
            <text class="stat-label">今日获得</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">LV{{ summary.userLevel ?? 1 }}</text>
            <text class="stat-label">当前等级</text>
          </view>
        </view>
      </view>

      <!-- 积分商品 -->
      <view class="goods-section">
        <view class="section-header">
          <view class="section-line" />
          <view class="section-tag">
            <view class="i-solar:gift-bold section-tag-icon" />
            <text>积分兑换</text>
          </view>
          <view class="section-line" />
        </view>

        <view class="goods-grid">
          <view v-for="item in goodsList" :key="item.goodsCode" class="goods-card">
            <view class="goods-icon-wrap">
              <view :class="getGoodsIcon(item.goodsType)" class="goods-icon" />
            </view>
            <text class="goods-name">{{ item.goodsName }}</text>
            <text class="goods-desc">{{ item.description }}</text>
            <text class="goods-price">{{ item.pointsRequired }}<text class="goods-price-unit">积分</text></text>
            <button class="exchange-btn" :disabled="exchanging" @tap="handleExchange(item)">立即兑换</button>
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
import { getPointsGoodsList, exchangePoints, getPointsSummary } from '@/api/fee/points'
import type { PointsGoodsVO, PointsSummaryVO } from '@/types/biz/fee/points'

const { contentPaddingTop } = usePageLayout()

const goodsList = ref<PointsGoodsVO[]>([])
const summary = ref<PointsSummaryVO>({ availablePoints: 0, todayEarned: 0, userLevel: 1 })
const exchanging = ref(false)

onMounted(async () => {
  await Promise.all([loadGoods(), loadSummary()])
})

const loadGoods = async () => {
  try {
    const data = await getPointsGoodsList()
    goodsList.value = data ?? []
  } catch { /* ignore */ }
}

const loadSummary = async () => {
  try {
    const data = await getPointsSummary()
    summary.value = data ?? summary.value
  } catch { /* ignore */ }
}

const handleExchange = async (item: PointsGoodsVO) => {
  if (exchanging.value) return
  exchanging.value = true
  try {
    await exchangePoints(item.goodsCode)
    uni.showToast({ title: '兑换成功', icon: 'success' })
    await loadSummary()
  } catch (err: any) {
    uni.showToast({ title: err?.data?.msg ?? '兑换失败', icon: 'none' })
  } finally {
    exchanging.value = false
  }
}

const goExchangeRecord = () => {
  uni.navigateTo({ url: '/pages/fee/points/ExchangeRecord' })
}

const getGoodsIcon = (type: number): string => {
  const icons = ['i-solar:chat-round-dots-outline', 'i-solar:star-bold', 'i-solar:gift-bold']
  return icons[type] || icons[0]
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

// 积分卡片
.points-card {
  position: relative;
  margin: $tf-space-6;
  padding: $tf-space-8;
  background: $tf-gradient-brand;
  border-radius: $tf-radius-2xl;
  color: $tf-surface;
}

.record-btn {
  position: absolute;
  top: $tf-space-6;
  right: $tf-space-6;
  padding: $tf-space-1 $tf-space-4;
  background: rgba(255, 255, 255, 0.25);
  color: $tf-surface;
  border-radius: $tf-radius-3xl;
  font-size: $tf-text-xs;
  font-weight: 500;
  border: none;
  line-height: 1.4;
}

.points-info {
  margin-bottom: $tf-space-6;
}

.points-row {
  display: flex;
  align-items: baseline;
  gap: $tf-space-2;
}

.points-value {
  font-size: $tf-text-5xl;
  font-weight: bold;
}

.points-unit {
  font-size: $tf-text-lg;
  opacity: 0.85;
}

.points-stats {
  display: flex;
  gap: $tf-space-10;
  padding-top: $tf-space-6;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: $tf-text-xl;
  font-weight: bold;
}

.stat-label {
  font-size: $tf-text-sm;
  opacity: 0.7;
  margin-top: $tf-space-1;
}

// 标题
.section-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20rpx;
  margin: $tf-space-8 $tf-space-6 $tf-space-6;
}

.section-line {
  width: 120rpx;
  height: 1rpx;
  background: linear-gradient(to right, transparent, $tf-brand, transparent);
}

.section-tag {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: $tf-text-sm;
  color: $tf-brand;
  background-color: var(--tf-brand-alpha-8);
  padding: 8rpx 24rpx;
  border-radius: $tf-radius-2xl;
}

.section-tag-icon {
  width: 28rpx;
  height: 28rpx;
  color: $tf-brand;
}

// 商品两列网格
.goods-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $tf-space-4;
  padding: 0 $tf-space-6;
}

.goods-card {
  background: $tf-surface;
  border-radius: $tf-radius-xl;
  padding: $tf-space-6;
  display: flex;
  flex-direction: column;
}

.goods-icon-wrap {
  width: 72rpx;
  height: 72rpx;
  border-radius: $tf-radius-lg;
  background: $tf-brand-bg;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $tf-space-5;
}

.goods-icon {
  width: 36rpx;
  height: 36rpx;
  color: $tf-brand;
}

.goods-name {
  font-size: $tf-text-lg;
  font-weight: bold;
  color: $tf-gray-900;
  margin-bottom: $tf-space-2;
}

.goods-desc {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
  margin-bottom: $tf-space-5;
  flex: 1;
}

.goods-price {
  font-size: $tf-text-2xl;
  color: $tf-warning-dark;
  font-weight: bold;
  margin-bottom: $tf-space-5;
}

.goods-price-unit {
  font-size: $tf-text-xs;
  font-weight: normal;
  margin-left: 4rpx;
}

.exchange-btn {
  width: 100%;
  padding: $tf-space-3 0;
  background: $tf-gradient-brand;
  color: $tf-surface;
  border-radius: $tf-radius-3xl;
  font-size: $tf-text-sm;
  font-weight: 500;
  border: none;
  line-height: 1.4;

  &[disabled] {
    opacity: 0.5;
  }
}
</style>
