package com.code.web_homework.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 房源评价实体，对应数据表 {@code review}。
 */
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 订单ID */
    private Long orderId;
    /** 房源ID */
    private Long houseId;
    /** 评价用户ID */
    private Long userId;
    /** 评分 1-5 */
    private Integer score;
    /** 评价内容 */
    private String content;
    /** 房东回复 */
    private String reply;
    /** 回复时间 */
    private LocalDateTime replyTime;
    /** 状态 1显示 0隐藏 */
    private Integer status;
    /** 创建者ID */
    private Long createdBy;
    /** 评价时间 */
    private LocalDateTime creationDate;
    /** 修改者ID */
    private Long modifyBy;
    /** 修改时间 */
    private LocalDateTime modifyDate;

    /* ---------------- 以下为非数据库字段，由关联查询填充 ---------------- */

    /** 评价人姓名 */
    private String userRealName;
    /** 评价人头像 */
    private String userAvatar;
    /** 房源标题 */
    private String houseTitle;
    /** 订单编号 */
    private String orderNo;

    /** 评分对应的星级字符串，如 ★★★★★ */
    public String getStarText() {
        int s = score == null ? 0 : score;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i < s ? "★" : "☆");
        }
        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public LocalDateTime getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(LocalDateTime replyTime) {
        this.replyTime = replyTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getHouseTitle() {
        return houseTitle;
    }

    public void setHouseTitle(String houseTitle) {
        this.houseTitle = houseTitle;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
