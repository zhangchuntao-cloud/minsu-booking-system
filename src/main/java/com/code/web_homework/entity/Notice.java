package com.code.web_homework.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统公告实体，对应数据表 {@code notice}。
 */
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 公告标题 */
    private String title;
    /** 公告内容 */
    private String content;
    /** 公告类型 1系统公告 2活动促销 3入住须知 */
    private Integer type;
    /** 发布人ID */
    private Long publisherId;
    /** 发布时间 */
    private LocalDateTime publishTime;
    /** 浏览次数 */
    private Integer viewCount;
    /** 状态 1已发布 0草稿 */
    private Integer status;
    /** 创建者ID */
    private Long createdBy;
    /** 创建时间 */
    private LocalDateTime creationDate;
    /** 修改者ID */
    private Long modifyBy;
    /** 修改时间 */
    private LocalDateTime modifyDate;

    /** 发布人姓名（关联查询填充） */
    private String publisherName;

    /** 类型中文描述 */
    public String getTypeText() {
        if (type == null) {
            return "系统公告";
        }
        switch (type) {
            case 1:
                return "系统公告";
            case 2:
                return "活动促销";
            case 3:
                return "入住须知";
            default:
                return "系统公告";
        }
    }

    /** 类型对应的样式类名 */
    public String getTypeStyle() {
        if (type == null) {
            return "blue";
        }
        switch (type) {
            case 1:
                return "blue";
            case 2:
                return "orange";
            case 3:
                return "green";
            default:
                return "blue";
        }
    }

    /** 摘要，用于列表页展示 */
    public String getSummary() {
        if (content == null) {
            return "";
        }
        return content.length() > 60 ? content.substring(0, 60) + "…" : content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
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

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }
}
