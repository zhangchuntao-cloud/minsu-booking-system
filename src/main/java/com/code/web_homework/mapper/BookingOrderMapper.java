package com.code.web_homework.mapper;

import com.code.web_homework.dto.OrderQuery;
import com.code.web_homework.entity.BookingOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 预订订单数据访问接口。
 */
@Mapper
public interface BookingOrderMapper {

    /** 根据主键查询订单（关联房源与用户信息） */
    BookingOrder selectById(@Param("id") Long id);

    /** 根据订单编号查询订单 */
    BookingOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /** 条件分页查询订单列表 */
    List<BookingOrder> selectByQuery(OrderQuery query);

    /** 统计满足条件的订单总数 */
    long countByQuery(OrderQuery query);

    /** 新增订单 */
    int insert(BookingOrder order);

    /** 更新订单状态 */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /** 更新订单支付信息 */
    int updatePayInfo(@Param("id") Long id, @Param("status") Integer status,
                      @Param("payType") Integer payType, @Param("payTime") LocalDateTime payTime);

    /**
     * 统计某房源在指定日期区间内已占用的订单数。
     * 用于下单前的「防重复预订」校验，判断逻辑为两个日期区间是否存在交集。
     */
    int countDateConflict(@Param("houseId") Long houseId,
                          @Param("checkIn") LocalDate checkIn,
                          @Param("checkOut") LocalDate checkOut);

    /**
     * 查询某房源尚未结束的订单日期区间（待支付/待入住/已入住）。
     * 供详情页在日历中置灰已被预订的日期。
     */
    List<BookingOrder> selectActiveByHouseId(@Param("houseId") Long houseId);

    /** 订单总数 */
    int countAll();

    /** 按状态统计订单数量 */
    int countByStatus(@Param("status") Integer status);

    /** 累计成交金额（仅统计已支付及之后的状态） */
    BigDecimal sumTotalAmount();

    /** 本月成交金额 */
    BigDecimal sumMonthAmount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 本月订单数 */
    int countMonthOrders(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 按月统计成交金额，用于后台折线图 */
    List<Map<String, Object>> sumAmountGroupByMonth(@Param("year") Integer year);

    /** 按状态统计订单数量，用于后台环形图 */
    List<Map<String, Object>> countGroupByStatus();

    /** 房源预订排行，用于后台横向柱状图 */
    List<Map<String, Object>> selectTopHouses(@Param("limit") int limit);

    /** 删除订单 */
    int deleteById(@Param("id") Long id);
}
