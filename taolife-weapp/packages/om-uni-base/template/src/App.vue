<script setup lang="ts">
import { onLaunch } from '@dcloudio/uni-app'
import { useTheme } from '@om/uni-base'

const { setNavStyle, onThemeChange } = useTheme()

onLaunch(() => {
  console.log('App Launch')
  setNavStyle()
  onThemeChange()
})
</script>

<style lang="scss">
@import '@om/uni-base/src/styles/theme.scss';

page {
  height: 100vh;
}
</style>
