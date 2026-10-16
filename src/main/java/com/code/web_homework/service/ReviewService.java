package com.code.web_homework.service;

import com.code.web_homework.common.BusinessException;
import com.code.web_homework.common.Constants;
import com.code.web_homework.common.PageResult;
import com.code.web_homework.entity.BookingOrder;
import com.code.web_homework.entity.Review;
import com.code.web_homework.mapper.BookingOrderMapper;
import com.code.web_homework.mapper.ReviewMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 房源评价业务逻辑层。
 */
@Service
public class ReviewService {

    private final ReviewMapper reviewMapper;
    private final BookingOrderMapper orderMapper;
    private final HouseService houseService;

    public ReviewService(ReviewMapper reviewMapper, BookingOrderMapper orderMapper,
                         HouseService houseService) {
        this.reviewMapper = reviewMapper;
        this.orderMapper = orderMapper;
        this.houseService = houseService;
    }

    /**
     * 发表评价。
     * <p>
     * 只有已完成且未评价过的订单才能评价，评价成功后重新计算房源综合评分。
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(Long orderId, Integer score, String content, Long userId) {
        BookingOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("只能评价自己的订单");
        }
        if (order.getStatus() != Constants.ORDER_FINISHED) {
            throw new BusinessException("订单完成后才能发表评价");
        }
        if (reviewMapper.selectByOrderId(orderId) != null) {
            throw new BusinessException("该订单已经评价过了");
        }
        if (score == null || score < 1 || score > 5) {
            throw new BusinessException("请选择 1-5 分的评分");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("请填写评价内容");
        }
        if (content.length() > 500) {
            throw new BusinessException("评价内容不能超过 500 个字");
        }

        Review review = new Review();
        review.setOrderId(orderId);
        review.setHouseId(order.getHouseId());
        review.setUserId(userId);
        review.setScore(score);
        review.setContent(content.trim());
        review.setStatus(1);
        review.setCreatedBy(userId);
        reviewMapper.insert(review);

        // 评价写入后同步刷新房源的综合评分
        houseService.refreshScore(order.getHouseId());
    }

    /** 查询某房源的可见评价 */
    public List<Review> listByHouse(Long houseId) {
        return reviewMapper.selectByHouseId(houseId);
    }

    /** 查询某用户发表的评价 */
    public List<Review> listByUser(Long userId) {
        return reviewMapper.selectByUserId(userId);
    }

    /** 分页查询评价，支持按房源、房东、关键字筛选 */
    public PageResult<Review> page(Long houseId, Long hostId, String keyword, int pageNum, int pageSize) {
        int offset = (Math.max(pageNum, 1) - 1) * pageSize;
        long total = reviewMapper.countByQuery(houseId, hostId, keyword);
        List<Review> list = reviewMapper.selectByQuery(houseId, hostId, keyword, offset, pageSize);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    /** 房东回复评价 */
    @Transactional(rollbackFor = Exception.class)
    public void reply(Long id, String reply, Long operatorId) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        if (reply == null || reply.trim().isEmpty()) {
            throw new BusinessException("请填写回复内容");
        }
        if (reply.length() > 500) {
            throw new BusinessException("回复内容不能超过 500 个字");
        }
        reviewMapper.reply(id, reply.trim());
    }

    /** 显示或隐藏评价 */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        reviewMapper.updateStatus(id, status);
        houseService.refreshScore(review.getHouseId());
    }

    /** 删除评价 */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        reviewMapper.deleteById(id);
        houseService.refreshScore(review.getHouseId());
    }

    /** 评价总数 */
    public int countAll() {
        return reviewMapper.countAll();
    }

    /** 全站平均评分 */
    public java.math.BigDecimal avgScore() {
        return reviewMapper.avgScoreAll();
    }
}
