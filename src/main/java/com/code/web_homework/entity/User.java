package com.code.web_homework.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体，对应数据表 {@code sys_user}。
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 登录账号 */
    private String username;
    /** 登录密码（MD5 密文） */
    private String password;
    /** 真实姓名 */
    private String realName;
    /** 性别 1男 2女 */
    private String gender;
    /** 出生日期 */
    private LocalDate birthday;
    /** 手机号码 */
    private String phone;
    /** 电子邮箱 */
    private String email;
    /** 头像地址 */
    private String avatar;
    /** 身份证号 */
    private String idCard;
    /** 账户余额 */
    private BigDecimal balance;
    /** 角色编码 ADMIN/HOST/USER */
    private String roleCode;
    /** 账号状态 1正常 0禁用 */
    private Integer status;
    /** 创建者ID */
    private Long createdBy;
    /** 创建时间 */
    private LocalDateTime creationDate;
    /** 修改者ID */
    private Long modifyBy;
    /** 修改时间 */
    private LocalDateTime modifyDate;

    /* ---------------- 以下为非数据库字段，仅用于页面展示 ---------------- */

    /** 角色名称（关联 sys_role 查询得到） */
    private String roleName;

    /** 性别中文描述 */
    public String getGenderText() {
        return "2".equals(gender) ? "女" : "男";
    }

    /** 状态中文描述 */
    public String getStatusText() {
        return status != null && status == 1 ? "正常" : "已禁用";
    }

    /** 角色中文描述 */
    public String getRoleText() {
        if (roleCode == null) {
            return "未知";
        }
        switch (roleCode) {
            case "ADMIN":
                return "系统管理员";
            case "HOST":
                return "房东";
            case "USER":
                return "普通会员";
            default:
                return roleCode;
        }
    }

    /** 手机号脱敏显示，如 139****0001 */
    public String getMaskPhone() {
        if (phone == null || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /** 是否为管理员 */
    public boolean isAdmin() {
        return "ADMIN".equals(roleCode);
    }

    /** 是否为房东 */
    public boolean isHost() {
        return "HOST".equals(roleCode);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
