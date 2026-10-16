-- ============================================================================
--  云栖民宿预订管理系统 —— 数据库结构定义脚本 (DDL)
--  数据库：MySQL 8.0+ / 9.x
--  字符集：utf8mb4  排序规则：utf8mb4_general_ci
--  说明：脚本可重复执行，执行前会先删除同名数据库
-- ============================================================================

DROP DATABASE IF EXISTS `minsu_db`;
CREATE DATABASE `minsu_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `minsu_db`;

-- ============================================================================
-- 1. sys_role  角色表
--    系统采用 RBAC 权限模型，角色是权限分配的载体
-- ============================================================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code`     VARCHAR(20)  NOT NULL                COMMENT '角色编码(ADMIN/HOST/USER)',
  `role_name`     VARCHAR(30)  NOT NULL                COMMENT '角色名称',
  `description`   VARCHAR(100) DEFAULT NULL            COMMENT '角色描述',
  `sort`          INT          DEFAULT 0               COMMENT '显示顺序',
  `status`        TINYINT      DEFAULT 1               COMMENT '状态 1启用 0停用',
  `created_by`    BIGINT       DEFAULT NULL            COMMENT '创建者ID',
  `creation_date` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_by`     BIGINT       DEFAULT NULL            COMMENT '修改者ID',
  `modify_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================================================================
-- 2. sys_permission  权限表
-- ============================================================================
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `perm_code`     VARCHAR(50)  NOT NULL                COMMENT '权限编码',
  `perm_name`     VARCHAR(50)  NOT NULL                COMMENT '权限名称',
  `perm_type`     TINYINT      DEFAULT 1               COMMENT '权限类型 1菜单 2按钮 3接口',
  `parent_id`     BIGINT       DEFAULT 0               COMMENT '父权限ID，0为顶级',
  `url`           VARCHAR(100) DEFAULT NULL            COMMENT '权限对应的访问地址',
  `icon`          VARCHAR(50)  DEFAULT NULL            COMMENT '菜单图标',
  `sort`          INT          DEFAULT 0               COMMENT '显示顺序',
  `created_by`    BIGINT       DEFAULT NULL            COMMENT '创建者ID',
  `creation_date` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_by`     BIGINT       DEFAULT NULL            COMMENT '修改者ID',
  `modify_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`perm_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ============================================================================
-- 3. sys_role_permission  角色权限关联表（多对多中间表）
-- ============================================================================
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id`            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id`       BIGINT   NOT NULL                COMMENT '角色ID',
  `permission_id` BIGINT   NOT NULL                COMMENT '权限ID',
  `creation_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`, `permission_id`),
  KEY `idx_permission_id` (`permission_id`),
  CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rp_perm` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ============================================================================
-- 4. sys_user  用户表
-- ============================================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username`      VARCHAR(30)   NOT NULL                COMMENT '登录账号',
  `password`      VARCHAR(64)   NOT NULL                COMMENT '登录密码(MD5加密存储)',
  `real_name`     VARCHAR(30)   DEFAULT NULL            COMMENT '真实姓名',
  `gender`        CHAR(1)       DEFAULT '1'             COMMENT '性别 1男 2女',
  `birthday`      DATE          DEFAULT NULL            COMMENT '出生日期',
  `phone`         VARCHAR(15)   DEFAULT NULL            COMMENT '手机号码',
  `email`         VARCHAR(50)   DEFAULT NULL            COMMENT '电子邮箱',
  `avatar`        VARCHAR(150)  DEFAULT NULL            COMMENT '头像地址',
  `id_card`       VARCHAR(20)   DEFAULT NULL            COMMENT '身份证号',
  `balance`       DECIMAL(10,2) DEFAULT 0.00            COMMENT '账户余额',
  `role_code`     VARCHAR(20)   NOT NULL DEFAULT 'USER' COMMENT '角色编码',
  `status`        TINYINT       DEFAULT 1               COMMENT '账号状态 1正常 0禁用',
  `created_by`    BIGINT        DEFAULT NULL            COMMENT '创建者ID',
  `creation_date` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_by`     BIGINT        DEFAULT NULL            COMMENT '修改者ID',
  `modify_date`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ============================================================================
