package com.code.web_homework.mapper;

import com.code.web_homework.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限数据访问接口。
 */
@Mapper
public interface PermissionMapper {

    /**
     * 根据角色编码查询该角色拥有的全部权限。
     * 通过 sys_role_permission 中间表关联，体现 RBAC 模型的「角色-权限」多对多关系。
     */
    List<Permission> selectByRoleCode(@Param("roleCode") String roleCode);
}
