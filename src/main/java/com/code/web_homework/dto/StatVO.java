package com.code.web_homework.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 后台工作台概览统计数据。
 */
public class StatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户总数 */
    private Integer totalUsers;
    /** 房源总数 */
    private Integer totalHouses;
    /** 订单总数 */
    private Integer totalOrders;
    /** 累计成交金额 */
    private BigDecimal totalAmount;
    /** 待审核房源数 */
    private Integer pendingHouses;
    /** 已上架房源数 */
    private Integer onlineHouses;
    /** 待处理订单数（待支付 + 待入住） */
    private Integer pendingOrders;
    /** 本月新增订单数 */
    private Integer monthOrders;
    /** 本月成交金额 */
    private BigDecimal monthAmount;
    /** 评价总数 */
    private Integer totalReviews;
    /** 平均评分 */
    private BigDecimal avgScore;
    /** 收藏总数 */
    private Integer totalFavorites;

    public Integer getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Integer totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Integer getTotalHouses() {
        return totalHouses;
    }

    public void setTotalHouses(Integer totalHouses) {
        this.totalHouses = totalHouses;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getPendingHouses() {
        return pendingHouses;
    }

    public void setPendingHouses(Integer pendingHouses) {
        this.pendingHouses = pendingHouses;
    }

    public Integer getOnlineHouses() {
        return onlineHouses;
    }

    public void setOnlineHouses(Integer onlineHouses) {
        this.onlineHouses = onlineHouses;
    }

    public Integer getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(Integer pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public Integer getMonthOrders() {
        return monthOrders;
    }

    public void setMonthOrders(Integer monthOrders) {
        this.monthOrders = monthOrders;
    }

    public BigDecimal getMonthAmount() {
        return monthAmount;
    }

    public void setMonthAmount(BigDecimal monthAmount) {
        this.monthAmount = monthAmount;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    public BigDecimal getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(BigDecimal avgScore) {
        this.avgScore = avgScore;
    }

    public Integer getTotalFavorites() {
        return totalFavorites;
    }

    public void setTotalFavorites(Integer totalFavorites) {
        this.totalFavorites = totalFavorites;
    }
}
