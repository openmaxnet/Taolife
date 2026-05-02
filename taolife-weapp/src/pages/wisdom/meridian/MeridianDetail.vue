<template>
  <view class="page-container">

    <!-- 加载失败时显示 -->
    <TLReload v-if="loadFailed" @reload="handleReload" />

    <!-- 主内容区 -->
    <view v-else class="main-content" :style="{ paddingTop: capsulePosition.bottom + 12 + 'px' }">
      <!-- 经络信息卡片 -->
      <view class="meridian-card">
        <view class="meridian-header">
          <view class="meridian-icon" :style="{ backgroundColor: meridianDetail?.lineColor + '20' }">
            <view class="meridian-line" :style="{ backgroundColor: meridianDetail?.lineColor }"></view>
          </view>
          <view class="meridian-info">
            <text class="meridian-name">{{ meridianDetail?.name }}</text>
            <text class="meridian-pinyin">{{ meridianDetail?.namePinyin }}</text>
          </view>
          <view class="meridian-category-tag">{{ meridianDetail?.categoryName }}</view>
        </view>
        
        <view class="section">
          <text class="section-title">经络描述</text>
          <text class="section-content">{{ meridianDetail?.description }}</text>
        </view>
        
        <view class="section">
          <text class="section-title">循行路线</text>
          <text class="section-content">{{ meridianDetail?.pathDescription }}</text>
        </view>
        
        <view class="section">
          <text class="section-title">主治病症</text>
          <text class="section-content">{{ meridianDetail?.mainIndications }}</text>
        </view>
      </view>

      <!-- 穴位列表 -->
      <view class="acupoint-section">
        <view class="section-header">
          <text class="section-title">穴位列表</text>
          <text class="section-count">共 {{ acupointList.length }} 个穴位</text>
        </view>
        
        <view class="acupoint-list">
          <view
            v-for="item in acupointList"
            :key="item.id"
            class="acupoint-item"
            :class="'marker-' + item.markerType"
            @click="goToAcupointDetail(item.id)"
          >
            <view class="acupoint-info">
              <text class="acupoint-name">{{ item.name }}</text>
              <text class="acupoint-pinyin">{{ item.namePinyin }}</text>
            </view>
            <text class="acupoint-location">{{ item.locationDescription }}</text>
            <view class="acupoint-tags">
              <text class="acupoint-meridian">{{ item.meridianName }}</text>
              <text class="acupoint-marker">{{ item.markerTypeName }}</text>
            </view>
            <view class="acupoint-arrow">›</view>
          </view>
        </view>
        
        <!-- 空状态 -->
        <TLEmpty
          v-if="!acupointLoading && acupointList.length === 0"
          title="暂无穴位"
          description="该经络下还没有穴位~"
          icon-size="80rpx"
          padding="60rpx 32rpx"
          min-height="200rpx"
          title-size="26rpx"
          title-margin="24rpx"
          desc-size="22rpx"
        />
      </view>

      <!-- 加载状态 -->
      <view v-if="acupointLoading" class="loading-tip">
        <text>加载穴位中...</text>
      </view>
    </view>
    
    <!-- 底部导航栏 -->
    <TLNavBar
      ref="navBarRef"
      :title="meridianDetail?.name || '经络详情'"
    />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import TLNavBar from '@/components/TLNavBar/index.vue';
import TLReload from '@/components/TLReload/index.vue';
import TLEmpty from '@/components/TLEmpty/index.vue';
import { getMeridianDetail, getAcupointPage } from '@/api/wisdom/meridian';
import { usePageLayout } from '@/composables/usePageLayout';
import { useDetailLoader } from '@/composables/useDetailLoader';
import type { MeridianDetail, AcupointListItem } from '@/types/biz/wisdom/meridian';


const { navBarRef, capsulePosition } = usePageLayout();

let id = ''

onLoad((options) => {
  id = options?.id || ''
})

const { data: meridianDetail, loadFailed, load: _loadDetail } = useDetailLoader<MeridianDetail>(
  () => getMeridianDetail(id)
);

// 穴位列表
const acupointList = ref<AcupointListItem[]>([]);

// 加载状态
const acupointLoading = ref(false);

/**
 * 获取经络详情
 */
