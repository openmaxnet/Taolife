<template>
  <view class="page-container">
    <TLTopBar title="会员中心" />

    <TLLoading v-if="isLoading" :style="{ paddingTop: contentPaddingTop + 'px' }" />

    <view v-else class="content" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- Section 1: Hero Card -->
      <view class="hero-card">
        <view class="hero-bg-icon i-solar:crown-star-outline" />

        <view class="hero-top-row">
          <view class="hero-level-info" v-if="isActiveMember && currentGrowthLevel">
            <text class="hero-level-badge" :style="{ color: currentGrowthColor }">V{{ currentGrowthLevel.level }}</text>
            <text class="hero-level-name">{{ currentGrowthLevel.levelName }}</text>
          </view>
          <text v-else-if="isActiveMember" class="hero-level-name">{{ memberLevelName }}</text>
          <text v-else class="hero-level-name">开通会员享更多权益</text>
          <view class="hero-record-entry" v-if="isActiveMember" @tap="goToGrowthRecord">
            <text class="hero-record-text">成长记录</text>
            <text class="i-solar:arrow-right-linear hero-record-icon" />
          </view>
        </view>

        <!-- Growth progress (members only) -->
        <view v-if="isActiveMember && growthDetail" class="hero-growth-section">
          <view class="hero-growth-display">
            <text class="hero-growth-num" :style="{ color: currentGrowthColor }">{{ growthDetail.growthValue ?? 0 }}</text>
            <text class="hero-growth-unit">成长值</text>
          </view>
          <view class="hero-progress" v-if="!isMaxLevel">
            <view class="hero-progress-bar">
              <view class="hero-progress-fill" :style="{ width: progressWidth + '%', background: currentGrowthColor }" />
            </view>
            <text class="hero-progress-label">
              距离 <text :style="{ color: currentGrowthColor }">{{ nextGrowthLevel?.levelName }}</text> 还需
              <text :style="{ color: currentGrowthColor }">{{ growthToNext }}</text> 成长值
            </text>
          </view>
          <view v-else class="hero-max-level">
            <text class="hero-max-text" :style="{ color: currentGrowthColor }">已达到最高等级</text>
          </view>
        </view>

        <!-- Member expire info -->
        <view v-if="isActiveMember && memberExpireText" class="hero-expire-row">
          <text class="hero-expire-text">{{ memberLevelName }} · {{ memberExpireText }}</text>
        </view>

        <!-- Metric pills -->
        <view v-if="memberStatus" class="hero-metrics">
          <view class="hero-metric-pill">
            <text class="i-solar:chat-round-dots-outline hero-metric-icon" />
            <text class="hero-metric-text">AI {{ memberStatus.aiQuotaUsed ?? 0 }}/{{ memberStatus.aiQuotaTotal ?? 0 }}</text>
          </view>
          <view class="hero-metric-pill">
            <text class="i-solar:stars-outline hero-metric-icon" />
            <text class="hero-metric-text">积分×{{ memberStatus.pointsMultiplier ?? 1 }}</text>
          </view>
          <view class="hero-metric-pill">
            <text class="i-solar:bag-4-outline hero-metric-icon" />
            <text class="hero-metric-text">商城{{ ((memberStatus.storeDiscount ?? 1) * 10).toFixed(1) }}折</text>
          </view>
        </view>
      </view>

      <!-- Section 2: Plan Selection -->
      <view class="section" v-if="plans.length > 0">
        <view class="section-header">
          <view class="section-line section-line--brand" />
          <view class="section-tag section-tag--brand">
            <text class="i-solar:crown-star-outline section-tag-icon" />
            <text>{{ isActiveMember ? '续费套餐' : '选择套餐' }}</text>
          </view>
          <view class="section-line section-line--brand" />
        </view>
        <scroll-view scroll-x :show-scrollbar="false" class="plan-scroll">
          <view class="plan-track">
            <view
              v-for="plan in plans"
              :key="plan.id"
              class="plan-card"
              :class="{ active: selectedPlan?.id === plan.id }"
              @tap="selectPlan(plan)"
            >
              <view v-if="plan.discountLabel" class="discount-tag">{{ plan.discountLabel }}</view>
              <view class="plan-name-row">
                <text class="plan-name">{{ plan.planName }}</text>
                <text class="plan-duration">{{ plan.durationDays === 0 ? '永久' : `${plan.durationDays}天` }}</text>
              </view>
              <view class="plan-price">
                <text class="price-symbol">¥</text>
                <text class="price-value">{{ (plan.currentPrice / 100).toFixed(0) }}</text>
              </view>
              <text v-if="plan.originalPrice > plan.currentPrice" class="price-original">
                原价¥{{ (plan.originalPrice / 100).toFixed(0) }}
              </text>
              <view class="plan-divider" />
              <view class="plan-benefits">
                <view class="benefit-item">
                  <view class="i-solar:chat-round-dots-outline benefit-icon" />
                  <text class="benefit-text">AI每日{{ plan.aiDailyQuota === -1 ? '无限' : plan.aiDailyQuota + '次' }}</text>
                </view>
                <view class="benefit-item">
                  <view class="i-solar:document-text-outline benefit-icon" />
                  <text class="benefit-text">同时{{ plan.maxActivePlans === -1 ? '无限' : plan.maxActivePlans + '个' }}方案</text>
                </view>
                <view v-if="plan.pointsMultiplier > 1" class="benefit-item">
                  <view class="i-solar:star-bold benefit-icon" />
                  <text class="benefit-text">积分{{ plan.pointsMultiplier }}倍</text>
                </view>
                <view v-if="plan.storeDiscount < 1" class="benefit-item">
                  <view class="i-solar:bag-heart-outline benefit-icon" />
                  <text class="benefit-text">商城{{ (plan.storeDiscount * 10).toFixed(0) }}折</text>
                </view>
              </view>
            </view>
            <view class="plan-track-end" />
          </view>
        </scroll-view>
      </view>

      <!-- Fixed pay footer -->
      <view class="pay-footer" v-if="plans.length > 0">
        <button class="pay-btn" :disabled="!selectedPlan || paying" @tap="handlePay">
          {{ paying ? '跳转支付...' : `¥${selectedPlan ? (selectedPlan.currentPrice / 100).toFixed(2) : '0.00'}  ${isActiveMember ? '立即续费' : '立即开通'}` }}
        </button>
        <text class="pay-hint">开通前请阅读<text class="pay-link">《会员服务协议》</text></text>
      </view>

      <!-- Section 3: Growth Levels (members only) -->
      <view class="section" v-if="isActiveMember && growthDetail?.levels?.length">
        <view class="section-header">
          <view class="section-line" :style="{ background: `linear-gradient(to right, transparent, ${selectedLevelColor}, transparent)` }" />
          <view class="section-tag" :style="{ color: selectedLevelColor, backgroundColor: `${selectedLevelColor}15` }">
            <text class="i-solar:star-bold section-tag-icon" :style="{ color: selectedLevelColor }" />
            <text>成长等级</text>
          </view>
          <view class="section-line" :style="{ background: `linear-gradient(to right, transparent, ${selectedLevelColor}, transparent)` }" />
        </view>
        <scroll-view scroll-x :show-scrollbar="false" class="card-scroll">
          <view class="card-track">
            <view
              v-for="(item, idx) in growthDetail.levels"
              :key="item.id"
              class="level-card"
              :class="{ active: idx === selectedLevelIndex }"
              :style="{
                borderColor: idx === selectedLevelIndex ? (levelColors[item.level - 1] || levelColors[0]) : undefined,
                backgroundColor: idx === selectedLevelIndex ? `${levelColors[item.level - 1] || levelColors[0]}08` : '#fff',
              }"
              @tap="onCardTap(idx)"
            >
              <view class="card-header">
                <text class="card-level" :style="{ color: levelColors[item.level - 1] || levelColors[0] }">V{{ item.level }}</text>
              </view>
              <text class="card-name">{{ item.levelName }}</text>
              <view class="card-divider" :style="{ background: levelColors[item.level - 1] || levelColors[0] }" />
              <text class="card-growth">{{ item.minGrowthValue }} 成长值</text>
              <view class="card-benefits">
                <view class="card-benefit">
                  <text class="i-solar:expressionless-square-outline card-benefit-icon" />
                  <text class="card-benefit-text">+{{ item.bonusAiQuota }}次</text>
                </view>
                <view class="card-benefit">
                  <text class="i-solar:stars-outline card-benefit-icon" />
                  <text class="card-benefit-text">{{ item.bonusPointsMultiplier }}倍</text>
                </view>
                <view class="card-benefit">
                  <text class="i-solar:bag-4-outline card-benefit-icon" />
                  <text class="card-benefit-text">{{ (item.bonusStoreDiscount * 10).toFixed(1) }}折</text>
                </view>
              </view>
            </view>
            <view class="card-track-end" />
          </view>
        </scroll-view>
      </view>

      <!-- Section 4: Selected Level Benefits (members only) -->
      <view class="section" v-if="isActiveMember && selectedLevel">
        <view class="section-header">
          <view class="section-line" :style="{ background: `linear-gradient(to right, transparent, ${selectedLevelColor}, transparent)` }" />
          <view class="section-tag" :style="{ color: selectedLevelColor, backgroundColor: `${selectedLevelColor}15` }">
            <text class="i-solar:crown-line-outline section-tag-icon" :style="{ color: selectedLevelColor }" />
            <text>V{{ selectedLevel.level }} 专属权益</text>
          </view>
          <view class="section-line" :style="{ background: `linear-gradient(to right, transparent, ${selectedLevelColor}, transparent)` }" />
        </view>
        <view class="benefits-grid">
          <view v-for="item in benefitItems" :key="item.label" class="benefit-card">
            <view class="benefit-icon-wrap" :style="{ background: `${item.color}1a` }">
              <text class="benefit-icon" :class="item.icon" :style="{ color: item.color }" />
            </view>
            <view class="benefit-info">
              <text class="benefit-label">{{ item.label }}</text>
              <text class="benefit-value">{{ item.value }}</text>
            </view>
          </view>
        </view>
        <view class="privilege-card" v-if="selectedLevel.privilege">
          <view class="privilege-header">
            <text class="i-solar:shield-check-outline privilege-icon" />
            <text class="privilege-title">权益说明</text>
          </view>
          <text class="privilege-text">{{ selectedLevel.privilege }}</text>
        </view>
      </view>

      <TLEmpty v-if="!isLoading && !memberStatus" title="暂无会员数据" description="开通会员后即可查看详情" />
      <view class="bottom-space" />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLEmpty from '@/components/TLEmpty/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import { getMemberStatus, getGrowthDetail, getEnabledPlans } from '@/api/fee/member'
