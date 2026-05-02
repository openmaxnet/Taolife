<template>
  <view class="page-container">
    <view>
      <!-- 自定义导航栏 -->
      <TLTopBar title="养生经络" />

      <!-- 加载失败时显示 -->
      <TLReload v-if="loadFailed" @reload="handleReload" />

      <!-- 空状态时显示 -->
      <TLEmpty
        v-else-if="!loading && articleList.length === 0"
        title="暂无文章"
        description="该分类下还没有文章，敬请期待~"
      />

    <!-- 主内容区 -->
    <scroll-view v-else class="content-area" scroll-y @scrolltolower="loadMore" :style="{ paddingTop: contentPaddingTop + 'px' }">
<!-- 分类筛选 -->
      <view class="filter-container">
        <TLFilterBar mode="tabs" :dimensions="filterDimensions" @change="handleFilterChange" />
      </view>

      <!-- 文章列表 -->
        <view class="wisdom-article-grid">
          <view
            v-for="item in articleList"
            :key="item.id"
            class="wisdom-article-card"
            @click="goToDetail(item.id)"
          >
            <view class="wisdom-article-cover-wrap">
              <image class="wisdom-article-cover" :src="item.coverImageUrl" mode="aspectFill" />
              <view v-if="item.isRecommended === 1" class="wisdom-article-badge badge-recommend">
                <view class="i-solar:medal-ribbon-broken badge-icon" />
                <text class="wisdom-article-badge-text">推荐</text>
              </view>
            </view>
            <view class="wisdom-article-info">
              <view class="wisdom-article-title-row">
                <view v-if="item.isFeatured === 1" class="wisdom-article-tag-featured">
                  <view class="i-solar:fire-linear featured-icon" />
                  <text class="featured-text">精选</text>
                </view>
                <text class="wisdom-article-title">{{ item.title }}</text>
              </view>
              <view class="wisdom-article-meta">
                <text class="wisdom-article-tag"># {{ item.categoryName }}</text>
                <text class="wisdom-article-read">{{ formatCount(item.readCount) }} 阅读</text>
              </view>
            </view>
          </view>
        </view>

      <!-- 加载更多提示 -->
      <view v-if="loading" class="loading-tip">
        <text>加载中...</text>
      </view>
      <view v-else-if="noMore" class="loading-tip">
        <text>没有更多了</text>
      </view>
    </scroll-view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { onPullDownRefresh } from '@dcloudio/uni-app';
import TLReload from '@/components/TLReload/index.vue';
import TLEmpty from '@/components/TLEmpty/index.vue';
import TLTopBar from '@/components/TLTopBar/index.vue';
import TLFilterBar from '@/components/TLFilterBar/index.vue';
import { getArticlePage } from '@/api/wisdom/article';
import { usePageLayout } from '@/composables/usePageLayout';
import { formatCount } from '@/utils/format';
import type { ArticleListItem } from '@/types/biz/wisdom/article';
import type { FilterDimension, FilterParams } from '@/types/components/filterBar';


const { contentPaddingTop } = usePageLayout();

// 筛选维度配置
const filterDimensions: FilterDimension[] = [
  {
    key: 'category',
    options: [
      { label: '全部', value: 0 },
      { label: '养生方法', value: 1 },
      { label: '四季养生', value: 2 },
      { label: '节气养生', value: 3 },
      { label: '食疗方案', value: 4 },
      { label: '中医知识', value: 5 },
    ],
    defaultValue: 0,
  },
];

// 文章列表
const articleList = ref<ArticleListItem[]>([]);

// 当前选中分类
const activeCategory = ref(0);

/**
 * 筛选变化回调
 */
const handleFilterChange = (params: FilterParams) => {
  activeCategory.value = (params.category ?? 0) as number;
  fetchArticleList(true);
};

// 分页参数
const pageNo = ref(1);
const pageSize = 10;
const total = ref(0);
const loading = ref(false);
const noMore = ref(false);
const loadFailed = ref(false);

