package com.code.web_homework.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房源收藏实体，对应数据表 {@code favorite}。
 */
public class Favorite implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 房源ID */
    private Long houseId;
    /** 收藏时间 */
    private LocalDateTime creationDate;

    /* ---------------- 以下为非数据库字段，由关联查询填充 ---------------- */

    /** 房源标题 */
    private String houseTitle;
    /** 房源封面图 */
    private String houseCover;
    /** 房源城市 */
    private String houseCity;
    /** 房源价格 */
    private BigDecimal housePrice;
    /** 房源评分 */
    private BigDecimal houseScore;

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

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getHouseTitle() {
        return houseTitle;
    }

    public void setHouseTitle(String houseTitle) {
        this.houseTitle = houseTitle;
    }

    public String getHouseCover() {
        return houseCover;
    }

    public void setHouseCover(String houseCover) {
        this.houseCover = houseCover;
    }

    public String getHouseCity() {
        return houseCity;
    }

    public void setHouseCity(String houseCity) {
        this.houseCity = houseCity;
    }

    public BigDecimal getHousePrice() {
        return housePrice;
    }

    public void setHousePrice(BigDecimal housePrice) {
        this.housePrice = housePrice;
    }

    public BigDecimal getHouseScore() {
        return houseScore;
    }

    public void setHouseScore(BigDecimal houseScore) {
        this.houseScore = houseScore;
    }
}
