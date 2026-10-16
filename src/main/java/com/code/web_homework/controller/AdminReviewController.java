package com.code.web_homework.controller;

import com.code.web_homework.common.PageResult;
import com.code.web_homework.common.Result;
import com.code.web_homework.config.SessionUtil;
import com.code.web_homework.entity.Review;
import com.code.web_homework.entity.User;
import com.code.web_homework.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 后台评价管理控制器。
 */
@Controller
@RequestMapping("/admin/review")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /** 评价列表 */
    @GetMapping("/list")
    public String list(@RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                       HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        // 房东只管理自己房源的评价
        Long hostId = loginUser.isHost() ? loginUser.getId() : null;
        PageResult<Review> page = reviewService.page(null, hostId, keyword, pageNum, 10);

        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("menu", "review");
        model.addAttribute("loginUser", loginUser);
        return "admin/review-list";
    }

    /** 回复评价（AJAX） */
    @PostMapping("/reply")
    @ResponseBody
    public Result<Void> reply(@RequestParam("id") Long id,
                              @RequestParam("reply") String reply,
                              HttpServletRequest request) {
        User loginUser = SessionUtil.getLoginUser(request);
        reviewService.reply(id, reply, loginUser.getId());
        return Result.success("回复成功", null);
    }

    /** 显示 / 隐藏评价（AJAX） */
    @PostMapping("/status")
    @ResponseBody
    public Result<Void> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        reviewService.updateStatus(id, status);
        return Result.success(status == 1 ? "评价已显示" : "评价已隐藏", null);
    }

    /** 删除评价（AJAX） */
    @PostMapping("/delete")
    @ResponseBody
    public Result<Void> delete(@RequestParam("id") Long id) {
        reviewService.delete(id);
        return Result.success("评价已删除", null);
    }
}
