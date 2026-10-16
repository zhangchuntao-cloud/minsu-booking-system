package com.code.web_homework.mapper;

import com.code.web_homework.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 房源收藏数据访问接口。
 */
@Mapper
public interface FavoriteMapper {

    /** 查询某用户的收藏列表（关联房源信息） */
    List<Favorite> selectByUserId(@Param("userId") Long userId);

    /** 判断用户是否已收藏某房源，返回记录条数 */
    int countByUserAndHouse(@Param("userId") Long userId, @Param("houseId") Long houseId);

    /** 新增收藏 */
    int insert(Favorite favorite);

    /** 取消收藏 */
    int deleteByUserAndHouse(@Param("userId") Long userId, @Param("houseId") Long houseId);

    /** 根据主键删除收藏 */
    int deleteById(@Param("id") Long id);

    /** 收藏总数 */
    int countAll();
}
