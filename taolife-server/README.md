> 完整项目文档请参阅 [根目录 README](../README.md)

# 道养生活后端服务（taolife-server）

## 项目介绍

道养生活后端服务是基于 Spring Boot 4.x + Java 25 开发 RESTful API 服务，为小程序和管理后台提供数据支撑。采用单体应用模块化架构，分为启动模块、公共模块和业务模块。

## 模块架构

```
taolife-server/
├── taolife-app/            # 启动模块（主入口）
│   ├── src/main/java/
│   │   └── com/taolife/
│   │       └── TaoLifeApplication.java  # 启动类
│   ├── src/main/resources/
│   │   ├── application.yml              # 主配置文件
│   │   ├── application-dev.yml          # 开发环境配置
│   │   ├── application-prd.yml          # 生产环境配置
│   │   └── log4j2.xml                   # 日志配置
│   └── pom.xml                          # 启动模块pom
│
├── taolife-common/            # 公共模块（通用工具、配置和基础类）
│   ├── src/main/java/
│   │   └── com/taolife/common/
│   │       ├── config/                   # 配置类
│   │       │   ├── CorsAutoConfig.java   # 跨域配置
│   │       │   ├── CosConfig.java        # 腾讯云COS配置
│   │       │   ├── LoggingAutoConfig.java # 日志配置
│   │       │   ├── MybatisFlexConfig.java # MyBatis-Flex配置
│   │       │   ├── SecurityConfig.java   # 安全配置
│   │       │   ├── TaoLifeProperties.java # 应用配置属性
│   │       │   ├── WxApiConfig.java      # 微信API配置
│   │       │   └── CaptchaProperties.java # 验证码配置
│   │       ├── advice/                   # AOP切面
│   │       │   ├── RequestLoggingAdvice.java  # 请求日志切面
│   │       │   └── ResponseLoggingAdvice.java # 响应日志切面
│   │       ├── annotation/               # 自定义注解
│   │       │   └── AuthSkip.java       # 跳过认证注解
│   │       ├── constant/                 # 常量定义
│   │       │   ├── RedisConstant.java    # Redis常量
│   │       │   └── WxConstant.java       # 微信常量
│   │       ├── exception/                 # 异常处理
│   │       │   ├── BusinessException.java # 业务异常
│   │       │   ├── ExceptionCode.java    # 异常码
│   │       │   ├── ExceptionResult.java  # 异常结果
│   │       │   ├── GlobalBaseExceptionHandler.java # 全局异常处理
│   │       │   └── ResponseInterceptor.java # 响应拦截器
│   │       ├── security/                 # 安全认证
│   │       │   ├── JwtAuthenticationFilter.java # JWT认证过滤器
│   │       │   ├── JwtManager.java       # JWT管理器
│   │       │   ├── SecurityConfig.java   # 安全配置
│   │       │   ├── SecurityProperties.java # 安全配置属性
│   │       │   ├── UserContext.java     # 用户上下文
│   │       │   └── UserInfo.java        # 用户信息
│   │       └── utils/                    # 工具类
│   │           ├── CaptchaUtil.java     # 验证码工具
│   │           ├── CosSignUtil.java     # COS签名工具
│   │           ├── CosStsUtil.java      # COS临时凭证工具
│   │           ├── ImageCaptchaGenerator.java # 图形验证码生成器
│   │           ├── PageResult.java      # 分页结果
│   │           ├── PageUtils.java       # 分页工具
│   │           ├── UsernameGenerator.java # 用户名生成器
│   │           ├── UUIDKeyGenerator.java # UUID键生成器
│   │           └── WxMiniProgramApi.java # 微信小程序API
│   └── pom.xml
│
├── taolife-biz/               # 业务模块根目录
│   ├── pom.xml
│   │
│   └── taolife-identity/          # 认证授权模块
│       └── src/main/java/com/taolife/auth/
│           ├── controller/
│           │   └── AccountController.java
│           ├── service/
│           │   ├── IAccountService.java
│           │   ├── IWxLoginService.java
│           │   └── impl/
│           │       ├── AccountServiceImpl.java
│           │       └── WxLoginServiceImpl.java
│           ├── mapper/
│           │   ├── AccountMapper.java
│           │   └── UserMapper.java
│           ├── entity/
│           │   ├── Account.java
│           │   └── User.java
│           ├── param/
│           │   ├── UserInfoParam.java
│           │   └── WxLoginParam.java
│           └── vo/
│               ├── UserInfoVO.java
│               └── WxLoginVO.java
│
└── pom.xml                    # 父pom
```

