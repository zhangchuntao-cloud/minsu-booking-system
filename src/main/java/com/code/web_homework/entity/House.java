package com.code.web_homework.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 房源实体，对应数据表 {@code house}。
 */
public class House implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 房源编号 */
    private String houseNo;
    /** 房源标题 */
    private String title;
    /** 房东ID */
    private Long hostId;
    /** 分类ID */
    private Long categoryId;
    /** 封面图片地址 */
    private String coverImg;
    /** 省份 */
    private String province;
    /** 城市 */
    private String city;
    /** 区县 */
    private String district;
    /** 详细地址 */
    private String address;
    /** 每晚价格 */
    private BigDecimal price;
    /** 面积（平方米） */
    private Integer area;
    /** 卧室数量 */
    private Integer bedroom;
    /** 客厅数量 */
    private Integer livingRoom;
    /** 卫生间数量 */
    private Integer bathroom;
    /** 床位数量 */
    private Integer bedCount;
    /** 最大入住人数 */
    private Integer maxGuest;
    /** 楼层描述 */
    private String floorDesc;
    /** 配套设施，英文逗号分隔 */
    private String facilities;
    /** 房源描述 */
    private String description;
    /** 综合评分 */
    private BigDecimal score;
    /** 浏览次数 */
    private Integer viewCount;
    /** 预订次数 */
    private Integer orderCount;
    /** 状态 0待审核 1已上架 2已下架 3审核驳回 */
    private Integer status;
    /** 审核意见 */
    private String auditRemark;
    /** 创建者ID */
    private Long createdBy;
    /** 创建时间 */
    private LocalDateTime creationDate;
    /** 修改者ID */
    private Long modifyBy;
    /** 修改时间 */
    private LocalDateTime modifyDate;

    /* ---------------- 以下为非数据库字段，由关联查询或计算得到 ---------------- */

    /** 分类名称（关联 house_category） */
    private String categoryName;
    /** 房东姓名（关联 sys_user） */
    private String hostName;
    /** 房东联系电话（关联 sys_user） */
    private String hostPhone;
    /** 图片列表（关联 house_image） */
    private List<HouseImage> images = new ArrayList<>();
    /** 评价条数 */
    private Integer reviewCount;
    /** 当前登录用户是否已收藏 */
    private Boolean favorited = false;

    /** 将逗号分隔的配套设施拆分成集合，供页面遍历渲染 */
    public List<String> getFacilityList() {
        if (facilities == null || facilities.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(facilities.split(","));
    }

    /** 完整地址 */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (province != null) {
            sb.append(province);
        }
        if (city != null && !city.equals(province)) {
            sb.append(city);
        }
        if (district != null) {
            sb.append(district);
        }
        if (address != null) {
            sb.append(address);
        }
        return sb.toString();
    }

    /** 状态中文描述 */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "待审核";
            case 1:
                return "已上架";
            case 2:
                return "已下架";
            case 3:
                return "审核驳回";
            default:
                return "未知";
        }
    }

    /** 状态对应的样式类名，供页面区分颜色 */
    public String getStatusStyle() {
        if (status == null) {
            return "gray";
        }
        switch (status) {
            case 0:
                return "orange";
            case 1:
                return "green";
            case 2:
                return "gray";
            case 3:
                return "red";
            default:
                return "gray";
        }
    }

    /** 户型描述，如「2室1厅1卫」 */
    public String getLayoutText() {
        return (bedroom == null ? 0 : bedroom) + "室"
                + (livingRoom == null ? 0 : livingRoom) + "厅"
                + (bathroom == null ? 0 : bathroom) + "卫";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public Integer getBedroom() {
        return bedroom;
    }

    public void setBedroom(Integer bedroom) {
        this.bedroom = bedroom;
    }

    public Integer getLivingRoom() {
        return livingRoom;
    }

    public void setLivingRoom(Integer livingRoom) {
        this.livingRoom = livingRoom;
    }

    public Integer getBathroom() {
        return bathroom;
    }

    public void setBathroom(Integer bathroom) {
        this.bathroom = bathroom;
    }

    public Integer getBedCount() {
        return bedCount;
    }

    public void setBedCount(Integer bedCount) {
        this.bedCount = bedCount;
    }

    public Integer getMaxGuest() {
        return maxGuest;
    }

    public void setMaxGuest(Integer maxGuest) {
        this.maxGuest = maxGuest;
    }

    public String getFloorDesc() {
        return floorDesc;
    }

    public void setFloorDesc(String floorDesc) {
        this.floorDesc = floorDesc;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostPhone() {
        return hostPhone;
    }

    public void setHostPhone(String hostPhone) {
        this.hostPhone = hostPhone;
    }

    public List<HouseImage> getImages() {
        return images;
    }

    public void setImages(List<HouseImage> images) {
        this.images = images;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }
}
