package com.code.web_homework.controller;

import com.code.web_homework.common.PageResult;
import com.code.web_homework.common.Result;
import com.code.web_homework.config.SessionUtil;
import com.code.web_homework.dto.HouseQuery;
import com.code.web_homework.entity.House;
import com.code.web_homework.entity.Review;
import com.code.web_homework.entity.User;
import com.code.web_homework.mapper.BookingOrderMapper;
import com.code.web_homework.service.FavoriteService;
import com.code.web_homework.service.HouseCategoryService;
import com.code.web_homework.service.HouseService;
import com.code.web_homework.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台房源浏览控制器。
 * <p>
 * 同时提供页面路由与若干 JSON 接口，后者供页面中的 JavaScript
 * 通过 AJAX 异步调用（如实时计算价格、校验日期是否可订）。
 */
@Controller
@RequestMapping("/house")
public class HouseController {

    private final HouseService houseService;
    private final HouseCategoryService categoryService;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;
    private final BookingOrderMapper orderMapper;

    public HouseController(HouseService houseService, HouseCategoryService categoryService,
                           ReviewService reviewService, FavoriteService favoriteService,
                           BookingOrderMapper orderMapper) {
        this.houseService = houseService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
        this.favoriteService = favoriteService;
        this.orderMapper = orderMapper;
    }

    /**
     * 房源列表页，支持关键字、分类、城市、价格区间、人数等多条件筛选与排序。
     */
    @GetMapping("/list")
    public String list(HouseQuery query, Model model, HttpServletRequest request) {
        // 前台列表只展示审核通过并已上架的房源
        query.setStatus(1);
        PageResult<House> page = houseService.page(query);

        model.addAttribute("page", page);
        model.addAttribute("query", query);
        model.addAttribute("categories", categoryService.listWithCount());
        model.addAttribute("loginUser", SessionUtil.getLoginUser(request));
        return "house/list";
    }

    /**
     * 房源详情页，展示图集、设施、评价与预订入口。
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        House house = houseService.getById(id);
        // 累加浏览次数，用于「热门房源」排序
        houseService.increaseView(id);

        List<Review> reviews = reviewService.listByHouse(id);
        User loginUser = SessionUtil.getLoginUser(request);

        model.addAttribute("house", house);
        model.addAttribute("reviews", reviews);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("favorited",
                loginUser != null && favoriteService.isFavorited(loginUser.getId(), id));
        model.addAttribute("today", LocalDate.now().toString());
        return "house/detail";
    }

    /**
     * AJAX 接口：实时计算预订价格。
     * <p>
     * 用户在详情页选择入住与退房日期时触发，无需刷新页面即可看到
     * 晚数与总价，同时返回该日期区间是否已被预订。
     */
    @GetMapping("/api/price")
    @ResponseBody
    public Result<Map<String, Object>> calcPrice(@RequestParam("houseId") Long houseId,
                                                 @RequestParam("checkIn") String checkIn,
                                                 @RequestParam("checkOut") String checkOut) {
        House house = houseService.getRawById(houseId);
        if (house == null) {
            return Result.fail("房源不存在");
        }
        Map<String, Object> data = new HashMap<>();
        try {
            LocalDate in = LocalDate.parse(checkIn);
            LocalDate out = LocalDate.parse(checkOut);
            if (!out.isAfter(in)) {
                return Result.fail("退房日期必须晚于入住日期");
            }
            long nights = ChronoUnit.DAYS.between(in, out);
            if (nights > 30) {
                return Result.fail("单次预订最长不超过 30 晚");
            }
            BigDecimal total = house.getPrice().multiply(BigDecimal.valueOf(nights));

            data.put("nights", nights);
            data.put("unitPrice", house.getPrice());
            data.put("totalAmount", total);
            data.put("available", orderMapper.countDateConflict(houseId, in, out) == 0);
            return Result.success(data);
        } catch (Exception e) {
            return Result.fail("日期格式不正确");
        }
    }

    /**
     * AJAX 接口：查询房源已被预订的日期区间。
     * <p>
     * 详情页加载时调用，把这些日期在日历控件中置灰，避免用户选到无效日期。
     */
    @GetMapping("/api/booked-dates")
    @ResponseBody
    public Result<List<Map<String, Object>>> bookedDates(@RequestParam("houseId") Long houseId) {
        List<Map<String, Object>> list = orderMapper.selectActiveByHouseId(houseId).stream()
                .map(o -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("checkIn", o.getCheckInDate().toString());
                    item.put("checkOut", o.getCheckOutDate().toString());
                    return item;
                })
                .toList();
        return Result.success(list);
    }
}
