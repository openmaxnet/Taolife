import { createApp, defineComponent, h, Suspense } from 'vue'
import AppLoading from './components/common/AppLoading.vue'
import App from './App.vue'
import { initTelemetry } from './utils/telemetry'

// 用 Suspense 包裹异步根组件
const Root = defineComponent({
  setup() {
    return () =>
      h(Suspense, null, {
        default: () => h(App),
        fallback: () => h(AppLoading),
      })
  },
})

const app = createApp(Root)
app.mount('#app')

// 初始化 Web Vitals 采集
initTelemetry()
