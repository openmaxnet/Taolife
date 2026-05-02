package com.taolife.common.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应状态码枚举
 * 正常状态码以S开头，失败响应使用F开头，错误响应使用E开头，后面跟5位数字
 */
public enum ExceptionCode {

    // 成功响应 (S开头)
    SUCCESS("S00001", "请求成功"),
    CREATED("S00002", "创建成功"),
    UPDATED("S00003", "更新成功"),
    DELETED("S00004", "删除成功"),
    QUERY_SUCCESS("S00005", "查询成功"),

    // 业务失败 (F开头)
    // 通用业务失败 10000-19999
    BUSINESS_ERROR("F10001", "业务处理失败"),
    PARAM_ERROR("F10002", "参数错误"),
    DATA_NOT_FOUND("F10003", "数据不存在"),
    DATA_ALREADY_EXISTS("F10004", "数据已存在"),
    DATA_DECRYPT_ERROR("F10005", "数据处理失败"),               // 数据解密失败（不暴露解密操作）
    OPERATION_NOT_ALLOWED("F10006", "操作不允许"),

    // 用户相关业务失败 11000-11999
    USER_NOT_FOUND("F11001", "用户名或密码错误"),                // 用户不存在（登录时模糊化，防止枚举）
    USER_ALREADY_EXISTS("F11002", "注册失败，请重试"),            // 用户已存在（注册时模糊化）
    USER_DISABLED("F11003", "账号已禁用"),
    USER_LOCKED("F11004", "账号已锁定"),
    PASSWORD_ERROR("F11005", "用户名或密码错误"),                // 密码错误（与USER_NOT_FOUND保持一致，防止枚举）
    OLD_PASSWORD_ERROR("F11006", "原密码错误"),                  // 修改密码时原密码错误（已登录场景，无枚举风险）
    PASSWORD_NOT_MATCH("F11007", "两次密码不一致"),

    // 认证相关业务失败 12000-12999
    UNAUTHORIZED("F12000", "请先登录"),                          // 未授权访问
    LOGIN_FAILED("F12001", "用户名或密码错误"),                  // 登录失败（统一模糊化）
    TOKEN_INVALID("F12002", "登录已失效，请重新登录"),           // Token无效
    TOKEN_EXPIRED("F12003", "登录已过期，请重新登录"),           // Token过期
    TOKEN_NOT_FOUND("F12004", "请先登录"),                       // Token不存在
    REFRESH_TOKEN_INVALID("F12005", "登录已失效，请重新登录"),   // 刷新Token无效
    REFRESH_TOKEN_EXPIRED("F12006", "登录已过期，请重新登录"),   // 刷新Token过期
    PERMISSION_DENIED("F12007", "权限不足"),
    ACCESS_DENIED("F12008", "访问被拒绝"),
    REFRESH_TOKEN_REQUIRED("F12009", "刷新令牌不能为空"),        // 管理端刷新令牌为空
    NOT_LOGGED_IN("F12010", "未登录，请先登录"),                 // 未携带Token访问需认证接口

    // 验证码相关
    CAPTCHA_CREATE_ERROR("F400100", "图形验证码生成失败"),
    CAPTCHA_VALIDATE_ERROR("F400101", "图形验证码验证失败"),
    SMS_CAPTCHA_ERROR("F400102", "短信验证码发送失败"),
    RATE_LIMIT_EXCEEDED("F400103", "请求过于频繁，请稍后再试"),
    REQUEST_TOO_FREQUENT("F400105", "请求过于频繁，请稍后再试"),

    // 文件上传相关错误码
    FILE_UPLOAD_SIZE_EXCEEDED("F400400", "上传文件大小超过限制"),

    // Token相关错误码
    TOKEN_VALID("F400302", "Token有效"),
    TOKEN_MISMATCH("F400303", "Token不匹配"),
    TOKEN_PROCESS_ERROR("E500300", "Token处理异常"),

