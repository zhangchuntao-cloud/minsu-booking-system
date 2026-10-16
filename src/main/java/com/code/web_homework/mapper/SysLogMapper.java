package com.code.web_homework.mapper;

import com.code.web_homework.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统操作日志数据访问接口。
 */
@Mapper
public interface SysLogMapper {

    /** 写入一条操作日志 */
    int insert(SysLog sysLog);

    /** 分页查询操作日志 */
    List<SysLog> selectByQuery(@Param("keyword") String keyword,
                               @Param("offset") int offset,
                               @Param("pageSize") int pageSize);

    /** 统计满足条件的日志总数 */
    long countByQuery(@Param("keyword") String keyword);

    /** 查询最近的操作日志，用于后台工作台展示 */
    List<SysLog> selectRecent(@Param("limit") int limit);
}
