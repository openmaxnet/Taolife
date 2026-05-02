<template>
  <view class="page-container">
    <TLTopBar title="健康偏好" />
    <view class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 完善度进度 -->
      <view class="progress-card">
        <text class="progress-label">偏好完善度</text>
        <view class="progress-bar">
          <view class="progress-fill" :style="{ width: profile?.preferenceCompleteness || 0 + '%' }" />
        </view>
        <text class="progress-value">{{ profile?.preferenceCompleteness || 0 }}%</text>
      </view>

      <!-- 6 个 section card -->
      <view class="section-list">
        <view
          v-for="item in sections"
          :key="item.key"
          class="section-card"
          @click="goToSection(item.key)"
        >
          <view class="section-left">
            <view :class="['section-icon', `i-${item.icon}`]" :style="{ color: item.color }" />
            <view class="section-info">
              <text class="section-name">{{ item.name }}</text>
              <text class="section-desc">{{ getSummary(item.key) }}</text>
            </view>
          </view>
          <view class="i-solar:alt-arrow-right-linear section-arrow" />
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { enAuth } from '@/utils/authManager'
import { getPreferenceProfile } from '@/api/identity/preference'
import type { UserPreferenceProfile } from '@/types'

const { contentPaddingTop } = usePageLayout()

const profile = ref<UserPreferenceProfile | null>(null)

const sections = [
  { key: 'health', name: '身体数据', icon: 'solar:body-bold', color: '#10B981' },
  { key: 'dietary', name: '饮食偏好', icon: 'solar:cup-hot-bold', color: '#F59E0B' },
  { key: 'exercise', name: '运动偏好', icon: 'solar:running-round-bold', color: '#3B82F6' },
  { key: 'lifestyle', name: '生活习惯', icon: 'solar:sleeping-circle-bold', color: '#8B5CF6' },
  { key: 'goals', name: '健康目标', icon: 'solar:target-bold', color: '#EF4444' },
  { key: 'ai', name: 'AI偏好', icon: 'solar:chat-round-dots-bold', color: '#06B6D4' },
]

const bloodTypeMap: Record<number, string> = { 1: 'A型', 2: 'B型', 3: 'AB型', 4: 'O型' }

const getSummary = (key: string): string => {
  const p = profile.value
  if (!p) return '未设置'
  switch (key) {
    case 'health': {
      const parts: string[] = []
      if (p.height) parts.push(`${p.height}cm`)
      if (p.weight) parts.push(`${p.weight}kg`)
      if (p.bloodType) parts.push(bloodTypeMap[p.bloodType] || '')
      return parts.length ? parts.join(' · ') : '未设置'
    }
    case 'dietary': {
      const parts: string[] = []
      if (p.dislikedFoods?.length) parts.push(`${p.dislikedFoods.length}项忌口`)
      if (p.allergicFoods?.length) parts.push(`${p.allergicFoods.length}项过敏`)
      return parts.length ? parts.join(' · ') : '未设置'
    }
    case 'exercise': {
      const typeMap: Record<number, string> = { 1: '有氧', 2: '力量', 3: '柔韧', 4: '球类', 5: '传统功法' }
      return p.preferredExerciseType ? typeMap[p.preferredExerciseType] || '未设置' : '未设置'
    }
    case 'lifestyle':
      return p.sleepTime && p.wakeTime ? `${p.sleepTime} - ${p.wakeTime}` : '未设置'
    case 'goals': {
      const goalMap: Record<number, string> = { 1: '免疫', 2: '睡眠', 3: '体重', 4: '压力', 5: '消化', 6: '体力' }
      return p.healthGoalPrimary ? goalMap[p.healthGoalPrimary] || '未设置' : '未设置'
    }
    case 'ai': {
      const toneMap: Record<number, string> = { 1: '专业', 2: '亲切', 3: '简洁' }
      return p.aiTonePreference ? `风格：${toneMap[p.aiTonePreference] || '未设置'}` : '未设置'
    }
    default: return '未设置'
  }
}

const goToSection = (key: string) => {
  uni.navigateTo({ url: `./edit?section=${key}` })
}

onMounted(async () => {
  await enAuth()
  try {
    profile.value = await getPreferenceProfile()
  } catch (e) {
    console.error('加载偏好失败', e)
  }
})
</script>

<style lang="scss">
.progress-card {
  margin: $tf-space-4 $tf-space-6;
  padding: $tf-space-6;
  background: linear-gradient(135deg, $tf-brand-bg 0%, var(--tf-brand-alpha-15) 100%);
  border-radius: $tf-radius-2xl;
  display: flex;
  align-items: center;
  gap: $tf-space-3;
}

.progress-label {
  font-size: $tf-text-sm;
  color: $tf-brand-dark;
  white-space: nowrap;
}

.progress-bar {
  flex: 1;
  height: 12rpx;
  background: var(--tf-brand-alpha-20);
  border-radius: $tf-radius-3xl;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: $tf-brand;
  border-radius: $tf-radius-3xl;
  transition: width 0.3s ease;
}

.progress-value {
  font-size: $tf-text-sm;
  color: $tf-brand;
  font-weight: bold;
  min-width: 60rpx;
  text-align: right;
}

.section-list {
  padding: 0 $tf-space-6;
}

.section-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $tf-space-5 $tf-space-4;
  background: $tf-surface;
  border-radius: $tf-radius-xl;
  margin-bottom: $tf-space-3;
}

.section-left {
  display: flex;
  align-items: center;
  gap: $tf-space-4;
}

.section-icon {
  font-size: 48rpx;
}

.section-info {
  display: flex;
  flex-direction: column;
}

.section-name {
  font-size: $tf-text-md;
  color: $tf-gray-800;
  font-weight: 500;
}

.section-desc {
  font-size: $tf-text-sm;
  color: $tf-gray-400;
  margin-top: 4rpx;
}

.section-arrow {
  font-size: 36rpx;
  color: $tf-gray-300;
}
</style>
