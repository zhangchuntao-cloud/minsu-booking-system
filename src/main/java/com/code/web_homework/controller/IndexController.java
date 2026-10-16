package com.code.web_homework.controller;

import com.code.web_homework.entity.HouseCategory;
import com.code.web_homework.entity.Notice;
import com.code.web_homework.service.HouseCategoryService;
import com.code.web_homework.service.HouseService;
import com.code.web_homework.service.NoticeService;
import com.code.web_homework.service.StatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 前台首页与公告相关控制器。
 */
@Controller
public class IndexController {

    private final HouseService houseService;
    private final HouseCategoryService categoryService;
    private final NoticeService noticeService;
    private final StatService statService;

    /** 首页各板块展示的房源数量，可在配置文件中调整 */
    @Value("${minsu.page-size:6}")
    private int pageSize;

    public IndexController(HouseService houseService, HouseCategoryService categoryService,
                           NoticeService noticeService, StatService statService) {
        this.houseService = houseService;
        this.categoryService = categoryService;
        this.noticeService = noticeService;
        this.statService = statService;
    }

    /**
     * 网站首页。
     * <p>
     * 一次性准备好轮播、分类导航、热门房源、最新房源与公告，
     * 由 Thymeleaf 在服务端渲染成完整页面。
     */
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        List<HouseCategory> categories = categoryService.listWithCount();
        model.addAttribute("categories", categories);
        model.addAttribute("hotHouses", houseService.listHot(pageSize));
        model.addAttribute("latestHouses", houseService.listLatest(pageSize));
        model.addAttribute("notices", noticeService.listPublished(4));
        // 首页展示的运营数据
        model.addAttribute("overview", statService.overview());
        model.addAttribute("categoryChart", statService.categoryChart());
        return "index";
    }

    /** 公告列表页 */
    @GetMapping("/notice/list")
    public String noticeList(Model model) {
        model.addAttribute("notices", noticeService.listAll());
        return "notice/list";
    }

    /** 公告详情页 */
    @GetMapping("/notice/detail/{id}")
    public String noticeDetail(@PathVariable("id") Long id, Model model) {
        Notice notice = noticeService.getById(id);
        model.addAttribute("notice", notice);
        model.addAttribute("notices", noticeService.listPublished(5));
        return "notice/detail";
    }

    /** 关于我们页面 */
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("overview", statService.overview());
        return "about";
    }
}
