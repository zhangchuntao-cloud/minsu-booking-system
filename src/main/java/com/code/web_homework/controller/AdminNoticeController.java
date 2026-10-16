package com.code.web_homework.controller;

import com.code.web_homework.common.BusinessException;
import com.code.web_homework.common.Result;
import com.code.web_homework.config.SessionUtil;
import com.code.web_homework.entity.Notice;
import com.code.web_homework.service.NoticeService;
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
 * 后台公告管理控制器。
 */
@Controller
@RequestMapping("/admin/notice")
public class AdminNoticeController {

    private final NoticeService noticeService;

    public AdminNoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /** 公告列表 */
    @GetMapping("/list")
    public String list(HttpServletRequest request, Model model) {
        model.addAttribute("notices", noticeService.listAll());
        model.addAttribute("menu", "notice");
        model.addAttribute("loginUser", SessionUtil.getLoginUser(request));
        return "admin/notice-list";
    }

    /** 新增 / 编辑公告页面 */
    @GetMapping({"/edit", "/edit/{id}"})
    public String edit(@PathVariable(value = "id", required = false) Long id,
                       HttpServletRequest request, Model model) {
        Notice notice = id == null ? new Notice() : noticeService.getById(id);
        model.addAttribute("notice", notice);
        model.addAttribute("menu", "notice");
        model.addAttribute("loginUser", SessionUtil.getLoginUser(request));
        return "admin/notice-edit";
    }

    /** 保存公告 */
    @PostMapping("/save")
    public String save(Notice notice, HttpServletRequest request, Model model) {
        try {
            Long operatorId = SessionUtil.getLoginUser(request).getId();
            if (notice.getId() == null) {
                noticeService.save(notice, operatorId);
            } else {
                noticeService.update(notice, operatorId);
            }
            return "redirect:/admin/notice/list";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("notice", notice);
            model.addAttribute("menu", "notice");
            model.addAttribute("loginUser", SessionUtil.getLoginUser(request));
            return "admin/notice-edit";
        }
    }

    /** 删除公告（AJAX） */
    @PostMapping("/delete")
    @ResponseBody
    public Result<Void> delete(@RequestParam("id") Long id) {
        noticeService.delete(id);
        return Result.success("公告已删除", null);
    }
}