import { createOrder, getOrderStatus } from '@/api/fee/order'
import type { MemberStatusVO, GrowthDetailVO, GrowthLevelVO, MemberPlanVO } from '@/types/biz/fee/member'
import { usePageLayout } from '@/composables/usePageLayout'

const { contentPaddingTop } = usePageLayout()

// --- State ---
const isLoading = ref(true)
const memberStatus = ref<MemberStatusVO | null>(null)
const growthDetail = ref<GrowthDetailVO | null>(null)
const plans = ref<MemberPlanVO[]>([])
const selectedPlan = ref<MemberPlanVO | null>(null)
const selectedLevelIndex = ref(0)
const paying = ref(false)

// --- Colors ---
const levelColors = [
  '#B8865A', '#8E99A4', '#C9A84C', '#A0A8B0',
  '#6BA8B8', '#B07DB8', '#C8807A', '#8B70B0', '#C07858',
]

// --- Computed: Member ---
const isActiveMember = computed(() => memberStatus.value?.isActiveMember ?? false)

const memberLevelName = computed(() => {
  const map: Record<number, string> = { 1: '月卡会员', 2: '年卡会员', 3: '终身会员' }
  return map[memberStatus.value?.memberLevel ?? 0] ?? ''
})

const memberExpireText = computed(() => {
  if (!memberStatus.value?.remainDays) return ''
  const level = memberStatus.value.memberLevel
  if (level === 3) return '终身会员'
  return `剩余${memberStatus.value.remainDays}天`
})

