package com.code.web_homework.common;

import java.io.Serializable;

/**
 * 统一响应结果封装。
 * <p>
 * 前后端交互统一使用该结构，前端 JavaScript 只需判断 {@code code} 字段
 * 即可知道本次请求成功与否，避免每个接口各写一套返回格式。
 *
 * @param <T> 业务数据类型
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 成功状态码 */
    public static final int CODE_SUCCESS = 200;
    /** 业务失败状态码 */
    public static final int CODE_FAIL = 500;
    /** 未登录状态码 */
    public static final int CODE_UNAUTHORIZED = 401;
    /** 无权限状态码 */
    public static final int CODE_FORBIDDEN = 403;

    /** 状态码 */
    private Integer code;
    /** 提示信息 */
    private String msg;
    /** 业务数据 */
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /** 成功，不带数据 */
    public static <T> Result<T> success() {
        return new Result<>(CODE_SUCCESS, "操作成功", null);
    }

    /** 成功，带数据 */
    public static <T> Result<T> success(T data) {
        return new Result<>(CODE_SUCCESS, "操作成功", data);
    }

    /** 成功，自定义提示与数据 */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(CODE_SUCCESS, msg, data);
    }

    /** 失败，默认状态码 500 */
    public static <T> Result<T> fail(String msg) {
        return new Result<>(CODE_FAIL, msg, null);
    }

    /** 失败，自定义状态码 */
    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /** 未登录 */
    public static <T> Result<T> unauthorized(String msg) {
        return new Result<>(CODE_UNAUTHORIZED, msg, null);
    }

    /** 无权限 */
    public static <T> Result<T> forbidden(String msg) {
        return new Result<>(CODE_FORBIDDEN, msg, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
