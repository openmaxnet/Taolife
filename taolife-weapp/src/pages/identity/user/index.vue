<template>
  <view class="page-container">

    <!-- 自定义 tabbar -->
    <TLTabBar :current="4" />

    <!-- 加载失败 -->
    <TLReload
      v-if="loadFailed"
      title="加载失败"
      description="无法获取用户信息"
      @reload="loadData"
    />

    <!-- 正在加载 -->
    <view v-else-if="isLoading" class="loading-state" :style="{ paddingTop: capsulePosition.bottom + 12 + 'px' }">
      <TLLoading />
    </view>

    <!-- 主内容区 -->
    <view v-else>

      <!-- Hero 全宽背景（从页面顶部开始） -->
      <view class="profile-hero" :style="{ paddingTop: capsulePosition.bottom + 12 + 'px' }">
        <view class="profile-bg-icon i-solar:hand-stars-outline" />
        <view class="profile-card-inner" @click="handleProfileClick">
          <!-- 左侧：头像 + 昵称 -->
          <view class="profile-left">
            <view class="avatar-wrapper">
              <image class="profile-avatar" :src="isLoggedIn ? (userInfo.avatar || defaultAvatar) : defaultAvatar" mode="aspectFill" />
              <view v-if="userInfo.memberLevel > 0" class="member-badge">
                <text class="member-badge-text">VIP</text>
              </view>
            </view>
            <view class="profile-info">
              <text class="profile-name">{{ isLoggedIn ? userInfo.name : '点击登录' }}</text>
              <text v-if="isLoggedIn" class="profile-desc">
                {{ userInfo.memberLevel > 0 ? memberDesc : '开通会员享更多权益' }}
              </text>
              <text v-else class="profile-desc">登录后查看个人信息</text>
              <text v-if="isLoggedIn && userInfo.availablePoints > 0" class="profile-points">{{ userInfo.availablePoints }}积分</text>
            </view>
          </view>
          <!-- 右侧：箭头 -->
          <text class="profile-arrow">›</text>
        </view>
      </view>

      <!-- 互动统计 -->
      <view v-if="isLoggedIn" class="stats-section">
        <view class="stats-grid">
          <view class="stats-item" @click="goToPage('/pages/identity/user/likeList')">
            <text class="stats-value">{{ userInfo.likeCount }}</text>
            <text class="stats-label">点赞</text>
          </view>
          <view class="stats-item" @click="goToPage('/pages/identity/user/collectList')">
            <text class="stats-value">{{ userInfo.collectCount }}</text>
            <text class="stats-label">收藏</text>
          </view>
          <view class="stats-item" @click="goToPage('/pages/identity/user/followList')">
            <text class="stats-value">{{ userInfo.followingCount }}</text>
            <text class="stats-label">关注</text>
          </view>
        </view>
      </view>

      <!-- 功能菜单 -->
      <view class="menu-section">
        <view class="menu-grid">
          <view
            v-for="item in allMenuItems"
            :key="item.id"
            class="menu-grid-item"
            @click="handleMenuClick(item)"
          >
            <view class="menu-grid-icon">
              <text :class="item.icon" :style="{ color: item.iconColor }" />
            </view>
            <text class="menu-grid-name">{{ item.name }}</text>
            <text v-if="item.extra" class="menu-grid-extra">{{ item.extra }}</text>
          </view>
        </view>
      </view>

    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import TLTabBar from '@/components/TLTabBar/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import TLReload from '@/components/TLReload/index.vue'
import { isAuth, enAuth, wxLogin } from '@/utils/authManager'
import { usePageLayout } from '@/composables/usePageLayout'
import { getUserInfo } from '@/api/identity/account'
import { useUserProfile } from '@/composables/useUserProfile'
import type { UserInfoVO } from '@/types'

const defaultAvatar = '/static/logo.png'

const { capsulePosition } = usePageLayout()

const isLoading = ref(true)
const loadFailed = ref(false)

const { fetchConstitution } = useUserProfile()

const userInfo = ref({
  name: '',
  avatar: '',
  memberLevel: 0,
  memberExpireTime: '',
  totalPoints: 0,
  availablePoints: 0,
  birthday: '',
  likeCount: 0,
  collectCount: 0,
  followingCount: 0,
})

// 会员等级映射
const memberLevelMap: Record<number, string> = {
  1: '月卡会员',
  2: '年卡会员',
  3: '终身会员',
}

const memberDesc = computed(() => {
  const level = userInfo.value.memberLevel
  const name = memberLevelMap[level] ?? '会员'
  if (level === 3) return name
  const expireTime = userInfo.value.memberExpireTime
  if (expireTime) {
    const remainMs = new Date(expireTime).getTime() - Date.now()
    const remainDays = Math.max(0, Math.ceil(remainMs / (1000 * 60 * 60 * 24)))
    return `${name} · 剩余${remainDays}天`
  }
  return name
})

// 菜单数据
const allMenuItems = computed(() => [
  {
    id: 1,
    name: '会员中心',
    icon: 'i-solar:crown-star-outline',
    iconColor: '#8B5CF6',
    route: '/pages/fee/member/MemberCenter',
    extra: userInfo.value.memberLevel > 0 ? memberLevelMap[userInfo.value.memberLevel] : '开通',
    requireLogin: true,
  },
  {
    id: 2,
    name: '养生订单',
    icon: 'i-solar:running-2-outline',
    iconColor: '#10B981',
    route: '/pages/fee/member/OrderList',
    requireLogin: true,
  },
  {
    id: 6,
    name: '健康偏好',
    icon: 'i-solar:heart-pulse-outline',
    iconColor: '#06B6D4',
    route: '/pages/identity/user/preference/index',
    requireLogin: true,
  },
  {
    id: 5,
    name: '积分商城',
    icon: 'i-solar:gift-outline',
    iconColor: '#F59E0B',
    route: '/pages/fee/points/index',
    extra: userInfo.value.availablePoints > 0 ? `${userInfo.value.availablePoints}积分` : '',
    requireLogin: true,
  },
  {
    id: 4,
    name: '系统设置',
    icon: 'i-solar:settings-outline',
    iconColor: '#F43F5E',
    route: '/pages/identity/setting/index',
    requireLogin: false,
  },
])

