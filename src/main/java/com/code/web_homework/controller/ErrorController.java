package com.code.web_homework.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 错误页面控制器。
 */
@Controller
@RequestMapping("/error")
public class ErrorController {

    /** 403 无权限 */
    @GetMapping("/403")
    public String forbidden(Model model) {
        model.addAttribute("message", "抱歉，您没有访问该页面的权限");
        return "error/403";
    }

    /** 404 页面不存在 */
    @GetMapping("/404")
    public String notFound(Model model) {
        model.addAttribute("message", "抱歉，您访问的页面不存在或已被删除");
        return "error/404";
    }
}
