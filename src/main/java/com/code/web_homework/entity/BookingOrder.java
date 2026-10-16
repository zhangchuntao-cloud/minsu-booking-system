package com.code.web_homework.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预订订单实体，对应数据表 {@code booking_order}。
 */
public class BookingOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 订单编号 */
    private String orderNo;
    /** 下单用户ID */
    private Long userId;
    /** 房源ID */
    private Long houseId;
    /** 入住日期 */
    private LocalDate checkInDate;
    /** 退房日期 */
    private LocalDate checkOutDate;
    /** 入住晚数 */
    private Integer nights;
    /** 入住人数 */
    private Integer guestNum;
    /** 每晚单价（下单时快照） */
    private BigDecimal unitPrice;
    /** 订单总金额 */
    private BigDecimal totalAmount;
    /** 联系人姓名 */
    private String contactName;
    /** 联系人手机 */
    private String contactPhone;
    /** 订单备注 */
    private String remark;
    /** 状态 0待支付 1待入住 2已入住 3已完成 4已取消 */
    private Integer status;
    /** 支付方式 1余额 2支付宝 3微信 */
    private Integer payType;
    /** 支付时间 */
    private LocalDateTime payTime;
    /** 创建者ID */
    private Long createdBy;
    /** 下单时间 */
    private LocalDateTime creationDate;
    /** 修改者ID */
    private Long modifyBy;
    /** 修改时间 */
    private LocalDateTime modifyDate;

    /* ---------------- 以下为非数据库字段，由关联查询填充 ---------------- */

    /** 房源标题 */
    private String houseTitle;
    /** 房源封面图 */
    private String houseCover;
    /** 房源所在城市 */
    private String houseCity;
    /** 房源详细地址 */
    private String houseAddress;
    /** 房东ID */
    private Long hostId;
    /** 下单人账号 */
    private String username;
    /** 下单人姓名 */
    private String userRealName;
    /** 是否已评价 */
    private Boolean reviewed = false;

    /** 状态中文描述 */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "待支付";
            case 1:
                return "待入住";
            case 2:
                return "已入住";
            case 3:
                return "已完成";
            case 4:
                return "已取消";
            default:
                return "未知";
        }
    }

    /** 状态对应的样式类名 */
    public String getStatusStyle() {
        if (status == null) {
            return "gray";
        }
        switch (status) {
            case 0:
                return "orange";
            case 1:
                return "blue";
            case 2:
                return "green";
            case 3:
                return "gray";
            case 4:
                return "red";
            default:
                return "gray";
        }
    }

    /** 支付方式中文描述 */
    public String getPayTypeText() {
        if (payType == null) {
            return "未支付";
        }
        switch (payType) {
            case 1:
                return "余额支付";
            case 2:
                return "支付宝";
            case 3:
                return "微信支付";
            default:
                return "其他";
        }
    }

    /** 是否可以支付：仅待支付状态可支付 */
    public boolean isCanPay() {
        return status != null && status == 0;
    }

    /** 是否可以取消：待支付或待入住状态可取消 */
    public boolean isCanCancel() {
        return status != null && (status == 0 || status == 1);
    }

    /** 是否可以评价：订单已完成且尚未评价 */
    public boolean isCanReview() {
        return status != null && status == 3 && !Boolean.TRUE.equals(reviewed);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Integer getNights() {
        return nights;
    }

    public void setNights(Integer nights) {
        this.nights = nights;
    }

    public Integer getGuestNum() {
        return guestNum;
    }

    public void setGuestNum(Integer guestNum) {
        this.guestNum = guestNum;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(Long modifyBy) {
        this.modifyBy = modifyBy;
    }

    public LocalDateTime getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(LocalDateTime modifyDate) {
        this.modifyDate = modifyDate;
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

    public String getHouseAddress() {
        return houseAddress;
    }

    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public Boolean getReviewed() {
        return reviewed;
    }

    public void setReviewed(Boolean reviewed) {
        this.reviewed = reviewed;
    }
}
