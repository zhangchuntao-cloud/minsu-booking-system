package com.code.web_homework.controller;

import com.code.web_homework.common.BusinessException;
import com.code.web_homework.common.Result;
import com.code.web_homework.config.SessionUtil;
import com.code.web_homework.entity.HouseCategory;
import com.code.web_homework.service.HouseCategoryService;
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
 * 后台房源分类管理控制器。
 */
@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

    private final HouseCategoryService categoryService;

    public AdminCategoryController(HouseCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** 分类列表 */
    @GetMapping("/list")
    public String list(HttpServletRequest request, Model model) {
        model.addAttribute("categories", categoryService.listWithCount());
        model.addAttribute("menu", "category");
        model.addAttribute("loginUser", SessionUtil.getLoginUser(request));
        return "admin/category-list";
    }

    /** 新增 / 编辑分类页面 */
    @GetMapping({"/edit", "/edit/{id}"})
    public String edit(@PathVariable(value = "id", required = false) Long id,
                       HttpServletRequest request, Model model) {
        HouseCategory category = id == null ? new HouseCategory() : categoryService.getById(id);
        model.addAttribute("category", category);
        model.addAttribute("menu", "category");
        model.addAttribute("loginUser", SessionUtil.getLoginUser(request));
        return "admin/category-edit";
    }

    /** 保存分类 */
    @PostMapping("/save")
    public String save(HouseCategory category, HttpServletRequest request, Model model) {
        try {
            Long operatorId = SessionUtil.getLoginUser(request).getId();
            if (category.getId() == null) {
                categoryService.save(category, operatorId);
            } else {
                categoryService.update(category, operatorId);
            }
            return "redirect:/admin/category/list";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("category", category);
            model.addAttribute("menu", "category");
            model.addAttribute("loginUser", SessionUtil.getLoginUser(request));
            return "admin/category-edit";
        }
    }

    /** 删除分类（AJAX） */
    @PostMapping("/delete")
    @ResponseBody
    public Result<Void> delete(@RequestParam("id") Long id) {
        categoryService.delete(id);
        return Result.success("分类已删除", null);
    }
}