const currentGrowthLevel = computed(() => {
  if (!growthDetail.value?.levels) return null
  return growthDetail.value.levels.find(l => l.currentLevel) ?? null
})

const currentGrowthColor = computed(() => {
  if (!currentGrowthLevel.value) return levelColors[0]
  return levelColors[currentGrowthLevel.value.level - 1] || levelColors[0]
})

const nextGrowthLevel = computed<GrowthLevelVO | null>(() => {
  if (!growthDetail.value?.levels || !currentGrowthLevel.value) return null
  const idx = growthDetail.value.levels.findIndex(l => l.currentLevel)
  return growthDetail.value.levels[idx + 1] ?? null
})

const isMaxLevel = computed(() => {
  if (!growthDetail.value?.levels || !currentGrowthLevel.value) return true
  const idx = growthDetail.value.levels.findIndex(l => l.currentLevel)
  return idx >= growthDetail.value.levels.length - 1
})

const growthToNext = computed(() => {
  if (!nextGrowthLevel.value || !growthDetail.value) return 0
  return Math.max(0, nextGrowthLevel.value.minGrowthValue - (growthDetail.value.growthValue ?? 0))
})

const progressWidth = computed(() => {
  if (!currentGrowthLevel.value || !nextGrowthLevel.value || !growthDetail.value) return 0
  const growth = growthDetail.value.growthValue ?? 0
  const range = Math.max(1, nextGrowthLevel.value.minGrowthValue - currentGrowthLevel.value.minGrowthValue)
  return Math.min(100, Math.max(0, ((growth - currentGrowthLevel.value.minGrowthValue) / range) * 100))
})

