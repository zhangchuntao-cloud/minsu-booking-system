package com.code.web_homework.service;

import com.code.web_homework.common.BusinessException;
import com.code.web_homework.common.Constants;
import com.code.web_homework.common.PageResult;
import com.code.web_homework.dto.BookingForm;
import com.code.web_homework.dto.OrderQuery;
import com.code.web_homework.entity.BookingOrder;
import com.code.web_homework.entity.House;
import com.code.web_homework.entity.User;
import com.code.web_homework.mapper.BookingOrderMapper;
import com.code.web_homework.mapper.HouseMapper;
import com.code.web_homework.mapper.ReviewMapper;
import com.code.web_homework.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 预订订单业务逻辑层。
 * <p>
 * 集中处理下单、支付、取消等涉及多表写入的操作，
 * 通过 {@code @Transactional} 保证「扣款 + 改单」这类组合操作的原子性。
 */
@Service
public class OrderService {

    private final BookingOrderMapper orderMapper;
    private final HouseMapper houseMapper;
    private final UserMapper userMapper;
    private final ReviewMapper reviewMapper;

    public OrderService(BookingOrderMapper orderMapper, HouseMapper houseMapper,
                        UserMapper userMapper, ReviewMapper reviewMapper) {
        this.orderMapper = orderMapper;
        this.houseMapper = houseMapper;
        this.userMapper = userMapper;
        this.reviewMapper = reviewMapper;
    }

    /**
     * 创建预订订单。
     * <p>
     * 下单前依次完成：房源状态校验 → 日期合法性校验 → 人数校验 →
     * 日期冲突校验（防同一房源被重复预订），全部通过后按房源单价计算总金额。
     */
    @Transactional(rollbackFor = Exception.class)
    public BookingOrder create(BookingForm form, Long userId) {
        House house = houseMapper.selectById(form.getHouseId());
        if (house == null) {
            throw new BusinessException("房源不存在或已被删除");
        }
        if (house.getStatus() == null || house.getStatus() != Constants.HOUSE_ONLINE) {
            throw new BusinessException("该房源当前不可预订");
        }
        if (house.getHostId().equals(userId)) {
            throw new BusinessException("不能预订自己发布的房源");
        }

        LocalDate checkIn = form.getCheckInDate();
        LocalDate checkOut = form.getCheckOutDate();
        LocalDate today = LocalDate.now();
        if (checkIn.isBefore(today)) {
            throw new BusinessException("入住日期不能早于今天");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new BusinessException("退房日期必须晚于入住日期");
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights > 30) {
            throw new BusinessException("单次预订最长不超过 30 晚");
        }
        if (form.getGuestNum() > house.getMaxGuest()) {
            throw new BusinessException("该房源最多可住 " + house.getMaxGuest() + " 人");
        }

        // 防重复预订：检查所选日期区间内是否已有未取消的订单
        int conflict = orderMapper.countDateConflict(house.getId(), checkIn, checkOut);
        if (conflict > 0) {
            throw new BusinessException("所选日期已被预订，请更换日期或选择其他房源");
        }

        BigDecimal total = house.getPrice().multiply(BigDecimal.valueOf(nights));

        BookingOrder order = new BookingOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setHouseId(house.getId());
        order.setCheckInDate(checkIn);
        order.setCheckOutDate(checkOut);
        order.setNights((int) nights);
        order.setGuestNum(form.getGuestNum());
        order.setUnitPrice(house.getPrice());
        order.setTotalAmount(total);
        order.setContactName(form.getContactName());
        order.setContactPhone(form.getContactPhone());
        order.setRemark(form.getRemark());
        order.setStatus(Constants.ORDER_UNPAID);
        order.setCreatedBy(userId);

        orderMapper.insert(order);
        return order;
    }

