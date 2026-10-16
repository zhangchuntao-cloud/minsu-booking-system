package com.code.web_homework.common;

/**
 * 业务异常。
 * <p>
 * 业务校验不通过时抛出该异常，由 {@code GlobalExceptionHandler} 统一捕获，
 * 转换成友好的提示信息返回给前端，避免把堆栈直接暴露给用户。
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误状态码 */
    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = Result.CODE_FAIL;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
