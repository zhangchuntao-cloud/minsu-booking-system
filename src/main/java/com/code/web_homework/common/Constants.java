package com.code.web_homework.common;

/**
 * 系统常量定义。
 * <p>
 * 将散落在各层的「魔法值」集中到此，便于统一维护，也避免写错数字。
 */
public final class Constants {

    private Constants() {
    }

    /* ------------------------- Session 相关 ------------------------- */
    /** 登录用户存放在 Session 中的键名 */
    public static final String SESSION_USER = "loginUser";

    /* ------------------------- 角色编码 ------------------------- */
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_HOST = "HOST";
    public static final String ROLE_USER = "USER";

    /* ------------------------- 账号状态 ------------------------- */
    public static final int USER_ENABLED = 1;
    public static final int USER_DISABLED = 0;

    /* ------------------------- 房源状态 ------------------------- */
    /** 待审核 */
    public static final int HOUSE_PENDING = 0;
    /** 已上架 */
    public static final int HOUSE_ONLINE = 1;
    /** 已下架 */
    public static final int HOUSE_OFFLINE = 2;
    /** 审核驳回 */
    public static final int HOUSE_REJECTED = 3;

    /* ------------------------- 订单状态 ------------------------- */
    /** 待支付 */
    public static final int ORDER_UNPAID = 0;
    /** 已支付，待入住 */
    public static final int ORDER_PAID = 1;
    /** 已入住 */
    public static final int ORDER_CHECKED_IN = 2;
    /** 已完成 */
    public static final int ORDER_FINISHED = 3;
    /** 已取消 */
    public static final int ORDER_CANCELLED = 4;

    /* ------------------------- 支付方式 ------------------------- */
    public static final int PAY_BALANCE = 1;
    public static final int PAY_ALIPAY = 2;
    public static final int PAY_WECHAT = 3;

    /** 房源编号前缀 */
    public static final String HOUSE_NO_PREFIX = "MS";
}
