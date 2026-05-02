package com.taolife.aicore.exception;

import lombok.Getter;

/**
 * 通用LLM异常
 */
@Getter
public class LlmException extends RuntimeException {

    /**
     * 错误码
     */
    private final String errorCode;

    /**
     * HTTP状态码
     */
    private final int httpStatus;

    public LlmException(String message) {
        super(message);
        this.errorCode = "LLM_ERROR";
        this.httpStatus = 500;
    }

    public LlmException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public LlmException(String errorCode, String message, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
