/**
 * 暗色模式主题适配
 * 跟随设备设置自动切换
 */
export const useTheme = () => {
  const setNavStyle = () => {
    const theme = uni.getSystemInfoSync().theme
    uni.setNavigationBarColor({
      frontColor: theme === 'dark' ? '#ffffff' : '#000000',
      backgroundColor: theme === 'dark' ? '#1E1E1E' : '#FFFFFF',
    })
  }

  const onThemeChange = (callback?: (theme: string) => void) => {
    uni.onThemeChange((result: { theme: string }) => {
      setNavStyle()
      callback?.(result.theme)
    })
  }

  return { setNavStyle, onThemeChange }
}