    // 邀请码相关错误码
    INVITE_CODE_EXISTS("F400200", "已有未过期的邀请码"),
    INVALID_INVITE_CODE("F400201", "无效的邀请码"),

    // 系统错误 (E开头)
    // 系统通用错误 20000-29999
    SYSTEM_ERROR("E20001", "系统错误"),
    DATABASE_ERROR("E20002", "数据库错误"),
    NETWORK_ERROR("E20003", "网络错误"),
    CONFIG_ERROR("E20004", "配置错误"),

    // 外部服务错误 21000-21999
    EXTERNAL_SERVICE_ERROR("E21001", "外部服务错误"),
    THIRD_PARTY_API_ERROR("E21002", "第三方API错误"),

    // 安全相关错误 22000-22999
    SECURITY_ERROR("E22001", "操作失败"),                        // 安全校验失败
    AUTHENTICATION_ERROR("E22002", "操作失败"),                  // 内部认证错误
    AUTHORIZATION_ERROR("E22003", "操作失败"),                   // 内部授权错误
    ENCRYPTION_ERROR("E22004", "数据处理失败"),                  // 加密错误（不暴露加密操作）

    // 参数验证错误 23000-23999
    VALIDATION_ERROR("E23001", "参数验证失败"),
    REQUIRED_PARAM_MISSING("E23002", "缺少必需参数"),
    INVALID_PARAM_FORMAT("E23003", "参数格式无效"),
    PARAM_OUT_OF_RANGE("E23004", "参数超出范围"),

    // 文件相关错误 24000-24999
    FILE_UPLOAD_ERROR("E24001", "文件上传失败"),
    FILE_DOWNLOAD_ERROR("E24002", "文件下载失败"),
    FILE_NOT_FOUND("E24003", "文件不存在"),
    FILE_SIZE_EXCEEDED("E24004", "文件大小超限"),
    FILE_TYPE_NOT_SUPPORTED("E24005", "不支持的文件类型"),

    // 业务规则错误 25000-25999
    BUSINESS_RULE_VIOLATION("E25001", "违反业务规则"),
    STATE_CONFLICT("E25002", "状态冲突"),
    CONCURRENT_MODIFICATION("E25003", "并发修改冲突"),
    RESOURCE_CONFLICT("E25004", "资源冲突"),

    // 健康方案相关业务失败 13000-13999
    PLAN_NOT_FOUND("F13001", "方案不存在"),
    PLAN_ALREADY_EXISTS("F13002", "方案已存在"),
    PLAN_DISABLED("F13003", "方案已禁用"),
    PLAN_GENERATION_FAILED("F13004", "方案生成失败"),
    PLAN_GENERATING("F13005", "方案生成中"),
    PLAN_EXPIRED("F13006", "方案已过期"),
    PLAN_COMPLETED("F13007", "方案已完成"),
    PLAN_TASK_NOT_FOUND("F13008", "方案任务不存在"),
    LIFESTYLE_PLAN_NOT_FOUND("F13101", "生活方案不存在"),
    MERIDIAN_PLAN_NOT_FOUND("F13103", "经络方案不存在"),
    ACUPOINT_PLAN_NOT_FOUND("F13104", "穴位方案不存在"),
    PLAN_ADJUSTMENT_NOT_FOUND("F13201", "方案调整记录不存在"),
    PLAN_SQUARE_NOT_FOUND("F13202", "方案广场不存在"),
    PLAN_GENERATION_TASK_NOT_FOUND("F14001", "方案生成任务不存在"),
    PLAN_GENERATION_TASK_EXISTS("F14002", "方案生成任务已存在"),
    PLAN_GENERATION_TASK_CANCELLED("F14003", "方案生成任务已取消"),
    PLAN_GENERATION_TASK_FAILED("F14004", "方案生成任务已失败"),
    PLAN_GENERATION_TASK_TIMEOUT("F14005", "方案生成任务超时"),
    AI_SERVICE_UNAVAILABLE("F14101", "服务暂时不可用，请稍后重试"),     // AI服务不可用
    AI_GENERATION_FAILED("F14102", "内容生成失败，请重试"),             // AI生成失败
    AI_REQUEST_TIMEOUT("F14103", "请求超时，请重试"),                  // AI请求超时
    AI_RESPONSE_FORMAT_ERROR("F14104", "内容生成失败，请重试"),        // AI响应格式错误
    AI_QUOTA_EXCEEDED("F14105", "服务额度不足"),                      // AI配额不足
    AI_AUTH_FAILED("F14106", "服务暂时不可用，请稍后重试"),            // AI服务认证失败
    AI_RATE_LIMITED("F14107", "请求过于频繁，请稍后再试"),            // AI服务请求过于频繁
    AI_QUOTA_EXHAUSTED("F14108", "服务额度已用完"),                    // AI服务额度已用完
    AI_MODEL_UNAVAILABLE("F14109", "服务暂时不可用，请稍后重试"),      // AI模型不可用
    AI_CONTENT_FILTERED("F14110", "内容不符合规范，请修改后重试"),    // AI内容审核未通过
    AI_ACCOUNT_ABNORMAL("F14111", "服务暂时不可用，请稍后重试"),      // AI服务账户异常
    AI_PROMPT_TOO_LONG("F14112", "输入内容过长"),

