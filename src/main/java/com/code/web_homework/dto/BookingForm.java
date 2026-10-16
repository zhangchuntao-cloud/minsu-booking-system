package com.code.web_homework.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 预订下单表单对象。
 * <p>
 * 使用 JSR-380 注解声明校验规则，Controller 中配合 {@code @Valid} 使用，
 * 校验不通过时由全局异常处理器统一返回错误提示，避免在业务代码里写大量 if 判断。
 */
public class BookingForm {

    /** 房源ID */
    @NotNull(message = "请选择要预订的房源")
    private Long houseId;

    /** 入住日期 */
    @NotNull(message = "请选择入住日期")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;

    /** 退房日期 */
    @NotNull(message = "请选择退房日期")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOutDate;

    /** 入住人数 */
    @NotNull(message = "请填写入住人数")
    @Min(value = 1, message = "入住人数至少为 1 人")
    private Integer guestNum;

    /** 联系人姓名 */
    @NotBlank(message = "请填写联系人姓名")
    private String contactName;

    /** 联系人手机号 */
    @NotBlank(message = "请填写联系人手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;

    /** 订单备注 */
    private String remark;

    /** 支付方式 1余额 2支付宝 3微信 */
    private Integer payType;

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

    public Integer getGuestNum() {
        return guestNum;
    }

    public void setGuestNum(Integer guestNum) {
        this.guestNum = guestNum;
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

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }
}
