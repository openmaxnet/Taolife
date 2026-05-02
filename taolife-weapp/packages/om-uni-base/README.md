# @om/uni-base

OpenMax uni-app 基础框架 — 通用 request、auth、composables、组件、样式 token。

适用于 uni-app 3.0 (Vue 3 + TypeScript) 项目，支持微信小程序等平台。

## 快速开始

### 1. 安装

在 pnpm workspace monorepo 中：

```json
{
  "dependencies": {
    "@om/uni-base": "workspace:*"
  }
}
```

### 2. 配置 request + auth

在 `src/utils/authManager.ts` 中初始化：

```ts
import { createAuthManager, createWechatAuthStrategy, configureRequest } from '@om/uni-base'
import type { AuthTokens } from '@om/uni-base'

// 微信小程序策略
const strategy = createWechatAuthStrategy({
  loginFn: async () => {
    const { code } = await uni.login({ provider: 'weixin' })
    return yourLoginApi(code)
  },
  refreshFn: (token) => yourRefreshApi(token),
})

const auth = createAuthManager(strategy)

// 配置 request 模块
configureRequest({
  getBaseUrl: () => 'https://your-api.example.com',
  handleAuthError: auth.handleAuthError,
})

export const { enAuth, isAuth, launchSilentLogin } = auth
```

### 3. 在页面中使用

```vue
<script setup lang="ts">
import { usePageLayout } from '@om/uni-base'
import { useListLoader } from '@om/uni-base'
import { getArticleList } from '@/api/article'

const { navBarRef, contentPaddingTop } = usePageLayout()
const { list, loading, refresh, loadMore } = useListLoader({
  fetchFn: (page) => getArticleList(page, 10),
})
</script>
```

## 模块说明

### Core（核心层）

| 模块 | 导出 | 说明 |
|------|------|------|
| `request` | `request`, `get`, `post`, `configureRequest` | HTTP 请求封装，自动 Token 携带、401 重试 |
| `storage` | `setToken`, `getToken`, `setStorageKeyPrefix`, ... | Storage 工具，key 前缀可配置 |
| `constants` | `BaseExceptionCode`, `BaseExceptionCodeMsg`, `isAuthError`, ... | 通用错误码定义 |
| `types` | `RequestOptions`, `PageParam`, `PageResult`, `ExceptionResult` | 类型定义 |
| `format` | `formatCount`, `parseTagsJson`, `parseTagsCsv`, `formatDate` | 格式化工具 |

### Auth（认证层）

| 模块 | 说明 |
|------|------|
| `createAuthManager` | 工厂函数，接受 `loginFn`/`refreshFn`，返回 auth 管理器实例 |
| `createWechatAuthStrategy` | 微信小程序登录策略，自动处理 `uni.login` + code 换 token |

```ts
// AuthManager 实例方法
interface AuthManager {
  isAuth: Ref<boolean>
  enAuth(): Promise<boolean>           // 确保已登录（未登录则触发登录）
  handleAuthError(): Promise<boolean>  // Token 过期时尝试刷新
  launchSilentLogin(): void            // 启动时静默登录
  checkLocalAuthState(): boolean       // 检查本地 token 状态
  onLoginSuccess(tokens: AuthTokens): void  // 登录成功回调
}
```

### Composables（组合式函数）

| 模块 | 说明 |
|------|------|
| `usePageLayout` | 页面布局（导航栏高度、胶囊位置计算） |
| `useDetailLoader<T>` | 详情页数据加载（支持 ensureAuth 注入） |
| `useListLoader<T>` | 分页列表加载（自动分页、刷新、加载更多） |
| `useCachedResource<T>` | 带缓存的数据获取（Storage + TTL） |
| `useInteraction` | 点赞/收藏（通过 InteractionApi 注入 API） |

### Platform（平台适配）

| 模块 | 说明 |
|------|------|
| `capsule` | 微信胶囊按钮位置计算（`getCapsulePosition`、`getNavbarHeight` 等） |

## 项目模板

`template/` 目录包含一个完整的项目模板，可以直接复制使用：

```bash
# 复制模板到新项目
cp -r packages/om-uni-base/template/ my-new-project/
cd my-new-project

# 修改 manifest.json 中的 appid
# 修改 src/utils/config.ts 中的 BASE_URL
# 修改 src/utils/authManager.ts 中的登录 API

# 安装依赖
pnpm install

# 开发
pnpm run dev:mp-weixin

# 构建
pnpm run build:mp-weixin
```

## 设计原则

1. **依赖注入**：auth 策略、API 函数通过参数注入，不硬编码具体实现
2. **工厂模式**：`createAuthManager` 返回实例，每个项目可独立配置
3. **薄重导出**：项目中可创建 `utils/index.ts` 重导出，页面无需感知 `@om/uni-base`
4. **可配置前缀**：Storage key 前缀默认 `om_`，可通过 `setStorageKeyPrefix()` 自定义
5. **零业务耦合**：不包含任何业务逻辑，纯基础设施

## 依赖

- **peerDependencies**: `vue ^3.4.0`
- 运行时依赖 `uni.request`、`uni.login` 等 uni-app API
