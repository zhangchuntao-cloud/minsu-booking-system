package com.code.web_homework.controller;

import com.code.web_homework.common.Constants;
import com.code.web_homework.common.PageResult;
import com.code.web_homework.common.Result;
import com.code.web_homework.config.SessionUtil;
import com.code.web_homework.dto.ChartData;
import com.code.web_homework.dto.HouseQuery;
import com.code.web_homework.dto.StatVO;
import com.code.web_homework.entity.SysLog;
import com.code.web_homework.entity.User;
import com.code.web_homework.service.HouseService;
import com.code.web_homework.service.LogService;
import com.code.web_homework.service.OrderService;
import com.code.web_homework.service.StatService;
import com.code.web_homework.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

/**
 * 后台工作台与数据统计控制器。
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StatService statService;
    private final HouseService houseService;
    private final OrderService orderService;
    private final UserService userService;
    private final LogService logService;

    public AdminController(StatService statService, HouseService houseService, OrderService orderService,
                           UserService userService, LogService logService) {
        this.statService = statService;
        this.houseService = houseService;
        this.orderService = orderService;
        this.userService = userService;
        this.logService = logService;
    }

    /**
     * 后台工作台首页。
     * <p>
     * 展示核心运营指标、待办事项与最近的操作日志。
     */
    @GetMapping({"", "/", "/index"})
    public String index(HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        StatVO overview = statService.overview();

        model.addAttribute("overview", overview);
        model.addAttribute("recentLogs", logService.listRecent(8));
        model.addAttribute("menu", "index");
        model.addAttribute("loginUser", loginUser);

        // 房东登录时，工作台只展示与自己相关的数据
        if (loginUser.isHost()) {
            List<com.code.web_homework.entity.House> myHouses = houseService.listByHost(loginUser.getId());
            model.addAttribute("myHouseCount", myHouses.size());
            model.addAttribute("myHouses", myHouses);
        } else {
            // 管理员可以看到待审核的房源，作为待办提醒
            HouseQuery query = new HouseQuery();
            query.setStatus(Constants.HOUSE_PENDING);
            query.setPageSize(5);
            model.addAttribute("pendingList", houseService.page(query).getList());
        }
        return "admin/index";
    }

    /**
     * 数据统计页面：营收趋势、分类占比、城市分布、订单状态与房源排行。
     */
    @GetMapping("/stat")
    public String stat(Model model, HttpServletRequest request) {
        model.addAttribute("menu", "stat");
        model.addAttribute("loginUser", SessionUtil.getLoginUser(request));
        model.addAttribute("overview", statService.overview());
        model.addAttribute("currentYear", LocalDate.now().getYear());
        return "admin/stat";
    }

    /**
     * AJAX 接口：一次性返回统计页所需的全部图表数据。
     * <p>
     * 合并成一个请求可以避免页面加载时并发发起多个请求，
     * 减少连接开销，页面渲染也更连贯。
     */
    @GetMapping("/stat/data")
    @ResponseBody
    public Result<java.util.Map<String, ChartData>> statData(
            @RequestParam(value = "year", required = false) Integer year) {
        java.util.Map<String, ChartData> data = new java.util.LinkedHashMap<>();
        data.put("monthAmount", statService.monthAmountChart(year));
        data.put("category", statService.categoryChart());
        data.put("city", statService.cityChart(8));
        data.put("orderStatus", statService.orderStatusChart());
        data.put("topHouse", statService.topHouseChart(8));
        data.put("score", statService.scoreChart());
        return Result.success(data);
    }

    /** 操作日志列表 */
    @GetMapping("/log/list")
    public String logList(@RequestParam(value = "keyword", required = false) String keyword,
                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                          Model model, HttpServletRequest request) {
        PageResult<SysLog> page = logService.page(keyword, pageNum, 15);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("menu", "log");
        model.addAttribute("loginUser", SessionUtil.getLoginUser(request));
        return "admin/log";
    }
}
