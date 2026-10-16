package com.code.web_homework.mapper;

import com.code.web_homework.entity.HouseCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 房源分类数据访问接口。
 */
@Mapper
public interface HouseCategoryMapper {

    /** 查询全部启用中的分类 */
    List<HouseCategory> selectAll();

    /** 查询分类并统计每个分类下的房源数量 */
    List<HouseCategory> selectWithHouseCount();

    /** 根据主键查询分类 */
    HouseCategory selectById(@Param("id") Long id);

    /** 新增分类 */
    int insert(HouseCategory category);

    /** 更新分类 */
    int update(HouseCategory category);

    /** 删除分类 */
    int deleteById(@Param("id") Long id);
}
