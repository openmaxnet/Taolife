# Spring Boot 4.x 开发规范

> 本规范基于道养生活后端服务（taolife-server）v2.0 项目技术栈整理

## 1 基础要求

### 1.1 技术版本要求

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 25 | 编程语言，推荐使用 |
| Spring Boot | 4.x（最新4.0.3） | 应用框架 |
| Spring Framework | 7.x | 核心框架 |
| Spring Security | 7.x | 安全框架 |
| MyBatis-Flex | 最新版 | ORM框架 |
| MySQL | 8.x | 数据库 |
| Redis | 7.x | 缓存 |

### 1.2 Java 版本要求

- **最低要求**：Java 17（LTS）
- **推荐版本**：Java 21 或 Java 25（增强功能）
- Java 25 提供虚拟线程等新特性

```xml
<properties>
    <java.version>25</java.version>
    <maven.compiler.source>25</maven.compiler.source>
    <maven.compiler.target>25</maven.compiler.target>
</properties>
```

### 1.3 Jakarta EE 11 对齐

Spring Boot 4.0 完全对齐 Jakarta EE 11，要求 Servlet 6.1 兼容容器。

**包名变化**：`javax.*` → `jakarta.*`

```java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
```

## 2 Spring Framework 7.0 新特性

### 2.1 JSpecify 空值安全

使用 JSpecify 注解声明空值性，减少 NullPointerException。

```java
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public void updateUser(@NonNull User user) { }
@Nullable public User findUserById(String id) { }
```

### 2.2 编程式 Bean 注册

引入 `BeanRegistrar` 接口，提供编程式 Bean 注册能力。

### 2.3 API 版本控制

内置 REST API 版本控制支持。

```java
@GetMapping(value = "/users", headers = "X-API-Version=1")
public ResponseEntity<List<UserV1>> getUsersV1() { }
```

### 2.4 内置韧性功能

内置重试、熔断、限流等韧性功能。

```java
@Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
public PaymentResult processPayment(PaymentRequest request) { }

@CircuitBreaker(failureRateThreshold = 50, waitDurationInOpenState = "30s")
public ExternalData fetchData(String id) { }

@RateLimiter(limitForPeriod = 100, limitRefreshPeriod = "1s")
public List<Data> search(String query) { }
```

### 2.5 HTTP Interface Clients

声明式 HTTP 客户端接口。

```java
@HttpExchange(url = "https://api.example.com")
public interface UserApiClient {
    @GetExchange("/users/{id}")
    User getUser(@PathVariable String id);
}
```

## 3 Spring Boot 4.0 新特性

### 3.1 完全模块化

实现完全模块化，镜像体积减少约 19%，启动速度提升约 30%。

### 3.2 HTTP Service Clients

自动配置和配置属性支持 HTTP Service Clients。

```yaml
spring:
  http:
    service:
      clients:
        user-api:
          base-url: https://api.example.com
```

### 3.3 改进的观测性

集成 Micrometer 提供更强大的观测能力。

```java
@Counted(value = "order.created")
public Order createOrder(OrderRequest request) { }

@Timed(value = "order.processing.time")
public void processOrder(Order order) { }
```

### 3.4 虚拟线程支持

完整支持 Java 21 的虚拟线程（Project Loom）。

```yaml
spring:
  threads:
    virtual:
      enabled: true
```

```java
@Bean
public Executor taskExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
}
```

### 3.5 性能提升总结

| 指标 | Spring Boot 3.x | Spring Boot 4.0 | 提升 |
|------|----------------|-----------------|------|
| 启动时间 | ~3.5s | ~2.4s | 31% |
| 内存占用 | ~512MB | ~384MB | 25% |
| 镜像体积 | ~387MB | ~312MB | 19% |
| 吞吐量 | ~5000 req/s | ~6500 req/s | 30% |

## 4 迁移指南

### 4.1 从 Spring 6.x 升级到 Spring 7.0

1. 添加 JSpecify 依赖
2. 更新注解使用 JSpecify
3. 迁移到 HTTP Interface Clients

### 4.2 从 Spring Boot 3.x 升级到 Spring Boot 4.0

1. 更新 parent 版本到 4.0.0
2. 更新 Java 版本到 21+
3. 更新 Jakarta EE 包名
4. 启用虚拟线程
5. 更新 HTTP 客户端