// 是否已登录
const isLoggedIn = computed(() => isAuth.value && userInfo.value.name !== '')

// 获取用户信息
const fetchUserInfo = async () => {
  if (!isAuth.value) return

  const userData: UserInfoVO = await getUserInfo()
  userInfo.value = {
    name: userData.nickname || '用户',
    avatar: userData.avatarUrl || defaultAvatar,
    memberLevel: userData.memberLevel || 0,
    memberExpireTime: userData.memberExpireTime || '',
    totalPoints: userData.totalPoints || 0,
    availablePoints: userData.availablePoints || 0,
    birthday: userData.birthday || '',
    likeCount: userData.likeCount || 0,
    collectCount: userData.collectCount || 0,
    followingCount: userData.followingCount || 0,
  }
}

// 加载数据
const loadData = async () => {
  isLoading.value = true
  loadFailed.value = false

  try {
    await enAuth()
    await Promise.all([fetchUserInfo(), fetchConstitution()])
  } catch {
    loadFailed.value = true
  } finally {
    isLoading.value = false
  }
}

// 点击个人信息卡
const handleProfileClick = () => {
  if (!isLoggedIn.value) {
    wxLogin().then(() => loadData())
    return
  }
  uni.navigateTo({
    url: '/pages/identity/user/ProfileEdit'
  })
}

// 页面跳转
const goToPage = (route: string) => {
  if (!isLoggedIn.value) {
    wxLogin().then(() => loadData())
    return
  }
  uni.navigateTo({ url: route })
}

// 点击菜单项
const handleMenuClick = async (item: any) => {
  if (item.requireLogin && !isLoggedIn.value) {
    await wxLogin()
    await loadData()
    return
  }

  if (item.route) {
    uni.navigateTo({ url: item.route })
  }
}

// 下拉刷新
onPullDownRefresh(async () => {
  await loadData()
  uni.stopPullDownRefresh()
})

// 页面加载
onMounted(async () => {
  await loadData()

  uni.$on('authLoginSuccess', loadData)
  uni.$on('profileUpdated', loadData)
  onUnmounted(() => {
    uni.$off('authLoginSuccess', loadData)
    uni.$off('profileUpdated', loadData)
  })
})
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
  padding-bottom: 140rpx;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}

// Hero 全宽背景
.profile-hero {
  position: relative;
  width: 100%;
  background: linear-gradient(180deg, rgba(5, 150, 105, 0.12) 0%, rgba(5, 150, 105, 0.06) 60%, rgba(5, 150, 105, 0.02) 85%, transparent 100%);
  padding-bottom: $tf-space-10;
  overflow: hidden;
}

.profile-bg-icon {
  position: absolute;
  right: -20rpx;
  top: 60rpx;
  width: 240rpx;
  height: 240rpx;
  opacity: 0.06;
  color: $tf-brand;
}

.profile-card-inner {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $tf-space-6 $tf-space-6 0;
}

.profile-left {
  display: flex;
  align-items: center;
  gap: $tf-space-4;
  flex: 1;
  min-width: 0;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.profile-avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
}

.member-badge {
  position: absolute;
  bottom: -4rpx;
  right: -8rpx;
  background: linear-gradient(135deg, #F59E0B, #D97706);
  border-radius: 16rpx;
  padding: 0 10rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.3);
}

.member-badge-text {
  font-size: 16rpx;
  color: #fff;
  font-weight: bold;
}

.profile-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.profile-name {
  font-size: $tf-text-xl;
  color: $tf-gray-900;
  font-weight: bold;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.profile-desc {
  font-size: $tf-text-sm;
  color: $tf-gray-600;
  margin-top: $tf-space-2;
}

// 右侧箭头
.profile-arrow {
  font-size: 36rpx;
  color: $tf-gray-400;
  flex-shrink: 0;
}

.profile-points {
  font-size: $tf-text-xs;
  color: $tf-brand;
  margin-top: $tf-space-1;
}

// 互动统计
.stats-section {
  margin: 0 $tf-space-6 $tf-space-5;
}

.stats-grid {
  display: flex;
  background: $tf-surface;
  border-radius: $tf-radius-xl;
  padding: $tf-space-5 0;
}

.stats-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $tf-space-1;
}

.stats-value {
  font-size: $tf-text-xl;
  font-weight: bold;
  color: $tf-gray-900;
}

.stats-label {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
}

// 功能菜单
.menu-section {
  margin: $tf-space-5 $tf-space-6 $tf-space-6;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $tf-space-4;
  background: $tf-surface;
  border-radius: $tf-radius-xl;
  padding: $tf-space-6 $tf-space-4;
}

.menu-grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $tf-space-2;
}

.menu-grid-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: $tf-radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;

  text {
    font-size: 36rpx;
  }
}

.menu-grid-name {
  font-size: $tf-text-sm;
  color: $tf-gray-800;
  font-weight: 500;
}

.menu-grid-extra {
  font-size: $tf-text-xs;
  color: $tf-gray-400;
}
</style>