// --- Computed: Selected level ---
const selectedLevel = computed<GrowthLevelVO | null>(() => {
  if (!growthDetail.value?.levels?.length) return null
  return growthDetail.value.levels[selectedLevelIndex.value] || null
})

const selectedLevelColor = computed(() => {
  if (!selectedLevel.value) return levelColors[0]
  return levelColors[selectedLevel.value.level - 1] || levelColors[0]
})

const benefitItems = computed(() => {
  if (!selectedLevel.value) return []
  return [
    { label: 'AI额外配额', value: `+${selectedLevel.value.bonusAiQuota}次/天`, icon: 'i-solar:expressionless-square-outline', color: '#409EFF' },
    { label: '积分倍率', value: `${selectedLevel.value.bonusPointsMultiplier}倍`, icon: 'i-solar:stars-outline', color: '#FFB800' },
    { label: '商城折扣', value: `${(selectedLevel.value.bonusStoreDiscount * 10).toFixed(1)}折`, icon: 'i-solar:bag-4-outline', color: '#F56C6C' },
    { label: '专属等级', value: selectedLevel.value.levelName, icon: 'i-solar:crown-outline', color: '#10B981' },
  ]
})

// --- Data loading ---
const loadAllData = async () => {
  isLoading.value = true
  try {
    const [status, growth, planData] = await Promise.all([
      getMemberStatus().catch(() => null),
      getGrowthDetail().catch(() => null),
      getEnabledPlans().catch(() => [] as MemberPlanVO[]),
    ])
    memberStatus.value = status
    growthDetail.value = growth
    plans.value = planData ?? []
    if (plans.value.length > 0) selectedPlan.value = plans.value[0]
    if (growth?.levels?.length) {
      const idx = growth.levels.findIndex(l => l.currentLevel)
      selectedLevelIndex.value = idx >= 0 ? idx : 0
    }
  } finally {
    isLoading.value = false
  }
}

// --- Level card interaction ---
const onCardTap = (idx: number) => {
  selectedLevelIndex.value = idx
}

// --- Plan selection ---
const selectPlan = (plan: MemberPlanVO) => {
  selectedPlan.value = plan
}

