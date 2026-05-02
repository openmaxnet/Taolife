import { createSSRApp } from "vue";
import App from "./App.vue";
import "uno.css";
import "@tdesign/uniapp/common/style/theme/index.css";

export function createApp() {
  const app = createSSRApp(App);
  return {
    app,
  };
}