/**
 * 获取文章列表
 */
const fetchArticleList = async (reset = false) => {
  if (loading.value) return;

  loading.value = true;
  loadFailed.value = false;

  try {
    if (reset) {
      pageNo.value = 1;
      noMore.value = false;
      articleList.value = [];
    }

    const param = {
      category: activeCategory.value,
      pageNo: pageNo.value,
      pageSize
    };

    const result = await getArticlePage(param);

    if (reset) {
      articleList.value = result.list || [];
    } else {
      articleList.value = [...articleList.value, ...(result.list || [])];
    }

    total.value = result.total || 0;

    // 判断是否还有更多
    if (articleList.value.length >= total.value) {
      noMore.value = true;
    }
  } catch (error: any) {
    console.error('获取文章列表失败:', error);
    loadFailed.value = true;
  } finally {
    loading.value = false;
  }
};

/**
 * 重新加载
 */
const handleReload = () => {
  fetchArticleList(true);
};

/**
 * 加载更多
 */
const loadMore = () => {
  if (!noMore.value && !loading.value) {
    pageNo.value++;
    fetchArticleList();
  }
};

/**
 * 跳转到详情页
 */
const goToDetail = (id: string) => {
  uni.navigateTo({
    url: `/pages/wisdom/article/ArticleDetail?id=${id}`
  });
};

/**
 * 下拉刷新
 */
onPullDownRefresh(async () => {
  await fetchArticleList(true);
  uni.stopPullDownRefresh();
});

// 页面加载
onMounted(() => {
  // 登录状态由 useAuth composable 自动检查
  fetchArticleList(true);
});
</script>

<style lang="scss">
.page-container {
  background: $tf-page-bg-color;
}

.filter-container {
  margin: 0 24rpx;
}

// 文章列表
.wisdom-article-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
  margin: 0 24rpx;
}

.wisdom-article-card {
  background: $tf-surface;
  border-radius: $tf-radius-lg;
  overflow: hidden;
}

.wisdom-article-cover-wrap {
  position: relative;
}

.wisdom-article-cover {
  width: 100%;
  height: 256rpx;
}

.wisdom-article-badge {
  position: absolute;
  top: 16rpx;
  left: 16rpx;
  display: flex;
  align-items: center;
  gap: 4rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
}

.badge-recommend {
  background: rgba(45, 90, 39, 0.85);
}

.badge-icon {
  font-size: 20rpx;
  color: $tf-surface;
}

.wisdom-article-badge-text {
  font-size: 20rpx;
  color: $tf-surface;
}

.wisdom-article-title-row {
  line-height: 1.4;
}

.wisdom-article-tag-featured {
  display: inline-flex;
  align-items: center;
  gap: 2rpx;
  padding: 2rpx 10rpx;
  background: rgba(180, 120, 40, 0.12);
  border-radius: 6rpx;
  vertical-align: middle;
  margin-right: 8rpx;
}

.featured-icon {
  font-size: 20rpx;
  color: rgba(180, 120, 40, 0.9);
}

.featured-text {
  font-size: 18rpx;
  color: rgba(180, 120, 40, 0.9);
  white-space: nowrap;
}

.wisdom-article-info {
  padding: 12rpx;
}

.wisdom-article-title {
  display: inline;
  font-size: 24rpx;
  color: $tf-gray-900;
  font-weight: 700;
  line-height: 1.4;
}

.wisdom-article-meta {
  display: flex;
  gap: 16rpx;
  margin-top: 16rpx;
}

.wisdom-article-tag {
  font-size: 20rpx;
  color: $tf-gray-600;
}

.wisdom-article-read {
  font-size: 20rpx;
  color: $tf-gray-500;
}

// 加载提示
.loading-tip {
  text-align: center;
  padding: 24rpx;
  margin: 0 24rpx;
  color: $tf-gray-500;
  font-size: 24rpx;
}
</style>
