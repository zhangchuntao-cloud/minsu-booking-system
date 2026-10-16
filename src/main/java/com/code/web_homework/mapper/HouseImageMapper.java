package com.code.web_homework.mapper;

import com.code.web_homework.entity.HouseImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 房源图片数据访问接口。
 */
@Mapper
public interface HouseImageMapper {

    /** 查询某房源的全部图片 */
    List<HouseImage> selectByHouseId(@Param("houseId") Long houseId);

    /** 批量插入房源图片，使用 foreach 标签拼接 values */
    int insertBatch(@Param("list") List<HouseImage> list);

    /** 删除某房源的全部图片 */
    int deleteByHouseId(@Param("houseId") Long houseId);
}