## 5 最佳实践

- 使用 JSpecify 注解声明空值性
- 使用 HTTP Interface Clients 替代 RestTemplate
- 使用内置韧性功能（重试、熔断、限流）
- 使用虚拟线程提升并发性能

## 6 总结

| 特性 | Spring 6.x / Boot 3.x | Spring 7.0 / Boot 4.0 | 优势 |
|------|----------------------|----------------------|------|
| 空值安全 | 有限支持 | JSpecify 完整支持 | 减少 NullPointerException |
| HTTP 客户端 | RestTemplate/WebClient | HTTP Interface Clients | 声明式、类型安全 |
| 韧性功能 | 需要第三方库 | 内置支持 | 减少依赖、简化配置 |
| API 版本控制 | 自定义实现 | 内置支持 | 开箱即用 |
| 性能 | 基准 | 提升 30%+ | 更快的响应时间 |
| 内存占用 | 基准 | 减少 25% | 更低的资源消耗 |
| 镜像体积 | 基准 | 减少 19% | 更小的部署包 |
| 虚拟线程 | 有限支持 | 完整支持 | 更高的并发性能 |

---

## 7 开发规范

### 7.1 接口命名规范

| 操作类型 | 方法前缀 | 示例 |
|---------|---------|------|
| 查询 | get | getUserPage、getUserDetail、getUserList |
| 新增 | create | createUser、createArticle |
| 修改 | modify | modifyUserPassword、modifyUserInfo |
| 删除 | remove | removeUser、removeArticle |

**重要说明**：

- 不允许使用 `@GetMapping("/{id}")` 路径参数形式
- 所有接口使用 `@GetMapping` 或 `@PostMapping`
- 查询类接口使用 `?id=xxx` 参数形式
- 接口命名必须使用清晰的业务语义

### 7.2 命名禁用词

以下词汇禁止在方法名中使用：

- save、update、delete（过于简单，无业务语义）
- query、add、edit（不够清晰）
- list、detail（与模块无关）

### 7.3 命名规范

| 类型 | 规范 | 示例 |
|-----|------|------|
| 项目包 | com.taolife | com.taolife.user |
| 类名 | PascalCase | UserController, UserService |
| 接口名 | I + PascalCase | IUserService |
| 方法名 | 动词 + 业务语义 | getUserPage, createUserAccount |
| 变量名 | camelCase | userName, userList |
| 常量名 | UPPER_SNAKE_CASE | MAX_COUNT, DEFAULT_STATUS |
| 数据库表 | tf_ + 下划线 | tf_account, tf_user |
| 数据库字段 | 下划线 | user_name, create_time |

### 7.4 Import 规范

所有类和工具类必须通过 `import` 语句导入后使用，禁止在代码中直接使用完整包名。

**错误**：`cn.hutool.core.codec.Base64.decode()`

**正确**：
```java
import cn.hutool.core.codec.Base64;
Base64.decode()
```

### 7.5 Controller 规范

```java
/**
 * 用户管理控制器
 * 负责处理用户相关的HTTP请求
 *
 * @author 文二
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * 分页获取用户列表
     * 根据条件分页获取用户信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getUserPage")
    public ExceptionResult<Page<User>> getUserPage(UserQueryParam param) {
        return ExceptionResult.success(userService.getUserPage(param));
    }

    /**
     * 获取用户详情
     * 根据用户ID获取用户的详细信息
     *
     * @param id 用户ID
     * @return 用户详细信息
     */
    @GetMapping("/getUserDetail")
    public ExceptionResult<User> getUserDetail(@RequestParam("id") Long id) {
        return ExceptionResult.success(userService.getUserDetail(id));
    }

    /**
     * 创建用户
     * 新增一个用户记录
     *
     * @param param 用户创建参数
     * @return 操作结果
     */
    @PostMapping("/createUser")
    public ExceptionResult<Void> createUser(@Valid @RequestBody UserSaveParam param) {
        userService.createUser(param);
        return ExceptionResult.success();
    }

    /**
     * 修改用户信息
     * 根据用户ID修改用户信息
     *
     * @param id 用户ID
     * @param param 用户修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyUserInfo")
    public ExceptionResult<Void> modifyUserInfo(@RequestParam("id") Long id,
                                       @Valid @RequestBody UserSaveParam param) {
        userService.modifyUserInfo(id, param);
        return ExceptionResult.success();
    }

    /**
     * 删除用户
     * 根据用户ID删除用户（逻辑删除）
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PostMapping("/removeUser")
    public ExceptionResult<Void> removeUser(@RequestParam("id") Long id) {
        userService.removeUser(id);
        return ExceptionResult.success();
    }
}
```

