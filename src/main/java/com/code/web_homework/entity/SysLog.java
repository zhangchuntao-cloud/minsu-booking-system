package com.code.web_homework.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统操作日志实体，对应数据表 {@code sys_log}。
 */
public class SysLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 操作人ID */
    private Long userId;
    /** 操作人账号 */
    private String username;
    /** 操作描述 */
    private String operation;
    /** 请求方法 */
    private String method;
    /** 请求参数 */
    private String params;
    /** 操作IP */
    private String ip;
    /** 耗时（毫秒） */
    private Long costTime;
    /** 操作时间 */
    private LocalDateTime creationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getCostTime() {
        return costTime;
    }

    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
