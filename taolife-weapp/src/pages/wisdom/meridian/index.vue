<template>
  <view class="page-container">
    <!-- 顶部导航栏 -->
    <TLTopBar show-search search-placeholder="搜索经络..." @search="handleSearch" />

    <!-- 分类筛选（固定在 TopBar 正下方） -->
    <view v-if="!loadFailed" class="filter-container" :style="{ top: contentPaddingTop + 'px' }">
      <TLFilterBar mode="tabs" :dimensions="filterDimensions" @change="handleFilterChange" />
    </view>

    <!-- 内容区域 -->
    <view class="content-area" :style="{ paddingTop: (contentPaddingTop + filterBarHeight) + 'px' }">
      <TLReload v-if="loadFailed" @reload="handleReload" />
      <template v-else>
        <!-- 空状态 -->
        <TLEmpty
          v-if="!loading && meridianList.length === 0"
          title="暂无经络"
          description="经络数据正在准备中~"
          icon-size="80rpx"
          padding="60rpx 32rpx"
          min-height="200rpx"
          title-size="26rpx"
          title-margin="24rpx"
          desc-size="22rpx"
        />

        <!-- 经络列表 -->
        <view class="meridian-list">
          <view
            v-for="item in meridianList"
            :key="item.id"
            class="meridian-card"
            @click="goToMeridianDetail(item.id)"
          >
            <view class="meridian-header">
              <view class="meridian-icon" :style="{ backgroundColor: item.lineColor + '20' }">
                <view class="meridian-line" :style="{ backgroundColor: item.lineColor }"></view>
              </view>
              <view class="meridian-info">
                <text class="meridian-name">{{ item.name }}</text>
                <text class="meridian-pinyin">{{ item.namePinyin }}</text>
              </view>
              <view class="meridian-arrow">›</view>
            </view>
            <view class="meridian-desc">{{ item.description }}</view>
            <view class="meridian-footer">
              <text class="meridian-category">{{ item.categoryName }}</text>
              <text class="meridian-indications">主治：{{ item.mainIndications }}</text>
            </view>
          </view>
        </view>

        <view v-if="loading" class="loading-tip">
          <text>加载中...</text>
        </view>
        <view v-else-if="noMore && meridianList.length > 0" class="loading-tip">
          <text>没有更多了</text>
        </view>
      </template>
    </view>

    <!-- 底部导航栏 -->
    <TLNavBar ref="navBarRef" title="全身经络" />

  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import TLTopBar from '@/components/TLTopBar/index.vue';
import TLNavBar from '@/components/TLNavBar/index.vue';
import TLReload from '@/components/TLReload/index.vue';
import TLEmpty from '@/components/TLEmpty/index.vue';
import TLFilterBar from '@/components/TLFilterBar/index.vue';
import { getMeridianPage } from '@/api/wisdom/meridian';
import { usePageLayout } from '@/composables/usePageLayout';
import type { MeridianListItem } from '@/types/biz/wisdom/meridian';
import type { FilterDimension, FilterParams } from '@/types/components/filterBar';

const { contentPaddingTop, navBarRef } = usePageLayout();

// 筛选栏预估高度
const filterBarHeight = 52;

// 搜索关键词
const searchKeyword = ref('');

// 筛选维度配置
const filterDimensions: FilterDimension[] = [
  {
    key: 'category',
    options: [
      { label: '全部', value: 0 },
      { label: '十二正经', value: 1 },
      { label: '奇经八脉', value: 2 },
    ],
    defaultValue: 0,
  },
];

// 经络列表
const meridianList = ref<MeridianListItem[]>([]);

// 当前选中分类
const activeCategory = ref(0);

// 分页参数
const page = ref(1);
const pageSize = 10;
const total = ref(0);
const loading = ref(false);
const noMore = ref(false);
const loadFailed = ref(false);

/**
 * 获取经络列表
 */
const fetchMeridianList = async (reset = false) => {
  if (loading.value) return;

  loading.value = true;
  loadFailed.value = false;

  try {
    if (reset) {
      page.value = 1;
      noMore.value = false;
      meridianList.value = [];
    }

    const param: Record<string, any> = {
      category: activeCategory.value,
      pageNo: page.value,
      pageSize
    };
    if (searchKeyword.value) param.keyword = searchKeyword.value;

    const result = await getMeridianPage(param);

    if (reset) {
      meridianList.value = result.list || [];
    } else {
      meridianList.value = [...meridianList.value, ...(result.list || [])];
    }

    total.value = result.total || 0;

    // 判断是否还有更多
    if (meridianList.value.length >= total.value) {
      noMore.value = true;
    }
  } catch (error: any) {
    console.error('获取经络列表失败:', error);
    loadFailed.value = true;
  } finally {
    loading.value = false;
  }
};

/**
 * 重新加载
 */
const handleReload = () => {
  fetchMeridianList(true);
};

/**
 * 搜索回调
 */
const handleSearch = (keyword: string) => {
  searchKeyword.value = keyword;
  fetchMeridianList(true);
};

/**
 * 筛选变化回调
 */
const handleFilterChange = (params: FilterParams) => {
  activeCategory.value = (params.category ?? 0) as number;
  fetchMeridianList(true);
};

/**
 * 跳转到经络详情页
 */
const goToMeridianDetail = (id: string) => {
  uni.navigateTo({
    url: `/pages/wisdom/meridian/MeridianDetail?id=${id}`
  });
};

/**
 * 下拉刷新
 */
onPullDownRefresh(async () => {
  await fetchMeridianList(true);
  uni.stopPullDownRefresh();
});

/**
 * 上拉加载更多
 */
onReachBottom(() => {
  if (!noMore.value && !loading.value) {
    page.value++;
    fetchMeridianList();
  }
});

// 页面加载
onMounted(() => {
  fetchMeridianList(true);
});
</script>

<style lang="scss">
.page-container {
  padding-bottom: 140rpx;
}

.content-area {
  padding-bottom: 32rpx;
}

// 分类筛选（固定在 TopBar 下方）
.filter-container {
  position: fixed;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 0 24rpx;
  background: $tf-page-bg-color;
}

// 经络列表
.meridian-list {
  margin: 0 24rpx;
}

.meridian-card {
  background: #FFFFFF;
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.meridian-header {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 16rpx;
}

.meridian-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.meridian-line {
  width: 48rpx;
  height: 6rpx;
  border-radius: 3rpx;
}

.meridian-info {
  flex: 1;
}

.meridian-name {
  display: block;
  font-size: 32rpx;
  color: $tf-gray-900;
  font-weight: 600;
}

.meridian-pinyin {
  display: block;
  font-size: 22rpx;
  color: $tf-gray-500;
  margin-top: 4rpx;
}

.meridian-arrow {
  font-size: 36rpx;
  color: $tf-gray-400;
}

.meridian-desc {
  font-size: 24rpx;
  color: $tf-gray-600;
  line-height: 1.6;
  margin-bottom: 16rpx;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meridian-footer {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding-top: 16rpx;
  border-top: $tf-border-light;
}

.meridian-category {
  font-size: 20rpx;
  color: $tf-primary-color;
  padding: 4rpx 16rpx;
  background: $tf-brand-bg;
  border-radius: 8rpx;
}

.meridian-indications {
  flex: 1;
  font-size: 22rpx;
  color: $tf-gray-500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

// 加载提示
.loading-tip {
  text-align: center;
  padding: 32rpx;
  color: $tf-gray-500;
  font-size: 24rpx;
}
</style>