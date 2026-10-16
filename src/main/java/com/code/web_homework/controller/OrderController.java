package com.code.web_homework.controller;

import com.code.web_homework.common.Constants;
import com.code.web_homework.common.PageResult;
import com.code.web_homework.common.Result;
import com.code.web_homework.config.SessionUtil;
import com.code.web_homework.dto.BookingForm;
import com.code.web_homework.dto.OrderQuery;
import com.code.web_homework.entity.BookingOrder;
import com.code.web_homework.entity.User;
import com.code.web_homework.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 订单控制器：下单、支付、取消与我的订单。
 * <p>
 * 下单、支付、取消等写操作均以 AJAX 方式提交并返回统一的 JSON 结果，
 * 页面无需整体刷新即可给出反馈。
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 提交预订订单（AJAX）。
     */
    @PostMapping("/create")
    @ResponseBody
    public Result<BookingOrder> create(@Valid BookingForm form, HttpServletRequest request) {
        User loginUser = SessionUtil.getLoginUser(request);
        BookingOrder order = orderService.create(form, loginUser.getId());
        return Result.success("下单成功，请在 30 分钟内完成支付", order);
    }

    /**
     * 支付订单（AJAX）。
     */
    @PostMapping("/pay")
    @ResponseBody
    public Result<Void> pay(@RequestParam("orderNo") String orderNo,
                            @RequestParam(value = "payType", defaultValue = "2") Integer payType,
                            HttpServletRequest request) {
        User loginUser = SessionUtil.getLoginUser(request);
        orderService.pay(orderNo, payType, loginUser.getId());
        return Result.success("支付成功，祝您入住愉快", null);
    }

    /**
     * 取消订单（AJAX）。
     */
    @PostMapping("/cancel")
    @ResponseBody
    public Result<Void> cancel(@RequestParam("orderNo") String orderNo, HttpServletRequest request) {
        User loginUser = SessionUtil.getLoginUser(request);
        orderService.cancel(orderNo, loginUser.getId(), false);
        return Result.success("订单已取消", null);
    }

    /**
     * 我的订单列表，可按状态筛选。
     */
    @GetMapping("/my")
    public String my(OrderQuery query, HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        // 强制限定为当前登录用户，避免通过参数查看他人订单
        query.setUserId(loginUser.getId());
        query.setHostId(null);
        PageResult<BookingOrder> page = orderService.page(query);

        model.addAttribute("page", page);
        model.addAttribute("query", query);
        return "order/my";
    }

    /** 订单详情 */
    @GetMapping("/detail/{orderNo}")
    public String detail(@PathVariable("orderNo") String orderNo,
                         HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        BookingOrder order = orderService.getByOrderNo(orderNo);
        // 越权校验：只能查看自己的订单，管理员除外
        if (!order.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin()) {
            return "redirect:/error/403";
        }
        model.addAttribute("order", order);
        return "order/detail";
    }

    /** 下单成功后的引导页 */
    @GetMapping("/success/{orderNo}")
    public String success(@PathVariable("orderNo") String orderNo,
                          HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        BookingOrder order = orderService.getByOrderNo(orderNo);
        if (!order.getUserId().equals(loginUser.getId())) {
            return "redirect:/error/403";
        }
        model.addAttribute("order", order);
        return "order/success";
    }

    /**
     * AJAX 接口：查询订单状态，用于支付页轮询。
     */
    @GetMapping("/api/status")
    @ResponseBody
    public Result<Integer> status(@RequestParam("orderNo") String orderNo) {
        BookingOrder order = orderService.getByOrderNo(orderNo);
        return Result.success(order.getStatus());
    }

    /** 供页面显示的订单状态常量 */
    @org.springframework.web.bind.annotation.ModelAttribute("orderStatusUnpaid")
    public Integer orderStatusUnpaid() {
        return Constants.ORDER_UNPAID;
    }
}