### 7.6 Service 规范

**接口定义**：

```java
public interface IUserService {
    IPage<User> getUserPage(UserQueryParam param);
    User getUserDetail(Long id);
    void createUser(UserSaveParam param);
    void modifyUserInfo(Long id, UserSaveParam param);
    void removeUser(Long id);
}
```

**实现类**：

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;

    /**
     * 分页获取用户列表
     * 根据查询条件分页获取用户数据
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @Override
    public IPage<User> getUserPage(UserQueryParam param) {
        // 调用Mapper的查询方法，由Mapper层处理查询条件构建
        return userMapper.selectPageByParam(param);
    }

    /**
     * 获取用户详情
     * 根据用户ID查询用户详情，如果用户不存在则抛出业务异常
     *
     * @param id 用户ID
     * @return 用户详情
     * @throws BusinessException 如果用户不存在
     */
    @Override
    public User getUserDetail(Long id) {
        // 调用Mapper的查询方法
        User user = userMapper.selectById(id);
        // 用户不存在则抛出异常（使用枚举值）
        if (user == null) {
            throw new BusinessException(ExceptionCode.USER_NOT_FOUND, "用户不存在");
        }
        return user;
    }

    /**
     * 创建用户
     * 保存新的用户记录到数据库
     *
     * @param param 用户创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserSaveParam param) {
        // 创建用户实体
        User user = new User();
        // 设置用户基本信息
        user.setUsername(param.getUsername());
        user.setNickname(param.getNickname());
        user.setPhone(param.getPhone());
        // 设置默认状态为启用（使用枚举值）
        user.setStatus(UserStatusEnum.ENABLED.getCode());
        // 调用Mapper的插入方法
        userMapper.insert(user);
    }

    /**
     * 修改用户信息
     * 根据用户ID更新用户信息
     *
     * @param id 用户ID
     * @param param 用户修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUserInfo(Long id, UserSaveParam param) {
        // 调用Mapper的更新方法，由Mapper层处理动态更新逻辑
        int rows = userMapper.updateUserInfo(id, param.getNickname(), param.getPhone());
        
        // 检查更新是否成功，失败则抛出异常（使用枚举值）
        if (rows <= 0) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "更新用户信息失败");
        }
    }

    /**
     * 删除用户
     * 逻辑删除指定用户
     *
     * @param id 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUser(Long id) {
        // 调用Mapper的删除方法，由Mapper层处理逻辑删除逻辑
        int rows = userMapper.removeById(id);
        
        // 检查删除是否成功，失败则抛出异常（使用枚举值）
        if (rows <= 0) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "删除用户失败");
        }
    }
}
```

**重要说明**：

- **业务代码中不包含任何 SQL 语句**
- **所有数据库操作通过 Mapper 调用，Service层不使用QueryWrapper**
- **Service层负责业务逻辑处理**（如数据校验、异常抛出、事务控制）
- **Mapper层负责数据库操作和查询条件构建**
- **Service层调用Mapper的default方法，由Mapper层封装查询逻辑**
- **保持单一职责原则：Service处理业务，Mapper处理数据访问**

### 7.7 Entity 规范

```java
/**
 * 用户实体类
 * 对应数据库表 tf_user
 *
 * @author 文二
 * @date 2026-03-15
 */
@Data
@Table("tf_account")
public class User implements Serializable {

    /**
     * 用户ID（主键）
     */
    @Id(keyMode = KeyMode.ASSIGN_UUID)
    private String id;

    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 性别（0：未知，1：男，2：女）
     */
    private Integer gender;
    
    /**
     * 会员等级
     */
    private Integer memberLevel;
    
    /**
     * 会员过期时间
     */
    private LocalDateTime memberExpireTime;

