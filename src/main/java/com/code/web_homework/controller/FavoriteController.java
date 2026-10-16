package com.code.web_homework.controller;

import com.code.web_homework.common.Result;
import com.code.web_homework.config.SessionUtil;
import com.code.web_homework.entity.User;
import com.code.web_homework.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 前台收藏控制器。
 */
@Controller
@RequestMapping("/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * 收藏 / 取消收藏（AJAX）。
     * <p>
     * 同一个接口完成两种操作，由服务层根据当前收藏状态决定行为，
     * 前端只需根据返回的 favorited 字段更新按钮样式。
     */
    @PostMapping("/toggle")
    @ResponseBody
    public Result<Map<String, Object>> toggle(@RequestParam("houseId") Long houseId,
                                              HttpServletRequest request) {
        User loginUser = SessionUtil.getLoginUser(request);
        boolean favorited = favoriteService.toggle(loginUser.getId(), houseId);

        Map<String, Object> data = new HashMap<>();
        data.put("favorited", favorited);
        return Result.success(favorited ? "已加入收藏" : "已取消收藏", data);
    }
}
