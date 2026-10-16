package com.code.web_homework.dto;

/**
 * 订单列表查询条件对象。
 */
public class OrderQuery {

    /** 订单编号，支持模糊查询 */
    private String orderNo;
    /** 订单状态 */
    private Integer status;
    /** 下单用户ID，用于「我的订单」 */
    private Long userId;
    /** 房东ID，用于房东查看自己房源的订单 */
    private Long hostId;
    /** 联系人姓名或手机号 */
    private String keyword;
    /** 排序方式：newest / amountDesc */
    private String sortBy = "newest";
    /** 当前页码 */
    private Integer pageNum = 1;
    /** 每页条数 */
    private Integer pageSize = 10;

    /** 计算 SQL 的 LIMIT 偏移量 */
    public int getOffset() {
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return (num - 1) * size;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