    /**
     * 支付订单。
     * <p>
     * 选择余额支付时会先校验并扣减账户余额，扣款与订单状态变更在同一事务中完成，
     * 任一步骤失败都会整体回滚，避免出现「扣了钱但订单未支付」的脏数据。
     */
    @Transactional(rollbackFor = Exception.class)
    public void pay(String orderNo, Integer payType, Long userId) {
        BookingOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != Constants.ORDER_UNPAID) {
            throw new BusinessException("该订单当前状态不可支付");
        }
        if (payType == null) {
            payType = Constants.PAY_ALIPAY;
        }

        if (payType == Constants.PAY_BALANCE) {
            User user = userMapper.selectById(userId);
            if (user.getBalance() == null || user.getBalance().compareTo(order.getTotalAmount()) < 0) {
                throw new BusinessException("账户余额不足，请选择其他支付方式或先充值");
            }
            BigDecimal balance = user.getBalance().subtract(order.getTotalAmount());
            userMapper.updateBalance(userId, balance);
        }

        orderMapper.updatePayInfo(order.getId(), Constants.ORDER_PAID, payType, LocalDateTime.now());
        // 支付成功后才计入房源的预订次数，保证排行数据反映真实成交
        houseMapper.increaseOrderCount(order.getHouseId());
    }

    /**
     * 取消订单。
     * <p>
     * 已支付的订单取消时自动把款项退回账户余额。
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancel(String orderNo, Long userId, boolean isAdmin) {
        BookingOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!isAdmin && !order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        Integer status = order.getStatus();
        if (status == null || (status != Constants.ORDER_UNPAID && status != Constants.ORDER_PAID)) {
            throw new BusinessException("该订单当前状态不可取消");
        }

        // 已支付订单取消时退款到余额
        if (status == Constants.ORDER_PAID) {
            User user = userMapper.selectById(order.getUserId());
            BigDecimal balance = (user.getBalance() == null ? BigDecimal.ZERO : user.getBalance())
                    .add(order.getTotalAmount());
            userMapper.updateBalance(order.getUserId(), balance);
        }
        orderMapper.updateStatus(order.getId(), Constants.ORDER_CANCELLED);
    }

    /** 确认入住，由房东或管理员操作 */
    @Transactional(rollbackFor = Exception.class)
    public void checkIn(Long id) {
        BookingOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != Constants.ORDER_PAID) {
            throw new BusinessException("仅「待入住」状态的订单可以办理入住");
        }
        orderMapper.updateStatus(id, Constants.ORDER_CHECKED_IN);
    }

    /** 完成订单，由房东或管理员操作 */
    @Transactional(rollbackFor = Exception.class)
    public void finish(Long id) {
        BookingOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != Constants.ORDER_CHECKED_IN) {
            throw new BusinessException("仅「已入住」状态的订单可以标记完成");
        }
        orderMapper.updateStatus(id, Constants.ORDER_FINISHED);
    }

    /** 分页查询订单 */
    public PageResult<BookingOrder> page(OrderQuery query) {
        long total = orderMapper.countByQuery(query);
        List<BookingOrder> list = orderMapper.selectByQuery(query);
        return new PageResult<>(list, total, query.getPageNum(), query.getPageSize());
    }

    /** 根据订单编号查询，同时标记是否已评价 */
    public BookingOrder getByOrderNo(String orderNo) {
        BookingOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        order.setReviewed(reviewMapper.selectByOrderId(order.getId()) != null);
        return order;
    }

    /** 根据主键查询订单 */
    public BookingOrder getById(Long id) {
        BookingOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    /** 订单总数 */
    public int countAll() {
        return orderMapper.countAll();
    }

    /** 按状态统计订单数 */
    public int countByStatus(Integer status) {
        return orderMapper.countByStatus(status);
    }

    /** 累计成交金额 */
    public BigDecimal sumTotalAmount() {
        return orderMapper.sumTotalAmount();
    }

    /** 生成订单编号，格式 MS + 年月日时分秒 + 4 位随机数 */
    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "MS" + time + random;
    }
}
