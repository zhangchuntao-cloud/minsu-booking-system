package com.code.web_homework.service;

import com.code.web_homework.common.Constants;
import com.code.web_homework.dto.ChartData;
import com.code.web_homework.dto.StatVO;
import com.code.web_homework.mapper.BookingOrderMapper;
import com.code.web_homework.mapper.FavoriteMapper;
import com.code.web_homework.mapper.HouseMapper;
import com.code.web_homework.mapper.ReviewMapper;
import com.code.web_homework.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计业务逻辑层。
 * <p>
 * 把数据库的分组聚合结果整理成前端 Chart.js 直接可用的
 * {@link ChartData} 结构，避免在页面里写复杂的转换脚本。
 */
@Service
public class StatService {

    private final UserMapper userMapper;
    private final HouseMapper houseMapper;
    private final BookingOrderMapper orderMapper;
    private final ReviewMapper reviewMapper;
    private final FavoriteMapper favoriteMapper;

    public StatService(UserMapper userMapper, HouseMapper houseMapper, BookingOrderMapper orderMapper,
                       ReviewMapper reviewMapper, FavoriteMapper favoriteMapper) {
        this.userMapper = userMapper;
        this.houseMapper = houseMapper;
        this.orderMapper = orderMapper;
        this.reviewMapper = reviewMapper;
        this.favoriteMapper = favoriteMapper;
    }

    /**
     * 后台工作台概览数据。
     */
    public StatVO overview() {
        StatVO vo = new StatVO();
        vo.setTotalUsers(userMapper.countAll());
        vo.setTotalHouses(houseMapper.countAll());
        vo.setTotalOrders(orderMapper.countAll());
        vo.setTotalAmount(orderMapper.sumTotalAmount());
        vo.setPendingHouses(houseMapper.countByStatus(Constants.HOUSE_PENDING));
        vo.setOnlineHouses(houseMapper.countByStatus(Constants.HOUSE_ONLINE));
        vo.setPendingOrders(orderMapper.countByStatus(Constants.ORDER_UNPAID)
                + orderMapper.countByStatus(Constants.ORDER_PAID));
        vo.setTotalReviews(reviewMapper.countAll());
        vo.setAvgScore(reviewMapper.avgScoreAll());
        vo.setTotalFavorites(favoriteMapper.countAll());

        LocalDate today = LocalDate.now();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime nextMonthStart = monthStart.plusMonths(1);
        vo.setMonthOrders(orderMapper.countMonthOrders(monthStart, nextMonthStart));
        vo.setMonthAmount(orderMapper.sumMonthAmount(monthStart, nextMonthStart));
        return vo;
    }

    /**
     * 本年度各月成交金额折线图。
     * <p>
     * 数据库只会返回有订单的月份，这里补齐 1-12 月，
     * 保证图表横轴完整，不会因为某月没有数据而缺格。
     */
    public ChartData monthAmountChart(Integer year) {
        int targetYear = year == null ? LocalDate.now().getYear() : year;
        Map<Integer, BigDecimal> monthMap = new HashMap<>();
        for (Map<String, Object> row : orderMapper.sumAmountGroupByMonth(targetYear)) {
            monthMap.put(toInt(row.get("name")), toDecimal(row.get("value")));
        }
        ChartData chart = new ChartData("成交金额(元)");
        for (int month = 1; month <= 12; month++) {
            chart.add(month + "月", monthMap.getOrDefault(month, BigDecimal.ZERO));
        }
        return chart;
    }

    /** 各分类房源数量饼图 */
    public ChartData categoryChart() {
        ChartData chart = new ChartData("房源数量");
        for (Map<String, Object> row : houseMapper.countGroupByCategory()) {
            chart.add(String.valueOf(row.get("name")), toDecimal(row.get("value")));
        }
        return chart;
    }

    /** 各城市房源数量柱状图 */
    public ChartData cityChart(int limit) {
        ChartData chart = new ChartData("房源数量");
        for (Map<String, Object> row : houseMapper.countGroupByCity(limit)) {
            chart.add(String.valueOf(row.get("name")), toDecimal(row.get("value")));
        }
        return chart;
    }

    /** 订单状态分布环形图 */
    public ChartData orderStatusChart() {
        ChartData chart = new ChartData("订单数量");
        for (Map<String, Object> row : orderMapper.countGroupByStatus()) {
            chart.add(orderStatusText(toInt(row.get("name"))), toDecimal(row.get("value")));
        }
        return chart;
    }

    /** 房源预订排行横向柱状图 */
    public ChartData topHouseChart(int limit) {
        ChartData chart = new ChartData("预订次数");
        for (Map<String, Object> row : orderMapper.selectTopHouses(limit)) {
            String name = String.valueOf(row.get("name"));
            chart.add(name.length() > 12 ? name.substring(0, 12) + "…" : name,
                    toDecimal(row.get("value")));
        }
        return chart;
    }

    /** 评分分布柱状图 */
    public ChartData scoreChart() {
        Map<Integer, BigDecimal> scoreMap = new HashMap<>();
        for (Map<String, Object> row : reviewMapper.countGroupByScore()) {
            scoreMap.put(toInt(row.get("name")), toDecimal(row.get("value")));
        }
        ChartData chart = new ChartData("评价数量");
        for (int score = 5; score >= 1; score--) {
            chart.add(score + " 分", scoreMap.getOrDefault(score, BigDecimal.ZERO));
        }
        return chart;
    }

    /* ==================== 类型转换辅助 ==================== */

    private int toInt(Object value) {
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    private BigDecimal toDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return BigDecimal.ZERO;
    }

    private String orderStatusText(int status) {
        switch (status) {
            case 0:
                return "待支付";
            case 1:
                return "待入住";
            case 2:
                return "已入住";
            case 3:
                return "已完成";
            case 4:
                return "已取消";
            default:
                return "其他";
        }
    }
}
