package com.taolife.common.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * 智谱AI错误码枚举
 * 包含智谱AI官方全部HTTP状态码和业务错误码，使用官方错误描述
 * 每个错误码映射到系统内部的 {@link ExceptionCode}
 *
 * @author 文二
 * @date 2026-04-09
 */
public enum ZhipuErrorCode {

    // ===== HTTP 状态错误码 =====
    HTTP_400(400, "参数错误", ExceptionCode.PARAM_ERROR),
    HTTP_401(401, "鉴权失败或 Token 超时", ExceptionCode.AI_AUTH_FAILED),
    HTTP_404(404, "微调功能未开放或任务不存在", ExceptionCode.AI_SERVICE_UNAVAILABLE),
    HTTP_429(429, "请求频率超额、余额不足或账户异常", ExceptionCode.AI_RATE_LIMITED),
    HTTP_434(434, "暂无 API 权限", ExceptionCode.AI_SERVICE_UNAVAILABLE),
    HTTP_435(435, "文件大小超过 100MB", ExceptionCode.PARAM_ERROR),
    HTTP_500(500, "服务器处理请求时发生错误", ExceptionCode.AI_SERVICE_UNAVAILABLE),

    // ===== 基础错误 =====
    INTERNAL_ERROR("500", "内部错误", ExceptionCode.AI_SERVICE_UNAVAILABLE),

    // ===== 身份验证错误 =====
    AUTH_FAILED("1000", "身份验证失败", ExceptionCode.AI_AUTH_FAILED),
    AUTH_HEADER_MISSING("1001", "Header 中未收到 Authentication 参数，无法进行身份验证", ExceptionCode.AI_AUTH_FAILED),
    AUTH_TOKEN_INVALID("1002", "Authentication Token 非法，请确认 Authentication Token 正确传递", ExceptionCode.AI_AUTH_FAILED),
    AUTH_TOKEN_EXPIRED("1003", "Authentication Token 已过期，请重新生成/获取", ExceptionCode.AI_AUTH_FAILED),
    AUTH_TOKEN_VERIFY_FAILED("1004", "通过 Authentication Token 的验证失败", ExceptionCode.AI_AUTH_FAILED),
    ACCOUNT_READ_WRITE("1100", "账户读写", ExceptionCode.AI_ACCOUNT_ABNORMAL),

    // ===== 账户错误 =====
    ACCOUNT_INACTIVE("1110", "账户当前处于非活动状态", ExceptionCode.AI_ACCOUNT_ABNORMAL),
    ACCOUNT_NOT_EXIST("1111", "账户不存在", ExceptionCode.AI_ACCOUNT_ABNORMAL),
    ACCOUNT_LOCKED("1112", "账户已被锁定，请联系客服解锁", ExceptionCode.AI_ACCOUNT_ABNORMAL),
    ACCOUNT_IN_DEBT("1113", "账户已欠费，请充值后重试", ExceptionCode.AI_QUOTA_EXHAUSTED),
    ACCOUNT_ACCESS_FAILED("1120", "无法成功访问账户，请稍后重试", ExceptionCode.AI_ACCOUNT_ABNORMAL),
    ACCOUNT_VIOLATION("1121", "账户存违规行为，账号已被锁定", ExceptionCode.AI_ACCOUNT_ABNORMAL),

    // ===== API 调用错误 =====
    API_ERROR("1200", "API 调用错误", ExceptionCode.AI_GENERATION_FAILED),
    API_PARAM_ERROR("1210", "API 调用参数有误，请检查文档", ExceptionCode.PARAM_ERROR),
    MODEL_NOT_FOUND("1211", "模型不存在，请检查模型代码", ExceptionCode.AI_MODEL_UNAVAILABLE),
    METHOD_NOT_SUPPORTED("1212", "当前模型不支持该调用方式", ExceptionCode.AI_GENERATION_FAILED),
    PARAM_MISSING("1213", "未正常接收到参数", ExceptionCode.PARAM_ERROR),
    PARAM_INVALID("1214", "参数非法，请检查文档", ExceptionCode.PARAM_ERROR),
    PARAM_CONFLICT("1215", "参数不能同时设置，请检查文档", ExceptionCode.PARAM_ERROR),
    API_NO_PERMISSION("1220", "无权访问该 API", ExceptionCode.AI_AUTH_FAILED),
    API_OFFLINE("1221", "API 已下线", ExceptionCode.AI_SERVICE_UNAVAILABLE),
    API_NOT_EXIST("1222", "API 不存在", ExceptionCode.AI_SERVICE_UNAVAILABLE),
    API_FLOW_ERROR("1230", "API 调用流程出错", ExceptionCode.AI_GENERATION_FAILED),
    REQUEST_DUPLICATE("1231", "已有请求", ExceptionCode.AI_RATE_LIMITED),
    NETWORK_ERROR("1234", "网络错误，请联系客服", ExceptionCode.NETWORK_ERROR),
    PROMPT_TOO_LONG("1261", "Prompt 超长", ExceptionCode.AI_PROMPT_TOO_LONG),

