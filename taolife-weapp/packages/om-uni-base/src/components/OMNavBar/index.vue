<template>
  <view class="nav-bar" :class="{ 'nav-bar-hidden': !isVisible }">
    <view class="nav-bar-container">
      <!-- 左侧按钮区：所有操作按钮都在左侧 -->
      <view class="nav-left">
        <slot name="left">
          <template v-for="btn in leftButtonList" :key="btn.type">
            <view
              v-if="btn.show"
              class="nav-action"
              @click="handleAction(btn)"
            >
              <view class="nav-icon-wrap">
                <view :class="btn.iconClass" class="nav-icon" />
              </view>
            </view>
          </template>
        </slot>
      </view>

      <!-- 右侧标题 -->
      <view class="nav-title">
        <view class="nav-title-content">
          <text class="nav-title-text">{{ title }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';

/** 按钮类型 */
type ButtonType = 'back' | 'home' | 'history' | 'like' | 'collect' | 'share' | 'adjust';

/** 按钮配置项 */
interface NavButton {
  /** 按钮类型 */
  type: string;
  /** 图标 class（UnoCSS 图标类名） */
  iconClass: string;
  /** 是否显示 */
  show: boolean;
}

/** 组件属性 */
const props = withDefaults(
  defineProps<{
    /** 标题文字 */
    title?: string;
    /** 需要显示的按钮列表 */
    buttons?: ButtonType[];
    /** 返回按钮是否直接回首页 */
    backToHome?: boolean;
    /** 是否启用滚动自动隐藏 */
    autoHide?: boolean;
    /** 触发显隐的滚动距离阈值 */
    scrollThreshold?: number;
    /** 点赞状态（受控） */
    isLiked?: boolean;
    /** 收藏状态（受控） */
    isCollected?: boolean;
  }>(),
  {
    title: '',
    buttons: () => ['back'],
    backToHome: false,
    autoHide: true,
    scrollThreshold: 10,
    isLiked: undefined,
    isCollected: undefined,
  }
);

/** 组件事件 */
const emit = defineEmits<{
  (e: 'back'): void;
  (e: 'home'): void;
  (e: 'history'): void;
  (e: 'like', value: boolean): void;
  (e: 'collect', value: boolean): void;
  (e: 'share'): void;
  (e: 'adjust'): void;
  (e: 'checkin'): void;
}>();

/** 导航栏是否可见 */
const isVisible = ref(true);
/** 点赞内部状态 */
const isLiked = ref(props.isLiked ?? false);
/** 收藏内部状态 */
const isCollected = ref(props.isCollected ?? false);
/** 上一次滚动位置 */
let lastScrollY = 0;
/** 是否正在向下滚动 */
let isScrollingDown = false;

/** 按钮默认图标映射 */
const iconMap: Record<ButtonType, string> = {
  back: 'i-solar:arrow-left-outline',
  home: 'i-solar:home-smile-angle-outline',
  history: 'i-solar:clock-circle-outline',
  like: 'i-solar:heart-outline',
  collect: 'i-solar:star-outline',
  share: 'i-solar:share-outline',
  adjust: 'i-solar:tablet-broken',
};

/**
 * 获取按钮图标类名
 * 点赞和收藏按钮根据状态切换实心/空心图标
 */
const getIcon = (type: ButtonType) =>
  type === 'like'
    ? (isLiked.value ? 'i-solar:heart-bold' : 'i-solar:heart-outline')
    : type === 'collect'
      ? (isCollected.value ? 'i-solar:star-bold' : 'i-solar:star-outline')
      : iconMap[type];

/** 计算左侧按钮列表，根据 props 动态生成 */
const leftButtonList = computed<NavButton[]>(() => {
  const list = props.buttons.map((btn) => ({
    type: btn,
    iconClass: getIcon(btn),
    show: true,
  }));
  // backToHome 模式下自动追加首页按钮
  if (props.backToHome && !props.buttons.includes('home')) {
    list.push({ type: 'home', iconClass: iconMap.home, show: true });
  }
  return list;
});

/** 跳转首页，switchTab 失败时降级为 redirectTo */
const goHome = () => {
  uni.switchTab({
    url: '/pages/index/index',
    fail: () => uni.redirectTo({ url: '/pages/index/index' }),
  });
};

/** 返回上一页，backToHome 或无历史页时回首页 */
const goBack = () => {
  if (props.backToHome) {
    goHome();
  } else {
    const pages = getCurrentPages();
    if (pages.length > 1) {
      // 有历史页，正常返回
      uni.navigateBack({ fail: () => goHome() });
    } else {
      // 已是首页栈底，直接跳首页
      goHome();
    }
  }
};

/** 按钮类型 → 执行动作映射 */
const actionMap: Record<string, () => void> = {
  back: () => { goBack(); emit('back'); },
  home: () => { goHome(); emit('home'); },
  history: () => emit('history'),
  like: () => { isLiked.value = !isLiked.value; emit('like', isLiked.value); },
  collect: () => { isCollected.value = !isCollected.value; emit('collect', isCollected.value); },
  share: () => emit('share'),
  adjust: () => emit('adjust'),
  checkin: () => emit('checkin'),
};

/** 按钮点击处理：查表执行对应动作 */
const handleAction = (btn: NavButton) => actionMap[btn.type]?.();

/**
 * 处理页面滚动，向下滚动隐藏导航栏，向上滚动显示
 * @param currentScrollY 当前滚动位置
 */
const handleScroll = (currentScrollY: number) => {
  if (!props.autoHide) return;

  const scrollDiff = currentScrollY - lastScrollY;
  // 滚动距离未达阈值，只更新方向不切换显隐
  if (Math.abs(scrollDiff) < props.scrollThreshold) {
    isScrollingDown = scrollDiff > 0;
    return;
  }

  isScrollingDown = scrollDiff > 0;
  // 向下滚动且超过 50px 时隐藏，向上滚动时立即显示
  if (isScrollingDown && isVisible.value && currentScrollY > 50) {
    isVisible.value = false;
  } else if (!isScrollingDown && !isVisible.value) {
    isVisible.value = true;
  }
  lastScrollY = currentScrollY;
};

/** 由父组件在 onPageScroll 中调用，更新滚动位置 */
const updateScrollTop = (scrollTop: number) => handleScroll(scrollTop);

defineExpose({ updateScrollTop });

export type { ButtonType };
</script>

<style lang="scss">
.nav-bar {
  position: fixed;
  bottom: calc(40rpx + constant(safe-area-inset-bottom));
  bottom: calc(40rpx + env(safe-area-inset-bottom));
  left: 50%;
  transform: translateX(-50%);
  background: $om-surface;
  z-index: 100;
  border-radius: $om-space-20;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.08), 0 0 1rpx rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s ease-in-out, opacity 0.3s ease-in-out;
}