### 模块职责说明

#### taolife-app - 启动模块
- 应用启动入口
- 配置文件管理
- 环境配置（dev/prd）
- 日志配置

#### taolife-common - 公共模块
- **配置类**：跨域、COS、日志、MyBatis-Flex、安全、微信、验证码等配置
- **AOP切面**：请求日志、响应日志
- **自定义注解**：忽略认证等注解
- **常量定义**：Redis、微信等常量
- **异常处理**：业务异常、全局异常处理、响应拦截
- **安全认证**：JWT认证、安全配置、用户上下文
- **工具类**：验证码、COS、分页、用户名生成、微信API等工具

#### taolife-identity - 认证授权模块
- **账号登录**：账号登录、Token管理
- **微信登录**：微信小程序登录、用户信息同步
- **用户信息**：用户基本信息管理

## 技术栈

| 技术 | 版本 | 说明 |
|-----|------|------|
| Java | 25 | 编程语言 |
| Spring Boot | 4.x（最新4.0.3） | 应用框架 |
| Spring Security | 7.x | 安全框架 |
| MyBatis-Flex | 最新版 | ORM框架 |
| MySQL | 9.x | 数据库 |
| Redis | 8.x | 缓存 |
| Lombok | 最新版 | 代码生成 |
| Hutool | 最新版 | 工具库 |

## 开发指南

### 安装依赖

项目使用 Maven 管理依赖，确保已安装 Java 25。

```bash
mvn clean install
```

### 运行项目

项目支持多环境配置，通过 `spring.profiles.active` 指定环境：

```bash
# 开发环境（默认）
mvn spring-boot:run

# 生产环境
mvn spring-boot:run -Dspring-boot.run.profiles=prd

# 打包
mvn clean package -DskipTests

# 指定环境打包
mvn clean package -DskipTests -Dspring-boot.run.profiles=prd

# Docker构建
docker build -t taolife-server:latest .
```

### 环境配置说明

项目提供两种环境配置：

- **开发环境（dev）**：本地开发使用，端口 8080，数据库和 Redis 连接本地服务
- **生产环境（prd）**：生产环境使用，端口 8080，使用环境变量配置敏感信息

生产环境需要配置的环境变量：

```bash
# 数据库配置
DB_URL=jdbc:mysql://prod-db-server:3306/taolife?...
DB_USERNAME=taolife
DB_PASSWORD=your_password

# Redis 配置
REDIS_HOST=prod-redis-server
REDIS_PORT=6379
REDIS_DATABASE=0
REDIS_PASSWORD=your_redis_password

# JWT 配置
JWT_SECRET=your_jwt_secret
JWT_EXPIRE_TIME=7200000
JWT_REFRESH_TIME=3600000

# 微信小程序配置
WX_MINI_PROGRAM_APPID=your_app_id
WX_MINI_PROGRAM_APPSECRET=your_app_secret
```

### 接口文档

启动项目后访问：
- 开发环境：http://localhost:8080/swagger-ui.html
- 生产环境：接口文档默认禁用（可通过环境变量 `springdoc.api-docs.enabled=true` 启用）

## 注意事项

1. 所有数据库表使用 tf_ 前缀
2. 主键ID使用 Binary(16) 类型（UUID）
3. 是否字段统一使用 0/1
4. 类型字段使用正整数
5. 所有接口必须添加 Swagger 注解
6. Controller 必须返回 Result 包装
7. Service 必须抛出业务异常而不是返回null
8. 日志必须包含关键参数和异常堆栈
