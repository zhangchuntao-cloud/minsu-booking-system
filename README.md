# 云栖民宿预订管理系统

> 《Web编程技术》课程设计 · 计算机科学与工程学院

一个基于 Spring Boot + MyBatis + Thymeleaf + MySQL 的民宿在线预订管理系统，
实现了「找房 — 预订 — 支付 — 入住 — 评价」的完整业务闭环，
包含前台预订与后台管理两部分，支持管理员、房东、普通会员三种角色。

---

## 一、功能概览

### 前台（面向访客与会员）

| 功能 | 说明 |
| --- | --- |
| 首页 | 轮播大图、房源搜索、分类导航、热门推荐、最新上架、平台公告 |
| 房源检索 | 关键词、类型、价格区间、入住人数四组筛选 + 五种排序方式 + 分页 |
| 房源详情 | 图片画廊、户型参数、配套设施、房东信息、住客评价、日期占用查询 |
| 在线预订 | 选择日期人数、实时价格试算、日期冲突校验、下单 |
| 在线支付 | 余额 / 支付宝 / 微信三种支付方式，支持取消退款 |
| 会员中心 | 账户概览、我的订单、我的收藏、我的评价、资料维护、修改密码 |
| 平台公告 | 公告列表与详情，含浏览计数 |
| 关于我们 | 平台介绍、核心功能、技术架构 |

### 后台（面向管理员与房东）

| 功能 | 管理员 | 房东 |
| --- | :---: | :---: |
| 工作台（核心指标、待办、图表） | ✓ | ✓ |
| 房源管理（列表、新增、编辑、上下架） | ✓ | 仅自己的 |
| 房源审核（通过 / 驳回并填写意见） | ✓ | — |
| 订单管理（办理入住、完成、取消） | 全部订单 | 仅自己房源的 |
| 评价管理（回复、显示 / 隐藏、删除） | ✓ | 仅自己房源的 |
| 分类管理 | ✓ | — |
| 用户管理（增删改、启禁用、重置密码） | ✓ | — |
| 公告管理 | ✓ | — |
| 数据统计（六张可视化图表） | ✓ | — |
| 操作日志 | ✓ | — |

---

## 二、技术栈

| 层次 | 技术选型 |
| --- | --- |
| 开发语言 | Java 17 |
| Web 框架 | Spring Boot 4.0.8 + Spring MVC |
| 视图技术 | Thymeleaf 3.1.5 |
| 持久层 | MyBatis 3（XML 映射 + 动态 SQL） |
| 数据库 | MySQL 8.0（InnoDB） |
| 连接池 | HikariCP |
| 前端 | HTML5 + CSS3 + 原生 JavaScript + Chart.js 4.4.1 |
| 构建工具 | Maven |

---

## 三、快速开始

### 1. 准备数据库

> 本项目开发时使用独立 MySQL 实例（端口 3307），可直接用脚本启动：
>
> ```bash
> bash 启动数据库.sh
> ```
>
> 若改用本机默认的 3306 实例，按下面的命令建库，并把 `application.yaml`
> 中连接串的端口改为 `3306`。

```bash
mysql -u root -p --default-character-set=utf8mb4 < src/main/resources/db/schema.sql
mysql -u root -p --default-character-set=utf8mb4 < src/main/resources/db/data.sql
```

创建专用账号（可选，推荐）：

```sql
CREATE USER 'minsu'@'localhost' IDENTIFIED BY 'minsu123';
GRANT ALL PRIVILEGES ON minsu_db.* TO 'minsu'@'localhost';
FLUSH PRIVILEGES;
```

### 2. 核对配置

检查 `src/main/resources/application.yaml` 中的数据库连接信息
（端口、库名、账号、密码）是否与本机环境一致。

### 3. 启动应用

```bash
# 开发模式
mvn spring-boot:run

# 或打包运行
mvn clean package -DskipTests
java -jar target/web_homework-0.0.1-SNAPSHOT.jar
```

### 4. 访问系统

- 前台首页：http://localhost:8080/index
- 后台工作台：http://localhost:8080/admin/index

详细部署步骤与常见问题排查请见 **《部署文档.md》**。

---

## 四、演示账号

三个账号密码均为 `123456`：

| 角色 | 账号 | 可体验的功能 |
| --- | --- | --- |
| 系统管理员 | `admin` | 房源审核、用户管理、分类/公告管理、数据统计、操作日志 |
| 房东 | `host01` | 发布房源、处理订单、回复评价 |
| 普通会员 | `user01` | 预订房源、在线支付、发表评价、收藏 |

---

## 五、项目结构

```
web_homework/
├── pom.xml                              Maven 配置
├── README.md                            项目说明（本文件）
├── 部署文档.md                           部署与排查手册
├── 启动数据库.sh                          一键启动专用 MySQL 实例
└── src/main/
    ├── java/com/code/web_homework/
    │   ├── WebHomeworkApplication.java   启动类
    │   ├── common/                       统一响应、分页、常量、MD5、业务异常
    │   ├── config/                       MVC 配置与三个拦截器
    │   ├── handler/                      全局异常处理器
    │   ├── entity/                       11 个实体类（对应数据表）
    │   ├── dto/                          查询条件、表单、统计对象
    │   ├── mapper/                       11 个 MyBatis 接口
    │   ├── service/                      10 个业务服务类
    │   └── controller/                   16 个控制器（前台 6 + 后台 7 + 其它）
    └── resources/
        ├── application.yaml              应用配置
        ├── db/schema.sql                 建库建表脚本（12 表 + 1 视图）
        ├── db/data.sql                   初始化数据
        ├── mapper/*.xml                  11 个 MyBatis 映射文件
        ├── templates/                    37 个 Thymeleaf 页面模板
        └── static/                       CSS / JavaScript / 图片素材
```

---

## 六、数据库设计概要

共 12 张业务表 + 1 个视图：

| 表名 | 说明 |
| --- | --- |
| `sys_user` | 用户表 |
| `sys_role` | 角色表 |
| `sys_permission` | 权限表 |
| `sys_role_permission` | 角色权限关联表 |
| `house_category` | 房源分类表 |
| `house` | 房源表（核心） |
| `house_image` | 房源图片表 |
| `booking_order` | 预订订单表（核心） |
| `review` | 评价表 |
| `favorite` | 收藏表 |
| `notice` | 公告表 |
| `sys_log` | 操作日志表 |
| `v_house_detail` | 房源详情视图 |

权限模型采用 RBAC 思想，通过「用户 — 角色 — 权限」三张表实现；
订单与房态的一致性由外键约束 + 唯一索引 + 事务三者共同保障。

---

## 七、几点实现说明

- **密码安全**：数据库中不保存明文密码，注册与登录均经过 MD5 摘要处理。
- **防重复预订**：下单前通过日期区间重叠检测，避免同一房源被重复预订；
  该逻辑同时在前端（提示）与后端（强制校验）实现，以后端为准。
- **评分一致性**：房源评分是评价数据的派生值，
  新增 / 隐藏 / 删除评价后统一重算，保证两处数据始终一致。
- **权限分级**：拦截器控制「能否进入后台」，
  Service 层控制「能操作哪些数据」，两者配合防止越权。
- **操作留痕**：后台写操作自动记录到 `sys_log`，
  日志中过滤密码类字段，避免敏感信息泄露。
