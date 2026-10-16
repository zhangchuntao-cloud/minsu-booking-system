package com.code.web_homework.controller;

import com.code.web_homework.common.Result;
import com.code.web_homework.config.SessionUtil;
import com.code.web_homework.entity.User;
import com.code.web_homework.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 前台评价控制器。
 */
@Controller
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * 发表评价（AJAX）。
     */
    @PostMapping("/add")
    @ResponseBody
    public Result<Void> add(@RequestParam("orderId") Long orderId,
                            @RequestParam("score") Integer score,
                            @RequestParam("content") String content,
                            HttpServletRequest request) {
        User loginUser = SessionUtil.getLoginUser(request);
        reviewService.add(orderId, score, content, loginUser.getId());
        return Result.success("评价发表成功，感谢您的反馈", null);
    }
}