    // ===== API 策略阻止错误 =====
    POLICY_BLOCKED("1300", "API 调用被策略阻止", ExceptionCode.AI_GENERATION_FAILED),
    CONTENT_UNSAFE("1301", "系统检测到输入或生成内容可能包含不安全或敏感内容", ExceptionCode.AI_CONTENT_FILTERED),
    CONCURRENT_EXCEEDED("1302", "当前使用该 API 的并发数过高", ExceptionCode.AI_RATE_LIMITED),
    FREQUENCY_EXCEEDED("1303", "当前使用该 API 的频率过高", ExceptionCode.AI_RATE_LIMITED),
    DAILY_LIMIT("1304", "该 API 已达今日调用次数限额", ExceptionCode.AI_QUOTA_EXHAUSTED),
    TRAFFIC_LIMIT("1305", "该 API 已触发流量限制", ExceptionCode.AI_RATE_LIMITED),
    USAGE_LIMIT("1308", "已达使用上限", ExceptionCode.AI_QUOTA_EXHAUSTED),
    PLAN_EXPIRED("1309", "GLM Coding Plan 套餐已到期", ExceptionCode.AI_QUOTA_EXHAUSTED),
    PERIOD_LIMIT("1310", "已达到每周/每月使用上限", ExceptionCode.AI_QUOTA_EXHAUSTED),
    MODEL_NO_PERMISSION("1311", "当前订阅套餐暂未开放该模型权限", ExceptionCode.AI_MODEL_UNAVAILABLE),
    MODEL_OVERLOADED("1312", "该模型当前访问量过大", ExceptionCode.AI_RATE_LIMITED),
    FAIR_USE_LIMITED("1313", "触发公平使用策略限制", ExceptionCode.AI_RATE_LIMITED),

    // ===== 未知错误 =====
    UNKNOWN_ERROR("unknown", "未知错误", ExceptionCode.AI_GENERATION_FAILED);

    /** HTTP 状态码（仅 HTTP 错误有值，业务码为 null） */
    private final Integer httpStatus;

    /** 智谱业务错误码（仅业务错误有值，HTTP 错误为 null） */
    private final String code;

    /** 智谱官方错误描述 */
    private final String message;

    /** 映射到系统内部异常码 */
    private final ExceptionCode mappedCode;

    /** HTTP 状态码查找表 */
    private static final Map<Integer, ZhipuErrorCode> HTTP_STATUS_MAP = new HashMap<>();

    /** 业务错误码查找表 */
    private static final Map<String, ZhipuErrorCode> BIZ_CODE_MAP = new HashMap<>();

    static {
        for (ZhipuErrorCode e : values()) {
            if (e.httpStatus != null) {
                HTTP_STATUS_MAP.put(e.httpStatus, e);
            }
            if (e.code != null) {
                BIZ_CODE_MAP.put(e.code, e);
            }
        }
    }

    /**
     * HTTP 状态码构造函数
     */
    ZhipuErrorCode(int httpStatus, String message, ExceptionCode mappedCode) {
        this.httpStatus = httpStatus;
        this.code = null;
        this.message = message;
        this.mappedCode = mappedCode;
    }

    /**
     * 业务错误码构造函数
     */
    ZhipuErrorCode(String code, String message, ExceptionCode mappedCode) {
        this.httpStatus = null;
        this.code = code;
        this.message = message;
        this.mappedCode = mappedCode;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ExceptionCode getMappedCode() {
        return mappedCode;
    }

    /**
     * 根据HTTP状态码查找对应的智谱错误码
     *
     * @param httpStatus HTTP状态码
     * @return 对应的错误码枚举，未找到时返回 UNKNOWN_ERROR
     */
    public static ZhipuErrorCode fromHttpStatus(int httpStatus) {
        return HTTP_STATUS_MAP.getOrDefault(httpStatus, UNKNOWN_ERROR);
    }

    /**
     * 根据智谱业务错误码查找对应的枚举
     *
     * @param code 智谱业务错误码
     * @return 对应的错误码枚举，未找到时返回 UNKNOWN_ERROR
     */
    public static ZhipuErrorCode fromBizCode(String code) {
        return BIZ_CODE_MAP.getOrDefault(code, UNKNOWN_ERROR);
    }
}
