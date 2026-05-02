<script setup lang="ts">
import { onLaunch, onShow, onHide } from "@dcloudio/uni-app";
import {
  launchSilentLogin,
  checkLocalAuthState,
} from "@/utils/authManager";
import { useTheme } from "@/composables/useTheme";
import { miniAppMonitor } from "@/utils/monitor";

const { setNavStyle, onThemeChange } = useTheme();

onLaunch(() => {
  console.log("App Launch");
  miniAppMonitor.start();
  setNavStyle();
  onThemeChange();
  // 同步检查本地 token 状态（立即设置 isAuth，避免 UI 闪烁）
  checkLocalAuthState();
  // 后台异步执行静默登录（强制服务端验证，不短路）
  launchSilentLogin().then((success) => {
    if (success) {
      console.log("Silent login succeeded");
    } else {
      console.log("Silent login failed");
    }
  });
});
onShow(() => {
  console.log("App Show");
});
onHide(() => {
  console.log("App Hide");
});
</script>
<style lang="scss">
/**
 * 主题 CSS 变量 - 浅色（默认）
 */
page {
  // 品牌色
  --tf-brand: #059669;
  --tf-brand-dark: #064E3B;
  --tf-brand-700: #047857;
  --tf-brand-light: #10B981;
  --tf-brand-bg: #ECFDF5;
  --tf-brand-bg-light: #F0FDF4;
  --tf-brand-alpha-4: rgba(5, 150, 105, 0.04);
  --tf-brand-alpha-6: rgba(5, 150, 105, 0.06);
  --tf-brand-alpha-8: rgba(5, 150, 105, 0.08);
  --tf-brand-alpha-10: rgba(5, 150, 105, 0.1);
  --tf-brand-alpha-15: rgba(5, 150, 105, 0.15);
  --tf-brand-alpha-20: rgba(5, 150, 105, 0.2);
  --tf-brand-alpha-35: rgba(5, 150, 105, 0.35);
  --tf-brand-alpha-40: rgba(5, 150, 105, 0.4);

  // 渐变色
  --tf-gradient-from: #2D5A27;
  --tf-gradient-to: #4F7942;

  // 中性灰
  --tf-gray-50: #F9FAFB;
  --tf-gray-100: #F5F5F5;
  --tf-gray-200: #F3F4F6;
  --tf-gray-300: #E5E7EB;
  --tf-gray-400: #D1D5DB;
  --tf-gray-500: #9CA3AF;
  --tf-gray-600: #6B7280;
  --tf-gray-700: #4B5563;
  --tf-gray-800: #374151;
  --tf-gray-900: #1F2937;

  // 表面色
  --tf-surface: #ffffff;
  --tf-surface-dim: #F3F7F2;
  --tf-page-bg: #f7f8fa;

  // 功能色
  --tf-warning: #F59E0B;
  --tf-warning-bg: #FEF3C7;
  --tf-warning-dark: #D97706;
  --tf-warning-alpha-10: rgba(245, 158, 11, 0.1);

  // 渐变
  --tf-gradient-brand: linear-gradient(135deg, #2D5A27, #4F7942);
  --tf-gradient-brand-alt: linear-gradient(135deg, #059669, #2D5A27);
  --tf-gradient-progress: linear-gradient(90deg, #2D5A27, #4F7942);

  // 阴影
  --tf-shadow-card: 0 2rpx 12rpx rgba(45, 90, 39, 0.08);
  --tf-shadow-card-lg: 0 4rpx 16rpx rgba(45, 90, 39, 0.12);
  --tf-shadow-card-light: 0 2rpx 12rpx rgba(45, 90, 39, 0.05);
  --tf-shadow-bottom: 0 -2rpx 8rpx rgba(0, 0, 0, 0.08);

  // TDesign 品牌色覆盖
  --td-brand-color: var(--tf-brand);
  --td-brand-color-active: var(--tf-brand-700);
  --td-brand-color-disabled: var(--tf-brand-alpha-40);
  --td-brand-color-light: var(--tf-brand-bg);
}

/**
 * 主题 CSS 变量 - 暗色
 */
@media (prefers-color-scheme: dark) {
  page {
    // 品牌色
    --tf-brand: #34d399;
    --tf-brand-dark: #6ee7b7;
    --tf-brand-700: #10b981;
    --tf-brand-light: #6ee7b7;
    --tf-brand-bg: rgba(52, 211, 153, 0.08);
    --tf-brand-bg-light: rgba(52, 211, 153, 0.05);
    --tf-brand-alpha-4: rgba(52, 211, 153, 0.04);
    --tf-brand-alpha-6: rgba(52, 211, 153, 0.06);
    --tf-brand-alpha-8: rgba(52, 211, 153, 0.08);
    --tf-brand-alpha-10: rgba(52, 211, 153, 0.1);
    --tf-brand-alpha-15: rgba(52, 211, 153, 0.15);
    --tf-brand-alpha-20: rgba(52, 211, 153, 0.2);
    --tf-brand-alpha-35: rgba(52, 211, 153, 0.35);
    --tf-brand-alpha-40: rgba(52, 211, 153, 0.4);

    // 渐变色
    --tf-gradient-from: #065f46;
    --tf-gradient-to: #047857;

    // 中性灰
    --tf-gray-50: #1a1a1a;
    --tf-gray-100: #1e1e1e;
    --tf-gray-200: #2a2a2a;
    --tf-gray-300: #333333;
    --tf-gray-400: #4a4a4a;
    --tf-gray-500: #6b6b6b;
    --tf-gray-600: #8b8b8b;
    --tf-gray-700: #ababab;
    --tf-gray-800: #d4d4d4;
    --tf-gray-900: #f0f0f0;

    // 表面色
    --tf-surface: #1e1e1e;
    --tf-surface-dim: #252525;
    --tf-page-bg: #111111;

    // 功能色
    --tf-warning: #FBBF24;
    --tf-warning-bg: rgba(251, 191, 36, 0.15);
    --tf-warning-dark: #F59E0B;
    --tf-warning-alpha-10: rgba(251, 191, 36, 0.1);

    // 渐变
    --tf-gradient-brand: linear-gradient(135deg, #065f46, #047857);
    --tf-gradient-brand-alt: linear-gradient(135deg, #10b981, #065f46);
    --tf-gradient-progress: linear-gradient(90deg, #065f46, #047857);

    // 阴影
    --tf-shadow-card: 0 2rpx 12rpx rgba(0, 0, 0, 0.3);
    --tf-shadow-card-lg: 0 4rpx 16rpx rgba(0, 0, 0, 0.4);
    --tf-shadow-card-light: 0 2rpx 12rpx rgba(0, 0, 0, 0.2);
    --tf-shadow-bottom: 0 -2rpx 8rpx rgba(0, 0, 0, 0.3);
  }
}

/**
 * 全局页面容器样式
 * 所有使用 .page-container 和 .content-area 的页面都会自动应用
 */

.page-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  min-height: 100vh;
  box-sizing: border-box;
  background: $tf-page-bg-color;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}

.content-area {
  flex: 1;
  width: 100%;
  box-sizing: border-box;
}

/* 全局隐藏滚动条 - 微信小程序 */
::-webkit-scrollbar {
  display: none !important;
  width: 0 !important;
  height: 0 !important;
  -webkit-appearance: none;
  background: transparent;
}
</style>