    /**
     * 逻辑删除标记（0：未删除，1：已删除）
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 禁用标记（0：启用，1：禁用）
     */
    private Integer isDisabled;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
```

### 7.8 Mapper 规范

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据ID查询用户（带逻辑删除过滤）
     *
     * @param id 用户ID
     * @return 用户对象
     */
    default User selectById(String id) {
        // 构建查询条件：根据ID查询且未被逻辑删除
        return selectOneByQuery(
            QueryWrapper.create()
                .where(User::getId).eq(id)
                .and(User::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 分页查询用户列表
     * 根据查询条件构建分页查询
     *
     * @param param 查询参数
     * @return 分页结果
     */
    default IPage<User> selectPageByParam(UserQueryParam param) {
        // 创建查询条件构建器
        QueryWrapper<User> wrapper = QueryWrapper.create();
        
        // 用户名模糊查询（条件：用户名不为空）
        wrapper.like(StringUtils.hasText(param.getUsername()), User::getUsername, param.getUsername())
               // 状态精确匹配（条件：状态不为空）
               .eq(param.getStatus() != null, User::getStatus, param.getStatus())
               // 按创建时间倒序排列（false表示降序）
               .orderBy(User::getCreateTime, false);
        
        // 执行分页查询
        return selectPage(new Page<>(param.getPage(), param.getPageSize()), wrapper);
    }

    /**
     * 更新用户基本信息
     * 使用动态SQL，只更新非空字段
     *
     * @param id        用户ID
     * @param nickname  昵称
     * @param phone     手机号
     * @return 更新行数
     */
    default int updateUserInfo(String id, String nickname, String phone) {
        // 创建更新对象
        User update = new User();
        // 动态设置昵称（仅当参数不为空时）
        if (nickname != null) {
            update.setNickname(nickname);
        }
        // 动态设置手机号（仅当参数不为空时）
        if (phone != null) {
            update.setPhone(phone);
        }
        // 设置更新时间
        update.setUpdateTime(LocalDateTime.now());

        // 执行更新操作（条件：ID匹配且未被逻辑删除）
        return updateByQuery(
            update,
            QueryWrapper.create()
                .where(User::getId).eq(id)
                .and(User::getIsDeleted).eq(0)
        );
    }

    /**
     * 逻辑删除用户
     *
     * @param id 用户ID
     * @return 更新行数
     */
    default int removeById(String id) {
        // 创建更新对象
        User update = new User();
        // 设置逻辑删除标记（1表示已删除）
        update.setIsDeleted(1);
        // 设置更新时间
        update.setUpdateTime(LocalDateTime.now());

        // 执行更新操作（条件：ID匹配且未被逻辑删除）
        return updateByQuery(
            update,
            QueryWrapper.create()
                .where(User::getId).eq(id)
                .and(User::getIsDeleted).eq(0)
        );
    }
}
```

**Mapper规范要求**：

1. **禁止使用 SELECT \*** - 明确指定查询字段
2. **禁止更新所有字段** - 只更新实际变化的字段
3. **禁止自定义SQL** - 全部使用 MyBatis-Flex 方法
4. **禁止在XML文件中写SQL**
5. **业务代码中不包含任何SQL** - 所有SQL操作通过Mapper调用
6. **使用`default`方法封装查询逻辑** - 所有查询条件构建在Mapper层的default方法中完成
7. **Service层不使用QueryWrapper** - Service层只调用Mapper的default方法，不直接使用QueryWrapper
8. **自动处理逻辑删除** - 查询和更新时自动添加逻辑删除条件
9. **动态字段更新** - 更新方法只更新非空字段，避免覆盖未修改的数据

### 7.9 Param/VO 规范

**Param示例**：

```java
/**
 * 用户保存参数
 * 用于创建和修改用户时的参数验证
 *
 * @author 文二
 * @date 2026-03-15
 */
@Data
public class UserSaveParam {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别（0：未知，1：男，2：女）
     */
    private Integer gender;
}
```

**VO示例**：

```java
/**
 * 用户详情视图对象
 * 用于返回用户详细信息给前端
 *
 * @author 文二
 * @date 2026-03-15
 */
@Data
public class UserDetailVO {
    /**
     * 用户ID
     */
    private String id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 性别（0：未知，1：男，2：女）
     */
    private Integer gender;
    
    /**
     * 会员等级
     */
    private Integer memberLevel;
    
    /**
     * 会员过期时间
     */
    private String memberExpireTime;
}
```

### 7.10 异常处理规范

**业务异常类**：

```java
/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 * 支持使用 ExceptionCode 枚举或自定义异常码
 *
 * @author 文二
 * @date 2026-03-15
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 异常码
     */
    private final String code;

    /**
     * 异常消息
     */
    private final String message;

    /**
     * 创建业务异常（使用默认异常码）
     *
     * @param message 异常消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ExceptionCode.BUSINESS_ERROR.getCode();
        this.message = message;
    }

    /**
     * 创建业务异常（使用枚举异常码）
     *
     * @param code 异常码枚举
     * @param message 异常消息
     */
    public BusinessException(ExceptionCode code, String message) {
        super(message);
        this.code = code.getCode();
        this.message = message;
    }

    /**
     * 创建业务异常（使用自定义异常码）
     *
     * @param code 异常码
     * @param message 异常消息
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
```

**异常码枚举类**：

```java
/**
 * 统一响应状态码枚举
 * 正常状态码以S开头，失败响应使用F开头，错误响应使用E开头，后面跟5位数字
 *
 * @author 文二
 * @date 2026-03-15
 */
public enum ExceptionCode {
    
    // ========== 成功响应 (S开头) ==========
    
    /**
     * 请求成功
     */
    SUCCESS("S00001", "请求成功"),
    
    /**
     * 创建成功
     */
    CREATED("S00002", "创建成功"),
    
    /**
     * 更新成功
     */
    UPDATED("S00003", "更新成功"),
    
    /**
     * 删除成功
     */
    DELETED("S00004", "删除成功"),
    
    /**
     * 查询成功
     */
    QUERY_SUCCESS("S00005", "查询成功"),
    
    // ========== 业务失败 (F开头) ==========
    
    // 通用业务失败 10000-19999
    
    /**
     * 业务处理失败
     */
    BUSINESS_ERROR("F10001", "业务处理失败"),
    
    /**
     * 参数错误
     */
    PARAM_ERROR("F10002", "参数错误"),
    
    /**
     * 数据不存在
     */
    DATA_NOT_FOUND("F10003", "数据不存在"),
    
    /**
     * 数据已存在
     */
    DATA_ALREADY_EXISTS("F10004", "数据已存在"),
    
    /**
     * 操作不允许
     */
    OPERATION_NOT_ALLOWED("F10005", "操作不允许"),
    
    // 用户相关业务失败 11000-11999
    
    /**
     * 用户不存在
     */
    USER_NOT_FOUND("F11001", "用户不存在"),
    
    /**
     * 用户已存在
     */
    USER_ALREADY_EXISTS("F11002", "用户已存在"),
    
    /**
     * 用户已禁用
     */
    USER_DISABLED("F11003", "用户已禁用"),
    
    /**
     * 用户已锁定
     */
    USER_LOCKED("F11004", "用户已锁定"),
    
    /**
     * 密码错误
     */
    PASSWORD_ERROR("F11005", "密码错误"),
    
    /**
     * 原密码错误
     */
    OLD_PASSWORD_ERROR("F11006", "原密码错误"),
    
    /**
     * 密码不匹配
     */
    PASSWORD_NOT_MATCH("F11007", "密码不匹配"),
    
    // 认证相关业务失败 12000-12999
    
    /**
     * 未授权访问
     */
    UNAUTHORIZED("F12000", "未授权访问"),
    
    /**
     * 登录失败
     */
    LOGIN_FAILED("F12001", "登录失败"),
    
    /**
     * 登录凭证无效
     */
    TOKEN_INVALID("F12002", "登录凭证无效"),
    
    /**
     * 登录凭证已过期
     */
    TOKEN_EXPIRED("F12003", "登录凭证已过期"),
    
    /**
     * 登录凭证不存在
     */
    TOKEN_NOT_FOUND("F12004", "登录凭证不存在"),
    
    /**
     * 刷新登录凭证无效
     */
    REFRESH_TOKEN_INVALID("F12005", "刷新登录凭证无效"),
    
    /**
     * 刷新登录凭证已过期
     */
    REFRESH_TOKEN_EXPIRED("F12006", "刷新登录凭证已过期"),
    
    /**
     * 权限不足
     */
    PERMISSION_DENIED("F12007", "权限不足"),
    
    /**
     * 访问被拒绝
     */
    ACCESS_DENIED("F12008", "访问被拒绝"),
    
    // 验证码相关 400000-499999
    
    /**
     * 图形验证码生成失败
     */
    CAPTCHA_CREATE_ERROR("F400100", "图形验证码生成失败"),
    
    /**
     * 图形验证码验证失败
     */
    CAPTCHA_VALIDATE_ERROR("F400101", "图形验证码验证失败"),
    
    /**
     * 短信验证码发送失败
     */
    SMS_CAPTCHA_ERROR("F400102", "短信验证码发送失败"),
    
    /**
     * 请求过于频繁，请稍后再试
     */
    RATE_LIMIT_EXCEEDED("F400103", "请求过于频繁，请稍后再试"),
    
    /**
     * 请求过于频繁，请稍后再试
     */
    REQUEST_TOO_FREQUENT("F400105", "请求过于频繁，请稍后再试"),
    
    // ========== 系统错误 (E开头) ==========
    
    // 系统通用错误 20000-29999
    
    /**
     * 系统错误
     */
    SYSTEM_ERROR("E20001", "系统错误"),
    
    /**
     * 数据库错误
     */
    DATABASE_ERROR("E20002", "数据库错误"),
    
    /**
     * 网络错误
     */
    NETWORK_ERROR("E20003", "网络错误"),
    
    /**
     * 配置错误
     */
    CONFIG_ERROR("E20004", "配置错误"),
    
    // 外部服务错误 21000-21999
    
    /**
     * 外部服务错误
     */
    EXTERNAL_SERVICE_ERROR("E21001", "外部服务错误"),
    
    /**
     * 第三方API错误
     */
    THIRD_PARTY_API_ERROR("E21002", "第三方API错误"),
    
    // 安全相关错误 22000-22999
    
    /**
     * 安全错误
     */
    SECURITY_ERROR("E22001", "安全错误"),
    
    /**
     * 认证错误
     */
    AUTHENTICATION_ERROR("E22002", "认证错误"),
    
    /**
     * 授权错误
     */
    AUTHORIZATION_ERROR("E22003", "授权错误"),
    
    /**
     * 加密错误
     */
    ENCRYPTION_ERROR("E22004", "加密错误"),
    
    // 参数验证错误 23000-23999
    
    /**
     * 参数验证失败
     */
    VALIDATION_ERROR("E23001", "参数验证失败"),
    
    /**
     * 缺少必需参数
     */
    REQUIRED_PARAM_MISSING("E23002", "缺少必需参数"),
    
    /**
     * 参数格式无效
     */
    INVALID_PARAM_FORMAT("E23003", "参数格式无效"),
    
    /**
     * 参数超出范围
     */
    PARAM_OUT_OF_RANGE("E23004", "参数超出范围");

    /**
     * 状态码
     */
    private final String code;

    /**
     * 状态消息
     */
    private final String msg;

    /**
     * 构造函数
     *
     * @param code 状态码
     * @param msg 状态消息
     */
    ExceptionCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取状态消息
     *
     * @return 状态消息
     */
    public String getMsg() {
        return msg;
    }
}
```

### 7.11 统一响应规范

> 项目中使用 `ExceptionResult` 作为统一响应类，包含异常码体系支持。

```java
/**
 * 统一响应结果类
 * 用于封装所有API接口的返回结果
 *
 * @param <T> 数据类型
 * @author 文二
 * @date 2026-03-15
 */
@Data
public class ExceptionResult<T> implements Serializable {

    /**
     * 响应码
     */
    private String code;
    
    /**
     * 响应消息
     */
    private String msg;
    
    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ExceptionResult<T> success() {
        return new ExceptionResult<>(ExceptionCode.SUCCESS.getCode(), ExceptionCode.SUCCESS.getMsg(), null);
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ExceptionResult<T> success(T data) {
        return new ExceptionResult<>(ExceptionCode.SUCCESS.getCode(), ExceptionCode.SUCCESS.getMsg(), data);
    }

    /**
     * 业务失败响应
     *
     * @param msg 失败消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ExceptionResult<T> failed(String msg) {
        return new ExceptionResult<>(ExceptionCode.BUSINESS_ERROR.getCode(), msg, null);
    }

    /**
     * 系统错误响应
     *
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ExceptionResult<T> error(String msg) {
        return new ExceptionResult<>(ExceptionCode.SYSTEM_ERROR.getCode(), msg, null);
    }

    /**
     * 自定义响应
     *
     * @param code 响应码
     * @param message 响应消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ExceptionResult<T> custom(String code, String message) {
        return new ExceptionResult<>(code, message, null);
    }
}
```

**异常码定义（ExceptionCode文件内有枚举）**
```

### 7.12 日志规范

```java
/**
 * 用户管理控制器
 * 负责处理用户相关的HTTP请求
 *
 * @author 文二
 * @date 2026-03-15
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * 获取用户详情
     * 根据用户ID获取用户的详细信息
     *
     * @param id 用户ID
     * @return 用户详细信息
     */
    @GetMapping("/getUserDetail")
    public Result<User> getUserDetail(@RequestParam("id") Long id) {
        // 记录查询请求
        log.info("查询用户详情，id: {}", id);
        try {
            // 调用Service层获取用户详情
            User user = userService.getUserDetail(id);
            // 记录查询成功
            log.info("查询成功");
            return Result.success(user);
        } catch (Exception e) {
            // 记录查询失败异常
            log.error("查询用户详情失败，id: {}", id, e);
            return Result.error("查询失败");
        }
    }
}
```

### 7.13 统一返回格式

- 所有接口必须包含清晰的中文注释
- Controller 返回 Result 包装类型
- Service 必须抛出业务异常而不是返回 null
- 保持单一职责原则

### 7.14 Git 提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 样式调整
refactor: 代码重构
perf: 性能优化
test: 测试相关
chore: 构建/工具/依赖
```

