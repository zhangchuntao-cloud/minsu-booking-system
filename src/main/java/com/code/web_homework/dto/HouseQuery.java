package com.code.web_homework.dto;

import java.math.BigDecimal;

/**
 * 房源列表查询条件对象。
 * <p>
 * 把列表页的多个筛选条件封装成一个对象，配合 MyBatis 的
 * {@code <where>} 与 {@code <if>} 标签实现动态 SQL：
 * 用户填了哪个条件就拼哪个条件，没填的自动忽略。
 */
public class HouseQuery {

    /** 关键字，匹配房源标题或地址 */
    private String keyword;
    /** 分类ID */
    private Long categoryId;
    /** 城市 */
    private String city;
    /** 最低价格 */
    private BigDecimal minPrice;
    /** 最高价格 */
    private BigDecimal maxPrice;
    /** 最少可住人数 */
    private Integer guestNum;
    /** 房源状态，用于后台筛选 */
    private Integer status;
    /** 房东ID，用于房东只查看自己的房源 */
    private Long hostId;
    /** 排序方式：default / priceAsc / priceDesc / scoreDesc / hotDesc */
    private String sortBy = "default";
    /** 当前页码 */
    private Integer pageNum = 1;
    /** 每页条数 */
    private Integer pageSize = 8;

    /** 计算 SQL 的 LIMIT 偏移量 */
    public int getOffset() {
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 8 : pageSize;
        return (num - 1) * size;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getGuestNum() {
        return guestNum;
    }

    public void setGuestNum(Integer guestNum) {
        this.guestNum = guestNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
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