const fetchMeridianDetail = async () => {
  if (!id) return;
  await _loadDetail();
  if (meridianDetail.value) {
    fetchAcupointList(meridianDetail.value.code);
  }
};

/**
 * 重新加载
 */
const handleReload = () => {
  fetchMeridianDetail();
};

/**
 * 获取穴位列表
 */
const fetchAcupointList = async (meridianCode: string) => {
  acupointLoading.value = true;
  
  try {
    const result = await getAcupointPage({
      meridianCode,
      pageNo: 1,
      pageSize: 100
    });
    acupointList.value = result.list || [];
  } catch (error: any) {
    console.error('获取穴位列表失败:', error);
  } finally {
    acupointLoading.value = false;
  }
};

/**
 * 跳转到穴位详情页
 */
const goToAcupointDetail = (id: string) => {
  uni.navigateTo({
    url: `/pages/wisdom/meridian/AcupointDetail?id=${id}`
  });
};

// 页面加载
onMounted(() => {
  fetchMeridianDetail();
});
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
  padding-bottom: 32rpx;
}

.main-content {
  min-height: 100vh;
}

// 经络信息卡片
.meridian-card {
  background: #FFFFFF;
  margin: 24rpx;
  border-radius: 24rpx;
  padding: 32rpx;
  // box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.meridian-header {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 32rpx;
  padding-bottom: 24rpx;
  border-bottom: $tf-border-light;
}

.meridian-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.meridian-line {
  width: 56rpx;
  height: 8rpx;
  border-radius: 4rpx;
}

.meridian-info {
  flex: 1;
}

.meridian-name {
  display: block;
  font-size: 36rpx;
  color: $tf-gray-900;
  font-weight: 700;
}

.meridian-pinyin {
  display: block;
  font-size: 24rpx;
  color: $tf-gray-500;
  margin-top: 8rpx;
}

.meridian-category-tag {
  font-size: 24rpx;
  color: $tf-primary-color;
  padding: 8rpx 24rpx;
  background: $tf-brand-bg;
  border-radius: 24rpx;
}

.section {
  margin-bottom: 24rpx;
}

.section-title {
  display: block;
  font-size: 28rpx;
  color: $tf-primary-color;
  font-weight: 600;
  margin-bottom: 16rpx;
}

.section-content {
  display: block;
  font-size: 26rpx;
  color: $tf-gray-700;
  line-height: 1.8;
}

// 穴位列表
.acupoint-section {
  margin: 24rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-header .section-title {
  margin-bottom: 0;
}

.section-count {
  font-size: 24rpx;
  color: $tf-gray-500;
}

.acupoint-list {
  background: #FFFFFF;
  border-radius: 24rpx;
  overflow: hidden;
  // box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.acupoint-item {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: $tf-border-light;
  position: relative;
}

.acupoint-item:last-child {
  border-bottom: none;
}

.acupoint-info {
  flex: 1;
}

.acupoint-name {
  display: block;
  font-size: 30rpx;
  color: $tf-gray-900;
  font-weight: 600;
}

.acupoint-pinyin {
  display: block;
  font-size: 22rpx;
  color: $tf-gray-500;
  margin-top: 4rpx;
}

.acupoint-location {
  flex: 1.5;
  font-size: 22rpx;
  color: $tf-gray-600;
  padding: 0 16rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.acupoint-tags {
  display: flex;
  gap: 8rpx;
  margin-right: 16rpx;
}

.acupoint-meridian {
  font-size: 20rpx;
  color: $tf-primary-color;
  padding: 4rpx 12rpx;
  background: $tf-brand-bg;
  border-radius: 8rpx;
}

.acupoint-marker {
  font-size: 20rpx;
  color: #D97706;
  padding: 4rpx 12rpx;
  background: #FEF3C7;
  border-radius: 8rpx;
}

.marker-3 .acupoint-marker {
  color: #E11D48;
  background: #FFE4E6;
}

.acupoint-arrow {
  font-size: 32rpx;
  color: $tf-gray-400;
}

// 加载提示
.loading-tip {
  text-align: center;
  padding: 32rpx;
  color: $tf-gray-500;
  font-size: 24rpx;
}
</style>