package com.taolife.common.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 统一响应结果
 *
 * @author 文二
 * @date 2026-03-16
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ExceptionResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码
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
    
    public ExceptionResult(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    /**
     * 成功响应
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> ExceptionResult<T> success() {
        return new ExceptionResult<>(ExceptionCode.SUCCESS.getCode(), ExceptionCode.SUCCESS.getMsg(), null);
    }
    
    /**
     * 成功响应（带数据）
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> ExceptionResult<T> success(T data) {
        return new ExceptionResult<>(ExceptionCode.SUCCESS.getCode(), ExceptionCode.SUCCESS.getMsg(), data);
    }
    
    /**
     * 成功响应（自定义消息）
     * @param data 响应数据
     * @param msg 自定义消息
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> ExceptionResult<T> success(T data, String msg) {
        return new ExceptionResult<>(ExceptionCode.SUCCESS.getCode(), msg, data);
    }
    
    /**
     * 成功响应（自定义状态码）
     * @param code 自定义状态码
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> ExceptionResult<T> success(ExceptionCode code, T data) {
        return new ExceptionResult<>(code.getCode(), code.getMsg(), data);
    }
    
    /**
     * 成功响应（自定义状态码和数据）
     * @param code 自定义状态码
     * @param data 响应数据
     * @param msg 自定义消息
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> ExceptionResult<T> success(ExceptionCode code, T data, String msg) {
        return new ExceptionResult<>(code.getCode(), msg, data);
    }
    
    /**
     * 失败响应
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> ExceptionResult<T> failed() {
        return new ExceptionResult<>(ExceptionCode.BUSINESS_ERROR.getCode(), ExceptionCode.BUSINESS_ERROR.getMsg(), null);
    }
    
    /**
     * 失败响应（自定义消息）
     * @param msg 自定义消息
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> ExceptionResult<T> failed(String msg) {
        return new ExceptionResult<>(ExceptionCode.BUSINESS_ERROR.getCode(), msg, null);
    }
    
    /**
     * 失败响应（带数据）
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> ExceptionResult<T> failed(T data) {
        return new ExceptionResult<>(ExceptionCode.BUSINESS_ERROR.getCode(), ExceptionCode.BUSINESS_ERROR.getMsg(), data);
    }
    
    /**
     * 失败响应（自定义状态码）
     * @param code 自定义状态码
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> ExceptionResult<T> failed(ExceptionCode code) {
        return new ExceptionResult<>(code.getCode(), code.getMsg(), null);
    }
    
    /**
     * 失败响应（自定义状态码和消息）
     * @param code 自定义状态码
     * @param msg 自定义消息
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> ExceptionResult<T> failed(ExceptionCode code, String msg) {
        return new ExceptionResult<>(code.getCode(), msg, null);
    }
    
    /**
     * 失败响应（自定义状态码、数据和消息）
     * @param code 自定义状态码
     * @param data 响应数据
     * @param msg 自定义消息
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> ExceptionResult<T> failed(ExceptionCode code, T data, String msg) {
        return new ExceptionResult<>(code.getCode(), msg, data);
    }
    
    /**
     * 错误响应
     * @param <T> 数据类型
     * @return 错误响应结果
     */
    public static <T> ExceptionResult<T> error() {
        return new ExceptionResult<>(ExceptionCode.SYSTEM_ERROR.getCode(), ExceptionCode.SYSTEM_ERROR.getMsg(), null);
    }
    
    /**
     * 错误响应（自定义消息）
     * @param msg 自定义消息
     * @param <T> 数据类型
     * @return 错误响应结果
     */
    public static <T> ExceptionResult<T> error(String msg) {
        return new ExceptionResult<>(ExceptionCode.SYSTEM_ERROR.getCode(), msg, null);
    }
    
    /**
     * 错误响应（自定义状态码）
     * @param code 自定义状态码
     * @param <T> 数据类型
     * @return 错误响应结果
     */
    public static <T> ExceptionResult<T> error(ExceptionCode code) {
        return new ExceptionResult<>(code.getCode(), code.getMsg(), null);
    }
    
    /**
     * 错误响应（自定义状态码和消息）
     * @param code 自定义状态码
     * @param msg 自定义消息
     * @param <T> 数据类型
     * @return 错误响应结果
     */
    public static <T> ExceptionResult<T> error(ExceptionCode code, String msg) {
        return new ExceptionResult<>(code.getCode(), msg, null);
    }
    
}