### 7.15 注释规范

**注释要求**：

- 所有方法必须包含清晰的中文注释
- 方法内部关键逻辑部分需要添加注释
- 注释应说明"做什么"而不是"怎么做"
- 复杂业务逻辑必须详细注释
- 避免无意义的注释

**示例**：

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;

    /**
     * 分页获取用户列表
     * 根据查询条件分页获取用户数据
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @Override
    public IPage<User> getUserPage(UserQueryParam param) {
        // 调用Mapper的查询方法，由Mapper层处理查询条件构建
        return userMapper.selectPageByParam(param);
    }

    /**
     * 获取用户详情
     * 根据用户ID查询用户详情，如果用户不存在则抛出业务异常
     *
     * @param id 用户ID
     * @return 用户详情
     * @throws BusinessException 如果用户不存在
     */
    @Override
    public User getUserDetail(Long id) {
        // 调用Mapper的查询方法
        User user = userMapper.selectById(id);
        // 用户不存在则抛出异常（使用枚举值）
        if (user == null) {
            throw new BusinessException(ExceptionCode.USER_NOT_FOUND, "用户不存在");
        }
        return user;
    }

    /**
     * 创建用户
     * 保存新的用户记录到数据库
     *
     * @param param 用户创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserSaveParam param) {
        // 创建用户实体
        User user = new User();
        // 设置用户基本信息
        user.setUsername(param.getUsername());
        user.setNickname(param.getNickname());
        user.setPhone(param.getPhone());
        // 设置默认状态为启用（使用枚举值）
        user.setStatus(UserStatusEnum.ENABLED.getCode());
        // 调用Mapper的插入方法
        userMapper.insert(user);
    }

    /**
     * 修改用户信息
     * 根据用户ID更新用户信息
     *
     * @param id 用户ID
     * @param param 用户修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUserInfo(Long id, UserSaveParam param) {
        // 调用Mapper的更新方法，由Mapper层处理动态更新逻辑
        int rows = userMapper.updateUserInfo(id, param.getNickname(), param.getPhone());
        
        // 检查更新是否成功，失败则抛出异常（使用枚举值）
        if (rows <= 0) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "更新用户信息失败");
        }
    }

    /**
     * 删除用户
     * 逻辑删除指定用户
     *
     * @param id 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUser(Long id) {
        // 调用Mapper的删除方法，由Mapper层处理逻辑删除逻辑
        int rows = userMapper.removeById(id);
        
        // 检查删除是否成功，失败则抛出异常（使用枚举值）
        if (rows <= 0) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "删除用户失败");
        }
    }
}
```

**注释示例说明**：

- 方法注释使用 `/** */` 格式，包含功能描述、参数说明、返回值说明
- 关键逻辑使用 `//` 单行注释说明
- 复杂业务逻辑需要详细注释
- 避免注释与代码重复（如 `i++; // i加1`）
- 注释应简洁明了，便于维护
