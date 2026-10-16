package com.code.web_homework.mapper;

import com.code.web_homework.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 房源评价数据访问接口。
 */
@Mapper
public interface ReviewMapper {

    /** 查询某房源的全部可见评价（关联评价人信息） */
    List<Review> selectByHouseId(@Param("houseId") Long houseId);

    /** 查询某用户发表的全部评价 */
    List<Review> selectByUserId(@Param("userId") Long userId);

    /** 分页查询评价列表，支持按房源、关键字筛选 */
    List<Review> selectByQuery(@Param("houseId") Long houseId,
                               @Param("hostId") Long hostId,
                               @Param("keyword") String keyword,
                               @Param("offset") int offset,
                               @Param("pageSize") int pageSize);

    /** 统计满足条件的评价总数 */
    long countByQuery(@Param("houseId") Long houseId,
                      @Param("hostId") Long hostId,
                      @Param("keyword") String keyword);

    /** 根据主键查询评价 */
    Review selectById(@Param("id") Long id);

    /** 根据订单查询评价，用于判断订单是否已评价 */
    Review selectByOrderId(@Param("orderId") Long orderId);

    /** 新增评价 */
    int insert(Review review);

    /** 房东回复评价 */
    int reply(@Param("id") Long id, @Param("reply") String reply);

    /** 更新评价显示状态 */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /** 删除评价 */
    int deleteById(@Param("id") Long id);

    /** 评价总数 */
    int countAll();

    /** 某房源的评价条数 */
    int countByHouseId(@Param("houseId") Long houseId);

    /** 某房源的平均评分 */
    BigDecimal avgScoreByHouseId(@Param("houseId") Long houseId);

    /** 全站平均评分 */
    BigDecimal avgScoreAll();

    /** 按评分统计数量，用于后台评分分布图 */
    List<Map<String, Object>> countGroupByScore();
}