// --- Payment ---
const handlePay = async () => {
  if (!selectedPlan.value || paying.value) return
  paying.value = true
  try {
    const orderData = await createOrder({
      planCode: selectedPlan.value.planCode,
      wxOpenid: '',
    })
    if (orderData?.prepayId && orderData?.paySign) {
      uni.requestPayment({
        provider: 'wxpay',
        timeStamp: orderData.timeStamp,
        nonceStr: orderData.nonceStr,
        package: orderData.packageStr,
        signType: orderData.signType as 'RSA',
        paySign: orderData.paySign,
        success: () => { pollOrderStatus(orderData.orderNo) },
        fail: (err) => {
          paying.value = false
          uni.showToast({
            title: err.errMsg?.includes('cancel') ? '支付取消' : '支付失败',
            icon: 'none',
          })
        },
      })
    } else {
      paying.value = false
      uni.showToast({ title: '订单创建成功（支付服务待配置）', icon: 'success' })
      setTimeout(() => loadAllData(), 1500)
    }
  } catch (err: any) {
    paying.value = false
    uni.showToast({ title: err?.data?.msg ?? '创建订单失败', icon: 'none' })
  }
}

const pollOrderStatus = (orderNo: string) => {
  let retries = 0
  const maxRetries = 5
  const poll = async () => {
    try {
      const order = await getOrderStatus(orderNo)
      if (order?.status === 1) {
        paying.value = false
        uni.showToast({ title: '支付成功', icon: 'success' })
        setTimeout(() => loadAllData(), 1500)
        return
      }
    } catch { /* ignore */ }
    retries++
    if (retries < maxRetries) {
      setTimeout(poll, 2000)
    } else {
      paying.value = false
      uni.showToast({ title: '请稍后在订单列表确认支付状态', icon: 'none' })
    }
  }
  setTimeout(poll, 1000)
}

// --- Navigation ---
const goToGrowthRecord = () => {
  uni.navigateTo({ url: '/pages/fee/member/GrowthRecord' })
}

// --- Lifecycle ---
onPullDownRefresh(async () => {
  await loadAllData()
  uni.stopPullDownRefresh()
})

onMounted(() => {
  loadAllData()
})
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: $tf-page-bg-color;
}

.content {
  padding-bottom: 40rpx;
}

// ==================== Section 1: Hero Card ====================
.hero-card {
  position: relative;
  margin: $tf-space-4 $tf-space-6 0;
  background: $tf-gradient-brand;
  border-radius: $tf-radius-2xl;
  padding: $tf-space-8;
  overflow: hidden;
}

.hero-bg-icon {
  position: absolute;
  right: -20rpx;
  top: -30rpx;
  width: 200rpx;
  height: 200rpx;
  opacity: 0.08;
  color: $tf-surface;
}

.hero-top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.hero-level-info {
  display: flex;
  align-items: baseline;
  gap: $tf-space-2;
}

.hero-level-badge {
  font-size: $tf-text-3xl;
  font-weight: 800;
  color: $tf-surface;
}

.hero-level-name {
  font-size: $tf-text-lg;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 600;
}

.hero-record-entry {
  display: flex;
  align-items: center;
  gap: $tf-space-1;
  padding: $tf-space-1 $tf-space-3;
  border-radius: $tf-radius-pill;
  background: rgba(255, 255, 255, 0.15);
}

.hero-record-text {
  font-size: $tf-text-sm;
  color: rgba(255, 255, 255, 0.85);
}

.hero-record-icon {
  font-size: $tf-text-sm;
  color: rgba(255, 255, 255, 0.85);
}

// Growth display
.hero-growth-section {
  position: relative;
  z-index: 1;
}

.hero-growth-display {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: $tf-space-2;
  margin: $tf-space-10 0;
}

.hero-growth-num {
  font-size: $tf-text-5xl;
  font-weight: 800;
  color: $tf-surface;
  line-height: 1;
}

.hero-growth-unit {
  font-size: $tf-text-base;
  color: rgba(255, 255, 255, 0.7);
}

.hero-progress {
  margin-top: $tf-space-2;
}

.hero-progress-bar {
  height: 8rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4rpx;
  overflow: hidden;
  margin-bottom: $tf-space-3;
}

.hero-progress-fill {
  height: 100%;
  border-radius: 4rpx;
  transition: width 0.3s ease;
}

.hero-progress-label {
  font-size: $tf-text-sm;
  color: rgba(255, 255, 255, 0.8);
}

.hero-max-level {
  text-align: center;
  margin-top: $tf-space-4;
}

.hero-max-text {
  font-size: $tf-text-md;
  font-weight: 500;
  color: $tf-surface;
}

