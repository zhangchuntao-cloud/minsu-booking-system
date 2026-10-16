package com.code.web_homework.mapper;

import com.code.web_homework.dto.UserQuery;
import com.code.web_homework.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户数据访问接口，SQL 定义在 {@code resources/mapper/UserMapper.xml}。
 */
@Mapper
public interface UserMapper {

    /** 根据主键查询用户 */
    User selectById(@Param("id") Long id);

    /** 根据登录账号查询用户，登录时使用 */
    User selectByUsername(@Param("username") String username);

    /** 新增用户 */
    int insert(User user);

    /** 更新用户基本信息 */
    int update(User user);

    /** 单独更新密码 */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /** 更新账号启用状态 */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /** 更新账户余额 */
    int updateBalance(@Param("id") Long id, @Param("balance") BigDecimal balance);

    /** 条件分页查询用户列表 */
    List<User> selectByQuery(UserQuery query);

    /** 统计满足条件的用户总数 */
    long countByQuery(UserQuery query);

    /** 查询全部房东，用于后台房源分配 */
    List<User> selectHosts();

    /** 用户总数 */
    int countAll();

    /** 按角色统计人数 */
    int countByRoleCode(@Param("roleCode") String roleCode);

    /** 删除用户 */
    int deleteById(@Param("id") Long id);
}
