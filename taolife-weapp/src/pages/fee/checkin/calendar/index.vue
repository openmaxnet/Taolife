<template>
  <view class="page-container">
    <!-- 自定义导航栏 -->
    <TLTopBar title="打卡日历" :show-back="true" />

    <!-- 主内容区 -->
    <view class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <!-- 统计卡片 -->
      <view class="stats-card">
        <view class="stat-item">
          <text class="stat-value">{{ calendarData.consecutiveDays || 0 }}</text>
          <text class="stat-label">连续签到</text>
        </view>
        <view class="stat-divider" />
        <view class="stat-item">
          <text class="stat-value">{{ calendarData.totalDays || 0 }}</text>
          <text class="stat-label">累计签到</text>
        </view>
        <view class="stat-divider" />
        <view class="stat-item">
          <text class="stat-value">{{ calendarData.currentMonthPoints || 0 }}</text>
          <text class="stat-label">当月积分</text>
        </view>
      </view>

      <!-- 月份选择 -->
      <view class="month-selector">
        <view class="month-btn" @click="prevMonth">
          <view class="i-solar:alt-arrow-left-outline" />
        </view>
        <text class="month-text">{{ currentYear }}年{{ currentMonth }}月</text>
        <view class="month-btn" @click="nextMonth">
          <view class="i-solar:alt-arrow-right-outline" />
        </view>
      </view>

      <!-- 日历 -->
      <view class="calendar-container">
        <!-- 星期标题 -->
        <view class="week-header">
          <text v-for="day in weekDays" :key="day" class="week-day">{{ day }}</text>
        </view>

        <!-- 日期网格 -->
        <view class="date-grid">
          <view
            v-for="(date, index) in calendarDates"
            :key="index"
            class="date-item"
            :class="{
              'date-empty': !date,
              'date-checked': date && isDateChecked(date),
              'date-today': date && isToday(date)
            }"
          >
            <text v-if="date" class="date-text">{{ date }}</text>
          </view>
        </view>
      </view>

      <!-- 图例 -->
      <view class="legend">
        <view class="legend-item">
          <view class="legend-dot legend-checked" />
          <text class="legend-text">已签到</text>
        </view>
        <view class="legend-item">
          <view class="legend-dot legend-today" />
          <text class="legend-text">今天</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { getCheckinCalendar } from '@/api/fee/checkin'
import type { CheckinCalendar } from '@/types/biz/fee/checkin'
import TLTopBar from '@/components/TLTopBar/index.vue'

// 页面布局（TopBar 页面只需要 contentPaddingTop）
const { contentPaddingTop } = usePageLayout()

// 当前年月
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth() + 1)

// 日历数据
const calendarData = ref<CheckinCalendar>({
  checkedDays: [],
  consecutiveDays: 0,
  totalDays: 0,
  currentMonthPoints: 0
})

// 加载状态
const loading = ref(false)

// 星期标题
const weekDays = ['日', '一', '二', '三', '四', '五', '六']

/**
 * 计算日历日期数组
 */
const calendarDates = computed(() => {
  const dates: (number | null)[] = []
  const year = currentYear.value
  const month = currentMonth.value

  // 获取当月第一天是星期几
  const firstDay = new Date(year, month - 1, 1).getDay()

  // 获取当月总天数
  const daysInMonth = new Date(year, month, 0).getDate()

  // 填充空白
  for (let i = 0; i < firstDay; i++) {
    dates.push(null)
  }

  // 填充日期
  for (let i = 1; i <= daysInMonth; i++) {
    dates.push(i)
  }

  return dates
})

/**
 * 判断日期是否已签到
 */
const isDateChecked = (date: number): boolean => {
  return calendarData.value.checkedDays?.includes(date) || false
}

/**
 * 判断是否是今天
 */
const isToday = (date: number): boolean => {
  const today = new Date()
  return today.getFullYear() === currentYear.value &&
         today.getMonth() + 1 === currentMonth.value &&
         today.getDate() === date
}

