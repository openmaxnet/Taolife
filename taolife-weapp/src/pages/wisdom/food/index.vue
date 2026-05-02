<template>
  <view class="page-container">

    <!-- 自定义 tabbar -->
    <TLTabBar :current="1" />

    <!-- 顶部导航栏 -->
    <TLTopBar :show-back="false" show-logo show-search search-placeholder="搜索食材..." @search="handleSearch" />

    <!-- 分类筛选（固定在 TopBar 正下方，不随页面滚动） -->
    <view v-if="!loadFailed" class="filter-container" :style="{ top: contentPaddingTop + 'px' }">
      <TLFilterBar mode="dropdown" :dimensions="filterDimensions" @change="handleFilterChange" />
    </view>

    <!-- 内容区域 -->
    <view class="content-area" :style="{ paddingTop: (contentPaddingTop + filterBarHeight) + 'px' }">
      <TLReload v-if="loadFailed" @reload="handleReload" />
      <template v-else>
        <!-- 空状态 -->
        <TLEmpty
          v-if="!loading && foodList.length === 0"
          title="暂无食材"
          description="该分类下还没有食材~"
        />

        <view class="food-grid">
          <view
            v-for="item in foodList"
            :key="item.id"
            class="food-card"
            @click="goToDetail(item.id)"
          >
            <image class="food-image" :src="item.imageUrl || defaultImage" mode="aspectFill" />
            <view class="food-content">
              <text class="food-name">{{ item.name }}</text>
              <text class="food-desc">{{ item.flavor }}。{{ item.meridianEntry }}</text>
              <view class="food-footer">
                <text
                  class="food-effect"
                  :class="'effect-' + getEffectColor(item.nature)"
                >
                  {{ item.natureName }}
                </text>
                <view class="food-favorite" @click.stop="toggleFavorite(item.id)">
                  <text class="favorite-icon">♡</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <view v-if="loading" class="loading-tip">
          <text>加载中...</text>
        </view>
        <view v-else-if="noMore" class="loading-tip">
          <text>没有更多了</text>
        </view>
      </template>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import TLTabBar from '@/components/TLTabBar/index.vue';
import TLTopBar from '@/components/TLTopBar/index.vue';
import TLReload from '@/components/TLReload/index.vue';
import TLEmpty from '@/components/TLEmpty/index.vue';
import TLFilterBar from '@/components/TLFilterBar/index.vue';
import { getFoodPage } from '@/api/wisdom/food';
import { usePageLayout } from '@/composables/usePageLayout';
import type { FoodListItem } from '@/types/biz/wisdom/food';
import type { FilterDimension, FilterParams } from '@/types/components/filterBar';

// 默认图片
const defaultImage = 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d362c3036e7c4995ac1af6ed687c80e3.jpg';

const { contentPaddingTop } = usePageLayout();

// 搜索关键词
const searchKeyword = ref('');

// 筛选栏预估高度（bar + padding + margin，单位 px）
const filterBarHeight = 52;

// 筛选维度配置
const filterDimensions: FilterDimension[] = [
  {
    key: 'category',
    options: [
      { label: '分类', value: 0 },
      { label: '谷物', value: 1 },
      { label: '蔬菜', value: 2 },
      { label: '水果', value: 3 },
      { label: '肉类', value: 4 },
      { label: '药材', value: 5 },
    ],
    defaultValue: 0,
  },
  {
    key: 'flavor',
    options: [
      { label: '味道', value: '' },
      { label: '甘', value: '甘' },
      { label: '辛', value: '辛' },
      { label: '苦', value: '苦' },
      { label: '咸', value: '咸' },
      { label: '酸', value: '酸' },
      { label: '淡', value: '淡' },
      { label: '涩', value: '涩' },
    ],
    defaultValue: '',
  },
  {
    key: 'nature',
    options: [
      { label: '性质', value: null },
      { label: '寒', value: 1 },
      { label: '凉', value: 2 },
      { label: '平', value: 3 },
      { label: '温', value: 4 },
      { label: '热', value: 5 },
    ],
    defaultValue: null,
  },
  {
    key: 'meridianEntry',
    options: [
      { label: '归经', value: '' },
      { label: '肺', value: '肺' },
      { label: '胃', value: '胃' },
      { label: '脾', value: '脾' },
      { label: '心', value: '心' },
      { label: '肝', value: '肝' },
      { label: '肾', value: '肾' },
      { label: '胆', value: '胆' },
      { label: '膀胱', value: '膀胱' },
      { label: '大肠', value: '大肠' },
      { label: '小肠', value: '小肠' },
      { label: '三焦', value: '三焦' },
    ],
    defaultValue: '',
  },
];

// 当前筛选状态
const activeCategory = ref(0);
const activeFlavor = ref('');
const activeNature = ref<number | null>(null);
const activeMeridian = ref('');

// 食材列表
const foodList = ref<FoodListItem[]>([]);