// Expire row
.hero-expire-row {
  position: relative;
  z-index: 1;
  margin-top: $tf-space-4;
}

.hero-expire-text {
  font-size: $tf-text-sm;
  color: rgba(255, 255, 255, 0.7);
}

// Metric pills
.hero-metrics {
  display: flex;
  gap: $tf-space-3;
  margin-top: $tf-space-6;
  position: relative;
  z-index: 1;
}

.hero-metric-pill {
  flex: 1;
  display: flex;
  align-items: center;
  gap: $tf-space-1;
  background: rgba(255, 255, 255, 0.12);
  border-radius: $tf-radius-pill;
  padding: $tf-space-2 $tf-space-3;
}

.hero-metric-icon {
  font-size: $tf-text-base;
  color: rgba(255, 255, 255, 0.8);
}

.hero-metric-text {
  font-size: $tf-text-xs;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

// ==================== Shared Section Header ====================
.section {
  margin-top: $tf-space-8;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $tf-space-5;
  margin-bottom: $tf-space-6;
}

.section-line {
  flex: 1;
  height: 1rpx;
}

.section-tag {
  display: flex;
  align-items: center;
  gap: $tf-space-2;
  font-size: $tf-text-base;
  padding: $tf-space-2 $tf-space-6;
  border-radius: $tf-radius-3xl;
  font-weight: 600;
  white-space: nowrap;
}

.section-tag-icon {
  font-size: $tf-text-lg;
}

// Brand variant for plan section
.section-line--brand {
  background: linear-gradient(to right, transparent, $tf-brand, transparent);
}

.section-tag--brand {
  color: $tf-brand;
  background-color: var(--tf-brand-alpha-8);
}

// ==================== Section 2: Plan Selection ====================
.plan-scroll {
  width: 100%;
}

.plan-track {
  display: flex;
  padding: $tf-space-2 0 $tf-space-2 $tf-space-6;
}

.plan-card {
  position: relative;
  flex-shrink: 0;
  width: 280rpx;
  background: $tf-surface;
  border-radius: $tf-radius-xl;
  padding: $tf-space-5;
  margin-right: $tf-space-4;
  border: 2rpx solid $tf-gray-200;
  display: flex;
  flex-direction: column;
  transition: all 0.25s;

  &.active {
    border-color: $tf-brand;
    background: $tf-brand-bg-light;
    box-shadow: $tf-shadow-card;

    .plan-name { color: $tf-brand; }
    .price-symbol, .price-value { color: $tf-brand; }
    .price-original { color: $tf-brand-light; }
    .benefit-icon { color: $tf-brand; }
    .benefit-text { color: $tf-brand-dark; }
    .plan-divider { background: $tf-brand-light; }
  }
}

.plan-track-end {
  flex-shrink: 0;
  width: $tf-space-2;
}

.discount-tag {
  position: absolute;
  top: 0;
  right: $tf-space-6;
  background: $tf-gradient-brand;
  color: $tf-surface;
  font-size: $tf-text-xs;
  padding: $tf-space-1 $tf-space-4;
  border-radius: 0 0 $tf-radius-md $tf-radius-md;
}

.plan-name-row {
  display: flex;
  flex-direction: column;
  gap: $tf-space-1;
  margin-bottom: $tf-space-4;
}

.plan-name {
  font-size: $tf-text-xl;
  font-weight: bold;
  color: $tf-gray-900;
}

.plan-duration {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
}

.plan-price {
  display: flex;
  align-items: baseline;
  margin-bottom: $tf-space-1;
}

.price-symbol {
  font-size: $tf-text-base;
  color: $tf-warning-dark;
  margin-right: 2rpx;
}

.price-value {
  font-size: $tf-text-4xl;
  font-weight: bold;
  color: $tf-warning-dark;
  line-height: 1;
}

.price-original {
  font-size: $tf-text-xs;
  color: $tf-gray-400;
  text-decoration: line-through;
}

.plan-divider {
  height: 1rpx;
  background: $tf-gray-200;
  margin: $tf-space-4 0;
}

.plan-benefits {
  display: flex;
  flex-direction: column;
  gap: $tf-space-3;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: $tf-space-2;
}

.benefit-icon {
  width: 32rpx;
  height: 32rpx;
  color: $tf-gray-600;
  flex-shrink: 0;
}

.benefit-text {
  font-size: $tf-text-sm;
  color: $tf-gray-700;
}

// Pay footer
.pay-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  // background: $tf-surface;
  padding: $tf-space-5 $tf-space-8;
  padding-bottom: calc(#{$tf-space-5} + env(safe-area-inset-bottom));
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $tf-space-3;
  // box-shadow: $tf-shadow-bottom;
  z-index: 10;
}

.pay-btn {
  width: 100%;
  padding: $tf-space-5 0;
  background: $tf-gradient-brand;
  color: $tf-surface;
  border-radius: $tf-radius-4xl;
  font-size: $tf-text-xl;
  font-weight: bold;
  border: none;
  line-height: 1.2;

  &[disabled] {
    opacity: 0.5;
  }
}

.pay-hint {
  font-size: $tf-text-xs;
  color: $tf-gray-400;
}

.pay-link {
  color: $tf-brand;
}

// ==================== Section 3: Growth Levels ====================
.card-scroll {
  width: 100%;
}

.card-track {
  display: flex;
  padding: $tf-space-2 0 $tf-space-2 $tf-space-6;
}

.card-track-end {
  flex-shrink: 0;
  width: $tf-space-2;
}

.level-card {
  flex-shrink: 0;
  width: 240rpx;
  background: $tf-surface;
  border-radius: $tf-radius-2xl;
  padding: $tf-space-6;
  margin-right: $tf-space-4;
  display: flex;
  flex-direction: column;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.active {
    box-shadow: $tf-shadow-card;
  }
}

.card-header {
  margin-bottom: $tf-space-1;
}

.card-level {
  font-size: $tf-text-2xl;
  font-weight: 800;
}

.card-name {
  font-size: $tf-text-md;
  color: $tf-gray-800;
  font-weight: 600;
  margin-bottom: $tf-space-3;
}

.card-divider {
  width: 40rpx;
  height: 4rpx;
  border-radius: 2rpx;
  margin-bottom: $tf-space-3;
}

.card-growth {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
  margin-bottom: $tf-space-4;
}

.card-benefits {
  display: flex;
  flex-direction: column;
  gap: $tf-space-2;
}

.card-benefit {
  display: flex;
  align-items: center;
  gap: $tf-space-2;
}

.card-benefit-icon {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
}

.card-benefit-text {
  font-size: $tf-text-sm;
  color: $tf-gray-700;
}

// ==================== Section 4: Benefits ====================
.benefits-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $tf-space-4;
  margin: 0 $tf-space-6;
  margin-bottom: $tf-space-4;
}

