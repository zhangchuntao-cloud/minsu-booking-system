package com.code.web_homework.controller;

import com.code.web_homework.common.BusinessException;
import com.code.web_homework.common.Constants;
import com.code.web_homework.common.PageResult;
import com.code.web_homework.common.Result;
import com.code.web_homework.config.SessionUtil;
import com.code.web_homework.dto.RegisterForm;
import com.code.web_homework.entity.Favorite;
import com.code.web_homework.entity.Review;
import com.code.web_homework.entity.User;
import com.code.web_homework.service.FavoriteService;
import com.code.web_homework.service.OrderService;
import com.code.web_homework.service.ReviewService;
import com.code.web_homework.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户相关控制器：登录、注册、个人中心。
 */
@Controller
public class UserController {

    private final UserService userService;
    private final FavoriteService favoriteService;
    private final ReviewService reviewService;
    private final OrderService orderService;

    public UserController(UserService userService, FavoriteService favoriteService,
                          ReviewService reviewService, OrderService orderService) {
        this.userService = userService;
        this.favoriteService = favoriteService;
        this.reviewService = reviewService;
        this.orderService = orderService;
    }

    /** 登录页 */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "redirect", required = false) String redirect,
                            Model model) {
        model.addAttribute("redirect", redirect);
        return "user/login";
    }

    /**
     * 处理登录表单提交。
     * <p>
     * 登录失败时不抛异常，而是把提示信息放回模型重新渲染登录页，
     * 这样用户输入过的账号不会丢失，体验更好。
     */
    @PostMapping("/login")
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam(value = "redirect", required = false) String redirect,
                          HttpServletRequest request, Model model) {
        try {
            User user = userService.login(username, password);
            // 登录成功后把用户写入 Session，后续请求由拦截器校验
            SessionUtil.setLoginUser(request, user);
            if (redirect != null && !redirect.isEmpty() && redirect.startsWith("/")) {
                return "redirect:" + redirect;
            }
            // 管理员与房东登录后直接进入后台工作台，普通会员回到首页
            if (user.isAdmin() || user.isHost()) {
                return "redirect:/admin/index";
            }
            return "redirect:/index";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("redirect", redirect);
            return "user/login";
        }
    }

    /** 注册页 */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "user/register";
    }

    /** 处理注册表单提交 */
    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("form") RegisterForm form,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getFieldError().getDefaultMessage());
            return "user/register";
        }
        try {
            userService.register(form);
            model.addAttribute("success", "注册成功，请使用新账号登录");
            return "user/login";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "user/register";
        }
    }

    /** 退出登录，销毁 Session 中的登录信息 */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        SessionUtil.removeLoginUser(request);
        return "redirect:/index";
    }

    /** 个人中心首页：展示账户概览 */
    @GetMapping("/user/center")
    public String center(HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        // 重新查询以获取最新的余额等信息
        User user = userService.getById(loginUser.getId());
        SessionUtil.setLoginUser(request, user);

        model.addAttribute("user", user);
        model.addAttribute("favoriteCount", favoriteService.listByUser(user.getId()).size());
        model.addAttribute("reviewCount", reviewService.listByUser(user.getId()).size());
        // 订单数量按状态分别统计，便于在概览页给出待办提醒
        com.code.web_homework.dto.OrderQuery orderQuery = new com.code.web_homework.dto.OrderQuery();
        orderQuery.setUserId(user.getId());
        orderQuery.setPageSize(1);
        model.addAttribute("orderCount", orderService.page(orderQuery).getTotal());
        orderQuery.setStatus(Constants.ORDER_UNPAID);
        model.addAttribute("unpaidCount", orderService.page(orderQuery).getTotal());
        return "user/center";
    }

    /** 个人资料页 */
    @GetMapping("/user/profile")
    public String profile(HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        model.addAttribute("user", userService.getById(loginUser.getId()));
        return "user/profile";
    }

    /** 保存个人资料 */
    @PostMapping("/user/profile/save")
    public String saveProfile(User form, HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        form.setId(loginUser.getId());
        try {
            userService.updateProfile(form);
            // 同步刷新 Session 中的用户信息，保证页面显示即时更新
            SessionUtil.setLoginUser(request, userService.getById(loginUser.getId()));
            model.addAttribute("success", "个人资料保存成功");
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("user", userService.getById(loginUser.getId()));
        return "user/profile";
    }

    /** 修改密码页 */
    @GetMapping("/user/password")
    public String passwordPage() {
        return "user/password";
    }

    /**
     * 修改密码。使用 AJAX 提交，成功后前端提示并跳转到登录页。
     */
    @PostMapping("/user/password/save")
    @ResponseBody
    public Result<Void> savePassword(@RequestParam("oldPassword") String oldPassword,
                                     @RequestParam("newPassword") String newPassword,
                                     @RequestParam("confirmPassword") String confirmPassword,
                                     HttpServletRequest request) {
        User loginUser = SessionUtil.getLoginUser(request);
        userService.changePassword(loginUser.getId(), oldPassword, newPassword, confirmPassword);
        SessionUtil.removeLoginUser(request);
        return Result.success("密码修改成功，请重新登录", null);
    }

    /** 我的收藏 */
    @GetMapping("/user/favorite")
    public String favorite(HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        List<Favorite> favorites = favoriteService.listByUser(loginUser.getId());
        model.addAttribute("favorites", favorites);
        return "user/favorite";
    }

    /** 我的评价 */
    @GetMapping("/user/review")
    public String review(HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        List<Review> reviews = reviewService.listByUser(loginUser.getId());
        model.addAttribute("reviews", reviews);
        return "user/review";
    }

    /**
     * AJAX 接口：校验账号是否已被注册。
     * <p>
     * 注册页中用户输入账号后失焦即触发，实现即时的可用性提示。
     */
    @GetMapping("/api/user/check-username")
    @ResponseBody
    public Result<Map<String, Object>> checkUsername(@RequestParam("username") String username) {
        Map<String, Object> data = new HashMap<>();
        boolean exists = userService.getByUsername(username) != null;
        data.put("exists", exists);
        return Result.success(exists ? "该账号已被注册" : "该账号可以使用", data);
    }
}
