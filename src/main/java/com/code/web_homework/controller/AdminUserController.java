package com.code.web_homework.controller;

import com.code.web_homework.common.BusinessException;
import com.code.web_homework.common.PageResult;
import com.code.web_homework.common.Result;
import com.code.web_homework.config.SessionUtil;
import com.code.web_homework.dto.UserQuery;
import com.code.web_homework.entity.User;
import com.code.web_homework.service.UserService;
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
 * 后台用户管理控制器。
 * <p>
 * 用户管理属于管理员专属功能，页面入口在侧边栏中仅对管理员显示。
 */
@Controller
@RequestMapping("/admin/user")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /** 用户列表 */
    @GetMapping("/list")
    public String list(UserQuery query, HttpServletRequest request, Model model) {
        PageResult<User> page = userService.page(query);
        model.addAttribute("page", page);
        model.addAttribute("query", query);
        model.addAttribute("roles", userService.listRoles());
        model.addAttribute("menu", "user");
        model.addAttribute("loginUser", SessionUtil.getLoginUser(request));
        return "admin/user-list";
    }

    /** 新增 / 编辑用户页面 */
    @GetMapping({"/edit", "/edit/{id}"})
    public String edit(@PathVariable(value = "id", required = false) Long id,
                       HttpServletRequest request, Model model) {
        User user = id == null ? new User() : userService.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", userService.listRoles());
        model.addAttribute("menu", "user");
        model.addAttribute("loginUser", SessionUtil.getLoginUser(request));
        return "admin/user-edit";
    }

    /**
     * 保存用户（新增或修改）。
     */
    @PostMapping("/save")
    public String save(User user, HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        try {
            if (user.getId() == null) {
                // 后台新增用户，统一使用初始密码 123456
                com.code.web_homework.dto.RegisterForm form = new com.code.web_homework.dto.RegisterForm();
                form.setUsername(user.getUsername());
                form.setPassword("123456");
                form.setConfirmPassword("123456");
                form.setRealName(user.getRealName());
                form.setGender(user.getGender());
                form.setPhone(user.getPhone());
                form.setEmail(user.getEmail());
                form.setRoleCode(user.getRoleCode());
                userService.register(form);
            } else {
                user.setModifyBy(loginUser.getId());
                userService.updateProfile(user);
                // 角色调整单独处理
                if (user.getRoleCode() != null) {
                    User update = new User();
                    update.setId(user.getId());
                    update.setRoleCode(user.getRoleCode());
                    update.setModifyBy(loginUser.getId());
                    userService.updateRole(update);
                }
            }
            return "redirect:/admin/user/list";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("roles", userService.listRoles());
            model.addAttribute("menu", "user");
            model.addAttribute("loginUser", loginUser);
            return "admin/user-edit";
        }
    }

    /** 启用 / 禁用账号（AJAX） */
    @PostMapping("/status")
    @ResponseBody
    public Result<Void> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        userService.updateStatus(id, status);
        return Result.success(status == 1 ? "账号已启用" : "账号已禁用", null);
    }

    /** 重置密码（AJAX） */
    @PostMapping("/reset")
    @ResponseBody
    public Result<Void> resetPassword(@RequestParam("id") Long id) {
        userService.resetPassword(id);
        return Result.success("密码已重置为 123456", null);
    }

    /** 删除用户（AJAX） */
    @PostMapping("/delete")
    @ResponseBody
    public Result<Void> delete(@RequestParam("id") Long id) {
        userService.delete(id);
        return Result.success("用户已删除", null);
    }
}
