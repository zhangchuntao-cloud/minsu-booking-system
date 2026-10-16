package com.code.web_homework.mapper;

import com.code.web_homework.dto.HouseQuery;
import com.code.web_homework.entity.House;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 房源数据访问接口。
 */
@Mapper
public interface HouseMapper {

    /** 根据主键查询房源详情（关联分类表与用户表带出分类名、房东信息） */
    House selectById(@Param("id") Long id);

    /** 条件分页查询房源列表，内部使用动态 SQL 拼接筛选条件 */
    List<House> selectByQuery(HouseQuery query);

    /** 统计满足筛选条件的房源总数，供分页使用 */
    long countByQuery(HouseQuery query);

    /** 查询热门房源（按预订次数倒序） */
    List<House> selectHotHouses(@Param("limit") int limit);

    /** 查询最新上架房源 */
    List<House> selectLatestHouses(@Param("limit") int limit);

    /** 查询某房东名下的全部房源 */
    List<House> selectByHostId(@Param("hostId") Long hostId);

    /** 新增房源 */
    int insert(House house);

    /** 更新房源 */
    int update(House house);

    /** 更新房源状态及审核意见 */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status,
                     @Param("auditRemark") String auditRemark);

    /** 浏览次数加一 */
    int increaseViewCount(@Param("id") Long id);

    /** 预订次数加一 */
    int increaseOrderCount(@Param("id") Long id);

    /** 删除房源 */
    int deleteById(@Param("id") Long id);

    /** 房源总数 */
    int countAll();

    /** 按状态统计房源数量 */
    int countByStatus(@Param("status") Integer status);

    /** 按分类统计房源数量，用于后台饼图 */
    List<Map<String, Object>> countGroupByCategory();

    /** 按城市统计房源数量，用于后台柱状图 */
    List<Map<String, Object>> countGroupByCity(@Param("limit") int limit);
}
