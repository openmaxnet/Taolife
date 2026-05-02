package com.taolife.common.exception;

/**
 * 业务异常类
 */
public class BusinessException extends RuntimeException {
    
    private final ExceptionCode code;
    private final Object data;
    
    /**
     * 构造业务异常（使用默认消息）
     * @param code 异常状态码
     */
    public BusinessException(ExceptionCode code) {
        super(code.getMsg());
        this.code = code;
        this.data = null;
    }
    
    /**
     * 构造业务异常（自定义消息）
     * @param code 异常状态码
     * @param message 自定义异常消息
     */
    public BusinessException(ExceptionCode code, String message) {
        super(message);
        this.code = code;
        this.data = null;
    }
    
    /**
     * 构造业务异常（自定义消息和原因）
     * @param code 异常状态码
     * @param message 自定义异常消息
     * @param cause 异常原因
     */
    public BusinessException(ExceptionCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.data = null;
    }
    
    /**
     * 构造业务异常（带数据）
     * @param code 异常状态码
     * @param data 异常携带的数据
     */
    public BusinessException(ExceptionCode code, Object data) {
        super(code.getMsg());
        this.code = code;
        this.data = data;
    }
    
    /**
     * 构造业务异常（自定义消息和带数据）
     * @param code 异常状态码
     * @param message 自定义异常消息
     * @param data 异常携带的数据
     */
    public BusinessException(ExceptionCode code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }
    
    /**
     * 构造业务异常（自定义消息、数据和原因）
     * @param code 异常状态码
     * @param message 自定义异常消息
     * @param data 异常携带的数据
     * @param cause 异常原因
     */
    public BusinessException(ExceptionCode code, String message, Object data, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.data = data;
    }
    
    /**
     * 获取异常状态码
     * @return 异常状态码
     */
    public ExceptionCode getCode() {
        return code;
    }
    
    /**
     * 获取异常携带的数据
     * @return 异常携带的数据
     */
    public Object getData() {
        return data;
    }
}