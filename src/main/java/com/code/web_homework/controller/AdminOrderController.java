package com.code.web_homework.controller;

import com.code.web_homework.common.PageResult;
import com.code.web_homework.common.Result;
import com.code.web_homework.config.SessionUtil;
import com.code.web_homework.dto.OrderQuery;
import com.code.web_homework.entity.BookingOrder;
import com.code.web_homework.entity.User;
import com.code.web_homework.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 后台订单管理控制器。
 * <p>
 * 管理员可以查看和处理全部订单，房东只能看到自己名下房源的订单。
 */
@Controller
@RequestMapping("/admin/order")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 订单列表 */
    @GetMapping("/list")
    public String list(OrderQuery query, HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        if (loginUser.isHost()) {
            query.setHostId(loginUser.getId());
        }
        query.setPageSize(10);
        PageResult<BookingOrder> page = orderService.page(query);

        model.addAttribute("page", page);
        model.addAttribute("query", query);
        model.addAttribute("menu", "order");
        model.addAttribute("loginUser", loginUser);
        return "admin/order-list";
    }

    /** 订单详情 */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        model.addAttribute("order", orderService.getById(id));
        model.addAttribute("menu", "order");
        model.addAttribute("loginUser", SessionUtil.getLoginUser(request));
        return "admin/order-detail";
    }

    /** 办理入住（AJAX） */
    @PostMapping("/checkin")
    @ResponseBody
    public Result<Void> checkIn(@RequestParam("id") Long id) {
        orderService.checkIn(id);
        return Result.success("已办理入住", null);
    }

    /** 标记订单完成（AJAX） */
    @PostMapping("/finish")
    @ResponseBody
    public Result<Void> finish(@RequestParam("id") Long id) {
        orderService.finish(id);
        return Result.success("订单已完成", null);
    }

    /** 后台取消订单（AJAX），管理员操作时不做归属校验 */
    @PostMapping("/cancel")
    @ResponseBody
    public Result<Void> cancel(@RequestParam("id") Long id, HttpServletRequest request) {
        User loginUser = SessionUtil.getLoginUser(request);
        BookingOrder order = orderService.getById(id);
        orderService.cancel(order.getOrderNo(), loginUser.getId(), true);
        return Result.success("订单已取消", null);
    }
}
