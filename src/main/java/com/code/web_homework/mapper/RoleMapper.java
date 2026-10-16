package com.code.web_homework.mapper;

import com.code.web_homework.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色数据访问接口。
 */
@Mapper
public interface RoleMapper {

    /** 查询全部启用中的角色 */
    List<Role> selectAll();

    /** 根据角色编码查询角色 */
    Role selectByCode(@Param("roleCode") String roleCode);
}