/**
 * 导航栏隐藏状态
 * 向下滚动时隐藏，添加向下的位移和透明度变化
 */
.nav-bar-hidden {
  transform: translateX(-50%) translateY(100%);
  opacity: 0;
}

.nav-bar-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 120rpx;
  padding: 0 24rpx;
  min-width: 600rpx;
}

/**
 * 左侧按钮区域
 */
.nav-left {
  display: flex;
  align-items: center;
}

/**
 * 右侧标题区域
 */
.nav-title {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16rpx;
}

/**
 * 标题内容容器（带边框胶囊样式）
 */
.nav-title-content {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10rpx 32rpx;
  border: 2rpx solid $om-gradient-from;
  border-radius: $om-radius-3xl;
}

/**
 * 标题文字（超长自动省略）
 */
.nav-title-text {
  font-size: 28rpx;
  font-weight: 500;
  color: $om-primary-color;
  max-width: 300rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/**
 * 操作按钮
 */
.nav-action {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8rpx;
}

/**
 * 按钮图标容器
 */
.nav-icon-wrap {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

/**
 * 按钮图标
 */
.nav-icon {
  width: 40rpx;
  height: 40rpx;
  color: $om-primary-color;
}

/**
 * 按钮按下时的反馈效果
 */
.nav-action:active .nav-icon-wrap {
  background: #E0EBE0;  // unique active state color
}
</style>