    // 付费模块相关业务失败 15000-16999
    // 配额 15000-15099
    AI_DAILY_QUOTA_EXHAUSTED("F15001", "今日AI问答次数已用完"),
    ASSESSMENT_QUOTA_EXHAUSTED("F15002", "本月体质评估次数已用完"),
    PLAN_QUOTA_EXHAUSTED("F15003", "活跃方案数量已达上限"),
    ORDER_STATUS_INVALID("F15004", "订单状态不允许操作"),

    // 支付/会员 15100-15199
    MEMBER_PLAN_NOT_FOUND("F15101", "套餐不存在"),
    ORDER_NOT_FOUND("F15102", "订单不存在"),
    ORDER_EXPIRED("F15103", "订单已过期"),
    ORDER_ALREADY_PAID("F15104", "订单已支付"),
    PAY_CREATE_FAILED("F15105", "创建支付失败"),
    PAY_CALLBACK_VERIFY_FAILED("F15106", "支付回调验签失败"),
    MEMBER_PLAN_CODE_DUPLICATE("F15107", "套餐代码已存在"),

    // 积分商城 15200-15299
    POINTS_INSUFFICIENT("F15201", "积分不足"),
    POINTS_GOODS_NOT_FOUND("F15202", "积分商品不存在"),
    POINTS_EXCHANGE_LIMIT("F15203", "今日兑换次数已达上限"),

    // 广告 15300-15399
    AD_CONFIG_NOT_FOUND("F15301", "广告配置不存在"),
    AD_REWARD_LIMIT("F15302", "今日广告奖励次数已达上限"),

    // 未知错误
    UNKNOWN_ERROR("E99999", "未知错误");

    private static final Map<String, ExceptionCode> CODE_MAP = new HashMap<>();

    static {
        for (ExceptionCode code : values()) {
            CODE_MAP.put(code.code, code);
        }
    }

    private final String code;
    private final String msg;

    ExceptionCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 根据code获取枚举
     * @param code 状态码
     * @return 对应的枚举值
     */
    public static ExceptionCode fromCode(String code) {
        for (ExceptionCode exceptionCode : values()) {
            if (exceptionCode.getCode().equals(code)) {
                return exceptionCode;
            }
        }
        return UNKNOWN_ERROR;
    }

    /**
     * 根据code获取message
     * @param code 状态码
     * @return 对应的消息
     */
    public static String getMessageByCode(String code) {
        return fromCode(code).getMsg();
    }
}