/**
 * 获取签到日历数据
 */
const fetchCalendarData = async () => {
  if (loading.value) return

  loading.value = true

  try {
    const result = await getCheckinCalendar({
      year: currentYear.value,
      month: currentMonth.value
    })
    calendarData.value = result
  } catch (error: any) {
    console.error('获取签到日历失败:', error)
    uni.showToast({
      title: '加载失败，请重试',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

/**
 * 上个月
 */
const prevMonth = () => {
  if (currentMonth.value === 1) {
    currentYear.value--
    currentMonth.value = 12
  } else {
    currentMonth.value--
  }
  fetchCalendarData()
}

/**
 * 下个月
 */
const nextMonth = () => {
  const today = new Date()
  const maxYear = today.getFullYear()
  const maxMonth = today.getMonth() + 1

  // 不允许查看未来月份
  if (currentYear.value > maxYear ||
      (currentYear.value === maxYear && currentMonth.value >= maxMonth)) {
    uni.showToast({
      title: '暂无未来数据',
      icon: 'none'
    })
    return
  }

  if (currentMonth.value === 12) {
    currentYear.value++
    currentMonth.value = 1
  } else {
    currentMonth.value++
  }
  fetchCalendarData()
}

// 页面加载
onMounted(() => {
  // 获取签到日历数据
  fetchCalendarData()
})
</script>

<style lang="scss" scoped>
.page-container {
  overflow-y: auto;
}

// 内容区
.content-area {
  padding: 24rpx;
  padding-top: calc(80px + 24rpx);
}

// 统计卡片
.stats-card {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 40rpx 24rpx;
  background: $tf-gradient-brand-alt;
  border-radius: 24rpx;
  margin-bottom: 32rpx;
  // box-shadow: 0 8rpx 24rpx rgba(5, 150, 105, 0.3);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.stat-value {
  font-size: 48rpx;
  font-weight: bold;
  color: $tf-surface;
}

.stat-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}

.stat-divider {
  width: 2rpx;
  height: 60rpx;
  background: rgba(255, 255, 255, 0.3);
}

// 月份选择
.month-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 48rpx;
  padding: 24rpx 0;
  margin-bottom: 24rpx;
}

.month-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $tf-surface;
  border-radius: 50%;
  // box-shadow: $uni-card-shadow;
  font-size: 32rpx;
  color: $tf-gray-600;

  &:active {
    transform: scale(0.95);
  }
}

.month-text {
  font-size: 32rpx;
  font-weight: 600;
  color: $tf-gray-900;
}

// 日历容器
.calendar-container {
  background: $tf-surface;
  border-radius: 24rpx;
  padding: 32rpx;
  // box-shadow: $uni-card-shadow;
}

// 星期标题
.week-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8rpx;
  margin-bottom: 16rpx;
}

.week-day {
  text-align: center;
  font-size: 24rpx;
  color: $tf-gray-500;
  font-weight: 500;
}

// 日期网格
.date-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8rpx;
}

.date-item {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12rpx;
  transition: all 0.3s;
}

.date-empty {
  background: transparent;
}

.date-text {
  font-size: 28rpx;
  color: $tf-gray-800;
  font-weight: 500;
}

.date-checked {
  background: $tf-brand-bg;

  .date-text {
    color: $tf-brand;
    font-weight: 600;
  }
}

.date-today {
  background: $tf-brand;

  .date-text {
    color: $tf-surface;
    font-weight: 600;
  }
}

// 图例
.legend {
  display: flex;
  justify-content: center;
  gap: 48rpx;
  margin-top: 32rpx;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.legend-dot {
  width: 24rpx;
  height: 24rpx;
  border-radius: 6rpx;
}

.legend-checked {
  background: $tf-brand-bg;
  border: 2rpx solid $tf-brand;
}

.legend-today {
  background: $tf-brand;
}

.legend-text {
  font-size: 24rpx;
  color: $tf-gray-600;
}
</style>
