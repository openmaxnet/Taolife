/**
 * 微信小程序胶囊按钮工具类
 * 用于动态计算胶囊按钮位置，以便实现自定义导航栏等样式
 */

interface SystemInfo {
  screenWidth: number;
  statusBarHeight: number;
  safeAreaInsets?: {
    top: number;
    left: number;
    right: number;
    bottom: number;
  };
}

export interface CapsulePosition {
  width: number;
  height: number;
  left: number;
  right: number;
  top: number;
  bottom: number;
  screenWidth: number;
  safeAreaTop: number;
}

const getSystemInfo = (): SystemInfo => {
  // #ifndef MP-WEIXIN
  return {
    screenWidth: 375,
    statusBarHeight: 44,
    safeAreaInsets: { top: 44, left: 0, right: 375, bottom: 34 },
  };
  // #endif

  // #ifdef MP-WEIXIN
  let screenWidth = 375;
  let statusBarHeight = 44;
  let safeAreaTop = 0;

  try {
    const windowInfo = uni.getWindowInfo();
    screenWidth = windowInfo.screenWidth || 375;
    statusBarHeight = windowInfo.statusBarHeight || 44;
    safeAreaTop = windowInfo.safeArea?.top || 0;
  } catch {
    const info = uni.getSystemInfoSync();
    screenWidth = info.screenWidth || 375;
    statusBarHeight = info.statusBarHeight || 44;
    safeAreaTop = info.safeAreaInsets?.top || 0;
  }

  return {
    screenWidth,
    statusBarHeight,
    safeAreaInsets: { top: safeAreaTop, left: 0, right: screenWidth, bottom: 0 },
  };
  // #endif
};

export const getCapsulePosition = (): CapsulePosition => {
  const systemInfo = getSystemInfo();

  // #ifdef MP-WEIXIN
  const menuButtonBoundingClientRect = uni.getMenuButtonBoundingClientRect();
  const { width, height, left, top, right } = menuButtonBoundingClientRect;

  return {
    width, height, left, right, top,
    bottom: top + height,
    screenWidth: systemInfo.screenWidth,
    safeAreaTop: systemInfo.safeAreaInsets?.top || 0,
  };
  // #endif

  // #ifndef MP-WEIXIN
  return {
    width: 0, height: 0, left: 0,
    right: systemInfo.screenWidth,
    top: systemInfo.statusBarHeight || 0,
    bottom: (systemInfo.statusBarHeight || 0) + 44,
    screenWidth: systemInfo.screenWidth,
    safeAreaTop: systemInfo.safeAreaInsets?.top || 0,
  };
  // #endif
};

export const getNavbarHeight = (): number => {
  const capsule = getCapsulePosition();
  return capsule.bottom - capsule.safeAreaTop;
};

export const getNavbarTop = (): number => {
  const capsule = getCapsulePosition();
  return capsule.safeAreaTop;
};

export const getRightContentStart = (): number => {
  const capsule = getCapsulePosition();
  return capsule.right;
};

export const getContentPaddingTop = (extraGap = 16): number => {
  const capsule = getCapsulePosition()
  return capsule.top + (capsule.bottom - capsule.safeAreaTop) + extraGap
}

export const watchCapsulePosition = (
  callback: (position: CapsulePosition) => void
): (() => void) => {
  callback(getCapsulePosition());

  // #ifdef MP-WEIXIN
  uni.onWindowResize(() => {
    setTimeout(() => {
      callback(getCapsulePosition());
    }, 100);
  });
  // #endif

  return () => {
    // #ifdef MP-WEIXIN
    uni.offWindowResize(() => {});
    // #endif
  };
};