.benefit-card {
  display: flex;
  align-items: center;
  gap: $tf-space-4;
  background: $tf-surface;
  border-radius: $tf-radius-lg;
  padding: $tf-space-6;
}

.benefit-icon-wrap {
  width: 72rpx;
  height: 72rpx;
  border-radius: $tf-radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.benefit-icon {
  font-size: $tf-text-lg;
}

.benefit-info {
  display: flex;
  flex-direction: column;
  gap: $tf-space-1;
}

.benefit-label {
  font-size: $tf-text-sm;
  color: $tf-gray-600;
}

.benefit-value {
  font-size: $tf-text-lg;
  color: $tf-gray-800;
  font-weight: 600;
}

// Privilege
.privilege-card {
  background: $tf-surface;
  border-radius: $tf-radius-lg;
  padding: $tf-space-6;
  margin: 0 $tf-space-6 $tf-space-4;
}

.privilege-header {
  display: flex;
  align-items: center;
  gap: $tf-space-2;
  margin-bottom: $tf-space-3;
}

.privilege-icon {
  font-size: $tf-text-lg;
  color: $tf-brand-light;
}

.privilege-title {
  font-size: $tf-text-md;
  color: $tf-gray-800;
  font-weight: 600;
}

.privilege-text {
  font-size: $tf-text-base;
  color: $tf-gray-700;
  line-height: 1.6;
}

.bottom-space {
  height: 200rpx;
}
</style>