/**
 * 搜索回调
 */
const handleSearch = (keyword: string) => {
  searchKeyword.value = keyword;
  fetchFoodList(true);
};

/**
 * 筛选变化回调
 */
const handleFilterChange = (params: FilterParams) => {
  activeCategory.value = (params.category ?? 0) as number;
  activeFlavor.value = (params.flavor ?? '') as string;
  activeNature.value = params.nature as number | null;
  activeMeridian.value = (params.meridianEntry ?? '') as string;
  fetchFoodList(true);
};

// 分页参数
const page = ref(1);
const pageSize = 10;
const total = ref(0);
const loading = ref(false);
const noMore = ref(false);
const loadFailed = ref(false);

/**
 * 获取食材列表
 */
const fetchFoodList = async (reset = false) => {
  if (loading.value) return;

  loading.value = true;
  loadFailed.value = false;

  try {
    if (reset) {
      page.value = 1;
      noMore.value = false;
      foodList.value = [];
    }

const param: Record<string, any> = {
      category: activeCategory.value,
      pageNo: page.value,
      pageSize,
    };
    if (searchKeyword.value) param.keyword = searchKeyword.value;
    if (activeFlavor.value) param.flavor = activeFlavor.value;
    if (activeNature.value !== null) param.nature = activeNature.value;
    if (activeMeridian.value) param.meridianEntry = activeMeridian.value;

    const result = await getFoodPage(param);

    if (reset) {
      foodList.value = result.list || [];
    } else {
      foodList.value = [...foodList.value, ...(result.list || [])];
    }

    total.value = result.total || 0;

    // 判断是否还有更多
    if (foodList.value.length >= total.value) {
      noMore.value = true;
    }
  } catch (error: any) {
    console.error('获取食材列表失败:', error);
    loadFailed.value = true;
  } finally {
    loading.value = false;
  }
};

/**
 * 重新加载
 */
const handleReload = () => {
  fetchFoodList(true);
};

/**
 * 加载更多
 */
const loadMore = () => {
  if (!noMore.value && !loading.value) {
    page.value++;
    fetchFoodList();
  }
};

/**
 * 获取功效颜色
 */
const getEffectColor = (nature: number): string => {
  // nature: 1-寒，2-凉，3-平，4-温，5-热
  const colorMap: Record<number, string> = {
    1: 'blue',    // 寒 - 蓝色
    2: 'emerald', // 凉 - 绿色
    3: 'amber',   // 平 - 黄色
    4: 'rose',    // 温 - 红色
    5: 'rose'     // 热 - 红色
  };
  return colorMap[nature] || 'amber';
};

/**
 * 跳转到详情页
 */
const goToDetail = (id: string) => {
  uni.navigateTo({
    url: `/pages/wisdom/food/FoodDetail?id=${id}`
  });
};

/**
 * 收藏食材
 */
const toggleFavorite = (id: string) => {
  uni.showToast({
    title: '已收藏',
    icon: 'success'
  });
};

/**
 * 下拉刷新
 */
onPullDownRefresh(async () => {
  await fetchFoodList(true);
  uni.stopPullDownRefresh();
});

onReachBottom(() => {
  loadMore();
});

// 页面加载
onMounted(() => {
  fetchFoodList(true);
});
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
  padding-bottom: 140rpx;
}

.content-area {
  padding-bottom: 32rpx;
}

// 筛选容器（固定在 TopBar 下方）
.filter-container {
  position: fixed;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 0 24rpx;
  background: $tf-page-bg-color;
}

// 食材网格
.food-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
  margin: 24rpx;
}

.food-card {
  background: $tf-surface;
  border-radius: $tf-radius-lg;
  overflow: hidden;
  // box-shadow: $uni-card-shadow;
}

.food-image {
  width: 100%;
  height: 256rpx;
}

.food-content {
  padding: 24rpx;
}

.food-name {
  display: block;
  font-size: 28rpx;
  color: $tf-gray-900;
  font-weight: bold;
}

.food-desc {
  display: block;
  font-size: 20rpx;
  color: $tf-gray-500;
  margin-top: 8rpx;
}

.food-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16rpx;
}

.food-effect {
  padding: 4rpx 16rpx;
  font-size: 20rpx;
  border-radius: 8rpx;
}

.effect-emerald {
  background: $tf-brand-bg;
  color: $tf-brand;
}

.effect-amber {
  background: #FEF3C7;
  color: #D97706;
}

.effect-rose {
  background: #FFE4E6;
  color: #E11D48;
}

.effect-blue {
  background: #DBEAFE;
  color: #2563EB;
}

.food-favorite {
  padding: 8rpx;
}

.favorite-icon {
  font-size: 32rpx;
  color: $tf-gray-400;
}

// 加载提示
.loading-tip {
  text-align: center;
  padding: 32rpx;
  margin: 0 24rpx;
  color: $tf-gray-500;
  font-size: 24rpx;
}
</style>
