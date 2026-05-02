package com.taolife.common.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理器
 * 统一处理系统中的各种异常，返回标准化的响应格式
 */
@Slf4j
@RestControllerAdvice
public class GlobalBaseExceptionHandler {

    /**
     * 处理业务异常
     * 
     * @param e 业务异常
     * @return 统一失败响应
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ExceptionResult<Object> handleBusinessException(BusinessException e) {
        // 业务异常不打印堆栈，只记录消息
        log.warn("业务异常：{}", e.getMessage());
        return ExceptionResult.failed(e.getCode(), e.getData(), e.getMessage());
    }

    /**
     * 处理权限拒绝异常
     * 
     * @param e 权限拒绝异常
     * @return 统一失败响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResult<Object> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限拒绝异常：{}", e.getMessage());
        return ExceptionResult.failed(ExceptionCode.ACCESS_DENIED, "权限不足，无法访问该资源");
    }

    /**
     * 处理参数验证异常
     * 
     * @param e 参数验证异常
     * @return 统一失败响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        String errorMessage = String.join(", ", errors);
        log.warn("参数验证异常：{}", errorMessage);
        return ExceptionResult.failed(ExceptionCode.VALIDATION_ERROR, errorMessage);
    }

    /**
     * 处理绑定异常
     * 
     * @param e 绑定异常
     * @return 统一失败响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResult<Object> handleBindException(BindException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        String errorMessage = String.join(", ", errors);
        log.warn("绑定异常：{}", errorMessage);
        return ExceptionResult.failed(ExceptionCode.VALIDATION_ERROR, errorMessage);
    }

    /**
     * 处理缺少请求参数异常
     * 
     * @param e 缺少请求参数异常
     * @return 统一失败响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResult<Object> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        String errorMessage = "缺少必需参数: " + e.getParameterName();
        log.warn("缺少请求参数异常：{}", errorMessage);
        return ExceptionResult.failed(ExceptionCode.REQUIRED_PARAM_MISSING, errorMessage);
    }

    /**
     * 处理缺少请求头异常
     * 
     * @param e 缺少请求头异常
     * @return 统一失败响应
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResult<Object> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        String errorMessage = "缺少必需请求头: " + e.getHeaderName();
        log.warn("缺少请求头异常：{}", errorMessage);
        return ExceptionResult.failed(ExceptionCode.REQUIRED_PARAM_MISSING, errorMessage);
    }

    /**
     * 处理参数类型不匹配异常
     * 
     * @param e 参数类型不匹配异常
     * @return 统一失败响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResult<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String errorMessage = "参数类型不匹配: " + e.getName();
        log.warn("参数类型不匹配异常：{}", errorMessage);
        return ExceptionResult.failed(ExceptionCode.INVALID_PARAM_FORMAT, errorMessage);
    }

    /**
     * 处理请求方法不支持异常
     * 
     * @param e 请求方法不支持异常
     * @return 统一失败响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ExceptionResult<Object> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        String errorMessage = "不支持的请求方法: " + e.getMethod();
        log.warn("请求方法不支持异常：{}", errorMessage);
        return ExceptionResult.failed(ExceptionCode.OPERATION_NOT_ALLOWED, errorMessage);
    }

    /**
     * 处理404异常
     * 
     * @param e 404异常
     * @return 统一失败响应
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResult<Object> handleNoHandlerFoundException(NoHandlerFoundException e) {
        String errorMessage = "请求的资源不存在: " + e.getRequestURL();
        log.warn("404异常：{}", errorMessage);
        return ExceptionResult.failed(ExceptionCode.DATA_NOT_FOUND, errorMessage);
    }

    /**
     * 处理系统异常
     * 
     * @param e 系统异常
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResult<Object> handleException(Exception e) {
        // 打印完整堆栈，方便调试数据库连接问题
        log.error("系统异常：", e);
        return ExceptionResult.error(ExceptionCode.SYSTEM_ERROR, "系统繁忙，请稍后重试");
    }

    /**
     * 处理运行时异常
     *
     * @param e 运行时异常
     * @return 统一错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResult<Object> handleRuntimeException(RuntimeException e) {
        // 只打印异常类型和消息，不打印完整堆栈
        log.error("运行时异常：{} - {}", e.getClass().getSimpleName(), e.getMessage());
        return ExceptionResult.error(ExceptionCode.SYSTEM_ERROR, "系统繁忙，请稍后重试");
    }

    /**
     * 处理空指针异常
     *
     * @param e 空指针异常
     * @return 统一错误响应
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResult<Object> handleNullPointerException(NullPointerException e) {
        // 只打印异常类型和消息，不打印完整堆栈
        log.error("空指针异常：{} - {}", e.getClass().getSimpleName(), e.getMessage());
        return ExceptionResult.error(ExceptionCode.SYSTEM_ERROR, "系统繁忙，请稍后重试");
    }

    /**
     * 处理非法参数异常
     * 
     * @param e 非法参数异常
     * @return 统一失败响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResult<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        // 非法参数异常不打印堆栈，只记录消息
        log.warn("非法参数：{}", e.getMessage());
        return ExceptionResult.failed(ExceptionCode.PARAM_ERROR, e.getMessage());
    }

    /**
     * 处理状态异常
     * 
     * @param e 状态异常
     * @return 统一失败响应
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResult<Object> handleIllegalStateException(IllegalStateException e) {
        // 状态异常不打印堆栈，只记录消息
        log.error("状态异常：{}", e.getMessage());
        return ExceptionResult.failed(ExceptionCode.STATE_CONFLICT, e.getMessage());
    }

    /**
     * 处理JWT过期异常
     * 
     * @param e JWT过期异常
     * @return 统一失败响应
     */
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResult<Object> handleExpiredJwtException(ExpiredJwtException e) {
        // JWT过期异常不打印完整堆栈，只记录关键信息
        log.warn("JWT令牌已过期：过期时间={}, 当前时间={}", e.getClaims().getExpiration(), new java.util.Date());
        return ExceptionResult.failed(ExceptionCode.TOKEN_EXPIRED, "登录已过期，请重新登录");
    }

    /**
     * 处理资源未找到异常
     *
     * @param e 资源未找到异常
     * @return 统一失败响应
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResult<Object> handleNoResourceFoundException(NoResourceFoundException e) {
        // 资源未找到异常不打印完整堆栈，只记录关键信息
        log.warn("请求的资源不存在：{}", e.getResourcePath());
        return ExceptionResult.error(ExceptionCode.DATA_NOT_FOUND, "请求的资源不存在");
    }

    /**
     * 处理Redis连接异常
     * 打印完整堆栈，方便调试
     *
     * @param e Redis连接异常
     * @return 统一错误响应
     */
    @ExceptionHandler(RedisConnectionFailureException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ExceptionResult<Object> handleRedisConnectionFailureException(RedisConnectionFailureException e) {
        // 打印完整堆栈，方便调试
        log.error("Redis连接异常：", e);
        return ExceptionResult.error(ExceptionCode.SYSTEM_ERROR, "服务暂时不可用，请稍后重试");
    }
}