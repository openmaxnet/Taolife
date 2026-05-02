<template>
  <view class="page-container">

    <!-- 自定义导航栏 -->
    <TLTopBar title="系统设置" />

    <!-- 主内容区 -->
    <view class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <!-- 设置列表 -->
      <view 
        v-for="section in settingSections" 
        :key="section.title"
        class="setting-section"
      >
        <!-- {{ section.title }} -->
        <view class="setting-list">
          <view 
            v-for="item in section.items" 
            :key="item.id"
            class="setting-item"
            @click="handleSettingClick(item)"
          >
            <view class="setting-left">
              <view class="setting-icon" :class="item.icon" :style="{ color: item.iconColor }" />
              <text class="setting-name">{{ item.name }}</text>
            </view>
            <text class="setting-arrow">›</text>
          </view>
        </view>
      </view>

    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { onPullDownRefresh } from '@dcloudio/uni-app'

const { contentPaddingTop } = usePageLayout()

// 设置分组数据
const settingSections = ref([
  {
    title: '账号与安全',
    items: [
      { id: 1, name: '绑定手机', icon: 'i-solar:phone-outline', iconColor: '#10B981' },
      { id: 2, name: '账号注销', icon: 'i-solar:trash-bin-trash-outline', iconColor: '#EF4444', route: '/pages/identity/setting/AccountCancel' }
    ]
  },
  {
    title: '偏好设置',
    items: [
      { id: 8, name: '健康偏好', icon: 'i-solar:heart-pulse-outline', iconColor: '#06B6D4', route: '/pages/identity/user/preference/index' },
      { id: 3, name: '通知设置', icon: 'i-solar:bell-outline', iconColor: '#F59E0B' },
    ]
  },
  {
    title: '关于',
    items: [
      { id: 5, name: '关于我们', icon: 'i-solar:info-circle-outline', iconColor: '#3B82F6', route: '/pages/identity/agreement/AgreementDetail?code=about&title=' + encodeURIComponent('关于我们') },
      { id: 6, name: '用户协议', icon: 'i-solar:document-text-outline', iconColor: '#10B981', route: '/pages/identity/agreement/AgreementDetail?code=agreement&title=' + encodeURIComponent('用户协议') },
      { id: 7, name: '隐私政策', icon: 'i-solar:clipboard-text-outline', iconColor: '#F59E0B', route: '/pages/identity/agreement/AgreementDetail?code=privacy&title=' + encodeURIComponent('隐私政策') }
    ]
  }
])

// 点击设置项
const handleSettingClick = (item: any) => {
  if (item.route) {
    uni.navigateTo({ url: item.route })
    return
  }
  uni.showToast({
    title: item.name,
    icon: 'none'
  })
}

// 下拉刷新
onPullDownRefresh(() => {
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
}

.content-area {
  padding-bottom: 32rpx;
}

// 设置区块
.setting-section {
  margin: 0 32rpx 32rpx 32rpx;
}

.setting-list {
  background: $tf-surface;
  border-radius: 20rpx;
  overflow: hidden;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: $tf-border-normal;
  
  &:last-child {
    border-bottom: none;
  }
}

.setting-left {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.setting-icon {
  width: 36rpx;
  height: 36rpx;
}

.setting-name {
  font-size: 28rpx;
  color: $tf-gray-800;
}

.setting-arrow {
  font-size: 36rpx;
  color: $tf-gray-400;
}
</style>