-- 5. house_category  房源分类表
-- ============================================================================
DROP TABLE IF EXISTS `house_category`;
CREATE TABLE `house_category` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_name`   VARCHAR(30)  NOT NULL                COMMENT '分类名称',
  `category_notes`  VARCHAR(100) DEFAULT NULL            COMMENT '分类备注',
  `icon`            VARCHAR(50)  DEFAULT NULL            COMMENT '分类图标',
  `sort`            INT          DEFAULT 0               COMMENT '显示顺序',
  `status`          TINYINT      DEFAULT 1               COMMENT '状态 1启用 0停用',
  `created_by`      BIGINT       DEFAULT NULL            COMMENT '创建者ID',
  `creation_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_by`       BIGINT       DEFAULT NULL            COMMENT '修改者ID',
  `modify_date`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_name` (`category_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源分类表';

-- ============================================================================
-- 6. house  房源表
-- ============================================================================
DROP TABLE IF EXISTS `house`;
CREATE TABLE `house` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `house_no`      VARCHAR(20)   NOT NULL                COMMENT '房源编号',
  `title`         VARCHAR(60)   NOT NULL                COMMENT '房源标题',
  `host_id`       BIGINT        NOT NULL                COMMENT '房东ID(关联sys_user.id)',
  `category_id`   BIGINT        NOT NULL                COMMENT '分类ID(关联house_category.id)',
  `cover_img`     VARCHAR(150)  DEFAULT NULL            COMMENT '封面图片地址',
  `province`      VARCHAR(30)   DEFAULT NULL            COMMENT '省份',
  `city`          VARCHAR(30)   DEFAULT NULL            COMMENT '城市',
  `district`      VARCHAR(30)   DEFAULT NULL            COMMENT '区县',
  `address`       VARCHAR(120)  DEFAULT NULL            COMMENT '详细地址',
  `price`         DECIMAL(10,2) NOT NULL DEFAULT 0.00   COMMENT '每晚价格(元)',
  `area`          INT           DEFAULT 0               COMMENT '面积(平方米)',
  `bedroom`       TINYINT       DEFAULT 1               COMMENT '卧室数量',
  `living_room`   TINYINT       DEFAULT 0               COMMENT '客厅数量',
  `bathroom`      TINYINT       DEFAULT 1               COMMENT '卫生间数量',
  `bed_count`     TINYINT       DEFAULT 1               COMMENT '床位数量',
  `max_guest`     TINYINT       DEFAULT 2               COMMENT '最大入住人数',
  `floor_desc`    VARCHAR(30)   DEFAULT NULL            COMMENT '楼层描述',
  `facilities`    VARCHAR(200)  DEFAULT NULL            COMMENT '配套设施(逗号分隔)',
  `description`   TEXT                                  COMMENT '房源描述',
  `score`         DECIMAL(3,1)  DEFAULT 5.0             COMMENT '综合评分(0-5)',
  `view_count`    INT           DEFAULT 0               COMMENT '浏览次数',
  `order_count`   INT           DEFAULT 0               COMMENT '预订次数',
  `status`        TINYINT       DEFAULT 0               COMMENT '状态 0待审核 1已上架 2已下架 3审核驳回',
  `audit_remark`  VARCHAR(100)  DEFAULT NULL            COMMENT '审核意见',
  `created_by`    BIGINT        DEFAULT NULL            COMMENT '创建者ID',
  `creation_date` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_by`     BIGINT        DEFAULT NULL            COMMENT '修改者ID',
  `modify_date`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_house_no` (`house_no`),
  KEY `idx_host_id` (`host_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  -- 组合索引：首页/列表页最常用的过滤条件是「已上架 + 按价格排序」
  KEY `idx_status_price` (`status`, `price`),
  CONSTRAINT `fk_house_host` FOREIGN KEY (`host_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_house_category` FOREIGN KEY (`category_id`) REFERENCES `house_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源信息表';

-- ============================================================================
-- 7. house_image  房源图片表
-- ============================================================================
DROP TABLE IF EXISTS `house_image`;
CREATE TABLE `house_image` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `house_id`      BIGINT       NOT NULL                COMMENT '房源ID',
  `image_url`     VARCHAR(150) NOT NULL                COMMENT '图片地址',
  `image_name`    VARCHAR(60)  DEFAULT NULL            COMMENT '图片原始名称',
  `sort`          INT          DEFAULT 0               COMMENT '显示顺序',
  `creation_date` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_house_id` (`house_id`),
  CONSTRAINT `fk_image_house` FOREIGN KEY (`house_id`) REFERENCES `house` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源图片表';

-- ============================================================================
-- 8. booking_order  预订订单表
-- ============================================================================
DROP TABLE IF EXISTS `booking_order`;
CREATE TABLE `booking_order` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no`       VARCHAR(32)   NOT NULL                COMMENT '订单编号',
  `user_id`        BIGINT        NOT NULL                COMMENT '下单用户ID',
  `house_id`       BIGINT        NOT NULL                COMMENT '房源ID',
  `check_in_date`  DATE          NOT NULL                COMMENT '入住日期',
  `check_out_date` DATE          NOT NULL                COMMENT '退房日期',
  `nights`         INT           NOT NULL DEFAULT 1      COMMENT '入住晚数',
  `guest_num`      TINYINT       DEFAULT 1               COMMENT '入住人数',
  `unit_price`     DECIMAL(10,2) NOT NULL DEFAULT 0.00   COMMENT '每晚单价(下单时快照)',
  `total_amount`   DECIMAL(10,2) NOT NULL DEFAULT 0.00   COMMENT '订单总金额',
  `contact_name`   VARCHAR(30)   DEFAULT NULL            COMMENT '联系人姓名',
  `contact_phone`  VARCHAR(15)   DEFAULT NULL            COMMENT '联系人手机',
  `remark`         VARCHAR(200)  DEFAULT NULL            COMMENT '订单备注',
  `status`         TINYINT       DEFAULT 0               COMMENT '状态 0待支付 1待入住 2已入住 3已完成 4已取消',
  `pay_type`       TINYINT       DEFAULT NULL            COMMENT '支付方式 1余额 2支付宝 3微信',
  `pay_time`       DATETIME      DEFAULT NULL            COMMENT '支付时间',
  `created_by`     BIGINT        DEFAULT NULL            COMMENT '创建者ID',
  `creation_date`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `modify_by`      BIGINT        DEFAULT NULL            COMMENT '修改者ID',
  `modify_date`    DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_house_id` (`house_id`),
  -- 组合索引：统计某房源在指定日期区间是否已被占用（防超卖校验）
  KEY `idx_house_dates` (`house_id`, `check_in_date`, `check_out_date`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_order_house` FOREIGN KEY (`house_id`) REFERENCES `house` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预订订单表';

-- ============================================================================
-- 9. review  评价表
-- ============================================================================
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id`      BIGINT       NOT NULL                COMMENT '订单ID',
  `house_id`      BIGINT       NOT NULL                COMMENT '房源ID',
  `user_id`       BIGINT       NOT NULL                COMMENT '评价用户ID',
  `score`         TINYINT      NOT NULL DEFAULT 5      COMMENT '评分(1-5)',
  `content`       VARCHAR(500) DEFAULT NULL            COMMENT '评价内容',
  `reply`         VARCHAR(500) DEFAULT NULL            COMMENT '房东回复',
  `reply_time`    DATETIME     DEFAULT NULL            COMMENT '回复时间',
  `status`        TINYINT      DEFAULT 1               COMMENT '状态 1显示 0隐藏',
  `created_by`    BIGINT       DEFAULT NULL            COMMENT '创建者ID',
  `creation_date` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  `modify_by`     BIGINT       DEFAULT NULL            COMMENT '修改者ID',
  `modify_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `booking_order` (`id`),
  CONSTRAINT `fk_review_house` FOREIGN KEY (`house_id`) REFERENCES `house` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源评价表';

-- ============================================================================
-- 10. favorite  收藏表
-- ============================================================================
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id`            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`       BIGINT   NOT NULL                COMMENT '用户ID',
  `house_id`      BIGINT   NOT NULL                COMMENT '房源ID',
  `creation_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  -- 唯一索引保证同一用户对同一房源只能收藏一次
  UNIQUE KEY `uk_user_house` (`user_id`, `house_id`),
  KEY `idx_house_id` (`house_id`),
  CONSTRAINT `fk_fav_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_fav_house` FOREIGN KEY (`house_id`) REFERENCES `house` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源收藏表';

-- ============================================================================
-- 11. notice  系统公告表
-- ============================================================================
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title`         VARCHAR(80)  NOT NULL                COMMENT '公告标题',
  `content`       TEXT                                 COMMENT '公告内容',
  `type`          TINYINT      DEFAULT 1               COMMENT '公告类型 1系统公告 2活动促销 3入住须知',
  `publisher_id`  BIGINT       DEFAULT NULL            COMMENT '发布人ID',
  `publish_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `view_count`    INT          DEFAULT 0               COMMENT '浏览次数',
  `status`        TINYINT      DEFAULT 1               COMMENT '状态 1已发布 0草稿',
  `created_by`    BIGINT       DEFAULT NULL            COMMENT '创建者ID',
  `creation_date` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_by`     BIGINT       DEFAULT NULL            COMMENT '修改者ID',
  `modify_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_time` (`status`, `publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

-- ============================================================================
-- 12. sys_log  操作日志表
-- ============================================================================
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`       BIGINT       DEFAULT NULL            COMMENT '操作人ID',
  `username`      VARCHAR(30)  DEFAULT NULL            COMMENT '操作人账号',
  `operation`     VARCHAR(100) DEFAULT NULL            COMMENT '操作描述',
  `method`        VARCHAR(150) DEFAULT NULL            COMMENT '请求方法',
  `params`        VARCHAR(500) DEFAULT NULL            COMMENT '请求参数',
  `ip`            VARCHAR(50)  DEFAULT NULL            COMMENT '操作IP',
  `cost_time`     BIGINT       DEFAULT 0               COMMENT '耗时(毫秒)',
  `creation_date` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_creation_date` (`creation_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';

-- ============================================================================
--  视图：房源综合信息视图（关联分类与房东，简化列表页查询）
-- ============================================================================
DROP VIEW IF EXISTS `v_house_detail`;
CREATE VIEW `v_house_detail` AS
SELECT h.`id`, h.`house_no`, h.`title`, h.`cover_img`, h.`city`, h.`district`,
       h.`address`, h.`price`, h.`area`, h.`bedroom`, h.`max_guest`,
       h.`facilities`, h.`score`, h.`view_count`, h.`order_count`, h.`status`,
       c.`category_name`, u.`real_name` AS host_name, u.`phone` AS host_phone
FROM `house` h
LEFT JOIN `house_category` c ON h.`category_id` = c.`id`
LEFT JOIN `sys_user` u ON h.`host_id` = u.`id`;
